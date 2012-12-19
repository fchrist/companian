package de.fct.fdmm;

import java.util.ArrayList;
import java.util.List;

import de.fct.fdmm.basis.FDObject;
import de.fct.fdmm.basis.INamedElement;

public class FDifference {

    private final FDKind kind;
    private FDRating rating;
    private FDObject source;
    private String description;

    private FDifference parentDiff = null;
    private List<FDifference> subDiffs = null;

    public FDifference(FDKind kind, FDRating rating) {
        this.kind = kind;
        this.rating = rating;
        this.subDiffs = new ArrayList<FDifference>();
    }

    public FDKind getKind() {
        return kind;
    }

    public FDRating getRating() {
        return rating;
    }
    
    public void setRating(FDRating rating) {
        this.rating = rating;
    }

    public FDObject getSource() {
        return source;
    }

    public void setSource(FDObject source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FDifference getParentDiff() {
        return parentDiff;
    }

    public void setParentDiff(FDifference parentDiff) {
        this.parentDiff = parentDiff;
    }

    public void addDifference(FDifference difference) {
        if (difference != null) {
            if (this.subDiffs == null) {
                this.subDiffs = new ArrayList<FDifference>();
            }
            this.subDiffs.add(difference);
            difference.setParentDiff(this);
        }
    }

    public List<FDifference> getSubDiffs() {
        return this.subDiffs;
    }

    public FDifference clone(boolean deepClone) {
        FDifference clone = new FDifference(this.kind, this.rating);
        clone.setDescription(this.description);
        clone.setSource(this.source);

        if (deepClone) {
            if (this.subDiffs != null) {
                for (FDifference subDiff : this.subDiffs) {
                    clone.addDifference(subDiff.clone(true));
                }
            }
        }
        return clone;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FDifference) {
            FDifference comparedDiff = (FDifference) obj;
            if (comparedDiff.getKind().equals(this.kind) && comparedDiff.getRating().equals(this.rating)
                && comparedDiff.getDescription().equals(this.description)
                && comparedDiff.getSource() == this.source) {
                return true;
            }
        }

        return false;
    }

    public String toString() {
    	return this.toString(0);
    }
    
    private String toString( int level) {
    	StringBuilder sb = new StringBuilder();
    	// indent
    	for (int l=0; l < level; l++) {
    		sb.append("  ");
    	}
    	sb.append("- ");
        sb.append(this.kind.name()).append(" ");
        sb.append(this.rating.name()).append(" @ ");
        sb.append(this.source).append(" : ");
        sb.append(this.description);
        sb.append("\n");
        if (!this.subDiffs.isEmpty()) {
            for (FDifference subDiff : this.subDiffs) {
            	level++;
                sb.append(subDiff.toString(level));
            }
        }
        
        return sb.toString();
    }
    
    public String toHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<ul class=\"fdifflist\">");
        sb.append("<li class=\"fdiffentry, diff");
        sb.append(this.rating.name());
        sb.append("\">");
        sb.append(this.rating.name()).append(" -&gt; ");
        sb.append(this.kind.name()).append(" @ ");
        if (this.source instanceof INamedElement) {
            INamedElement ne = (INamedElement)this.source;
            sb.append(ne.getName());
        }
        else {
            sb.append(this.source);
        }
        sb.append(" : ");
        sb.append(this.description);
        sb.append("</li>");
        if (this.subDiffs != null) {
            for (FDifference subDiff : this.subDiffs) {
                sb.append(subDiff.toHtml());
            }
        }
        sb.append("</ul>");
        
        return sb.toString();
    }
}
