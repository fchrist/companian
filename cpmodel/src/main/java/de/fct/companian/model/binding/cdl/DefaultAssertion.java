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
package de.fct.companian.model.binding.cdl;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import de.fct.fdmm.constraintdl.Assertion;

public class DefaultAssertion implements Assertion {
    
    private String name;
    private List<String> parameters;

    @XmlAttribute
    public String getName() {
        return this.name;
    }
    
    @XmlElement
    public List<String> getParameters() {
        return this.parameters;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }
    
    public String getSignature() {
        String pl = this.name + "(";
        if (this.parameters != null && !this.parameters.isEmpty()) {
            for (int i=0; i<this.parameters.size(); i++) {
                if (i > 0) {
                    pl += ", ";
                }
                pl += this.parameters.get(i);
            }
        }
        pl += ")";
        return pl;
    }

}
