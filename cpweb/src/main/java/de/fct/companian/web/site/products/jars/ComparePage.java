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
package de.fct.companian.web.site.products.jars;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.analyze.db.model.Jar;
import de.fct.companian.analyze.db.model.Product;
import de.fct.companian.compare.FDCompareResult;
import de.fct.companian.web.site.AbstractPageRenderer;
import de.fct.fdmm.core.FrameworkDescription;

@Path("products/{productId}/jars/compare")
public class ComparePage extends AbstractPageRenderer {

    private static Logger logger = LoggerFactory.getLogger(ComparePage.class);

    public String getPage() {
        return "";
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String compare(@PathParam("productId") int productId,
                          @QueryParam("left") int leftJarId,
                          @QueryParam("right") int rightJarId) {

        CompareModel model = (CompareModel) this.pageModel;
        model.refresh(productId, leftJarId, rightJarId);

        return this.renderPage(model.getContext(), this.getTemplate("CompareResult"));
    }

    @GET
    @Path("/fdm")
    @Produces(MediaType.APPLICATION_JSON)
    public Response compareFDMJson(@PathParam("productId") int productId,
                                   @QueryParam("left") int leftJarId,
                                   @QueryParam("right") int rightJarId) {

        if (logger.isDebugEnabled()) {
            logger.debug("compareFDMJson() left " + leftJarId + ", right " + rightJarId);
        }

        CompareModel model = (CompareModel) this.pageModel;
        FrameworkDescription leftFD = model.refresh(productId, leftJarId, "leftJar");

        if (leftFD == null) {
            return Response.status(Status.NO_CONTENT).entity(
                "No framework description found for left JAR " + leftJarId).build();
        }

        FrameworkDescription rightFD = model.refresh(productId, rightJarId, "rightJar");
        if (rightFD == null) {
            return Response.status(Status.NO_CONTENT).entity(
                "No framework description found for right JAR " + rightJarId).build();
        }

        FDCompareResult result = model.frameworkCompare((Jar) model.getContext().get("leftJar"), leftFD,
            (Jar) model.getContext().get("rightJar"), rightFD);

        return Response.ok(result).build();
    }

    @GET
    @Path("/fdm")
    @Produces(MediaType.TEXT_HTML)
    public Response compareFDMPage(@PathParam("productId") int productId,
                                   @QueryParam("left") int leftJarId,
                                   @QueryParam("right") int rightJarId) {

        if (logger.isDebugEnabled()) {
            logger.debug("compareFDMPage() left " + leftJarId + ", right " + rightJarId);
        }

        CompareModel model = (CompareModel) this.pageModel;

        FrameworkDescription leftFD = model.refresh(productId, leftJarId, "leftJar");
        if (leftFD == null) {
            return Response.status(Status.NO_CONTENT).entity(
                "No framework description found for left JAR " + leftJarId).build();
        }

        FrameworkDescription rightFD = model.refresh(productId, rightJarId, "rightJar");
        if (rightFD == null) {
            return Response.status(Status.NO_CONTENT).entity(
                "No framework description found for right JAR " + rightJarId).build();
        }

        FDCompareResult result = model.frameworkCompare((Jar) model.getContext().get("leftJar"), leftFD, (Jar) model.getContext().get(
            "rightJar"), rightFD);
        model.getContext().put("result", result);

        return Response.ok(this.renderPage(model.getContext(), this.getTemplate("FDMCompareResult"))).build();
    }

    @Override
    protected List<String> getNavigation() {
        List<String> navigation = super.getDefaultNavigation();

        Product p = (Product) this.pageModel.getContext().get("product");
        navigation.add(this.createNavLink("/products/" + p.getProductId(), p.getName()));

        Jar jar = (Jar) this.pageModel.getContext().get("leftJar");
        navigation.add(this.createNavLink("/products/" + p.getProductId() + "/jars/" + jar.getJarId(),
            jar.getArtifact() + " " + jar.getVersion()));

        return navigation;
    }

}
