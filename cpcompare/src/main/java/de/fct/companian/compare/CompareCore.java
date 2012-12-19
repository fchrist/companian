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
import de.fct.fdmm.architecturedl.ArchitectureDescription;
import de.fct.fdmm.architecturedl.ArchitectureDlProxy;
import de.fct.fdmm.architecturedl.behaviordl.BehaviorDlProxy;
import de.fct.fdmm.architecturedl.behaviordl.Process;
import de.fct.fdmm.core.ArchitectureView;
import de.fct.fdmm.core.BehaviorView;
import de.fct.fdmm.core.FrameworkDescription;
import de.fct.fdmm.core.HotSpotGroup;
import de.fct.fdmm.core.StructureView;
import de.fct.fdmm.hotspot.BindingCapability;
import de.fct.fdmm.hotspot.DeploymentCapability;
import de.fct.fdmm.hotspot.HotSpot;

public class CompareCore extends AbstractCompare {

    private static Logger log = LoggerFactory.getLogger(CompareCore.class);

    private CompareBasis basis;
    private CompareADL adl;
    private CompareBehaviorDL behaviordl;
    private CompareHotspot hotspot;
    private CompareBDL bdl;
    private CompareDDL ddl;

    private ArchitectureDlProxy sviewProxy;
    private BehaviorDlProxy bviewProxy;

    public CompareCore(Map<String,Object> context) {
        super(context);
    }

    protected FDifference compareStructureViews(FrameworkDescription leftFD, FrameworkDescription rightFD) {
        if ((leftFD.getStructureViews() == null || leftFD.getStructureViews().isEmpty())
            && (rightFD.getStructureViews() == null || rightFD.getStructureViews().isEmpty())) {
            return null;
        }

        FDifference diff = null;

        // 1) Check from left to right -> Changed and Deleted elements
        if (leftFD.getStructureViews() != null) {
            for (StructureView leftView : leftFD.getStructureViews()) {
                StructureView rightView = (StructureView) getFromList(rightFD.getStructureViews(), leftView);
                if (rightView != null) {
                    // Check for changes in this structure view
                    FDifference subDiff = compareArchitectureViews(leftView, rightView, this.sviewProxy);
                    if (subDiff != null) {
                        if (diff == null) {
                            diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                        }
                        diff.addDifference(subDiff);
                    }
                } else {
                    // Left view is not part of the right model - Deleted element
                    FDifference subDiff = new FDifference(FDKind.DeletedElement, FDRating.Warning);
                    subDiff.setSource(leftView);
                    subDiff.setDescription("The structural view '" + leftView.getName()
                                           + "' was deleted in version " + getRightVersion());
                    if (subDiff != null) {
                        if (diff == null) {
                            diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                        }
                        diff.addDifference(subDiff);
                    }
                }
            }
        }

        // 2) Check from right to left -> Created elements
        if (rightFD.getStructureViews() != null) {
            for (StructureView rightView : rightFD.getStructureViews()) {
                if (getFromList(leftFD.getStructureViews(), rightView) == null) {
                    // Right view is not part of left model - Created element
                    FDifference subDiff = new FDifference(FDKind.CreatedElement, FDRating.Info);
                    subDiff.setSource(rightView);
                    subDiff.setDescription("The structural view '" + rightView.getName()
                                           + "' was added in version " + getRightVersion());
                    if (subDiff != null) {
                        if (diff == null) {
                            diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                        }
                        diff.addDifference(subDiff);
                    }
                }
            }
        }

        if (diff != null) {
            diff.setSource(leftFD);
            diff.setDescription("There are changes in the structural views of version " + this.getRightVersion());
        }

        return diff;
    }

