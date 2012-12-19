package de.fct.companian.compare;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.fdmm.FDKind;
import de.fct.fdmm.FDRating;
import de.fct.fdmm.FDifference;
import de.fct.fdmm.apidl.APIDlProxy;
import de.fct.fdmm.apidl.MethodDescription;
import de.fct.fdmm.hotspot.BindingCapability;
import de.fct.fdmm.hotspot.CodingUnit;
import de.fct.fdmm.hotspot.Constraint;
import de.fct.fdmm.hotspot.DeploymentCapability;
import de.fct.fdmm.hotspot.HookCall;
import de.fct.fdmm.hotspot.HookProtocol;
import de.fct.fdmm.hotspot.HotSpot;
import de.fct.fdmm.hotspot.HotSpotUnit;

public class CompareHotspot extends AbstractCompare {

    private static Logger log = LoggerFactory.getLogger(CompareHotspot.class);

    private CompareBasis basis;
    private CompareCDL cdl;
    private ComparePDL pdl;
    private CompareAPIDL apidl;
    
    private APIDlProxy apiProxy;

	public CompareHotspot(Map<String,Object> context) {
        super(context);
    }

    protected FDifference compareHotSpots(HotSpot leftHs, HotSpot rightHs) {
    	log.debug("comparing hot spots {} <> {}", leftHs, rightHs);
        FDifference diff = null;

        FDifference descDiff = basis.compareDescriptions(leftHs, rightHs, leftHs);
        FDifference bindDiff = this.compareHsBinding(leftHs.getBinding(), rightHs.getBinding());
        FDifference deplDiff = this.compareHsDeployment(leftHs.getDeployment(), rightHs.getDeployment());
        FDifference consDiff = this.compareHsConstraints(leftHs, rightHs);
        FDifference unitDiff = this.compareHsUnits(leftHs, rightHs);

        if (descDiff != null || bindDiff != null || deplDiff != null || consDiff != null || unitDiff != null) {
            diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
            diff.setSource(leftHs);
            diff.setDescription("The Hotspot '" + leftHs.getName() + "' has changed in version "
                                + this.getRightVersion());
            diff.addDifference(descDiff);
            diff.addDifference(bindDiff);
            diff.addDifference(deplDiff);
            diff.addDifference(consDiff);
            diff.addDifference(unitDiff);
        }

        return diff;
    }

    private FDifference compareHsBinding(BindingCapability leftBinding,
                                         BindingCapability rightBinding) {

        return basis.compareNamedElements("binding", leftBinding, rightBinding, leftBinding);
    }

    private FDifference compareHsDeployment(DeploymentCapability leftDeployment,
                                            DeploymentCapability rightDeployment) {

        return basis.compareNamedElements("deployment", leftDeployment, rightDeployment, leftDeployment);
    }

    private FDifference compareHsConstraints(HotSpot leftHs, HotSpot rightHs) {

        FDifference diff = null;

        if (leftHs.getConstraints() != null) {
            for (Constraint leftConstr : leftHs.getConstraints()) {
                Constraint rightConstr = (Constraint) this.getFromList(rightHs.getConstraints(), leftConstr);
                if (rightConstr != null) {
                    FDifference subDiff = this.compareConstraints(leftConstr, rightConstr);
                    if (subDiff != null) {
                        if (diff == null) {
                            diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                        }
                        diff.addDifference(subDiff);
                    }
                } else {
                    // Deleted Constraint
                    FDifference subDiff = new FDifference(FDKind.DeletedElement, FDRating.Warning);
                    subDiff.setSource(leftConstr);
                    subDiff.setDescription("The constraint '" + leftConstr.getName() + "' for Hotspot '"
                                           + leftHs.getName() + "' was deleted in version "
                                           + this.getRightVersion());
                    if (diff == null) {
                        diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                    }
                    diff.addDifference(subDiff);
                }
            }
        }

        if (rightHs.getConstraints() != null) {
            for (Constraint rightConstr : rightHs.getConstraints()) {
                if (this.getFromList(leftHs.getConstraints(), rightConstr) == null) {
                    // Created Constraint
                    FDifference subDiff = new FDifference(FDKind.CreatedElement, FDRating.Warning);
                    subDiff.setSource(rightConstr);
                    subDiff.setDescription("The constraint '" + rightConstr.getName() + "' for Hotspot '"
                                           + leftHs.getName() + "' was added in version "
                                           + this.getRightVersion());
                    if (diff == null) {
                        diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                    }
                    diff.addDifference(subDiff);
                }
            }
        }

        if (diff != null) {
            diff.setSource(leftHs);
            diff.setDescription("The constraints of Hotspot '" + leftHs.getName()
                                + "' have changed in version " + this.getRightVersion());
        }
        return diff;
    }

