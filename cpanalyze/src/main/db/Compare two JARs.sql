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

SELECT side, signature, mAccessFlags, fqcn, cAccessFlags FROM (

SELECT side, signature, mAccessFlags, fqcn, cAccessFlags FROM (

SELECT 'left' as side, '' as signature, 0 as mAccessFlags, C1.fqcn, C1.accessFlags as cAccessFlags FROM cpanalyze.classes as C1
WHERE ((C1.accessFlags & 0x0001) = 0x0001)
  AND C1.jarId = 25

UNION ALL

SELECT 'right' as side, '' as signature, 0 as mAccessFlags, C2.fqcn, C2.accessFlags as cAccessFlags FROM cpanalyze.classes as C2
WHERE ((C2.accessFlags & 0x0001) = 0x0001)
  AND C2.jarId = 26

) AS ccomp_table
GROUP BY fqcn
HAVING count(*) = 1

UNION ALL

SELECT side, signature, mAccessFlags, fqcn, cAccessFlags FROM (

SELECT 'left' as side, M1.signature, M1.accessFlags as mAccessFlags, C1.fqcn, C1.accessFlags as cAccessFlags FROM cpanalyze.classes as C1
JOIN cpanalyze.methods as M1
ON C1.classId = M1.classId
WHERE ((C1.accessFlags & 0x0001) = 0x001)
  AND ((M1.accessFlags & 0x0001) = 0x0001 OR (M1.accessFlags & 0x0004) = 0x0004)
  AND C1.jarId = 25

UNION ALL

SELECT 'right' as side, M2.signature, M2.accessFlags as mAccessFlags, C2.fqcn, C2.accessFlags as cAccessFlags FROM cpanalyze.classes as C2
JOIN cpanalyze.methods as M2
ON C2.classId = M2.classId
WHERE ((C2.accessFlags & 0x0001) = 0x0001)
  AND ((M2.accessFlags & 0x0001) = 0x0001 OR (M2.accessFlags & 0x0004) = 0x0004)
  AND C2.jarId = 26

) AS mcomp_table
GROUP BY signature
HAVING count(*) = 1
ORDER BY fqcn

) AS alias_table
ORDER BY fqcn;