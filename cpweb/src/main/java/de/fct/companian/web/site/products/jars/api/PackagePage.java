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
package de.fct.companian.web.site.products.jars.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.analyze.db.model.Jar;
import de.fct.companian.analyze.db.model.Product;
import de.fct.companian.web.site.AbstractPageRenderer;
import de.fct.fdmm.apidl.PackageDescription;

@Path("products/{productId}/jars/{jarId}/api/{packageName}")
public class PackagePage extends AbstractPageRenderer {

    private static Logger logger = LoggerFactory.getLogger(PackagePage.class);

    @Override
    public String getPage() {
        // Empty
        return null;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getPackagePage(@PathParam("productId") int productId,
                                   @PathParam("jarId") int jarId,
                                   @PathParam("packageName") String packageName) {
        if (logger.isDebugEnabled()) {
            logger.debug("getPackagePage() for JAR " + jarId);
        }
        PackageDescription pd = ((PackageModel) this.pageModel).refresh(productId, jarId, packageName);
        if (pd != null) {
            return Response.ok(super.getPage()).build();
        } else {
            return Response.status(Status.NOT_FOUND).entity(
                "Could not find PackageDescription for package " + packageName).build();
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
                                          + "/api/", "API"));

        return navigation;
    }

}
