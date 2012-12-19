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
package de.fct.companian.analyze.helper;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;

public class DbHelper {
	
	private static Logger logger = Logger.getLogger(DbHelper.class);

	public static DataSource createDataSource(Properties properties) {
		String dbDriver;
		String dbUrl;
		String dbUser;
		String dbPass;
		String dbDriverDefault = "com.mysql.jdbc.Driver";
		String dbUrlDefault = "jdbc:mysql://localhost:3306/cpanalyze";
		String dbUserDefault = "cpanalyze";
		String dbPassDefault = "ezylanapc";

		if (properties != null) {
			dbDriver = properties.getProperty("database.driver");
			if (dbDriver == null) {
				dbDriver = dbDriverDefault;
			}

			dbUrl = properties.getProperty("database.url");
			if (dbUrl == null) {
				dbUrl = dbUrlDefault;
			}
			
			dbUser = properties.getProperty("database.username");
			if (dbUser == null) {
				dbUser = dbUserDefault;
			}
			
			dbPass = properties.getProperty("database.password");
			if (dbPass == null && dbUser.equals("cpanalyze")) {
				dbPass = dbPassDefault;
			}
		}
		else {
			dbDriver = dbDriverDefault;
			dbUrl = dbUrlDefault;
			dbUser = dbUserDefault;
			dbPass = dbPassDefault;
		}
		
        logger.debug("createDataSource() loading underlying JDBC driver.");
        try {
            Class.forName(dbDriver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        ObjectPool connectionPool = new GenericObjectPool(null);
        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(dbUrl, dbUser, dbPass);
        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory,connectionPool,null,null,false,true);
        PoolingDataSource ds = new PoolingDataSource(connectionPool);

        if (ds == null) {
        	logger.error("createDataSource() could not create data source");
        }
        
        return ds;
	}
}
