package de.fct.companian.analyze.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import de.fct.companian.analyze.db.model.ExtCall;

public class ExtCallDao extends AbstractDao {

	private static Logger logger = Logger.getLogger(ExtCallDao.class);
	
	public ExtCallDao(DataSource dataSource) {
		super(dataSource);
	}
	
	public void addExtCall(String extFqcn, String extSignature, Integer classId, Integer methodId) {
		String sql = "INSERT INTO extcalls (extFqcn, extSignature, classId, methodId) VALUES (?, ?, ?, ?)";
		
		PreparedStatement statement;
		try {
			Connection con = this.dataSource.getConnection();
			statement = con.prepareStatement(sql);
			statement.setString(1, extFqcn);
			statement.setString(2, extSignature);
			statement.setObject(3, classId);
			statement.setObject(4, methodId);
			statement.executeUpdate();
			statement.close();
			con.close();
		} catch (SQLException e) {
			logger.error("addExtCall() error adding external call", e);
		}
	}
	
	public ExtCall loadExtCall(String extFqcn, String extSignature, Integer classId, Integer methodId) {
		ExtCall extCall = null;
		
		String classIdPart;
		if (classId == null) {
			classIdPart = "classId is null";
		}
		else {
			classIdPart = "classId=?";
		}
		
		String methodIdPart;
		if (methodId == null) {
			methodIdPart = "methodId is null";
		}
		else {
			methodIdPart = "methodId=?";
		}
		
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT * FROM extcalls WHERE extFqcn=? AND extSignature=? AND " + classIdPart + " AND " + methodIdPart + " LIMIT 0,1";
			con = this.dataSource.getConnection();
			statement = con.prepareStatement(sql);
			statement.setString(1, extFqcn);
			statement.setString(2, extSignature);
			if (classId != null) {
				statement.setInt(3, classId);
			}
			else if (methodId != null) {
				statement.setInt(3, methodId);
			}
			if (logger.isDebugEnabled()) {
				logger.debug("loadExtCall() executing " + statement.toString());
			}
			rs = statement.executeQuery();
			if (rs != null) {
				if (rs.next()) {
					extCall = new ExtCall();
					extCall.setCallId(rs.getInt("callId"));
					extCall.setExtFqcn(rs.getString("extFqcn"));
					extCall.setExtSignature(rs.getString("extSignature"));
					extCall.setClassId(rs.getInt("classId"));
					extCall.setMethodId(rs.getInt("methodId"));
				}
			}
		} catch (SQLException e) {
			logger.error("addExtCall() error loading external call", e);
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
		
		return extCall;
	}
	
	public List<ExtCall> listExtCallsOfClass(int classId) {
		List<ExtCall> extCalls = new ArrayList<ExtCall>();
		
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT * FROM ExtCalls WHERE classId=?";
			con = this.dataSource.getConnection();
			statement = con.prepareStatement(sql);
			statement.setInt(1, classId);
			if (logger.isDebugEnabled()) {
				logger.debug("loadExtCall() executing " + statement.toString());
			}
			rs = statement.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					ExtCall extCall = new ExtCall();
					extCall.setCallId(rs.getInt("callId"));
					extCall.setExtFqcn(rs.getString("extFqcn"));
					extCall.setExtSignature(rs.getString("extSignature"));
					extCall.setClassId(rs.getInt("classId"));
					extCall.setMethodId(rs.getInt("methodId"));
					
					extCalls.add(extCall);
				}
			}
		} catch (SQLException e) {
			logger.error("addExtCall() error loading external call", e);
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
		
		return extCalls;
	}
	
	public List<ExtCall> listExtCallsOfMethod(int methodId) {
		List<ExtCall> extCalls = new ArrayList<ExtCall>();
		
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT * FROM ExtCalls WHERE methodId=?";
			con = this.dataSource.getConnection();
			statement = con.prepareStatement(sql);
			statement.setInt(1, methodId);
			if (logger.isDebugEnabled()) {
				logger.debug("loadExtCall() executing " + statement.toString());
			}
			rs = statement.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					ExtCall extCall = new ExtCall();
					extCall.setCallId(rs.getInt("callId"));
					extCall.setExtFqcn(rs.getString("extFqcn"));
					extCall.setExtSignature(rs.getString("extSignature"));
					extCall.setClassId(rs.getInt("classId"));
					extCall.setMethodId(rs.getInt("methodId"));
					
					extCalls.add(extCall);
				}
			}
		} catch (SQLException e) {
			logger.error("addExtCall() error loading external call", e);
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
		
		return extCalls;
	}
}
