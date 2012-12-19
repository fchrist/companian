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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.jeantessier.classreader.Class_info;
import com.jeantessier.classreader.Classfile;
import com.jeantessier.classreader.Field_info;
import com.jeantessier.classreader.Method_info;
import com.jeantessier.classreader.VisitorBase;

import de.fct.companian.analyze.config.Config;
import de.fct.companian.analyze.db.dao.ClassesDao;
import de.fct.companian.analyze.db.dao.JarDao;
import de.fct.companian.analyze.db.dao.MemberDao;
import de.fct.companian.analyze.db.dao.MethodDao;
import de.fct.companian.analyze.db.dao.ProductDao;
import de.fct.companian.analyze.db.model.Clazz;
import de.fct.companian.analyze.db.model.Iface;
import de.fct.companian.analyze.db.model.Jar;
import de.fct.companian.analyze.db.model.Member;
import de.fct.companian.analyze.db.model.Method;
import de.fct.companian.analyze.db.model.Product;
import de.fct.companian.analyze.mvn.PomInfo;

public class ClassIndexer extends VisitorBase {

	private static Logger logger = Logger.getLogger(ClassIndexer.class);

	private final PomInfo pomInfo;

	private final ClassesDao classesDao;
	private final MethodDao methodDao;
	private final MemberDao memberDao;
	private final ProductDao productDao;
	private final JarDao jarDao;

	private int jarId = -1;
	private int productId = -1;

	private long classCount = 0;
	private long visitClassTime = 0;
	
	private boolean skipped = false;

	public ClassIndexer(DataSource dataSource, PomInfo pomInfo) {
		this.pomInfo = pomInfo;
		this.classesDao = new ClassesDao(dataSource);
		this.methodDao = new MethodDao(dataSource);
		this.memberDao = new MemberDao(dataSource);
		this.productDao = new ProductDao(dataSource);
		this.jarDao = new JarDao(dataSource);
	}
	
	@Override
	public void visitClassfile(Classfile classfile) {
		// Wenn dieses JAR übersprungen werden soll, nichts machen.
		if (this.skipped)
			return;
		
		long start = System.currentTimeMillis();
		this.classCount++;
		if (this.productId < 0) {
			this.productId = updateProducts();
		}
		try {
			if (this.jarId < 0) {
				this.jarId = updateJars(this.productId);
			}
		}
		catch (Exception e) {
			logger.warn("visitClassfile() exception while updating JAR information - skipping this JAR");
			this.skipped = true;
			return;
		}

		saveClass(classfile, this.jarId);

		long startVisitField = System.currentTimeMillis();
		for (Field_info field : classfile.getAllFields()) {
			field.accept(this);
		}
		long endVisitField = System.currentTimeMillis();
		float timeVisitField = endVisitField - startVisitField;

		long startVisitMethod = System.currentTimeMillis();
		for (Method_info method : classfile.getAllMethods()) {
			method.accept(this);
		}
		long endVisitMethod = System.currentTimeMillis();
		float timeVisitMethod = endVisitMethod - startVisitMethod;

		if (logger.isInfoEnabled()) {
		    long end = System.currentTimeMillis();
		    long time = (end - start);
		    this.visitClassTime += time;
		    if (logger.isDebugEnabled()) {
		        logger.debug("visitClassfile() took " + time + "ms (fields " + timeVisitField + "ms | methods " + timeVisitMethod + "ms)");
		    }
		}
	}

