package de.fct.companian.web.site.products.jars.doc;

import de.fct.companian.analyze.db.dao.JarDao;
import de.fct.fdmm.bindingdl.MetaEntry;
import de.fct.fdmm.core.FrameworkDescription;
import de.fct.fdmm.hotspot.BindingCapability;

public class MetaEntryModel extends BindingCapabilityModel {

    public MetaEntryModel(JarDao jarDao) {
        super(jarDao);
    }

    public MetaEntry refresh(FrameworkDescription fd, String bindingName, String metaEntryName) {
        MetaEntry metaEntry = null;
        
        BindingCapability bc = super.refresh(fd, bindingName);
        if (bc != null && bc.getBindingDescription() != null) {
            if (bc.getBindingDescription().getMetaEntries() != null && !bc.getBindingDescription().getMetaEntries().isEmpty()) {
                for (MetaEntry me : bc.getBindingDescription().getMetaEntries()) {
                    if (me.getName().equals(metaEntryName)) {
                        metaEntry = me;
                        this.context.put("metaentry", me);
                        break;
                    }
                }
            }
        }
        
        return metaEntry;
    }

}
