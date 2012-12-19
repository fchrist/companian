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
package de.fct.companian.web.site.products.jars.doc.core;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
import de.fct.fdmm.hotspot.HotSpot;

@Path("products/{productId}/jars/{jarid}/doc/hsgs/{hsgName}")
public class HsgPage extends AbstractPageRenderer {

    private static Logger logger = LoggerFactory.getLogger(HsgPage.class);

    @Override
    public String getPage() {
        // Empty
        return null;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getHsgPage(@PathParam("productId") int productId,
                               @PathParam("jarid") int jarId,
                               @PathParam("hsgName") String hsgName) {
        if (logger.isDebugEnabled()) {
            logger.debug("getHsgPage() for JAR " + jarId + " and HSG " + hsgName);
        }
        HsgModel model = (HsgModel) this.pageModel;
        FrameworkDescription fd = model.refresh(productId, jarId);
        HotSpotGroup hsg = model.refresh(fd, hsgName);

        if (hsg == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        return Response.ok(this.renderPage(model.getContext(), this.getTemplate("HsgPage"))).build();
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response saveHsgDoc(@PathParam("productId") int productId,
                               @PathParam("jarid") int jarId,
                               @PathParam("hsgName") String hsgName,
                               @FormParam("content") String content,
                               @FormParam("id") String elementId) {
        if (logger.isDebugEnabled()) {
            logger.debug("saveHsgDoc() for JAR " + jarId + " and HSG " + hsgName + " with elementId="
                         + elementId + " and content\n" + content);
        }
        HsgModel model = (HsgModel) this.pageModel;
        FrameworkDescription fd = model.refresh(productId, jarId);
        HotSpotGroup hsg = model.refresh(fd, hsgName);

        if (hsg == null) {
            return Response.status(Status.NOT_FOUND).build();
        } else {

            if (elementId.equals("hotSpotGroupDescription")) {
                if (logger.isDebugEnabled()) {
                    logger.debug("saveHsgDoc() saving HSG doc:\n" + content);
                }
                hsg.setDescription(content);
            }

            if (elementId.equals("hsgName")) {
                String newHsgName = content.trim();
                if (newHsgName.contains("<")) {
                    if (logger.isInfoEnabled()) {
                        logger.info("saveHsgDoc() new HSG name contains illegal chars: " + newHsgName);
                    }
                    return Response.status(Status.BAD_REQUEST).entity("Illegal characters in name").build();
                } else {
                    hsg.setName(newHsgName);
                }
            }

            try {
                model.save(jarId, fd);
            } catch (Exception e) {
                logger.error("saveHsgDoc() error during save", e);
                return Response.serverError().build();
            }
        }

        return Response.ok(this.renderPage(model.getContext(), this.getTemplate("HsgPage"))).build();
    }

    @POST
    @Path("/hss")
    @Produces(MediaType.APPLICATION_JSON)
    public Response postNewHotSpot(@PathParam("productId") int productId,
                                   @PathParam("jarid") int jarId,
                                   @PathParam("hsgName") String hsgName,
                                   @FormParam("name") String name) {
        if (logger.isDebugEnabled()) {
            logger.debug("postNewHotSpot() '" + name + "'");
        }
        HsgModel model = (HsgModel) this.pageModel;
        FrameworkDescription fd = model.refresh(productId, jarId);
        HotSpotGroup hsg = model.refresh(fd, hsgName);

        HotSpot hs = new HotSpot();
        hs.setName(name);

        if (hsg.getHotSpots() == null) {
            List<HotSpot> hss = new ArrayList<HotSpot>();
            hsg.setHotSpots(hss);
        }
        hsg.getHotSpots().add(hs);

        try {
            model.save(jarId, fd);
        } catch (Exception e) {
            logger.error("postNewHotSpot() error during save", e);
            return Response.serverError().build();
        }

        JSONObject jsName = new JSONObject();
        try {
            jsName.put("name", name);
        } catch (JSONException e) {
            logger.error("postNewHotSpot() error while creating JSON object", e);
            return Response.serverError().build();
        }

        return Response.ok(jsName.toString()).build();
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

        return navigation;
    }

}
