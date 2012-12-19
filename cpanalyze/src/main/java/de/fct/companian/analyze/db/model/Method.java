package de.fct.companian.analyze.db.model;

public class Method {

	private int methodId;
	private int accessFlags;
	private boolean constructor;
	private String returnType;
	private String signature;
	private int classId;

	public int getMethodId() {
		return methodId;
	}

	public void setMethodId(int methodId) {
		this.methodId = methodId;
	}

	public int getAccessFlags() {
		return accessFlags;
	}

	public void setAccessFlags(int accessFlags) {
		this.accessFlags = accessFlags;
	}

	public boolean isConstructor() {
		return constructor;
	}

	public void setConstructor(boolean constructor) {
		this.constructor = constructor;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
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
		StringBuffer sb = new StringBuffer("Method[");
		sb.append("accessFlags=").append(this.accessFlags).append(",");
		sb.append("constructor=").append(this.constructor).append(",");
		sb.append("returnType=").append(this.returnType).append(",");
		sb.append("signature=").append(this.signature).append(",");
		sb.append("classId=").append(this.classId);
		sb.append("]");
		return sb.toString();
	}
}
