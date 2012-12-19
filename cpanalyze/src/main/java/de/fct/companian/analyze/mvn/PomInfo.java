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

public class PomInfo {

	private String jarName;
	private String groupId;
	private String artifactId;
	private String version;

	public PomInfo() {
		// default constructor
	}

	public PomInfo(String jarName, String groupId, String artifactId, String version) {
		super();
		this.jarName = jarName;
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
	}

	public String getJarName() {
		return jarName;
	}

	public void setJarName(String jarName) {
		this.jarName = jarName;
	}	
	
	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("PomInfo[");
		sb.append("groupId=").append(this.groupId).append(",");
		sb.append("artifacId=").append(this.artifactId).append(",");
		sb.append("version=").append(this.version);
		sb.append("]");
		
		return sb.toString();
	}

}
