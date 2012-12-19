package de.fct.companian.web.site.products.jars;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.analyze.db.model.Jar;
import de.fct.companian.web.site.AbstractPageRenderer;

@Path("products/{productId}/jars/analyzeuse")
public class AnalyzeUsePage extends AbstractPageRenderer {

    private static Logger logger = LoggerFactory.getLogger(AnalyzeUsePage.class);
    
    public String getPage() {
        return "";
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String analyzeuse(@PathParam("productId") int productId,
                          @QueryParam("using") int usingJarId,
                          @QueryParam("used") int usedJarId) {

        if (logger.isDebugEnabled()) {
            logger.debug("analyzeuse() Uses JAR " + usingJarId + " the JAR " + usedJarId + " ?");
        }
        AnalyzeUseModel model = (AnalyzeUseModel) this.pageModel;
        model.refresh(productId, usingJarId, usedJarId);

        return this.renderPage(model.getContext(), this.getTemplate("AnalyzeUseResult"));
    }
    
    @Override
    protected List<String> getNavigation() {
        List<String> navigation = super.getDefaultNavigation();

        Jar jar = (Jar) this.pageModel.getContext().get("usingJar");

        navigation.add(this.createNavLink("/products/" + jar.getProduct().getProductId(), jar.getProduct().getName()));

        navigation.add(this.createNavLink("/products/" + jar.getProduct().getProductId() + "/jars/" + jar.getJarId(),
            jar.getArtifact() + " " + jar.getVersion()));

        return navigation;
    }

}
