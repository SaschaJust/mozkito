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

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.mozkito.issues.tracker.model.Report;
import org.mozkito.mappings.engines.master.Environment;
import org.mozkito.mappings.finder.Finder;
import org.mozkito.mappings.model.Feature;
import org.mozkito.mappings.model.Relation;
import org.mozkito.persistence.ConnectOptions;
import org.mozkito.testing.DatabaseTest;
import org.mozkito.testing.annotation.DatabaseSettings;
import org.mozkito.versions.model.RCSTransaction;

/**
 * The Class PatchEngineTest.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@DatabaseSettings (unit = "mappings", database = "moskito_jruby_july2010", options = ConnectOptions.CREATE)
public class PatchEngineTest extends DatabaseTest {
	
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
		finder = Environment.setup("/engines.codeFragments.test.properties");
		
	}
	
	/**
	 * Tear down after class.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	/**
	 * Score.
	 * 
	 * @param transactionId
	 *            the transaction id
	 * @param reportId
	 *            the report id
	 * @return the relation
	 * @throws Exception
	 *             the exception
	 */
	private Relation score(final String transactionId,
	                       final String reportId) throws Exception {
		final RCSTransaction rCSTransaction = Environment.loadTransaction(getPersistenceUtil(), transactionId);
		assertNotNull("Failed retreiving transaction from database.", rCSTransaction);
		
		final Report report = Environment.loadReport(getPersistenceUtil(), reportId);
		assertNotNull("Failed retreiving report from database.", report);
		
		final Relation relation = Environment.relation(rCSTransaction, report);
		assertNotNull("Failed creating relation from " + rCSTransaction + " and " + report, relation);
		
		finder.score(this.engine, relation);
		
		return relation;
	}
	
	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
		this.engines = finder.getEngines();
		this.engine = this.engines.get(CodeFragmentsEngine.class);
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
		System.err.println(JavaUtils.collectionToString(features));
		
		fail("Not yet implemented");
	}
}
