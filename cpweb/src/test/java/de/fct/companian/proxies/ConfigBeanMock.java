package de.fct.companian.proxies;

import de.fct.companian.web.beans.ConfigBean;

public class ConfigBeanMock extends ConfigBean {

    @Override
    public String getRootPath() {
        return "http://localhost:8080/cpweb";
    }
}
