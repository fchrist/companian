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
package de.fct.companian.model.binding.jel;

import java.util.ArrayList;
import java.util.List;

import de.fct.companian.model.jel.JelClass;
import de.fct.fdmm.apidl.ClassDescription;
import de.fct.fdmm.apidl.ExceptionDescription;
import de.fct.fdmm.apidl.InterfaceDescription;
import de.fct.fdmm.apidl.PackageDescription;
import de.fct.fdmm.apidl.TypeDescription;

public class JelPackageDescription implements PackageDescription {

    private final String name;
    private final List<JelClass> jelClasses;
    
    private final List<TypeDescription> ownedTypes;
    private final List<InterfaceDescription> ownedInterfaces;
    private final List<ClassDescription> ownedClasses;

    public JelPackageDescription(String name, List<JelClass> jelClasses) {
        this.name = name;
        this.jelClasses = jelClasses;
        
        this.ownedTypes = new ArrayList<TypeDescription>();
        this.ownedInterfaces = new ArrayList<InterfaceDescription>();
        this.ownedClasses = new ArrayList<ClassDescription>();
        
        if (this.jelClasses != null) {
            for (JelClass jelClass : this.jelClasses) {
                if (jelClass.getInterface()) {
                    JelInterfaceDescription jelid = new JelInterfaceDescription(jelClass, this);
                    this.ownedTypes.add(jelid);
                    this.ownedInterfaces.add(jelid);
                }
                else {
                    JelClassDescription jelcd = new JelClassDescription(jelClass, this);
                    this.ownedTypes.add(jelcd);
                    this.ownedClasses.add(jelcd);
                }
            }
        }
    }

    public String getName() {
        return this.name;
    }
    
    public String getDescription() {
        return null;
    }
    
    public List<ClassDescription> getOwnedClasses() {
        return this.ownedClasses;
    }

    public List<ExceptionDescription> getOwnedExceptions() {
        return null;
    }

    public List<InterfaceDescription> getOwnedInterfaces() {
        return this.ownedInterfaces;
    }

    public List<TypeDescription> getOwnedTypes() {
        return this.ownedTypes;
    }

}
