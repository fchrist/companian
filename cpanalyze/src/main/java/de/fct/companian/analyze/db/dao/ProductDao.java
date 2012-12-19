package de.fct.companian.analyze.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import de.fct.companian.analyze.db.model.Product;

public class ProductDao extends AbstractDao {

    public ProductDao(DataSource dataSource) {
        super(dataSource);
    }

    private static Logger logger = Logger.getLogger(ProductDao.class);

    public Product loadProduct(String name) {
        Product product = null;

        try {
            String sql = "SELECT * FROM products WHERE name=? LIMIT 0,1";

            Connection con = this.dataSource.getConnection();
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, name);
            ResultSet rs = statement.executeQuery();
            if (rs != null) {
                if (rs.next()) {
                    product = new Product();
                    product.setProductId(rs.getInt("productId"));
                    product.setName(rs.getString("name"));
                    product.setCategory(rs.getString("category"));
                }
                rs.close();
                statement.close();
                con.close();
            }
        } catch (SQLException e) {
            logger.error("loadProduct() SQL error loading product", e);
        }

        return product;
    }
    
    public Product loadProduct(int productId) {
        Product product = null;

        try {
            String sql = "SELECT * FROM products WHERE productId=? LIMIT 0,1";

            Connection con = this.dataSource.getConnection();
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, productId);
            ResultSet rs = statement.executeQuery();
            if (rs != null) {
                if (rs.next()) {
                    product = new Product();
                    product.setProductId(rs.getInt("productId"));
                    product.setName(rs.getString("name"));
                    product.setCategory(rs.getString("category"));
                }
                rs.close();
                statement.close();
                con.close();
            }
        } catch (SQLException e) {
            logger.error("loadProduct() SQL error loading product", e);
        }

        return product;
    }    

    public Product saveProduct(String name) {
        if (logger.isDebugEnabled()) {
            logger.debug("addProduct() inserting " + name);
        }
        Product product = null;

        Connection con = null;
        PreparedStatement statement = null;
        try {
            String insertProduct = "INSERT INTO products (name) VALUES ( ? )";
            con = this.dataSource.getConnection();
            statement = con.prepareStatement(insertProduct);
            statement.setString(1, name);

            if (logger.isDebugEnabled()) {
                logger.debug("addProduct() executing " + statement.toString());
            }
            statement.executeUpdate();
            ResultSet keySet = statement.getGeneratedKeys();
            if (keySet.next()) {
                int productId = keySet.getInt(1);
                product = this.loadProduct(productId);
            }
        } catch (SQLException e) {
            logger.error("addProduct() db error", e);
        } finally {
            if (statement != null) try {
                statement.close();
            } catch (SQLException e) {}
            if (con != null) try {
                con.close();
            } catch (SQLException e) {}
        }
        
        return product;
    }

    public List<Product> listAllProducts() {
        if (logger.isDebugEnabled()) {
            logger.debug("listAllProducts() loading all products from database");
        }
        List<Product> products = new ArrayList<Product>();

        try {
            String sql = "SELECT * FROM products p ORDER BY p.name";

            Connection con = this.dataSource.getConnection();
            PreparedStatement statement = con.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    Product product = new Product();
                    product.setProductId(rs.getInt("productId"));
                    product.setName(rs.getString("name"));
                    product.setCategory(rs.getString("category"));
                    products.add(product);
                }
                rs.close();
                statement.close();
                con.close();
            }
        } catch (SQLException e) {
            logger.error("listAllProducts() SQL error loading products", e);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("listAllProducts() returning list with " + products.size() + " products");
        }
        return products;
    }
}
