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

public class JarCompareResult {

    private String side;
    private String methodSignature;
    private int methodAccessFlags;
    private String fqcn;
    private int classAccessFlags;

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getMethodSignature() {
        return methodSignature;
    }

    public void setMethodSignature(String methodSignature) {
        this.methodSignature = methodSignature;
    }

    public int getMethodAccessFlags() {
        return methodAccessFlags;
    }

    public void setMethodAccessFlags(int methodAccessFlags) {
        this.methodAccessFlags = methodAccessFlags;
    }

    public String getFqcn() {
        return fqcn;
    }

    public void setFqcn(String fqcn) {
        this.fqcn = fqcn;
    }

    public int getClassAccessFlags() {
        return classAccessFlags;
    }

    public void setClassAccessFlags(int classAccessFlags) {
        this.classAccessFlags = classAccessFlags;
    }

}
