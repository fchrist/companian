/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
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
