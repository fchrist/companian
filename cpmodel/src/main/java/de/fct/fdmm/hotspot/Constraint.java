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

import javax.xml.bind.annotation.XmlElement;

import de.fct.companian.model.binding.cdl.DefaultAssertion;
import de.fct.fdmm.basis.Description;
import de.fct.fdmm.constraintdl.Assertion;

public class Constraint extends Description {

    private Assertion assertion;

    @XmlElement(type=DefaultAssertion.class)
    public Assertion getAssertion() {
        return assertion;
    }

    public void setAssertion(Assertion function) {
        this.assertion = function;
    }

    @Override
    public String getName() {
    	String name = super.getName();
    	if (name == null && this.assertion != null) {
    		name = this.assertion.getSignature();
    	}

    	return name;
    }
}
