package de.fct.companian.web.site.products.jars.api;

import de.fct.companian.analyze.db.dao.JarDao;
import de.fct.fdmm.apidl.PackageDescription;
import de.fct.fdmm.apidl.TypeDescription;

public class TypeModel extends PackageModel {

    public TypeModel(JarDao jarDao) {
        super(jarDao);
    }

    public TypeDescription refresh(int productId, int jarId, String packageName, String typeName) {
        PackageDescription pd = super.refresh(productId, jarId, packageName);
        
        TypeDescription typeDescription = null;
        for(TypeDescription td : pd.getOwnedTypes()) {
            if (td.getName().equals(typeName)) {
                typeDescription = td;
                this.context.put("type", td);
                break;
            }
        }
        
        return typeDescription;
    }

}
