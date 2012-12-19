/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package de.fct.companian.analyze;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.jeantessier.dependency.ClassNode;
import com.jeantessier.dependency.FeatureNode;
import com.jeantessier.dependency.Node;
import com.jeantessier.dependency.VisitorBase;

import de.fct.companian.analyze.db.dao.ClassesDao;
import de.fct.companian.analyze.db.dao.ExtCallDao;
import de.fct.companian.analyze.db.dao.ExtImplementsDao;
import de.fct.companian.analyze.db.dao.ExtInstanceOfDao;
import de.fct.companian.analyze.db.dao.ExtLinkDao;
import de.fct.companian.analyze.db.dao.JarDao;
import de.fct.companian.analyze.db.dao.MemberDao;
import de.fct.companian.analyze.db.dao.MethodDao;
import de.fct.companian.analyze.db.dao.ProductDao;
import de.fct.companian.analyze.db.model.Clazz;
import de.fct.companian.analyze.db.model.ExtCall;
import de.fct.companian.analyze.db.model.ExtImplements;
import de.fct.companian.analyze.db.model.ExtInstanceOf;
import de.fct.companian.analyze.db.model.ExtLink;
import de.fct.companian.analyze.db.model.Jar;
import de.fct.companian.analyze.db.model.Member;
import de.fct.companian.analyze.db.model.Method;
import de.fct.companian.analyze.db.model.Product;
import de.fct.companian.analyze.helper.ClassHelper;
import de.fct.companian.analyze.model.JarEpDependency;
import de.fct.companian.analyze.mvn.PomInfo;

public class ExtDependencyAnalyzer extends VisitorBase {

	private static Logger logger = Logger.getLogger(ExtDependencyAnalyzer.class);

	private final DataSource dataSource;
	private final PomInfo pomInfo;
	
	private Product product;
	private Jar jar;
	
	private final ClassesDao classDao;
	private final MethodDao methodDao;
	private final MemberDao memberDao;
	private final ExtCallDao extCallDao;
	private final ExtLinkDao extLinkDao;
	private final ExtInstanceOfDao extInstanceOfDao;
	private final ExtImplementsDao extImplementsDao;
	
	private ClassToJarMapper classToJarMapper;
	private Map<String, JarEpDependency> jarEpDependencies = new HashMap<String, JarEpDependency>();

	public ExtDependencyAnalyzer(DataSource dataSource, PomInfo pomInfo) {
		this.dataSource = dataSource;
		this.pomInfo = pomInfo;
		
		this.classDao = new ClassesDao(this.dataSource);
		this.methodDao = new MethodDao(this.dataSource);
		this.memberDao = new MemberDao(this.dataSource);
		this.extCallDao = new ExtCallDao(this.dataSource);
		this.extLinkDao = new ExtLinkDao(this.dataSource);
		this.extInstanceOfDao = new ExtInstanceOfDao(this.dataSource);
		this.extImplementsDao = new ExtImplementsDao(this.dataSource);
		
		ProductDao productDao = new ProductDao(this.dataSource);
		this.product = productDao.loadProduct(this.pomInfo.getGroupId());
		
		if (this.product != null) {
			JarDao jarDao = new JarDao(this.dataSource);
			this.jar = jarDao.loadJar(this.pomInfo.getArtifactId(), this.pomInfo.getVersion(), this.product.getProductId());
			if (this.jar == null) {
				logger.error("<init> jar " + this.pomInfo.getArtifactId() + " version " + this.pomInfo.getVersion() + " not found in DB for product " + this.pomInfo.getGroupId());
			}
		}
		else {
			logger.error("<init> product " + this.pomInfo.getGroupId() + " not found in DB");
		}
	}

	public ClassToJarMapper getSourceCollector() {
		return classToJarMapper;
	}

	public void setClassfileCollector(ClassToJarMapper loader) {
		this.classToJarMapper = loader;
	}

	@Override
	protected void postprocessClassNode(ClassNode node) {
		super.preprocessClassNode(node);

		String currentClassName = node.getName().replace("[]", "");
		String classSource = classToJarMapper.getSource(currentClassName);
		
		if (classSource == null) {
			for (Node inDepNode : node.getInboundDependencies()) {
				if (inDepNode instanceof ClassNode) {
					String className = inDepNode.getName();

					if (logger.isDebugEnabled()) {
						logger.debug("postprocessClassNode() " + className + " implements " + currentClassName);
					}
					addExtImplements(className, currentClassName);
					
				} else if (inDepNode instanceof FeatureNode) {
					FeatureNode inDepFeatureNode = (FeatureNode)inDepNode;

					String className = inDepFeatureNode.getClassNode().getName(); 
					String feature = inDepFeatureNode.getName().replace(className + ".", "");
					if (feature.indexOf("(") > -1 || feature.indexOf("{") > -1) {
						if (logger.isDebugEnabled()) {
							logger.debug("postprocessClassNode() " + feature + " links " + currentClassName);
						}
						addExtLink(currentClassName, className, feature);
					}
					else {
						if (logger.isDebugEnabled()) {
							logger.debug("postprocessClassNode() " + feature + " instanceof " + currentClassName);
						}
						addExtInstanceOf(className, feature, currentClassName);
					}
				}
			}
		}
	}

