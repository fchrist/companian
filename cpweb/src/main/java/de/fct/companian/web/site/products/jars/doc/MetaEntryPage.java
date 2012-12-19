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

import javax.ws.rs.Consumes;
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
import de.fct.companian.model.binding.bdl.DefaultDescriptor;
import de.fct.companian.model.binding.bdl.DefaultMetaEntry;
import de.fct.companian.model.binding.bdl.DefaultTask;
import de.fct.companian.web.site.AbstractPageRenderer;
import de.fct.fdmm.bindingdl.MetaEntry;
import de.fct.fdmm.bindingdl.Task;
import de.fct.fdmm.core.FrameworkDescription;
import de.fct.fdmm.hotspot.BindingCapability;

@Path("products/{productId}/jars/{jarid}/doc/bindings/{bindingName}/metaentries/{metaEntryName}")
public class MetaEntryPage extends AbstractPageRenderer {

    private static Logger logger = LoggerFactory.getLogger(MetaEntryPage.class);

    @Override
    public String getPage() {
        // Empty
        return null;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getMetaEntryPage(@PathParam("productId") int productId,
                                     @PathParam("jarid") int jarId,
                                     @PathParam("bindingName") String bindingName,
                                     @PathParam("metaEntryName") String metaEntryName) {
        if (logger.isDebugEnabled()) {
            logger.debug("getMetaEntryPage() " + metaEntryName + " for JAR " + jarId);
        }
        MetaEntryModel model = (MetaEntryModel) this.pageModel;
        FrameworkDescription fd = model.refresh(productId, jarId);
        MetaEntry me = model.refresh(fd, bindingName, metaEntryName);

        if (me == null) {
            return Response.status(Status.NOT_FOUND).entity("Could not find meta entry " + metaEntryName)
                    .build();
        }

        return Response.ok(super.getPage()).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response postDescription(@PathParam("productId") int productId,
                                    @PathParam("jarid") int jarId,
                                    @PathParam("bindingName") String bindingName,
                                    @PathParam("metaEntryName") String metaEntryName,
                                    @FormParam("content") String content,
                                    @FormParam("id") String elementId) {
        if (logger.isDebugEnabled()) {
            logger.debug("postDescription() for " + metaEntryName + ", JAR " + jarId + ", content: "
                         + content);
        }

        if (!elementId.equals("description")) {
            return Response.status(Status.BAD_REQUEST).entity("Unknown element id " + elementId).build();
        }

        if (content == null || content.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).entity("No content found in request.").build();
        }

        MetaEntryModel model = (MetaEntryModel) this.pageModel;
        FrameworkDescription fd = model.refresh(productId, jarId);
        MetaEntry me = model.refresh(fd, bindingName, metaEntryName);

        if (me == null) {
            return Response.status(Status.NOT_FOUND).entity("Could not find Meta Entry " + metaEntryName)
                    .build();
        }

        ((DefaultMetaEntry) me).setDescription(content);

        try {
            model.save(jarId, fd);
        } catch (Exception e) {
            logger.error("postDescription() error during save", e);
            return Response.serverError().build();
        }

        return Response.ok().build();
    }

    @PUT
    @Path("/tasks")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response putTask(@PathParam("productId") int productId,
                            @PathParam("jarid") int jarId,
                            @PathParam("bindingName") String bindingName,
                            @PathParam("metaEntryName") String metaEntryName,
                            @FormParam("taskName") String taskName,
                            @FormParam("selector") String selector,
                            @FormParam("content") String content,
                            @FormParam("descriptorName") String descriptorName,
                            @FormParam("descriptorSchemaUrn") String descriptorSchemaUrn) {

        if (logger.isDebugEnabled()) {
            logger.debug("putTask() " + taskName + " for " + metaEntryName + ", JAR " + jarId);
        }

        if (metaEntryName == null || metaEntryName.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).entity("No meta entry name given").build();
        }
        
        if (taskName == null || taskName.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).entity("No Task name given").build();
        }

        MetaEntryModel model = (MetaEntryModel) this.pageModel;
        FrameworkDescription fd = model.refresh(productId, jarId);
        MetaEntry me = model.refresh(fd, bindingName, metaEntryName);

        if (me == null) {
            return Response.status(Status.NOT_FOUND).entity(
                "Could not find Meta Entry " + metaEntryName).build();
        }

        DefaultMetaEntry dme = (DefaultMetaEntry)me;
        if (dme.getTasks() == null) {
            dme.setTasks(new ArrayList<Task>());
        }
        
        DefaultTask task = new DefaultTask();
        task.setName(taskName);
        task.setSelector(selector);
        task.setContent(content);
        
        if (descriptorName != null && !descriptorName.isEmpty()) {
            DefaultDescriptor descriptor = new DefaultDescriptor();
            descriptor.setName(descriptorName);
            descriptor.setSchemaUrn(descriptorSchemaUrn);
            task.setDescriptor(descriptor);
        }
        
        dme.getTasks().add(task);

        try {
            model.save(jarId, fd);
        } catch (Exception e) {
            logger.error("putTask() error during save", e);
            return Response.serverError().build();
        }

        JSONObject jsReturn = new JSONObject();
        try {
            jsReturn.put("taskName", taskName);
        } catch (JSONException e) {
            logger.error("putTask() error creating JSON result", e);
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

        BindingCapability binding = (BindingCapability) this.pageModel.getContext().get("binding");
        navigation.add(this.createNavLink("/products/" + p.getProductId() + "/jars/" + jar.getJarId()
                                          + "/doc/bindings/" + binding.getName(), binding.getName()));

        return navigation;
    }

}
