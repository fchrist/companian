package de.fct.companian.compare;

import java.util.Map;

import de.fct.fdmm.FDKind;
import de.fct.fdmm.FDRating;
import de.fct.fdmm.FDifference;
import de.fct.fdmm.deploymentdl.DeploymentDescription;

public class CompareDDL extends AbstractCompare {

    private CompareBasis basis;

    public CompareDDL(Map<String,Object> context) {
        super(context);
    }

    public FDifference compareDeployments(DeploymentDescription leftDeployment, DeploymentDescription rightDeployment) {

        FDifference diff = new FDifference(FDKind.ChangedElement, FDRating.Warning);

        diff.addDifference(basis.compareDescriptions("installation description", leftDeployment
                .getInstallationDescription(), rightDeployment.getInstallationDescription(), leftDeployment));
        diff.addDifference(basis.compareDescriptions("package format description", leftDeployment.getPackageFormatDescription(),
            rightDeployment.getPackageFormatDescription(), leftDeployment));

        if (diff.getSubDiffs().isEmpty()) {
            diff = null;
        }
        else {
            diff.setSource(leftDeployment);
            diff.setDescription("The Deployment has changed in version " + this.getRightVersion());
        }
        return diff;
    }

    public void setBasis(CompareBasis basis) {
        this.basis = basis;
    }
}
