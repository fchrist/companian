package de.fct.companian.use;

import de.fct.companian.analyze.db.model.Clazz;

public class ClassUseConfidence {

    private int classId;
    private double confidence;
    
    private Clazz clazz;
    
    public ClassUseConfidence() {};
    
    public ClassUseConfidence(int classId, double confidence) {
        this.classId = classId;
        this.confidence = confidence;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public Clazz getClazz() {
        return clazz;
    }

    public void setClazz(Clazz clazz) {
        this.clazz = clazz;
    }
    
}