    private FDifference compareConstraints(Constraint leftConstr, Constraint rightConstr) {
        FDifference diff = null;

        FDifference descDiff = basis.compareDescriptions(leftConstr, rightConstr, leftConstr);
        FDifference assertDiff = null;
        if (leftConstr.getAssertion() != null) {
            if (rightConstr.getAssertion() != null) {
                assertDiff = cdl.compareConstraintAssertions(leftConstr.getAssertion(), rightConstr
                        .getAssertion());
            } else {
                // Deleted Assertion
                assertDiff = new FDifference(FDKind.DeletedElement, FDRating.Warning);
                assertDiff.setSource(leftConstr.getAssertion());
                assertDiff.setDescription("The assertion '" + leftConstr.getAssertion().getName()
                                          + "' of constraint '" + leftConstr.getName()
                                          + "' was deleted in version " + this.getRightVersion());
            }
        } else if (rightConstr.getAssertion() != null) {
            // Created Assertion
            assertDiff = new FDifference(FDKind.DeletedElement, FDRating.Warning);
            assertDiff.setSource(rightConstr.getAssertion());
            assertDiff.setDescription("The assertion '" + rightConstr.getAssertion().getName()
                                      + "' of constraint '" + leftConstr.getName()
                                      + "' was added in version " + this.getRightVersion());
        }

        if (descDiff != null || assertDiff != null) {
            diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
            diff.addDifference(descDiff);
            diff.addDifference(assertDiff);
        }

        return diff;
    }

    private FDifference compareHsUnits(HotSpot leftHs, HotSpot rightHs) {
        FDifference diff = null;

        if (leftHs.getUnits() != null) {
            for (HotSpotUnit leftUnit : leftHs.getUnits()) {
                HotSpotUnit rightUnit = (HotSpotUnit) this.getFromList(rightHs.getUnits(), leftUnit);
                if (rightUnit != null) {
                    if (leftUnit instanceof CodingUnit) {
                        CodingUnit leftCodingUnit = (CodingUnit) leftUnit;
                        CodingUnit rightCodingUnit = (CodingUnit) rightUnit;
                        FDifference subDiff = this.compareCodingUnits(leftCodingUnit, rightCodingUnit);
                        if (subDiff != null) {
                            if (diff == null) {
                                diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                            }
                            diff.addDifference(subDiff);
                        }
                    } else {
                        log.warn("Unkown Hotspot unit kind " + leftUnit.getKind().name()
                                    + " for comparison.");
                    }
                } else {
                    // Deleted Unit
                    FDifference subDiff = new FDifference(FDKind.DeletedElement, FDRating.Conflict);
                    subDiff.setSource(leftUnit);
                    subDiff.setDescription("The Hotspot Unit '" + leftUnit.getName() + "' for Hotspot '"
                                           + leftHs.getName() + "' was removed in version "
                                           + this.getRightVersion());
                    if (diff == null) {
                        diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                    }
                    diff.addDifference(subDiff);
                }
            }
        }

        if (rightHs.getUnits() != null) {
            for (HotSpotUnit rightUnit : rightHs.getUnits()) {
                if (this.getFromList(leftHs.getUnits(), rightUnit) == null) {
                    // Created Unit
                    FDifference subDiff = new FDifference(FDKind.CreatedElement, FDRating.Warning);
                    subDiff.setSource(rightUnit);
                    subDiff.setDescription("The Hotspot Unit '" + rightUnit.getName() + "' for Hotspot '"
                                           + leftHs.getName() + "' was added in version "
                                           + this.getRightVersion());
                    if (diff == null) {
                        diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                    }
                    diff.addDifference(subDiff);
                }
            }
        }

        if (diff != null) {
            diff.setSource(leftHs);
            diff.setDescription("The list of Hotspot Units of Hotspot '" + leftHs.getName()
                                + "' has changed in version " + this.getRightVersion());
        }
        return diff;
    }

