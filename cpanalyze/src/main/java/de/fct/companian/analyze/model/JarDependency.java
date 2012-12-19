package de.fct.companian.analyze.model;

import java.util.HashSet;

/**
 * @author Fabian Christ
 *
 */
public class JarDependency {

	private HashSet<JarDependency> inboundDependencies;
	private String name;
	private HashSet<JarDependency> outboundDependencies;
	private JarDependency parent;
	
	public JarDependency(String name) {
		this.name = name;
	}
	
	public void addInboundDependency(JarDependency dep) {
		if (inboundDependencies == null) {
			inboundDependencies = new HashSet<JarDependency>();
		}
		inboundDependencies.add(dep);
		dep.setParent(this);
	}
	
	public void addOutboundDependency(JarDependency dep) {
		if (outboundDependencies == null) {
			outboundDependencies = new HashSet<JarDependency>();
		}
		outboundDependencies.add(dep);
		dep.setParent(this);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		JarDependency x = (JarDependency)obj;
		return (this.name.equals(x.getName()));
	}
	
	public HashSet<JarDependency> getInboundDependencies() {
		return inboundDependencies;
	}
	
	public String getName() {
		return name;
	}
	
	public HashSet<JarDependency> getOutboundDependencies() {
		return outboundDependencies;
	}

	public JarDependency getParent() {
		return parent;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setParent(JarDependency parent) {
		this.parent = parent;
	}

}
