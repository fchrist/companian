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
package de.fct.companian.analyze.prover;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.apache.maven.artifact.Artifact;

import de.fct.companian.analyze.db.dao.ClassesDao;
import de.fct.companian.analyze.db.dao.ExtCallDao;
import de.fct.companian.analyze.db.dao.JarDao;
import de.fct.companian.analyze.db.dao.MethodDao;
import de.fct.companian.analyze.db.dao.ProductDao;
import de.fct.companian.analyze.db.model.Clazz;
import de.fct.companian.analyze.db.model.ExtCall;
import de.fct.companian.analyze.db.model.Iface;
import de.fct.companian.analyze.db.model.Jar;
import de.fct.companian.analyze.db.model.Method;
import de.fct.companian.analyze.db.model.Product;
import de.fct.companian.analyze.mvn.PomInfo;

public class JarProver {

	private static Logger logger = Logger.getLogger(JarProver.class);
	
	private final ProductDao productDao;
	private final JarDao jarDao;
	private final ClassesDao classesDao;
	private final MethodDao methodDao;
	private final ExtCallDao extCallDao;
	
	private Product product;
	private Jar jar;
	
	public JarProver(DataSource dataSource) {
		this.productDao = new ProductDao(dataSource);
		this.jarDao = new JarDao(dataSource);
		this.classesDao = new ClassesDao(dataSource);
		this.methodDao = new MethodDao(dataSource);
		this.extCallDao = new ExtCallDao(dataSource);
	}
	
	public void prove(PomInfo pomInfo, Set<Artifact> artifactSet) {
		logger.info("prove() starting JAR proof of " + pomInfo);
		
		// TODO: Consider only artifacts in artifactSet - not all JARs that are present in the DB
		
		this.product = this.productDao.loadProduct(pomInfo.getGroupId());
		if (this.product != null) {
			this.jar = this.jarDao.loadJar(pomInfo.getArtifactId(), pomInfo.getVersion(), this.product.getProductId());

			// prove Classes
			if (this.jar != null) {
				List<Clazz> classList = this.classesDao.listClasses(this.jar.getJarId());
				if (classList.size() > 0) {
					List<ExtCall> extCallFails = new ArrayList<ExtCall>();
					for (Clazz dbClass : classList) {
						proveExtCallsOfClass(pomInfo, dbClass, extCallFails);
						proveExtCallsOfMethods(pomInfo, dbClass, extCallFails);
						
					}					
					logger.info("prove() " + extCallFails.size() + " ExtCalls could not be resolved by any artifact from the db");
				}
				else {
					logger.warn("prove() No classes found in DB for " + pomInfo.getArtifactId() + ":" + pomInfo.getVersion());
				}
			}
			else {
				logger.warn("prove() JAR not found in DB for " + pomInfo.getArtifactId() + ":" + pomInfo.getVersion());
			}
		}
		else {
			logger.warn("prove() no product found for " + pomInfo.getGroupId());
		}
		
		logger.info("prove() JAR proof finished");
	}

	private void proveExtCallsOfClass(PomInfo pomInfo, Clazz dbClass, List<ExtCall> extCallFails) {
		if (logger.isDebugEnabled()) {
			logger.debug("proveExtCallsOfClass() class=" + dbClass);
		}
		// prove ExtCalls of Class
		List<ExtCall> extCalls = this.extCallDao.listExtCallsOfClass(dbClass.getClassId());
		
		if (extCalls.size() > 0) {
			for (ExtCall extCall : extCalls) {
				if (logger.isDebugEnabled()) {
					logger.debug("proveExtCallsOfClass() fqcn=" + extCall.getExtFqcn() + ", signature=" + extCall.getExtSignature());
				}

				List<Jar> jarList = this.jarDao.listJarsForExtCall(extCall.getExtFqcn(), extCall.getExtSignature());
				if (jarList.size() > 0) {
					if (logger.isDebugEnabled()) {
						for (Jar jar : jarList) {
							logger.debug("proveExtCallsOfClass() JAR " + jar.getJarname() + " solves dependency " + extCall.getExtFqcn() + ", signature=" + extCall.getExtSignature() + " raised by " + dbClass.getFqcn());
						}
					}
				}
				else {
					logger.warn("proveExtCallsOfClass() no JAR solves the dependency fqcn=" + extCall.getExtFqcn() + "." + extCall.getExtSignature());
					extCallFails.add(extCall);
				}
			}			
		}
		else {
			logger.debug("proveExtCallsOfClass() No ExtCalls found in DB for " + pomInfo.getArtifactId() + ":" + pomInfo.getVersion());
		}
		
		logger.debug("proveExtCallsOfClass() " + extCalls.size() + " ExtCalls were proved");
	}
	
