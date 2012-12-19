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
package de.fct.companian.model.binding.umlact;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.OpaqueAction;

import de.fct.fdmm.architecturedl.behaviordl.HotSpotGroupCall;

public class UMLOpaqueAction extends UMLActivityNode implements HotSpotGroupCall {

    public UMLOpaqueAction(UMLActivity umlActivity, OpaqueAction opaqueAction) {
        super(umlActivity, opaqueAction);
    }

    public Set<String> getHotSpotGroups() {
        Set<String> hotSpotGroups = new HashSet<String>();
        OpaqueAction opaqueAction = (OpaqueAction)this.activityNode;
        EList<String> bodies = opaqueAction.getBodies();
        if (bodies != null && bodies.size() > 1) {
            String body = bodies.get(0);
            if (body.contains("HotSpotGroups")) {
                for (int i=1; i<bodies.size(); i++) {
                    hotSpotGroups.add(bodies.get(i));
                }
            }
        }
        
        return hotSpotGroups;
    }

    public String getType() {
        return "hotspot group call";
    }
}
