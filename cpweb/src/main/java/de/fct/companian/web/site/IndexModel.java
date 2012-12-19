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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.analyze.db.dao.JarDao;
import de.fct.companian.analyze.db.dao.ProductDao;
import de.fct.companian.analyze.db.model.Jar;
import de.fct.companian.analyze.db.model.Product;


public class IndexModel extends AbstractPageModel {

    @SuppressWarnings("unused")
    private Logger logger = LoggerFactory.getLogger(IndexModel.class);
    
    private final ProductDao productDao;
    private final JarDao jarDao;
    
    public IndexModel(ProductDao productDao, JarDao jarDao) {
        this.productDao = productDao;
        this.jarDao = jarDao;
    }
    
    @Override
    public void init() {
        this.context.put("title", "Welcome!");
        List<Product> products = this.productDao.listAllProducts();
        this.context.put("products", products);
        
        List<Jar> usableJars = this.jarDao.listAllJars();
        this.context.put("useableJars", usableJars);
    }

}
