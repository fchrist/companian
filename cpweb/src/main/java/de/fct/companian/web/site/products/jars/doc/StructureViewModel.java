package de.fct.companian.web.site.products.jars.doc;

import org.codehaus.jettison.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.analyze.db.dao.JarDao;
import de.fct.companian.web.ArchitectureDlSerializer;
import de.fct.fdmm.architecturedl.ArchitectureDescription;
import de.fct.fdmm.architecturedl.ArchitectureDlProxy;
import de.fct.fdmm.core.FrameworkDescription;
import de.fct.fdmm.core.StructureView;

public class StructureViewModel extends DocModel {

    private Logger logger = LoggerFactory.getLogger(StructureViewModel.class);

    private ArchitectureDlProxy proxy;
    
    public StructureViewModel(JarDao jarDao) {
        super(jarDao);
    }

    @Override
    public void init() {}

    public StructureView refresh(FrameworkDescription fd, String structureViewName) {
        StructureView structureView = null;
        if (fd != null && structureViewName != null) {
            if (fd.getStructureViews() != null && !fd.getStructureViews().isEmpty()) {
                for (StructureView sv : fd.getStructureViews()) {
                    if (sv.getName().equals(structureViewName)) {
                        this.context.put("structureView", sv);
                        structureView = sv;
                        break;
                    }
                }
            }
        }
        
        return structureView;
    }

    public ArchitectureDescription refresh(StructureView sv) {
        ArchitectureDescription ad = null;
        if (sv.getArchitectureDescriptionFile() != null) {
            ad = sv.getArchitectureDescription(this.proxy);
            if (ad != null) {
                try {
                    this.context.put("architectureAsJSON", ArchitectureDlSerializer.toJSON(ad).toString(2));
                } catch (JSONException e) {
                    logger.error("refresh() error while serializing architecture to JSON", e);
                }
            }
        }
        
        return ad;
    }

    public void setProxy(ArchitectureDlProxy proxy) {
        this.proxy = proxy;
    }
    
}
