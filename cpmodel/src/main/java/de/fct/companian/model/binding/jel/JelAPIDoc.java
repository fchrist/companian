package de.fct.companian.model.binding.jel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.fct.companian.model.jel.JEL;
import de.fct.companian.model.jel.JelClass;
import de.fct.fdmm.apidl.APIDoc;
import de.fct.fdmm.apidl.PackageDescription;

public class JelAPIDoc implements APIDoc {
    
    private final JEL jel;
    
    private List<PackageDescription> packages;
    
    public JelAPIDoc(JEL jel) {
        this.jel = jel;
    }
    
    public List<PackageDescription> getPackages() {
        if (this.packages == null) {
            Map<String, List<JelClass>> packageMap = new HashMap<String,List<JelClass>>();
            for (JelClass jelClass : jel.getJelclass()) {
                if (packageMap.get(jelClass.getPackage()) == null) {
                    packageMap.put(jelClass.getPackage(), new ArrayList<JelClass>());
                }
                packageMap.get(jelClass.getPackage()).add(jelClass);
            }
            
            this.packages = new ArrayList<PackageDescription>();
            for (String pack : packageMap.keySet()) {
                JelPackageDescription jpd = new JelPackageDescription(pack, packageMap.get(pack));
                this.packages.add(jpd);
            }
            Collections.sort(this.packages, new Comparator<PackageDescription>() {
                public int compare(PackageDescription o1, PackageDescription o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
        }
        
        return packages;
    }

}
