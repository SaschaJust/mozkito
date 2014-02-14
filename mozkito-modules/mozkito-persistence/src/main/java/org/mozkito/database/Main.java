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

import java.util.List;

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
	 */
	public static void main(final String[] args) {
		final DBConnector connector = new DBConnector();
		connector.connect("postgresql", "localhost", "5432", "nojpa_test", "miner", "miner");
		
		final DBQueryPool pool = new DBQueryPool(connector);
		
		final DBQuery<TestChangeSet> changeSetQuery = pool.getLoader(TestChangeSet.class);
		final List<TestChangeSet> load = changeSetQuery.load();
		for (final TestChangeSet changeSet : load) {
			System.out.println(changeSet);
		}
		
		final TestChangeSet changeSet = new TestChangeSet();
		changeSetQuery.saveOrUpdate(changeSet);
		
		System.out.println(changeSet);
	}
}
