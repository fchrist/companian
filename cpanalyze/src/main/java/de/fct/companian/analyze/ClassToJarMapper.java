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
