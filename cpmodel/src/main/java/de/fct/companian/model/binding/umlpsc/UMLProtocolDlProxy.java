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
package de.fct.companian.model.binding.umlpsc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.fdmm.protocoldl.ProtocolDlProxy;
import de.fct.fdmm.protocoldl.StateMachine;

public class UMLProtocolDlProxy implements ProtocolDlProxy {

    private static Logger logger = LoggerFactory.getLogger(UMLProtocolDlProxy.class);

    public StateMachine getStateMachine(String hookProtocolFile) {
        if (logger.isDebugEnabled()) {
            logger.debug("getStateMachine() for " + hookProtocolFile);
        }
        org.eclipse.emf.common.util.URI emfModelUri = org.eclipse.emf.common.util.URI
                .createURI(hookProtocolFile);
        org.eclipse.uml2.uml.StateMachine uml2StateMachine = UMLStateMachineLoader
                .loadStateMachine(emfModelUri);

        return new UMLStateMachine(uml2StateMachine);
    }

}
