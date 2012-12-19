package de.fct.companian.analyze.db.model;

public class ExtImplements {

	private int implementsId;
	private String extFqcn;
	private int classId;

	public int getImplementsId() {
		return implementsId;
	}

	public void setImplementsId(int implementsId) {
		this.implementsId = implementsId;
	}

	public String getExtFqcn() {
		return extFqcn;
	}

	public void setExtFqcn(String extFqcn) {
		this.extFqcn = extFqcn;
	}

	public int getClassId() {
		return classId;
	}

	public void setClassId(int classId) {
		this.classId = classId;
	}

}
