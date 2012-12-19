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
package de.fct.companian.web.site.products;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.web.site.AbstractPageRenderer;

@Path("products")
public class ProductsPage extends AbstractPageRenderer {

    private static Logger logger = LoggerFactory.getLogger(ProductsPage.class);

    private ProductJarsModel productJarsModel;

    @Path("/{productId}")
    @GET
    @Produces("text/html")
    public String renderJarsPage(@PathParam("productId") int productId) {
        if (logger.isDebugEnabled()) {
            logger.debug("renderJarsPage() for product " + productId);
        }

        this.productJarsModel.refresh(productId);
        return this.renderPage(this.productJarsModel.getContext(), this.getTemplate("ProductJarsPage"));
    }

    @Override
    protected List<String> getNavigation() {
        List<String> navigation = super.getDefaultNavigation();
        return navigation;
    }

    public void setProductJarsModel(ProductJarsModel productJarsModel) {
        this.productJarsModel = productJarsModel;
    }

}
