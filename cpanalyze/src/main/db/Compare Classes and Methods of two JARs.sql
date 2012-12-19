SELECT tblname, signature, fqcn FROM (

SELECT 'left' as tblname, M1.signature, C1.fqcn FROM cpanalyze.classes as C1
JOIN cpanalyze.methods as M1
ON C1.classId = M1.classId
WHERE ((C1.accessFlags & 0x0001) = 0x001)
  AND ((M1.accessFlags & 0x0001) = 0x0001 OR (M1.accessFlags & 0x0004) = 0x0004)
  AND C1.jarId = 24

UNION ALL

SELECT 'right' as tblname, M2.signature, C2.fqcn FROM cpanalyze.classes as C2
JOIN cpanalyze.methods as M2
ON C2.classId = M2.classId
WHERE ((C2.accessFlags & 0x0001) = 0x0001)
  AND ((M2.accessFlags & 0x0001) = 0x0001 OR (M2.accessFlags & 0x0004) = 0x0004)
  AND C2.jarId = 26

) AS alias_table
GROUP BY signature
HAVING count(*) = 1
ORDER BY fqcn;