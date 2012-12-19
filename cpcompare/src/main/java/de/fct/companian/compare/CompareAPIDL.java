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

import de.fct.fdmm.FDKind;
import de.fct.fdmm.FDRating;
import de.fct.fdmm.FDifference;
import de.fct.fdmm.apidl.APIDlProxy;
import de.fct.fdmm.apidl.MethodDescription;
import de.fct.fdmm.apidl.TypeDescription;
import de.fct.fdmm.basis.FDObject;
import de.fct.fdmm.hotspot.CodingUnit;

public class CompareAPIDL extends AbstractCompare {

    private APIDlProxy proxy;

    private CompareBasis basis;

    public CompareAPIDL(Map<String,Object> context) {
        super(context);
    }

    public FDifference compareTypeDescriptions(CodingUnit leftCodingUnit,
                                               CodingUnit rightCodingUnit,
                                               FDObject source) {

    	FDifference diff = null;
        TypeDescription leftType = proxy.getTypeDescription(leftCodingUnit.getTypeAPIPath());
        TypeDescription rightType = proxy.getTypeDescription(rightCodingUnit.getTypeAPIPath());

        if (leftType != null && rightType != null) {
            FDifference nameDiff = basis.compareNamedElements("type descriptions", leftType, rightType, source);
            FDifference descDiff = basis.compareDescriptions(leftType, rightType, source);
            FDifference fqtnDiff = basis.compareValues("FQTN", leftType.getFqtn(), rightType.getFqtn(), source);
            FDifference methDiff = this.compareMethodDescriptions(leftType, rightType, source);
            FDifference packDiff = this.comparePackageDescriptions(leftType, rightType, source);

            if (nameDiff != null || descDiff != null || fqtnDiff != null || methDiff != null || packDiff != null) {
                diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                diff.setSource(leftCodingUnit);
                diff.setDescription("The type description of '" + leftCodingUnit.getName()
                                    + "' has changed in version " + this.getRightVersion());

                diff.addDifference(nameDiff);
                diff.addDifference(descDiff);
                diff.addDifference(fqtnDiff);
                diff.addDifference(methDiff);
                diff.addDifference(packDiff);
            }
        }
        
        return diff;
    }

    private FDifference compareMethodDescriptions(TypeDescription leftType,
                                                  TypeDescription rightType,
                                                  FDObject source) {
        FDifference diff = null;

        if (leftType.getMethods() != null) {
            if (rightType.getMethods() != null) {
                FDifference subDiff = compareMethodLists(leftType.getMethods(), rightType.getMethods(), leftType);
                if (subDiff != null) {
                    if (diff == null) {
                        diff = new FDifference(FDKind.ChangedElement, subDiff.getRating());
                    }
                    diff.addDifference(subDiff);
                }
            } else {
                // Deleted methods
                FDifference delDiff = new FDifference(FDKind.DeletedElement, FDRating.Conflict);
                delDiff.setSource(leftType);
                delDiff.setDescription(leftType.getMethods().size() + " were removed from type '"
                                       + leftType.getFqtn() + "' in version " + this.getRightVersion());
                if (diff == null) {
                    diff = new FDifference(FDKind.ChangedElement, FDRating.Conflict);
                }
                diff.addDifference(delDiff);
            }
        } else if (rightType.getMethods() != null) {
            // New methods
            FDifference addDiff = new FDifference(FDKind.CreatedElement, FDRating.Warning);
            addDiff.setSource(rightType);
            addDiff.setDescription("The type '" + rightType.getFqtn() + "' has "
                                   + rightType.getMethods().size() + " new methods in version "
                                   + this.getRightVersion());
            if (diff == null) {
                diff = new FDifference(FDKind.ChangedElement, FDRating.Conflict);
            }
            diff.addDifference(addDiff);
        }

        if (diff != null) {
            diff.setSource(leftType);
            diff.setDescription("The type description of '" + leftType.getFqtn()
                                + "' has changed in version " + this.getRightVersion());
        }
        return diff;
    }

