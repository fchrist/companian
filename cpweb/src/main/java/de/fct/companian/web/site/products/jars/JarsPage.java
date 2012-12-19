package de.fct.companian.web.site.products.jars;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.analyze.db.model.Product;
import de.fct.companian.web.site.AbstractPageRenderer;

@Path("products/{productId}/jars")
public class JarsPage extends AbstractPageRenderer {

    private static Logger logger = LoggerFactory.getLogger(JarsPage.class);
    
    private JarModel jarModel;
    
    @GET
    @Path("/{jarid}")
    @Produces("text/html")
    public String getJarPage(@PathParam("productId") int productId, @PathParam("jarid") int jarId) {
        if (logger.isDebugEnabled()) {
            logger.debug("getJarPage() for JAR " + jarId);
        }
        this.jarModel.refresh(productId, jarId);
        return this.renderPage(this.jarModel.getContext(), this.getTemplate("JarPage"));
    }
    
    @Override
    protected List<String> getNavigation() {
        List<String> navigation = super.getDefaultNavigation();
        Product p = (Product) this.jarModel.getContext().get("product");
        navigation.add(this.createNavLink("/products/" + p.getProductId(), p.getName()));
        return navigation;
    }
    
    public void setJarModel(JarModel jarModel) {
        this.jarModel = jarModel;
    }
    
}
