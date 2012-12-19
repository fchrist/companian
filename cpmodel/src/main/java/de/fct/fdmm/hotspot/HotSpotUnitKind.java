package de.fct.fdmm.hotspot;

public enum HotSpotUnitKind {

    Coding("Coding Unit");
    
    private String description;
    
    private HotSpotUnitKind(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
    
}
