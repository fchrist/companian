package de.fct.companian.analyze.helper;

import org.apache.log4j.Logger;

public class ClassHelper {

	private static Logger logger = Logger.getLogger(ClassHelper.class);
	
	public static boolean isMethodCall(String methodCall) {
		int bracketPos = methodCall.indexOf('(');
		
		return bracketPos > 0;
	}
	
	public static String getClassName(String methodCall) {
		if (logger.isDebugEnabled()) {
			logger.debug("getClassName() for " + methodCall);
		}
		int bracket = methodCall.indexOf('(');
		String className = methodCall.substring(0, bracket);
		
		int lastDot = className.lastIndexOf('.');
		className = className.substring(0, lastDot);
		return className;
	}
	
	public static String getSignature(String methodCall) {
		int bracket = methodCall.indexOf('(');
		String parameter = methodCall.substring(bracket);
		String signature = methodCall.substring(0, bracket);
		
		int lastDot = signature.lastIndexOf('.');
		signature = signature.substring(lastDot + 1);
		signature += parameter;
		
		return signature;
	}
	
	public static void main(String [] args) {
		String input = "junit.framework.TestCase.TestCase(dfgdfg fxgsdfg)";
		
		System.out.println("input     " + input);
		System.out.println("class     " + getClassName(input));
		System.out.println("signature " + getSignature(input));
	}
	
}
