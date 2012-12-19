package de.fct.companian.model.binding.uml;

import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UMLLoader {

    private static Logger logger = LoggerFactory.getLogger(UMLLoader.class);
    
    public static Package loadUMLModel(URI modelUri) {
        Package umlPackage = null;
        ResourceSet resourceSet = createResourceSet();
        
        Resource modelResource = null;
        try {
            modelResource = resourceSet.getResource(modelUri, true);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        
        if (modelResource != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("loadUMLModel() loaded resource with contents=" + modelResource.getContents());
            }
            umlPackage = (Package) EcoreUtil.getObjectByType(modelResource.getContents(), UMLPackage.Literals.PACKAGE);          
        }
        
        return umlPackage;
    }
    
    private static ResourceSet createResourceSet() {
        ResourceSet resourceSet = new ResourceSetImpl();
        URI uri = URI.createURI("classpath:/");
        registerPathmaps(uri);      
        registerPackages(resourceSet);
        registerExtensions();
        
        return resourceSet;
    }
    
    protected static void registerPathmaps(URI umlResourcePluginURI) {
        URIConverter.URI_MAP.put(URI.createURI(UMLResource.LIBRARIES_PATHMAP), umlResourcePluginURI.appendSegment("libraries").appendSegment(""));
        URIConverter.URI_MAP.put(URI.createURI(UMLResource.METAMODELS_PATHMAP), umlResourcePluginURI.appendSegment("metamodels").appendSegment(""));
        URIConverter.URI_MAP.put(URI.createURI(UMLResource.PROFILES_PATHMAP), umlResourcePluginURI.appendSegment("profiles").appendSegment(""));
    }

    protected static void registerPackages(ResourceSet resourceSet) {
        Map<String, Object> packageRegistry = resourceSet.getPackageRegistry();
        packageRegistry.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
        packageRegistry.put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
        packageRegistry.put("http://www.eclipse.org/uml2/2.0.0/UML", UMLPackage.eINSTANCE);
    }

    protected static void registerExtensions() {
        Map<String, Object> extensionFactoryMap = Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap();
        extensionFactoryMap.put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
    }
}
