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

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import de.fct.companian.analyze.db.model.Member;

public class MemberDao extends AbstractDao {

	public MemberDao(DataSource dataSource) {
		super(dataSource);
	}

	private static Logger logger = Logger.getLogger(MemberDao.class);

	public void addMember(String signature, int classId) {
		String sql = "INSERT INTO members (signature, classId) VALUES (?, ?)";
		PreparedStatement statement;
		try {
			Connection con = this.dataSource.getConnection();			
			statement = con.prepareStatement(sql);
			statement.setString(1, signature);
			statement.setInt(2, classId);
			statement.executeUpdate();
			statement.close();
			con.close();
		} catch (SQLException e) {
			logger.error("addMember() error adding member", e);
		}
	}
	
	public Member loadMember(String signature, int classId) {
		Member member = null;
		
		try {
			String sql = "SELECT * FROM members WHERE signature=? AND classId=? LIMIT 0,1";
			
			Connection con = this.dataSource.getConnection();
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setString(1, signature);
			statement.setInt(2, classId);
			ResultSet rs = statement.executeQuery();
			if (rs != null) {
				if (rs.next()) {
					member = new Member();
					member.setMemberId(rs.getInt("memberId"));
					member.setSignature(rs.getString("signature"));
					member.setClassId(rs.getInt("classId"));
				}
				rs.close();
				statement.close();
				con.close();
			}
		} catch (SQLException e) {
			logger.error("loadMember() SQL error loading member", e);
		}
		
		return member;
	}

}
