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
import de.fct.companian.model.jel.JelMethod;
import de.fct.companian.model.jel.JelMethods;
import de.fct.fdmm.apidl.MethodDescription;
import de.fct.fdmm.apidl.PackageDescription;
import de.fct.fdmm.apidl.TypeDescription;

public abstract class JelTypeDescription implements TypeDescription {

    protected final JelClass jelClass;
    protected final PackageDescription packageDescription;
    
    public JelTypeDescription(JelClass jelClass, PackageDescription packageDescription) {
        this.jelClass = jelClass;
        this.packageDescription = packageDescription;
    }
    
    public String getName() {
        return this.jelClass.getType();
    }
    
    public String getFqtn() {
        return this.jelClass.getFulltype();
    }
    
    public String getDescription() {
        if (this.jelClass.getComment() != null) {
            return this.jelClass.getComment().getDescription();
        }
        
        return null;
    }

    public PackageDescription getPackage() {
        return this.packageDescription;
    }

    public List<MethodDescription> getMethods() {
        List<JelMethods> jelMethods = this.jelClass.getMethods();
        
        List<MethodDescription> methods = new ArrayList<MethodDescription>();
        if (jelMethods != null) {
	        for (JelMethods jms : jelMethods) {
	            if (jms.getMethod() != null) {
	                for (JelMethod jelMethod : jms.getMethod()) {
	                    JelMethodDescription jelMethodDescription = new JelMethodDescription(jelMethod, this);
	                    methods.add(jelMethodDescription);
	                }
	            }
	        }
        }
        
        return methods;
    }
    
}
