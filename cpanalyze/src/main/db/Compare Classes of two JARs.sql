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