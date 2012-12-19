package de.fct.companian.web.site.products;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.web.site.AbstractPageRenderer;

@Path("products")
public class ProductsPage extends AbstractPageRenderer {

    private static Logger logger = LoggerFactory.getLogger(ProductsPage.class);

    private ProductJarsModel productJarsModel;

    @Path("/{productId}")
    @GET
    @Produces("text/html")
    public String renderJarsPage(@PathParam("productId") int productId) {
        if (logger.isDebugEnabled()) {
            logger.debug("renderJarsPage() for product " + productId);
        }

        this.productJarsModel.refresh(productId);
        return this.renderPage(this.productJarsModel.getContext(), this.getTemplate("ProductJarsPage"));
    }

    @Override
    protected List<String> getNavigation() {
        List<String> navigation = super.getDefaultNavigation();
        return navigation;
    }

    public void setProductJarsModel(ProductJarsModel productJarsModel) {
        this.productJarsModel = productJarsModel;
    }

}
