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