    private FDifference compareCodingUnits(CodingUnit leftCodingUnit,
                                           CodingUnit rightCodingUnit) {

        FDifference hooksDiff = compareHooks(leftCodingUnit, rightCodingUnit);
        FDifference constrDiff = compareCodingUnitConstraints(leftCodingUnit, rightCodingUnit);
        FDifference protDiff = compareCodingUnitProtocols(leftCodingUnit, rightCodingUnit);
        FDifference typeDiff = compareCodingUnitType(leftCodingUnit, rightCodingUnit);

        FDifference diff = null;
        if (hooksDiff != null || constrDiff != null || protDiff != null || typeDiff != null) {
            diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
            diff.setSource(leftCodingUnit);
            diff.setDescription("The Coding Unit '" + leftCodingUnit.getName() + "' has changed in version "
                                + this.getRightVersion());
            diff.addDifference(hooksDiff);
            diff.addDifference(constrDiff);
            diff.addDifference(protDiff);
            diff.addDifference(typeDiff);
        }
        return diff;
    }

    private FDifference compareHooks(CodingUnit leftCodingUnit, CodingUnit rightCodingUnit) {
        FDifference diff = null;

        if (leftCodingUnit.getHooks() != null) {
            for (HookCall leftHook : leftCodingUnit.getHooks()) {
                HookCall rightHook = (HookCall) this.getFromList(rightCodingUnit.getHooks(), leftHook);
                if (rightHook != null) {
                    FDifference subDiff = this.compareHookCalls(leftHook, rightHook);
                    if (subDiff != null) {
                    	if (diff == null) {
                    		diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                    	}
                    	diff.addDifference(subDiff);
                    }
                } else {
                    // Deleted HookCall
                    FDifference subDiff = new FDifference(FDKind.DeletedElement, FDRating.Conflict);
                    subDiff.setSource(leftHook);
                    subDiff.setDescription("The Hook '" + leftHook.getName() + "' of Coding Unit '"
                                           + leftCodingUnit.getName() + "' was removed in version "
                                           + this.getRightVersion());
                    if (diff == null) {
                        diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                    }
                    diff.addDifference(subDiff);
                }
            }
        }

        if (rightCodingUnit.getHooks() != null) {
            for (HookCall rightHook : rightCodingUnit.getHooks()) {
                if (this.getFromList(leftCodingUnit.getHooks(), rightHook) == null) {
                    // Created HookCall
                    FDifference subDiff = new FDifference(FDKind.CreatedElement, FDRating.Warning);
                    subDiff.setSource(rightHook);
                    subDiff.setDescription("The Hook '" + rightHook.getName() + "' of Coding Unit '"
                                           + leftCodingUnit.getName() + "' was added in version "
                                           + this.getRightVersion());
                    if (diff == null) {
                        diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                    }
                    diff.addDifference(subDiff);
                }
            }
        }

        if (diff != null) {
            diff.setSource(leftCodingUnit);
            diff.setDescription("The list of Hooks of Coding Unit '" + leftCodingUnit.getName()
                                + "' has changed in version " + this.getRightVersion());
        }
        return diff;
    }

    private FDifference compareHookCalls(HookCall leftHook, HookCall rightHook) {
        log.debug("comparing hook calls {} <> {}", leftHook, rightHook);
        
        FDifference signDiff = null;
        FDifference signDescrDiff = null;
        MethodDescription leftMethod = apiProxy.getMethodDescription(leftHook.getMethodAPIPath());
        MethodDescription rightMethod = apiProxy.getMethodDescription(rightHook.getMethodAPIPath());
        if (leftMethod != null && rightMethod != null) {
            signDiff = basis.compareValues("signature", leftMethod.getSignature().toString(),
                rightMethod.getSignature().toString(), leftMethod);
            
            signDescrDiff = basis.compareDescriptions("Hook " + leftMethod.getSignature(), leftMethod.getSignature().getDescription(), rightMethod.getSignature().getDescription(), leftMethod);
        }
        
        FDifference constrDiff = this.compareHookCallConstraints(leftHook, rightHook);

        FDifference diff = null;
        if (constrDiff != null || signDiff != null || signDescrDiff != null) {
            diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
            diff.setSource(leftHook);
            diff.setDescription("The Hook '" + leftHook.getName() + "' has changed in version "
                                + this.getRightVersion());
            diff.addDifference(constrDiff);
            diff.addDifference(signDiff);
            diff.addDifference(signDescrDiff);
        }

        return diff;
    }

