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
