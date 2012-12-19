package de.fct.companian.analyze.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JarEpDependency {

	private String jarName;
	private HashMap<String, List<String>> dependencyByClass;
	
	public JarEpDependency(String jarName) {
		this.jarName = jarName;
		this.dependencyByClass = new HashMap<String, List<String>>();
	}
	
	public void addDependency(String className, String depDescription) {
		List<String> existingDescr = this.dependencyByClass.get(className);
		if (existingDescr == null) {
			existingDescr = new ArrayList<String>();
		}
		existingDescr.add(depDescription);
		this.dependencyByClass.put(className, existingDescr);
	}
	
	public HashMap<String, List<String>> getDependencyByClass() {
		return this.dependencyByClass;
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean eq = false;
		if (obj != null && obj instanceof JarEpDependency) {
			JarEpDependency other = (JarEpDependency)obj;
			eq = this.jarName.equals(other.jarName);
		}
		
		return eq;
	}
}
