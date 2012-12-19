package de.fct.companian.analyze.db.model;

public class ExtInstanceOf {

	private int instanceId;
	private String extFqcn;
	private int memberId;

	public int getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(int instanceId) {
		this.instanceId = instanceId;
	}

	public String getExtFqcn() {
		return extFqcn;
	}

	public void setExtFqcn(String extFqcn) {
		this.extFqcn = extFqcn;
	}

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}

}
