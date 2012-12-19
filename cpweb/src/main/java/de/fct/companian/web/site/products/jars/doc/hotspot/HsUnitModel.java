package de.fct.companian.web.site.products.jars.doc.hotspot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.analyze.db.dao.JarDao;
import de.fct.fdmm.core.FrameworkDescription;
import de.fct.fdmm.hotspot.HotSpot;
import de.fct.fdmm.hotspot.HotSpotUnit;

public class HsUnitModel extends HsModel {

    private Logger logger = LoggerFactory.getLogger(HsUnitModel.class);

    public HsUnitModel(JarDao jarDao) {
        super(jarDao);
    }

    public HotSpotUnit refresh(FrameworkDescription fd, String hsgName, String hsName, String unitName) {
        if (logger.isDebugEnabled()) {
            logger.debug("refresh() with hotspot unit " + unitName);
        }
        HotSpot hs = super.refresh(fd, hsgName, hsName);
        HotSpotUnit hotSpotUnit = null;
        for (HotSpotUnit hsu : hs.getUnits()) {
            if (hsu.getName().equals(unitName)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("refresh() found hotspot unit " + hsu.getName());
                }                
                this.context.put("hsu", hsu);
                hotSpotUnit = hsu;
                
                break;
            }
        }
        
        return hotSpotUnit;
    }

}
