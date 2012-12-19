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

import java.util.Map;

import de.fct.fdmm.FDKind;
import de.fct.fdmm.FDRating;
import de.fct.fdmm.FDifference;
import de.fct.fdmm.deploymentdl.DeploymentDescription;

public class CompareDDL extends AbstractCompare {

    private CompareBasis basis;

    public CompareDDL(Map<String,Object> context) {
        super(context);
    }

    public FDifference compareDeployments(DeploymentDescription leftDeployment, DeploymentDescription rightDeployment) {

        FDifference diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);

        diff.addDifference(basis.compareDescriptions("installation description", leftDeployment
                .getInstallationDescription(), rightDeployment.getInstallationDescription(), leftDeployment));
        diff.addDifference(basis.compareDescriptions("package format description", leftDeployment.getPackageFormatDescription(),
            rightDeployment.getPackageFormatDescription(), leftDeployment));

        if (diff.getSubDiffs().isEmpty()) {
            diff = null;
        }
        else {
            diff.setSource(leftDeployment);
            diff.setDescription("The Deployment has changed in version " + this.getRightVersion());
        }
        return diff;
    }

    public void setBasis(CompareBasis basis) {
        this.basis = basis;
    }
}
