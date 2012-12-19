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

import java.io.File;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.dom4j.DocumentException;

import de.fct.companian.analyze.helper.DbHelper;
import de.fct.companian.analyze.mvn.PomInfo;
import de.fct.companian.analyze.mvn.helper.MvnProjectBuilder;
import de.fct.companian.analyze.mvn.helper.PomHelper;

public class DepProver {

	private static Logger logger = Logger.getLogger(DepProver.class);

	private DataSource dataSource;

	public void prove(String pomFileName) {
		long beforeStart = System.currentTimeMillis();
		logger.info("prove() start " + pomFileName);
		
		dataSource = DbHelper.createDataSource(null);
		if (dataSource != null) {
			File pomFile = new File(pomFileName);
			if (pomFile.exists() && pomFile.isFile() && pomFile.canRead()) {
				PomInfo pomInfo = null;
				try {
					PomHelper pomHelper = new PomHelper(pomFile);
					pomInfo = pomHelper.extractPomInfo();
				}
				catch (DocumentException e) {
					logger.error("prove() error extracting POM info", e);
				}
				
				if (pomInfo != null) {
					MavenProject mvnProject = MvnProjectBuilder.buildMavenProject(pomFile);
					if (mvnProject != null) {
						Set<Artifact> artifactSet = mvnProject.getArtifacts();
						if (artifactSet != null) {
							JarProver jarProver = new JarProver(dataSource);
							jarProver.prove(pomInfo, artifactSet);
						}
						else {
							logger.error("prove() no dependency artifacts returned by Maven");
						}
					}
					else {
						logger.error("prove() no Maven project returned");
					}
				}
			}
		} else {
			logger.error("prove() data source creation failed");
			System.err.println("Error creating data source.");
			System.exit(-1);
		}
		
		if (logger.isInfoEnabled()) {
			long now = System.currentTimeMillis();
			long secs = (now - beforeStart) / 1000;
			long min = 0;
			if (secs >= 60) {
				min = secs / 60;
				secs = secs - (min * 60);
			}
			logger.info("prove() finished in " + min + ":" + secs + " min");
		}
	}
	
}
