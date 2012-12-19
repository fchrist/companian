package de.fct.companian.web;

import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VelocityConfigurer {

    private static Logger logger = LoggerFactory.getLogger(VelocityConfigurer.class);

    private VelocityEngine velocityEngine;
    private Properties config;

    public void init() {
        logger.debug("init() configuring Velocity engine");
        if (config != null) {
            for (Object key : config.keySet()) {
                this.velocityEngine.addProperty((String) key, config.get(key));
            }

            // Configure to use Log4j for logging
            this.velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
                "org.apache.velocity.runtime.log.Log4JLogChute");
            this.velocityEngine.setProperty("runtime.log.logsystem.log4j.logger", "velocityLogger");

            try {
                this.velocityEngine.init();
            } catch (Exception e) {
                logger.error("setVelocityEngine() error setting velocity engine", e);
            }
        } else {
            logger.error("init() no config found");
        }
    }

    public Properties getConfig() {
        return config;
    }

    public void setConfig(Properties config) {
        this.config = config;
    }

    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

}
