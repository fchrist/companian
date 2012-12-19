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
import de.fct.companian.model.binding.bdl.DefaultBindingDescription;
import de.fct.companian.model.binding.bdl.DefaultMetaEntry;
import de.fct.companian.model.binding.ddl.DefaultDeployment;
import de.fct.companian.web.site.AbstractPageRenderer;
import de.fct.fdmm.bindingdl.MetaEntry;
import de.fct.fdmm.core.FrameworkDescription;
import de.fct.fdmm.hotspot.BindingCapability;
import de.fct.fdmm.hotspot.DeploymentCapability;

@Path("products/{productId}/jars/{jarid}/doc/deployments/{deploymentName}")
public class DeploymentCapabilityPage extends AbstractPageRenderer {

    private static Logger logger = LoggerFactory.getLogger(DeploymentCapabilityPage.class);

    @Override
    public String getPage() {
        // Empty
        return null;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getDeploymentPage(@PathParam("productId") int productId,
                                      @PathParam("jarid") int jarId,
                                      @PathParam("deploymentName") String deploymentName) {
        if (logger.isDebugEnabled()) {
            logger.debug("getDeploymentPage() " + deploymentName + " for JAR " + jarId);
        }
        DeploymentCapabilityModel model = (DeploymentCapabilityModel) this.pageModel;
        FrameworkDescription fd = model.refresh(productId, jarId);
        DeploymentCapability dc = model.refresh(fd, deploymentName);

        if (dc == null) {
            return Response.status(Status.NOT_FOUND).entity(
                "Could not find Deployment Capability " + deploymentName).build();
        }

        return Response.ok(super.getPage()).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response postDescription(@PathParam("productId") int productId,
                                    @PathParam("jarid") int jarId,
                                    @PathParam("deploymentName") String deploymentName,
                                    @FormParam("content") String content,
                                    @FormParam("id") String elementId) {
        if (logger.isDebugEnabled()) {
            logger.debug("postDescription() for " + deploymentName + ", JAR " + jarId + ", content: "
                         + content);
        }

        if (!elementId.equals("description") && !elementId.equals("packageFormatDescription")
            && !elementId.equals("installationDescription")) {
            return Response.status(Status.BAD_REQUEST).entity("Unknown element id " + elementId).build();
        }

        if (content == null || content.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).entity("No content found in request.").build();
        }

        DeploymentCapabilityModel model = (DeploymentCapabilityModel) this.pageModel;
        FrameworkDescription fd = model.refresh(productId, jarId);
        DeploymentCapability dc = model.refresh(fd, deploymentName);

        if (dc == null) {
            return Response.status(Status.NOT_FOUND).entity(
                "Could not find Deployment Capability " + deploymentName).build();
        }

        if (elementId.equals("description")) {
            if (logger.isDebugEnabled()) {
                logger.debug("postDescription() setting deployment description");
            }
            dc.setDescription(content);
        }
        else {
            if (dc.getDeploymentDescription() == null) {
                dc.setDeploymentDescription(new DefaultDeployment());
            }

            DefaultDeployment dd = (DefaultDeployment) dc.getDeploymentDescription();
            if (elementId.equals("packageFormatDescription")) {
                if (logger.isDebugEnabled()) {
                    logger.debug("postDescription() setting package format description");
                }
                dd.setPackageFormatDescription(content);
            } else if (elementId.equals("installationDescription")) {
                if (logger.isDebugEnabled()) {
                    logger.debug("postDescription() setting installation description");
                }
                dd.setInstallationDescription(content);
            }            
        }

        try {
            model.save(jarId, fd);
        } catch (Exception e) {
            logger.error("postDescription() error during save", e);
            return Response.serverError().build();
        }

        return Response.ok().build();
    }

    @PUT
    @Path("/metaentries")
    @Produces(MediaType.APPLICATION_JSON)
    public Response putMetaEntry(@PathParam("productId") int productId,
                                 @PathParam("jarid") int jarId,
                                 @PathParam("bindingName") String bindingName,
                                 @FormParam("metaEntryName") String metaEntryName) {

        if (logger.isDebugEnabled()) {
            logger.debug("putMetaEntry() " + metaEntryName + " for " + bindingName + ", JAR " + jarId);
        }

        if (metaEntryName == null || metaEntryName.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).entity("No meta entry name given").build();
        }

        BindingCapabilityModel model = (BindingCapabilityModel) this.pageModel;
        FrameworkDescription fd = model.refresh(productId, jarId);
        BindingCapability bc = model.refresh(fd, bindingName);

        if (bc == null) {
            return Response.status(Status.NOT_FOUND).entity(
                "Could not find Binding Capability " + bindingName).build();
        }

        if (bc.getBindingDescription() != null) {
            if (bc.getBindingDescription().getMetaEntries() != null
                && !bc.getBindingDescription().getMetaEntries().isEmpty()) {
                for (MetaEntry me : bc.getBindingDescription().getMetaEntries()) {
                    if (me.getName().equals(metaEntryName)) {
                        return Response.status(Status.CONFLICT).entity(
                            "Meta entry " + metaEntryName + " already exists").build();
                    }
                }
            }
        }

        if (bc.getBindingDescription() == null) {
            bc.setBindingDescription(new DefaultBindingDescription());
        }
        DefaultBindingDescription dbd = (DefaultBindingDescription) bc.getBindingDescription();
        if (dbd.getMetaEntries() == null) {
            dbd.setMetaEntries(new ArrayList<MetaEntry>());
        }

        DefaultMetaEntry metaEntry = new DefaultMetaEntry();
        metaEntry.setName(metaEntryName);
        dbd.getMetaEntries().add(metaEntry);

        try {
            model.save(jarId, fd);
        } catch (Exception e) {
            logger.error("putMetaEntry() error during save", e);
            return Response.serverError().build();
        }

        JSONObject jsReturn = new JSONObject();
        try {
            jsReturn.put("name", metaEntryName);
        } catch (JSONException e) {
            logger.error("putMetaEntry() error creating JSON result", e);
            return Response.serverError().build();
        }

        return Response.status(Status.CREATED).entity(jsReturn.toString()).build();
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