    private FDifference compareHookCallConstraints(HookCall leftHook, HookCall rightHook) {
        FDifference diff = null;

        if (leftHook.getConstraints() != null) {
            for (Constraint leftConstr : leftHook.getConstraints()) {
                Constraint rightConstr = (Constraint) this
                        .getFromList(rightHook.getConstraints(), leftConstr);
                if (rightConstr != null) {
                    FDifference subDiff = this.compareConstraints(leftConstr, rightConstr);
                    if (subDiff != null) {
                        if (diff == null) {
                            diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                        }
                        diff.addDifference(subDiff);
                    }
                } else {
                    // Deleted Constraint
                    FDifference subDiff = new FDifference(FDKind.DeletedElement, FDRating.Warning);
                    subDiff.setSource(leftConstr);
                    subDiff.setDescription("The constraint '" + leftConstr.getName() + "' for Hook '"
                                           + leftHook.getName() + "' was deleted in version "
                                           + this.getRightVersion());
                    if (diff == null) {
                        diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                    }
                    diff.addDifference(subDiff);
                }
            }
        }

        if (rightHook.getConstraints() != null) {
            for (Constraint rightConstr : rightHook.getConstraints()) {
                if (this.getFromList(leftHook.getConstraints(), rightConstr) == null) {
                    // Created Constraint
                    FDifference subDiff = new FDifference(FDKind.CreatedElement, FDRating.Warning);
                    subDiff.setSource(rightConstr);
                    subDiff.setDescription("The constraint '" + rightConstr.getName() + "' for Hook '"
                                           + leftHook.getName() + "' was added in version "
                                           + this.getRightVersion());
                    if (diff == null) {
                        diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                    }
                    diff.addDifference(subDiff);
                }
            }
        }

        if (diff != null) {
            diff.setSource(leftHook);
            diff.setDescription("The constraints of Hook '" + leftHook.getName()
                                + "' have changed in version " + this.getRightVersion());
        }
        return diff;
    }

    private FDifference compareCodingUnitConstraints(CodingUnit leftCodingUnit,
                                                     CodingUnit rightCodingUnit) {
        FDifference diff = null;

        if (leftCodingUnit.getConstraints() != null) {
            for (Constraint leftConstr : leftCodingUnit.getConstraints()) {
                Constraint rightConstr = (Constraint) this.getFromList(rightCodingUnit.getConstraints(),
                    leftConstr);
                if (rightConstr != null) {
                    FDifference subDiff = this.compareConstraints(leftConstr, rightConstr);
                    if (subDiff != null) {
                        if (diff == null) {
                            diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                        }
                        diff.addDifference(subDiff);
                    }
                } else {
                    // Deleted Constraint
                    FDifference subDiff = new FDifference(FDKind.DeletedElement, FDRating.Warning);
                    subDiff.setSource(leftConstr);
                    subDiff.setDescription("The constraint '" + leftConstr.getName() + "' for Coding Unit '"
                                           + leftCodingUnit.getName() + "' was deleted in version "
                                           + this.getRightVersion());
                    if (diff == null) {
                        diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                    }
                    diff.addDifference(subDiff);
                }
            }
        }

        if (rightCodingUnit.getConstraints() != null) {
            for (Constraint rightConstr : rightCodingUnit.getConstraints()) {
                if (this.getFromList(leftCodingUnit.getConstraints(), rightConstr) == null) {
                    // Created Constraint
                    FDifference subDiff = new FDifference(FDKind.CreatedElement, FDRating.Warning);
                    subDiff.setSource(rightConstr);
                    subDiff.setDescription("The constraint '" + rightConstr.getName() + "' for Coding Unit '"
                                           + leftCodingUnit.getName() + "' was added in version "
                                           + this.getRightVersion());
                    if (diff == null) {
                        diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                    }
                    diff.addDifference(subDiff);
                }
            }
        }

        if (diff != null) {
            diff.setSource(leftCodingUnit);
            diff.setDescription("The constraints of Coding Unit '" + leftCodingUnit.getName()
                                + "' have changed in version " + this.getRightVersion());
        }
        return diff;
    }

