package de.fct.companian.web.site.products.jars.doc;

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

import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.analyze.db.model.Jar;
import de.fct.companian.analyze.db.model.Product;
import de.fct.companian.web.ArchitectureDlSerializer;
import de.fct.companian.web.BehaviorDlSerializer;
import de.fct.companian.web.site.AbstractPageRenderer;
import de.fct.fdmm.architecturedl.ArchitectureDescription;
import de.fct.fdmm.architecturedl.ArchitectureDlProxy;
import de.fct.fdmm.architecturedl.behaviordl.BehaviorDlProxy;
import de.fct.fdmm.architecturedl.behaviordl.Process;
import de.fct.fdmm.core.BehaviorView;
import de.fct.fdmm.core.FrameworkDescription;

@Path("products/{productId}/jars/{jarid}/doc/bviews/{behaviorViewName}")
public class BehaviorViewPage extends AbstractPageRenderer {

    private static Logger logger = LoggerFactory.getLogger(BehaviorViewPage.class);

    private BehaviorDlProxy proxy;
    
    @Override
    public String getPage() {
        // Empty
        return null;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getBehaviorViewPage(@PathParam("productId") int productId,
                                       @PathParam("jarid") int jarId,
                                       @PathParam("behaviorViewName") String behaviorViewName) {
        if (logger.isDebugEnabled()) {
            logger.debug("getBehaviorViewPage() for JAR " + jarId);
        }
        BehaviorViewModel model = (BehaviorViewModel) this.pageModel;
        FrameworkDescription fd = model.refresh(productId, jarId);
        BehaviorView bv = model.refresh(fd, behaviorViewName);

        if (bv == null) {
            return Response.status(Status.NOT_FOUND).entity(
                "Could not find behavior view " + behaviorViewName).build();
        }
        
        model.refresh(bv);

        return Response.ok(super.getPage()).build();
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response postDescription(@PathParam("productId") int productId,
                                    @PathParam("jarid") int jarId,
                                    @PathParam("behaviorViewName") String behaviorViewName,
                                    @FormParam("content") String content,
                                    @FormParam("id") String elementId) {
        if (logger.isDebugEnabled()) {
            logger.debug("postDescription() for behavior view " + behaviorViewName + " of JAR " + jarId);
        }

        if (!elementId.equals("description")) {
            return Response.status(Status.BAD_REQUEST).entity("Unknown element ID " + elementId).build();
        }

        if (content == null || content.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).entity("No content").build();            
        }
        
        BehaviorViewModel model = (BehaviorViewModel) this.pageModel;
        FrameworkDescription fd = model.refresh(productId, jarId);
        BehaviorView bv = model.refresh(fd, behaviorViewName);

        if (bv == null) {
            return Response.status(Status.NOT_FOUND).entity(
                "Could not find behavior view " + behaviorViewName).build();
        }

        bv.setDescription(content);
        
        try {
            model.save(jarId, fd);
        } catch (Exception e) {
            logger.error("postDescription() error during save", e);
            return Response.serverError().build();
        }

        return Response.ok().build();
    }
    
    @POST
    @Path("/diagram")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addDiagram(@PathParam("productId") int productId,
                               @PathParam("jarid") int jarId,
                               @PathParam("behaviorViewName") String behaviorViewName,
                               @FormParam("activityDiagramFile") String activityDiagramFile) {

        if (logger.isDebugEnabled()) {
            logger.debug("addDiagram() " + activityDiagramFile);
        }

        if (activityDiagramFile == null || activityDiagramFile.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).entity("No activity diagram file name given").build();
        }

        BehaviorViewModel model = (BehaviorViewModel) this.pageModel;
        FrameworkDescription fd = model.refresh(productId, jarId);
        BehaviorView bv = model.refresh(fd, behaviorViewName);

        if (bv == null) {
            return Response.status(Status.NOT_FOUND).entity(
                "Could not find behavior view " + behaviorViewName).build();
        }

        Process process = proxy.getProcess(activityDiagramFile);
        if (process == null) {
            return Response.status(Status.BAD_REQUEST).entity("Could not open " + activityDiagramFile)
                    .build();
        }

        bv.setProcessFile(activityDiagramFile);

        // Save the model
        try {
            model.save(jarId, fd);
        } catch (Exception e) {
            logger.error("addDiagram() error during save", e);
            return Response.serverError().build();
        }

        // Return architecture DL as JSON
        JSONObject jsReturn = BehaviorDlSerializer.toJSON(process);
        return Response.ok(jsReturn.toString()).build();
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

        return navigation;
    }

    public void setProxy(BehaviorDlProxy proxy) {
        this.proxy = proxy;
    }
    
}
