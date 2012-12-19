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
