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
import de.fct.fdmm.hotspot.BindingCapability;
import de.fct.fdmm.hotspot.CodingUnit;
import de.fct.fdmm.hotspot.DeploymentCapability;
import de.fct.fdmm.hotspot.HotSpot;
import de.fct.fdmm.hotspot.HotSpotUnit;
import de.fct.fdmm.hotspot.HotSpotUnitKind;

@Path("products/{productId}/jars/{jarid}/doc/hsgs/{hsgName}/hss/{hsName}")
public class HsPage extends AbstractPageRenderer {

    private static Logger logger = LoggerFactory.getLogger(HsPage.class);

    @Override
    public String getPage() {
        // Empty
        return null;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getHssPage(@PathParam("productId") int productId,
                             @PathParam("jarid") int jarId,
                             @PathParam("hsgName") String hsgName,
                             @PathParam("hsName") String hsName) {
        if (logger.isDebugEnabled()) {
            logger.debug("getHssPage() for JAR " + jarId);
        }
        HsModel model = (HsModel) this.pageModel;
        FrameworkDescription fd = model.refresh(productId, jarId);
        model.refresh(fd, hsgName, hsName);

        return super.getPage();
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    public Response saveHsDoc(@PathParam("productId") int productId,
                              @PathParam("jarid") int jarId,
                              @PathParam("hsgName") String hsgName,
                              @PathParam("hsName") String hsName,
                              @FormParam("content") String content,
                              @FormParam("id") String elementId) {
        if (logger.isDebugEnabled()) {
            logger.debug("saveHsDoc() for JAR " + jarId + " and HS " + hsName);
        }
        HsModel model = (HsModel) this.pageModel;
        FrameworkDescription fd = model.refresh(productId, jarId);
        HotSpot hs = model.refresh(fd, hsgName, hsName);

        if (hs == null) {
            return Response.status(Status.NOT_FOUND).build();
        } else {

            if (elementId.equals("hotSpotDescription")) {
                if (logger.isDebugEnabled()) {
                    logger.debug("saveHsDoc() saving HS doc:\n" + content);
                }
                hs.setDescription(content);
            }

            if (elementId.equals("hsName")) {
                String newHsName = content.trim();
                if (newHsName.contains("<")) {
                    if (logger.isInfoEnabled()) {
                        logger.info("saveHsDoc() new HS name contains illegal chars: " + newHsName);
                    }
                    return Response.status(Status.BAD_REQUEST).entity("Illegal characters in name").build();
                } else {
                    hs.setName(newHsName);
                }
            }

            try {
                model.save(jarId, fd);
            } catch (Exception e) {
                logger.error("saveHsDoc() error during save", e);
                return Response.serverError().build();
            }
        }

        return Response.ok(this.renderPage(model.getContext(), this.getTemplate("HsPage"))).build();
    }

    @POST
    @Path("/units")
    @Produces(MediaType.APPLICATION_JSON)
    public Response postNewHotSpotUnit(@PathParam("productId") int productId,
                                       @PathParam("jarid") int jarId,
                                       @PathParam("hsgName") String hsgName,
                                       @PathParam("hsName") String hsName,
                                       @FormParam("name") String name) {
        if (logger.isDebugEnabled()) {
            logger.debug("postNewHotSpotUnit() '" + name + "'");
        }
        
        if (name == null || name.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).entity("No hot spot unit name given.").build();
        }
        
        HsModel model = (HsModel) this.pageModel;
        FrameworkDescription fd = model.refresh(productId, jarId);
        HotSpot hs = model.refresh(fd, hsgName, hsName);

        if (hs == null) {
            return Response.status(Status.NOT_FOUND).entity("Could not find hot spot " + hsName).build();
        }
        
        CodingUnit unit = new CodingUnit();
        unit.setName(name);
        unit.setKind(HotSpotUnitKind.Coding);

        if (hs.getUnits() == null) {
            hs.setUnits(new ArrayList<HotSpotUnit>());
        }
        hs.getUnits().add(unit);

        try {
            model.save(jarId, fd);
        } catch (Exception e) {
            logger.error("postNewHotSpotUnit() error during save", e);
            return Response.serverError().build();
        }

        JSONObject jsName = new JSONObject();
        try {
            jsName.put("name", name);
        } catch (JSONException e) {
            logger.error("postNewHotSpotUnit() error while creating JSON object", e);
            return Response.serverError().build();
        }

        return Response.ok(jsName.toString()).build();
    }

    @POST
    @Path("/binding")
    @Produces(MediaType.APPLICATION_JSON)
    public Response linkBinding(@PathParam("productId") int productId,
                                @PathParam("jarid") int jarId,
                                @PathParam("hsgName") String hsgName,
                                @PathParam("hsName") String hsName,
                                @FormParam("bindingName") String bindingName) {
        if (logger.isDebugEnabled()) {
            logger.debug("linkBinding() " + bindingName);
        }

        if (bindingName == null || bindingName.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).entity("No binding name given").build();
        }

        HsModel model = (HsModel) this.pageModel;
        FrameworkDescription fd = model.refresh(productId, jarId);
        HotSpot hs = model.refresh(fd, hsgName, hsName);

        boolean foundBinding = false;
        if (fd.getBindingCapabilities() != null) {
            for (BindingCapability bc : fd.getBindingCapabilities()) {
                if (bc.getName().equals(bindingName)) {
                    foundBinding = true;
                    hs.setBinding(bc);
                    break;
                }
            }
        }

        if (!foundBinding) {
            if (logger.isDebugEnabled()) {
                logger.debug("linkBinding() could not find Binding Capability " + bindingName);
            }
            return Response.status(Status.NOT_FOUND).entity(
                "Could not find Binding Capability " + bindingName).build();
        }

        try {
            model.save(jarId, fd);
        } catch (Exception e) {
            logger.error("linkBinding() error during save", e);
            return Response.serverError().build();
        }

        JSONObject jsName = new JSONObject();
        try {
            jsName.put("name", bindingName);
        } catch (JSONException e) {
            logger.error("linkBinding() error while creating JSON object", e);
            return Response.serverError().build();
        }

        return Response.ok(jsName.toString()).build();
    }

