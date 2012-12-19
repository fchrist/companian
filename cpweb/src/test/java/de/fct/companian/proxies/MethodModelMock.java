package de.fct.companian.proxies;

import de.fct.companian.web.site.products.jars.api.MethodModel;
import de.fct.fdmm.apidl.MethodDescription;

public class MethodModelMock extends MethodModel {

    int productId;
    int jarId;
    String packageName;
    String typeName;
    String signature;
    
    public MethodModelMock() {
        super(null);
    }
    
    @Override
    public MethodDescription refresh(int productId,
                                     int jarId,
                                     String packageName,
                                     String typeName,
                                     String signature) {
        this.productId = productId;
        this.jarId = jarId;
        this.packageName = packageName;
        this.typeName = typeName;
        this.signature = signature;
        return null;
    }

    public int getProductId() {
        return productId;
    }

    public int getJarId() {
        return jarId;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getSignature() {
        return signature;
    }
    
}
