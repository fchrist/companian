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
import de.fct.fdmm.basis.FDObject;
import de.fct.fdmm.bindingdl.APICall;
import de.fct.fdmm.bindingdl.BindingDescription;
import de.fct.fdmm.bindingdl.Descriptor;
import de.fct.fdmm.bindingdl.MetaEntry;
import de.fct.fdmm.bindingdl.Task;

public class CompareBDL extends AbstractCompare {

	private static Logger log = LoggerFactory.getLogger(CompareBDL.class);
	
    private CompareBasis basis;

	public CompareBDL(Map<String,Object> context) {
        super(context);
    }

    public FDifference compareBindingDescriptions(BindingDescription leftBinding,
                                                  BindingDescription rightBinding) {

        FDifference apicDiff = this.compareAPICalls(leftBinding, rightBinding);
        FDifference metaDiff = this.compareMetaEntries(leftBinding, rightBinding);

        FDifference diff = null;
        if (apicDiff != null || metaDiff != null) {
            diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
            diff.setDescription("The Binding Description has changed in version " + this.getRightVersion());
            diff.setSource(leftBinding);
            diff.addDifference(apicDiff);
            diff.addDifference(metaDiff);
        }
        return diff;
    }

    private FDifference compareAPICalls(BindingDescription leftBinding,
                                        BindingDescription rightBinding) {

        FDifference diff = null;

        if (leftBinding.getApiCalls() != null) {
            if (rightBinding.getApiCalls() != null) {
                for (APICall leftCall : leftBinding.getApiCalls()) {
                    APICall rightCall = (APICall) basis.getFromList(rightBinding.getApiCalls(), leftCall);

                    if (rightCall != null) {
                        FDifference callDiff = this.compareCalls(leftCall, rightCall);
                        if (callDiff != null) {
                            if (diff == null) {
                                diff = new FDifference(FDKind.ChangedElement, FDRating.Conflict);
                            }
                            diff.addDifference(callDiff);
                        }

                    } else {
                        // Deleted Call
                        FDifference deleDiff = new FDifference(FDKind.DeletedElement, FDRating.Conflict);
                        deleDiff.setSource(leftCall);
                        deleDiff.setDescription("The Binding Capability API Call '" + leftCall.getName()
                                                + "' was removed in version " + this.getRightVersion());
                        if (diff == null) {
                            diff = new FDifference(FDKind.ChangedElement, FDRating.Conflict);
                        }
                        diff.addDifference(deleDiff);
                    }
                }

                for (APICall rightCall : rightBinding.getApiCalls()) {
                    if (this.getFromList(leftBinding.getApiCalls(), rightCall) == null) {
                        // Created Call
                        FDifference createDiff = new FDifference(FDKind.CreatedElement, FDRating.Conflict);
                        createDiff.setSource(rightCall);
                        createDiff.setDescription("The Binding Capability API Call '" + rightCall.getName()
                                                  + "' was added in version " + this.getRightVersion());
                        if (diff == null) {
                            diff = new FDifference(FDKind.ChangedElement, FDRating.Conflict);
                        }
                        diff.addDifference(createDiff);
                    }
                }
            } else {
                // Deleted Calls
                FDifference deleDiff = new FDifference(FDKind.DeletedElement, FDRating.Conflict);
                deleDiff.setSource(leftBinding);
                deleDiff.setDescription("All Binding Capability API Calls were removed in version "
                                        + this.getRightVersion());
                if (diff == null) {
                    diff = new FDifference(FDKind.ChangedElement, FDRating.Conflict);
                }
                diff.addDifference(deleDiff);
            }
        } else if (rightBinding.getApiCalls() != null) {
            // Created Calls
            FDifference createDiff = new FDifference(FDKind.CreatedElement, FDRating.Conflict);
            createDiff.setSource(leftBinding);
            createDiff.setDescription("The Binding Capability API Calls were added in version "
                                      + this.getRightVersion());
            if (diff == null) {
                diff = new FDifference(FDKind.ChangedElement, FDRating.Conflict);
            }
            diff.addDifference(createDiff);
        }

        if (diff != null) {
            diff.setSource(leftBinding);
            diff.setDescription("The API Calls of the Binding Capability have changed in version "
                                + this.getRightVersion());
        }
        return diff;
    }

