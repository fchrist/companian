package de.fct.fdmm.apidl;

import java.util.List;

public interface ClassDescription extends TypeDescription {
    
    public ClassDescription getExtClass();

    public List<InterfaceDescription> getInterfaces();

}
