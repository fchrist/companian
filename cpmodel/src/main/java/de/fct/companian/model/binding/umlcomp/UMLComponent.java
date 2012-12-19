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
import org.eclipse.uml2.uml.Dependency;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Port;
import org.eclipse.uml2.uml.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.fdmm.architecturedl.ArchitectureDescription;
import de.fct.fdmm.architecturedl.ArchitectureElement;

public class UMLComponent implements ArchitectureElement {

    private static Logger logger = LoggerFactory.getLogger(UMLComponent.class);
    
    private final Component component;
    
    private Set<ArchitectureElement> dependencies;
    
    private ArchitectureDescription subArchitecture;
    
    private Set<String> hotSpotGroups;
    
    public UMLComponent(Component component) {
        this.component = component;
    }
    
    public String getName() {
        if (this.component.getName() == null || this.component.getName().isEmpty()) {
            return "id" + this.component.hashCode();
        }
        return this.component.getName();
    }
    
    public Set<ArchitectureElement> getDependencies() {
        if (this.dependencies == null) {
            this.dependencies = new HashSet<ArchitectureElement>();
            
            for (Dependency dependency : this.component.getClientDependencies()) {
                if (dependency.getSuppliers() != null && dependency.getSuppliers().size() == 1) {
                    Component depComponent = (Component)dependency.getSuppliers().get(0);
                    this.dependencies.add(new UMLComponent(depComponent));
                }
                else {
                    logger.warn("getDependencies() could not handle client dependencies with number of clients > 1");
                }
            }            
        }
        
        return this.dependencies;
    }

    public ArchitectureDescription getSubArchitecture() {
        if (this.subArchitecture == null) {
            Set<ArchitectureElement> subElements = new HashSet<ArchitectureElement>();
            if (this.component.getPackagedElements() != null) {
                for (PackageableElement pg : this.component.getPackagedElements()) {
                    if (pg instanceof Component) {
                        Component subComponent = (Component) pg;
                        subElements.add(new UMLComponent(subComponent));
                    }
                }
            }
            this.subArchitecture = new UMLCompArchitecture(subElements);
        }

        return this.subArchitecture;
    }

    public Set<String> getHotSpotGroups() {
        if (this.hotSpotGroups == null) {
            this.hotSpotGroups = new HashSet<String>();
            if (this.component.getOwnedAttributes() != null) {
                for (Property attribute : this.component.getOwnedAttributes()) {
                    if (attribute instanceof Port) {
                        Port port = (Port) attribute;
                        this.hotSpotGroups.add(port.getName());
                    }
                }
            }            
        }
        return this.hotSpotGroups;
    }
    
}
