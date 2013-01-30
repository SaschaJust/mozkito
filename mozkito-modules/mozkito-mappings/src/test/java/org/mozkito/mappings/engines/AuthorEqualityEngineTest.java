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

package org.mozkito.mappings.engines;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Map;
import java.util.Queue;

import net.ownhero.dev.ioda.JavaUtils;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.mozkito.issues.tracker.model.Report;
import org.mozkito.mappings.engines.master.Environment;
import org.mozkito.mappings.finder.Finder;
import org.mozkito.mappings.model.Feature;
import org.mozkito.mappings.model.Relation;
import org.mozkito.persistence.ConnectOptions;
import org.mozkito.persistence.DatabaseType;
import org.mozkito.testing.DatabaseTest;
import org.mozkito.testing.annotation.DatabaseSettings;
import org.mozkito.versions.model.ChangeSet;

/**
 * The Class AuthorEqualityEngineTest.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@DatabaseSettings (unit = "mappings",
                   database = "moskito_jruby_july2010",
                   options = ConnectOptions.VALIDATE_OR_CREATE_SCHEMA,
                   hostname = "grid1.st.cs.uni-saarland.de",
                   password = "miner",
                   username = "miner",
                   type = DatabaseType.POSTGRESQL)
@Ignore
public class AuthorEqualityEngineTest extends DatabaseTest {
	
	/** The engines. */
	private Map<Class<? extends Engine>, Engine> engines;
	
	/** The engine. */
	private Engine                               engine;
	
	/** The finder. */
	private static Finder                        finder;
	
	/**
	 * Sets the up before class.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AuthorEqualityEngineTest.finder = Environment.setup("/engines.authorEquality.test.properties");
	}
	
	/**
	 * Score.
	 * 
	 * @param changeSetId
	 *            the transaction id
	 * @param reportId
	 *            the report id
	 * @return the relation
	 * @throws Exception
	 *             the exception
	 */
	private Relation score(final String changeSetId,
	                       final String reportId) throws Exception {
		final ChangeSet changeset = Environment.loadTransaction(getPersistenceUtil(), changeSetId);
		assertNotNull("Failed retreiving transaction from database.", changeset);
		
		final Report report = Environment.loadReport(getPersistenceUtil(), reportId);
		assertNotNull("Failed retreiving report from database.", report);
		
		final Relation relation = Environment.relation(changeset, report);
		assertNotNull("Failed creating relation from " + changeset + " and " + report, relation);
		
		AuthorEqualityEngineTest.finder.score(this.engine, relation);
		
		return relation;
	}
	
	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
		this.engines = AuthorEqualityEngineTest.finder.getEngines();
		this.engine = this.engines.get(AuthorEqualityEngine.class);
	}
	
	/**
	 * Test score_9be47462f004ba9a9f729a46448fde9e304fe848_21.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public final void testScore_9be47462f004ba9a9f729a46448fde9e304fe848_21() throws Exception {
		final Relation relation = score("9be47462f004ba9a9f729a46448fde9e304fe848", "JRUBY-21");
		final Queue<Feature> features = relation.getFeatures();
		
		fail(JavaUtils.collectionToString(features));
	}
}
