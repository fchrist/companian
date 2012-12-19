package de.fct.fdmm.bindingdl;

import java.util.List;

import de.fct.fdmm.basis.IDescription;
import de.fct.fdmm.basis.INamedElement;

/**
 * A meta entry may be an entry in a (deployment) descriptor or a source code annotation that tells the
 * compiler how to handle this unit.
 * 
 * @author fchrist
 */
public interface MetaEntry extends INamedElement, IDescription {

    public List<Task> getTasks();
    
}
