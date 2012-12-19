package de.fct.companian.analyze.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import de.fct.companian.analyze.db.model.Clazz;
import de.fct.companian.analyze.db.model.Iface;

public class ClassesDao extends AbstractDao {

	private static Logger logger = Logger.getLogger(ClassesDao.class);

	private final InterfacesDao interfacesDao;
	
	public ClassesDao(DataSource dataSource) {
		super(dataSource);
		this.interfacesDao = new InterfacesDao(dataSource);
	}

	public void saveClass(Clazz dbClass) {
		Connection con = null;
		PreparedStatement statement = null;
		try {
			String insertClass = "INSERT INTO classes (fqcn, accessFlags, superFqcn, jarId) VALUES (?, ?, ?, ?)";

			con = this.dataSource.getConnection();
			statement = con.prepareStatement(insertClass);
			statement.setString(1, dbClass.getFqcn());
			statement.setInt(2, dbClass.getAccessFlags());
			statement.setString(3, dbClass.getSuperFqcn());
			statement.setInt(4, dbClass.getJarId());
			if (logger.isDebugEnabled()) {
				logger.debug("saveClass() executing " + statement.toString());
			}
			statement.executeUpdate();
		}
		catch (SQLException e) {
			logger.error("saveClass() db error", e);
		}
		finally {
			if (statement != null)
				try {
					statement.close();
				}
				catch (SQLException e) {
				}
			if (con != null)
				try {
					con.close();
				}
				catch (SQLException e) {
				}
		}
		
		List<Iface> interfaces = dbClass.getInterfaces();
		dbClass = this.loadClass(dbClass.getFqcn(), dbClass.getJarId());
		dbClass.setInterfaces(interfaces);
		this.interfacesDao.saveInterfaces(dbClass);
	}
	
	public void updateClass(Clazz clazz) {
		Connection con = null;
		PreparedStatement statement = null;
		try {
			String sql = "UPDATE Classes SET fqcn=?, accessFlags=?, superFqcn=?, jarId=? WHERE classId=?";

			con = this.dataSource.getConnection();
			statement = con.prepareStatement(sql);
			statement.setInt(1, clazz.getClassId());
			statement.setInt(2, clazz.getAccessFlags());
			statement.setString(3, clazz.getFqcn());
			statement.setString(4, clazz.getSuperFqcn());
			statement.setInt(5, clazz.getJarId());
			statement.executeUpdate();
		}
		catch (SQLException e) {
			logger.error("updateClass() SQL error loading class", e);
		}
		finally {
			if (con != null) {
				if (statement != null) {
					try {
						statement.close();
					}
					catch (SQLException e) {
					}
				}

				try {
					con.close();
				}
				catch (SQLException e) {
				}
			}
		}
	}
	
    public Clazz loadClass(int classId) {
        Clazz dbClass = null;

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT * FROM classes WHERE classId=?";

            con = this.dataSource.getConnection();
            statement = con.prepareStatement(sql);
            statement.setInt(1, classId);
            rs = statement.executeQuery();
            if (rs != null) {
                if (rs.next()) {
                    dbClass = new Clazz();
                    dbClass.setClassId(rs.getInt("classId"));
                    dbClass.setFqcn(rs.getString("fqcn"));
                    dbClass.setJarId(rs.getInt("jarId"));
                    dbClass.setSuperFqcn(rs.getString("superFqcn"));
                }
            }
        }
        catch (SQLException e) {
            logger.error("listClasses() SQL error loading class", e);
        }
        finally {
            if (con != null) {
                if (statement != null) {
                    if (rs != null) {
                        try {
                            rs.close();
                        }
                        catch (SQLException e) {
                        }
                    }
                    
                    try {
                        statement.close();
                    }
                    catch (SQLException e) {
                    }
                }

                try {
                    con.close();
                }
                catch (SQLException e) {
                }
            }
        }

