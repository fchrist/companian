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
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.fdmm.FDKind;
import de.fct.fdmm.FDRating;
import de.fct.fdmm.FDifference;
import de.fct.fdmm.basis.FDObject;
import de.fct.fdmm.basis.IDescription;
import de.fct.fdmm.basis.INamedElement;

public class CompareBasis extends AbstractCompare {

    private static Logger log = LoggerFactory.getLogger(CompareBasis.class);

    public CompareBasis(Map<String,Object> context) {
        super(context);
    }

    protected FDifference compareValues(String context, String left, String right, FDObject source) {

        FDifference diff = null;

        if (left != null & right != null) {
            if (!left.equals(right)) {
                log.debug("different values: left={} <> right={}", left, right);
                diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                diff.setSource(source);
                diff.setDescription("The " + context + " has changed from '" + left + "' in version "
                                    + this.getLeftVersion() + " to '" + right + "' in version "
                                    + this.getRightVersion());
            }
        }

        return diff;
    }

    protected FDifference compareLists(String context,
                                       List<String> leftList,
                                       List<String> rightList,
                                       FDObject source) {

        FDifference diff = null;

        if (leftList != null) {
            for (String left : leftList) {
                boolean existsInRightList = false;
                if (rightList != null) {
                    existsInRightList = rightList.contains(left);
                }
                if (!existsInRightList) {
                    FDifference subDiff = new FDifference(FDKind.DeletedElement, FDRating.Conflict);
                    subDiff.setSource(source);
                    subDiff.setDescription("In the list of " + context + " the element '" + left
                                           + "' at position " + (leftList.indexOf(left) + 1)
                                           + " was removed in version " + this.getRightVersion());
                    if (diff == null) {
                        diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                    }
                    diff.addDifference(subDiff);
                }
            }
        }

        if (rightList != null) {
            for (String right : rightList) {
                boolean existsInLeftList = false;
                if (leftList != null) {
                    existsInLeftList = leftList.contains(right);
                }
                if (!existsInLeftList) {
                    FDifference subDiff = new FDifference(FDKind.CreatedElement, FDRating.Conflict);
                    subDiff.setSource(source);
                    subDiff.setDescription("In the list of " + context + " the element '" + right
                                           + "' was added at position " + (rightList.indexOf(right) + 1)
                                           + " in version " + this.getRightVersion());
                    if (diff == null) {
                        diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                    }
                    diff.addDifference(subDiff);
                }
            }
        }

        if (diff != null) {
            diff.setSource(source);
            diff.setDescription("The list of " + context + " has changed in version "
                                + this.getRightVersion());
        }
        return diff;
    }

    protected FDifference compareSets(String context,
                                      Set<String> leftSet,
                                      Set<String> rightSet,
                                      FDObject source) {

        FDifference diff = null;

        if (leftSet != null) {
            for (String left : leftSet) {
                boolean existsInRightSet = false;
                if (rightSet != null) {
                    existsInRightSet = rightSet.contains(left);
                }
                if (!existsInRightSet) {
                    FDifference subDiff = new FDifference(FDKind.DeletedElement, FDRating.Conflict);
                    subDiff.setSource(source);
                    subDiff.setDescription("In the set of " + context + " the element '" + left
                                           + "' was removed in version " + this.getRightVersion());
                    if (diff == null) {
                        diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                    }
                    diff.addDifference(subDiff);
                }
            }
        }

        if (rightSet != null) {
            for (String right : rightSet) {
                boolean existsInLeftSet = false;
                if (leftSet != null) {
                    existsInLeftSet = leftSet.contains(right);
                }
                if (!existsInLeftSet) {
                    FDifference subDiff = new FDifference(FDKind.CreatedElement, FDRating.Conflict);
                    subDiff.setSource(source);
                    subDiff.setDescription("In the set of " + context + " the element '" + right
                                           + "' was added in version " + this.getRightVersion());
                    if (diff == null) {
                        diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                    }
                    diff.addDifference(subDiff);
                }
            }
        }

        if (diff != null) {
            diff.setSource(source);
            diff.setDescription("The set of " + context + " has changed in version " + this.getRightVersion());
        }
        return diff;
    }

