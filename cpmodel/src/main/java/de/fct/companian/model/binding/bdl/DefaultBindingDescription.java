package de.fct.companian.model.binding.bdl;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import de.fct.fdmm.bindingdl.APICall;
import de.fct.fdmm.bindingdl.BindingDescription;
import de.fct.fdmm.bindingdl.MetaEntry;

public class DefaultBindingDescription implements BindingDescription {

    private List<APICall> apiCalls;
    private List<MetaEntry> metaEntries;
    
    @XmlElement(type=DefaultAPICall.class)
    public List<APICall> getApiCalls() {
        return this.apiCalls;
    }

    public void setApiCalls(List<APICall> apiCalls) {
        this.apiCalls = apiCalls;
    }
    
    @XmlElement(type=DefaultMetaEntry.class)
    public List<MetaEntry> getMetaEntries() {
        return this.metaEntries;
    }

    public void setMetaEntries(List<MetaEntry> metaEntries) {
        this.metaEntries = metaEntries;
    }

    
}
