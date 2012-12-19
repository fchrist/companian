package de.fct.companian.analyze.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import de.fct.companian.analyze.db.model.ExtInstanceOf;

public class ExtInstanceOfDao extends AbstractDao {

	public ExtInstanceOfDao(DataSource dataSource) {
		super(dataSource);
	}

	private static Logger logger = Logger.getLogger(ExtInstanceOfDao.class);
	
	public void addExtInstanceOf(String extFqcn, int memberId) {
		String sql = "INSERT INTO extinstanceof (extFqcn, memberId) VALUES (?, ?)";
		PreparedStatement statement;
		try {
			Connection con = this.dataSource.getConnection();			
			statement = con.prepareStatement(sql);
			statement.setString(1, extFqcn);
			statement.setInt(2, memberId);
			if (logger.isDebugEnabled()) {
				logger.debug("addExtInstanceOf() executing " + statement.toString());
			}			
			statement.executeUpdate();
			statement.close();
			con.close();
		} catch (SQLException e) {
			logger.error("addExtInstanceOf() error adding external instanceof relation for extFqcn=" + extFqcn + ", memberId=" + memberId, e);
		}
	}

	public ExtInstanceOf loadExtInstanceOf(String extFqcn, int memberId) {
		ExtInstanceOf extInstanceOf = null;
		
		try {
			String sql = "SELECT * FROM extinstanceof WHERE extFqcn=? AND memberId=? LIMIT 0,1";
			
			Connection con = this.dataSource.getConnection();
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setString(1, extFqcn);
			statement.setInt(2, memberId);
			if (logger.isDebugEnabled()) {
				logger.debug("loadExtInstanceOf() executing " + statement.toString());
			}
			ResultSet rs = statement.executeQuery();
			if (rs != null) {
				if (rs.next()) {
					extInstanceOf = new ExtInstanceOf();
					extInstanceOf.setInstanceId(rs.getInt("instanceId"));
					extInstanceOf.setExtFqcn(rs.getString("extFqcn"));
					extInstanceOf.setMemberId(rs.getInt("memberId"));
				}
				rs.close();
				statement.close();
				con.close();
			}
		} catch (SQLException e) {
			logger.error("loadExtInstanceOf() error loading external instanceof", e);
		}
		
		return extInstanceOf;
	}
}
