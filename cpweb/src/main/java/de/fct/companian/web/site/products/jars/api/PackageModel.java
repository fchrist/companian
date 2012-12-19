package de.fct.companian.web.site.products.jars.api;

import de.fct.companian.analyze.db.dao.JarDao;
import de.fct.fdmm.apidl.APIDoc;
import de.fct.fdmm.apidl.PackageDescription;

public class PackageModel extends APIDocModel {

    
    public PackageModel(JarDao jarDao) {
        super(jarDao);
    }

    public PackageDescription refresh(int productId, int jarId, String packageName) {
        APIDoc apidoc = super.refresh(productId, jarId); 
        
        PackageDescription packageDescription = null;
        for (PackageDescription pd : apidoc.getPackages()) {
            if (pd.getName().equals(packageName)) {
                packageDescription = pd;
                this.context.put("package", packageDescription);
                break;
            }
        }
        
        return packageDescription;
    }

}
