package de.fct.companian.ccc;

import java.util.ArrayList;
import java.util.List;

import de.fct.companian.compare.FDCompareResult;
import de.fct.companian.use.UseResult;
import de.fct.fdmm.FDKind;
import de.fct.fdmm.FDRating;
import de.fct.fdmm.FDifference;
import de.fct.fdmm.architecturedl.behaviordl.HotSpotGroupCall;
import de.fct.fdmm.core.BehaviorView;
import de.fct.fdmm.hotspot.BindingCapability;
import de.fct.fdmm.hotspot.DeploymentCapability;
import de.fct.fdmm.hotspot.HotSpot;

public class CompatibilityResult {

    /*
     * This will hold the final relevant diffs with respect to the changes and used hot spots.
     */
    private final List<FDifference> diffs = new ArrayList<FDifference>();

    /*
     * Contains only the used hot spots which confidence value was greater than a given threshold.
     */
    private final List<FDifference> hotSpotDiffs = new ArrayList<FDifference>();

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
    
    public void addHotSpotDiff(List<FDifference> diffs) {
        if (diffs != null) {
            this.hotSpotDiffs.addAll(diffs);
        }
    }

    public List<FDifference> getHotSpotDiffs() {
        return this.hotSpotDiffs;
    }

    /**
     * Compute the diff tree that only contains relevant changes.
     * 
     * @param compareResult
     *            - The complete compare result that contains all changes found.
     */
    public void computeDiffTree(List<UseResult> useResults, FDCompareResult compareResult) {
        // First clear old results.
        this.diffs.clear();
        
        // The hotSpotDiffs list points to diff structures for all used hot spots. Single hot spot diffs
        // may appear several times in this list.
        for (FDifference hsDiff : this.hotSpotDiffs) {
            // We create a clone of the hot spot diff and compute the parent element
            // where this hot spot diff belongs to.
            FDifference clonedParentDiff = this.computeClonedParentPath(hsDiff.clone(true), hsDiff);

            // Having the cloned parent diff, we merge it in the overall diff structure. With this
            // step we are recreating the whole diff tree which contains only the relevant diffs
            // for used hot spots.
            this.mergeDiffs(this.diffs, clonedParentDiff);

            // We have a look at the binding and deployment information that may have changed for
            // the current hot spot. If there are relevant changes we add such diffs to the
            // diff tree.
            if (hsDiff.getSource() instanceof HotSpot) {
                this.addBindingDiff(hsDiff, compareResult);
                this.addDeploymentDiff(hsDiff, compareResult);
            }
        }

        // Check for relevant behavior view changes.
        List<FDifference> behaviorDiffs = compareResult.findDiffsOfType(BehaviorView.class);
        for (FDifference behaviorDiff : behaviorDiffs) {
            if (behaviorDiff.getKind() == FDKind.DeletedElement) {
                FDifference clonedBehaviorDiff = this.computeClonedParentPath(
                    behaviorDiff.clone(true), behaviorDiff);
                this.mergeDiffs(this.diffs, clonedBehaviorDiff);
            }
        }
        
        List<FDifference> hotSpotGroupCallDiffs = compareResult.findDiffsOfType(HotSpotGroupCall.class);
        for (FDifference hsgcDiff : hotSpotGroupCallDiffs) {
            HotSpotGroupCall hsgc = (HotSpotGroupCall) hsgcDiff.getSource();
            if (hsgc.getHotSpotGroups() != null && !hsgc.getHotSpotGroups().isEmpty()) {
                boolean diffAdded = false;
                for (String hsgName : hsgc.getHotSpotGroups()) {
                    for (UseResult useResult : useResults) {
                        if (hsgName.equals(useResult.getHotSpotGroup().getName())) {
                            FDifference clonedBehaviorDiff = this.computeClonedParentPath(
                                hsgcDiff.clone(true), hsgcDiff);
                            this.mergeDiffs(this.diffs, clonedBehaviorDiff);
                            diffAdded = true;
                            break;
                        }
                    }
                    if (diffAdded) break;
                }
            }
        }
        
        

        // TODO Include architecture view diffs

    }

    private FDifference computeClonedParentPath(FDifference clonedDiff, FDifference diff) {
        FDifference parentDiff = diff.getParentDiff();
        if (parentDiff != null) {
            FDifference parentClone = parentDiff.clone(false);
            parentClone.addDifference(clonedDiff);
            return this.computeClonedParentPath(parentClone, parentDiff);
        }

        return clonedDiff;
    }

    private void mergeDiffs(List<FDifference> diffs, FDifference mergedDiff) {
        int idx = diffs.indexOf(mergedDiff);
        if (idx == -1) {
            diffs.add(mergedDiff);
        } else {
            if (!mergedDiff.getSubDiffs().isEmpty()) {
                FDifference mergedSubPath = mergedDiff.getSubDiffs().get(0);
                FDifference subDiff = diffs.get(idx);
                this.mergeDiffs(subDiff.getSubDiffs(), mergedSubPath);
            }
        }
    }

    private void addBindingDiff(FDifference hsDiff, FDCompareResult compareResult) {
        HotSpot hotspot = (HotSpot) hsDiff.getSource();
        BindingCapability hotspotBinding = hotspot.getBinding();
        FDifference bindingDiff = compareResult.findDiff(hotspotBinding);
        if (bindingDiff != null) {
            hsDiff.addDifference(bindingDiff.clone(true));
        }
    }

    private void addDeploymentDiff(FDifference hsDiff, FDCompareResult compareResult) {
        HotSpot hotspot = (HotSpot) hsDiff.getSource();
        DeploymentCapability hotspotDeployment = hotspot.getDeployment();
        FDifference deploymentDiff = compareResult.findDiff(hotspotDeployment);
        if (deploymentDiff != null) {
            hsDiff.addDifference(deploymentDiff.clone(true));
        }
    }

    public String toHtml() {
        StringBuilder sb = new StringBuilder();
        for (FDifference diff : this.diffs) {
            sb.append(diff.toHtml()).append("\n");
        }

        return sb.toString();
    }
}
