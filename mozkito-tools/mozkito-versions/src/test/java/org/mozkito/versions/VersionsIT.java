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

package org.mozkito.versions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.ownhero.dev.ioda.CommandExecutor;
import net.ownhero.dev.ioda.Tuple;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class VersionsIT.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class VersionsIT {
	
	/**
	 * Exec.
	 * 
	 * @param properties
	 *            the properties
	 * @return the tuple
	 */
	@SuppressWarnings ({ "unchecked", "rawtypes" })
	private Tuple<Integer, List<String>> exec(final Properties properties) {
		
		// TODO this should be automatically generated and be platform independent
		final String jar = "target/mozkito-versions-tool-0.4-SNAPSHOT-jar-with-dependencies.jar";
		
		// move properties to hashmap
		final HashMap<String, String> hmap = new HashMap<>();
		hmap.putAll((Map) properties);
		
		// execute java
		return CommandExecutor.execute("java", new String[] { "-jar", jar }, null, null, hmap);
		
	}
	
	/**
	 * Setup.
	 */
	@Before
	public void setup() {
		final String dropAllTables = "function dropalltables() { dbname=$1; for f in $(psql $dbname <<<'\\dt' | awk -F'|' '/^ [^ ]/ { print $2; }' | tail -n +2); do psql $dbname <<<\"DROP TABLE $f CASCADE;\" ; done; }";
		CommandExecutor.execute(dropAllTables, new String[0], null, null, new HashMap<String, String>());
		CommandExecutor.execute("dropalltables", new String[] { "mozkitoversionsintegrationtest" }, null, null,
		                        new HashMap<String, String>());
	}
	
	/**
	 * Test.
	 */
	@Test
	public final void testHelp() {
		final Properties properties = new Properties();
		properties.put("help", "");
		final Tuple<Integer, List<String>> execute = exec(properties);
		
		// check log
		assertFalse(CollectionUtils.exists(execute.getSecond(), new Predicate() {
			
			@Override
			public boolean evaluate(final Object object) {
				PRECONDITIONS: {
					// none
				}
				
				try {
					final String line = (String) object;
					if (line.contains("] ERROR")) {
						System.err.println(line);
						return true;
					}
					return false;
				} finally {
					POSTCONDITIONS: {
						// none
					}
				}
			}
		}));
		
		// check return value
		assertEquals("Exit code non-zero.", (Integer) 0, execute.getFirst());
	}
	
	/**
	 * Test.
	 */
	@Test
	public final void testMozkitoRepository() {
		final Properties properties = new Properties();
		properties.put("database.host", "");
		properties.put("database.name", "mozkitoversionsintegrationtest");
		properties.put("database.user", "miner");
		properties.put("database.password", "miner");
		properties.put("repository.uri", "http://mozkito.org:7990/scm/MOZKITO/mozkito.git");
		properties.put("repository.type", "GIT");
		properties.put("repository.mainBranch", "master");
		properties.put("headless", "");
		properties.put("repository.user", "mozkito_integration_tests");
		properties.put("repository.password", "mozkito_integration_tests");
		
		final Tuple<Integer, List<String>> execute = exec(properties);
		
		// check log
		assertFalse(CollectionUtils.exists(execute.getSecond(), new Predicate() {
			
			@Override
			public boolean evaluate(final Object object) {
				PRECONDITIONS: {
					// none
				}
				
				try {
					final String line = (String) object;
					if (line.contains("] ERROR")) {
						System.err.println(line);
						return true;
					}
					return false;
				} finally {
					POSTCONDITIONS: {
						// none
					}
				}
			}
		}));
		
		// check return value
		assertEquals("Exit code non-zero.", (Integer) 0, execute.getFirst());
	}
}
