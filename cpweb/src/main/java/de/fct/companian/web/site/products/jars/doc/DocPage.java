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
package de.fct.companian.web.site.products.jars.doc;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
import de.fct.fdmm.core.BehaviorView;
import de.fct.fdmm.core.FrameworkDescription;
import de.fct.fdmm.core.HotSpotGroup;
import de.fct.fdmm.core.StructureView;
import de.fct.fdmm.hotspot.BindingCapability;
import de.fct.fdmm.hotspot.DeploymentCapability;

@Path("products/{productId}/jars/{jarid}/doc")
public class DocPage extends AbstractPageRenderer {

    private static Logger logger = LoggerFactory.getLogger(DocPage.class);

    @Override
    public String getPage() {
        // Empty
        return null;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getDocPage(@PathParam("productId") int productId, @PathParam("jarid") int jarId) {
        if (logger.isDebugEnabled()) {
            logger.debug("getDocPage() for JAR " + jarId);
        }
        FrameworkDescription fd = ((DocModel) this.pageModel).refresh(productId, jarId);

        if (fd == null) {
            return Response.status(Status.NOT_FOUND).entity("Could not find framework description").build();
        }

        return Response.ok(super.getPage()).build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response putFDMFromJar(@PathParam("productId") int productId,
                                  @PathParam("jarid") int jarId,
                                  @FormParam("fdmFromJar") int fdmFromJar) {
        if (logger.isDebugEnabled()) {
            logger.debug("putFDMFromJar() from " + fdmFromJar);
        }
        DocModel docModel = (DocModel) this.pageModel;
        
        // load the FDM of given Jar
        FrameworkDescription fdNew = docModel.refresh(productId, fdmFromJar);
        if (fdNew == null) {
            return Response.status(Status.NOT_FOUND).entity("Could not find framework description for JAR " + fdmFromJar).build();
        }
        Jar fromJar = (Jar)docModel.getContext().get("jar");
        
        // refresh model of current Jar
        docModel.refresh(productId, jarId);

        // save the model
        try {
            docModel.save(jarId, fdNew);
        } catch (Exception e) {
            logger.error("putFDMFromJar() error during save", e);
            return Response.serverError().build();
        }
        
        JSONObject jsResult = new JSONObject();
        try {
            jsResult.put("fromVersion", fromJar.getVersion());
        } catch (JSONException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON response").build();
        }
        
        return Response.status(Status.CREATED).entity(jsResult.toString()).build();
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response saveFrameworkDescription(@PathParam("productId") int productId,
                                             @PathParam("jarid") int jarId,
                                             @FormParam("content") String content,
                                             @FormParam("id") String elementId) {
        if (logger.isDebugEnabled()) {
            logger.debug("saveFrameworkDescription() saving for JAR " + jarId + " content '" + content
                         + "' of element " + elementId);
        }
        if (elementId.equals("documentation")) {
            DocModel docModel = (DocModel) this.pageModel;
            FrameworkDescription fd = docModel.refresh(productId, jarId);
            fd.setDescription(content);
            try {
                docModel.save(jarId, fd);
            } catch (Exception e) {
                logger.error("saveFrameworkDescription() error during save", e);
                return Response.serverError().build();
            }
            return Response.ok().build();
        } else {
            return Response.status(Status.BAD_REQUEST).entity("Unknown element ID " + elementId).build();
        }
    }

    @PUT
    @Path("/sviews")
    @Produces(MediaType.APPLICATION_JSON)
    public Response putStructureView(@PathParam("productId") int productId,
                                     @PathParam("jarid") int jarId,
                                     @FormParam("structureViewName") String structureViewName) {
        if (logger.isDebugEnabled()) {
            logger.debug("putStructureView() '" + structureViewName + "'");
        }

        if (structureViewName == null || structureViewName.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).entity("No structure view name given").build();
        }

        DocModel model = (DocModel) this.pageModel;
        FrameworkDescription fd = model.refresh(productId, jarId);

        if (fd.getStructureViews() != null) {
            for (StructureView sv : fd.getStructureViews()) {
                if (sv.getName().equals(structureViewName)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("putStructureView() Conflict: structure view " + structureViewName
                                     + " already exists");
                        return Response.status(Status.CONFLICT).entity(
                            "A structure view with name " + structureViewName + " already exists.").build();
                    }
                }
            }
        }

        if (fd.getStructureViews() == null) {
            fd.setStructureViews(new ArrayList<StructureView>());
        }

        StructureView structureView = new StructureView();
        structureView.setName(structureViewName);
        fd.getStructureViews().add(structureView);

        try {
            model.save(jarId, fd);
        } catch (Exception e) {
            logger.error("putStructureView() error during save", e);
            return Response.serverError().build();
        }

        JSONObject jsName = new JSONObject();
        try {
            jsName.put("name", structureViewName);
        } catch (JSONException e) {
            logger.error("putStructureView() error while creating JSON object", e);
            return Response.serverError().build();
        }

        return Response.ok(jsName.toString()).build();
    }

    @PUT
    @Path("/bviews")
    @Produces(MediaType.APPLICATION_JSON)
    public Response putBehaviorView(@PathParam("productId") int productId,
                                    @PathParam("jarid") int jarId,
                                    @FormParam("behaviorViewName") String behaviorViewName) {
        if (logger.isDebugEnabled()) {
            logger.debug("putBehaviorView() '" + behaviorViewName + "'");
        }

        if (behaviorViewName == null || behaviorViewName.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).entity("No behavior view name given").build();
        }

        DocModel model = (DocModel) this.pageModel;
        FrameworkDescription fd = model.refresh(productId, jarId);

        if (fd.getBehaviorViews() != null) {
            for (BehaviorView bv : fd.getBehaviorViews()) {
                if (bv.getName().equals(behaviorViewName)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("putBehaviorView() Conflict: behavior view " + behaviorViewName
                                     + " already exists");
                        return Response.status(Status.CONFLICT).entity(
                            "A behavior view with name " + behaviorViewName + " already exists.").build();
                    }
                }
            }
        }

