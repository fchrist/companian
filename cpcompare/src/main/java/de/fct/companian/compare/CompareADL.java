package de.fct.companian.compare;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.fdmm.FDKind;
import de.fct.fdmm.FDRating;
import de.fct.fdmm.FDifference;
import de.fct.fdmm.architecturedl.ArchitectureDescription;
import de.fct.fdmm.architecturedl.ArchitectureElement;

public class CompareADL extends AbstractCompare {

    private static Logger logger = LoggerFactory.getLogger(CompareADL.class);
    
    public CompareADL(Map<String,Object> context) {
        super(context);
    }

    protected FDifference compareArchitectureDescriptions(ArchitectureDescription leftAd,
                                                          ArchitectureDescription rightAd) {

        FDifference archDiff = null;
        if (leftAd != null && rightAd != null) {
            if (leftAd.getSubElements() != null) {
                for (ArchitectureElement leftAe : leftAd.getSubElements()) {
                    ArchitectureElement rightAe = (ArchitectureElement) this.getFromSet(rightAd
                            .getSubElements(), leftAe);
                    if (rightAe != null) {
                        // Changed element?
                        FDifference subDiff = this.compareArchitectureElements(leftAe, rightAe);
                        if (subDiff != null) {
                            if (archDiff == null) {
                                archDiff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                            }
                            archDiff.addDifference(subDiff);
                        }
                    } else {
                        // Deleted architecture element
                        FDifference subDiff = new FDifference(FDKind.DeletedElement, FDRating.Warning);
                        subDiff.setSource(leftAd);
                        subDiff.setDescription("The architecture element '" + leftAe.getName()
                                               + "' was deleted in version " + this.getRightVersion());
                        if (archDiff == null) {
                            archDiff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                        }
                        archDiff.addDifference(subDiff);
                    }
                }
            }

            if (rightAd.getSubElements() != null) {
                for (ArchitectureElement rightAe : rightAd.getSubElements()) {
                    if (this.getFromSet(leftAd.getSubElements(), rightAe) == null) {
                        // Created element
                        FDifference subDiff = new FDifference(FDKind.CreatedElement, FDRating.Warning);
                        subDiff.setSource(rightAe);
                        subDiff.setDescription("The architecture element '" + rightAe.getName()
                                               + "' was created in version " + this.getRightVersion());
                        if (archDiff == null) {
                            archDiff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                        }
                        archDiff.addDifference(subDiff);
                    }
                }
            }
        } else {
            logger.error("Could not read architecture descriptions - no compare possible!");
        }

        if (archDiff != null) {
            archDiff.setSource(leftAd);
            archDiff.setDescription("The architecture description has changed in version "
                                    + this.getRightVersion());
        }
        return archDiff;
    }

    protected FDifference compareArchitectureElements(ArchitectureElement leftAe,
                                                      ArchitectureElement rightAe) {

        FDifference depDiff = compareAeDependencies(leftAe, rightAe);
        FDifference hsgDiff = compareAeHotSpotGroups(leftAe, rightAe);
        FDifference subArchDiff = compareArchitectureDescriptions(leftAe.getSubArchitecture(), rightAe
                .getSubArchitecture());

        FDifference aeDiff = null;
        if (depDiff != null || hsgDiff != null || subArchDiff != null) {
            aeDiff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
            aeDiff.setSource(leftAe);
            aeDiff.setDescription("The architecture element '" + leftAe.getName()
                                  + "' has changed in version " + this.getRightVersion());
        }

        return aeDiff;
    }

    protected FDifference compareAeDependencies(ArchitectureElement leftAe,
                                                ArchitectureElement rightAe) {
        FDifference depDiff = null;

        if (leftAe.getDependencies() != null) {
            for (ArchitectureElement leftAeDep : leftAe.getDependencies()) {
                if (this.getFromSet(rightAe.getDependencies(), leftAeDep) == null) {
                    // Deleted dependency
                    FDifference subDiff = new FDifference(FDKind.DeletedElement, FDRating.Warning);
                    subDiff.setSource(leftAeDep);
                    subDiff.setDescription("The dependency to '" + leftAeDep.getName() + "' from '"
                                           + leftAe.getName() + "' was deleted in version "
                                           + this.getRightVersion());
                    if (depDiff == null) {
                        depDiff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                    }
                    depDiff.addDifference(subDiff);
                }
            }
        }

        if (rightAe.getDependencies() != null) {
            for (ArchitectureElement rightAeDep : rightAe.getDependencies()) {
                if (this.getFromSet(leftAe.getDependencies(), rightAeDep) == null) {
                    // New dependency
                    FDifference subDiff = new FDifference(FDKind.CreatedElement, FDRating.Warning);
                    subDiff.setSource(rightAeDep);
                    subDiff.setDescription("The dependency to '" + rightAeDep.getName() + "' from '"
                                           + leftAe.getName() + "' was added in version "
                                           + this.getRightVersion());
                    if (depDiff == null) {
                        depDiff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                    }
                    depDiff.addDifference(subDiff);
                }
            }
        }

        if (depDiff != null) {
            depDiff.setSource(leftAe);
            depDiff.setDescription("The dependencies of '" + leftAe.getName() + "' have changed in version "
                                   + this.getRightVersion());
        }
        return depDiff;
    }

    protected FDifference compareAeHotSpotGroups(ArchitectureElement leftAe,
                                                 ArchitectureElement rightAe) {
        FDifference hsgDiff = null;

        if (leftAe.getHotSpotGroups() != null) {
            for (String leftHsg : leftAe.getHotSpotGroups()) {
                if (this.getFromSet(rightAe.getHotSpotGroups(), leftHsg) == null) {
                    // Deleted HSG
                    FDifference subDiff = new FDifference(FDKind.DeletedElement, FDRating.Warning);
                    subDiff.setSource(leftAe);
                    subDiff.setDescription("The Hotspot group '" + leftHsg
                                           + "' is not anymore associated with '" + leftAe.getName()
                                           + "' in version " + this.getRightVersion());
                    if (hsgDiff == null) {
                        hsgDiff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                    }
                    hsgDiff.addDifference(subDiff);
                }
            }
        }

        if (rightAe.getHotSpotGroups() != null) {
            for (String rightHsg : rightAe.getHotSpotGroups()) {
                if (this.getFromSet(leftAe.getHotSpotGroups(), rightHsg) == null) {
                    // Created HSG
                    FDifference subDiff = new FDifference(FDKind.CreatedElement, FDRating.Warning);
                    subDiff.setSource(leftAe);
                    subDiff.setDescription("The Hotspot group '" + rightHsg + "' is now associated with '"
                                           + leftAe.getName() + "' in version " + this.getRightVersion());
                    if (hsgDiff == null) {
                        hsgDiff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
                    }
                    hsgDiff.addDifference(subDiff);
                }
            }
        }

        if (hsgDiff != null) {
            hsgDiff.setSource(leftAe);
            hsgDiff.setDescription("The associated Hotspot groups of '" + leftAe.getName()
                                   + "' have changed in version " + this.getRightVersion());
        }

        return hsgDiff;
    }
}
