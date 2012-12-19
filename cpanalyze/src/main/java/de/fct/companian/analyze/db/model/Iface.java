package de.fct.companian.analyze.db.model;

public class Iface {

	String fqin;

	public String getFqin() {
		return fqin;
	}

	public void setFqin(String fqin) {
		this.fqin = fqin;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer("Iface[");
		buf.append("fqin=").append(fqin).append("]");

		return buf.toString();
	}
}
