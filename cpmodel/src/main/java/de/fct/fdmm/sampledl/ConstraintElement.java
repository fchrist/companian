package de.fct.fdmm.sampledl;

import de.fct.fdmm.constraintdl.Assertion;

public interface ConstraintElement extends DocElement {

    public Assertion getConstraint();

    public void setConstraint(Assertion constraint);

}