    private FDifference compareCalls(APICall leftCall, APICall rightCall) {
        FDifference descDiff = basis.compareDescriptions(leftCall, rightCall, leftCall);

        FDifference fileDiff = null;
        if (leftCall.getMethodAPIPath() != null) {
            if (rightCall.getMethodAPIPath() != null) {
                if (!leftCall.getMethodAPIPath().equals(rightCall.getMethodAPIPath())) {
                    fileDiff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                    fileDiff.setSource(leftCall);
                    fileDiff.setDescription("The used API path to the API call '" + leftCall.getName()
                                            + "' has changed from '" + leftCall.getMethodAPIPath() + "' to '"
                                            + rightCall.getMethodAPIPath() + "' in version "
                                            + this.getRightVersion());
                }

                // TODO Compare the API Call method -> Missing proxy
            } else {
                // Deleted API Call path
                fileDiff = new FDifference(FDKind.DeletedElement, FDRating.Warning);
                fileDiff.setSource(leftCall);
                fileDiff.setDescription("The path '" + leftCall.getMethodAPIPath() + "' to the API call of '"
                                        + leftCall.getName() + "' was removed in version "
                                        + this.getRightVersion());
            }
        } else if (rightCall.getMethodAPIPath() != null) {
            // Created API Call path
            fileDiff = new FDifference(FDKind.CreatedElement, FDRating.Warning);
            fileDiff.setSource(rightCall);
            fileDiff
                    .setDescription("The path '" + rightCall.getMethodAPIPath() + "' to the API call of '"
                                    + leftCall.getName() + "' was added in version " + this.getRightVersion());
        }

        FDifference diff = null;
        if (descDiff != null || fileDiff != null) {
            diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
            diff.setSource(leftCall);
            diff.setDescription("The API Call of '" + leftCall.getName() + "' has changed in version "
                                + this.getRightVersion());

            diff.addDifference(descDiff);
            diff.addDifference(fileDiff);
        }
        return diff;
    }

    private FDifference compareMetaEntries(BindingDescription leftBinding,
                                           BindingDescription rightBinding) {

        FDifference diff = null;
        if (leftBinding.getMetaEntries() != null) {
            if (rightBinding.getMetaEntries() != null) {
                diff = compareMetaEntriesDeep(leftBinding, rightBinding);
            } else {
                // Deleted entries
                diff = new FDifference(FDKind.DeletedElement, FDRating.Conflict);
                diff.setSource(leftBinding);
                diff.setDescription("All Meta Entries were removed in version " + this.getRightVersion());
            }
        } else if (rightBinding.getMetaEntries() != null) {
            // Created entries
            diff = new FDifference(FDKind.CreatedElement, FDRating.Conflict);
            diff.setSource(leftBinding);
            diff.setDescription("All Meta Entries were added in version " + this.getRightVersion());
        }

        return diff;
    }

    private FDifference compareMetaEntriesDeep(BindingDescription leftBinding,
                                               BindingDescription rightBinding) {

        FDifference diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);

        for (MetaEntry leftEntry : leftBinding.getMetaEntries()) {
            MetaEntry rightEntry = (MetaEntry) this.getFromList(rightBinding.getMetaEntries(), leftEntry);
            if (rightEntry != null) {
                diff.addDifference(compareSingleMetaEntries(leftEntry, rightEntry));
            } else {
                // Deleted entry
                FDifference delDiff = new FDifference(FDKind.DeletedElement, FDRating.Conflict);
                delDiff.setSource(leftEntry);
                delDiff.setDescription("The Meta Entry '" + leftEntry.getName() + "' was removed in version "
                                       + this.getRightVersion());
                diff.addDifference(delDiff);
            }
        }

        for (MetaEntry rightEntry : rightBinding.getMetaEntries()) {
            if (this.getFromList(leftBinding.getMetaEntries(), rightEntry) == null) {
                // Created entry
                FDifference creaDiff = new FDifference(FDKind.CreatedElement, FDRating.Conflict);
                creaDiff.setSource(rightEntry);
                creaDiff.setDescription("The Meta Entry '" + rightEntry.getName() + "' was added in version "
                                        + this.getRightVersion());
                diff.addDifference(creaDiff);
            }
        }

