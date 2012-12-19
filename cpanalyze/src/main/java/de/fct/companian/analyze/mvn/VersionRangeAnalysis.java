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
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;

import de.fct.companian.analyze.mvn.helper.MvnProjectBuilder;

public class VersionRangeAnalysis {

	private static Logger logger = Logger.getLogger(VersionRangeAnalysis.class);
	
	public void analyseVersionRanges(File pomFile) {
		MavenProject mvnProject = MvnProjectBuilder.buildMavenProject(pomFile);
		this.analyseVersionRange(mvnProject);
	}
	
	public void analyseVersionRange(MavenProject mvnProject) {
		logger.info("analyseVersionRanges() start");
		if (mvnProject != null) {
			logger.info("analyseVersionRanges() project=" + mvnProject.getArtifact().getArtifactId());
			Set<Artifact> artifactSet = mvnProject.getArtifacts();
			boolean versionRangeUsed = false;
			if (artifactSet != null) {
				for (Artifact artifact : artifactSet) {
					if (!artifact.getScope().equalsIgnoreCase(Artifact.SCOPE_TEST)) {
						if (artifact.getType().equalsIgnoreCase("jar")) {
							if (isRangeSet(artifact.getVersionRange().toString())) {
								logger.info("analyseVersionRanges() artifact=" + artifact.getArtifactId() + ", versionRange=" + artifact.getVersionRange());
								versionRangeUsed = true;
							}
						}
					}
				}
			}
			else {
				logger.info("analyseVersionRanges() no artifacts found for this Maven project");
			}
			
			if (!versionRangeUsed) {
				logger.info("analyseVersionRanges() no version ranges where used by the dependencies of project " + mvnProject.getArtifact().getDependencyConflictId());
			}
		}
		else {
			logger.error("analyseVersionRanges() no Maven project found");
		}
		logger.info("analyseVersionRanges() finished");	
	}
	
	private boolean isRangeSet(String versionRange) {
		boolean range = false;
		if (versionRange.contains(",")) {
			range = true;
		}
		
		return range;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args != null && args.length == 1) {
			File pomFile = new File(args[0]);
			if (pomFile != null) {
				if (pomFile.exists() && pomFile.canRead()) {
					VersionRangeAnalysis vra = new VersionRangeAnalysis();
					vra.analyseVersionRanges(pomFile);
				}
				else {
					System.err.println("Error reading file " + args[0]);
				}
			}
			else {
				System.err.println("Error opening file " + args[0]);
			}
		}
		else {
			System.err.println("No arguments given.");
		}
	}

}
