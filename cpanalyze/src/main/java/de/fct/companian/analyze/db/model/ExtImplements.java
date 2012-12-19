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

public class ExtImplements {

	private int implementsId;
	private String extFqcn;
	private int classId;

	public int getImplementsId() {
		return implementsId;
	}

	public void setImplementsId(int implementsId) {
		this.implementsId = implementsId;
	}

	public String getExtFqcn() {
		return extFqcn;
	}

	public void setExtFqcn(String extFqcn) {
		this.extFqcn = extFqcn;
	}

	public int getClassId() {
		return classId;
	}

	public void setClassId(int classId) {
		this.classId = classId;
	}

}
