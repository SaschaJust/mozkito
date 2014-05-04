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

package org.mozkito.versions.database;

import org.mozkito.database.DatabaseEnvironment;
import org.mozkito.database.DatabaseManager;
import org.mozkito.database.PersistenceUtil;
import org.mozkito.database.QueryPool;
import org.mozkito.database.exceptions.DatabaseException;
import org.mozkito.database.meta.MetaData;
import org.mozkito.database.meta.MetaDataAdapter;
import org.mozkito.exceptions.ConfigurationException;
import org.mozkito.persistence.ConnectOptions;
import org.mozkito.persistence.DatabaseType;
import org.mozkito.persons.elements.PersonFactory;
import org.mozkito.persons.model.Person;
import org.mozkito.persons.model.PersonAdapter;
import org.mozkito.versions.model.Branch;
import org.mozkito.versions.model.ChangeSet;
import org.mozkito.versions.model.Handle;
import org.mozkito.versions.model.Revision;
import org.mozkito.versions.model.VersionArchive;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class Main {
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 * @throws DatabaseException
	 *             the database exception
	 * @throws ConfigurationException
	 */
	public static void main(final String[] args) throws DatabaseException, ConfigurationException {
		final DatabaseEnvironment options = new DatabaseEnvironment(DatabaseType.POSTGRESQL, "mozkito_nojpa",
		                                                            "10.20.30.1", "miner", "miner",
		                                                            ConnectOptions.VALIDATE_OR_CREATE_SCHEMA, "persons");
		final PersistenceUtil util = DatabaseManager.createUtil(options);
		
		final QueryPool pool = util.getPool();
		
		pool.addLoader(Person.class, new PersonAdapter(pool, new PersonFactory()));
		pool.addLoader(MetaData.class, new MetaDataAdapter(pool));
		pool.addLoader(VersionArchive.class, new VersionArchiveAdapter(pool));
		pool.addLoader(Branch.class, new BranchAdapter(pool));
		pool.addLoader(Handle.class, new HandleAdapter(pool));
		pool.addLoader(Revision.class, new RevisionAdapter(pool));
		pool.addLoader(ChangeSet.class, new ChangeSetAdapter(pool));
		
		pool.createScheme();
		
		util.shutdown();
		System.exit(1);
	}
	
}
