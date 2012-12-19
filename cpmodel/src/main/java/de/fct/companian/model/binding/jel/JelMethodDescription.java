package de.fct.companian.model.binding.jel;

import de.fct.companian.model.jel.JelMethod;
import de.fct.fdmm.apidl.MethodDescription;
import de.fct.fdmm.apidl.MethodSignature;
import de.fct.fdmm.apidl.TypeDescription;

public class JelMethodDescription implements MethodDescription {

    private final JelMethod jelMethod;
    private final JelTypeDescription typeDescription;
    
    public JelMethodDescription (JelMethod jelMethod, JelTypeDescription typeDescription) {
        this.jelMethod = jelMethod;
        this.typeDescription = typeDescription;
    }
    
    public TypeDescription getDefiningType() {
        return this.typeDescription;
    }

    public MethodSignature getSignature() {
        return new JelMethodSignature(this.jelMethod);
    }

    public String getName() {
        return this.getSignature().toString();
    }

}
