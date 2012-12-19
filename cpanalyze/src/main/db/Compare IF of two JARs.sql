-- Licensed to the Apache Software Foundation (ASF) under one or more
-- contributor license agreements.  See the NOTICE file distributed with
-- this work for additional information regarding copyright ownership.
-- The ASF licenses this file to You under the Apache License, Version 2.0
-- (the "License"); you may not use this file except in compliance with
-- the License.  You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.

SELECT tblname, fqcn FROM (

SELECT 'OldVersion' as tblname, C1.fqcn FROM cpanalyze.classes as C1
JOIN cpanalyze.methods as M1
ON C1.classId = M1.classId
WHERE ((C1.accessFlags & 0x0200) = 0x0200 OR (C1.accessFlags & 0x0400) = 0x0400)
  AND C1.jarId = 25

UNION ALL

SELECT 'NewVersion' as tblname, C2.fqcn FROM cpanalyze.classes as C2
JOIN cpanalyze.methods as M2
ON C2.classId = M2.classId
WHERE ((C2.accessFlags & 0x0200) = 0x0200 OR (C2.accessFlags & 0x0400) = 0x0400)
  AND C2.jarId = 24

) AS alias_table
GROUP BY fqcn
HAVING count(*) = 1;