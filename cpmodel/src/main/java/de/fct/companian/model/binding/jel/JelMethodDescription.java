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

import de.fct.companian.model.jel.JelMethod;
import de.fct.fdmm.apidl.MethodDescription;
import de.fct.fdmm.apidl.MethodSignature;
import de.fct.fdmm.apidl.TypeDescription;

public class JelMethodDescription implements MethodDescription {

    private final JelMethod jelMethod;
    private final JelTypeDescription typeDescription;
    
    public JelMethodDescription (JelMethod jelMethod, JelTypeDescription typeDescription) {
        this.jelMethod = jelMethod;
        this.typeDescription = typeDescription;
    }
    
    public TypeDescription getDefiningType() {
        return this.typeDescription;
    }

    public MethodSignature getSignature() {
        return new JelMethodSignature(this.jelMethod);
    }

    public String getName() {
        return this.getSignature().toString();
    }

}
