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
import javax.xml.bind.annotation.XmlTransient;

import de.fct.fdmm.apidl.APIDlProxy;
import de.fct.fdmm.apidl.TypeDescription;

public class CodingUnit extends HotSpotUnit {

    private List<HookCall> hooks;
    private List<Constraint> constraints;
    private List<HookProtocol> protocols;
    private String typeAPIPath;

    @XmlElement
    public String getTypeAPIPath() {
        return typeAPIPath;
    }

    public void setTypeAPIPath(String typeAPIPath) {
        this.typeAPIPath = typeAPIPath;
    }
    
    public String getTypeName() {
        int lastSlash = this.typeAPIPath.lastIndexOf('/');
        String typeName = this.typeAPIPath.substring(lastSlash+1);
        return typeName;
    }

    public TypeDescription getTypeDescription(APIDlProxy proxy) {
        return proxy.getTypeDescription(this.typeAPIPath);
    }

    @XmlElement
    public List<HookCall> getHooks() {
        return hooks;
    }

    public void setHooks(List<HookCall> hooks) {
        this.hooks = hooks;
    }

    @XmlTransient
    public List<Constraint> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<Constraint> constraints) {
        this.constraints = constraints;
    }

    public List<HookProtocol> getProtocols() {
        return protocols;
    }

    public void setProtocols(List<HookProtocol> protocols) {
        this.protocols = protocols;
    }

}
