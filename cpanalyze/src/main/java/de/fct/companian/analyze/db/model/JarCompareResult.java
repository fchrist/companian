package de.fct.companian.analyze.db.model;

public class JarCompareResult {

    private String side;
    private String methodSignature;
    private int methodAccessFlags;
    private String fqcn;
    private int classAccessFlags;

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getMethodSignature() {
        return methodSignature;
    }

    public void setMethodSignature(String methodSignature) {
        this.methodSignature = methodSignature;
    }

    public int getMethodAccessFlags() {
        return methodAccessFlags;
    }

    public void setMethodAccessFlags(int methodAccessFlags) {
        this.methodAccessFlags = methodAccessFlags;
    }

    public String getFqcn() {
        return fqcn;
    }

    public void setFqcn(String fqcn) {
        this.fqcn = fqcn;
    }

    public int getClassAccessFlags() {
        return classAccessFlags;
    }

    public void setClassAccessFlags(int classAccessFlags) {
        this.classAccessFlags = classAccessFlags;
    }

}
