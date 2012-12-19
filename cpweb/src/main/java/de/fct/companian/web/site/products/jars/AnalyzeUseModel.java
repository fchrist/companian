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
package de.fct.companian.web.site.products.jars;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.analyze.db.dao.ClassesDao;
import de.fct.companian.analyze.db.dao.JarDao;
import de.fct.companian.analyze.db.dao.MethodDao;
import de.fct.companian.analyze.db.model.Clazz;
import de.fct.companian.analyze.db.model.Jar;
import de.fct.companian.analyze.db.model.Method;
import de.fct.companian.use.ClassUseConfidence;
import de.fct.companian.use.UseResult;
import de.fct.companian.web.site.products.jars.doc.DocModel;
import de.fct.fdmm.core.FrameworkDescription;
import de.fct.fdmm.core.HotSpotGroup;
import de.fct.fdmm.hotspot.CodingUnit;
import de.fct.fdmm.hotspot.HookCall;
import de.fct.fdmm.hotspot.HotSpot;
import de.fct.fdmm.hotspot.HotSpotUnit;

public class AnalyzeUseModel extends DocModel {

    private static Logger logger = LoggerFactory.getLogger(AnalyzeUseModel.class);

    private MethodDao methodDao;
    private ClassesDao classesDao;

    public AnalyzeUseModel(JarDao jarDao) {
        super(jarDao);
    }

    public List<UseResult> refresh(int productId, int usingJarId, int usedJarId) {
        List<UseResult> useResults = new ArrayList<UseResult>();

        Jar usingJar = this.jarDao.loadJar(usingJarId);
        if (usingJar != null) {
            this.context.put("usingJar", usingJar);
        } else {
            logger.warn("refresh() could not load using JAR with ID " + usingJarId);
        }

        FrameworkDescription fd = super.refresh(productId, usedJarId);

        useResults = analyzeFrameworkUse(usingJar, fd);
        this.context.put("useResults", useResults);

        this.context.put("title", "Framework Usage Analysis");
        return useResults;
    }

