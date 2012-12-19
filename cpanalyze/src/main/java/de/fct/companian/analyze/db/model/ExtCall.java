package de.fct.companian.analyze.db.model;

public class ExtCall {

	private int callId;
	private String extFqcn;
	private String extSignature;
	private Integer classId;
	private Integer methodId;

	public int getCallId() {
		return callId;
	}

	public void setCallId(int callId) {
		this.callId = callId;
	}

	public String getExtFqcn() {
		return extFqcn;
	}

	public void setExtFqcn(String extFqcn) {
		this.extFqcn = extFqcn;
	}

	public String getExtSignature() {
		return extSignature;
	}

	public void setExtSignature(String extSignature) {
		this.extSignature = extSignature;
	}

	public Integer getClassId() {
		return classId;
	}

	public void setClassId(Integer classId) {
		this.classId = classId;
	}

	public Integer getMethodId() {
		return methodId;
	}

	public void setMethodId(Integer methodId) {
		this.methodId = methodId;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer("ExtCall[");
		buf.append("callId=").append(callId).append(",");
		buf.append("extFqcn=").append(extFqcn).append(",");
		buf.append("extSignature=").append(extSignature).append(",");
		buf.append("classId=").append(classId).append(",");
		buf.append("methodId=").append(methodId).append("]");

		return buf.toString();
	}

}
