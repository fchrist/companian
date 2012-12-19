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

import java.util.HashMap;

import com.jeantessier.classreader.SourceFile_attribute;
import com.jeantessier.classreader.VisitorBase;

/**
 * Creates a map of class names with their corresponding source
 * files. By this we get a mapping of classes to JAR files.
 * 
 * @author Fabian Christ
 */
public class ClassToJarMapper extends VisitorBase {

	// ClassName -> Jar file name
	private HashMap<String, String> classToJarMap = new HashMap<String, String>();
	
	@Override
	public void visitSourceFile_attribute(SourceFile_attribute attribute) {
    	if (attribute.getSourceFile() != null) {
        	String sourceWithoutPath = attribute.getSourceFile().substring(attribute.getSourceFile().lastIndexOf("/") + 1);
        	sourceWithoutPath = sourceWithoutPath.substring(sourceWithoutPath.lastIndexOf("\\") + 1);
            classToJarMap.put(attribute.getSourceFile(), sourceWithoutPath);    		
    	};
	}
	
    /**
     * Get name of source file for given class name.
     * 
     * @param className
     * @return Name of source file (JAR)
     */
    public String getSource(String className) {
    	return classToJarMap.get(className);
    }

	public HashMap<String, String> getClassToJarMap() {
		return classToJarMap;
	}
    
}
