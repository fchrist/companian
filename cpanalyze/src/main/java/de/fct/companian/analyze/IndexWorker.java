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

import java.io.File;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

import de.fct.companian.analyze.db.dao.JarDao;
import de.fct.companian.analyze.db.dao.ProductDao;
import de.fct.companian.analyze.db.model.Jar;
import de.fct.companian.analyze.db.model.Product;
import de.fct.companian.analyze.mvn.PomInfo;
import de.fct.companian.analyze.mvn.helper.PomHelper;

public class IndexWorker implements Runnable {

	private static Logger logger = Logger.getLogger(IndexWorker.class);
	
	private final DataSource dataSource;
	
	private File jarFile;
	private File pomFile;
	
	private volatile boolean hasRun = false;
	
	public IndexWorker(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void run() {
		this.work();
		this.hasRun = true;
	}
	
	private void work() {
		if (logger.isDebugEnabled()) {
			logger.debug("work() start jarFile=" + jarFile.getName() + ", pomFile=" + pomFile.getName());
		}
		long beforeStart = System.currentTimeMillis();
		
		PomInfo pomInfo = null;
		try {
			PomHelper pomHelper = new PomHelper(pomFile);
			pomInfo = pomHelper.extractPomInfo();
		}
		catch (DocumentException e) {
			e.printStackTrace();
		}
		
		if (pomInfo != null) {
		    if (!this.jarPresentInDB(pomInfo)) {
	            try {
	                if (logger.isInfoEnabled()) {
	                    logger.info("work() starting to index JAR " + jarFile.getName());
	                }
	                JarIndexer indexer = new JarIndexer(dataSource, pomInfo);
	                indexer.run(new String [] {jarFile.getCanonicalPath()});
	                indexer = null;
	            }
	            catch (Throwable t) {
	            	logger.error("work() error during JarIndexer run", t);
	            	System.err.println("work() error during JarIndexer run");
	                t.printStackTrace();
	            }
	                    
	            try {
	                logger.info("work() starting new ExtDependencyExtractor for " + jarFile.getName());
	                ExtDependencyExtractor epExtractor = new ExtDependencyExtractor(dataSource, pomInfo);
	                epExtractor.run(new String [] {jarFile.getCanonicalPath()});
	                epExtractor = null;
	            }
	            catch (Throwable t) {
	                logger.error("work() error during ExtDependencyExtractor run", t);
	                System.err.println("work() error during ExtDependencyExtractor run");
	                t.printStackTrace();
	            }
		    }
		    else if (logger.isInfoEnabled()) {
		        logger.info("work() skipping " + jarFile.getName() + " as it is already present in DB");
		    }
		    
            try {
                if (logger.isInfoEnabled()) {
                    logger.info("work() starting new APIndexer for " + jarFile.getName());
                }
                APIndexer indexer = new APIndexer(dataSource, pomInfo, jarFile);
                indexer.run(new String [] {jarFile.getCanonicalPath()});
                indexer = null;
            }
            catch (Throwable t) {
                logger.error("work() error during APIndexer run", t);
                System.err.println("work() error during APIndexer run");
                t.printStackTrace();
            }
		    
		}
		else {
			logger.error("work() no POM info found for JAR " + jarFile.getName() + " - skipping this JAR");
		}
		
		if (logger.isInfoEnabled()) {
			long now = System.currentTimeMillis();
			long secs = (now - beforeStart) / 1000;
			long min = 0;
			if (secs >= 60) {
				min = secs / 60;
				secs = secs - (min * 60);
			}
			logger.info("work() finished " + jarFile.getAbsolutePath() + " in " + min + ":" + secs + " min");
		}
	}
	
	private boolean jarPresentInDB(PomInfo pomInfo) {
		boolean jarPresent = false;
		
		ProductDao productDao = new ProductDao(this.dataSource);
		Product product = productDao.loadProduct(pomInfo.getGroupId());
		
		if (product != null) {
			JarDao jarDao = new JarDao(this.dataSource);
			Jar jar = jarDao.loadJar(pomInfo.getArtifactId(), pomInfo.getVersion(), product.getProductId());
			if (jar != null) {
				jarPresent = true;
				if (logger.isDebugEnabled()) {
					logger.debug("jarPresentInDB() found JAR " + pomInfo.getArtifactId() + " version " + pomInfo.getVersion() + " in database");
				}
			}
			else if (logger.isDebugEnabled()) {
				logger.debug("jarPresentInDB() JAR " + pomInfo.getArtifactId() + " version " + pomInfo.getVersion() + " already in database");
			}
		}
		else if (logger.isDebugEnabled()) {
			logger.debug("jarNotPresentInDB() could not find product of JAR - so this JAR is not present in DB");
		}
		
		return jarPresent;
	}

	public File getJarFile() {
		return jarFile;
	}

	public void setJarFile(File jarFile) {
		this.jarFile = jarFile;
	}

	public File getPomFile() {
		return pomFile;
	}

	public void setPomFile(File pomFile) {
		this.pomFile = pomFile;
	}

	public boolean hasRun() {
		return hasRun;
	}
	
	
}