	private void proveExtCallsOfMethods(PomInfo pomInfo, Clazz dbClass, List<ExtCall> extCallFails) {
		if (logger.isDebugEnabled()) {
			logger.debug("proveExtCallsOfMethods() class=" + dbClass);
		}
		// prove ExtCalls of Methods
		int extCallCount = 0;
		List<Method> methods = this.methodDao.listMethodsOfClass(dbClass.getClassId());
		if (methods.size() > 0) {
			for (Method method : methods) {
				List<ExtCall> extCalls = this.extCallDao.listExtCallsOfMethod(method.getMethodId());
				extCallCount += extCalls.size();
				if (extCalls.size() > 0) {
					for (ExtCall extCall : extCalls) {
						if (logger.isDebugEnabled()) {
							logger.debug("proveExtCallsOfMethods() fqcn=" + extCall.getExtFqcn() + ", signature=" + extCall.getExtSignature());
						}

						List<Jar> solvingJars = new ArrayList<Jar>();
						proveClassHierarchy(extCall.getExtFqcn(), extCall, solvingJars);
						if (solvingJars.size() > 0) {
							if (logger.isDebugEnabled()) {
								for (Jar jar : solvingJars) {
									logger.debug("proveExtCallsOfMethods() JAR " + jar.getJarname() + " solves dependency fqcn=" + extCall.getExtFqcn() + ", signature=" + extCall.getExtSignature());
								}
							}							
						}
						else {
							logger.info("proveExtCallsOfMethods() failed to solve " + extCall);
							extCallFails.add(extCall);
						}

					}
				}
				else {
					logger.debug("proveExtCallsOfMethods() No ExtCalls found in DB for " + pomInfo.getArtifactId() + ":" + pomInfo.getVersion());
				}
			}
		}
		else {
			logger.debug("proveExtCallsOfMethods() no methods found in db for class " + dbClass.getFqcn());
		}
		
		logger.debug("proveExtCallsOfMethods() " + extCallCount + " ExtCalls were proved");
	}

	private void proveClassHierarchy(String fqcn, ExtCall extCall, List<Jar> solvingJars) {
		if (logger.isDebugEnabled()) {
			logger.debug("proveClassHierarchy() fqcn=" + fqcn + ", extCall=" + extCall);
		}
		List<Jar> jarList = this.jarDao.listJarsForExtCall(fqcn, extCall.getExtSignature());
		if (jarList.size() > 0) {
			logger.debug("proveClassHierarchy() found solving JARs");
			solvingJars.addAll(jarList);
		}
		else {
			List<Clazz> dbClasses = this.classesDao.listClasses(fqcn);
			if (dbClasses.size() > 0) {
				for (Clazz dbClass : dbClasses) {
					if (dbClass.isInterface()) {
						if (dbClass.getInterfaces() != null) {
							for (Iface ifa : dbClass.getInterfaces()) {
								proveClassHierarchy(ifa.getFqin(), extCall, solvingJars);
							}
						}
					}
					else if (!dbClass.getSuperFqcn().equals("java.lang.Object")) {
						logger.debug("proveClassHierarchy() proving super class");
						proveClassHierarchy(dbClass.getSuperFqcn(), extCall, solvingJars);
					}
				}
			}
		}		
	}
}