        if (fd.getBehaviorViews() == null) {
            fd.setBehaviorViews(new ArrayList<BehaviorView>());
        }

        BehaviorView behaviorView = new BehaviorView();
        behaviorView.setName(behaviorViewName);
        fd.getBehaviorViews().add(behaviorView);

        try {
            model.save(jarId, fd);
        } catch (Exception e) {
            logger.error("putBehaviorView() error during save", e);
            return Response.serverError().build();
        }

        JSONObject jsName = new JSONObject();
        try {
            jsName.put("name", behaviorViewName);
        } catch (JSONException e) {
            logger.error("putBehaviorView() error while creating JSON object", e);
            return Response.serverError().build();
        }

        return Response.ok(jsName.toString()).build();
    }

    @POST
    @Path("/hsgs")
    @Produces(MediaType.APPLICATION_JSON)
    public Response postNewHotSpotGroup(@PathParam("productId") int productId,
                                        @PathParam("jarid") int jarId,
                                        @FormParam("hotSpotGroupName") String hotSpotGroupName) {
        if (logger.isDebugEnabled()) {
            logger.debug("postNewHotSpotGroup() '" + hotSpotGroupName + "'");
        }

        if (hotSpotGroupName == null || hotSpotGroupName.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).entity("No Hotspot Group name given").build();
        }

        DocModel model = (DocModel) this.pageModel;
        FrameworkDescription fd = model.refresh(productId, jarId);

        HotSpotGroup hsg = new HotSpotGroup();
        hsg.setName(hotSpotGroupName);

        if (fd.getHotSpotGroups() == null) {
            fd.setHotSpotGroups(new ArrayList<HotSpotGroup>());
        }
        fd.getHotSpotGroups().add(hsg);

        try {
            model.save(jarId, fd);
        } catch (Exception e) {
            logger.error("postNewHotSpotGroup() error during save", e);
            return Response.serverError().build();
        }

        JSONObject jsName = new JSONObject();
        try {
            jsName.put("name", hotSpotGroupName);
        } catch (JSONException e) {
            logger.error("postNewHotSpotGroup() error while creating JSON object", e);
            return Response.serverError().build();
        }

        return Response.ok(jsName.toString()).build();
    }

    @PUT
    @Path("/bindings")
    @Produces(MediaType.APPLICATION_JSON)
    public Response putNewBinding(@PathParam("productId") int productId,
                                  @PathParam("jarid") int jarId,
                                  @FormParam("bindingName") String bindingName) {
        if (logger.isDebugEnabled()) {
            logger.debug("putNewBinding() '" + bindingName + "'");
        }

        if (bindingName == null || bindingName.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).entity("No binding name given").build();
        }

        DocModel model = (DocModel) this.pageModel;
        FrameworkDescription fd = model.refresh(productId, jarId);

        if (fd.getBindingCapabilities() != null) {
            for (BindingCapability bc : fd.getBindingCapabilities()) {
                if (bc.getName().equals(bindingName)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("putNewBinding() Conflict: binding " + bindingName + " already exists");
                        return Response.status(Status.CONFLICT).entity(
                            "A binding capability with name " + bindingName + " already exists.").build();
                    }
                }
            }
        }

        if (fd.getBindingCapabilities() == null) {
            fd.setBindingCapabilities(new ArrayList<BindingCapability>());
        }

        BindingCapability bc = new BindingCapability();
        bc.setName(bindingName);
        fd.getBindingCapabilities().add(bc);

        try {
            model.save(jarId, fd);
        } catch (Exception e) {
            logger.error("putNewBinding() error during save", e);
            return Response.serverError().build();
        }

        JSONObject jsName = new JSONObject();
        try {
            jsName.put("name", bindingName);
        } catch (JSONException e) {
            logger.error("putNewBinding() error while creating JSON object", e);
            return Response.serverError().build();
        }

        return Response.ok(jsName.toString()).build();
    }

    @PUT
    @Path("/deployments")
    @Produces(MediaType.APPLICATION_JSON)
    public Response putNewDeployment(@PathParam("productId") int productId,
                                     @PathParam("jarid") int jarId,
                                     @FormParam("deploymentName") String deploymentName) {

        if (logger.isDebugEnabled()) {
            logger.debug("putNewDeployment() '" + deploymentName + "'");
        }

        if (deploymentName == null || deploymentName.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).entity("No deployment name given").build();
        }

        DocModel model = (DocModel) this.pageModel;
        FrameworkDescription fd = model.refresh(productId, jarId);

        if (fd.getDeploymentCapabilities() != null) {
            for (DeploymentCapability dc : fd.getDeploymentCapabilities()) {
                if (dc.getName().equals(deploymentName)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("putNewDeployment() Conflict: deployment " + deploymentName
                                     + " already exists");
                        return Response.status(Status.CONFLICT).entity(
                            "A deployment capability with name " + deploymentName + " already exists.")
                                .build();
                    }
                }
            }
        }

        if (fd.getDeploymentCapabilities() == null) {
            fd.setDeploymentCapabilities(new ArrayList<DeploymentCapability>());
        }

        DeploymentCapability dc = new DeploymentCapability();
        dc.setName(deploymentName);
        fd.getDeploymentCapabilities().add(dc);
        try {
            model.save(jarId, fd);
        } catch (Exception e) {
            logger.error("putNewBinding() error during save", e);
            return Response.serverError().build();
        }

        JSONObject jsName = new JSONObject();
        try {
            jsName.put("name", deploymentName);
        } catch (JSONException e) {
            logger.error("putNewBinding() error while creating JSON object", e);
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

        return navigation;
    }
}
