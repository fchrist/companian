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

import de.fct.companian.analyze.db.dao.JarDao;
import de.fct.fdmm.apidl.APIDoc;
import de.fct.fdmm.apidl.PackageDescription;

public class PackageModel extends APIDocModel {

    
    public PackageModel(JarDao jarDao) {
        super(jarDao);
    }

    public PackageDescription refresh(int productId, int jarId, String packageName) {
        APIDoc apidoc = super.refresh(productId, jarId); 
        
        PackageDescription packageDescription = null;
        for (PackageDescription pd : apidoc.getPackages()) {
            if (pd.getName().equals(packageName)) {
                packageDescription = pd;
                this.context.put("package", packageDescription);
                break;
            }
        }
        
        return packageDescription;
    }

}
