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

SELECT tblname, signature, fqcn, cAccessFlags FROM (

SELECT 'left' as tblname, '' as signature, C1.fqcn, C1.accessFlags as cAccessFlags, C1.jarId FROM cpanalyze.classes as C1
WHERE ((C1.accessFlags & 0x0001) = 0x0001)
  AND C1.jarId = 25

UNION ALL

SELECT 'right' as tblname, '' as signature, C2.fqcn, C2.accessFlags as cAccessFlags, C2.jarId FROM cpanalyze.classes as C2
WHERE ((C2.accessFlags & 0x0001) = 0x0001)
  AND C2.jarId = 26

) AS alias_table
GROUP BY fqcn
HAVING count(*) = 1;