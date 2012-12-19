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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.PackageableElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.model.binding.uml.UMLLoader;
import de.fct.fdmm.architecturedl.ArchitectureDescription;
import de.fct.fdmm.architecturedl.ArchitectureDlProxy;
import de.fct.fdmm.architecturedl.ArchitectureElement;

public class UMLCompArchitectureDlProxy implements ArchitectureDlProxy {

    private static Logger logger = LoggerFactory.getLogger(UMLCompArchitectureDlProxy.class);

    public ArchitectureDescription getArchitectureDescription(String architectureDescriptionFile) {
        if (logger.isDebugEnabled()) {
            logger.debug("getArchitectureDescription() for " + architectureDescriptionFile);
        }
        UMLCompArchitecture architecture = null;
        org.eclipse.emf.common.util.URI emfModelUri = org.eclipse.emf.common.util.URI
                .createURI(architectureDescriptionFile);
        org.eclipse.uml2.uml.Package umlModel = UMLLoader.loadUMLModel(emfModelUri);

        Set<ArchitectureElement> subElements = new HashSet<ArchitectureElement>();
        if (umlModel.getPackagedElements() != null) {
            for (PackageableElement pe : umlModel.getPackagedElements()) {
                if (pe instanceof Component) {
                    Component component = (Component) pe;
                    subElements.add(new UMLComponent(component));
                }
            }
        }

        architecture = new UMLCompArchitecture(subElements);

        return architecture;
    }

}
