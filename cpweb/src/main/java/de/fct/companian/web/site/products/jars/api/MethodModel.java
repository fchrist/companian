package de.fct.companian.web.site.products.jars.api;

import de.fct.companian.analyze.db.dao.JarDao;
import de.fct.fdmm.apidl.MethodDescription;
import de.fct.fdmm.apidl.TypeDescription;

public class MethodModel extends TypeModel {

    public MethodModel(JarDao jarDao) {
        super(jarDao);
    }

    public MethodDescription refresh(int productId, int jarId, String packageName, String typeName, String signature) {
        MethodDescription methodDescription = null;
        TypeDescription td = super.refresh(productId, jarId, packageName, typeName);
        
        for (MethodDescription md : td.getMethods()) {
            if (md.getSignature().toString().equals(signature)) {
                methodDescription = md;
                this.context.put("method", methodDescription);
                break;
            }
        }

        return methodDescription;
    }
}
