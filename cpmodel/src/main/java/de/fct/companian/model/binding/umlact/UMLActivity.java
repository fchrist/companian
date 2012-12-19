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
import org.eclipse.uml2.uml.Activity;
import org.eclipse.uml2.uml.ActivityFinalNode;
import org.eclipse.uml2.uml.ActivityNode;
import org.eclipse.uml2.uml.CallBehaviorAction;
import org.eclipse.uml2.uml.InitialNode;
import org.eclipse.uml2.uml.OpaqueAction;

import de.fct.fdmm.architecturedl.behaviordl.Process;
import de.fct.fdmm.architecturedl.behaviordl.Step;

public class UMLActivity implements Process {

    private final Activity activity;
        
    public UMLActivity(Activity activity) {
        this.activity = activity;
    }
    
    public String getName() {
        if (this.activity.getName() == null || this.activity.getName().isEmpty()) {
            return "id" + this.activity.hashCode();
        }
        return this.activity.getName();
    }

    public Set<Step> getSteps() {
        EList<ActivityNode> nodes = this.activity.getNodes();
        Set<Step> steps = new HashSet<Step>();
        for (ActivityNode node : nodes) {
            steps.add(getInstanceFor(this, node));
        }
        
        return steps;
    }

    public static UMLActivityNode getInstanceFor(UMLActivity umlActivity, ActivityNode node) {
        if (node instanceof InitialNode) {
            return new UMLInitialNode(umlActivity, (InitialNode)node);
        }
        else if (node instanceof ActivityFinalNode) {
            return new UMLActivityFinalNode(umlActivity, (ActivityFinalNode)node);
        }
        else if (node instanceof OpaqueAction) {
            OpaqueAction opaqueAction = (OpaqueAction)node;
            EList<String> bodies = opaqueAction.getBodies();
            if (bodies != null && bodies.size() > 1) {
                String body = bodies.get(0);
                if (body.startsWith("HotSpotGroups")) {
                    return new UMLOpaqueAction(umlActivity, opaqueAction);
                }
            }
        }
        else if (node instanceof CallBehaviorAction) {
            return new UMLCallBehaviorAction(umlActivity, (CallBehaviorAction)node);
        }

        return new UMLActivityNode(umlActivity, node);
    }

}
