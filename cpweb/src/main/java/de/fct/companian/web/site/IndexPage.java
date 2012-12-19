package de.fct.companian.web.site;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Path;


@Path("/")
public class IndexPage extends AbstractPageRenderer {

    @Override
    protected List<String> getNavigation() {
        return new ArrayList<String>();
    }
}
