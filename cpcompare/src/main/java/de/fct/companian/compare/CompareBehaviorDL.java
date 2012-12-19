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
import de.fct.fdmm.architecturedl.behaviordl.EndStep;
import de.fct.fdmm.architecturedl.behaviordl.HotSpotGroupCall;
import de.fct.fdmm.architecturedl.behaviordl.Process;
import de.fct.fdmm.architecturedl.behaviordl.StartStep;
import de.fct.fdmm.architecturedl.behaviordl.Step;
import de.fct.fdmm.architecturedl.behaviordl.SubProcess;
import de.fct.fdmm.basis.FDObject;
import de.fct.fdmm.basis.INamedElement;

public class CompareBehaviorDL extends AbstractCompare {

    private static Logger logger = LoggerFactory.getLogger(CompareBehaviorDL.class);

    private CompareBasis basis;

    public CompareBehaviorDL(Map<String,Object> context) {
        super(context);
    }

    public FDifference compareProcesses(Process leftProcess, Process rightProcess) {
        if (logger.isDebugEnabled()) {
            logger.debug("compareProcesses() " + leftProcess.getName() + " with " + rightProcess.getName());
        }
        FDifference processDiff = null;
        if (leftProcess != null && rightProcess != null) {
            if (leftProcess.getSteps() != null && rightProcess.getSteps() != null) {
                // Find start step on the left
                StartStep leftStartStep = null;
                for (Step step : leftProcess.getSteps()) {
                    if (step instanceof StartStep) {
                        leftStartStep = (StartStep) step;
                        break;
                    }
                }

                // Find start step on the right
                StartStep rightStartStep = null;
                for (Step step : rightProcess.getSteps()) {
                    if (step instanceof StartStep) {
                        rightStartStep = (StartStep) step;
                        break;
                    }
                }

                if (leftStartStep != null && rightStartStep != null) {
                    FDifference nameDiff = this.compareStepIds("start step name", leftStartStep,
                        rightStartStep, leftProcess);

                    processDiff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                    processDiff.addDifference(nameDiff);
                    processDiff.setDescription("The process '" + leftProcess.getName()
                                               + "' describing the behavior has changed in version "
                                               + this.getRightVersion());
                    // start a simple bi-simulation
                    this.bisimulate(leftStartStep.getNextStep(), rightStartStep.getNextStep(), processDiff);

                    if (processDiff.getSubDiffs().isEmpty()) {
                        // There were no underlying diffs found so this one is obsolete
                        processDiff = null;
                    }

                } else if (leftStartStep != null) {
                    // right start step not found
                    processDiff = new FDifference(FDKind.DeletedElement, FDRating.Warning);
                    processDiff.setDescription("The process's start step was deleted in version "
                                               + this.getRightVersion());
                } else if (rightStartStep != null) {
                    // left start step not found
                    processDiff = new FDifference(FDKind.CreatedElement, FDRating.Warning);
                    processDiff.setDescription("The process's start step was added in version "
                                               + this.getRightVersion());
                }
            } else {
                processDiff = new FDifference(FDKind.DeletedElement, FDRating.Conflict);
                processDiff.setSource(leftProcess);
                processDiff.setDescription("Either the left or the right process has no steps in it.");
            }

        } else {
            logger.error("Could not read process files - no compare possible!");
        }

        if (processDiff != null) {
            processDiff.setSource(leftProcess);

        }
        return processDiff;
    }