    protected FDifference compareNamedElements(String context,
                                               INamedElement left,
                                               INamedElement right,
                                               FDObject source) {

        FDifference diff = null;

        if (left != null && right != null) {
            if (left.getName() != null & right.getName() != null) {
                if (!left.getName().equals(right.getName())) {
                    log.debug("different named elements: left={} <> right={}", left, right);
                    diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                    diff.setSource(source);
                    diff.setDescription("The " + context + " has changed from '" + left.getName()
                                        + "' in version " + this.getLeftVersion() + " to '" + right.getName()
                                        + "' in version " + this.getRightVersion());
                    log.debug("compare namend elements diff:\n{}", diff);
                }
            }
        } else {
            log.warn("comparing named elements with NULL arguments. left={}, right={}", left, right);
        }

        return diff;
    }

    protected FDifference compareDescriptions(IDescription left, IDescription right, FDObject source) {
        if (left != null && right != null) {
            String context = null;
            if (left instanceof INamedElement) {
                INamedElement leftNamed = (INamedElement) left;
                context = leftNamed.getName();
            }
            return compareDescriptions(context, left.getDescription(), right.getDescription(), source);
        } else {
            log.warn("comparing descriptions with NULL arguments. left={}, right={}", left, right);
            return null;
        }
    }

    protected FDifference compareDescriptions(String context,
                                              String leftDescr,
                                              String rightDescr,
                                              FDObject source) {
        FDifference diff = null;

        if (leftDescr != null && rightDescr != null) {

            leftDescr = cleanFromHtml(leftDescr);
            rightDescr = cleanFromHtml(rightDescr);

            if (leftDescr != null) {
                if (rightDescr != null) {
                    if (!weakequals(leftDescr, rightDescr)) {
                        log.debug("different descriptions: left={} <> right={}", leftDescr, rightDescr);

                        diff = new FDifference(FDKind.ChangedElement, FDRating.Info);
                        diff.setSource(source);

                        StringBuilder sbDescr = new StringBuilder();
                        sbDescr.append("The description ");
                        if (context != null) {
                            sbDescr.append("of '").append(context);
                        }
                        sbDescr.append("' has changed in version ").append(this.getRightVersion())
                                .append(".<br>");
                        sbDescr.append("<table><thead><tr>");
                        sbDescr.append("<th width='50%'>Version ").append(this.getLeftVersion())
                                .append("</th>");
                        sbDescr.append("<th width='50%'>Version ").append(this.getRightVersion())
                                .append("</th>");
                        sbDescr.append("</thead><tbody><tr>");
                        sbDescr.append("<td valign='top'>").append(leftDescr).append("</td>");
                        sbDescr.append("<td valign='top'>").append(rightDescr).append("</td>");
                        sbDescr.append("</tr></tbody></table>");
                        diff.setDescription(sbDescr.toString());
                    }
                } else {
                    diff = new FDifference(FDKind.DeletedElement, FDRating.Warning);
                    diff.setSource(source);
                    diff.setDescription("The description text was removed in version "
                                        + this.getRightVersion());
                }
            } else {
                diff = new FDifference(FDKind.CreatedElement, FDRating.Warning);
                diff.setSource(source);
                diff.setDescription("The description text was newly created in version "
                                    + this.getRightVersion());
            }
        }

        return diff;
    }

    private boolean weakequals(String left, String right) {
        boolean we = false;

        StringTokenizer leftTokens = new StringTokenizer(left, " ");
        StringTokenizer rightTokens = new StringTokenizer(right, " ");

        int wordDiff = 0;

        if (leftTokens.countTokens() != rightTokens.countTokens()) {
            int skipCount = 0;

            while (leftTokens.hasMoreTokens()) {
                String leftToken = leftTokens.nextToken();
                boolean foundRight = false;
                rightTokens = new StringTokenizer(right, " ");
                for (int s = 0; s < skipCount && rightTokens.hasMoreTokens(); s++) {
                    rightTokens.nextToken();
                }
                if (rightTokens.hasMoreTokens()) {
                    String rightToken = rightTokens.nextToken();
                    if (leftToken.equals(rightToken)) {
                        foundRight = true;
                    } else {
                        while (rightTokens.hasMoreTokens()) {
                            String skipToken = rightTokens.nextToken();
                            if (!leftToken.equals(skipToken)) {
                                wordDiff++;
                                skipCount++;
                            }
                        }
                    }
                } else {
                    break;
                }
                if (foundRight) {
                    skipCount++;
                } else {
                    wordDiff++;
                }
            }

            if (wordDiff == 0) {
                wordDiff = rightTokens.countTokens();
            }
        }

        if (wordDiff < 3) {
            we = true;
        }

        log.debug("word diff is {}", wordDiff);

        return we;
    }
}
