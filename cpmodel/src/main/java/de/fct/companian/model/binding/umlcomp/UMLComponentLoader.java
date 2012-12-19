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
