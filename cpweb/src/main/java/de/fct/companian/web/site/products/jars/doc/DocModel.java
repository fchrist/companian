package de.fct.companian.web.site.products.jars.doc;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.analyze.db.dao.JarDao;
import de.fct.companian.analyze.db.model.Jar;
import de.fct.companian.web.site.AbstractPageModel;
import de.fct.fdmm.core.FrameworkDescription;

public class DocModel extends AbstractPageModel {

    private Logger logger = LoggerFactory.getLogger(DocModel.class);

    protected final JarDao jarDao;

    public DocModel(JarDao jarDao) {
        this.jarDao = jarDao;
    }

    @Override
    public void init() {
        this.context.put("title", "Framework Documentation");
    }

    public FrameworkDescription refresh(int productId, int jarId) {
        if (logger.isDebugEnabled()) {
            logger.debug("refresh() with JAR " + jarId);
        }
        Jar jar = this.jarDao.loadJar(jarId);
        this.context.put("jar", jar);
        this.context.put("product", jar.getProduct());
        if (jar != null) {
            this.context.put("title", "Framework Documentation of " + jar.getArtifact() + " " + jar.getVersion());
        }

        return loadFrameworkDescription(jar);
    }

    protected FrameworkDescription loadFrameworkDescription(Jar jar) {
        FrameworkDescription frameworkDescription = null;
        String fdmmdoc = this.jarDao.loadFDMMDoc(jar.getJarId());
        if (fdmmdoc != null) {
            InputStream is;
            try {
                is = new ByteArrayInputStream(fdmmdoc.getBytes("UTF-8"));
                frameworkDescription = JAXB.unmarshal(is, FrameworkDescription.class);
                this.context.put("fd", frameworkDescription);
            } catch (UnsupportedEncodingException e) {
                logger.error("refresh() error while refreshing fdmmdoc", e);
            }
        } else {
            frameworkDescription = new FrameworkDescription();
            frameworkDescription.setName(jar.getArtifact());
        }
        
        return frameworkDescription;
    }

    public void save(int jarId, FrameworkDescription fd) throws Exception {
        StringWriter writer = new StringWriter();
        JAXB.marshal(fd, writer);
        this.jarDao.updateFdmmdoc(jarId, writer.toString());

        if (logger.isDebugEnabled()) {
            logger.debug("save() saved doc\n" + writer.toString());
        }
    }

}
