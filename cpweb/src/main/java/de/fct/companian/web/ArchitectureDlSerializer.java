package de.fct.companian.web;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.fdmm.architecturedl.ArchitectureDescription;
import de.fct.fdmm.architecturedl.ArchitectureElement;

public class ArchitectureDlSerializer {

    private static Logger logger = LoggerFactory.getLogger(ArchitectureDlSerializer.class);

    public static JSONObject toJSON(ArchitectureDescription ad) {
        JSONObject root = new JSONObject();
        try {
            for (ArchitectureElement ae : ad.getSubElements()) {
                JSONObject aeJSON = new JSONObject();
                root.put(ae.getName(), aeJSON);
                aeJSON.put("element", toJSON(ae));
                if (ae.getSubArchitecture() != null) {
                    aeJSON.put("subarchitecture", toJSON(ae.getSubArchitecture()));
                }
            }
        } catch (JSONException e) {
            logger.error("toJSON() error while serializing architecture DL", e);
            root = null;
        }

        return root;
    }

    private static JSONObject toJSON(ArchitectureElement ae) throws JSONException {
        JSONObject root = new JSONObject();
        JSONArray depArray = new JSONArray();
        for (ArchitectureElement dep : ae.getDependencies()) {
            depArray.put(dep.getName());
        }
        root.put("dependencies", depArray);

        JSONArray hsgs = new JSONArray();
        for (String hsg : ae.getHotSpotGroups()) {
            hsgs.put(hsg);
        }
        root.put("hotspotgroups", hsgs);

        return root;
    }
}