    private FDifference compareMethodLists(List<MethodDescription> leftMethods,
                                       List<MethodDescription> rightMethods,
                                       FDObject source) {
        FDifference diff = null;

        for (MethodDescription leftMethod : leftMethods) {
            MethodDescription rightMethod = (MethodDescription) basis.getFromList(rightMethods, leftMethod);
    		if (rightMethod != null) {
    			diff = compareMethods(diff, leftMethod, rightMethod);
    		} else {
    		    // Deleted method
    		    FDifference delDiff = new FDifference(FDKind.DeletedElement, FDRating.Conflict);
    		    delDiff.setSource(leftMethod);
    		    delDiff.setDescription("The method with signature '" + leftMethod.getSignature().toString()
    		                           + "' was removed in version " + this.getRightVersion());
    		    if (diff == null) {
    		        diff = new FDifference(FDKind.ChangedElement, FDRating.Conflict);
    		    }
    		    diff.addDifference(delDiff);
    		}
            
        }

        for (MethodDescription rightMethod : rightMethods) {
            MethodDescription leftMethod = (MethodDescription) basis.getFromList(leftMethods, rightMethod);
            if (leftMethod == null) {
                // New method
                FDifference addDiff = new FDifference(FDKind.CreatedElement, FDRating.Warning);
                addDiff.setSource(rightMethod);
                addDiff.setDescription("The method with signature '" + rightMethod.getSignature().toString()
                                       + "' was added in version " + this.getRightVersion());
                if (diff == null) {
                    diff = new FDifference(FDKind.ChangedElement, FDRating.Conflict);
                }
                diff.addDifference(addDiff);
            }
        }

        if (diff != null) {
            diff.setSource(source);
            diff.setDescription("Method signatures have changed in version " + this.getRightVersion());
        }
        return diff;
    }

	public FDifference compareMethods(FDifference diff, MethodDescription leftMethod, MethodDescription rightMethod) {
		FDifference signDiff = basis.compareValues("signature", leftMethod.getSignature().toString(),
																rightMethod.getSignature().toString(), leftMethod);
        FDifference descrDiff = basis.compareDescriptions(leftMethod.getName(), leftMethod.getSignature()
                .getDescription(), rightMethod.getSignature().getDescription(), leftMethod);
		
		if (signDiff != null || descrDiff != null) {
		    if (diff == null && signDiff != null) {
				diff = new FDifference(FDKind.ChangedElement, FDRating.Conflict);
			}
		    else if (diff == null) {
                diff = new FDifference(FDKind.ChangedElement, FDRating.Info);
            }

			diff.addDifference(signDiff);
			diff.addDifference(descrDiff);
		}

		return diff;
	}

    private FDifference comparePackageDescriptions(TypeDescription leftType,
                                                   TypeDescription rightType,
                                                   FDObject source) {

        FDifference nameDiff = null;
        FDifference descDiff = null;
        FDifference deleDiff = null;
        FDifference creaDiff = null;

        if (leftType.getPackage() != null) {
            if (rightType.getPackage() != null) {
                nameDiff = basis.compareNamedElements("package description", leftType.getPackage(), rightType
                        .getPackage(), leftType.getPackage());
                descDiff = basis.compareDescriptions(leftType.getPackage(), rightType.getPackage(), leftType.getPackage());
            } else {
                // Deleted package
                deleDiff = new FDifference(FDKind.DeletedElement, FDRating.Conflict);
                deleDiff.setSource(leftType.getPackage());
                deleDiff.setDescription("The package '" + leftType.getPackage().getName() + "' of type '"
                                        + leftType.getFqtn() + "' was removed in version "
                                        + this.getRightVersion());
            }
        } else if (rightType.getPackage() != null) {
            // Created package
            creaDiff = new FDifference(FDKind.CreatedElement, FDRating.Conflict);
            creaDiff.setSource(rightType.getPackage());
            creaDiff.setDescription("The package '" + rightType.getPackage().getName()
                                    + "' was added to the type '" + leftType.getFqtn() + "' in version "
                                    + this.getRightVersion());
        }

        FDifference diff = null;
        if (nameDiff != null || descDiff != null || deleDiff != null || creaDiff != null) {
            diff = new FDifference(FDKind.ChangedElement, FDRating.Conflict);
            diff.setSource(leftType.getPackage());
            diff.setDescription("The package description has changed in version " + this.getRightVersion());
            diff.addDifference(nameDiff);
            diff.addDifference(descDiff);
            diff.addDifference(deleDiff);
            diff.addDifference(creaDiff);
        }
        return diff;
    }

    public void setProxy(APIDlProxy proxy) {
        this.proxy = proxy;
    }

    public void setBasis(CompareBasis basis) {
        this.basis = basis;
    }

}
