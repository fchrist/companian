package de.fct.companian.web.site.products.jars.doc.hotspot;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.analyze.db.model.Jar;
import de.fct.companian.analyze.db.model.Product;
import de.fct.companian.model.binding.cdl.DefaultAssertion;
import de.fct.companian.web.site.AbstractPageRenderer;
import de.fct.fdmm.apidl.APIDlProxy;
import de.fct.fdmm.apidl.MethodDescription;
import de.fct.fdmm.core.FrameworkDescription;
import de.fct.fdmm.core.HotSpotGroup;
import de.fct.fdmm.hotspot.CodingUnit;
import de.fct.fdmm.hotspot.Constraint;
import de.fct.fdmm.hotspot.HookCall;
import de.fct.fdmm.hotspot.HotSpot;
import de.fct.fdmm.hotspot.HotSpotUnit;
import de.fct.fdmm.hotspot.HotSpotUnitKind;

@Path("products/{productId}/jars/{jarid}/doc/hsgs/{hsgName}/hss/{hsName}/units/{unitName}/hooks")
public class HookPage extends AbstractPageRenderer {

    private static Logger logger = LoggerFactory.getLogger(HookPage.class);

    private APIDlProxy apiDlProxy;

    @Override
    public String getPage() {
        // Empty
        return null;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getHookPage(@PathParam("productId") int productId,
                                @PathParam("jarid") int jarId,
                                @PathParam("hsgName") String hsgName,
                                @PathParam("hsName") String hsName,
                                @PathParam("unitName") String unitName,
                                @QueryParam("methodapipath") String methodAPIPath) {

        if (logger.isDebugEnabled()) {
            logger.debug("getHookPage() " + methodAPIPath + " from unit " + unitName);
        }
        HookModel model = (HookModel) this.pageModel;
        FrameworkDescription fd = model.refresh(productId, jarId);
        HookCall hook = model.refresh(fd, hsgName, hsName, unitName, methodAPIPath);

        if (hook == null) {
            return Response.status(Status.NOT_FOUND).entity("Could not find Hook for " + methodAPIPath)
                    .build();
        } else {
            return Response.ok(super.getPage()).build();
        }

    }

    @POST
    @Path("/constraints")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAssertion(@PathParam("productId") int productId,
                                 @PathParam("jarid") int jarId,
                                 @PathParam("hsgName") String hsgName,
                                 @PathParam("hsName") String hsName,
                                 @PathParam("unitName") String unitName,
                                 @QueryParam("methodapipath") String methodAPIPath,
                                 @FormParam("assertion") String assertionExpression,
                                 @FormParam("assertionDescription") String assertionDescription) {

        if (logger.isDebugEnabled()) {
            logger.debug("addAssertion() " + assertionExpression + " to Hook " + methodAPIPath);
        }
        HookModel model = (HookModel) this.pageModel;
        FrameworkDescription fd = model.refresh(productId, jarId);
        HookCall hook = model.refresh(fd, hsgName, hsName, unitName, methodAPIPath);

        if (hook != null) {
            int posOfOpenBrace = assertionExpression.indexOf('(');
            int posOfCloseBrace = assertionExpression.indexOf(')');

            if (posOfOpenBrace < 0 || posOfCloseBrace < 0) {
                return Response.status(Status.BAD_REQUEST).entity("Not well formed assertion expression.")
                        .build();
            }

            String assertion = assertionExpression.substring(0, posOfOpenBrace).trim();
            if (assertion == null || assertion.isEmpty()) {
                return Response.status(Status.BAD_REQUEST).entity("Not well formed assertion expression.")
                        .build();
            }

            String parameterList = assertionExpression.substring(posOfOpenBrace + 1, posOfCloseBrace).trim();
            String[] parameters = parameterList.split(",");

            Constraint constraint = new Constraint();
            DefaultAssertion assertionObj = new DefaultAssertion();
            assertionObj.setName(assertion);
            if (parameters != null && (parameters.length > 0)) {
                List<String> paramList = new ArrayList<String>();
                for (String p : parameters) {
                    paramList.add(p.trim());
                }
                assertionObj.setParameters(paramList);
            }
            constraint.setAssertion(assertionObj);
            constraint.setDescription(assertionDescription);

            if (hook.getConstraints() == null) {
                hook.setConstraints(new ArrayList<Constraint>());
            }
            hook.getConstraints().add(constraint);

            // Save the model
            try {
                model.save(jarId, fd);
            } catch (Exception e) {
                logger.error("addAssertion() error during save", e);
                return Response.serverError().build();
            }

            JSONObject jsReturn = new JSONObject();
            try {
                jsReturn.put("assertion", constraint.getAssertion().getSignature());
                jsReturn.put("assertionDescription", constraint.getDescription());
            } catch (JSONException e) {
                logger.error("addAssertion() error while creating JSON object", e);
                return Response.serverError().build();
            }

            return Response.ok(jsReturn.toString()).build();
        } else {
            return Response.status(Status.NOT_FOUND).entity("Could not find Hook for " + methodAPIPath)
                    .build();
        }
    }

    @DELETE
    @Path("/constraints")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeAssertion(@PathParam("productId") int productId,
                                    @PathParam("jarid") int jarId,
                                    @PathParam("hsgName") String hsgName,
                                    @PathParam("hsName") String hsName,
                                    @PathParam("unitName") String unitName,
                                    @QueryParam("methodapipath") String methodAPIPath,
                                    @QueryParam("assertion") String assertionExpression) {
        if (logger.isDebugEnabled()) {
            logger.debug("removeAssertion() " + assertionExpression + " from Hook " + methodAPIPath);
        }
        HookModel model = (HookModel) this.pageModel;
        FrameworkDescription fd = model.refresh(productId, jarId);
        HookCall hook = model.refresh(fd, hsgName, hsName, unitName, methodAPIPath);

        if (hook != null) {
            if (hook.getConstraints() != null && !hook.getConstraints().isEmpty()) {
                Constraint toDelete = null;
                for (Constraint c : hook.getConstraints()) {
                    if (c.getAssertion().getSignature().equals(assertionExpression)) {
                        toDelete = c;
                        break;
                    }
                }

                if (toDelete != null) {
                    // Remove from Model
                    hook.getConstraints().remove(toDelete);

                    // Save the model
                    try {
                        model.save(jarId, fd);
                    } catch (Exception e) {
                        logger.error("removeAssertion() error during save", e);
                        return Response.serverError().build();
                    }

                    JSONObject jsReturn = new JSONObject();
                    try {
                        jsReturn.put("assertion", toDelete.getAssertion().getSignature());
                    } catch (JSONException e) {
                        logger.error("removeAssertion() error while creating JSON object", e);
                        return Response.serverError().build();
                    }

                    return Response.ok(jsReturn.toString()).build();
                }
            }
        }

        // If we reach this code, the assertion could not be found before by any reason
        return Response.status(Status.NOT_FOUND)
                .entity("Could not find Assertion for " + assertionExpression).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addHookThroughMethod(@PathParam("productId") int productId,
                                         @PathParam("jarid") int jarId,
                                         @PathParam("hsgName") String hsgName,
                                         @PathParam("hsName") String hsName,
                                         @PathParam("unitName") String unitName,
                                         @FormParam("methodapiuri") String methodUri) {

        if (logger.isDebugEnabled()) {
            logger.debug("addHookThroughMethod() for unit " + unitName);
        }
        HsUnitModel model = (HsUnitModel) this.pageModel;
        FrameworkDescription fd = model.refresh(productId, jarId);
        HotSpotUnit hsu = model.refresh(fd, hsgName, hsName, unitName);

        if (hsu.getKind() == HotSpotUnitKind.Coding) {

            if (methodUri != null) {
                String methodAPIPath = methodUri.replaceFirst(".*\\" + this.configBean.getRootPath(), "");
                if (logger.isDebugEnabled()) {
                    logger.debug("addHookThroughMethod() from method API path " + methodAPIPath);
                }

                MethodDescription md = this.apiDlProxy.getMethodDescription(methodAPIPath);
                if (md != null) {
                    CodingUnit cu = (CodingUnit) hsu;
                    List<HookCall> hooks = cu.getHooks();
                    if (hooks == null) {
                        hooks = new ArrayList<HookCall>();
                    } else {
                        // Check if HookCall already exists for this unit
                        boolean existsAlready = false;
                        for (HookCall hook : hooks) {
                            if (hook.getMethodAPIPath().equals(methodAPIPath)) {
                                existsAlready = true;
                                break;
                            }
                        }
                        if (existsAlready) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("addHookThroughMethod() Hook " + methodAPIPath
                                             + " already defined for this CodingUnit.");
                            }
                            return Response.status(Status.CONFLICT).entity(
                                "Hook " + methodAPIPath + " already defined for this CodingUnit.").build();
                        }
                    }

                    // Everything checked - create new HookCall object and add it
                    HookCall newHook = new HookCall();
                    newHook.setMethodAPIPath(methodAPIPath);
                    hooks.add(newHook);

                    // Set the new list of Hooks
                    cu.setHooks(hooks);

                    // Save the model
                    try {
                        model.save(jarId, fd);
                    } catch (Exception e) {
                        logger.error("addHookThroughMethod() error during save", e);
                        return Response.serverError().build();
                    }

                    JSONObject jsReturn = new JSONObject();
                    try {
                        jsReturn.put("signature", md.getSignature().toString());
                    } catch (JSONException e) {
                        logger.error("addHookThroughMethod() error while creating JSON object", e);
                        return Response.serverError().build();
                    }

                    return Response.ok(jsReturn.toString()).build();
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("addHookThroughMethod() No API description found for " + methodAPIPath);
                    }
                    return Response.status(Status.NOT_FOUND).entity(
                        "No API description found for " + methodAPIPath).build();
                }
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("addHookThroughMethod() No valid method URI specified in request.");
                }
                return Response.status(Status.BAD_REQUEST)
                        .entity("No valid method URI specified in request.").build();
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("addHookThroughMethod() The specified Hotspot is of kind "
                             + hsu.getKind().name() + " and is not a CodingUnit");
            }
            return Response.status(Status.BAD_REQUEST).entity(
                "The specified Hotspot is of kind " + hsu.getKind().name() + " and is not a CodingUnit")
                    .build();
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeHook(@PathParam("productId") int productId,
                               @PathParam("jarid") int jarId,
                               @PathParam("hsgName") String hsgName,
                               @PathParam("hsName") String hsName,
                               @PathParam("unitName") String unitName,
                               @QueryParam("methodapipath") String methodAPIPath) {

        if (logger.isDebugEnabled()) {
            logger.debug("removeHook() " + methodAPIPath + " from unit " + unitName);
        }
        HsUnitModel model = (HsUnitModel) this.pageModel;
        FrameworkDescription fd = model.refresh(productId, jarId);
        HotSpotUnit hsu = model.refresh(fd, hsgName, hsName, unitName);

        if (hsu.getKind() == HotSpotUnitKind.Coding) {
            MethodDescription md = this.apiDlProxy.getMethodDescription(methodAPIPath);
            if (md != null) {
                CodingUnit cu = (CodingUnit) hsu;
                List<HookCall> hooks = cu.getHooks();

                HookCall hookToBeRemoved = null;
                if (hooks != null && !hooks.isEmpty()) {
                    for (HookCall hc : hooks) {
                        if (hc.getMethodAPIPath().equals(methodAPIPath)) {
                            hookToBeRemoved = hc;
                            break;
                        }
                    }

                }

                if (hookToBeRemoved == null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("removeHook() Hook " + methodAPIPath
                                     + " was not found and therefore not removed");
                    }
                    return Response.status(Status.NOT_FOUND).entity("No Hook found for " + methodAPIPath)
                            .build();
                } else {
                    // Remove from model
                    hooks.remove(hookToBeRemoved);

                    // Save the model
                    try {
                        model.save(jarId, fd);
                    } catch (Exception e) {
                        logger.error("removeHook() error during save", e);
                        return Response.serverError().build();
                    }

                    JSONObject jsReturn = new JSONObject();
                    try {
                        jsReturn.put("signature", hookToBeRemoved.getSignature().toString());
                    } catch (JSONException e) {
                        logger.error("removeHook() error while creating JSON object", e);
                        return Response.serverError().build();
                    }

                    return Response.ok(jsReturn.toString()).build();
                }

            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("removeHook() No API description found for " + methodAPIPath);
                }
                return Response.status(Status.NOT_FOUND).entity(
                    "No API description found for " + methodAPIPath).build();
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("removeHook() The specified Hotspot is of kind " + hsu.getKind().name()
                             + " and is not a CodingUnit");
            }
            return Response.status(Status.BAD_REQUEST).entity(
                "The specified Hotspot is of kind " + hsu.getKind().name() + " and is not a CodingUnit")
                    .build();
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

