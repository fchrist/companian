package de.fct.companian.web.site;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("ccc")
public class CCCPage extends AbstractPageRenderer {

    @SuppressWarnings("unused")
    private static Logger logger = LoggerFactory.getLogger(CCCPage.class);

    @Override
    public String getPage() {
        return "";
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String performCCC(@QueryParam("usingJarId") int usingJarId,
                             @QueryParam("usedJarId") int usedJarId,
                             @QueryParam("futureJarId") int futureJarId) {

        CCCModel model = (CCCModel) this.pageModel;
        model.performCCC(usingJarId, usedJarId, futureJarId);

        return this.renderPage(model.getContext(), this.getTemplate("CCCResult"));
    }

    @Override
    protected List<String> getNavigation() {
        return super.getDefaultNavigation();
    }

}
