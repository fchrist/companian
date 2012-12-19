package de.fct.companian.web.site;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.analyze.db.dao.JarDao;
import de.fct.companian.analyze.db.model.Jar;
import de.fct.companian.ccc.CompatibilityResult;
import de.fct.companian.compare.FDCompareResult;
import de.fct.companian.use.UseResult;
import de.fct.companian.web.site.products.jars.AnalyzeUseModel;
import de.fct.companian.web.site.products.jars.CompareModel;
import de.fct.companian.web.site.products.jars.doc.DocModel;
import de.fct.fdmm.FDifference;
import de.fct.fdmm.basis.FDObject;
import de.fct.fdmm.core.FrameworkDescription;
import de.fct.fdmm.hotspot.HotSpot;

public class CCCModel extends DocModel {

    private static Logger logger = LoggerFactory.getLogger(AnalyzeUseModel.class);

    public CCCModel(JarDao jarDao) {
        super(jarDao);
    }

    private AnalyzeUseModel analyzeUseModel;
    private CompareModel compareModel;
    
    @Override
    public void init() {
        this.context.put("title", "Compatibility Check");
    }

    public void performCCC(int usingJarId, int usedJarId, int futureJarId) {
        Jar usingJar = this.jarDao.loadJar(usingJarId);
        this.context.put("usingJar", usingJar);
        
        Jar usedJar = this.jarDao.loadJar(usedJarId);
        this.context.put("usedJar", usedJar);
        FrameworkDescription usedFd = this.loadFrameworkDescription(usedJar);

        List<UseResult> useResults = this.analyzeUseModel.analyzeFrameworkUse(usingJar, usedFd);
        
        Jar futureJar = this.jarDao.loadJar(futureJarId);
        this.context.put("futureJar", futureJar);
        FrameworkDescription futureFd = this.loadFrameworkDescription(futureJar);

        logger.info("performCCC() checking {} when updating to {}", usingJar.getJarname(), futureJar.getJarname());
        
        FDCompareResult compareResult = this.compareModel.frameworkCompare(usedJar, usedFd, futureJar, futureFd);
        
        CompatibilityResult compatibilityResult = this.checkCompatibility(useResults, compareResult);
        this.context.put("diffCount", compatibilityResult.getDiffCount());
        this.context.put("relevantDiffCount", compatibilityResult.getRelevantDiffCount());
        this.context.put("diffsAsHtml", compatibilityResult.toHtml());
    }

    /**
     * The compatibility check reduces the compare results by the results
     * which are not in the use results.
     * 
     * @param useResults
     * @param compareResult
     * @return
     */
    private CompatibilityResult checkCompatibility(List<UseResult> useResults, FDCompareResult compareResult) {
        logger.info("checkCompatibility() based on use and compare results");
        CompatibilityResult cResult = new CompatibilityResult();
        
        double confidenceThreshold = 0.2d;
        logger.debug("checkCompatibility() confidence threshold is {}", confidenceThreshold);
        
        // Remove all used hot spot which confidence value is below
        // the threshold. We do this by adding only the found hot
        // spots with confidence value greater as the threshold
        // to the hot spot diff structure of the CompatibilityResult.
        for (UseResult useResult : useResults) {
            logger.info("checkCompatibility() checking used Hotspot {} with confidence {}", useResult.getHotSpot()
                    .getName(), useResult.getConfidenceList().get(0).getConfidence());
            // The first confidence value is the greatest. We check if it
            // is greater or equal our set confidence threshold.
            if (useResult.getConfidenceList().get(0).getConfidence() >= confidenceThreshold) {
                HotSpot usedHs = useResult.getHotSpot();
                
                List<FDifference> relevantDiffs = new ArrayList<FDifference>();
                this.findRelevantDiffs(usedHs, compareResult, relevantDiffs);
                if (!relevantDiffs.isEmpty()) {
                    cResult.addHotSpotDiff(relevantDiffs);
                }
                else {
                    logger.info("checkCompatibility() Hotspot {} has not changed", usedHs.getName());
                }
            }
        }
        
        cResult.computeDiffTree(useResults, compareResult);
        
        logger.info("checkCompatibility() found {} relevant diffs", cResult.getHotSpotDiffs().size());
        return cResult;
    }

    private void findRelevantDiffs(FDObject fdObject, FDCompareResult compareResult, List<FDifference> relevantDiffs) {
        for (FDifference subDiff : compareResult.getDiffs()) {
            if (subDiff.getSource() == fdObject) {
                relevantDiffs.add(subDiff);
            }
            else {
                findDifference(fdObject, subDiff, relevantDiffs);
            }
        }
    }

    private void findDifference(FDObject fdObject, FDifference diff, List<FDifference> relevantDiffs) {
        if (diff.getSubDiffs() != null) {
            for (FDifference subDiff : diff.getSubDiffs()) {
                if (subDiff.getSource() == fdObject) {
                    relevantDiffs.add(subDiff);
                }
                else {
                    this.findDifference(fdObject, subDiff, relevantDiffs);
                }
            }
        }
    }
    
    
    public void setAnalyzeUseModel(AnalyzeUseModel analyzeUseModel) {
        this.analyzeUseModel = analyzeUseModel;
    }

    public void setCompareModel(CompareModel compareModel) {
        this.compareModel = compareModel;
    }
    
}
