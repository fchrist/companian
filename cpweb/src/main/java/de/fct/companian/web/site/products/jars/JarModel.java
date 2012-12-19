package de.fct.companian.web.site.products.jars;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.analyze.db.dao.JarDao;
import de.fct.companian.analyze.db.model.Jar;
import de.fct.companian.web.site.AbstractPageModel;

public class JarModel extends AbstractPageModel {

    @SuppressWarnings("unused")
    private Logger logger = LoggerFactory.getLogger(JarModel.class);
    
    private final JarDao jarDao;
    
    public JarModel(JarDao jarDao) {
        this.jarDao = jarDao;
    }
    
    @Override
    public void init() {
    }

    public void refresh(int productId, int jarId) {
        Jar jar = this.jarDao.loadJar(jarId);
        this.context.put("jar", jar);
        this.context.put("product", jar.getProduct());
        if (jar != null && jar.getProduct() != null) {
            this.context.put("title", jar.getProduct().getName()
                                        + " :: " + jar.getArtifact()
                                        + " :: " + jar.getVersion());
        }
        
        List<Jar> otherVersions = this.jarDao.listOtherVersions(jar);
        this.context.put("otherVersions", otherVersions);
        
        List<Jar> usableJars = this.jarDao.listAllJars();
        this.context.put("useableJars", usableJars);
    }
    
}
