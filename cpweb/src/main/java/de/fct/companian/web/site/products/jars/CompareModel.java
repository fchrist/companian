package de.fct.companian.web.site.products.jars;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.analyze.db.dao.JarDao;
import de.fct.companian.analyze.db.model.Jar;
import de.fct.companian.analyze.db.model.JarCompareResult;
import de.fct.companian.compare.FDCompareResult;
import de.fct.companian.compare.FDMCompare;
import de.fct.companian.web.site.products.jars.doc.DocModel;
import de.fct.fdmm.apidl.APIDlProxy;
import de.fct.fdmm.architecturedl.ArchitectureDlProxy;
import de.fct.fdmm.architecturedl.behaviordl.BehaviorDlProxy;
import de.fct.fdmm.core.FrameworkDescription;
import de.fct.fdmm.protocoldl.ProtocolDlProxy;

public class CompareModel extends DocModel {

    private static Logger log = LoggerFactory.getLogger(CompareModel.class);

    private ArchitectureDlProxy sviewProxy;
    private BehaviorDlProxy bviewProxy;
    private ProtocolDlProxy protocolProxy;
    private APIDlProxy apiProxy;

    public CompareModel(JarDao jarDao) {
        super(jarDao);
    }

    @Override
    public void init() {

    }

    public void refresh(int productId, int leftJarId, int rightJarId) {
        this.context.put("title", "API Compare");
        Jar jar = this.jarDao.loadJar(leftJarId);
        if (jar != null) {
            this.context.put("leftJar", jar);
            this.context.put("product", jar.getProduct());
        } else {
            log.warn("refresh() could not load JAR with ID " + leftJarId);
        }

        jar = this.jarDao.loadJar(rightJarId);
        if (jar != null) {
            this.context.put("rightJar", jar);
        } else {
            log.warn("refresh() could not load JAR with ID " + leftJarId);
        }

        List<JarCompareResult> results = this.jarDao.compare(leftJarId, rightJarId);

        List<JarCompareResult> addedRight = new ArrayList<JarCompareResult>();
        List<JarCompareResult> deletedRight = new ArrayList<JarCompareResult>();

        for (JarCompareResult cr : results) {
            if (cr.getSide().equals("right")) {
                addedRight.add(cr);
            } else {
                deletedRight.add(cr);
            }
        }
        this.context.put("addedRight", addedRight);
        this.context.put("deletedRight", deletedRight);
        this.context.put("diffCount", results.size());
    }

    public FrameworkDescription refresh(int productId, int jarId, String contextKey) {
        FrameworkDescription fd = super.refresh(productId, jarId);
        this.context.put("title", "Framework Compare");

        Object jar = this.context.remove("jar");
        this.context.put(contextKey, jar);

        this.context.remove("fd");
        this.context.put(contextKey + "FD", fd);

        return fd;
    }

    public FDCompareResult frameworkCompare(Jar leftJar, FrameworkDescription leftFD, Jar rightJar, FrameworkDescription rightFD) {
        log.info("frameworkCompare() started");
    	Map<String,Object> compareContext = new HashMap<String,Object>();
        compareContext.put("leftJar", leftJar);
        compareContext.put("rightJar", rightJar);
        
        FDMCompare fdmc = new FDMCompare(compareContext);
        fdmc.setSviewProxy(sviewProxy);
        fdmc.setBviewProxy(bviewProxy);
        fdmc.setProtocolProxy(protocolProxy);
        fdmc.setApiProxy(apiProxy);
        fdmc.init();
        
        FDCompareResult result = fdmc.compare(leftFD, rightFD);
        
        if (!result.getDiffs().isEmpty()) {
            log.info("frameworkCompare() the compared frameworks differ");
        }
        
        return result;

    }

    public void setSviewProxy(ArchitectureDlProxy sviewProxy) {
        this.sviewProxy = sviewProxy;
    }

    public void setBviewProxy(BehaviorDlProxy bviewProxy) {
        this.bviewProxy = bviewProxy;
    }

    public void setProtocolProxy(ProtocolDlProxy protocolProxy) {
        this.protocolProxy = protocolProxy;
    }

    public void setApiProxy(APIDlProxy apiProxy) {
        this.apiProxy = apiProxy;
    }

}