	private int updateProducts() {
		int productId = getProductId();
		if (productId < 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("updateProducts() inserting " + this.pomInfo.getGroupId());
			}
			
			Product product = this.productDao.saveProduct(this.pomInfo.getGroupId());
			if (product != null) {
			    productId = product.getProductId();
			}
		}
		return productId;
	}

	private int getProductId() {
		int productId = -1;

		Product product = this.productDao.loadProduct(this.pomInfo.getGroupId());
		if (product != null) {
		    productId = product.getProductId();
		}
		
		return productId;
	}

	private int updateJars(int productId) throws Exception {
		int jarId = getJarId(productId);
		if (jarId < 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("updateJars() inserting " + this.pomInfo + ", productId=" + productId);
			}
			
			Jar jarToInsert = new Jar();
			jarToInsert.setArtifact(this.pomInfo.getArtifactId());
			jarToInsert.setJarname(this.pomInfo.getJarName());
			jarToInsert.setVersion(this.pomInfo.getVersion());
			Product p = new Product();
			p.setProductId(productId);
			jarToInsert.setProduct(p);
			
			Jar insertedJar = this.jarDao.saveJar(jarToInsert);
			jarId = insertedJar.getJarId();
		}
		else {
			throw new Exception("JAR already indexed");
		}
		return jarId;
	}

	private int getJarId(int productId) {
		int jarId = -1;

		Jar jar = this.jarDao.loadJar(this.pomInfo.getArtifactId(), this.pomInfo.getVersion(), productId);
		if (jar != null) {
		    jarId = jar.getJarId();
		}

		return jarId;
	}

	private void saveClass(Classfile classfile, int jarId) {
		boolean classAlreadyExists = false;
		if (Config.proofExistence()) {
			classAlreadyExists = (getClassId(classfile.getClassName(), jarId) >= 0);
		}

		if (classAlreadyExists) {
			if (logger.isDebugEnabled()) {
				logger.debug("saveClass() skipping existing class " + classfile.getClassName() + " with metadata " + this.pomInfo);
			}
		}
		else {
			if (logger.isDebugEnabled()) {
				logger.debug("saveClass() adding class " + classfile.getClassName() + " with metadata " + this.pomInfo);
			}
			Clazz dbClass = new Clazz();
			dbClass.setFqcn(classfile.getClassName());
			dbClass.setAccessFlags(classfile.getAccessFlag());
			dbClass.setJarId(jarId);
			dbClass.setSuperFqcn(classfile.getSuperclassName());
			List<Iface> ifaces = new ArrayList<Iface>();
			for (Class_info cinfo : classfile.getAllInterfaces()) {
				Iface ifa = new Iface();
				ifa.setFqin(cinfo.getName());
				ifaces.add(ifa);
			}
			dbClass.setInterfaces(ifaces);
			this.classesDao.saveClass(dbClass);
		}
	}

	private int getClassId(String className, int jarId) {
		int classId = -1;

		Clazz clazz = this.classesDao.loadClass(className, jarId);
		if (clazz != null) {
		    classId = clazz.getClassId();
		}
		
		return classId;
	}

	@Override
	public void visitMethod_info(Method_info entry) {
		try {
			int classId = determineClassId(entry.getClassfile());

			Method method = null;
			if (Config.proofExistence()) {
				method = this.methodDao.loadMethod(entry.getSignature(), classId);
			}
			if (method == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("visitMethod_info() inserting " + entry.getDeclaration() + " of class " + entry.getClassfile());
				}
				if (!entry.isConstructor() && !entry.isStaticInitializer()) {
					this.methodDao.addMethod(entry.getAccessFlag(), entry.isConstructor(), entry.getReturnType(), entry.getSignature(), classId);
				}
				else {
					this.methodDao.addMethod(entry.getAccessFlag(), entry.isConstructor(), null, entry.getSignature(), classId);
				}
			}
			else {
				if (logger.isDebugEnabled()) {
					logger.debug("visitMethod_info() skipping " + entry.getSignature() + " because it already exists in DB");
				}
			}

		}
		catch (SQLException e) {
			logger.error("visitMethod_info() error updating db with method info " + entry.getName(), e);
		}
		catch (Exception e) {
			logger.error("visitMethod_info() error handling method " + entry.getName(), e);
		}
		finally {
			super.visitMethod_info(entry);
		}
	}

	@Override
	public void visitField_info(Field_info entry) {
		try {
			int classId = determineClassId(entry.getClassfile());
			Member member = null;
			if (Config.proofExistence()) {
				member = this.memberDao.loadMember(entry.getSignature(), classId);
			}
			if (member == null) {
				this.memberDao.addMember(entry.getSignature(), classId);
			}
			else {
				if (logger.isDebugEnabled()) {
					logger.debug("visitField_info() skipping " + entry.getSignature() + " because it already exists in DB");
				}
			}
		}
		catch (SQLException e) {
			logger.error("visitField_info() error updating db with field info " + entry.getName(), e);
		}
		catch (Exception e) {
			logger.error("visitField_info() error handling field " + entry.getName(), e);
		}
		finally {
			super.visitField_info(entry);
		}
	}

	private int determineClassId(Classfile classfile) throws Exception {
		if (this.productId < 0) {
			this.productId = updateProducts();
		}
		if (this.jarId < 0) {
			this.jarId = updateJars(this.productId);
		}

		int classId = getClassId(classfile.getClassName(), this.jarId);
		if (classId < 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("determineClassId() class " + classfile + " not indexed yet");
			}
			saveClass(classfile, this.jarId);
			classId = getClassId(classfile.getClassName(), this.jarId);
		}
		return classId;
	}

	public long getAverageClassTime() {
	    if (this.classCount > 0) {
	        return this.visitClassTime / this.classCount;
	    }
	    else {
	        return 0;
	    }
	}
}
