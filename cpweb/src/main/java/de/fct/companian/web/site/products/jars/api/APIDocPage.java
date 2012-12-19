package de.fct.companian.web.site.products.jars.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.analyze.db.model.Jar;
import de.fct.companian.analyze.db.model.Product;
import de.fct.companian.web.site.AbstractPageRenderer;
import de.fct.fdmm.apidl.APIDoc;

@Path("products/{productId}/jars/{jarId}/api")
public class APIDocPage extends AbstractPageRenderer {

    private static Logger logger = LoggerFactory.getLogger(APIDocPage.class);
    
    @Override
    public String getPage() {
        // Empty
        return null;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getHssPage(@PathParam("productId") int productId,
                             @PathParam("jarId") int jarId) {
        if (logger.isDebugEnabled()) {
            logger.debug("getHssPage() for JAR " + jarId);
        }
        APIDoc apidoc = ((APIDocModel) this.pageModel).refresh(productId, jarId);
        if (apidoc != null) {
            return Response.ok(super.getPage()).build();
        }
        else {
            return Response.status(Status.NOT_FOUND).entity("Could not find APIDoc for JAR with ID " + jarId).build();
        }
    }
    
    @Override
    protected List<String> getNavigation() {
        List<String> navigation = super.getDefaultNavigation();

        Product p = (Product) this.pageModel.getContext().get("product");
        navigation.add(this.createNavLink("/products/" + p.getProductId(), p.getName()));

        Jar jar = (Jar) this.pageModel.getContext().get("jar");
        navigation.add(this.createNavLink("/products/" + p.getProductId() + "/jars/" + jar.getJarId(),
            jar.getArtifact() + " " + jar.getVersion()));

        return navigation;
    }

}