    public List<UseResult> analyzeFrameworkUse(Jar usingJar, FrameworkDescription fd) {
        logger.info("analyzeFrameworkUse() of {} by {}", fd.getName(), usingJar.getJarname());
        List<UseResult> useResults = new ArrayList<UseResult>();

        if (fd.getHotSpotGroups() != null) {
            for (HotSpotGroup hsg : fd.getHotSpotGroups()) {
                if (hsg.getHotSpots() != null) {
                    for (HotSpot hs : hsg.getHotSpots()) {
                        if (hs.getUnits() != null) {
                            for (HotSpotUnit hsu : hs.getUnits()) {
                                if (hsu instanceof CodingUnit) {
                                    CodingUnit codingUnit = (CodingUnit) hsu;

                                    List<ClassUseConfidence> confidenceList = this.analyzeUse(codingUnit,
                                        usingJar);
                                    Collections.sort(confidenceList, new Comparator<ClassUseConfidence>() {
                                        public int compare(ClassUseConfidence o1, ClassUseConfidence o2) {
                                            if (o1.getConfidence() < o2.getConfidence()) {
                                                return -1;
                                            }
                                            return 1;
                                        }
                                    });

                                    // Store result if confidence is > 0
                                    if (!confidenceList.isEmpty()) {
                                        useResults.add(new UseResult(hsg, hs, hsu, confidenceList));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!useResults.isEmpty()) {
            Collections.sort(useResults, new Comparator<UseResult>() {
                public int compare(UseResult o1, UseResult o2) {
                    return o1.getHotSpot().getName().compareTo(o2.getHotSpot().getName());
                }
            });

            // During the process there may be some false positives in the list. For example,
            // the same class that is used with different confidence values for different
            // Hotspots. We remove those false positives here.
            this.removeFalsePositives(useResults);

            // To complete the data, we load the classes for the UI
            this.loadClasses(useResults);

            // There may be subclasses that were not found during the process as they
            // do not implement any Hook themselves but inherit them from the super
            // class. We add such subclasses for completeness.
            this.handleSubclasses(useResults, usingJar.getJarId());
            
            logger.info("analyzeFrameworkUse() {} is used by {}", fd.getName(), usingJar.getJarname());
            if (logger.isInfoEnabled()) {
                for (UseResult r : useResults) {
                    logger.info("analyzeFrameworkUse() {} uses {} ", usingJar.getJarname(), r.getHotSpot()
                            .getName());
                }
            }
        }
        else {
            logger.info("analyzeFrameworkUse() {} does not use {}", usingJar.getJarname(), fd.getName());
        }

        return useResults;
    }

    /**
     * Computes a confidence value declaring that the using JAR implements the given Coding Unit.
     * 
     * @param codingUnit
     * @param usingJar
     * @return
     */
    private List<ClassUseConfidence> analyzeUse(CodingUnit codingUnit, Jar usingJar) {
        logger.debug("analyzeUse() check if {} uses coding unit {}", usingJar.getJarname(),
            codingUnit.getName());

        double maxConfidence = 0d;
        List<ClassUseConfidence> confidenceList = new ArrayList<ClassUseConfidence>();

        if (codingUnit.getHooks() != null) {

            Map<Integer,List<Method>> implementedByClass = new HashMap<Integer,List<Method>>();
            for (HookCall hook : codingUnit.getHooks()) {
                logger.debug("analyzeUse() check if hook {} is implemented by {}",
                    hook.getSignature(), usingJar.getJarname());
                // List of methods of the using JAR with the same signature as the Hook
                List<Method> methods = this.methodDao.listMethodsOfJar(hook.getSignature(),
                    usingJar.getJarId());

                if (methods != null && !methods.isEmpty()) {
                    logger.debug("analyzeUse() hook {} is implemented by {}", hook.getSignature(),
                        usingJar.getJarname());
                    // Save the classes which implement the Hook
                    for (Method m : methods) {
                        List<Method> implementedHooks = implementedByClass.get(m.getClassId());
                        if (implementedHooks == null) {
                            implementedHooks = new ArrayList<Method>();
                        }
                        implementedHooks.add(m);
                        implementedByClass.put(m.getClassId(), implementedHooks);
                    }
                }
                else {
                    logger.debug("analyzeUse() no implementation found for hook {}", hook.getSignature());
                }
            }

            // Maximum number of Hooks to be implemented
            int numberOfHooks = codingUnit.getHooks().size();

            // Compute the confidence value that the using JAR implements
            // the Coding Unit. If there is a single class implementing all
            // Hooks, the confidence value is 1.
            for (Integer classId : implementedByClass.keySet()) {
                List<Method> implementedHooks = implementedByClass.get(classId);
                double confidence = implementedHooks.size() / (double) numberOfHooks;
                if (confidence > 0) {
                    if (confidence > maxConfidence) {
                        maxConfidence = confidence;
                    }
                    confidenceList.add(new ClassUseConfidence(classId, confidence));
                }
            }
        } else {
            logger.debug("analyzeUse() coding unit '{}' does not define any Hooks", codingUnit.getName());
        }

        if (logger.isDebugEnabled()) {
            logger.debug("analyzeUse() JAR " + usingJar.getJarname() + " implements coding unit '"
                         + codingUnit.getName() + "' with max confidence " + maxConfidence);
        }
        return confidenceList;
    }

    private void removeFalsePositives(List<UseResult> useResults) {
        // Filter false positives
        Map<UseResult,List<ClassUseConfidence>> falsePositivesMap = new HashMap<UseResult,List<ClassUseConfidence>>();
        for (UseResult result : useResults) {
            for (ClassUseConfidence classConfidence : result.getConfidenceList()) {
                for (UseResult comparedResult : useResults) {
                    if (comparedResult == result) {
                        continue;
                    } else {
                        for (ClassUseConfidence comparedConfidence : comparedResult.getConfidenceList()) {
                            if (classConfidence.getClassId() == comparedConfidence.getClassId()) {
                                if (classConfidence.getConfidence() > comparedConfidence.getConfidence()) {
                                    // comparedConfidence is a FP
                                    addToFalsePositives(comparedResult, comparedConfidence, falsePositivesMap);
                                }
                            }
                        }
                    }
                }
            }
        }

        List<UseResult> obsoleteResults = new ArrayList<UseResult>();
        for (UseResult result : useResults) {
            List<ClassUseConfidence> falseList = falsePositivesMap.get(result);
            if (falseList != null) {
                for (ClassUseConfidence confidence : falseList) {
                    result.getConfidenceList().remove(confidence);
                }
            }
            if (result.getConfidenceList().isEmpty()) {
                obsoleteResults.add(result);
            }
        }
        for (UseResult obsoleteResult : obsoleteResults) {
            useResults.remove(obsoleteResult);
        }
    }

    private void addToFalsePositives(UseResult useResult,
                                     ClassUseConfidence fpConfidence,
                                     Map<UseResult,List<ClassUseConfidence>> falsePositivesMap) {

        List<ClassUseConfidence> falseList = falsePositivesMap.get(useResult);
        if (falseList == null) {
            falseList = new ArrayList<ClassUseConfidence>();
        }
        falseList.add(fpConfidence);
        falsePositivesMap.put(useResult, falseList);
    }

    private void loadClasses(List<UseResult> useResults) {
        for (UseResult result : useResults) {
            for (ClassUseConfidence confidence : result.getConfidenceList()) {
                if (confidence.getClazz() == null) {
                    confidence.setClazz(this.classesDao.loadClass(confidence.getClassId()));
                }
            }
        }
    }

    private void handleSubclasses(List<UseResult> useResults, int usingJarId) {
        List<UseResult> newResults = new ArrayList<UseResult>();
        for (UseResult result : useResults) {
            for (ClassUseConfidence confidence : result.getConfidenceList()) {
                // Load all classes that have this class as super class
                List<Clazz> inheritedClasses = this.classesDao.listClassesBySuperClass(confidence.getClazz()
                        .getFqcn(), usingJarId);
                if (inheritedClasses != null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("handleSubclasses() found " + inheritedClasses.size()
                                     + " classes with super class " + confidence.getClazz().getFqcn());
                    }
                    for (Clazz inheritedClass : inheritedClasses) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("handleSubclasses() adding " + inheritedClass.getFqcn()
                                         + " to new results");
                        }
                        // Add all inherited classes to the result list
                        ClassUseConfidence newConfidence = new ClassUseConfidence(
                                inheritedClass.getClassId(), confidence.getConfidence());
                        newConfidence.setClazz(inheritedClass);
                        UseResult newResult = new UseResult(result.getHotSpotGroup(), result.getHotSpot(),
                                result.getHotSpotUnit(), newConfidence);
                        newResults.add(newResult);
                    }
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("handleSubclasses() found " + newResults.size()
                         + " new results by inheritance - starting to consolidate");
        }

        // After computing the new results by inherited classes, we have
        // to consolidate the results
        for (UseResult newResult : newResults) {
            UseResult existingResult = null;
            for (UseResult result : useResults) {
                if (result.getHotSpot() == newResult.getHotSpot()
                    && result.getHotSpotUnit() == newResult.getHotSpotUnit()) {
                    existingResult = result;
                    break;
                }
            }
            existingResult.getConfidenceList().addAll(newResult.getConfidenceList());
        }
    }

    public void setMethodDao(MethodDao methodDao) {
        this.methodDao = methodDao;
    }

    public void setClassesDao(ClassesDao classesDao) {
        this.classesDao = classesDao;
    }

}
