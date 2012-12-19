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
package de.fct.companian.analyze.db.model;

public class ExtCall {

	private int callId;
	private String extFqcn;
	private String extSignature;
	private Integer classId;
	private Integer methodId;

	public int getCallId() {
		return callId;
	}

	public void setCallId(int callId) {
		this.callId = callId;
	}

	public String getExtFqcn() {
		return extFqcn;
	}

	public void setExtFqcn(String extFqcn) {
		this.extFqcn = extFqcn;
	}

	public String getExtSignature() {
		return extSignature;
	}

	public void setExtSignature(String extSignature) {
		this.extSignature = extSignature;
	}

	public Integer getClassId() {
		return classId;
	}

	public void setClassId(Integer classId) {
		this.classId = classId;
	}

	public Integer getMethodId() {
		return methodId;
	}

	public void setMethodId(Integer methodId) {
		this.methodId = methodId;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer("ExtCall[");
		buf.append("callId=").append(callId).append(",");
		buf.append("extFqcn=").append(extFqcn).append(",");
		buf.append("extSignature=").append(extSignature).append(",");
		buf.append("classId=").append(classId).append(",");
		buf.append("methodId=").append(methodId).append("]");

		return buf.toString();
	}

}
