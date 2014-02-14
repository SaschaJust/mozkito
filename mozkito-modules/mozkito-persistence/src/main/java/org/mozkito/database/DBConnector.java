/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/

package org.mozkito.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * The Class DBConnector.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class DBConnector {
	
	/** The Constant JDBC_DRIVER. */
	private static final String JDBC_DRIVER = "org.postgresql.Driver";
	
	/** The connection. */
	private Connection          connection;
	
	static {
		try {
			Class.forName(JDBC_DRIVER);
		} catch (final ClassNotFoundException e) {
			throw new RuntimeException();
		}
	}
	
	/**
	 * Commit.
	 * 
	 * @param statements
	 *            the statements
	 * @throws SQLException
	 *             the sQL exception
	 */
	public void commit(final PreparedStatement... statements) throws SQLException {
		this.connection.setAutoCommit(false);
		for (final PreparedStatement statement : statements) {
			statement.executeUpdate();
		}
		this.connection.commit();
		this.connection.setAutoCommit(true);
	}
	
	/**
	 * Connect.
	 * 
	 * @param type
	 *            the type
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 * @param dbname
	 *            the dbname
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 */
	public void connect(final String type,
	                    final String host,
	                    final String port,
	                    final String dbname,
	                    final String username,
	                    final String password) {
		this.connection = null;
		
		try {
			
			this.connection = DriverManager.getConnection("jdbc:" + type + "://" + host + ":" + port + "/" + dbname,
			                                              username, port);
			
		} catch (final SQLException e) {
			
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * Prepare.
	 * 
	 * @param query
	 *            the query
	 * @param args
	 *            the args
	 * @return the prepared statement
	 * @throws SQLException
	 *             the sQL exception
	 */
	public PreparedStatement prepare(final String query,
	                                 final Object... args) throws SQLException {
		final PreparedStatement statement = this.connection.prepareStatement(query);
		
		for (int i = 1; i <= args.length; ++i) {
			if (Integer.class.isAssignableFrom(args[i - 1].getClass())) {
				statement.setInt(i, (Integer) args[i - 1]);
			} else if (String.class.isAssignableFrom(args[i - 1].getClass())) {
				statement.setString(i, (String) args[i - 1]);
			} else {
				throw new RuntimeException("Not yet implemented");
			}
		}
		
		return statement;
	}
}
