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
package de.fct.companian.web.site.products.jars.doc.hotspot;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.analyze.db.dao.JarDao;
import de.fct.fdmm.core.FrameworkDescription;
import de.fct.fdmm.hotspot.CodingUnit;
import de.fct.fdmm.hotspot.HookProtocol;
import de.fct.fdmm.hotspot.HotSpotUnit;
import de.fct.fdmm.hotspot.HotSpotUnitKind;
import de.fct.fdmm.protocoldl.HookProtocolState;
import de.fct.fdmm.protocoldl.ProtocolDlProxy;
import de.fct.fdmm.protocoldl.ProtocolHook;
import de.fct.fdmm.protocoldl.StateMachine;

public class ProtocolModel extends HsUnitModel {

    private Logger logger = LoggerFactory.getLogger(ProtocolModel.class);

    private ProtocolDlProxy protocolDlProxy;

    public ProtocolModel(JarDao jarDao) {
        super(jarDao);
    }

    public HookProtocol refresh(FrameworkDescription fd,
                                String hsgName,
                                String hsName,
                                String unitName,
                                String hookProtocolFile,
                                boolean loadProtocol) {

        HookProtocol hookProtocol = null;

        HotSpotUnit hsu = super.refresh(fd, hsgName, hsName, unitName);
        if (hsu != null) {
            if (hsu.getKind() == HotSpotUnitKind.Coding) {
                CodingUnit cu = (CodingUnit) hsu;
                if (cu.getProtocols() != null && !cu.getProtocols().isEmpty()) {
                    for (HookProtocol p : cu.getProtocols()) {
                        if (p.getHookProtocolFile().equals(hookProtocolFile)) {
                            hookProtocol = p;
                            this.context.put("protocol", p);
                            break;
                        }
                    }
                }
            }
        }

        if (loadProtocol && hookProtocol != null) {
            StateMachine sm = this.protocolDlProxy.getStateMachine(hookProtocol.getHookProtocolFile());

            JSONArray vertexes = new JSONArray();
            JSONObject edges = new JSONObject();
            this.createJSON(sm.getStartState(), vertexes, edges);

            String smString = "{}";
            try {
                JSONObject jsSM = new JSONObject();
                jsSM.put("vertexes", vertexes);
                jsSM.put("edges", edges);
                smString = jsSM.toString(2);
            } catch (JSONException e) {
                logger.warn("refresh() problem while creating JSON object", e);
            }

            this.context.put("sm", smString);
        }

        return hookProtocol;
    }

    private void createJSON(HookProtocolState state, JSONArray vertexes, JSONObject edges) {
        vertexes.put(state.toString());
        if (state.getSubsequentHooks() != null && !state.getSubsequentHooks().isEmpty()) {
            try {
                JSONArray phEdges = new JSONArray();
                for (ProtocolHook ph : state.getSubsequentHooks()) {
                    JSONObject edge = new JSONObject();
                    edge.put(ph.toString(), ph.getTarget().toString());
                    phEdges.put(edge);

                    if (!vertexes.toString().contains(ph.getTarget().toString())) {
                        createJSON(ph.getTarget(), vertexes, edges);
                    }
                }
                edges.put(state.toString(), phEdges);
            } catch (JSONException e) {
                logger.warn("createJSON() problem while creating JSON object", e);
            }
        }
    }

    public void setProtocolDlProxy(ProtocolDlProxy protocolDlProxy) {
        this.protocolDlProxy = protocolDlProxy;
    }

}
