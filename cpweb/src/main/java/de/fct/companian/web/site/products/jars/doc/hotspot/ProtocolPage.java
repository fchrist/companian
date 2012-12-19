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

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.analyze.db.model.Jar;
import de.fct.companian.analyze.db.model.Product;
import de.fct.companian.web.site.AbstractPageRenderer;
import de.fct.fdmm.core.FrameworkDescription;
import de.fct.fdmm.core.HotSpotGroup;
import de.fct.fdmm.hotspot.CodingUnit;
import de.fct.fdmm.hotspot.HookProtocol;
import de.fct.fdmm.hotspot.HotSpot;
import de.fct.fdmm.hotspot.HotSpotUnit;
import de.fct.fdmm.hotspot.HotSpotUnitKind;
import de.fct.fdmm.protocoldl.ProtocolDlProxy;
import de.fct.fdmm.protocoldl.StateMachine;

@Path("products/{productId}/jars/{jarid}/doc/hsgs/{hsgName}/hss/{hsName}/units/{unitName}/protocols")
public class ProtocolPage extends AbstractPageRenderer {

    private static Logger logger = LoggerFactory.getLogger(ProtocolPage.class);

    private ProtocolDlProxy protocolDlProxy;

    @Override
    public String getPage() {
        // Empty
        return null;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getProtocolPage(@PathParam("productId") int productId,
                                    @PathParam("jarid") int jarId,
                                    @PathParam("hsgName") String hsgName,
                                    @PathParam("hsName") String hsName,
                                    @PathParam("unitName") String unitName,
                                    @QueryParam("hookProtocolFile") String hookProtocolFile) {

        if (logger.isDebugEnabled()) {
            logger.debug("getProtocolPage() " + hookProtocolFile + " from unit " + unitName);
        }
        ProtocolModel model = (ProtocolModel) this.pageModel;
        FrameworkDescription fd = model.refresh(productId, jarId);
        HookProtocol protocol = model.refresh(fd, hsgName, hsName, unitName, hookProtocolFile, true);

        if (protocol == null) {
            return Response.status(Status.NOT_FOUND).entity("Could not find Hook for " + hookProtocolFile)
                    .build();
        } else {
            return Response.ok(super.getPage()).build();
        }

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addHookProtocol(@PathParam("productId") int productId,
                                    @PathParam("jarid") int jarId,
                                    @PathParam("hsgName") String hsgName,
                                    @PathParam("hsName") String hsName,
                                    @PathParam("unitName") String unitName,
                                    @FormParam("hookProtocolFile") String hookProtocolFile) {

        if (logger.isDebugEnabled()) {
            logger.debug("addHookProtocol() " + hookProtocolFile + " to unit " + unitName);
        }
        ProtocolModel model = (ProtocolModel) this.pageModel;
        FrameworkDescription fd = model.refresh(productId, jarId);
        HotSpotUnit hsu = model.refresh(fd, hsgName, hsName, unitName);

        if (hsu.getKind() == HotSpotUnitKind.Coding) {

            HookProtocol hp = model.refresh(fd, hsgName, hsName, unitName, hookProtocolFile, false);

            if (hp == null) {

                StateMachine sm = this.protocolDlProxy.getStateMachine(hookProtocolFile);
                if (sm != null) {

                    hp = new HookProtocol();
                    hp.setHookProtocolFile(hookProtocolFile);

                    CodingUnit cu = (CodingUnit) hsu;
                    if (cu.getProtocols() == null) {
                        cu.setProtocols(new ArrayList<HookProtocol>());
                    }
                    cu.getProtocols().add(hp);

                    // Save the model
                    try {
                        model.save(jarId, fd);
                    } catch (Exception e) {
                        logger.error("addHookProtocol() error during save", e);
                        return Response.serverError().build();
                    }

                    // Return added Hooks as JSON
                    JSONObject jsReturn = new JSONObject();
                    try {
                        jsReturn.put("hookProtocolFile", hookProtocolFile);
                    } catch (JSONException e) {
                        logger.error("addHookProtocol() error while creating JSON object", e);
                        return Response.serverError().build();
                    }

                    return Response.ok(jsReturn.toString()).build();
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("addHookProtocol() Could not load Hook protocol from "
                                     + hookProtocolFile);
                    }
                    return Response.status(Status.BAD_REQUEST).entity(
                        "Could not load Hook protocol from " + hookProtocolFile).build();
                }
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("addHookProtocol() Hook protocol " + hookProtocolFile
                                 + " already linked to this Coding Unit.");
                }
                return Response.status(Status.CONFLICT).entity(
                    "Hook protocol " + hookProtocolFile + " already linked to this Coding Unit.").build();
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("addHookProtocol() The specified Hotspot is of kind " + hsu.getKind().name()
                             + " and is not a CodingUnit");
            }
            return Response.status(Status.BAD_REQUEST).entity(
                "The specified Hotspot is of kind " + hsu.getKind().name() + " and is not a CodingUnit")
                    .build();
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeHookProtocol(@PathParam("productId") int productId,
                                       @PathParam("jarid") int jarId,
                                       @PathParam("hsgName") String hsgName,
                                       @PathParam("hsName") String hsName,
                                       @PathParam("unitName") String unitName,
                                       @QueryParam("hookProtocolFile") String hookProtocolFile) {

        if (logger.isDebugEnabled()) {
            logger.debug("removeHookProtocol() " + hookProtocolFile + " to unit " + unitName);
        }
        ProtocolModel model = (ProtocolModel) this.pageModel;
        FrameworkDescription fd = model.refresh(productId, jarId);
        HotSpotUnit hsu = model.refresh(fd, hsgName, hsName, unitName);

        if (hsu.getKind() == HotSpotUnitKind.Coding) {

            HookProtocol hp = model.refresh(fd, hsgName, hsName, unitName, hookProtocolFile, false);

            if (hp != null) {
                // Delete HookProtocol object
                CodingUnit cu = (CodingUnit)hsu;
                cu.getProtocols().remove(hp);
                hp = null;
                
                // Save the model
                try {
                    model.save(jarId, fd);
                } catch (Exception e) {
                    logger.error("removeHookProtocol() error during save", e);
                    return Response.serverError().build();
                }

                // Return added Hooks as JSON
                JSONObject jsReturn = new JSONObject();
                try {
                    jsReturn.put("hookProtocolFile", hookProtocolFile);
                } catch (JSONException e) {
                    logger.error("removeHookProtocol() error while creating JSON object", e);
                    return Response.serverError().build();
                }

                return Response.ok(jsReturn.toString()).build();
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("removeHookProtocol() Hook protocol " + hookProtocolFile
                                 + " not linked to this Coding Unit.");
                }
                return Response.status(Status.CONFLICT).entity(
                    "Hook protocol " + hookProtocolFile + " not linked to this Coding Unit.").build();
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("removeHookProtocol() The specified Hotspot is of kind " + hsu.getKind().name()
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

        HotSpotUnit hsu = (HotSpotUnit) this.pageModel.getContext().get("hsu");
        navigation.add(this.createNavLink("/products/" + p.getProductId() + "/jars/" + jar.getJarId()
                                          + "/doc/hsgs/" + hsg.getName() + "/hss/" + hs.getName() + "/units/"
                                          + hsu.getName(), hsu.getName()));

        return navigation;
    }

    public void setProtocolDlProxy(ProtocolDlProxy protocolDlProxy) {
        this.protocolDlProxy = protocolDlProxy;
    }

}
