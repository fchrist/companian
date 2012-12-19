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
package de.fct.companian.model.binding.bdl;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import de.fct.fdmm.bindingdl.APICall;
import de.fct.fdmm.bindingdl.BindingDescription;
import de.fct.fdmm.bindingdl.MetaEntry;

public class DefaultBindingDescription implements BindingDescription {

    private List<APICall> apiCalls;
    private List<MetaEntry> metaEntries;
    
    @XmlElement(type=DefaultAPICall.class)
    public List<APICall> getApiCalls() {
        return this.apiCalls;
    }

    public void setApiCalls(List<APICall> apiCalls) {
        this.apiCalls = apiCalls;
    }
    
    @XmlElement(type=DefaultMetaEntry.class)
    public List<MetaEntry> getMetaEntries() {
        return this.metaEntries;
    }

    public void setMetaEntries(List<MetaEntry> metaEntries) {
        this.metaEntries = metaEntries;
    }

    
}
