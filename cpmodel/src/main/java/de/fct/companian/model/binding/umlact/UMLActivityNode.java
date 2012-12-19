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

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.ActivityEdge;
import org.eclipse.uml2.uml.ActivityNode;
import org.eclipse.uml2.uml.OpaqueAction;

import de.fct.fdmm.architecturedl.behaviordl.Process;
import de.fct.fdmm.architecturedl.behaviordl.Step;

public class UMLActivityNode implements Step {

    protected final UMLActivity umlActivity;
    protected final ActivityNode activityNode;
       
    public UMLActivityNode(UMLActivity umlActivity, ActivityNode activityNode) {
        this.umlActivity = umlActivity;
        this.activityNode = activityNode;
    }
    
    public String getName() {
        if (this.activityNode.getLabel() != null && !this.activityNode.getLabel().isEmpty()) {
            return this.activityNode.getLabel();
        }
        else if (this.activityNode.getName() != null && !this.activityNode.getName().isEmpty()) {
            return this.activityNode.getName();    
        }
        else if (this.activityNode instanceof OpaqueAction) {
            OpaqueAction oa = (OpaqueAction)this.activityNode;
            if (oa.getBodies() != null && !oa.getBodies().isEmpty()) {
                return oa.getBodies().get(0);
            }
        }
        
        return "id" + this.activityNode.hashCode();
    }

    public Process getProcess() {
        return this.umlActivity;
    }

    public Step getNextStep() {
        EList<ActivityEdge> outgoings = this.activityNode.getOutgoings();
        if (outgoings != null && !outgoings.isEmpty()) {
            ActivityEdge edge = outgoings.get(0);
            return UMLActivity.getInstanceFor(this.umlActivity, edge.getTarget());
        }
        
        return null;
    }

    public Step getPrevStep() {
        EList<ActivityEdge> incomings = this.activityNode.getIncomings();
        if (incomings != null && !incomings.isEmpty()) {
            ActivityEdge edge = incomings.get(0);
            return UMLActivity.getInstanceFor(this.umlActivity, edge.getSource());
        }
        
        return null;        
    }

    public String getType() {
        return "simple step";
    }

}