    private FDifference compareCodingUnitProtocols(CodingUnit leftCodingUnit,
                                                   CodingUnit rightCodingUnit) {
        FDifference diff = null;

        if (leftCodingUnit.getProtocols() != null) {
            for (HookProtocol leftProtocol : leftCodingUnit.getProtocols()) {
                HookProtocol rightProtocol = (HookProtocol) this.getFromList(rightCodingUnit.getProtocols(),
                    leftProtocol);
                if (rightProtocol != null) {
                    FDifference subDiff = pdl.compareHookProtocols(leftProtocol, rightProtocol);
                    if (subDiff != null) {
                        if (diff == null) {
                            diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                        }
                        diff.addDifference(subDiff);
                    }
                } else {
                    // Deleted Protocol
                    FDifference subDiff = new FDifference(FDKind.DeletedElement, FDRating.Warning);
                    subDiff.setSource(leftProtocol);
                    subDiff.setDescription("The Hook Protocol '" + leftProtocol.getName()
                                           + "' for Coding Unit '" + leftCodingUnit.getName()
                                           + "' was deleted in version " + this.getRightVersion());
                    if (diff == null) {
                        diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                    }
                    diff.addDifference(subDiff);
                }
            }
        }

        if (rightCodingUnit.getProtocols() != null) {
            for (HookProtocol rightProtocol : rightCodingUnit.getProtocols()) {
                if (this.getFromList(leftCodingUnit.getProtocols(), rightProtocol) == null) {
                    // Created Protocol
                    FDifference subDiff = new FDifference(FDKind.CreatedElement, FDRating.Warning);
                    subDiff.setSource(rightProtocol);
                    subDiff.setDescription("The Hook Protocol '" + rightProtocol.getName()
                                           + "' for Coding Unit '" + leftCodingUnit.getName()
                                           + "' was added in version " + this.getRightVersion());
                    if (diff == null) {
                        diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                    }
                    diff.addDifference(subDiff);
                }
            }
        }

        if (diff != null) {
            diff.setSource(leftCodingUnit);
            diff.setDescription("The Hook protocols of Coding Unit '" + leftCodingUnit.getName()
                                + "' have changed in version " + this.getRightVersion());
        }
        return diff;
    }

    private FDifference compareCodingUnitType(CodingUnit leftCodingUnit,
                                              CodingUnit rightCodingUnit) {

        FDifference fileDiff = null;
        FDifference typeDiff = null;

        if (leftCodingUnit.getTypeAPIPath() != null) {
            if (rightCodingUnit.getTypeAPIPath() != null) {
                typeDiff = apidl.compareTypeDescriptions(leftCodingUnit, rightCodingUnit, leftCodingUnit);
            } else {
                // Deleted type description
                fileDiff = new FDifference(FDKind.DeletedElement, FDRating.Warning);
                fileDiff.setSource(leftCodingUnit);
                fileDiff.setDescription("The path '" + leftCodingUnit.getTypeAPIPath()
                                        + "' to the API type description of coding unit '"
                                        + leftCodingUnit.getName() + "' was removed in version "
                                        + this.getRightVersion());
            }
        } else if (rightCodingUnit.getTypeAPIPath() != null) {
            // Created type description
            fileDiff = new FDifference(FDKind.CreatedElement, FDRating.Warning);
            fileDiff.setSource(rightCodingUnit);
            fileDiff.setDescription("The path '" + rightCodingUnit.getTypeAPIPath()
                                    + "' to the API type description of coding unit '"
                                    + leftCodingUnit.getName() + "' was added in version "
                                    + this.getRightVersion());
        }

        FDifference diff = null;
        if (fileDiff != null || typeDiff != null) {
            diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
            diff.setSource(leftCodingUnit);
            diff.setDescription("The API type description of coding unit '" + leftCodingUnit.getName()
                                + "' has changed in version " + this.getRightVersion());

            diff.addDifference(fileDiff);
            diff.addDifference(typeDiff);
        }

        return diff;
    }

    public void setBasis(CompareBasis basis) {
        this.basis = basis;
    }

    public void setCdl(CompareCDL cdl) {
        this.cdl = cdl;
    }

    public void setPdl(ComparePDL pdl) {
        this.pdl = pdl;
    }

    public void setApidl(CompareAPIDL apidl) {
        this.apidl = apidl;
    }

    public void setApiProxy(APIDlProxy apiProxy) {
		this.apiProxy = apiProxy;
	}
}
