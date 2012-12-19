package de.fct.fdmm.bindingdl;

import java.util.List;

import de.fct.fdmm.basis.FDObject;

public interface BindingDescription extends FDObject {

    public List<APICall> getApiCalls();

    public List<MetaEntry> getMetaEntries();

}
