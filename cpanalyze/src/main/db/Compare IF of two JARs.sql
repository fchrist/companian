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