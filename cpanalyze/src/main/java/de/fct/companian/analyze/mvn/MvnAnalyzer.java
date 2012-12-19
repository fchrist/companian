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
package de.fct.companian.analyze.mvn;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.dom4j.DocumentException;

import de.fct.companian.analyze.Analyzer;
import de.fct.companian.analyze.mvn.helper.MvnProjectBuilder;
import de.fct.companian.analyze.mvn.helper.PomHelper;

/**
 * 
 * 
 * @author Fabian Christ
 */
public class MvnAnalyzer {

	private static Logger logger = Logger.getLogger(MvnAnalyzer.class);

	public static void main(String[] args) {
		long beforeStart = System.currentTimeMillis();
		logger.info("main() start");
		if (args != null && args.length > 0) {
			PomInfo pomInfo = null;
			File pomFile = new File(args[0]);
			try {
				PomHelper pomHelper = new PomHelper(pomFile);
				pomInfo = pomHelper.extractPomInfo();

			}
			catch (DocumentException e) {
				logger.error("main() error extracting POM info", e);
			}

			if (pomInfo != null) {
				if (logger.isInfoEnabled()) {
					logger.info("main() analyzing " + pomFile.getParentFile());
				}
				new Analyzer().analyze(pomFile.getParentFile().getAbsolutePath(), null, null);
				
				if (logger.isInfoEnabled()) {
					logger.info("main() building Maven project");
				}
				MavenProject mvnProject = MvnProjectBuilder.buildMavenProject(pomFile);
				if (mvnProject != null) {
					Set<Artifact> artifactSet = mvnProject.getArtifacts();
					if (artifactSet != null) {
						if (logger.isDebugEnabled()) {
							logger.debug("main() found " + artifactSet.size() + " artifacts");
						}
						for (Artifact artifact : artifactSet) {
							if (!artifact.getScope().equalsIgnoreCase(Artifact.SCOPE_TEST)) {
								if (artifact.getType().equalsIgnoreCase("jar")) {
									File jarFile = artifact.getFile();
									if (jarFile != null) {
										// File parentFile = new File(jarFile.getParentFile(), "../");
										File parentFile = jarFile.getParentFile();
										String parentPath = null;
										try {
											parentPath = parentFile.getCanonicalPath();
											if (parentPath != null) {
												if (logger.isInfoEnabled()) {
													logger.info("main() analyzing " + parentPath);
												}
												new Analyzer().analyze(parentPath, null, null);
											}
										}
										catch (IOException e) {
											logger.error("main() could not get parent artifact path of " + jarFile.getAbsolutePath(), e);
										}
									}
								}
							}
						}
					}
				}
				else {
					logger.error("main() no project returned");
				}
			}
			else {
				logger.error("main() no POM info found");
			}
		}
		else {
			logger.error("main() no arguments given");
			System.err.println("No arguments given.");
		}

		if (logger.isInfoEnabled()) {
			long now = System.currentTimeMillis();
			long secs = (now - beforeStart) / 1000;
			long min = 0;
			if (secs >= 60) {
				min = secs / 60;
				secs = secs - (min * 60);
			}
			logger.info("main() finished in " + min + ":" + secs + " min");
		}
	}

}
