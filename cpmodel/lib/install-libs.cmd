@echo off
rem Licensed to the Apache Software Foundation (ASF) under one or more
rem contributor license agreements.  See the NOTICE file distributed with
rem this work for additional information regarding copyright ownership.
rem The ASF licenses this file to You under the Apache License, Version 2.0
rem (the "License"); you may not use this file except in compliance with
rem the License.  You may obtain a copy of the License at
rem
rem     http://www.apache.org/licenses/LICENSE-2.0
rem
rem Unless required by applicable law or agreed to in writing, software
rem distributed under the License is distributed on an "AS IS" BASIS,
rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
rem See the License for the specific language governing permissions and
rem limitations under the License.

REM Installs the libs in the local Maven repo

call mvn install:install-file -Dfile=org.eclipse.emf.common_2.5.0.v200906151043.jar -DartifactId=emf-common -DgroupId=org.eclipse.emf -Dversion=2.5.0.v200906151043 -Dpackaging=jar

call mvn install:install-file -Dfile=org.eclipse.emf.ecore_2.5.0.v200906151043.jar -DartifactId=ecore -DgroupId=org.eclipse.emf -Dversion=2.5.0.v200906151043 -Dpackaging=jar

call mvn install:install-file -Dfile=org.eclipse.emf.ecore.xmi_2.5.0.v200906151043.jar -DartifactId=ecore-xmi -DgroupId=org.eclipse.emf.ecore -Dversion=2.5.0.v200906151043 -Dpackaging=jar

call mvn install:install-file -Dfile=org.eclipse.uml2.common_1.5.0.v200905041045.jar -DartifactId=uml2-common -DgroupId=org.eclipse.uml2 -Dversion=1.5.0.v200905041045 -Dpackaging=jar

call mvn install:install-file -Dfile=org.eclipse.uml2.uml_3.0.1.v200908281330.jar -DartifactId=uml -DgroupId=org.eclipse.uml2 -Dversion=3.0.1.v200908281330 -Dpackaging=jar

call mvn install:install-file -Dfile=org.eclipse.uml2.uml.resources_3.0.0.v200906011111.jar -DartifactId=uml-resources -DgroupId=org.eclipse.uml2.uml -Dversion=3.0.0.v200906011111 -Dpackaging=jar
