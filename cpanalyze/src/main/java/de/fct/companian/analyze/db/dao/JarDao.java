package de.fct.companian.analyze.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import de.fct.companian.analyze.db.model.JarCompareResult;
import de.fct.companian.analyze.db.model.Jar;
import de.fct.companian.analyze.db.model.Product;

public class JarDao extends AbstractDao {

    private static Logger logger = Logger.getLogger(JarDao.class);

    public JarDao(DataSource dataSource) {
        super(dataSource);
    }

    public List<JarCompareResult> compare(int leftJarId, int rightJarId) {
        if (logger.isDebugEnabled()) {
            logger.debug("compare() of left " + leftJarId + " with right " + rightJarId);
        }

        List<JarCompareResult> resultList = new ArrayList<JarCompareResult>();

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT side, signature, mAccessFlags, fqcn, cAccessFlags FROM (  SELECT side, signature, mAccessFlags, fqcn, cAccessFlags FROM (  SELECT 'left' as side, '' as signature, 0 as mAccessFlags, C1.fqcn, C1.accessFlags as cAccessFlags FROM cpanalyze.classes as C1 WHERE ((C1.accessFlags & 0x0001) = 0x0001)   AND C1.jarId = ?  UNION ALL  SELECT 'right' as side, '' as signature, 0 as mAccessFlags, C2.fqcn, C2.accessFlags as cAccessFlags FROM cpanalyze.classes as C2 WHERE ((C2.accessFlags & 0x0001) = 0x0001)   AND C2.jarId = ?  ) AS ccomp_table GROUP BY fqcn HAVING count(*) = 1  UNION ALL  SELECT side, signature, mAccessFlags, fqcn, cAccessFlags FROM (  SELECT 'left' as side, M1.signature, M1.accessFlags as mAccessFlags, C1.fqcn, C1.accessFlags as cAccessFlags FROM cpanalyze.classes as C1 JOIN cpanalyze.methods as M1 ON C1.classId = M1.classId WHERE ((C1.accessFlags & 0x0001) = 0x001)   AND ((M1.accessFlags & 0x0001) = 0x0001 OR (M1.accessFlags & 0x0004) = 0x0004)   AND C1.jarId = ?  UNION ALL  SELECT 'right' as side, M2.signature, M2.accessFlags as mAccessFlags, C2.fqcn, C2.accessFlags as cAccessFlags FROM cpanalyze.classes as C2 JOIN cpanalyze.methods as M2 ON C2.classId = M2.classId WHERE ((C2.accessFlags & 0x0001) = 0x0001)   AND ((M2.accessFlags & 0x0001) = 0x0001 OR (M2.accessFlags & 0x0004) = 0x0004)   AND C2.jarId = ?  ) AS mcomp_table GROUP BY signature HAVING count(*) = 1 ORDER BY fqcn  ) AS alias_table ORDER BY fqcn;";

            con = this.dataSource.getConnection();
            statement = con.prepareStatement(sql);
            statement.setInt(1, leftJarId);
            statement.setInt(2, rightJarId);
            statement.setInt(3, leftJarId);
            statement.setInt(4, rightJarId);
            rs = statement.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    JarCompareResult result = new JarCompareResult();
                    result.setSide(rs.getString("side"));
                    result.setMethodSignature(rs.getString("signature"));
                    result.setMethodAccessFlags(rs.getInt("mAccessFlags"));
                    result.setFqcn(rs.getString("fqcn"));
                    result.setClassAccessFlags(rs.getInt("cAccessFlags"));
                    resultList.add(result);
                }
                rs.close();
                statement.close();
                con.close();
            }
        } catch (SQLException e) {
            logger.error("compare() SQL error loading JAR", e);
        } finally {
            if (con != null) {
                if (statement != null) {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException e) {}
                    }

                    try {
                        statement.close();
                    } catch (SQLException e) {}
                }

                try {
                    con.close();
                } catch (SQLException e) {}
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("compare() returning " + resultList.size() + " results");
        }
        return resultList;
    }
    
    public List<Jar> listAllJars() {
        List<Jar> jars = new ArrayList<Jar>();

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT j.jarId, j.jarname, j.artifact, j.version, p.productId, p.name, p.category FROM products p "
                         + "LEFT JOIN (jars j) ON (p.productId = j.productId) ORDER BY p.name, j.artifact, j.version";

            con = this.dataSource.getConnection();
            statement = con.prepareStatement(sql);
            rs = statement.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    Jar jar = new Jar();
                    jar.setJarId(rs.getInt("jarId"));
                    jar.setJarname(rs.getString("jarname"));
                    jar.setArtifact(rs.getString("artifact"));
                    jar.setVersion(rs.getString("version"));

                    Product product = new Product();
                    product.setProductId(rs.getInt("productId"));
                    product.setName(rs.getString("name"));
                    product.setCategory(rs.getString("category"));
                    jar.setProduct(product);

                    jars.add(jar);
                }
                rs.close();
                statement.close();
                con.close();
            }
        } catch (SQLException e) {
            logger.error("listAllJars() SQL error loading JARs", e);
        } finally {
            if (con != null) {
                if (statement != null) {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException e) {}
                    }

                    try {
                        statement.close();
                    } catch (SQLException e) {}
                }

                try {
                    con.close();
                } catch (SQLException e) {}
            }
        }

        return jars;
    }

    public List<Jar> listJarsForExtCall(String fqcn, String signature) {
        List<Jar> jars = new ArrayList<Jar>();

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT j.jarId, j.jarname, j.artifact, j.version, p.productId, p.name, p.category FROM products p "
                         + "LEFT JOIN (jars j, classes c, methods m) ON (p.productId = j.productId AND j.jarId = c.jarId AND c.classId = m.classId) "
                         + "WHERE c.fqcn=? AND m.signature=?";

            con = this.dataSource.getConnection();
            statement = con.prepareStatement(sql);
            statement.setString(1, fqcn);
            statement.setString(2, signature);
            rs = statement.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    Jar jar = new Jar();
                    jar.setJarId(rs.getInt("jarId"));
                    jar.setJarname(rs.getString("jarname"));
                    jar.setArtifact(rs.getString("artifact"));
                    jar.setVersion(rs.getString("version"));

                    Product product = new Product();
                    product.setProductId(rs.getInt("productId"));
                    product.setName(rs.getString("name"));
                    product.setCategory(rs.getString("category"));
                    jar.setProduct(product);

                    jars.add(jar);
                }
                rs.close();
                statement.close();
                con.close();
            }
        } catch (SQLException e) {
            logger.error("listJarsForExtCall() SQL error loading JAR", e);
        } finally {
            if (con != null) {
                if (statement != null) {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException e) {}
                    }

                    try {
                        statement.close();
                    } catch (SQLException e) {}
                }

                try {
                    con.close();
                } catch (SQLException e) {}
            }
        }

        return jars;
    }
    
    public List<Jar> listJarsForProduct(int productId) {
        if (logger.isDebugEnabled()) {
            logger.debug("listJarsForProduct() for product " + productId);
        }
        List<Jar> jars = new ArrayList<Jar>();

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT j.jarId, j.jarname, j.artifact, j.version, p.productId, p.name, p.category FROM jars j "
                         + "LEFT JOIN (products p) ON (p.productId = j.productId) "
                         + "WHERE p.productId=? ORDER BY j.jarname";

            con = this.dataSource.getConnection();
            statement = con.prepareStatement(sql);
            statement.setInt(1, productId);
            rs = statement.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    Jar jar = new Jar();
                    jar.setJarId(rs.getInt("jarId"));
                    jar.setJarname(rs.getString("jarname"));
                    jar.setArtifact(rs.getString("artifact"));
                    jar.setVersion(rs.getString("version"));

                    Product product = new Product();
                    product.setProductId(rs.getInt("productId"));
                    product.setName(rs.getString("name"));
                    product.setCategory(rs.getString("category"));
                    jar.setProduct(product);

                    jars.add(jar);
                }
                rs.close();
                statement.close();
                con.close();
            }
        } catch (SQLException e) {
            logger.error("listJarsForProduct() SQL error loading JAR", e);
        } finally {
            if (con != null) {
                if (statement != null) {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException e) {}
                    }

                    try {
                        statement.close();
                    } catch (SQLException e) {}
                }

                try {
                    con.close();
                } catch (SQLException e) {}
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("listJarsForProduct() returning " + jars.size() + " JARs");
        }
        return jars;
    }

    public List<Jar> listOtherVersions(Jar jar) {
        if (logger.isDebugEnabled()) {
            logger.debug("listOtherVersions() of JAR " + jar.getArtifact() + " in version "
                         + jar.getVersion() + " of product " + jar.getProduct().getName());
        }
        List<Jar> jars = new ArrayList<Jar>();

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT j.jarId, j.jarname, j.artifact, j.version, p.productId, p.name, p.category FROM jars j "
                         + "LEFT JOIN (products p) ON (p.productId = j.productId) "
                         + "WHERE j.productId=? AND j.artifact=? AND j.version<>? ORDER BY j.jarname";

            con = this.dataSource.getConnection();
            statement = con.prepareStatement(sql);
            statement.setInt(1, jar.getProduct().getProductId());
            statement.setString(2, jar.getArtifact());
            statement.setString(3, jar.getVersion());
            rs = statement.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    Jar j = new Jar();
                    j.setJarId(rs.getInt("jarId"));
                    j.setJarname(rs.getString("jarname"));
                    j.setArtifact(rs.getString("artifact"));
                    j.setVersion(rs.getString("version"));

                    Product product = new Product();
                    product.setProductId(rs.getInt("productId"));
                    product.setName(rs.getString("name"));
                    product.setCategory(rs.getString("category"));
                    j.setProduct(product);

                    jars.add(j);
                }
                rs.close();
                statement.close();
                con.close();
            }
        } catch (SQLException e) {
            logger.error("listOtherVersions() SQL error loading JAR", e);
        } finally {
            if (con != null) {
                if (statement != null) {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException e) {}
                    }

                    try {
                        statement.close();
                    } catch (SQLException e) {}
                }

                try {
                    con.close();
                } catch (SQLException e) {}
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("listOtherVersions() returning " + jars.size() + " JARs");
        }
        return jars;
    }

    public String loadAPIDoc(int jarId) {
        if (logger.isDebugEnabled()) {
            logger.debug("loadAPIDoc() for JAR " + jarId);
        }
        String apidoc = null;

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT j.apidoc " + "FROM jars j WHERE j.jarId=? LIMIT 0,1";

            con = this.dataSource.getConnection();
            statement = con.prepareStatement(sql);
            statement.setInt(1, jarId);
            rs = statement.executeQuery();
            if (rs != null) {
                if (rs.next()) {
                    apidoc = rs.getString("apidoc");
                }
                rs.close();
                statement.close();
                con.close();
            }
        } catch (SQLException e) {
            logger.error("loadAPIDoc() SQL error loading JAR", e);
        } finally {
            if (con != null) {
                if (statement != null) {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException e) {}
                    }

                    try {
                        statement.close();
                    } catch (SQLException e) {}
                }

                try {
                    con.close();
                } catch (SQLException e) {}
            }
        }

        return apidoc;
    }

    public String loadFDMMDoc(int jarId) {
        if (logger.isDebugEnabled()) {
            logger.debug("loadFDMMDoc() for JAR " + jarId);
        }
        String fdmmdoc = null;

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT j.fdmmdoc " + "FROM jars j WHERE j.jarId=? LIMIT 0,1";

            con = this.dataSource.getConnection();
            statement = con.prepareStatement(sql);
            statement.setInt(1, jarId);
            rs = statement.executeQuery();
            if (rs != null) {
                if (rs.next()) {
                    fdmmdoc = rs.getString("fdmmdoc");
                }
                rs.close();
                statement.close();
                con.close();
            }
        } catch (SQLException e) {
            logger.error("loadFDMMDoc() SQL error loading JAR", e);
        } finally {
            if (con != null) {
                if (statement != null) {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException e) {}
                    }

                    try {
                        statement.close();
                    } catch (SQLException e) {}
                }

                try {
                    con.close();
                } catch (SQLException e) {}
            }
        }

        return fdmmdoc;
    }

    public Jar loadJar(int jarId) {
        Jar jar = null;

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT j.jarId, j.jarname, j.artifact, j.version, p.productId, p.name, p.category "
                         + "FROM jars j LEFT JOIN products p ON (j.productId = p.productId) "
                         + "WHERE j.jarId=? LIMIT 0,1";

            con = this.dataSource.getConnection();
            statement = con.prepareStatement(sql);
            statement.setInt(1, jarId);
            rs = statement.executeQuery();
            if (rs != null) {
                if (rs.next()) {
                    jar = new Jar();
                    jar.setJarId(rs.getInt("jarId"));
                    jar.setJarname(rs.getString("jarname"));
                    jar.setArtifact(rs.getString("artifact"));
                    jar.setVersion(rs.getString("version"));

                    Product product = new Product();
                    product.setProductId(rs.getInt("productId"));
                    product.setName(rs.getString("name"));
                    product.setCategory(rs.getString("category"));
                    jar.setProduct(product);
                }
                rs.close();
                statement.close();
                con.close();
            }
        } catch (SQLException e) {
            logger.error("loadJar() SQL error loading JAR", e);
        } finally {
            if (con != null) {
                if (statement != null) {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException e) {}
                    }

                    try {
                        statement.close();
                    } catch (SQLException e) {}
                }

                try {
                    con.close();
                } catch (SQLException e) {}
            }
        }

        return jar;
    }
    
    public Jar loadJar(String artifact, String version, int productId) {
        Jar jar = null;

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT j.jarId, j.jarname, j.artifact, j.version, p.productId, p.name, p.category "
                         + "FROM jars j LEFT JOIN products p ON (j.productId = p.productId) "
                         + "WHERE j.artifact=? AND j.version=? AND j.productId=? LIMIT 0,1";

            con = this.dataSource.getConnection();
            statement = con.prepareStatement(sql);
            statement.setString(1, artifact);
            statement.setString(2, version);
            statement.setInt(3, productId);
            rs = statement.executeQuery();
            if (rs != null) {
                if (rs.next()) {
                    jar = new Jar();
                    jar.setJarId(rs.getInt("jarId"));
                    jar.setJarname(rs.getString("jarname"));
                    jar.setArtifact(rs.getString("artifact"));
                    jar.setVersion(rs.getString("version"));

                    Product product = new Product();
                    product.setProductId(rs.getInt("productId"));
                    product.setName(rs.getString("name"));
                    product.setCategory(rs.getString("category"));
                    jar.setProduct(product);
                }
                rs.close();
                statement.close();
                con.close();
            }
        } catch (SQLException e) {
            logger.error("loadJar() SQL error loading JAR", e);
        } finally {
            if (con != null) {
                if (statement != null) {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException e) {}
                    }

                    try {
                        statement.close();
                    } catch (SQLException e) {}
                }

                try {
                    con.close();
                } catch (SQLException e) {}
            }
        }

        return jar;
    }

    public Jar loadJar(String artifact, String version, String productName) {
        Jar jar = null;

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT j.jarId, j.jarname, j.artifact, j.version, p.productId, p.name, p.category "
                         + "FROM jars j LEFT JOIN products p ON (j.productId = p.productId) "
                         + "WHERE j.artifact=? AND j.version=? AND p.name=? LIMIT 0,1";

            con = this.dataSource.getConnection();
            statement = con.prepareStatement(sql);
            statement.setString(1, artifact);
            statement.setString(2, version);
            statement.setString(3, productName);
            rs = statement.executeQuery();
            if (rs != null) {
                if (rs.next()) {
                    jar = new Jar();
                    jar.setJarId(rs.getInt("jarId"));
                    jar.setJarname(rs.getString("jarname"));
                    jar.setArtifact(rs.getString("artifact"));
                    jar.setVersion(rs.getString("version"));

                    Product product = new Product();
                    product.setProductId(rs.getInt("productId"));
                    product.setName(rs.getString("name"));
                    product.setCategory(rs.getString("category"));
                    jar.setProduct(product);
                }
                rs.close();
                statement.close();
                con.close();
            }
        } catch (SQLException e) {
            logger.error("loadJar() SQL error loading JAR", e);
        } finally {
            if (con != null) {
                if (statement != null) {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException e) {}
                    }

                    try {
                        statement.close();
                    } catch (SQLException e) {}
                }

                try {
                    con.close();
                } catch (SQLException e) {}
            }
        }

        return jar;
    }
    
    public Jar saveJar(Jar jar) throws Exception {
        Jar insertedJar = null;
        if (jar != null && jar.getProduct() != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("saveJar() inserting JAR " + jar.getJarname() + ", productId="
                            + jar.getProduct().getProductId());
            }
            Connection con = null;
            PreparedStatement statement = null;
            try {
                String insertJar = "INSERT INTO jars (jarname, artifact, version, productId) VALUES ( ?, ?, ?, ?)";

                con = this.dataSource.getConnection();
                statement = con.prepareStatement(insertJar);
                statement.setString(1, jar.getJarname());
                statement.setString(2, jar.getArtifact());
                statement.setString(3, jar.getVersion());
                statement.setInt(4, jar.getProduct().getProductId());
                if (logger.isDebugEnabled()) {
                    logger.debug("updateJars() executing " + statement.toString());
                }
                statement.executeUpdate();
                ResultSet keySet = statement.getGeneratedKeys();
                if (keySet.next()) {
                    int jarId = keySet.getInt(1);
                    insertedJar = this.loadJar(jarId);
                }
            } catch (SQLException e) {
                logger.error("updateClasses() db error", e);
                throw e;
            } finally {
                if (statement != null) try {
                    statement.close();
                } catch (SQLException e) {}
                if (con != null) try {
                    con.close();
                } catch (SQLException e) {}
            }
        } else {
            throw new Exception("No JAR and/or now Product for JAR given.");
        }

        return insertedJar;
    }

    public void updateApidoc(int jarId, String apidoc) throws Exception {
        if (logger.isInfoEnabled()) {
            logger.info("updateApidoc() for JAR " + jarId);
        }
        Connection con = null;
        PreparedStatement statement = null;
        try {
            String insertJar = "UPDATE jars j SET j.apidoc=? WHERE j.jarId=?";

            con = this.dataSource.getConnection();
            statement = con.prepareStatement(insertJar);
            statement.setString(1, apidoc);
            statement.setInt(2, jarId);
            if (logger.isDebugEnabled()) {
                logger.debug("updateApidoc() executing " + statement.toString());
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("updateApidoc() db error", e);
            throw e;
        } finally {
            if (statement != null) try {
                statement.close();
            } catch (SQLException e) {}
            if (con != null) try {
                con.close();
            } catch (SQLException e) {}
        }
    }

    public void updateFdmmdoc(int jarId, String fdmmdoc) throws Exception {
        if (logger.isInfoEnabled()) {
            logger.info("updateFdmmdoc() for JAR " + jarId);
        }
        Connection con = null;
        PreparedStatement statement = null;
        try {
            String insertJar = "UPDATE jars j SET j.fdmmdoc=? WHERE j.jarId=?";

            con = this.dataSource.getConnection();
            statement = con.prepareStatement(insertJar);
            statement.setString(1, fdmmdoc);
            statement.setInt(2, jarId);
            if (logger.isDebugEnabled()) {
                logger.debug("updateFdmmdoc() executing " + statement.toString());
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("updateFdmmdoc() db error", e);
            throw e;
        } finally {
            if (statement != null) try {
                statement.close();
            } catch (SQLException e) {}
            if (con != null) try {
                con.close();
            } catch (SQLException e) {}
        }
    }

}
