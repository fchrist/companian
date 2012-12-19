package de.fct.companian.analyze.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import de.fct.companian.analyze.db.model.ExtLink;

public class ExtLinkDao extends AbstractDao {

	public ExtLinkDao(DataSource dataSource) {
		super(dataSource);
		// TODO Auto-generated constructor stub
	}

	private static Logger logger = Logger.getLogger(ExtLinkDao.class);
	
	public void addExtLink(String extFqcn, Integer classId, Integer methodId) {
		String sql = "INSERT INTO extlinks (extFqcn, classId, methodId) VALUES (?, ?, ?)";
		PreparedStatement statement;
		try {
			Connection con = this.dataSource.getConnection();
			statement = con.prepareStatement(sql);
			statement.setString(1, extFqcn);
			statement.setObject(2, classId);
			statement.setObject(3, methodId);
			if (logger.isDebugEnabled()) {
				logger.debug("addExtLink() executing " + statement.toString());
			}			
			statement.executeUpdate();
			statement.close();
			con.close();
		} catch (SQLException e) {
			logger.error("addExtLink() error adding external link", e);
		}
	}

	public ExtLink loadExtLink(String extFqcn, Integer classId, Integer methodId) {
		ExtLink extLink = null;
		
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
		
		String sql = "SELECT * FROM extlinks WHERE extFqcn=? AND " + classIdPart + " AND " + methodIdPart + " LIMIT 0,1";
		PreparedStatement statement;
		try {
			Connection con = this.dataSource.getConnection();
			statement = con.prepareStatement(sql);
			statement.setString(1, extFqcn);
			if (classId != null) {
				statement.setInt(2, classId);
			}
			else if (methodId != null) {
				statement.setInt(2, methodId);
			}
			if (logger.isDebugEnabled()) {
				logger.debug("loadExtLink() executing " + statement.toString());
			}
			ResultSet rs = statement.executeQuery();
			if (rs != null) {
				if (rs.next()) {
					extLink = new ExtLink();
					extLink.setLinkId(rs.getInt("linkId"));
					extLink.setExtFqcn(rs.getString("extFqcn"));
					extLink.setClassId(rs.getInt("classId"));
					extLink.setMethodId(rs.getInt("methodId"));
				}
				rs.close();
				con.close();
				statement.close();
			}
		} catch (SQLException e) {
			logger.error("loadExtLink() error loading external link", e);
		}
		
		return extLink;
	}
}