	@Override
	protected void postprocessFeatureNode(FeatureNode node) {
		String currentClassName = node.getClassNode().getName().replace("[]", "");
		String classSource = classToJarMapper.getSource(currentClassName);
		
		if (classSource == null) {
			for (Node inDepNode : node.getInboundDependencies()) {
				if (inDepNode instanceof FeatureNode) {
					FeatureNode inDepFeatureNode = (FeatureNode)inDepNode;
					
					String feature = node.getName();
					String className = inDepFeatureNode.getClassNode().getName();
					String caller = inDepFeatureNode.getName().replace(className + ".", "");
					
					if (ClassHelper.isMethodCall(feature)) {
						if (logger.isDebugEnabled()) {
							logger.debug("postprocessFeatureNode() " + className + " calls " + feature);
						}
						addExtCall(feature, className, caller);
					}
					else {
						if (logger.isDebugEnabled()) {
							logger.debug("postprocessFeatureNode() " + className + " links " + feature);
						}
						addExtLink(feature, className, caller);
					}
				}
			}
		}
	}

	private void addExtImplements(String className, String extClassName) {
		String jarName = classToJarMapper.getSource(className);
		Clazz clazz = this.classDao.loadClass(className, this.jar.getJarId());
		if (clazz != null) {
				addDep(jarName, className, className + " implements " + extClassName);
				
				ExtImplements extImplements = this.extImplementsDao.loadExtImplements(extClassName, clazz.getClassId());
				if (extImplements == null) {
					if (logger.isDebugEnabled()) {
						logger.debug("addExtImplements() adding ExtImplements " + className + " implements " + extClassName);
					}

					this.extImplementsDao.addExtImplements(extClassName, clazz.getClassId());
				}
				else {
					if (logger.isDebugEnabled()) {
						logger.debug("addExtImplements() skipping existing external implements for " + extClassName);
					}
				}
		}
		else {
			logger.error("addExtImplements() class " + className + " not found in DB for JAR " + this.jar.getJarId());
		}
	}
	
	private void addExtInstanceOf(String className, String signature, String extClassName) {
		String jarName = classToJarMapper.getSource(className);
		Clazz clazz = this.classDao.loadClass(className, this.jar.getJarId());
		if (clazz != null) {
			Member member = this.memberDao.loadMember(signature, clazz.getClassId());
			if (member != null) {
				addDep(jarName, className, member + " is instanceof " + extClassName);

				ExtInstanceOf extInstanceOf = this.extInstanceOfDao.loadExtInstanceOf(extClassName, member.getMemberId());
				if (extInstanceOf == null) {
					if (logger.isDebugEnabled()) {
						logger.debug("addExtInstanceOf() adding ExtInstanceOf " + member + " is instanceof " + extClassName);
					}

					this.extInstanceOfDao.addExtInstanceOf(extClassName, member.getMemberId());
				}
				else {
					if (logger.isDebugEnabled()) {
						logger.debug("addExtInstanceOf() skipping existing external instanceof for " + signature);
					}
				}
			}
			else {
				logger.error("addExtInstanceOf() member " + signature + " not found in DB for class " + clazz.getClassId());
			}
		}
		else {
			logger.error("addExtInstanceOf() class " + className + " not found in DB for JAR " + this.jar.getJarId());
		}
	}
	
	private void addExtLink(String member, String className, String caller) {
		if (this.jar != null) {
			String jarName = classToJarMapper.getSource(className);
			Clazz clazz = this.classDao.loadClass(className, this.jar.getJarId());
			if (clazz != null) {
				Method method = this.methodDao.loadMethod(caller, clazz.getClassId());
				if (method != null) {
					addDep(jarName, className, caller + " links " + member);

					ExtLink extLink = this.extLinkDao.loadExtLink(member, null, method.getMethodId());
					if (extLink == null) {
						if (logger.isDebugEnabled()) {
							logger.debug("addExtLink() adding ExtLink " + member + " to method " + method);
						}
						this.extLinkDao.addExtLink(member, null, method.getMethodId());
					}
					else {
						if (logger.isDebugEnabled()) {
							logger.debug("addExtLink() skipping existing external link " + member);
						}
					}
				}
				else {
					logger.error("addExtLink() method " + caller + " not found in DB for class " + clazz.getClassId());
				}
			}
			else {
				logger.error("addExtLink() class " + className + " not found in DB for JAR " + this.jar.getJarId());
			}			
		}
	}

	private void addExtCall(String methodCall, String className, String caller) {
		if (this.jar != null) {
			String jarName = classToJarMapper.getSource(className);
			Clazz clazz = this.classDao.loadClass(className, this.jar.getJarId());
			if (clazz != null) {
				Method method = this.methodDao.loadMethod(caller, clazz.getClassId());
				if (method != null) {
					String extFqcn = ClassHelper.getClassName(methodCall);
					String extSignature = ClassHelper.getSignature(methodCall);
					addDep(jarName, className, caller + " calls " + methodCall);
					
					ExtCall extCall = this.extCallDao.loadExtCall(extFqcn, extSignature, null, method.getMethodId());
					if (extCall == null) {
						if (logger.isDebugEnabled()) {
							logger.debug("addExtCall() adding ExtCall " + extSignature + " to method " + method);
						}
						
						this.extCallDao.addExtCall(extFqcn, extSignature, null, method.getMethodId());
					}
					else {
						if (logger.isDebugEnabled()) {
							logger.debug("addExtCall() skipping existing external call " + extSignature);
						}
					}
				}
				else {
					logger.error("addExtCall() method " + caller + " not found in DB for class " + clazz.getClassId());
				}
			}
			else {
				logger.error("addExtCall() class " + className + " not found in DB for JAR " + this.jar.getJarId());
			}
		}
	}
	
	private void addDep(String jarName, String className, String description) {
		JarEpDependency dep = this.jarEpDependencies.get(jarName);
		if (dep == null) {
			dep = new JarEpDependency(jarName);
		}
		dep.addDependency(className, description);
		this.jarEpDependencies.put(jarName, dep);
	}

	public Map<String, JarEpDependency> getJarEpDependencies() {
		return jarEpDependencies;
	}
	
}
