package de.fct.companian.web.site.products;

import java.util.List;

import org.apache.velocity.VelocityContext;

import de.fct.companian.analyze.db.dao.JarDao;
import de.fct.companian.analyze.db.dao.ProductDao;
import de.fct.companian.analyze.db.model.Jar;
import de.fct.companian.analyze.db.model.Product;
import de.fct.companian.web.site.AbstractPageModel;

public class ProductJarsModel extends AbstractPageModel {

    private final ProductDao productDao;
    private final JarDao jarDao;
    
    public ProductJarsModel(ProductDao productDao, JarDao jarDao) {
        this.productDao = productDao;
        this.jarDao = jarDao;
    }
    
    @Override
    public void init() {
    }
    
    public void refresh(int productId) {
        this.context = new VelocityContext();

        Product product = this.productDao.loadProduct(productId);
        this.context.put("product", product);
        if (product != null) {
            this.context.put("title", product.getName());        
        }
        
        List<Jar> jars = this.jarDao.listJarsForProduct(productId);
        this.context.put("jars", jars);
    }

}
