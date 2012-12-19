package de.fct.fdmm.protocoldl;

import java.util.Set;

import de.fct.fdmm.basis.FDObject;

public interface HookProtocolState extends FDObject {

    public Set<ProtocolHook> getSubsequentHooks();

    // public void setSubsequentHooks(List<ProtocolHook> subsequentHooks);

    public String getId();

    // public void setId(String Id);

}
