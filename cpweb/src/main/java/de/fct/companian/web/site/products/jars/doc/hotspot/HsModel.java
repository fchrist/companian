package de.fct.companian.web.site.products.jars.doc.hotspot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.analyze.db.dao.JarDao;
import de.fct.companian.web.site.products.jars.doc.core.HsgModel;
import de.fct.fdmm.core.FrameworkDescription;
import de.fct.fdmm.core.HotSpotGroup;
import de.fct.fdmm.hotspot.HotSpot;

public class HsModel extends HsgModel {

    private Logger logger = LoggerFactory.getLogger(HsModel.class);
    
    public HsModel(JarDao jarDao) {
        super(jarDao);
    }

    public HotSpot refresh(FrameworkDescription fd, String hsgName, String hsName) {
        if (logger.isDebugEnabled()) {
            logger.debug("refresh() with Hotspot " + hsName);
        }
        HotSpotGroup hsg = super.refresh(fd, hsgName);
        HotSpot hotSpot = null;
        for (HotSpot hs : hsg.getHotSpots()) {
            if (hs.getName().equals(hsName)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("refresh() found hotspot " + hs.getName());
                }
                this.context.put("hs", hs);
                hotSpot = hs;
                break;
            }
        }
        
        return hotSpot;
    }

}
