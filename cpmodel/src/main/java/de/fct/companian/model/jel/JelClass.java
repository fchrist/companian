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
package de.fct.companian.model.jel;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class JelClass {

    private JelComment comment;

    private String fulltype;
    private String type;
    private String jelPackage;
    private String visibility;
    private Boolean jelAbstract;
    private Boolean jelSerializable;
    private Boolean jelInterface;
    private String superclass;
    private String superclassfulltype;
    
    private List<JelMethods> methods;
    
    @XmlElement
    public JelComment getComment() {
        return comment;
    }

    public void setComment(JelComment comment) {
        this.comment = comment;
    }

    @XmlAttribute(name="abstract")
    public Boolean getAbstract() {
        if (this.jelAbstract != null) {
            return this.jelAbstract;
        }
        
        return false;
    }
    
    public void setAbstract(Boolean jelAbstract) {
        this.jelAbstract = jelAbstract;
    }

    @XmlAttribute
    public String getFulltype() {
        return fulltype;
    }

    public void setFulltype(String fulltype) {
        this.fulltype = fulltype;
    }

    @XmlAttribute(name="interface")
    public Boolean getInterface() {
        if (this.jelInterface != null) {
            return this.jelInterface;
        }
        
        return false;
    }

    public void setInterface(Boolean jelInterface) {
        this.jelInterface = jelInterface;
    }

    @XmlAttribute
    public String getPackage() {
        return jelPackage;
    }

    public void setPackage(String jelPackage) {
        this.jelPackage = jelPackage;
    }

    @XmlAttribute(name="serializable")
    public Boolean getSerializable() {
        if (this.jelSerializable != null) {
            return this.jelSerializable;
        }
        
        return false;
    }

    public void setSerializable(Boolean jelSerializable) {
        this.jelSerializable = jelSerializable;
    }

    @XmlAttribute
    public String getSuperclass() {
        return superclass;
    }

    public void setSuperclass(String superclass) {
        this.superclass = superclass;
    }

    @XmlAttribute
    public String getSuperclassfulltype() {
        return superclassfulltype;
    }

    public void setSuperclassfulltype(String superclassfulltype) {
        this.superclassfulltype = superclassfulltype;
    }

    @XmlAttribute(name="type")
    public String getType() {
        return type;
    }

    public void setType(String name) {
        this.type = name;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    @XmlElement
    public List<JelMethods> getMethods() {
        return methods;
    }

    public void setMethods(List<JelMethods> methods) {
        this.methods = methods;
    }
    
}
