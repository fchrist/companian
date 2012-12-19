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
import de.fct.fdmm.constraintdl.Assertion;

public class CompareCDL extends AbstractCompare {

    private CompareBasis basis;
    
    public CompareCDL(Map<String,Object> context) {
        super(context);
    }

    protected FDifference compareConstraintAssertions(Assertion leftAssertion,
                                                    Assertion rightAssertion) {

        FDifference diff = null;

        FDifference nameDiff = basis.compareNamedElements("assertion", leftAssertion, rightAssertion, leftAssertion);
        FDifference sigDiff = basis.compareValues("signature", leftAssertion.getSignature(), rightAssertion
                .getSignature(), leftAssertion);
        FDifference paramDiff = basis.compareLists("parameter", leftAssertion.getParameters(), rightAssertion
                .getParameters(), leftAssertion);

        if (nameDiff != null || sigDiff != null || paramDiff != null) {
            diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
            diff.setSource(leftAssertion);
            diff.setDescription("The assertion '" + leftAssertion.getName() + "' was changed in version "
                                + this.getRightVersion());
            diff.addDifference(nameDiff);
            diff.addDifference(sigDiff);
        }
        return diff;
    }

    public void setBasis(CompareBasis basis) {
        this.basis = basis;
    }

}
