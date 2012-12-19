package de.fct.companian.model.binding.jel;

import java.util.ArrayList;
import java.util.List;

import de.fct.companian.model.jel.JelClass;
import de.fct.companian.model.jel.JelMethod;
import de.fct.companian.model.jel.JelMethods;
import de.fct.fdmm.apidl.MethodDescription;
import de.fct.fdmm.apidl.PackageDescription;
import de.fct.fdmm.apidl.TypeDescription;

public abstract class JelTypeDescription implements TypeDescription {

    protected final JelClass jelClass;
    protected final PackageDescription packageDescription;
    
    public JelTypeDescription(JelClass jelClass, PackageDescription packageDescription) {
        this.jelClass = jelClass;
        this.packageDescription = packageDescription;
    }
    
    public String getName() {
        return this.jelClass.getType();
    }
    
    public String getFqtn() {
        return this.jelClass.getFulltype();
    }
    
    public String getDescription() {
        if (this.jelClass.getComment() != null) {
            return this.jelClass.getComment().getDescription();
        }
        
        return null;
    }

    public PackageDescription getPackage() {
        return this.packageDescription;
    }

    public List<MethodDescription> getMethods() {
        List<JelMethods> jelMethods = this.jelClass.getMethods();
        
        List<MethodDescription> methods = new ArrayList<MethodDescription>();
        if (jelMethods != null) {
	        for (JelMethods jms : jelMethods) {
	            if (jms.getMethod() != null) {
	                for (JelMethod jelMethod : jms.getMethod()) {
	                    JelMethodDescription jelMethodDescription = new JelMethodDescription(jelMethod, this);
	                    methods.add(jelMethodDescription);
	                }
	            }
	        }
        }
        
        return methods;
    }
    
}