        if (diff.getSubDiffs().isEmpty()) {
            diff = null;
        } else {
            diff.setSource(leftBinding);
            diff.setDescription("The Meta Entries have changed in version " + this.getRightVersion());
        }
        return diff;
    }

    private FDifference compareSingleMetaEntries(MetaEntry leftEntry, MetaEntry rightEntry) {

        FDifference diff = null;
        if (leftEntry.getTasks() != null) {
            if (rightEntry.getTasks() != null) {
                diff = compareTasks(leftEntry, rightEntry);
            } else {
                // Deleted tasks
                diff = new FDifference(FDKind.DeletedElement, FDRating.Conflict);
                diff.setSource(leftEntry);
                diff.setDescription("All tasks of Meta Entry '" + leftEntry.getName()
                                    + "' were removed in version " + this.getRightVersion());
            }
        } else if (rightEntry.getTasks() != null) {
            // Created tasks
            diff = new FDifference(FDKind.CreatedElement, FDRating.Conflict);
            diff.setSource(leftEntry);
            diff.setDescription("The Meta Entry '" + leftEntry.getName()
                                + "' got completely new tasks in version " + this.getRightVersion());
        }

        return diff;
    }

    private FDifference compareTasks(MetaEntry leftEntry, MetaEntry rightEntry) {

        FDifference diff = new FDifference(FDKind.ChangedElement, FDRating.Conflict);

        for (Task leftTask : leftEntry.getTasks()) {
            Task rightTask = (Task) this.getFromList(rightEntry.getTasks(), leftTask);
            if (rightTask != null) {
                diff.addDifference(compareSingleTasks(leftTask, rightTask));
            } else {
                // Deleted task
                FDifference subDiff = new FDifference(FDKind.DeletedElement, FDRating.Conflict);
                subDiff.setSource(leftTask);
                subDiff.setDescription("The Task '" + leftTask.getName() + "' of Meta Entry '"
                                       + leftEntry.getName() + "' was removed in version "
                                       + this.getRightVersion());
                diff.addDifference(subDiff);
            }
        }

        for (Task rightTask : rightEntry.getTasks()) {
            if (this.getFromList(leftEntry.getTasks(), rightTask) == null) {
                // Created task
                FDifference subDiff = new FDifference(FDKind.DeletedElement, FDRating.Conflict);
                subDiff.setSource(rightTask);
                subDiff.setDescription("The Task '" + rightTask.getName() + "' of Meta Entry '"
                                       + leftEntry.getName() + "' was added in version "
                                       + this.getRightVersion());
                diff.addDifference(subDiff);
            }
        }

        if (diff.getSubDiffs().isEmpty()) {
            diff = null;
        } else {
            diff.setSource(leftEntry);
            diff.setDescription("The Tasks of Meta Entry '" + leftEntry.getName()
                                + "' have changed in version " + this.getRightVersion());
        }

        return diff;
    }

    private FDifference compareSingleTasks(Task leftTask, Task rightTask) {
    	log.debug("comparing single tasks {} <> {}", leftTask, rightTask);
    	
        FDifference diff = new FDifference(FDKind.ChangedElement, FDRating.Conflict);
        
        diff.addDifference(basis.compareValues("selector", leftTask.getSelector(), rightTask.getSelector(),
            leftTask));
        diff.addDifference(basis.compareValues("content", leftTask.getContent(), rightTask.getContent(), leftTask));
        diff.addDifference(this.compareDescriptors(leftTask.getDescriptor(), rightTask.getDescriptor(), leftTask));
        
        if (diff.getSubDiffs().isEmpty()) {
            diff = null;
        }
        else {
            diff.setSource(leftTask);
            diff.setDescription("The Task '" + leftTask.getName() + "' has changed in version " + this.getRightVersion());
        }
        return diff;
    }

    private FDifference compareDescriptors(Descriptor leftDescriptor, Descriptor rightDescriptor, FDObject source) {
        
        FDifference diff = new FDifference(FDKind.ChangedElement, FDRating.Conflict);
        
        diff.addDifference(basis.compareNamedElements("descriptor", leftDescriptor, rightDescriptor, source));
        diff.addDifference(basis.compareValues("descriptor schema URN", leftDescriptor.getSchemaUrn(), rightDescriptor.getSchemaUrn(), source));
        
        if (diff.getSubDiffs().isEmpty()) {
            diff = null;
        } else {
            diff.setSource(source);
            diff.setDescription("The Descriptor '" + leftDescriptor.getName() + "' has changed in version " + this.getRightVersion());
        }
        
        return diff;
    }

    public void setBasis(CompareBasis basis) {
		this.basis = basis;
	}
}
