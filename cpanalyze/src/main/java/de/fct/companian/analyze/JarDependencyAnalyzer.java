package de.fct.companian.analyze;

import java.util.Collection;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.jeantessier.dependency.ClassNode;
import com.jeantessier.dependency.FeatureNode;
import com.jeantessier.dependency.Node;
import com.jeantessier.dependency.VisitorBase;

import de.fct.companian.analyze.model.JarDependency;

/**
 * @author Fabian Christ 
 */
public class JarDependencyAnalyzer extends VisitorBase {

	private static Logger logger = Logger.getLogger(JarDependencyAnalyzer.class);
	
	private ClassToJarMapper sourceCollector;

	private HashMap<String, JarDependency> dependencies = new HashMap<String, JarDependency>();

	public ClassToJarMapper getSourceCollector() {
		return sourceCollector;
	}

	public void setClassfileCollector(ClassToJarMapper loader) {
		this.sourceCollector = loader;
	}

	private void addDependencyToSource(JarDependency jarDependency, String className) {
		String outSource = sourceCollector.getSource(className);
		if (outSource != null) {
			JarDependency outboundJarDependency = dependencies.get(outSource);
			if (outboundJarDependency == null) {
				outboundJarDependency = new JarDependency(outSource);
				dependencies.put(outSource, outboundJarDependency);
			}
			if (!jarDependency.equals(outboundJarDependency)) {
				jarDependency.addOutboundDependency(outboundJarDependency);
				outboundJarDependency.addInboundDependency(jarDependency);
			}
		}		
	}
	
	@Override
	protected void postprocessClassNode(ClassNode node) {
		super.preprocessClassNode(node);

		String classSource = sourceCollector.getSource(node.getName());
		if (classSource != null) {
			
			// Falls dieses JAR noch nicht in der Liste der abh�ngigen JARs enthalten ist,
			// f�gen wir es zun�chst hinzu.
			JarDependency jarDependency = dependencies.get(classSource);
			if (jarDependency == null) {
				jarDependency = new JarDependency(classSource);
				dependencies.put(classSource, jarDependency);
			}

			for (Node outDepNode : node.getOutboundDependencies()) {
				if (outDepNode instanceof ClassNode) {
					ClassNode outDepClassNode = (ClassNode) outDepNode;
					addDependencyToSource(jarDependency, outDepClassNode.getName());
				}
			}		

			for (FeatureNode fNode : node.getFeatures()) {
				for (Node outDepNode : fNode.getOutboundDependencies()) {
					String className = "";
					if (outDepNode instanceof ClassNode) {
						ClassNode outDepClassNode = (ClassNode) outDepNode;
						className = outDepClassNode.getName();
					} else {
						if (outDepNode instanceof FeatureNode) {
							FeatureNode feature = (FeatureNode) outDepNode;
							String featureName = feature.getName();
							int pos = featureName.indexOf("(");
							if (pos > -1) {
								featureName = featureName.substring(0, pos);
							}
							pos = featureName.lastIndexOf(".");
							if (pos > -1) {
								featureName = featureName.substring(0, pos);
							}
							className = featureName;
						}
					}
					addDependencyToSource(jarDependency, className);
				}				
			}
		}

	}

	public void printOutboundDependencyList() {
		System.out.println("Printing Outbound Dependencies:");
		for (JarDependency current : dependencies.values()) {
			System.out.println("* " + current.getName() + " verwendet ");
			if (current.getOutboundDependencies() != null) {
				printOutboundDependencies(current, 0);
			}
		}
	}
	
	public void printInboundDependencyList() {
		System.out.println("Printing Inbound Dependencies:");
		for (JarDependency current : dependencies.values()) {
			System.out.println("* " + current.getName() + " wird verwendet von ");
			if (current.getInboundDependencies() != null) {
				printInboundDependencies(current, 0);
			}
		}
	}	

	public void printInboundDependencies(JarDependency source, int indent) {
		for (JarDependency current : source.getInboundDependencies()) {
			StringBuffer line = new StringBuffer();
			for (int a = 0; a <= indent; a++) {
				line.append("** ");
			}
			line.append(current.getName());
			System.out.println(line.toString());
//			if (current.getInboundDependencies() != null) {
//				printDependencies(current, ++indent);
//				indent--;
//			}
		}
	}
	
	public void printOutboundDependencies(JarDependency source, int indent) {
		for (JarDependency current : source.getOutboundDependencies()) {
			StringBuffer line = new StringBuffer();
			for (int a = 0; a <= indent; a++) {
				line.append("** ");
			}
			line.append(current.getName());
			System.out.println(line.toString());
//			if (current.getOutboundDependencies() != null) {
//				printDependencies(current, ++indent);
//				indent--;
//			}
		}
	}

	public Collection<JarDependency> getDependencyList() {
		return dependencies.values();
	}

}
