package de.fct.companian.web.site.products.jars;

import de.fct.companian.analyze.db.dao.JarDao;
import de.fct.companian.web.site.AbstractPageModel;

public class JarsModel extends AbstractPageModel {

    @SuppressWarnings("unused")
    private final JarDao jarDao;
    
    public JarsModel(JarDao jarDao) {
        this.jarDao = jarDao;
    }
    
    @Override
    public void init() {
    }

    public void refresh() {

    }
    
}