        navigation.add(this.createNavLink("/products/" + p.getProductId() + "/jars/" + jar.getJarId()
                                          + "/doc/", "FDM"));

        HotSpotGroup hsg = (HotSpotGroup) this.pageModel.getContext().get("hsg");
        navigation.add(this.createNavLink("/products/" + p.getProductId() + "/jars/" + jar.getJarId()
                                          + "/doc/hsgs/" + hsg.getName(), hsg.getName()));

        HotSpot hs = (HotSpot) this.pageModel.getContext().get("hs");
        navigation.add(this.createNavLink("/products/" + p.getProductId() + "/jars/" + jar.getJarId()
                                          + "/doc/hsgs/" + hsg.getName() + "/hss/" + hs.getName(), hs
                .getName()));

        HotSpotUnit hsu = (HotSpotUnit) this.pageModel.getContext().get("hsu");
        navigation.add(this.createNavLink("/products/" + p.getProductId() + "/jars/" + jar.getJarId()
                                          + "/doc/hsgs/" + hsg.getName() + "/hss/" + hs.getName() + "/units/"
                                          + hsu.getName(), hsu.getName()));

        return navigation;
    }

    public void setApiDlProxy(APIDlProxy apiDlProxy) {
        this.apiDlProxy = apiDlProxy;
    }
}
