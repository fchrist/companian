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
package de.fct.fdmm.hotspot;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlTransient;

import de.fct.fdmm.basis.Description;

public class HotSpot extends Description {

    private Generalization generalization;
    private BindingCapability binding;
    private DeploymentCapability deployment;
    private List<HotSpotExample> examples;
    private List<Constraint> constraints;
    private List<HotSpotUnit> units;

    @XmlTransient // TODO support Hotspot inheritance
    public Generalization getGeneralization() {
        return generalization;
    }

    public void setGeneralization(Generalization generalization) {
        this.generalization = generalization;
    }

    @XmlIDREF
    public BindingCapability getBinding() {
        return binding;
    }

    public void setBinding(BindingCapability binding) {
        this.binding = binding;
    }

    @XmlIDREF
    public DeploymentCapability getDeployment() {
        return deployment;
    }

    public void setDeployment(DeploymentCapability deployment) {
        this.deployment = deployment;
    }

    @XmlTransient
    public List<HotSpotExample> getExamples() {
        return examples;
    }

    public void setExamples(List<HotSpotExample> examples) {
        this.examples = examples;
    }

    @XmlTransient
    public List<Constraint> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<Constraint> constraints) {
        this.constraints = constraints;
    }

    @XmlElements({
        @XmlElement(name="CodingUnit", type=CodingUnit.class)
    })
    public List<HotSpotUnit> getUnits() {
        return units;
    }

    public void setUnits(List<HotSpotUnit> units) {
        this.units = units;
    }

}
