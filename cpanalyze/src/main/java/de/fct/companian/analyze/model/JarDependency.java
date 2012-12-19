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
