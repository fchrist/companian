package de.fct.companian.compare;

import java.util.Map;

import de.fct.fdmm.FDKind;
import de.fct.fdmm.FDRating;
import de.fct.fdmm.FDifference;
import de.fct.fdmm.constraintdl.Assertion;

public class CompareCDL extends AbstractCompare {

    private CompareBasis basis;
    
    public CompareCDL(Map<String,Object> context) {
        super(context);
    }

    protected FDifference compareConstraintAssertions(Assertion leftAssertion,
                                                    Assertion rightAssertion) {

        FDifference diff = null;

        FDifference nameDiff = basis.compareNamedElements("assertion", leftAssertion, rightAssertion, leftAssertion);
        FDifference sigDiff = basis.compareValues("signature", leftAssertion.getSignature(), rightAssertion
                .getSignature(), leftAssertion);
        FDifference paramDiff = basis.compareLists("parameter", leftAssertion.getParameters(), rightAssertion
                .getParameters(), leftAssertion);

        if (nameDiff != null || sigDiff != null || paramDiff != null) {
            diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);
            diff.setSource(leftAssertion);
            diff.setDescription("The assertion '" + leftAssertion.getName() + "' was changed in version "
                                + this.getRightVersion());
            diff.addDifference(nameDiff);
            diff.addDifference(sigDiff);
        }
        return diff;
    }

    public void setBasis(CompareBasis basis) {
        this.basis = basis;
    }

}
