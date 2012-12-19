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
package de.fct.companian.analyze;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.fct.companian.analyze.model.JarDependency;


/**
 * Exporting a DOT file from a given deptree model.
 * 
 * @author <a href="fchrist@group.s-und-n.de">Fabian Christ</a>
 */
public final class DotExporter {

	private Collection<JarDependency> jarDependencies;
	
	private String fileName;
	private String path;
	private String projectName;
	
	private Set edgeSet = new HashSet();
	
	private Map nodeMap = new HashMap();
	private int nodeCounter = 0;
	
	/**
	 * Constructs the exporter with a given list of JAR dependencies and
	 * a file name for exporting the DOT file.
	 * 
	 * If the file name doesn't end with *.dot - the ending
	 * will be automatically added.
	 * 
	 * @param deptree The deptree model to export.
	 * @param fileName	The file name to store the exported DOT file.
	 */
	public DotExporter(Collection<JarDependency> dependencies, String fileName) {
		this.jarDependencies = dependencies;
		this.fileName = fileName;
	}
	
	private String checkFileName(String fileName) {
		if (!fileName.endsWith(".dot") && !fileName.endsWith(".DOT")) {
			fileName += ".dot";
		}
		return fileName;
	}
	
	/**
	 * Exports the deptree model into a DOT file. The
	 * model and file name for the export were given
	 * by constructor arguments.
	 * 
	 * @throws IOException
	 */
	public void export() throws IOException {
		fileName = checkFileName(fileName);
		File exFile = new File(fileName);
		
		projectName = fileName;
		StringBuffer content = new StringBuffer();
		
		content.append("digraph ");
		content.append(" deptree");
		content.append(" {\n");
		
		//content.append("\tlabel=\"JAR dependency tree\";\n");
		content.append("\tfontname=\"Arial\";\n");
		content.append("\tfontsize=\"14\";\n");
		
		generateNodes(content);
		generateEdges(content);
		
		content.append("}\n");
		
		saveFile(exFile, content.toString());
	}
	
	private void generateEdge(StringBuffer content, String from, String to) {
		String fromNode = (String)nodeMap.get(from);
		String toNode = (String)nodeMap.get(to);
		String edge = fromNode + " -> " + toNode;
		if (!edgeSet.contains(edge)) {
			content.append("\t");
			content.append(edge);
			content.append(" [");
			content.append(" fontname=\"Arial\"");
			content.append(" fontsize=\"12\"");
			content.append(" color=\"#000000\" label=\"\"");
			content.append(" ];\n");		
			edgeSet.add(edge);
		}
	}	
	
	private void generateEdges(StringBuffer content) {
		for (JarDependency dep : jarDependencies) {
//			generateEdge(content, "root", dep.getName());
			if (dep.getOutboundDependencies() != null) {
				for (JarDependency outDep : dep.getOutboundDependencies()) {
					generateEdge(content, dep.getName(), outDep.getName());
				}
			}
		}
	}
	
	private void generateNode(StringBuffer content, JarDependency depArtifact) {
		if (!nodeMap.containsKey(depArtifact.getName())) {
			String curNode = nextNodeName();
			
			content.append("\t");
			content.append(curNode);
			content.append(" [");
			content.append(" fontname=\"Arial\"");
			content.append(" fontsize=\"12\"");		
			content.append(" label=\"").append(depArtifact.getName()).append("\"");
			if (depArtifact.getInboundDependencies() == null) {
				content.append(" color=\"#FF0000\"");				
			}
			content.append("];\n");
			nodeMap.put(depArtifact.getName(), curNode);
		}
	}
	
	private void generateNodes(StringBuffer content) {
		for (JarDependency dep: jarDependencies) {
			generateNode(content, dep);
		}
	}
	
	private String nextNodeName() {
		return "n" + nodeCounter++;
	}
	
	private void saveFile(File file, String content) throws IOException {
		FileWriter fw = new FileWriter(file);
		fw.write(content);
		fw.flush();
		fw.close();
	}

	public void setJarDependencies(List<JarDependency> jarDependencies) {
		this.jarDependencies = jarDependencies;
	}
}
