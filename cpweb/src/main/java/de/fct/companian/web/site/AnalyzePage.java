package de.fct.companian.web.site;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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

import de.fct.companian.analyze.AnalyzeJob;
import de.fct.companian.analyze.AnalyzeJob.STATUS;
import de.fct.companian.web.beans.AnalyzeBean;

@Path("analyze")
public class AnalyzePage extends AbstractPageRenderer {

    private static Logger logger = LoggerFactory.getLogger(AnalyzePage.class);

    private AnalyzeBean analyzeBean;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response analyze(@FormParam("analyze_dir") String analyze_dir) {
        if (logger.isDebugEnabled()) {
            logger.debug("analyze() starting '" + analyze_dir + "'");
        }

        Response r = null;
        if (analyze_dir != null && !analyze_dir.isEmpty()) {
            String jobId = "{ \"id\":" + this.analyzeBean.startNewJob(analyze_dir) + "}";
            r = Response.ok(jobId).build();
        } else {
            logger.info("analyze() cancelling because no analyze dir given");
            r = Response.status(Status.BAD_REQUEST).build();
        }

        return r;
    }

    @GET
    @Path("status")
    @Produces(MediaType.APPLICATION_JSON)
    public String getJobs() {
        List<AnalyzeJob> jobs = this.analyzeBean.getJobs();

        JSONObject root = new JSONObject();
        try {
            JSONArray jsJobs = new JSONArray();
            for (AnalyzeJob job : jobs) {
                JSONObject jsJob = new JSONObject();
                jsJob.put("name", job.getName());
                jsJob.put("id", job.getId());
                jsJob.put("status", job.getStatus().name());
                jsJobs.put(jsJob);
            }
            root.put("jobs", jsJobs);
        } catch (JSONException e) {
            logger.error("getJobs() error creating JSON array of jobs", e);
        }
        return root.toString();
    }

    @GET
    @Path("status/{jobid}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getJobStatus(@PathParam("jobid") String jobid) {
        STATUS status = this.analyzeBean.getJobStatus(jobid);
        if (status != null) {
            return status.name();
        } else {
            return "nojob";
        }
    }

    @Override
    protected List<String> getNavigation() {
        return super.getDefaultNavigation();
    }

    public void setAnalyzeBean(AnalyzeBean analyzeBean) {
        this.analyzeBean = analyzeBean;
    }

}
