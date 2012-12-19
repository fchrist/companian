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
import de.fct.fdmm.apidl.PackageDescription;
import de.fct.fdmm.apidl.TypeDescription;

public class TypeModel extends PackageModel {

    public TypeModel(JarDao jarDao) {
        super(jarDao);
    }

    public TypeDescription refresh(int productId, int jarId, String packageName, String typeName) {
        PackageDescription pd = super.refresh(productId, jarId, packageName);
        
        TypeDescription typeDescription = null;
        for(TypeDescription td : pd.getOwnedTypes()) {
            if (td.getName().equals(typeName)) {
                typeDescription = td;
                this.context.put("type", td);
                break;
            }
        }
        
        return typeDescription;
    }

}
