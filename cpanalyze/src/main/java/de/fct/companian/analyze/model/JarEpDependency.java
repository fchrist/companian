/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
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