    protected FDifference compareBehaviorViews(FrameworkDescription leftFD, FrameworkDescription rightFD) {
        if ((leftFD.getBehaviorViews() == null || leftFD.getBehaviorViews().isEmpty())
            && (rightFD.getBehaviorViews() == null || rightFD.getBehaviorViews().isEmpty())) {
            return null;
        }

        FDifference diff = null;

        // 1) Check from left to right -> Changed and Deleted elements
        if (leftFD.getBehaviorViews() != null) {
            for (BehaviorView leftView : leftFD.getBehaviorViews()) {
                BehaviorView rightView = (BehaviorView) this
                        .getFromList(rightFD.getBehaviorViews(), leftView);
                if (rightView != null) {
                    // Check for changes in this view
                    FDifference subDiff = compareBehaviorViews(leftView, rightView, this.bviewProxy);
                    if (subDiff != null) {
                        if (diff == null) {
                            diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                        }
                        diff.addDifference(subDiff);
                    }
                } else {
                    // Left view is not part of the right model - Deleted element
                    FDifference subDiff = new FDifference(FDKind.DeletedElement, FDRating.Warning);
                    subDiff.setSource(leftView);
                    subDiff.setDescription("The behavioral view '" + leftView.getName()
                                           + "' was deleted in version " + getRightVersion());
                    if (subDiff != null) {
                        if (diff == null) {
                            diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                        }
                        diff.addDifference(subDiff);
                    }
                }
            }
        }

        // 2) Check from right to left -> Created elements
        if (rightFD.getBehaviorViews() != null) {
            for (BehaviorView rightView : rightFD.getBehaviorViews()) {
                if (getFromList(leftFD.getBehaviorViews(), rightView) == null) {
                    // Right view is not part of left model - Created element
                    FDifference subDiff = new FDifference(FDKind.CreatedElement, FDRating.Info);
                    subDiff.setSource(rightView);
                    subDiff.setDescription("The behavioral view '" + rightView.getName()
                                           + "' was added in version " + getRightVersion());
                    if (subDiff != null) {
                        if (diff == null) {
                            diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                        }
                        diff.addDifference(subDiff);
                    }
                }
            }
        }

        if (diff != null) {
            diff.setSource(leftFD);
            diff.setDescription("There are changes in the behavioral views of version " + this.getRightVersion());
        }

        return diff;
    }

    private FDifference compareBehaviorViews(BehaviorView leftView,
                                             BehaviorView rightView,
                                             BehaviorDlProxy proxy) {
        FDifference descrDiff = null;
        descrDiff = basis.compareDescriptions(leftView, rightView, leftView);

        FDifference fileDiff = null;
        FDifference processDiff = null;
        if (leftView.getProcessFile() != null) {
            if (rightView.getProcessFile() != null) {
                // Compare the Processes no matter if the file name is different
                Process leftProcess = leftView.getProcess(proxy);
                Process rightProcess = rightView.getProcess(proxy);
                processDiff = behaviordl.compareProcesses(leftProcess, rightProcess);
            } else {
                // Deleted process file
                fileDiff = new FDifference(FDKind.DeletedElement, FDRating.Conflict);
                fileDiff.setSource(leftView);
                fileDiff.setDescription("The process model file '" + leftView.getProcessFile()
                                        + "' was removed and not replaced in version "
                                        + this.getRightVersion());
            }
        } else if (rightView.getProcessFile() != null) {
            // New process description file
            fileDiff = new FDifference(FDKind.CreatedElement, FDRating.Warning);
            fileDiff.setSource(leftView);
            fileDiff.setDescription("The process model file '" + rightView.getProcessFile()
                                    + "' was not present before and added in version "
                                    + this.getRightVersion());
        }

        FDifference diff = null;
        if (descrDiff != null || fileDiff != null || processDiff != null) {
            diff = new FDifference(FDKind.ChangedElement, FDRating.Info);
            diff.setSource(leftView);
            diff.setDescription("The behavior view '" + leftView.getName() + "' has changed in version "
                                + this.getRightVersion());
            diff.addDifference(descrDiff);
            diff.addDifference(fileDiff);
            diff.addDifference(processDiff);
        }
        return diff;
    }

