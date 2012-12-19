/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package de.fct.companian.analyze.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import de.fct.companian.analyze.db.model.Method;

public class MethodDao extends AbstractDao {

    public MethodDao(DataSource dataSource) {
        super(dataSource);
    }

    private static Logger logger = Logger.getLogger(MethodDao.class);

    public void addMethod(int accessFlags,
                          boolean constructor,
                          String returnType,
                          String signature,
                          int classId) {
        if (signature.length() > 1000) {
            logger.warn("addMethod() signature has more than 1000 chars - skipping: classId=" + classId
                        + ", " + signature);
            return;
        }

        Connection con = null;
        PreparedStatement statement = null;
        try {
            String sql = "INSERT INTO methods (accessFlags, constructor, returnType, signature, classId) VALUES (?, ?, ?, ?, ?)";
            con = this.dataSource.getConnection();
            statement = con.prepareStatement(sql);
            statement.setInt(1, accessFlags);
            statement.setBoolean(2, constructor);
            statement.setString(3, returnType);
            statement.setString(4, signature);
            statement.setInt(5, classId);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("addMethod() error adding method", e);
        } finally {
            if (con != null) {
                try {
                    statement.close();
                } catch (SQLException e) {}
                try {
                    con.close();
                } catch (SQLException e) {}
            }
        }
    }

    public Method loadMethod(String signature, int classId) {
        Method method = null;

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT * FROM methods WHERE signature=? AND classId=? LIMIT 0,1";

            con = this.dataSource.getConnection();
            statement = con.prepareStatement(sql);
            statement.setString(1, signature);
            statement.setInt(2, classId);
            rs = statement.executeQuery();
            if (rs != null) {
                if (rs.next()) {
                    method = new Method();
                    method.setMethodId(rs.getInt("methodId"));
                    method.setAccessFlags(rs.getInt("accessFlags"));
                    method.setConstructor(rs.getBoolean("constructor"));
                    method.setReturnType(rs.getString("returnType"));
                    method.setSignature(rs.getString("signature"));
                    method.setClassId(rs.getInt("classId"));
                }
            }
        } catch (SQLException e) {
            logger.error("loadMethod() SQL error loading method", e);
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

        return method;
    }

    public List<Method> listMethodsOfJar(String signature, int jarId) {
        List<Method> methods = new ArrayList<Method>();

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT m.methodId, m.accessFlags, m.constructor, m.returnType, m.signature, m.classId FROM methods m JOIN classes c ON (m.classId = c.classId) JOIN jars j ON (c.jarId = j.jarId) WHERE m.signature=? AND j.jarId=?";

            con = this.dataSource.getConnection();
            statement = con.prepareStatement(sql);
            statement.setString(1, signature);
            statement.setInt(2, jarId);
            rs = statement.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    Method method = new Method();
                    method.setMethodId(rs.getInt("methodId"));
                    method.setAccessFlags(rs.getInt("accessFlags"));
                    method.setConstructor(rs.getBoolean("constructor"));
                    method.setReturnType(rs.getString("returnType"));
                    method.setSignature(rs.getString("signature"));
                    method.setClassId(rs.getInt("classId"));
                    methods.add(method);
                }
            }
        } catch (SQLException e) {
            logger.error("loadMethodsOfJar() SQL error loading method", e);
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

        return methods;
    }

    public List<Method> listMethodsOfClass(int classId) {
        List<Method> methods = new ArrayList<Method>();

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT * FROM methods WHERE classId=?";

            con = this.dataSource.getConnection();
            statement = con.prepareStatement(sql);
            statement.setInt(1, classId);
            rs = statement.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    Method method = new Method();
                    method.setMethodId(rs.getInt("methodId"));
                    method.setAccessFlags(rs.getInt("accessFlags"));
                    method.setConstructor(rs.getBoolean("constructor"));
                    method.setReturnType(rs.getString("returnType"));
                    method.setSignature(rs.getString("signature"));
                    method.setClassId(rs.getInt("classId"));

                    methods.add(method);
                }
            }
        } catch (SQLException e) {
            logger.error("loadMethod() SQL error loading method", e);
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

        return methods;
    }
}
