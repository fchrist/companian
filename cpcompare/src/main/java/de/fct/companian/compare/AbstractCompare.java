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
package de.fct.companian.compare;

import java.util.List;
import java.util.Map;
import java.util.Set;

import de.fct.companian.analyze.db.model.Jar;
import de.fct.fdmm.basis.INamedElement;

public abstract class AbstractCompare {
    
    // This is the compare context which holds references
    // to the left and right compared JARs.
    protected final Map<String,Object> context;
    
    public AbstractCompare(Map<String,Object> context) {
        this.context = context;
    }
    
    protected String getLeftVersion() {
        Jar leftJar = (Jar) this.context.get("leftJar");

        return leftJar.getVersion();
    }

    protected String getRightVersion() {
        Jar rightJar = (Jar) this.context.get("rightJar");

        return rightJar.getVersion();
    }
    
    /* Helper Functions ------------------------------------------------------------------------------ */

    protected INamedElement getFromList(List<?> namedElements, INamedElement namedElement) {
        if (namedElements != null && !namedElements.isEmpty()) {
            for (Object obj : namedElements) {
                if (obj instanceof INamedElement) {
                    INamedElement ne = (INamedElement) obj;
                    if (namedElement.getName() != null && ne.getName() != null) {
                    	if (namedElement.getName().equalsIgnoreCase(ne.getName())) {
                    		return ne;
                    	}
                    }
                } else {
                    return null;
                }
            }
        }

        return null;
    }

    protected INamedElement getFromSet(Set<?> namedElements, INamedElement namedElement) {
        if (namedElements != null && !namedElements.isEmpty()) {
            for (Object obj : namedElements) {
                if (obj instanceof INamedElement) {
                    INamedElement ne = (INamedElement) obj;
                    if (namedElement.getName().equalsIgnoreCase(ne.getName())) {
                        return ne;
                    }
                } else {
                    return null;
                }
            }
        }

        return null;
    }

    protected String getFromSet(Set<String> namedElements, String namedElement) {
        if (namedElements != null && !namedElements.isEmpty()) {
            for (String elementFromSet : namedElements) {
                if (namedElement.equalsIgnoreCase(elementFromSet)) {
                    return elementFromSet;
                }
            }
        }

        return null;
    }

    protected String cleanFromHtml(String input) {
        if (input == null) {
            return input;
        }

        String output = input.trim();
        output = output.replaceAll("\n", "");
        output = output.replaceAll("<.*?>", "");
        output = output.replaceAll("[ ]+", " ");
        output = output.replaceAll("^\\$\\{.*\\}", "");

        return output;
    }

}