    @POST
    @Path("/deployment")
    @Produces(MediaType.APPLICATION_JSON)
    public Response linkDeployment(@PathParam("productId") int productId,
                                @PathParam("jarid") int jarId,
                                @PathParam("hsgName") String hsgName,
                                @PathParam("hsName") String hsName,
                                @FormParam("deploymentName") String deploymentName) {
        if (logger.isDebugEnabled()) {
            logger.debug("linkDeployment() " + deploymentName);
        }

        if (deploymentName == null || deploymentName.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).entity("No deployment name given").build();
        }

        HsModel model = (HsModel) this.pageModel;
        FrameworkDescription fd = model.refresh(productId, jarId);
        HotSpot hs = model.refresh(fd, hsgName, hsName);

        boolean foundDeployment = false;
        if (fd.getDeploymentCapabilities() != null) {
            for (DeploymentCapability dc : fd.getDeploymentCapabilities()) {
                if (dc.getName().equals(deploymentName)) {
                    foundDeployment = true;
                    hs.setDeployment(dc);
                    break;
                }
            }
        }

        if (!foundDeployment) {
            if (logger.isDebugEnabled()) {
                logger.debug("linkDeployment() could not find Deployment Capability " + deploymentName);
            }
            return Response.status(Status.NOT_FOUND).entity(
                "Could not find Deployment Capability " + deploymentName).build();
        }

        try {
            model.save(jarId, fd);
        } catch (Exception e) {
            logger.error("linkDeployment() error during save", e);
            return Response.serverError().build();
        }

        JSONObject jsName = new JSONObject();
        try {
            jsName.put("name", deploymentName);
        } catch (JSONException e) {
            logger.error("linkDeployment() error while creating JSON object", e);
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

        HotSpotGroup hsg = (HotSpotGroup) this.pageModel.getContext().get("hsg");
        navigation.add(this.createNavLink("/products/" + p.getProductId() + "/jars/" + jar.getJarId()
                                          + "/doc/hsgs/" + hsg.getName(), hsg.getName()));

        return navigation;
    }

}
