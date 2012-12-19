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

public class InterfacesDao extends AbstractDao {

	private static Logger logger = Logger.getLogger(InterfacesDao.class);

	public InterfacesDao(DataSource dataSource) {
		super(dataSource);
	}

	public List<Iface> listInterfaces(Clazz dbClass) {
		List<Iface> ifList = new ArrayList<Iface>();

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT * FROM interfaces WHERE classId=?";

			con = this.dataSource.getConnection();
			statement = con.prepareStatement(sql);
			statement.setInt(1, dbClass.getClassId());
			rs = statement.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					Iface iface = new Iface();
					iface.setFqin(rs.getString("fqin"));

					ifList.add(iface);
				}
			}
		}
		catch (SQLException e) {
			logger.error("listInterfaces() SQL error loading interfaces", e);
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

		return ifList;
	}

	public void saveInterfaces(Clazz dbClass) {
		if (dbClass.getInterfaces() != null) {
			Connection con = null;
			PreparedStatement statement = null;
			try {
				con = this.dataSource.getConnection();
				for (Iface ifa : dbClass.getInterfaces()) {
					String insertClass = "INSERT INTO interfaces (fqin, classId) VALUES (?, ?)";

					statement = con.prepareStatement(insertClass);
					statement.setString(1, ifa.getFqin());
					statement.setInt(2, dbClass.getClassId());
					if (logger.isDebugEnabled()) {
						logger.debug("saveInterfaces() executing " + statement.toString());
					}
					statement.executeUpdate();
				}
			}
			catch (SQLException e) {
				logger.error("saveInterfaces() db error", e);
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
		}
	}
}
