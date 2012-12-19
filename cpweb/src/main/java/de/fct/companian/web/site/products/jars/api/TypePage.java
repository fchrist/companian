package de.fct.companian.web.site.products.jars.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.analyze.db.model.Jar;
import de.fct.companian.analyze.db.model.Product;
import de.fct.companian.web.site.AbstractPageRenderer;
import de.fct.fdmm.apidl.MethodDescription;
import de.fct.fdmm.apidl.PackageDescription;
import de.fct.fdmm.apidl.TypeDescription;

@Path("products/{productId}/jars/{jarId}/api/{packageName}/{typeName}")
public class TypePage extends AbstractPageRenderer {

    private static Logger logger = LoggerFactory.getLogger(TypePage.class);

    @Override
    public String getPage() {
        // Empty
        return null;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getTypePage(@PathParam("productId") int productId,
                                @PathParam("jarId") int jarId,
                                @PathParam("packageName") String packageName,
                                @PathParam("typeName") String typeName) {
        if (logger.isDebugEnabled()) {
            logger.debug("getTypePage() for JAR " + jarId);
        }
        TypeDescription td = ((TypeModel) this.pageModel).refresh(productId, jarId, packageName, typeName);
        if (td != null) {
            return Response.ok(super.getPage()).build();
        } else {
            return Response.status(Status.NOT_FOUND).entity(
                "Could not find TypeDescription for type " + typeName).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTypes(@PathParam("productId") int productId,
                             @PathParam("jarId") int jarId,
                             @PathParam("packageName") String packageName,
                             @PathParam("typeName") String typeName) {
        if (logger.isDebugEnabled()) {
            logger.debug("getTypes() as JSON for JAR " + jarId);
        }
        TypeDescription td = ((TypeModel) this.pageModel).refresh(productId, jarId, packageName, typeName);
        if (td != null) {
            String tdJson = generateJson(td);
            if (tdJson != null) {
                return Response.ok(tdJson).build();
            }
            else {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(
                    "Error while serializing the JSON object.").build();
            }
        } else {
            return Response.status(Status.NOT_FOUND).entity(
                "Could not find TypeDescription for type " + typeName).build();
        }
    }

    private String generateJson(TypeDescription td) {
        try {
            JSONObject root = new JSONObject();
            root.put("fqtn", td.getFqtn());
            
            if (td.getMethods() != null) {
                JSONArray methods = new JSONArray();
                for (MethodDescription md : td.getMethods()) {
                    JSONObject jsonMethod = new JSONObject();
                    jsonMethod.put("returnType", md.getSignature().getReturnType().getFqtn());
                    jsonMethod.put("signature", md.getSignature().toString());
                    methods.put(jsonMethod);
                }
                root.put("methods", methods);
            }
            
            return root.toString();
        } catch (JSONException e) {
            logger.error("generateJson() error while creating JSON objects", e);
        }
        
        return null;
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
                                          + "/api/", "API"));

        PackageDescription pd = (PackageDescription) this.pageModel.getContext().get("package");
        navigation.add(this.createNavLink("/products/" + p.getProductId() + "/jars/" + jar.getJarId()
                                          + "/api/" + pd.getName(), pd.getName()));

        return navigation;
    }

}