    private void bisimulate(Step leftStep, Step rightStep, FDifference parentDiff) {
        if (logger.isDebugEnabled()) {
            logger.debug("bisimulate() " + leftStep.getName() + " <-> " + rightStep.getName());
        }

        FDifference stepDiff = null;

        Step nextLeftStep = null;
        Step nextRightStep = null;

        if (leftStep != null && rightStep != null) {

            // Handle end steps
            if (leftStep instanceof EndStep) {
                if (!(rightStep instanceof EndStep)) {
                    // left process ends but right process proceeds
                    stepDiff = new FDifference(FDKind.CreatedElement, FDRating.Conflict);
                    stepDiff.setSource(leftStep);
                    stepDiff.setDescription("The left process ends with "
                                            + leftStep.getPrevStep().getType()
                                            + " '"
                                            + leftStep.getPrevStep().getName()
                                            + "' but there are unmatched process steps in the right process left - starting from "
                                            + rightStep.getType() + " '" + rightStep.getName() + "'.");
                }
            } else if (rightStep instanceof EndStep) {
                // the right process ends but the left process proceeds
                stepDiff = new FDifference(FDKind.DeletedElement, FDRating.Conflict);
                stepDiff.setSource(leftStep);
                stepDiff.setDescription("The right process ends with "
                                        + rightStep.getPrevStep().getType()
                                        + " '"
                                        + rightStep.getPrevStep().getName()
                                        + "' but there are unmatched process steps in the left process left - starting from "
                                        + leftStep.getType() + " '" + leftStep.getName() + "'.");
            } else if (match(leftStep, rightStep)) {
                // matching steps

                if (leftStep instanceof SubProcess) {
                    // the right step is implicitly of type SubProcess because we had matching steps
                    SubProcess leftSubProcess = (SubProcess) leftStep;
                    SubProcess rightSubProcess = (SubProcess) rightStep;

                    stepDiff = this.compareProcesses(leftSubProcess.getSubProcess(),
                        rightSubProcess.getSubProcess());
                } else if (leftStep instanceof HotSpotGroupCall) {
                    HotSpotGroupCall leftHsgc = (HotSpotGroupCall) leftStep;
                    HotSpotGroupCall rightHsgc = (HotSpotGroupCall) rightStep;
                    stepDiff = basis.compareSets("hot spot group calls", leftHsgc.getHotSpotGroups(),
                        rightHsgc.getHotSpotGroups(), leftHsgc);
                    if (stepDiff != null) {
                        stepDiff.setDescription("The hot spot group calls of step '" + leftHsgc.getName()
                                                + "' were changed in version " + this.getRightVersion());
                    }
                }

                // We had matching steps - go one step further on both sides
                nextLeftStep = leftStep.getNextStep();
                nextRightStep = rightStep.getNextStep();
            } else {
                // No match - see if there was something added or deleted on the right
                if (findMatch(leftStep, rightStep.getNextStep())) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("bisimulate() left step '" + leftStep.getName()
                                     + "' found later on the right. So the right step " + rightStep.getName()
                                     + " was added.");
                    }
                    // There are new elements on the right side until the left step comes
                    stepDiff = new FDifference(FDKind.CreatedElement, FDRating.Conflict);
                    stepDiff.setSource(rightStep);
                    stepDiff.setDescription("The " + rightStep.getType() + " '" + rightStep.getName()
                                            + "' was added in version " + this.getRightVersion());

                    // Go one step further on right
                    nextLeftStep = leftStep;
                    nextRightStep = rightStep.getNextStep();
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("bisimulate() left step '" + leftStep.getName()
                                     + "' not found on right. So this step was removed.");
                    }
                    // The left step was removed because there is no match on the right side
                    stepDiff = new FDifference(FDKind.DeletedElement, FDRating.Conflict);
                    stepDiff.setSource(leftStep);
                    stepDiff.setDescription("The " + leftStep.getType() + " '" + leftStep.getName()
                                            + "' was removed in version " + this.getRightVersion());

                    // Go one step further on left
                    nextLeftStep = leftStep.getNextStep();
                    nextRightStep = rightStep;
                }
            }

        } else if (leftStep != null) {
            // No right step
            stepDiff = new FDifference(FDKind.DeletedElement, FDRating.Conflict);
            stepDiff.setSource(leftStep);
            stepDiff.setDescription("The right process ends in the middle of the process but the left process proceeds with "
                                    + leftStep.getType() + " " + leftStep.getName());
        } else if (rightStep != null) {
            // No left step
            stepDiff = new FDifference(FDKind.CreatedElement, FDRating.Conflict);
            stepDiff.setSource(leftStep);
            stepDiff.setDescription("The left process ends in the middle of the process but the right process proceeds with "
                                    + rightStep.getType() + " " + rightStep.getName());
        }

        parentDiff.addDifference(stepDiff);

        // Compare the next steps
        if (nextLeftStep != null && nextRightStep != null) {
            bisimulate(nextLeftStep, nextRightStep, parentDiff);
        }

    }

    private boolean match(Step leftStep, Step rightStep) {
        if (logger.isDebugEnabled()) {
            logger.debug("match() for " + leftStep.getName() + " and " + rightStep.getName());
        }
        boolean nameMatch = false;
        if (rightStep != null) {
            boolean typeMatch = leftStep.getType().equals(rightStep.getType());

            if (typeMatch) {
                logger.debug("match() both steps are of type: " + leftStep.getType());
                if (leftStep.getName().equals(rightStep.getName())) {
                    nameMatch = true;
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("match() returns " + nameMatch);
        }
        return nameMatch;
    }

    private boolean findMatch(Step leftStep, Step rightStep) {
        if (logger.isDebugEnabled()) {
            logger.debug("findMatch() for " + leftStep.getName() + " and " + rightStep.getName());
        }
        boolean foundMatch = match(leftStep, rightStep);
        if (!foundMatch && rightStep.getNextStep() != null) {
            // No match - go on with search
            foundMatch = this.findMatch(leftStep, rightStep.getNextStep());
        }

        if (logger.isDebugEnabled()) {
            logger.debug("findMatch() returns " + foundMatch);
        }
        return foundMatch;
    }

    protected FDifference compareStepIds(String context,
                                         INamedElement left,
                                         INamedElement right,
                                         FDObject source) {

        FDifference diff = null;

        if (left != null && right != null) {
            if (left.getName() != null & right.getName() != null) {
                if (left.getName().startsWith("id") && right.getName().startsWith("id")) {
                    logger.debug("ignoring diff between generated IDs for steps {} and {}", left.getName(), right.getName());
                }
                else if (!left.getName().equals(right.getName())) {
                    logger.info("different step IDs: left={} <> right={}", left, right);
                    diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                    diff.setSource(source);
                    diff.setDescription("The " + context + " has changed from '" + left.getName()
                                        + "' in version " + this.getLeftVersion() + " to '" + right.getName()
                                        + "' in version " + this.getRightVersion());
                    logger.debug("compare step IDs diff:\n{}", diff);
                }
            }
        } else {
            logger.warn("comparing named elements with NULL arguments. left={}, right={}", left, right);
        }

        return diff;
    }
    
    public void setBasis(CompareBasis basis) {
        this.basis = basis;
    }

}
