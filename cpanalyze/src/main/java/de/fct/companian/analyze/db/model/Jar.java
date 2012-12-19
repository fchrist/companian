package de.fct.companian.analyze.db.model;

public class Jar {

	private int jarId;
	private String jarname;
	private String artifact;
	private String version;
	
	private Product product;

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public int getJarId() {
		return jarId;
	}

	public void setJarId(int jarId) {
		this.jarId = jarId;
	}

	public String getJarname() {
		return jarname;
	}

	public void setJarname(String jarname) {
		this.jarname = jarname;
	}

	public String getArtifact() {
		return artifact;
	}

	public void setArtifact(String artifact) {
		this.artifact = artifact;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
