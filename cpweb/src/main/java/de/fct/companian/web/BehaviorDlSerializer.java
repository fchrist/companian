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
package de.fct.companian.web;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.fdmm.architecturedl.behaviordl.EndStep;
import de.fct.fdmm.architecturedl.behaviordl.HotSpotGroupCall;
import de.fct.fdmm.architecturedl.behaviordl.Process;
import de.fct.fdmm.architecturedl.behaviordl.StartStep;
import de.fct.fdmm.architecturedl.behaviordl.Step;
import de.fct.fdmm.architecturedl.behaviordl.SubProcess;

public class BehaviorDlSerializer {
    
    private static Logger logger = LoggerFactory.getLogger(BehaviorDlSerializer.class);

    public static JSONObject toJSON(Process process) {
        JSONObject root = new JSONObject();
        try {
            for (Step s : process.getSteps()) {
                JSONObject stepJSON = toJSON(s);
                if (s instanceof SubProcess) {
                    SubProcess subProcess = (SubProcess) s;
                    JSONObject subProcessJSON = toJSON(subProcess.getSubProcess());
                    stepJSON.put("subprocess", subProcessJSON);
                }
                else if (s instanceof HotSpotGroupCall) {
                    HotSpotGroupCall hsgc = (HotSpotGroupCall) s;
                    JSONArray hsgcJSON = new JSONArray();
                    for (String hsg : hsgc.getHotSpotGroups()) {
                        hsgcJSON.put(hsg);
                    }
                    stepJSON.put("hotspotgroupcalls", hsgcJSON);
                }

                if (s instanceof StartStep) {
                    root.put("[StartStep]", stepJSON);
                }
                else if (s instanceof EndStep) {
                    root.put("[EndStep]", stepJSON);
                }
                else {
                    root.put(s.getName(), stepJSON);
                }
            }
        } catch (JSONException e) {
            logger.error("toJSON() error while serializing Process of BehaviorDL", e);
            root = null;
        }

        return root;
    }

    private static JSONObject toJSON(Step s) {
        JSONObject root = new JSONObject();
        try {
            if (s.getNextStep() != null) {
                root.put("nextStep", s.getNextStep().getName());
            }
            if (s.getPrevStep() != null) {
                root.put("prevStep", s.getPrevStep().getName());
            }
        } catch (JSONException e) {
            logger.error("toJSON() error while serializing Step of BehaviorDL", e);
            root = null;
        }
        
        return root;
    }
}
