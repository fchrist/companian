package de.fct.fdmm.apidl;


public interface APIDlProxy {

    public APIDoc getAPIDoc(String apiDocPath);
    public MethodDescription getMethodDescription(String methodAPIPath);
    public TypeDescription getTypeDescription(String typeAPIPath);
    
}