    protected FDifference compareArchitectureViews(ArchitectureView leftView,
                                                   ArchitectureView rightView,
                                                   ArchitectureDlProxy proxy) {

        FDifference descrDiff = null;
        descrDiff = basis.compareDescriptions(leftView, rightView, leftView);

        FDifference fileDiff = null;
        FDifference archDiff = null;
        if (leftView.getArchitectureDescriptionFile() != null) {
            if (rightView.getArchitectureDescriptionFile() != null) {
                if (!leftView.getArchitectureDescriptionFile().equals(
                    rightView.getArchitectureDescriptionFile())) {
                    fileDiff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                    fileDiff.setSource(leftView);
                    fileDiff.setDescription("The used architecture description model file has changed from '"
                                            + leftView.getArchitectureDescriptionFile() + "' to '"
                                            + rightView.getArchitectureDescriptionFile() + "' in version "
                                            + this.getRightVersion());
                }

                // Compare the architectures no matter if the file name is different
                ArchitectureDescription leftAd = leftView.getArchitectureDescription(proxy);
                ArchitectureDescription rightAd = rightView.getArchitectureDescription(proxy);
                archDiff = adl.compareArchitectureDescriptions(leftAd, rightAd);
            } else {
                // Deleted architecture description file
                fileDiff = new FDifference(FDKind.DeletedElement, FDRating.Conflict);
                fileDiff.setSource(leftView);
                fileDiff.setDescription("The architecture description model file '"
                                        + leftView.getArchitectureDescriptionFile()
                                        + "' was removed and not replaced in version "
                                        + this.getRightVersion());
            }
        } else if (rightView.getArchitectureDescriptionFile() != null) {
            // New architecture description file
            fileDiff = new FDifference(FDKind.CreatedElement, FDRating.Warning);
            fileDiff.setSource(leftView);
            fileDiff.setDescription("The architecture description model file '"
                                    + rightView.getArchitectureDescriptionFile()
                                    + "' was not present before and added in version "
                                    + this.getRightVersion());
        }

        FDifference diff = null;
        if (descrDiff != null || fileDiff != null || archDiff != null) {
            diff = new FDifference(FDKind.ChangedElement, FDRating.Info);
            diff.setSource(leftView);
            diff.setDescription("The architecture view " + leftView.getName() + " has changed in version "
                                + this.getRightVersion());
            diff.addDifference(descrDiff);
            diff.addDifference(fileDiff);
            diff.addDifference(archDiff);
        }
        return diff;
    }

    protected FDifference compareHotSpotGroups(FrameworkDescription leftFD, FrameworkDescription rightFD) {
        if ((leftFD.getHotSpotGroups() == null || leftFD.getHotSpotGroups().isEmpty())
            && (rightFD.getHotSpotGroups() == null || rightFD.getHotSpotGroups().isEmpty())) {
            return null;
        }

        FDifference diff = null;

        if (leftFD.getHotSpotGroups() != null) {
            for (HotSpotGroup leftHsg : leftFD.getHotSpotGroups()) {
                HotSpotGroup rightHsg = (HotSpotGroup) this.getFromList(rightFD.getHotSpotGroups(), leftHsg);
                if (rightHsg != null) {
                    // Check for changes
                    FDifference subDiff = this.compareHotSpotGroups(leftHsg, rightHsg);
                    if (subDiff != null) {
                        if (diff == null) {
                            diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                        }
                        diff.addDifference(subDiff);
                    }
                } else {
                    // Left view is not part of the right model - Deleted element
                    FDifference subDiff = new FDifference(FDKind.DeletedElement, FDRating.Conflict);
                    subDiff.setSource(leftHsg);
                    subDiff.setDescription("The Hotspot Group '" + leftHsg.getName()
                                           + "' was deleted in version " + getRightVersion());
                    if (subDiff != null) {
                        if (diff == null) {
                            diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                        }
                        diff.addDifference(subDiff);
                    }
                }
            }
        }

        if (rightFD.getHotSpotGroups() != null) {
            for (HotSpotGroup rightHsg : rightFD.getHotSpotGroups()) {
                if (getFromList(leftFD.getHotSpotGroups(), rightHsg) == null) {
                    // Right group is not part of left model - Created element
                    FDifference subDiff = new FDifference(FDKind.CreatedElement, FDRating.Info);
                    subDiff.setSource(rightHsg);
                    subDiff.setDescription("The Hotspot Group '" + rightHsg.getName()
                                           + "' was added in version " + getRightVersion());
                    if (subDiff != null) {
                        if (diff == null) {
                            diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                        }
                        diff.addDifference(subDiff);
                    }
                }
            }
        }

        if (diff != null) {
            diff.setSource(leftFD);
            diff.setDescription("There are Hotspot Group changes.");
            log.debug("hotspot group compare result\n{}", diff);
        }

        return diff;
    }

