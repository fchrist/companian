package de.fct.companian.model.uml;

import java.util.Iterator;

import junit.framework.Assert;
import junit.framework.TestCase;
import de.fct.companian.model.binding.umlcomp.UMLCompArchitectureDlProxy;
import de.fct.fdmm.architecturedl.ArchitectureDescription;
import de.fct.fdmm.architecturedl.ArchitectureElement;

public class UMLComponentTest extends TestCase {

    private static final String[] TEST_FILES = {"../testdata/componentdiagrams/example1/component-test1.uml",
                                                "../testdata/componentdiagrams/example1/component-test2.uml"};

    public void testLoadViaProxy() {
        for (String file : TEST_FILES) {
            UMLCompArchitectureDlProxy proxy = new UMLCompArchitectureDlProxy();
            ArchitectureDescription ad = proxy.getArchitectureDescription(file);
            Assert.assertNotNull(ad);            
        }
    }
    
    public void testTraverse() {
        for (String file : TEST_FILES) {
            UMLCompArchitectureDlProxy proxy = new UMLCompArchitectureDlProxy();
            ArchitectureDescription ad = proxy.getArchitectureDescription(file);
            Assert.assertNotNull(ad);
            
            traverseArchitecture(ad);            
        }
    }
    
    private void traverseArchitecture(ArchitectureDescription ad) {
        if (ad != null) {
            assertNotNull(ad.getSubElements());
            for (ArchitectureElement ae : ad.getSubElements()) {
                assertNotNull(ae.getName());
                traverseArchitectureElement(ae);
            }
        }
    }
    
    private void traverseArchitectureElement(ArchitectureElement ae) {
        assertNotNull(ae.getDependencies());
        for (ArchitectureElement depAe : ae.getDependencies()) {
            assertNotNull(depAe.getName());
        }
        traverseArchitecture(ae.getSubArchitecture());
    }

    public void testHotSpotGroups() {
        UMLCompArchitectureDlProxy proxy = new UMLCompArchitectureDlProxy();
        ArchitectureDescription ad = proxy.getArchitectureDescription(TEST_FILES[0]);
        assertNotNull(ad);
        assertNotNull(ad.getSubElements());
        assertEquals(2, ad.getSubElements().size());
        
        Iterator<ArchitectureElement> it = ad.getSubElements().iterator();
        while (it.hasNext()) {
            ArchitectureElement ae = it.next();
            if (ae.getName() != null && ae.getName().equals("number1")) {
                assertNotNull(ae);
                assertNotNull(ae.getHotSpotGroups());
                assertEquals(1, ae.getHotSpotGroups().size());
                assertEquals("hotSpotGroupName", ae.getHotSpotGroups().iterator().next());
                break;
            }
        }
    }
    
}
