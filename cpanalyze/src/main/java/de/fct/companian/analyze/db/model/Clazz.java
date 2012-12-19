package de.fct.companian.analyze.db.model;

import java.util.List;

import com.jeantessier.classreader.Classfile;

public class Clazz {

	private int classId;
	private String fqcn;
	private int accessFlags;
	private int jarId;
	private String superFqcn;
	private List<Iface> interfaces;

	public boolean isInterface() {
		return (accessFlags & Classfile.ACC_INTERFACE) != 0;
	}
	
	public int getClassId() {
		return classId;
	}

	public void setClassId(int classId) {
		this.classId = classId;
	}

	public String getFqcn() {
		return fqcn;
	}

	public void setFqcn(String fqcn) {
		this.fqcn = fqcn;
	}
	
	public int getAccessFlags() {
		return accessFlags;
	}

	public void setAccessFlags(int accessFlags) {
		this.accessFlags = accessFlags;
	}

	public int getJarId() {
		return jarId;
	}

	public void setJarId(int jarId) {
		this.jarId = jarId;
	}

	public String getSuperFqcn() {
		return superFqcn;
	}

	public void setSuperFqcn(String parentFqcn) {
		this.superFqcn = parentFqcn;
	}
	
	public List<Iface> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(List<Iface> interfaces) {
		this.interfaces = interfaces;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer("Clazz[");
		buf.append("classId=").append(classId).append(",");
		buf.append("jarId=").append(jarId).append(",");
		buf.append("fqcn=").append(fqcn).append(",");
		buf.append("superFqcn=").append(superFqcn).append(",");
		buf.append("interfaces={");
		if (interfaces != null) {
			boolean first = true;
			for (Iface iface : interfaces) {
				if (first) {
					buf.append(iface);
				}
				buf.append(",").append(iface);
				first = false;
			}
		}
		buf.append("}").append("]");

		return buf.toString();
	}
}
