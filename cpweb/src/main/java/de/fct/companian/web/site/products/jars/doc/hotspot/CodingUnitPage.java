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

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.analyze.db.model.Jar;
import de.fct.companian.analyze.db.model.Product;
import de.fct.companian.web.site.AbstractPageRenderer;
import de.fct.fdmm.apidl.APIDlProxy;
import de.fct.fdmm.apidl.MethodDescription;
import de.fct.fdmm.apidl.TypeDescription;
import de.fct.fdmm.core.FrameworkDescription;
import de.fct.fdmm.core.HotSpotGroup;
import de.fct.fdmm.hotspot.CodingUnit;
import de.fct.fdmm.hotspot.HookCall;
import de.fct.fdmm.hotspot.HotSpot;
import de.fct.fdmm.hotspot.HotSpotUnit;
import de.fct.fdmm.hotspot.HotSpotUnitKind;

@Path("products/{productId}/jars/{jarid}/doc/hsgs/{hsgName}/hss/{hsName}/units/{unitName}")
public class CodingUnitPage extends AbstractPageRenderer {

    private static Logger logger = LoggerFactory.getLogger(CodingUnitPage.class);

    private APIDlProxy apiDlProxy;

    @Override
    public String getPage() {
        // Empty
        return null;
    }

    @POST
    @Path("/type")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addHooksThroughType(@PathParam("productId") int productId,
                                        @PathParam("jarid") int jarId,
                                        @PathParam("hsgName") String hsgName,
                                        @PathParam("hsName") String hsName,
                                        @PathParam("unitName") String unitName,
                                        @FormParam("typeapiuri") String typeUri) {

        if (logger.isDebugEnabled()) {
            logger.debug("addHooksThroughType() for unit " + unitName);
        }
        HsUnitModel model = (HsUnitModel) this.pageModel;
        FrameworkDescription fd = model.refresh(productId, jarId);
        HotSpotUnit hsu = model.refresh(fd, hsgName, hsName, unitName);

        if (hsu.getKind() == HotSpotUnitKind.Coding) {

            if (typeUri != null) {
                String typeAPIPath = typeUri.replaceFirst(".*\\" + this.configBean.getRootPath(), "");
                if (logger.isDebugEnabled()) {
                    logger.debug("addHooksThroughType() from type API path " + typeAPIPath);
                }

                // Check if Type already defined for this CodingUnit
                CodingUnit cu = (CodingUnit) hsu;
                if (cu.getTypeAPIPath() != null) {
                    if (cu.getTypeAPIPath().equals(typeAPIPath)) {
                        if (logger.isInfoEnabled()) {
                            logger.info("addHooksThroughType() Conflict! type " + typeAPIPath
                                        + " already associated with this CodingUnit");
                            return Response.status(Status.CONFLICT).entity(
                                "Type " + typeAPIPath + " already defined for this CodingUnit.").build();
                        }
                    }
                }

                TypeDescription td = this.apiDlProxy.getTypeDescription(typeAPIPath);
                if (td != null) {
                    // Init hook list
                    List<HookCall> addedHooks = new ArrayList<HookCall>();

                    if (td.getMethods() != null) {

                        for (MethodDescription md : td.getMethods()) {
                            String methodAPIPath = "/site/products/" + productId + "/jars/" + jarId + "/api/"
                                                   + td.getPackage().getName() + "/" + td.getName() + "/"
                                                   + md.getSignature().toString();

                            // Skip if HookCall already exists for this unit
                            boolean existsAlready = false;
                            if (cu.getHooks() != null) {
                                for (HookCall hook : cu.getHooks()) {
                                    if (hook.getMethodAPIPath().equals(methodAPIPath)) {
                                        existsAlready = true;
                                        break;
                                    }
                                }
                            }

                            if (existsAlready) {
                                if (logger.isInfoEnabled()) {
                                    logger.info("addHooksThroughType() Skipping Hook " + methodAPIPath
                                                + " as already defined for this CodingUnit.");
                                }
                            } else {
                                // Everything checked - create new HookCall object and add it
                                HookCall newHook = new HookCall();
                                newHook.setMethodAPIPath(methodAPIPath);
                                addedHooks.add(newHook);
                            }
                        }
                    }

                    // Set the type
                    cu.setTypeAPIPath(typeAPIPath);

                    // Add new Hooks to CodingUnit
                    if (!addedHooks.isEmpty()) {
                        if (cu.getHooks() == null) {
                            cu.setHooks(new ArrayList<HookCall>());
                        }
                        for (HookCall hc : addedHooks) {
                            cu.getHooks().add(hc);
                        }
                    }

                    // Save the model
                    try {
                        model.save(jarId, fd);
                    } catch (Exception e) {
                        logger.error("addHooksThroughType() error during save", e);
                        return Response.serverError().build();
                    }

                    // Return added Hooks as JSON
                    JSONObject hooks = new JSONObject();
                    try {
                        JSONArray hookList = new JSONArray();
                        for (HookCall hook : addedHooks) {
                            JSONObject jsHook = new JSONObject();
                            jsHook.put("signature", hook.getSignature());
                            jsHook.put("path", hook.getMethodAPIPath());
                            hookList.put(jsHook);
                        }
                        hooks.put("hooks", hookList);
                    } catch (JSONException e) {
                        logger.error("addHooksThroughType() error while creating JSON object", e);
                        return Response.serverError().build();
                    }

                    return Response.ok(hooks.toString()).build();

                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("addHooksThroughType() No API description found for " + typeAPIPath);
                    }
                    return Response.status(Status.NOT_FOUND).entity(
                        "No API description found for " + typeAPIPath).build();
                }
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("addHooksThroughType() No valid method URI specified in request.");
                }
                return Response.status(Status.BAD_REQUEST).entity(
                    "No valid type API URI specified in request.").build();
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("addHooksThroughType() The specified Hotspot is of kind " + hsu.getKind().name()
                             + " and is not a CodingUnit");
            }
            return Response.status(Status.BAD_REQUEST).entity(
                "The specified Hotspot is of kind " + hsu.getKind().name() + " and is not a CodingUnit")
                    .build();
        }
    }

    @Override
    protected List<String> getNavigation() {
        List<String> navigation = super.getDefaultNavigation();

        Product p = (Product) this.pageModel.getContext().get("product");
        navigation.add(this.createNavLink("/products/" + p.getProductId(), p.getName()));

        Jar jar = (Jar) this.pageModel.getContext().get("jar");
        navigation.add(this.createNavLink("/products/" + p.getProductId() + "/jars/" + jar.getJarId(),
            jar.getArtifact() + " " + jar.getVersion()));

        navigation.add(this.createNavLink("/products/" + p.getProductId() + "/jars/" + jar.getJarId()
                                          + "/doc/", "FDM"));

        HotSpotGroup hsg = (HotSpotGroup) this.pageModel.getContext().get("hsg");
        navigation.add(this.createNavLink("/products/" + p.getProductId() + "/jars/" + jar.getJarId()
                                          + "/doc/hsgs/" + hsg.getName(), hsg.getName()));

        HotSpot hs = (HotSpot) this.pageModel.getContext().get("hs");
        navigation.add(this.createNavLink("/products/" + p.getProductId() + "/jars/" + jar.getJarId()
                                          + "/doc/hsgs/" + hsg.getName() + "/hss/" + hs.getName(), hs
                .getName()));

        return navigation;
    }

    public void setApiDlProxy(APIDlProxy apiDlProxy) {
        this.apiDlProxy = apiDlProxy;
    }

}
