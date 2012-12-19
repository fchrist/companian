package de.fct.companian.model.binding.jel;

import de.fct.companian.model.jel.JelClass;
import de.fct.fdmm.apidl.InterfaceDescription;
import de.fct.fdmm.apidl.PackageDescription;

public class JelInterfaceDescription extends JelTypeDescription implements InterfaceDescription {

    public JelInterfaceDescription(JelClass jelClass, PackageDescription packageDescription) {
        super(jelClass, packageDescription);
    }
    
}
