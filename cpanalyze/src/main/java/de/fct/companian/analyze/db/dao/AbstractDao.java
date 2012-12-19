package de.fct.companian.analyze.db.dao;

import javax.sql.DataSource;

public class AbstractDao {

	protected final DataSource dataSource;
	
	public AbstractDao(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
