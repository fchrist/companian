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
package de.fct.companian.model.binding.jel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.fct.companian.model.jel.JEL;
import de.fct.companian.model.jel.JelClass;
import de.fct.fdmm.apidl.APIDoc;
import de.fct.fdmm.apidl.PackageDescription;

public class JelAPIDoc implements APIDoc {
    
    private final JEL jel;
    
    private List<PackageDescription> packages;
    
    public JelAPIDoc(JEL jel) {
        this.jel = jel;
    }
    
    public List<PackageDescription> getPackages() {
        if (this.packages == null) {
            Map<String, List<JelClass>> packageMap = new HashMap<String,List<JelClass>>();
            for (JelClass jelClass : jel.getJelclass()) {
                if (packageMap.get(jelClass.getPackage()) == null) {
                    packageMap.put(jelClass.getPackage(), new ArrayList<JelClass>());
                }
                packageMap.get(jelClass.getPackage()).add(jelClass);
            }
            
            this.packages = new ArrayList<PackageDescription>();
            for (String pack : packageMap.keySet()) {
                JelPackageDescription jpd = new JelPackageDescription(pack, packageMap.get(pack));
                this.packages.add(jpd);
            }
            Collections.sort(this.packages, new Comparator<PackageDescription>() {
                public int compare(PackageDescription o1, PackageDescription o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
        }
        
        return packages;
    }

}
