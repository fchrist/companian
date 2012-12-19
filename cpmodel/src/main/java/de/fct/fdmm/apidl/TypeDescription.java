package de.fct.fdmm.apidl;

import java.util.List;

import de.fct.fdmm.basis.IDescription;
import de.fct.fdmm.basis.INamedElement;

public interface TypeDescription extends IDescription, INamedElement {
    
    public String getFqtn();
    
    public PackageDescription getPackage();
    
    public List<MethodDescription> getMethods();

}
