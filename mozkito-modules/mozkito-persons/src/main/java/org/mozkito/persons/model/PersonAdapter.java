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

package org.mozkito.persons.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;

import org.mozkito.database.EntityAdapter;
import org.mozkito.database.QueryPool;
import org.mozkito.database.exceptions.DatabaseException;
import org.mozkito.database.model.Table;
import org.mozkito.database.types.Type;
import org.mozkito.persons.elements.PersonFactory;

/**
 * The Class DBQueryPerson.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class PersonAdapter extends EntityAdapter<Person> {
	
	/** The person factory. */
	private PersonFactory personFactory;
	private Table         mainTable;
	private Table         userTable;
	private Table         emailTable;
	private Table         fullnameTable;
	
	/**
	 * Instantiates a new dB query person.
	 * 
	 * @param queryPool
	 *            the d b query pool
	 * @param personFactory
	 *            the person factory
	 */
	public PersonAdapter(final QueryPool queryPool, final PersonFactory personFactory) {
		super(queryPool);
		PRECONDITIONS: {
			if (queryPool == null) {
				throw new NullPointerException();
			}
			
			if (personFactory == null) {
				throw new NullPointerException();
			}
		}
		
		try {
			// body
			this.personFactory = personFactory;
			
			SANITY: {
				assert Person.LAYOUT != null;
			}
			
			this.mainTable = Person.LAYOUT.getMainTable();
			this.userTable = Person.LAYOUT.getTable("person_usernames");
			this.emailTable = Person.LAYOUT.getTable("person_emails");
			this.fullnameTable = Person.LAYOUT.getTable("person_fullnames");
		} finally {
			POSTCONDITIONS: {
				assert this.personFactory != null;
				assert this.mainTable != null;
				assert this.userTable != null;
				assert this.emailTable != null;
				assert this.fullnameTable != null;
			}
		}
	}
	
	/**
	 * Creates the instance.
	 * 
	 * @param id
	 *            the id
	 * @return the test person
	 */
	private Person createInstance(final long id) {
		new PersonFactory();
		
		@SuppressWarnings ("deprecation")
		final Person person = new Person();
		
		person.setId(id);
		
		person.setEmailAddresses(new HashSet<String>());
		person.setUsernames(new HashSet<String>());
		person.setFullnames(new HashSet<String>());
		
		return person;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws DatabaseException
	 * @see org.mozkito.database.EntityAdapter#getId(java.sql.ResultSet)
	 */
	@Override
	public Object getId(final ResultSet idSet) throws DatabaseException {
		PRECONDITIONS: {
			if (idSet == null) {
				throw new NullPointerException();
			}
		}
		
		SANITY: {
			assert Person.MAIN_TABLE.column(0) != null;
			assert Person.MAIN_TABLE.primaryKey() != null;
			assert Person.MAIN_TABLE.primaryKey().getColumns() != null;
			assert Person.MAIN_TABLE.primaryKey().getColumns().length == 1;
			assert Person.MAIN_TABLE.primaryKey().getColumns()[0] != null;
			assert Person.MAIN_TABLE.primaryKey().getColumns()[0].type() != null;
			assert Type.getLong().equals(Person.MAIN_TABLE.primaryKey().getColumns()[0].type());
			assert Person.MAIN_TABLE.primaryKey().getColumns()[0].equals(Person.MAIN_TABLE.column(0));
		}
		
		try {
			return idSet.getLong(1);
		} catch (final SQLException e) {
			throw new DatabaseException(e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.EntityAdapter#load()
	 */
	@Override
	public Iterator<Person> load() throws DatabaseException {
		SANITY: {
			assert Person.LAYOUT != null;
		}
		
		final Table mainTable = Person.LAYOUT.getMainTable();
		
		SANITY: {
			assert mainTable != null;
		}
		
		final ResultSet idSet = getConnector().executeQuery("SELECT " + mainTable.primaryKey() + " FROM " + mainTable
		                                                            + ";");
		return loadForIds(idSet);
		
	}
	
	/**
	 * Load by id.
	 * 
	 * @param id
	 *            the id
	 * @return the person
	 * @throws DatabaseException
	 *             the database exception
	 */
	@Override
	public Person loadById(final Object id) throws DatabaseException {
		PRECONDITIONS: {
			if (id == null) {
				throw new NullPointerException();
			}
			if (!Long.class.isAssignableFrom(id.getClass())) {
				throw new IllegalArgumentException();
			}
		}
		
		final long localId = (long) id;
		try {
			SANITY: {
				assert this.mainTable.primaryKey().getColumns() != null;
				assert this.mainTable.primaryKey().getColumns().length == 1;
				assert Type.getLong().equals(this.mainTable.primaryKey().getColumnType(0));
			}
			
			PreparedStatement preparedStatement = getConnector().prepare("SELECT " + this.mainTable.primaryKey()
			                                                                     + " FROM " + this.mainTable
			                                                                     + " WHERE "
			                                                                     + this.mainTable.primaryKey()
			                                                                     + " = ?;");
			
			preparedStatement.setLong(1, localId);
			ResultSet result = preparedStatement.executeQuery();
			
			if (!result.next()) {
				return null;
			}
			
			final Person person = createInstance(localId);
			
			// get emails
			preparedStatement = getConnector().prepare("SELECT email FROM " + this.emailTable + " WHERE "
			                                                   + this.emailTable.column("id") + " = ?;");
			preparedStatement.setLong(1, localId);
			result = preparedStatement.executeQuery();
			
			setEmails(person, result);
			
			// get usernames
			preparedStatement = getConnector().prepare("SELECT username FROM " + this.userTable + " WHERE "
			                                                   + this.userTable.column("id") + " = ?;");
			preparedStatement.setLong(1, localId);
			result = preparedStatement.executeQuery();
			
			setUsernames(person, result);
			
			// get fullnames
			preparedStatement = getConnector().prepare("SELECT fullname FROM " + this.fullnameTable + " WHERE "
			                                                   + this.fullnameTable.column("id") + " = ?;");
			preparedStatement.setLong(1, localId);
			result = preparedStatement.executeQuery();
			
			setFullnames(person, result);
			
			this.personFactory.load(person);
			
			return person;
		} catch (final SQLException e) {
			throw new DatabaseException(e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.EntityAdapter#saveOrUpdate(org.mozkito.database.Entity)
	 */
	@Override
	public void saveOrUpdate(final Person entity) throws DatabaseException {
		PRECONDITIONS: {
			// none
		}
		
		try {
			try {
				ResultSet result = null;
				
				SANITY: {
					assert entity.getId() >= 0;
				}
				
				// 0 is the default value. this means this object wasn't loaded from the database
				if (entity.getId() > 0) {
					final PreparedStatement preparedStatement = getConnector().prepare("SELECT "
					                                                                           + this.mainTable.primaryKey()
					                                                                           + " FROM "
					                                                                           + this.mainTable
					                                                                           + " WHERE "
					                                                                           + this.mainTable.primaryKey()
					                                                                           + " = ?;");
					preparedStatement.setLong(1, entity.getId());
					result = preparedStatement.executeQuery();
				}
				
				long id = 0;
				
				if ((result == null) || !result.next()) {
					// INSERT
					final ResultSet idResult = getConnector().executeQuery("SELECT nextval('persons_id_seq');");
					if (!idResult.next()) {
						throw new DatabaseException("Failed to get serial key.");
					}
					
					id = idResult.getInt(1);
					getConnector().beginTransaction();
					final PreparedStatement statement = getConnector().prepare("INSERT INTO " + this.mainTable + " ("
					                                                                   + this.mainTable.primaryKey()
					                                                                   + ") VALUES (?)");
					statement.setLong(1, id);
					statement.executeUpdate();
					entity.setId(id);
				} else {
					// UPDATE
					id = result.getInt(1);
					
					getConnector().beginTransaction();
					PreparedStatement statement = getConnector().prepare("DELETE FROM " + this.emailTable + " WHERE "
					                                                             + this.emailTable.column("id")
					                                                             + " = ?;");
					statement.setLong(1, id);
					statement.executeUpdate();
					
					statement = getConnector().prepare("DELETE FROM " + this.userTable + " WHERE "
					                                           + this.userTable.column("id") + " = ?;");
					statement.setLong(1, id);
					statement.executeUpdate();
					
					statement = getConnector().prepare("DELETE FROM " + this.fullnameTable + " WHERE "
					                                           + this.fullnameTable.column("id") + " = ?;");
					statement.setLong(1, id);
					statement.executeUpdate();
				}
				
				PreparedStatement statement;
				
				for (final String email : entity.getEmailAddresses()) {
					statement = getConnector().prepare("INSERT INTO " + this.emailTable + " ("
					                                           + this.emailTable.columnNames() + ")" + " VALUES (?, ?)");
					statement.setLong(1, id);
					statement.setString(2, email);
					statement.executeUpdate();
				}
				
				for (final String username : entity.getUsernames()) {
					statement = getConnector().prepare("INSERT INTO " + this.userTable + " ("
					                                           + this.userTable.columnNames() + ")" + " VALUES (?, ?)");
					statement.setLong(1, id);
					statement.setString(2, username);
					statement.executeUpdate();
				}
				
				for (final String fullname : entity.getFullnames()) {
					statement = getConnector().prepare("INSERT INTO " + this.fullnameTable + " ("
					                                           + this.fullnameTable.columnNames() + ")"
					                                           + " VALUES (?, ?)");
					statement.setLong(1, id);
					statement.setString(2, fullname);
					statement.executeUpdate();
				}
				
				getConnector().commit();
			} catch (final SQLException e) {
				getConnector().rollback();
				throw new DatabaseException(e);
			}
			
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the emails.
	 * 
	 * @param person
	 *            the person
	 * @param result
	 *            the result
	 * @return the test person
	 * @throws SQLException
	 *             the sQL exception
	 */
	private Person setEmails(final Person person,
	                         final ResultSet result) throws SQLException {
		while (result.next()) {
			person.getEmailAddresses().add(result.getString("email"));
		}
		
		return person;
	}
	
	/**
	 * Sets the fullnames.
	 * 
	 * @param person
	 *            the person
	 * @param result
	 *            the result
	 * @return the test person
	 * @throws SQLException
	 *             the sQL exception
	 */
	private Person setFullnames(final Person person,
	                            final ResultSet result) throws SQLException {
		while (result.next()) {
			person.getFullnames().add(result.getString("fullname"));
		}
		
		return person;
	}
	
	/**
	 * Sets the usernames.
	 * 
	 * @param person
	 *            the person
	 * @param result
	 *            the result
	 * @return the test person
	 * @throws SQLException
	 *             the sQL exception
	 */
	private Person setUsernames(final Person person,
	                            final ResultSet result) throws SQLException {
		while (result.next()) {
			person.getUsernames().add(result.getString("username"));
		}
		
		return person;
	}
	
}
