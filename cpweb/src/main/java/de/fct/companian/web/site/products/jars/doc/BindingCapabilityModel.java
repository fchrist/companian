package de.fct.companian.web.site.products.jars.doc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.analyze.db.dao.JarDao;
import de.fct.fdmm.bindingdl.MetaEntry;
import de.fct.fdmm.core.FrameworkDescription;
import de.fct.fdmm.hotspot.BindingCapability;

public class BindingCapabilityModel extends DocModel {

    private Logger logger = LoggerFactory.getLogger(BindingCapabilityModel.class);
    
    public BindingCapabilityModel(JarDao jarDao) {
        super(jarDao);
    }

    public BindingCapability refresh(FrameworkDescription fd, String bindingName) {
        if (logger.isDebugEnabled()) {
            logger.debug("refresh() binding " + bindingName);
        }
        BindingCapability bindingCapability = null;
        
        if (fd.getBindingCapabilities() != null && !fd.getBindingCapabilities().isEmpty()) {
            for (BindingCapability bc : fd.getBindingCapabilities()) {
                if (bc.getName().equals(bindingName)) {
                    this.context.put("binding", bc);
                    bindingCapability = bc;
                    break;
                }
            }
        }
        
        return bindingCapability;
    }

}
