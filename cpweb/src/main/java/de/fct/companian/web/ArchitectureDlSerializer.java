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

import de.fct.fdmm.architecturedl.ArchitectureDescription;
import de.fct.fdmm.architecturedl.ArchitectureElement;

public class ArchitectureDlSerializer {

    private static Logger logger = LoggerFactory.getLogger(ArchitectureDlSerializer.class);

    public static JSONObject toJSON(ArchitectureDescription ad) {
        JSONObject root = new JSONObject();
        try {
            for (ArchitectureElement ae : ad.getSubElements()) {
                JSONObject aeJSON = new JSONObject();
                root.put(ae.getName(), aeJSON);
                aeJSON.put("element", toJSON(ae));
                if (ae.getSubArchitecture() != null) {
                    aeJSON.put("subarchitecture", toJSON(ae.getSubArchitecture()));
                }
            }
        } catch (JSONException e) {
            logger.error("toJSON() error while serializing architecture DL", e);
            root = null;
        }

        return root;
    }

    private static JSONObject toJSON(ArchitectureElement ae) throws JSONException {
        JSONObject root = new JSONObject();
        JSONArray depArray = new JSONArray();
        for (ArchitectureElement dep : ae.getDependencies()) {
            depArray.put(dep.getName());
        }
        root.put("dependencies", depArray);

        JSONArray hsgs = new JSONArray();
        for (String hsg : ae.getHotSpotGroups()) {
            hsgs.put(hsg);
        }
        root.put("hotspotgroups", hsgs);

        return root;
    }
}