    protected FDifference compareHotSpotGroups(HotSpotGroup leftHsg, HotSpotGroup rightHsg) {

        FDifference diff = null;

        FDifference descrDiff = basis.compareDescriptions(leftHsg, rightHsg, leftHsg);
        if (descrDiff != null) {
            diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
            diff.addDifference(descrDiff);
        }

        if (leftHsg.getHotSpots() != null) {
            for (HotSpot leftHs : leftHsg.getHotSpots()) {
                HotSpot rightHs = (HotSpot) this.getFromList(rightHsg.getHotSpots(), leftHs);
                if (rightHs != null) {
                    FDifference hsDiff = hotspot.compareHotSpots(leftHs, rightHs);
                    if (hsDiff != null) {
                        if (diff == null) {
                            diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                        }
                        diff.addDifference(hsDiff);
                    }
                } else {
                    // Deleted Hotspot
                    FDifference subDiff = new FDifference(FDKind.DeletedElement, FDRating.Conflict);
                    subDiff.setSource(leftHs);
                    subDiff.setDescription("The Hotspot '" + leftHs.getName()
                                           + "' was deleted from the Hotspot Group '" + leftHsg.getName()
                                           + "' in version " + this.getRightVersion());
                    if (diff == null) {
                        diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                    }
                    diff.addDifference(subDiff);
                }
            }
        }

        if (rightHsg.getHotSpots() != null) {
            for (HotSpot rightHs : rightHsg.getHotSpots()) {
                if (this.getFromList(leftHsg.getHotSpots(), rightHs) == null) {
                    // Created Hotspot
                    FDifference subDiff = new FDifference(FDKind.CreatedElement, FDRating.Info);
                    subDiff.setSource(rightHs);
                    subDiff.setDescription("The Hotspot '" + rightHs.getName()
                                           + "' was created in the Hotspot Group '" + leftHsg.getName()
                                           + "' in version " + this.getRightVersion());
                    if (diff == null) {
                        diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                    }
                    diff.addDifference(subDiff);
                }
            }
        }

        if (diff != null) {
            diff.setSource(leftHsg);
            diff.setDescription("The Hotspot Group '" + leftHsg.getName() + "' has changed in version "
                                + this.getRightVersion());
        }
        return diff;
    }

    protected FDifference compareBindingCapabilities(FrameworkDescription leftFD, FrameworkDescription rightFD) {
        if ((leftFD.getBindingCapabilities() == null || leftFD.getBindingCapabilities().isEmpty())
            && (rightFD.getBindingCapabilities() == null || rightFD.getBindingCapabilities().isEmpty())) {
            return null;
        }

        FDifference diff = null;

        if (leftFD.getBindingCapabilities() != null) {
            for (BindingCapability leftBinding : leftFD.getBindingCapabilities()) {
                BindingCapability rightBinding = (BindingCapability) this.getFromList(
                    rightFD.getBindingCapabilities(), leftBinding);
                if (rightBinding != null) {
                    // Check for changes
                    FDifference subDiff = compareBindingCapabilities(leftBinding, rightBinding);
                    if (subDiff != null) {
                        if (diff == null) {
                            diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                        }
                        diff.addDifference(subDiff);
                    }
                } else {
                    // Left is not part of the right model - Deleted element
                    FDifference subDiff = new FDifference(FDKind.DeletedElement, FDRating.Conflict);
                    subDiff.setSource(leftBinding);
                    subDiff.setDescription("The Binding Capability '" + leftBinding.getName()
                                           + "' was deleted in version " + getRightVersion());
                    if (subDiff != null) {
                        if (diff == null) {
                            diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                        }
                        diff.addDifference(subDiff);
                    }
                }
            }
        }

        if (rightFD.getBindingCapabilities() != null) {
            for (BindingCapability rightBinding : rightFD.getBindingCapabilities()) {
                if (getFromList(leftFD.getBindingCapabilities(), rightBinding) == null) {
                    // Right is not part of left model - Created element
                    FDifference subDiff = new FDifference(FDKind.CreatedElement, FDRating.Info);
                    subDiff.setSource(rightBinding);
                    subDiff.setDescription("The Binding Capability '" + rightBinding.getName()
                                           + "' was added in version " + getRightVersion());
                    if (subDiff != null) {
                        if (diff == null) {
                            diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                        }
                        diff.addDifference(subDiff);
                    }
                }
            }
        }

        if (diff != null) {
            diff.setSource(leftFD);
            diff.setDescription("There are Binding Capability changes.");
        }

        return diff;
    }

    protected FDifference compareBindingCapabilities(BindingCapability leftBinding,
                                                     BindingCapability rightBinding) {

        FDifference descDiff = basis.compareDescriptions(leftBinding, rightBinding, leftBinding);

        FDifference bindDiff = null;
        if (leftBinding.getBindingDescription() != null) {
            if (rightBinding.getBindingDescription() != null) {
                bindDiff = bdl.compareBindingDescriptions(leftBinding.getBindingDescription(),
                    rightBinding.getBindingDescription());
            }
        }

        FDifference diff = null;
        if (descDiff != null || bindDiff != null) {
            diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
            diff.setSource(leftBinding);
            diff.setDescription("The Binding Capability '" + leftBinding.getName()
                                + "' has changed in version " + this.getRightVersion());
            diff.addDifference(descDiff);
            diff.addDifference(bindDiff);
        }
        return diff;
    }

