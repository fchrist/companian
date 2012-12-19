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

import java.util.ArrayList;
import java.util.List;

import de.fct.fdmm.FDKind;
import de.fct.fdmm.FDRating;
import de.fct.fdmm.FDifference;
import de.fct.fdmm.basis.FDObject;

public class FDCompareResult {

    private final List<FDifference> diffs = new ArrayList<FDifference>();
    
    public void addDifference(FDifference difference) {
        if (difference != null) {
            this.diffs.add(difference);
        }
    }

    public List<FDifference> getDiffs() {
        return this.diffs;
    }
    
    public int getDiffCount() {
        int diffCount = 0;
        for (FDifference diff : this.diffs) {
            diffCount += this.countDiffsNotOfRating(diff, null);
        }
        return diffCount;
    }

    public int getRelevantDiffCount() {
        int diffCount = 0;
        for (FDifference diff : this.diffs) {
            diffCount += this.countDiffsNotOfRating(diff, FDRating.Info);
        }
        return diffCount;
    }

    private int countDiffsNotOfRating(FDifference rootDiff, FDRating rating) {
        int diffCount = 0;
        
        for (FDifference diff : rootDiff.getSubDiffs()) {
            if (diff.getSubDiffs().isEmpty()) {
                if (rating == null || rating != diff.getRating()) {
                    diffCount++;
                }
            }
            diffCount += countDiffsNotOfRating(diff, rating);
        }
        
        return diffCount;
    }
    
    public List<FDifference> findDiffsOfType(Class<?> diffType) {
        List<FDifference> foundDiffs = new ArrayList<FDifference>();
        for (FDifference diff : this.diffs) {
            getDiffsOfType(diffType, foundDiffs, diff);
        }
        
        return foundDiffs;
    }

    private void getDiffsOfType(Class<?> diffType, List<FDifference> clonedDiffs, FDifference diff) {
        if (diffType.isInstance(diff.getSource())) {
            clonedDiffs.add(diff);
            return;
        }
        for (FDifference subDiff : diff.getSubDiffs()) {
            getDiffsOfType(diffType, clonedDiffs, subDiff);
        }
    }

    public FDifference findDiff(FDObject source) {
        for (FDifference diff : diffs) {
            if (diff.getSource() == source) {
                return diff;
            }
        }
        for (FDifference diff : diffs) {
            return findSubDiff(source, diff);
        }
        return null;
    }

    private FDifference findSubDiff(FDObject source, FDifference diff) {
        for (FDifference subDiff : diff.getSubDiffs()) {
            if (subDiff.getSource() == source) {
                return diff;
            }
        }
        for (FDifference subDiff : diff.getSubDiffs()) {
            return findSubDiff(source, subDiff);
        }
        return null;
    }
}
