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

import de.fct.fdmm.architecturedl.behaviordl.BehaviorDlProxy;
import de.fct.fdmm.architecturedl.behaviordl.Process;
import de.fct.fdmm.basis.Description;

public class BehaviorView extends Description {

    protected String processFile;
    
    @XmlElement
    public String getProcessFile() {
        return processFile;
    }

    public void setProcessFile(String processFile) {
        this.processFile = processFile;
    }

    public Process getProcess(BehaviorDlProxy proxy) {
        return proxy.getProcess(processFile);
    }
}
