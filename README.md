<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
Companian
=========

The Companian tool suite is a Java web application for

* documenting application frameworks according to the "Framework Description Meta-Model" (FDMM)
* running automatic framework compatibility analysis based on the FDMM documentation

Companian is a prototypical implementation of the ideas described in my Ph.D. thesis.

Companian is licensed under the Apache Software License, Version 2.0.

Compile
-------

Companian uses Apache Maven to compile. Not all artifacts used by Companian are available in the
central Maven repository. Therefore, you have to install some of them by hand into your local
Maven repository. You can do this by executing the following scripts:

    $ cpanalyze/lib/install-libs.[cmd|sh]
    $ cpmodel/lib/install-libs.[cmd|sh]

After this step you can compile Companian by

    $ mvn clean install

If you want to develop in Eclipse, you should create the Eclipse projects by

    $ mvn eclipse:eclipse -Dwtpversion=2.0

Setup
-----

Companian uses a MySQL database to store its data. You have to setup a database in MySQL and
configure Companian accordingly to use that database.

[... more details to come ...]