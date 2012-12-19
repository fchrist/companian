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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.fdmm.FDKind;
import de.fct.fdmm.FDRating;
import de.fct.fdmm.FDifference;
import de.fct.fdmm.apidl.APIDlProxy;
import de.fct.fdmm.architecturedl.ArchitectureDlProxy;
import de.fct.fdmm.architecturedl.behaviordl.BehaviorDlProxy;
import de.fct.fdmm.core.FrameworkDescription;
import de.fct.fdmm.protocoldl.ProtocolDlProxy;

public class FDMCompare extends AbstractCompare  {

	private static Logger log = LoggerFactory.getLogger(FDMCompare.class);
	
    private ArchitectureDlProxy sviewProxy;
    private BehaviorDlProxy bviewProxy;
    
    private ProtocolDlProxy protocolProxy;
    
    private APIDlProxy apiProxy;
    
    private final CompareBasis basis;
    private final CompareCore core;
    private final CompareADL adl;
    private final CompareBehaviorDL behaviordl;
    private final CompareHotspot hotspot;
    private final CompareCDL cdl;
    private final ComparePDL pdl;
    private final CompareAPIDL apidl;
    private final CompareBDL bdl;
    private final CompareDDL ddl;
    
    public FDMCompare(Map<String,Object> context) {
        super(context);
        log.debug("FDM comparing {}", context);
        
        this.basis = new CompareBasis(context);
        this.adl = new CompareADL(context);
        this.behaviordl = new CompareBehaviorDL(context);
        this.cdl = new CompareCDL(context);
        this.pdl = new ComparePDL(context);
        this.apidl = new CompareAPIDL(context);
        this.hotspot = new CompareHotspot(context);
        this.bdl = new CompareBDL(context);
        this.ddl = new CompareDDL(context);
        this.core = new CompareCore(context);
    }
    
    public void init() {
        this.cdl.setBasis(basis);

        this.pdl.setProxy(this.protocolProxy);

        this.apidl.setProxy(apiProxy);
        this.apidl.setBasis(basis);
        
        this.hotspot.setBasis(basis);
        this.hotspot.setCdl(cdl);
        this.hotspot.setPdl(pdl);
        this.hotspot.setApidl(apidl);
        this.hotspot.setApiProxy(apiProxy);

        this.bdl.setBasis(basis);
        this.ddl.setBasis(basis);
        
        this.behaviordl.setBasis(basis);

        this.core.setBasis(basis);
        this.core.setAdl(adl);
        this.core.setBehaviordl(behaviordl);
        this.core.setHotspot(hotspot);
        this.core.setBdl(bdl);
        this.core.setDdl(ddl);
        this.core.setSviewProxy(sviewProxy);
        this.core.setBviewProxy(bviewProxy);
    }
    
    public FDCompareResult compare(FrameworkDescription leftFD, FrameworkDescription rightFD) {
        FDCompareResult result = new FDCompareResult();
        
        if (leftFD != null) {
            if (rightFD != null) {
                result.addDifference(basis.compareNamedElements("framework name", leftFD, rightFD, leftFD));
                result.addDifference(basis.compareDescriptions(leftFD, rightFD, leftFD));
                result.addDifference(core.compareStructureViews(leftFD, rightFD));
                result.addDifference(core.compareBehaviorViews(leftFD, rightFD));
                result.addDifference(core.compareHotSpotGroups(leftFD, rightFD));
                result.addDifference(core.compareBindingCapabilities(leftFD, rightFD));
                result.addDifference(core.compareDeploymentCapabilities(leftFD, rightFD));
            } else {
                FDifference diff = new FDifference(FDKind.DeletedElement, FDRating.Conflict);
                diff.setSource(leftFD);
                diff.setDescription("There is no FDM available for version " + this.getRightVersion());
                result.addDifference(diff);
            }
        } else if (rightFD != null) {
            FDifference diff = new FDifference(FDKind.CreatedElement, FDRating.Conflict);
            diff.setSource(rightFD);
            diff.setDescription("There is no FDM available for version " + this.getLeftVersion());
            result.addDifference(diff);
        }
        
        postprocessResult(result);
        
        return result;
    }

    private void postprocessResult(FDCompareResult result) {
        for (FDifference diff : result.getDiffs()) {
            adjustRating(diff);
        }
    }

    private void adjustRating(FDifference diff) {        
        FDRating highRating = null;
        for (FDifference subDiff : diff.getSubDiffs()) {
            adjustRating(subDiff);
            if (highRating == null) {
                highRating = subDiff.getRating();
            }
            else if (subDiff.getRating().ordinal() > highRating.ordinal()) {
                highRating = subDiff.getRating();
            }
        }
        if (highRating != null) {
            diff.setRating(highRating);
        }        
    }

    public void setSviewProxy(ArchitectureDlProxy sviewProxy) {
        this.sviewProxy = sviewProxy;
    }

    public void setBviewProxy(BehaviorDlProxy bviewProxy) {
        this.bviewProxy = bviewProxy;
    }

    public void setProtocolProxy(ProtocolDlProxy protocolProxy) {
        this.protocolProxy = protocolProxy;
    }

    public void setApiProxy(APIDlProxy apiProxy) {
        this.apiProxy = apiProxy;
    }
    
}