        return dbClass;        
    }
    
	public Clazz loadClass(String fqcn, int jarId) {
		Clazz dbClass = null;

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT * FROM classes WHERE fqcn=? AND jarId=? LIMIT 0,1";

			con = this.dataSource.getConnection();
			statement = con.prepareStatement(sql);
			statement.setString(1, fqcn);
			statement.setInt(2, jarId);
			rs = statement.executeQuery();
			if (rs != null) {
				if (rs.next()) {
					dbClass = new Clazz();
					dbClass.setClassId(rs.getInt("classId"));
					dbClass.setFqcn(rs.getString("fqcn"));
					dbClass.setJarId(rs.getInt("jarId"));
					dbClass.setSuperFqcn(rs.getString("superFqcn"));
					dbClass.setAccessFlags(rs.getInt("accessFlags"));
				}
			}
		}
		catch (SQLException e) {
			logger.error("loadClass() SQL error loading class", e);
		}
		finally {
			if (con != null) {
				if (statement != null) {
					if (rs != null) {
						try {
							rs.close();
						}
						catch (SQLException e) {
						}
					}
					
					try {
						statement.close();
					}
					catch (SQLException e) {
					}
				}

				try {
					con.close();
				}
				catch (SQLException e) {
				}
			}
		}
		
		if (dbClass != null) {
		    List<Iface> interfaces = this.interfacesDao.listInterfaces(dbClass);
		    dbClass.setInterfaces(interfaces);
		}
		return dbClass;
	}

	public List<Clazz> listClasses(String fqcn) {
		List<Clazz> classList = new ArrayList<Clazz>();

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT * FROM classes WHERE fqcn=?";

			con = this.dataSource.getConnection();
			statement = con.prepareStatement(sql);
			statement.setString(1, fqcn);
			rs = statement.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					Clazz dbClass = new Clazz();
					dbClass.setClassId(rs.getInt("classId"));
					dbClass.setFqcn(rs.getString("fqcn"));
					dbClass.setJarId(rs.getInt("jarId"));
					dbClass.setSuperFqcn(rs.getString("superFqcn"));
					dbClass.setAccessFlags(rs.getInt("accessFlags"));

					List<Iface> interfaces = this.interfacesDao.listInterfaces(dbClass);
					dbClass.setInterfaces(interfaces);
					
					classList.add(dbClass);
				}
			}
		}
		catch (SQLException e) {
			logger.error("listClasses() SQL error loading class", e);
		}
		finally {
			if (con != null) {
				if (statement != null) {
					if (rs != null) {
						try {
							rs.close();
						}
						catch (SQLException e) {
						}
					}
					
					try {
						statement.close();
					}
					catch (SQLException e) {
					}
				}

				try {
					con.close();
				}
				catch (SQLException e) {
				}
			}
		}

		return classList;
	}
	
	public List<Clazz> listClasses(int jarId) {
		List<Clazz> classList = new ArrayList<Clazz>();

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT * FROM classes WHERE jarId=?";

			con = this.dataSource.getConnection();
			statement = con.prepareStatement(sql);
			statement.setInt(1, jarId);
			rs = statement.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					Clazz dbClass = new Clazz();
					dbClass.setClassId(rs.getInt("classId"));
					dbClass.setFqcn(rs.getString("fqcn"));
					dbClass.setJarId(rs.getInt("jarId"));
					dbClass.setSuperFqcn(rs.getString("superFqcn"));
					
					classList.add(dbClass);
				}
			}
		}
		catch (SQLException e) {
			logger.error("listClasses() SQL error loading class", e);
		}
		finally {
			if (con != null) {
				if (statement != null) {
					if (rs != null) {
						try {
							rs.close();
						}
						catch (SQLException e) {
						}
					}
					
					try {
						statement.close();
					}
					catch (SQLException e) {
					}
				}

				try {
					con.close();
				}
				catch (SQLException e) {
				}
			}
		}

		return classList;
	}

    public List<Clazz> listClassesBySuperClass(String superFqcn, int jarId) {
        List<Clazz> classList = new ArrayList<Clazz>();

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT * FROM classes WHERE superFqcn=? AND jarId=?";

            con = this.dataSource.getConnection();
            statement = con.prepareStatement(sql);
            statement.setString(1, superFqcn);
            statement.setInt(2, jarId);
            rs = statement.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    Clazz dbClass = new Clazz();
                    dbClass.setClassId(rs.getInt("classId"));
                    dbClass.setFqcn(rs.getString("fqcn"));
                    dbClass.setJarId(rs.getInt("jarId"));
                    dbClass.setSuperFqcn(rs.getString("superFqcn"));
                    
                    classList.add(dbClass);
                }
            }
        }
        catch (SQLException e) {
            logger.error("listClassesBySuperClass() SQL error loading class", e);
        }
        finally {
            if (con != null) {
                if (statement != null) {
                    if (rs != null) {
                        try {
                            rs.close();
                        }
                        catch (SQLException e) {
                        }
                    }
                    
                    try {
                        statement.close();
                    }
                    catch (SQLException e) {
                    }
                }

                try {
                    con.close();
                }
                catch (SQLException e) {
                }
            }
        }

        return classList;
    }
	
}
