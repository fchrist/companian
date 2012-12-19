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
package de.fct.fdmm.core;

import javax.xml.bind.annotation.XmlElement;

import de.fct.fdmm.architecturedl.ArchitectureDescription;
import de.fct.fdmm.architecturedl.ArchitectureDlProxy;
import de.fct.fdmm.basis.Description;

public abstract class ArchitectureView extends Description {

    protected String architectureDescriptionFile;
    
    @XmlElement
    public String getArchitectureDescriptionFile() {
        return architectureDescriptionFile;
    }

    public void setArchitectureDescriptionFile(String architectureDescriptionFile) {
        this.architectureDescriptionFile = architectureDescriptionFile;
    }

    public ArchitectureDescription getArchitectureDescription(ArchitectureDlProxy proxy) {
        return proxy.getArchitectureDescription(architectureDescriptionFile);
    }
}
