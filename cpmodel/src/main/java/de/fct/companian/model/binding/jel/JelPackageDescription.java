package de.fct.companian.model.binding.jel;

import java.util.ArrayList;
import java.util.List;

import de.fct.companian.model.jel.JelClass;
import de.fct.fdmm.apidl.ClassDescription;
import de.fct.fdmm.apidl.ExceptionDescription;
import de.fct.fdmm.apidl.InterfaceDescription;
import de.fct.fdmm.apidl.PackageDescription;
import de.fct.fdmm.apidl.TypeDescription;

public class JelPackageDescription implements PackageDescription {

    private final String name;
    private final List<JelClass> jelClasses;
    
    private final List<TypeDescription> ownedTypes;
    private final List<InterfaceDescription> ownedInterfaces;
    private final List<ClassDescription> ownedClasses;

    public JelPackageDescription(String name, List<JelClass> jelClasses) {
        this.name = name;
        this.jelClasses = jelClasses;
        
        this.ownedTypes = new ArrayList<TypeDescription>();
        this.ownedInterfaces = new ArrayList<InterfaceDescription>();
        this.ownedClasses = new ArrayList<ClassDescription>();
        
        if (this.jelClasses != null) {
            for (JelClass jelClass : this.jelClasses) {
                if (jelClass.getInterface()) {
                    JelInterfaceDescription jelid = new JelInterfaceDescription(jelClass, this);
                    this.ownedTypes.add(jelid);
                    this.ownedInterfaces.add(jelid);
                }
                else {
                    JelClassDescription jelcd = new JelClassDescription(jelClass, this);
                    this.ownedTypes.add(jelcd);
                    this.ownedClasses.add(jelcd);
                }
            }
        }
    }

    public String getName() {
        return this.name;
    }
    
    public String getDescription() {
        return null;
    }
    
    public List<ClassDescription> getOwnedClasses() {
        return this.ownedClasses;
    }

    public List<ExceptionDescription> getOwnedExceptions() {
        return null;
    }

    public List<InterfaceDescription> getOwnedInterfaces() {
        return this.ownedInterfaces;
    }

    public List<TypeDescription> getOwnedTypes() {
        return this.ownedTypes;
    }

}
