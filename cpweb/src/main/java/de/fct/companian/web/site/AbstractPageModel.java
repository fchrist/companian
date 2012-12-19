package de.fct.companian.web.site;

import org.apache.velocity.VelocityContext;

public abstract class AbstractPageModel {

    protected VelocityContext context = new VelocityContext();
    
    public abstract void init();
    
    public VelocityContext getContext() {
        return this.context;
    }
}