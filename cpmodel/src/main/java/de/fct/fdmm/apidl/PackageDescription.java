package de.fct.fdmm.apidl;

import java.util.List;

import de.fct.fdmm.basis.IDescription;
import de.fct.fdmm.basis.INamedElement;

public interface PackageDescription extends IDescription, INamedElement {

    public List<TypeDescription> getOwnedTypes();
    
    public List<ClassDescription> getOwnedClasses();
    public List<InterfaceDescription> getOwnedInterfaces();
    public List<ExceptionDescription> getOwnedExceptions();

}
