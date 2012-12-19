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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.uml2.uml.FinalState;
import org.eclipse.uml2.uml.Pseudostate;
import org.eclipse.uml2.uml.Transition;
import org.eclipse.uml2.uml.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.fdmm.protocoldl.HookProtocolState;
import de.fct.fdmm.protocoldl.ProtocolHook;

public class UMLState implements HookProtocolState {

    private static Logger logger = LoggerFactory.getLogger(UMLState.class);

    private Vertex vertex;

    private Set<ProtocolHook> subsequentHooks;

    public UMLState(Vertex vertex) {
        this.vertex = vertex;

        if (logger.isDebugEnabled()) {
            logger.debug("<init> created new state from vertex " + this.vertex);
        }
    }

    public Set<ProtocolHook> getSubsequentHooks() {
        if (this.subsequentHooks == null) {
            this.subsequentHooks = new HashSet<ProtocolHook>();

            if (vertex.getOutgoings() != null && vertex.getOutgoings().size() > 0) {
                for (Transition t : vertex.getOutgoings()) {
                    this.subsequentHooks.add(transitionToHook(t));
                }
            }
        }

        return this.subsequentHooks;
    }

    private ProtocolHook transitionToHook(Transition t) {
        UMLTransition umlTrans = new UMLTransition(t);

        return umlTrans;
    }

    public Set<ProtocolHook> getSubsequentMessages() {
        if (this.subsequentHooks == null) {
            this.subsequentHooks = new HashSet<ProtocolHook>();

            if (vertex.getOutgoings() != null && vertex.getOutgoings().size() > 0) {
                for (Transition t : vertex.getOutgoings()) {
                    this.subsequentHooks.add(transitionToHook(t));
                }
            }
        }

        return this.subsequentHooks;
    }

    public String getId() {
        return this.vertex.getName();
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (this.vertex instanceof Pseudostate) {
            Pseudostate ps = (Pseudostate) vertex;
            sb.append(ps.getKind().getName().toUpperCase());
        } else if (this.vertex instanceof FinalState) {
            sb.append("FINAL");
        } else {
            sb.append("STATE");
        }
        if (this.getId() != null && !this.getId().isEmpty()) {
            sb.append(" ");
            sb.append(this.getId());
        }

        return sb.toString();
    }

}
