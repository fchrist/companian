package de.fct.fdmm.sampledl;

import java.util.List;

public interface HyperDocument {

    public List<DocElement> getOwnedElements();

    public void setOwnedElements(List<DocElement> ownedElements);
}
