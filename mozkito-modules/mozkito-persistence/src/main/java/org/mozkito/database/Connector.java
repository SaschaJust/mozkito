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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import net.ownhero.dev.kisa.Logger;

import org.mozkito.database.exceptions.DatabaseException;
import org.mozkito.database.exceptions.NoActiveTransactionException;
import org.mozkito.database.exceptions.TransactionAlreadyActiveException;

/**
 * The Class DBConnector.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class Connector {
	
	/** The Constant JDBC_DRIVER. */
	private static final String JDBC_DRIVER       = "org.postgresql.Driver";
	
	/** The connection. */
	private Connection          connection        = null;
	
	/** The active transaction. */
	private boolean             activeTransaction = false;
	
	static {
		try {
			Class.forName(JDBC_DRIVER);
		} catch (final ClassNotFoundException e) {
			throw new RuntimeException();
		}
	}
	
	/**
	 * Instantiates a new dB connector.
	 * 
	 * @param environment
	 *            the environment
	 * @throws DatabaseException
	 *             the database exception
	 */
	public Connector(final DatabaseEnvironment environment) throws DatabaseException {
		PRECONDITIONS: {
			if (environment == null) {
				throw new NullPointerException();
			}
		}
		
		// body
		
		try {
			Class.forName(environment.getDatabaseDriver());
		} catch (final ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		
		final Properties connectionProps = new Properties();
		connectionProps.put("user", environment.getDatabaseUsername());
		connectionProps.put("password", environment.getDatabasePassword());
		try {
			this.connection = DriverManager.getConnection(environment.getUrl(), connectionProps);
		} catch (final SQLException e1) {
			throw new DatabaseException("Could not establish a connection to the database.", e1);
		}
		
		try {
			this.connection.setAutoCommit(false);
		} catch (final SQLException e) {
			throw new DatabaseException("SQLException when trying to set AUTO_COMMIT to false.", e);
		}
		
		assert this.connection != null;
	}
	
	/**
	 * Active transaction.
	 * 
	 * @return true, if successful
	 */
	public boolean activeTransaction() {
		assert this.connection != null;
		return this.activeTransaction;
	}
	
	/**
	 * Begin transaction.
	 * 
	 * @throws DatabaseException
	 *             the database exception
	 */
	public void beginTransaction() throws DatabaseException {
		PRECONDITIONS: {
			if (activeTransaction()) {
				throw new TransactionAlreadyActiveException();
			}
		}
		
		assert this.connection != null;
		
		this.activeTransaction = true;
		assert activeTransaction();
		
	}
	
	/**
	 * Close.
	 * 
	 * @throws DatabaseException
	 *             the database exception
	 */
	public void close() throws DatabaseException {
		assert this.connection != null;
		
		try {
			if (this.connection.isClosed()) {
				if (Logger.logWarn()) {
					Logger.warn("Connection already closed.");
				}
				return;
			}
		} catch (final SQLException e2) {
			throw new DatabaseException("Caught SLQException when asking if connection is closed.", e2);
		}
		
		if (activeTransaction()) {
			try {
				commit();
			} catch (final DatabaseException e) {
				try {
					this.connection.close();
				} catch (final SQLException e1) {
					throw new DatabaseException(
					                            "Failed to close connection after unsuccessful attempt to commit active transaction.",
					                            e1, e);
				}
			}
		} else {
			try {
				this.connection.close();
			} catch (final SQLException e) {
				throw new DatabaseException("Could not close the database connection.", e);
			}
		}
	}
	
	/**
	 * Commit.
	 * 
	 * @throws DatabaseException
	 *             the database exception
	 */
	public void commit() throws DatabaseException {
		PRECONDITIONS: {
			if (!activeTransaction()) {
				throw new NoActiveTransactionException();
			}
		}
		
		assert this.connection != null;
		
		try {
			this.connection.commit();
		} catch (final SQLException e) {
			try {
				this.connection.rollback();
			} catch (final SQLException e1) {
				throw new DatabaseException(
				                            "Could not commit active transaction and rollback to last savepoint failed.",
				                            e1, e);
			}
			throw new DatabaseException("Could not commit active transaction. Executed rollback to last savepoint.", e);
		}
		
		this.activeTransaction = false;
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
		for (final PreparedStatement statement : statements) {
			statement.executeUpdate();
		}
		this.connection.commit();
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
	 * @throws DatabaseException
	 *             the database exception
	 */
	public void connect(final String type,
	                    final String host,
	                    final String port,
	                    final String dbname,
	                    final String username,
	                    final String password) throws DatabaseException {
		PRECONDITIONS: {
			if (this.connection != null) {
				throw new DatabaseException("Connection already active.");
			}
		}
		
		assert this.connection == null;
		
		try {
			// TODO this needs to be patched up
			this.connection = DriverManager.getConnection("jdbc:" + type + "://" + host + ":" + port + "/" + dbname,
			                                              username, port);
			this.connection.setAutoCommit(false);
			
		} catch (final SQLException e) {
			throw new DatabaseException("Could not connect to the database.", e);
		}
	}
	
	/**
	 * Creates the statement.
	 * 
	 * @return the statement
	 * @throws SQLException
	 *             the SQL exception
	 */
	public Statement createStatement() throws SQLException {
		return this.connection.createStatement();
	}
	
	/**
	 * Execute query.
	 * 
	 * @param string
	 *            the string
	 * @return the result set
	 * @throws DatabaseException
	 *             the database exception
	 */
	public ResultSet executeQuery(final String string) throws DatabaseException {
		PRECONDITIONS: {
			if (string == null) {
				throw new NullPointerException("Query must not be null.");
			}
			
			if (this.connection == null) {
				throw new DatabaseException("No active connection.");
			}
			
			if (activeTransaction()) {
				throw new DatabaseException(
				                            "We currently do not allow interleaving of statements and query execution during active transactions. Please commit your active transaction first.");
			}
		}
		
		assert this.connection != null;
		assert !activeTransaction();
		
		Statement statement;
		
		try {
			statement = this.connection.createStatement();
		} catch (final SQLException e) {
			throw new DatabaseException("Statement creation failed.", e);
		}
		
		assert statement != null;
		
		try {
			final ResultSet resultSet = statement.executeQuery(string);
			assert resultSet != null;
			return resultSet;
		} catch (final SQLException e) {
			throw new DatabaseException("Query execution failed for query: " + string, e);
		}
		
	}
	
	/**
	 * Execute statement.
	 * 
	 * @param statement
	 *            the statement
	 * @return the int
	 * @throws DatabaseException
	 *             the database exception
	 */
	public int executeStatement(final String statement) throws DatabaseException {
		PRECONDITIONS: {
			if (statement == null) {
				throw new NullPointerException();
			}
			
			if (this.connection == null) {
				throw new DatabaseException("No active connection.");
			}
			
			if (activeTransaction()) {
				throw new DatabaseException(
				                            "We currently do not allow interleaving of statements and query execution during active transactions. Please commit your active transaction first.");
			}
		}
		
		assert this.connection != null;
		assert !activeTransaction();
		
		Statement theStatement;
		
		try {
			theStatement = this.connection.createStatement();
		} catch (final SQLException e) {
			throw new DatabaseException("Statement creation failed.", e);
		}
		
		assert theStatement != null;
		
		try {
			final int returnCode = theStatement.executeUpdate(statement);
			this.connection.commit();
			return returnCode;
		} catch (final SQLException e) {
			throw new DatabaseException("Query execution failed for query: " + statement, e);
		}
	}
	
	/**
	 * Prepare.
	 * 
	 * @param query
	 *            the query
	 * @return the prepared statement
	 * @throws SQLException
	 *             the sQL exception
	 */
	public PreparedStatement prepare(final String query) throws SQLException {
		final PreparedStatement statement = this.connection.prepareStatement(query);
		return statement;
	}
	
	/**
	 * Rollback.
	 * 
	 * @throws DatabaseException
	 *             the database exception
	 */
	public void rollback() throws DatabaseException {
		PRECONDITIONS: {
			if (!activeTransaction()) {
				throw new NoActiveTransactionException();
			}
		}
		
		assert this.connection != null;
		
		try {
			this.connection.rollback();
		} catch (final SQLException e) {
			throw new DatabaseException("Could not rollback to last savepoint.", e);
		}
		
		this.activeTransaction = false;
	}
	
}
