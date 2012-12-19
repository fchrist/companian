package de.fct.companian.model.binding.jel;

import java.util.List;

import de.fct.companian.model.jel.JelClass;
import de.fct.fdmm.apidl.ClassDescription;
import de.fct.fdmm.apidl.InterfaceDescription;
import de.fct.fdmm.apidl.PackageDescription;

public class JelClassDescription extends JelTypeDescription implements ClassDescription {

    public JelClassDescription(JelClass jelClass, PackageDescription packageDescription) {
        super(jelClass, packageDescription);
    }

    public ClassDescription getExtClass() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<InterfaceDescription> getInterfaces() {
        // TODO Auto-generated method stub
        return null;
    }

}
