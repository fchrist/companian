package de.fct.companian.analyze.mvn.helper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.jaxen.JaxenException;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.dom4j.Dom4jXPath;

import de.fct.companian.analyze.mvn.PomInfo;

public class PomHelper {

	private static Logger logger = Logger.getLogger(PomHelper.class);
	
	private final File pomFile;
	private final SimpleNamespaceContext nsContext;
	private Document document;
	
	public PomHelper(File pomFile) throws DocumentException {
		this.pomFile = pomFile;
		this.document = null;
		
        SAXReader reader = new SAXReader();
        reader.setEncoding("ISO-8859-1");
        reader.setIgnoreComments(true);
        reader.setValidation(false);
        
        try {
        	this.document = reader.read(this.pomFile);
        }
        catch (Throwable t) {
        	t.printStackTrace();
        }
        
        if (this.document != null) {
            Element projectElement = this.document.getRootElement();
            Namespace defaultNS = projectElement.getNamespace();
            if (logger.isDebugEnabled()) {
            	logger.debug("extractPomInfo() using default namespace " + defaultNS.getURI());
            }
            
            Map<String, String> nsMap = new HashMap<String, String>();
            nsMap.put("mvn", defaultNS.getURI());
            
            this.nsContext = new SimpleNamespaceContext(nsMap);
        }
        else {
        	throw new DocumentException("Could not create document.");
        }
	}
	
	public PomInfo extractPomInfo() throws DocumentException {
        Node groupIdNode = this.selectSingleNode("/mvn:project/mvn:groupId");
        Node artifactIdNode = this.selectSingleNode("/mvn:project/mvn:artifactId");
        Node versionNode = this.selectSingleNode("/mvn:project/mvn:version");
        
        PomInfo pomInfo = null;
        if (groupIdNode != null && artifactIdNode != null && versionNode != null) {
        	String groupId = groupIdNode.getText();
        	String artifactId = artifactIdNode.getText();
        	String version = versionNode.getText();
        	if (groupId != null && artifactId != null && version != null) {
        		String jarName = this.pomFile.getName().replace(".pom", ".jar");
        		pomInfo = new PomInfo(jarName, groupId, artifactId, version);
        	}
        }
        else {
        	if (logger.isDebugEnabled()) {
        		logger.debug("extractPomInfo() no PomInfo found - perhaps a parent POM exists");
        	}
        	if (artifactIdNode != null) {
        		String artifactId = artifactIdNode.getText();
        		if (logger.isDebugEnabled()) {
        			logger.debug("extractPomInfo() found artifactId " + artifactId + " and look for parent POM");
        		}
        		pomInfo = this.extractParentPom();
        		if (pomInfo != null) {
        			pomInfo.setArtifactId(artifactId);
        		}
        	}
        	else {
        		if (logger.isDebugEnabled()) {
        			logger.debug("extractPomInfo() no artifactId found - no use to look for parent POM");
        		}
        	}
        }

		if (logger.isInfoEnabled()) {
			logger.info("extractPomInfo() extracted " + pomInfo + " from " + this.pomFile.getName());
		}
        return pomInfo;
	}

	private Node selectSingleNode(String xpath) {
		Node node = null;
		try {
			Dom4jXPath dom4jXPath = new Dom4jXPath(xpath);
			dom4jXPath.setNamespaceContext(this.nsContext);
			node = (Node)dom4jXPath.selectSingleNode(this.document);
		} catch (JaxenException e) {
			logger.error("selectSingleNode() error selecting node " + xpath, e);
		} catch (RuntimeException re) {
			logger.error("selectSingleNode() error casting Node", re);
		}
		
		return node;
	}

	private PomInfo extractParentPom() throws DocumentException {
		PomInfo pomInfo = null;
		
		// Schaue nach Parent POM
		Node parentArtifactIdNode = this.selectSingleNode("/mvn:project/mvn:parent/mvn:artifactId");
		Node parentGroupIdNode = this.selectSingleNode("/mvn:project/mvn:parent/mvn:groupId");
		Node parentVersionNode = this.selectSingleNode("/mvn:project/mvn:parent/mvn:version");
		
		if (parentArtifactIdNode != null && parentGroupIdNode != null && parentVersionNode != null) {
			String parentArtifactId = parentArtifactIdNode.getText();
			String parentGroupId = parentGroupIdNode.getText();
			String parentVersion = parentVersionNode.getText();
			
			String userHome = System.getProperty("user.home");
			
			String parentPomName = userHome + "/.m2/repository/" + makePath(parentGroupId) + "/" + parentArtifactId + "/" + parentVersion + "/" + parentArtifactId + "-" + parentVersion + ".pom";
			if (logger.isInfoEnabled()) {
				logger.info("extractParentPom() looking for parent POM at " + parentPomName);
			}
			File parentPom = new File(parentPomName);
			if (parentPom.exists() && parentPom.canRead()) {
				if (logger.isDebugEnabled()) {
					logger.debug("extractParentPom() parent POM found - starting new extraction round");
				}
				PomHelper parentHelper = new PomHelper(parentPom);
				pomInfo = parentHelper.extractPomInfo();
			}
			else {
			    if (logger.isInfoEnabled()) {
			        logger.info("extractParentPom() either the parent POM " + parentPomName + " does not exist or can't be read");
			    }
			}
		}
		
		return pomInfo;
	}
	
	private static String makePath(String dottedName) {
		String path = null;
		if (dottedName != null) {
			path = dottedName.replaceAll("\\.", "/");
		}
		
		return path;
	}
}