    protected FDifference compareDeploymentCapabilities(FrameworkDescription leftFD,
                                                        FrameworkDescription rightFD) {
        if ((leftFD.getDeploymentCapabilities() == null || leftFD.getDeploymentCapabilities().isEmpty())
            && (rightFD.getDeploymentCapabilities() == null || rightFD.getDeploymentCapabilities().isEmpty())) {
            return null;
        }

        FDifference diff = null;

        if (leftFD.getDeploymentCapabilities() != null) {
            for (DeploymentCapability leftDeployment : leftFD.getDeploymentCapabilities()) {
                DeploymentCapability rightDeployment = (DeploymentCapability) this.getFromList(
                    rightFD.getDeploymentCapabilities(), leftDeployment);
                if (rightDeployment != null) {
                    // Check for changes
                    FDifference subDiff = compareDeploymentCapabilities(leftDeployment, rightDeployment);
                    if (subDiff != null) {
                        if (diff == null) {
                            diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                        }
                        diff.addDifference(subDiff);
                    }
                } else {
                    // Left is not part of the right model - Deleted element
                    FDifference subDiff = new FDifference(FDKind.DeletedElement, FDRating.Conflict);
                    subDiff.setSource(leftDeployment);
                    subDiff.setDescription("The Deployment Capability '" + leftDeployment.getName()
                                           + "' was deleted in version " + getRightVersion());
                    if (subDiff != null) {
                        if (diff == null) {
                            diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                        }
                        diff.addDifference(subDiff);
                    }
                }
            }
        }

        if (rightFD.getDeploymentCapabilities() != null) {
            for (DeploymentCapability rightDeployment : rightFD.getDeploymentCapabilities()) {
                if (getFromList(leftFD.getDeploymentCapabilities(), rightDeployment) == null) {
                    // Right is not part of left model - Created element
                    FDifference subDiff = new FDifference(FDKind.CreatedElement, FDRating.Info);
                    subDiff.setSource(rightDeployment);
                    subDiff.setDescription("The Deployment Capability '" + rightDeployment.getName()
                                           + "' was added in version " + getRightVersion());
                    if (subDiff != null) {
                        if (diff == null) {
                            diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                        }
                        diff.addDifference(subDiff);
                    }
                }
            }
        }

        if (diff != null) {
            diff.setSource(leftFD);
            diff.setDescription("There are Deployment Capability changes.");
        }

        return diff;
    }

    protected FDifference compareDeploymentCapabilities(DeploymentCapability leftDeployment,
                                                        DeploymentCapability rightDeployment) {

        FDifference descDiff = basis.compareDescriptions(leftDeployment, rightDeployment, leftDeployment);

        FDifference deplDiff = null;
        if (leftDeployment.getDeploymentDescription() != null) {
            if (rightDeployment.getDeploymentDescription() != null) {
                deplDiff = ddl.compareDeployments(leftDeployment.getDeploymentDescription(),
                    rightDeployment.getDeploymentDescription());
            }
        }

        FDifference diff = null;
        if (descDiff != null || deplDiff != null) {
            diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
            diff.setSource(leftDeployment);
            diff.setDescription("The Deployment Capability '" + leftDeployment.getName()
                                + "' has changed in version " + this.getRightVersion());
            diff.addDifference(descDiff);
            diff.addDifference(deplDiff);
        }
        return diff;
    }

    public void setBasis(CompareBasis basis) {
        this.basis = basis;
    }

    public void setAdl(CompareADL adl) {
        this.adl = adl;
    }

    public void setBehaviordl(CompareBehaviorDL behaviordl) {
        this.behaviordl = behaviordl;
    }

    public void setHotspot(CompareHotspot hotspot) {
        this.hotspot = hotspot;
    }

    public void setBdl(CompareBDL bdl) {
        this.bdl = bdl;
    }

    public void setDdl(CompareDDL ddl) {
        this.ddl = ddl;
    }

    public void setSviewProxy(ArchitectureDlProxy sviewProxy) {
        this.sviewProxy = sviewProxy;
    }

    public void setBviewProxy(BehaviorDlProxy bviewProxy) {
        this.bviewProxy = bviewProxy;
    }

}
