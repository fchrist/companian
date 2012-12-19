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
package de.fct.fdmm.hotspot;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import de.fct.fdmm.apidl.APIDlProxy;
import de.fct.fdmm.apidl.MethodDescription;
import de.fct.fdmm.basis.INamedElement;

public class HookCall implements INamedElement {

    private String methodAPIPath;
    private List<Constraint> constraints;
    
    @XmlElement
    public String getMethodAPIPath() {
        return methodAPIPath;
    }

    public void setMethodAPIPath(String methodUri) {
        this.methodAPIPath = methodUri;
    }
    
    public String getSignature() {
        int lastSlash = this.methodAPIPath.lastIndexOf('/');
        String signature = this.methodAPIPath.substring(lastSlash+1);
        return signature;
    }

    public MethodDescription getMethod(APIDlProxy proxy) {
        return proxy.getMethodDescription(this.methodAPIPath);
    }

    @XmlElement
    public List<Constraint> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<Constraint> constraints) {
        this.constraints = constraints;
    }

    public String getName() {
        return this.getSignature();
    }
    
    public String toString() {
    	return this.getSignature();
    }

}
