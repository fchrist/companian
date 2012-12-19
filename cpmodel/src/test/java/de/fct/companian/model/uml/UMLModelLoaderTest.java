package de.fct.companian.model.uml;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.eclipse.emf.common.util.URI;
import org.eclipse.uml2.uml.Package;

import de.fct.companian.model.binding.uml.UMLLoader;

public class UMLModelLoaderTest extends TestCase {

    private static final String [] TEST_FILES = {"../testdata/componentdiagrams/example1/component-test1.uml",
                                                 "../testdata/componentdiagrams/example1/component-test2.uml",
                                                 "../testdata/activitydiagrams/activity-test-1/activity-test-1.uml",
                                                 "../testdata/statemachines/example1/ProtocolExample.uml"};
    
    public void testLoadModel() {
        for (String file : TEST_FILES) {
            URI modelUri = URI.createFileURI(file);
            Package model = UMLLoader.loadUMLModel(modelUri);
            Assert.assertNotNull(model);            
        }
    }
    
}
