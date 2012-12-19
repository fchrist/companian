package de.fct.companian.web.site.products.jars.doc.hotspot;

import de.fct.companian.analyze.db.dao.JarDao;
import de.fct.fdmm.core.FrameworkDescription;
import de.fct.fdmm.hotspot.CodingUnit;
import de.fct.fdmm.hotspot.HookCall;
import de.fct.fdmm.hotspot.HotSpotUnit;
import de.fct.fdmm.hotspot.HotSpotUnitKind;

public class HookModel extends HsUnitModel {

    public HookModel(JarDao jarDao) {
        super(jarDao);
    }

    public HookCall refresh(FrameworkDescription fd,
                                     String hsgName,
                                     String hsName,
                                     String unitName,
                                     String methodAPIPath) {

        HookCall hookCall = null;
        
        HotSpotUnit hsu = super.refresh(fd, hsgName, hsName, unitName);
        if (hsu != null) {
            if (hsu.getKind() == HotSpotUnitKind.Coding) {
                CodingUnit cu = (CodingUnit)hsu;
                if (cu.getHooks() != null && !cu.getHooks().isEmpty()) {
                    for (HookCall hc : cu.getHooks()) {
                        if (hc.getMethodAPIPath().equals(methodAPIPath)) {
                            hookCall = hc;
                            this.context.put("hook", hc);
                            break;
                        }
                    }
                }
            }            
        }
        
        return hookCall;
    }

}
