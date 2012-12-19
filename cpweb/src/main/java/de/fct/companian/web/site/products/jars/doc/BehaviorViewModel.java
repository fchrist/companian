package de.fct.companian.web.site.products.jars.doc;

import org.codehaus.jettison.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.analyze.db.dao.JarDao;
import de.fct.companian.web.BehaviorDlSerializer;
import de.fct.fdmm.architecturedl.behaviordl.BehaviorDlProxy;
import de.fct.fdmm.architecturedl.behaviordl.Process;
import de.fct.fdmm.core.BehaviorView;
import de.fct.fdmm.core.FrameworkDescription;

public class BehaviorViewModel extends DocModel {

    private Logger logger = LoggerFactory.getLogger(BehaviorViewModel.class);

    private BehaviorDlProxy proxy;
    
    public BehaviorViewModel(JarDao jarDao) {
        super(jarDao);
    }

    @Override
    public void init() {}

    public BehaviorView refresh(FrameworkDescription fd, String behaviorViewName) {
        BehaviorView behaviorView = null;
        if (fd != null && behaviorViewName != null) {
            if (fd.getBehaviorViews() != null && !fd.getBehaviorViews().isEmpty()) {
                for (BehaviorView bv : fd.getBehaviorViews()) {
                    if (bv.getName().equals(behaviorViewName)) {
                        this.context.put("behaviorView", bv);
                        behaviorView = bv;
                        break;
                    }
                }
            }
        }
        
        return behaviorView;
    }

    public Process refresh(BehaviorView bv) {
        Process process = null;
        if (bv.getProcessFile() != null) {
            process = bv.getProcess(this.proxy);
            if (process != null) {
                try {
                    this.context.put("architectureAsJSON", BehaviorDlSerializer.toJSON(process).toString(2));
                } catch (JSONException e) {
                    logger.error("refresh() error while serializing architecture to JSON", e);
                }
            }
        }
        
        return process;
    }

    public void setProxy(BehaviorDlProxy proxy) {
        this.proxy = proxy;
    }
}
