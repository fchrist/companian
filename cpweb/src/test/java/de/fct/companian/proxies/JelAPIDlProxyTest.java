package de.fct.companian.proxies;

import org.junit.Assert;
import org.junit.Test;



public class JelAPIDlProxyTest {

    @Test
    public void testGetMethodDescription() throws Exception {
        MethodModelMock modelMock = new MethodModelMock();
        JelAPIDlProxy proxy = new JelAPIDlProxy(modelMock);
        
        Assert.assertNull(proxy.getMethodDescription(null));
        
        String methodDescriptionUri = "/site/products/1/jars/1/api/org.apache.stanbol.enhancer.servicesapi/EnhancementEngine/canEnhance(org.apache.stanbol.enhancer.servicesapi.ContentItem)";
        proxy.getMethodDescription(methodDescriptionUri);
        Assert.assertEquals(1, modelMock.getProductId());
        Assert.assertEquals(1, modelMock.getJarId());
        Assert.assertEquals("org.apache.stanbol.enhancer.servicesapi", modelMock.getPackageName());
        Assert.assertEquals("EnhancementEngine", modelMock.getTypeName());
        Assert.assertEquals("canEnhance(org.apache.stanbol.enhancer.servicesapi.ContentItem)", modelMock.getSignature());

    }
}
