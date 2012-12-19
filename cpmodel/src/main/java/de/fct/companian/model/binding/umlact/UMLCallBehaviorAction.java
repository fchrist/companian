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

import org.eclipse.uml2.uml.Activity;
import org.eclipse.uml2.uml.CallBehaviorAction;

import de.fct.fdmm.architecturedl.behaviordl.Process;
import de.fct.fdmm.architecturedl.behaviordl.SubProcess;

public class UMLCallBehaviorAction extends UMLActivityNode implements SubProcess {

    public UMLCallBehaviorAction(UMLActivity umlActivity, CallBehaviorAction callBehaviorAction) {
        super(umlActivity, callBehaviorAction);
    }

    public Process getSubProcess() {
        CallBehaviorAction callBehaviorAction = (CallBehaviorAction)this.activityNode;
        Activity subActivity = (Activity)callBehaviorAction.getBehavior();
        return new UMLActivity(subActivity);
    }
 
    public String getType() {
        return "sub process";
    }
}
