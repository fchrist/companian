package de.fct.companian.analyze.db.model;

public class Member {

	private int memberId;
	private String signature;
	private int classId;

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public int getClassId() {
		return classId;
	}

	public void setClassId(int classId) {
		this.classId = classId;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("Member[");
		sb.append("memberId=").append(this.memberId).append(",");
		sb.append("signature='").append(this.signature).append("',");
		sb.append("classId=").append(this.classId);
		sb.append("]");
		return sb.toString();
	}

}
