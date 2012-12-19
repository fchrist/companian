package de.fct.fdmm.apidl;

import java.util.List;

import de.fct.fdmm.basis.IDescription;

public interface MethodSignature extends IDescription {
    
    public TypeDescription getReturnType();

    public List<TypeDescription> getParameters();

    public List<ExceptionDescription> getExceptions();

}
