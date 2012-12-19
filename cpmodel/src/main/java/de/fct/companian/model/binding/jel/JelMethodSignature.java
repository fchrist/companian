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
import java.util.List;

import de.fct.companian.model.jel.JelClass;
import de.fct.companian.model.jel.JelComment;
import de.fct.companian.model.jel.JelMethod;
import de.fct.companian.model.jel.JelParam;
import de.fct.companian.model.jel.JelParams;
import de.fct.fdmm.apidl.ExceptionDescription;
import de.fct.fdmm.apidl.MethodSignature;
import de.fct.fdmm.apidl.PackageDescription;
import de.fct.fdmm.apidl.TypeDescription;

public class JelMethodSignature implements MethodSignature {

    private final JelMethod jelMethod;

    public JelMethodSignature(JelMethod jelMethod) {
        this.jelMethod = jelMethod;
    }

    public List<ExceptionDescription> getExceptions() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getName() {
        return this.jelMethod.getName();
    }
    
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        if (this.jelMethod.getComment() != null) {
            for (JelComment jelComment : this.jelMethod.getComment()) {
                sb.append(jelComment.getDescription()).append("\n");
            }
        }
        if (this.jelMethod.getReturncomment() != null) {
            sb.append("<br>\nReturns: ");
            sb.append(this.jelMethod.getReturncomment());
        }
        return sb.toString();
    }

    public List<TypeDescription> getParameters() {
        List<TypeDescription> parameters = new ArrayList<TypeDescription>();

        List<JelParams> jelParamsList = this.jelMethod.getParams();
        if (jelParamsList != null) {
            for (JelParams jelParams : jelParamsList) {
                if (jelParams.getParam() != null) {
                    for (JelParam jelParam : jelParams.getParam()) {
                        TypeDescription td = this.generateTypeDescription(jelParam.getFulltype());
                        if (td != null) {
                            parameters.add(td);
                        }
                    }
                }
            }
        }

        return parameters;
    }

    public TypeDescription getReturnType() {
        String fullType = this.jelMethod.getFulltype();
        return generateTypeDescription(fullType);
    }

    private TypeDescription generateTypeDescription(String fulltype) {
        int lastDot = fulltype.lastIndexOf('.');
        String pack = null;
        String type = fulltype;
        if (lastDot > 0) {
            pack = fulltype.substring(0, lastDot);
            type = fulltype.substring(lastDot + 1);
        }
        
        if (pack != null) {
            if (pack.startsWith("java.") || pack.startsWith("javax.")) {
                pack = null;
            }            
        }
        
        PackageDescription pd = new JelPackageDescription(pack, null);
        JelClass jelClass = new JelClass();
        jelClass.setType(type);
        jelClass.setFulltype(fulltype);
        TypeDescription td = new JelClassDescription(jelClass, pd);
        return td;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        //sb.append(this.jelMethod.getFulltype()).append(":");
        sb.append(this.jelMethod.getName()).append("(");
        List<JelParams> jelParamsList = this.jelMethod.getParams();
        if (jelParamsList != null) {
            boolean firstParam = true;
            for (JelParams jelParams : jelParamsList) {
                if (jelParams.getParam() != null) {
                    for (JelParam jelParam : jelParams.getParam()) {
                        if (!firstParam) {
                            sb.append(", ");
                        }
                        sb.append(jelParam.getFulltype());
                        firstParam = false;
                    }
                }
            }
        }
        sb.append(")");
        return sb.toString();
    }

}
