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

import de.fct.fdmm.basis.INamedElement;
import de.fct.fdmm.protocoldl.ProtocolDlProxy;
import de.fct.fdmm.protocoldl.StateMachine;

public class HookProtocol implements INamedElement {

    private String hookProtocolFile;

    @XmlElement
    public String getHookProtocolFile() {
        return hookProtocolFile;
    }

    public void setHookProtocolFile(String stateMachinePath) {
        this.hookProtocolFile = stateMachinePath;
    }

    public StateMachine getStateMachine(ProtocolDlProxy protocolDlProxy) {
        return protocolDlProxy.getStateMachine(this.hookProtocolFile);
    }

    public String getName() {
        int lastSlash = this.hookProtocolFile.lastIndexOf('/');
        return this.hookProtocolFile.substring(lastSlash + 1);
    }

}
