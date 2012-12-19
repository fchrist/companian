#!/bin/sh

# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.


mvn install:install-file -Dfile=depfind-1.2.0.jar -DartifactId=depfind -DgroupId=net.sourceforge.depfind -Dversion=1.2.0 -Dpackaging=jar

mvn install:install-file -Dfile=jmxtools-1.2.1.jar -DartifactId=jmxtools -DgroupId=com.sun.jdmk -Dversion=1.2.1 -Dpackaging=jar

mvn install:install-file -Dfile=jmxri-1.2.1.jar -DartifactId=jmxri -DgroupId=com.sun.jmx -Dversion=1.2.1 -Dpackaging=jar

mvn install:install-file -Dfile=javax-jms-1.1.jar -DartifactId=jms -DgroupId=javax.jms -Dversion=1.1 -Dpackaging=jar
