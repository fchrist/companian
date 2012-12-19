package de.fct.companian.web.site.products.jars.doc.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.analyze.db.dao.JarDao;
import de.fct.companian.web.site.products.jars.doc.DocModel;
import de.fct.fdmm.core.FrameworkDescription;
import de.fct.fdmm.core.HotSpotGroup;

public class HsgModel extends DocModel {

    private Logger logger = LoggerFactory.getLogger(HsgModel.class);
    
    public HsgModel(JarDao jarDao) {
        super(jarDao);
    }

    public HotSpotGroup refresh(FrameworkDescription fd, String hsgName) {
        if (logger.isDebugEnabled()) {
            logger.debug("refresh() with HSG " + hsgName);
        }
        
        // search HSG and add to context
        HotSpotGroup hotSpotGroup = null;
        if (fd.getHotSpotGroups() != null
            && !fd.getHotSpotGroups().isEmpty()) {
            for (HotSpotGroup hsg : fd.getHotSpotGroups()) {
                if (hsg.getName().equals(hsgName)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("refresh() found hotspot group " + hsg.getName());
                    }
                    this.context.put("hsg", hsg);
                    hotSpotGroup = hsg;
                    break;
                }
            }
        }
        
        return hotSpotGroup;
    }
}
