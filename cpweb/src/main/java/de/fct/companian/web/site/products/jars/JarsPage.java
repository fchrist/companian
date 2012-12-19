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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.analyze.db.model.Product;
import de.fct.companian.web.site.AbstractPageRenderer;

@Path("products/{productId}/jars")
public class JarsPage extends AbstractPageRenderer {

    private static Logger logger = LoggerFactory.getLogger(JarsPage.class);
    
    private JarModel jarModel;
    
    @GET
    @Path("/{jarid}")
    @Produces("text/html")
    public String getJarPage(@PathParam("productId") int productId, @PathParam("jarid") int jarId) {
        if (logger.isDebugEnabled()) {
            logger.debug("getJarPage() for JAR " + jarId);
        }
        this.jarModel.refresh(productId, jarId);
        return this.renderPage(this.jarModel.getContext(), this.getTemplate("JarPage"));
    }
    
    @Override
    protected List<String> getNavigation() {
        List<String> navigation = super.getDefaultNavigation();
        Product p = (Product) this.jarModel.getContext().get("product");
        navigation.add(this.createNavLink("/products/" + p.getProductId(), p.getName()));
        return navigation;
    }
    
    public void setJarModel(JarModel jarModel) {
        this.jarModel = jarModel;
    }
    
}
