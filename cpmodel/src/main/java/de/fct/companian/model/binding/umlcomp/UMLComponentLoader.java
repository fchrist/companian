package de.fct.companian.model.binding.umlcomp;

import org.eclipse.emf.common.util.URI;
import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.UMLPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.model.binding.uml.UMLLoader;

public class UMLComponentLoader {

    private static Logger logger = LoggerFactory.getLogger(UMLComponentLoader.class);
    
    public static Component loadComponent(URI modelUri) {
        org.eclipse.uml2.uml.Package model = UMLLoader.loadUMLModel(modelUri);
        
        Component umlComponent = null;
        if (model != null) {
            umlComponent = (Component)model.getPackagedElement(null, true, UMLPackage.Literals.COMPONENT, false);
        }
        
        if (umlComponent != null && logger.isDebugEnabled()) {
            logger.debug("loadComponent() loaded component " + umlComponent.getLabel());
        }
        
        return umlComponent;
    }

}
