SELECT p.name as 'product', j.artifact, j.version, c.fqcn, ei.extFqcn FROM products p
LEFT JOIN (jars j) ON (p.productId=j.productId)
LEFT JOIN (classes c) ON (j.jarId=c.jarId)
LEFT JOIN (extImplements ei) ON (c.classId=ei.classId)
WHERE  ei.extFqcn is not null
ORDER BY ei.extFqcn;