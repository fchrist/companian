package de.fct.companian.web.site;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.analyze.db.dao.JarDao;
import de.fct.companian.analyze.db.dao.ProductDao;
import de.fct.companian.analyze.db.model.Jar;
import de.fct.companian.analyze.db.model.Product;


public class IndexModel extends AbstractPageModel {

    @SuppressWarnings("unused")
    private Logger logger = LoggerFactory.getLogger(IndexModel.class);
    
    private final ProductDao productDao;
    private final JarDao jarDao;
    
    public IndexModel(ProductDao productDao, JarDao jarDao) {
        this.productDao = productDao;
        this.jarDao = jarDao;
    }
    
    @Override
    public void init() {
        this.context.put("title", "Welcome!");
        List<Product> products = this.productDao.listAllProducts();
        this.context.put("products", products);
        
        List<Jar> usableJars = this.jarDao.listAllJars();
        this.context.put("useableJars", usableJars);
    }

}
