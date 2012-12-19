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

import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Pseudostate;
import org.eclipse.uml2.uml.PseudostateKind;
import org.eclipse.uml2.uml.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.fdmm.protocoldl.HookProtocolState;
import de.fct.fdmm.protocoldl.StateMachine;

public class UMLStateMachine implements StateMachine {

    private static Logger logger = LoggerFactory.getLogger(UMLStateMachine.class);
    
    private org.eclipse.uml2.uml.StateMachine uml2StateMachine;
    
    public UMLStateMachine(org.eclipse.uml2.uml.StateMachine uml2StateMachine) {
        this.uml2StateMachine = uml2StateMachine;
    }
    
    public HookProtocolState getStartState() {
        Pseudostate pseudoInit = retrieveInitState();
        UMLState startState = new UMLState(pseudoInit);

        return startState;
    }

    private Pseudostate retrieveInitState() {
        Pseudostate initState = null;
        for (Element smEl : this.uml2StateMachine.getOwnedElements()) {
            if (smEl instanceof Region) {
                Region region = (Region) smEl;
                if (logger.isDebugEnabled()) {
                    logger.debug("retrieveInitState() found region with owned elements "
                                 + region.getOwnedElements());
                }
                for (Element regEl : region.getOwnedElements()) {
                    if (regEl instanceof Pseudostate) {
                        Pseudostate ps = (Pseudostate) regEl;
                        if (logger.isDebugEnabled()) {
                            logger.debug("retrieveInitState() found pseudostate " + ps);
                        }
                        if (ps.getKind() == PseudostateKind.INITIAL_LITERAL) {
                            initState = ps;
                            if (logger.isDebugEnabled()) {
                                logger.debug("retrieveInitState() found INIT pseudostate " + initState);
                            }
                            break;
                        }
                    }
                }
            }
            if (initState != null) {
                break;
            }
        }

        return initState;
    }
    
    @Override
    public String toString() {
        return this.uml2StateMachine.toString();
    }
}
