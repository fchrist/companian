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
package de.fct.companian.web.site;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("ccc")
public class CCCPage extends AbstractPageRenderer {

    @SuppressWarnings("unused")
    private static Logger logger = LoggerFactory.getLogger(CCCPage.class);

    @Override
    public String getPage() {
        return "";
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String performCCC(@QueryParam("usingJarId") int usingJarId,
                             @QueryParam("usedJarId") int usedJarId,
                             @QueryParam("futureJarId") int futureJarId) {

        CCCModel model = (CCCModel) this.pageModel;
        model.performCCC(usingJarId, usedJarId, futureJarId);

        return this.renderPage(model.getContext(), this.getTemplate("CCCResult"));
    }

    @Override
    protected List<String> getNavigation() {
        return super.getDefaultNavigation();
    }

}
