package de.fct.companian.web.site.products;

import java.util.List;

import de.fct.companian.analyze.db.dao.ProductDao;
import de.fct.companian.analyze.db.model.Product;
import de.fct.companian.web.site.AbstractPageModel;

public class ProductsModel extends AbstractPageModel {

    private final ProductDao productDao;

    public ProductsModel(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public void init() {
        this.context.put("title", "Products");
        List<Product> products = this.productDao.listAllProducts();
        this.context.put("products", products);
    }
}
