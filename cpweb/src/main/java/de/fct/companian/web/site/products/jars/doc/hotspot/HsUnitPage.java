package de.fct.companian.web.site.products.jars.doc.hotspot;

import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import de.fct.fdmm.core.FrameworkDescription;
import de.fct.fdmm.core.HotSpotGroup;
import de.fct.fdmm.hotspot.HotSpot;
import de.fct.fdmm.hotspot.HotSpotUnit;

@Path("products/{productId}/jars/{jarid}/doc/hsgs/{hsgName}/hss/{hsName}/units/{unitName}")
public class HsUnitPage extends AbstractPageRenderer {

    private static Logger logger = LoggerFactory.getLogger(HsUnitPage.class);

    @Override
    public String getPage() {
        // Empty
        return null;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getHsUnitPage(@PathParam("productId") int productId,
                                @PathParam("jarid") int jarId,
                                @PathParam("hsgName") String hsgName,
                                @PathParam("hsName") String hsName,
                                @PathParam("unitName") String unitName) {
        if (logger.isDebugEnabled()) {
            logger.debug("getHsUnitPage() for unit " + unitName);
        }
        HsUnitModel model = (HsUnitModel) this.pageModel;
        FrameworkDescription fd = model.refresh(productId, jarId);
        HotSpotUnit hsu = model.refresh(fd, hsgName, hsName, unitName);

        switch (hsu.getKind()) {
            case Coding:
                return this.renderPage(model.getContext(), this.getTemplate("CodingUnitPage.htm"));
            default:
                return super.getPage();
        }

    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response saveHsUnitDoc(@PathParam("productId") int productId,
                                  @PathParam("jarid") int jarId,
                                  @PathParam("hsgName") String hsgName,
                                  @PathParam("hsName") String hsName,
                                  @PathParam("unitName") String unitName,
                                  @FormParam("content") String content,
                                  @FormParam("id") String elementId) {
        if (logger.isDebugEnabled()) {
            logger.debug("saveHsUnitDoc() for JAR " + jarId + " and unit " + unitName);
        }
        HsUnitModel model = (HsUnitModel) this.pageModel;
        FrameworkDescription fd = model.refresh(productId, jarId);
        HotSpotUnit hsu = model.refresh(fd, hsgName, hsName, unitName);

        if (hsu == null) {
            return Response.status(Status.NOT_FOUND).entity("Could not find unit " + unitName).build();
        }

        if (elementId.equals("hsUnitDescription")) {
            if (logger.isDebugEnabled()) {
                logger.debug("saveHsUnitDoc() saving HS unit doc:\n" + content);
            }
            hsu.setDescription(content);
        }
        else if (elementId.equals("hsUnitName")) {
            String newHsuName = content.trim();
            if (newHsuName.contains("<")) {
                if (logger.isInfoEnabled()) {
                    logger.info("saveHsUnitDoc() new HS unit name contains illegal chars: " + newHsuName);
                }
                return Response.status(Status.BAD_REQUEST).entity("Illegal characters in name").build();
            } else {
                hsu.setName(newHsuName);
            }
        }

        try {
            model.save(jarId, fd);
        } catch (Exception e) {
            logger.error("saveHsUnitDoc() error during save", e);
            return Response.serverError().build();
        }

        return Response.ok().build();
    }

    @Override
    protected List<String> getNavigation() {
        List<String> navigation = super.getDefaultNavigation();

        Product p = (Product) this.pageModel.getContext().get("product");
        navigation.add(this.createNavLink("/products/" + p.getProductId(), p.getName()));

        Jar jar = (Jar) this.pageModel.getContext().get("jar");
        navigation.add(this.createNavLink("/products/" + p.getProductId() + "/jars/" + jar.getJarId(),
            jar.getArtifact() + " " + jar.getVersion()));

        navigation.add(this.createNavLink("/products/" + p.getProductId() + "/jars/" + jar.getJarId()
                                          + "/doc/", "FDM"));

        HotSpotGroup hsg = (HotSpotGroup) this.pageModel.getContext().get("hsg");
        navigation.add(this.createNavLink("/products/" + p.getProductId() + "/jars/" + jar.getJarId()
                                          + "/doc/hsgs/" + hsg.getName(), hsg.getName()));

        HotSpot hs = (HotSpot) this.pageModel.getContext().get("hs");
        navigation.add(this.createNavLink("/products/" + p.getProductId() + "/jars/" + jar.getJarId()
                                          + "/doc/hsgs/" + hsg.getName() + "/hss/" + hs.getName(), hs
                .getName()));

        return navigation;
    }

}
