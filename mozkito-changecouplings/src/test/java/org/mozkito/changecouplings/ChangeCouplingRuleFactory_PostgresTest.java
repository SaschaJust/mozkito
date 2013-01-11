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
package org.mozkito.changecouplings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kisa.Logger;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mozkito.changecouplings.model.FileChangeCoupling;
import org.mozkito.persistence.ConnectOptions;
import org.mozkito.persistence.DatabaseType;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.persistence.model.Person;
import org.mozkito.testing.DatabaseTest;
import org.mozkito.testing.annotation.DatabaseSettings;
import org.mozkito.versions.RevDependencyGraph;
import org.mozkito.versions.RevDependencyGraph.EdgeType;
import org.mozkito.versions.elements.ChangeType;
import org.mozkito.versions.model.ChangeSet;
import org.mozkito.versions.model.Handle;
import org.mozkito.versions.model.Revision;
import org.mozkito.versions.model.VersionArchive;

/**
 * The Class ChangeCouplingRuleFactoryTest.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
@DatabaseSettings (unit = "versions",
                   type = DatabaseType.POSTGRESQL,
                   hostname = "grid1.st.cs.uni-saarland.de",
                   username = "miner",
                   password = "miner",
                   options = ConnectOptions.DROP_AND_CREATE_DATABASE,
                   database = "changecouplings",
                   remote = true)
public class ChangeCouplingRuleFactory_PostgresTest extends DatabaseTest {
	
	/** The persistence util. */
	private static PersistenceUtil persistenceUtil = null;
	
	/**
	 * Instantiates a new iGNOR e_ ownher o_ change coupling rule factory_ mozkito test.
	 */
	@Before
	public void setupIGNORE_OWNHERO_ChangeCouplingRuleFactory_MozkitoTest() {
		try {
			
			ChangeCouplingRuleFactory_PostgresTest.persistenceUtil = getPersistenceUtil();
			URL sqlURL = ChangeCouplingRuleFactory_PostgresTest.class.getResource(FileUtils.fileSeparator
			        + "change_file_couplings.psql"); //$NON-NLS-1$
			
			java.io.File sqlFile = new java.io.File(sqlURL.toURI());
			String query = FileUtils.readFileToString(sqlFile);
			ChangeCouplingRuleFactory_PostgresTest.persistenceUtil.executeNativeQuery("CREATE LANGUAGE plpythonu;"); //$NON-NLS-1$
			final String query2 = "CREATE LANGUAGE plpython2u;";
			ChangeCouplingRuleFactory_PostgresTest.persistenceUtil.executeNativeQuery(query2);
			ChangeCouplingRuleFactory_PostgresTest.persistenceUtil.executeNativeQuery(query);
			sqlURL = ChangeCouplingRuleFactory_PostgresTest.class.getResource(FileUtils.fileSeparator
			        + "change_file_couplings.psql"); //$NON-NLS-1$
			
			sqlFile = new java.io.File(sqlURL.toURI());
			query = FileUtils.readFileToString(sqlFile);
			ChangeCouplingRuleFactory_PostgresTest.persistenceUtil.executeNativeQuery(query);
		} catch (final IOException e) {
			if (Logger.logWarn()) {
				Logger.warn(e, "Could not set or update change coupling functions. Trying to continue ... ");
			}
		} catch (final URISyntaxException e) {
			if (Logger.logWarn()) {
				Logger.warn(e, "Could not set or update change coupling functions. Trying to continue ... ");
			}
		} finally {
			ChangeCouplingRuleFactory_PostgresTest.persistenceUtil.commitTransaction();
		}
		
	}
	
	/**
	 * Test change couplings.
	 */
	@Test
	public void testChangeCouplings() {
		
		ChangeCouplingRuleFactory_PostgresTest.persistenceUtil.beginTransaction();
		final Person person = new Person("kim", "", "");
		final DateTime now = new DateTime();
		final ChangeSet cs0 = new ChangeSet("0", "", now, person, "");
		final ChangeSet cs1 = new ChangeSet("1", "", now.plus(10000), person, "");
		final ChangeSet cs2 = new ChangeSet("2", "", now.plus(20000), person, "");
		final ChangeSet cs3 = new ChangeSet("3", "", now.plus(30000), person, "");
		
		final VersionArchive versionArchive = new VersionArchive() {
			
			/**
             * 
             */
			private static final long serialVersionUID = 1L;
			
			@Override
			public ChangeSet getChangeSetById(final String id) {
				switch (id) {
					case "0":
						return cs0;
					case "1":
						return cs1;
					case "2":
						return cs2;
					case "3":
						return cs3;
					default:
						return null;
				}
			}
		};
		try {
			final RevDependencyGraph revDepGraph = new RevDependencyGraph();
			revDepGraph.addBranch("master", cs3.getId());
			revDepGraph.addEdge(cs2.getId(), cs3.getId(), EdgeType.BRANCH_HEAD);
			revDepGraph.addEdge(cs1.getId(), cs2.getId(), EdgeType.BRANCH_HEAD);
			revDepGraph.addEdge(cs0.getId(), cs1.getId(), EdgeType.BRANCH_HEAD);
			versionArchive.setRevDependencyGraph(revDepGraph);
			
			// ###transaction 1
			
			final Handle fileA = new Handle(versionArchive);
			fileA.assignRevision(new Revision(cs0, fileA, ChangeType.Added), "A.java");
			
			final Handle fileB = new Handle(versionArchive);
			fileB.assignRevision(new Revision(cs0, fileB, ChangeType.Added), "B.java");
			
			final Handle fileC = new Handle(versionArchive);
			fileC.assignRevision(new Revision(cs0, fileC, ChangeType.Added), "C.java");
			
			ChangeCouplingRuleFactory_PostgresTest.persistenceUtil.saveOrUpdate(cs0);
			
			// ### transaction 2
			
			new Revision(cs1, fileA, ChangeType.Modified);
			new Revision(cs1, fileB, ChangeType.Added);
			
			final Handle fileD = new Handle(versionArchive);
			fileD.assignRevision(new Revision(cs1, fileD, ChangeType.Added), "D.java");
			ChangeCouplingRuleFactory_PostgresTest.persistenceUtil.saveOrUpdate(cs1);
			
			// ### transaction 3
			
			new Revision(cs2, fileA, ChangeType.Modified);
			new Revision(cs2, fileC, ChangeType.Modified);
			new Revision(cs2, fileB, ChangeType.Added);
			ChangeCouplingRuleFactory_PostgresTest.persistenceUtil.saveOrUpdate(cs2);
			
			// ### transaction 4
			
			new Revision(cs3, fileA, ChangeType.Modified);
			new Revision(cs3, fileC, ChangeType.Modified);
			new Revision(cs3, fileB, ChangeType.Modified);
			ChangeCouplingRuleFactory_PostgresTest.persistenceUtil.saveOrUpdate(cs3);
			
			ChangeCouplingRuleFactory_PostgresTest.persistenceUtil.commitTransaction();
			
			final List<FileChangeCoupling> changeCouplingRules = ChangeCouplingRuleFactory.getFileChangeCouplings(cs2,
			                                                                                                      1,
			                                                                                                      0,
			                                                                                                      ChangeCouplingRuleFactory_PostgresTest.persistenceUtil);
			assertEquals(9, changeCouplingRules.size());
			FileChangeCoupling rule = changeCouplingRules.get(0);
			assertEquals(1, rule.getPremise().size());
			assertTrue(rule.getPremise().contains(fileB));
			assertEquals(fileA, rule.getImplication());
			assertEquals(2, rule.getSupport().intValue());
			assertEquals(1, rule.getConfidence().doubleValue(), 0);
			
			rule = changeCouplingRules.get(1);
			assertEquals(1, rule.getPremise().size());
			assertTrue(rule.getPremise().contains(fileA));
			assertEquals(fileB, rule.getImplication());
			assertEquals(2, rule.getSupport().intValue());
			assertEquals(1, rule.getConfidence().doubleValue(), 0);
			
			rule = changeCouplingRules.get(2);
			assertEquals(2, rule.getPremise().size());
			assertTrue(rule.getPremise().contains(fileC));
			assertTrue(rule.getPremise().contains(fileB));
			assertEquals(fileA, rule.getImplication());
			assertEquals(1, rule.getSupport().intValue());
			assertEquals(1, rule.getConfidence().doubleValue(), 0);
			
			rule = changeCouplingRules.get(3);
			assertEquals(2, rule.getPremise().size());
			assertTrue(rule.getPremise().contains(fileA));
			assertTrue(rule.getPremise().contains(fileC));
			assertEquals(fileB, rule.getImplication());
			assertEquals(1, rule.getSupport().intValue());
			assertEquals(1, rule.getConfidence().doubleValue(), 0);
			
			rule = changeCouplingRules.get(4);
			assertEquals(1, rule.getPremise().size());
			assertTrue(rule.getPremise().contains(fileC));
			assertEquals(fileA, rule.getImplication());
			assertEquals(1, rule.getSupport().intValue());
			assertEquals(1, rule.getConfidence().doubleValue(), 0);
			
			rule = changeCouplingRules.get(5);
			assertEquals(1, rule.getPremise().size());
			assertTrue(rule.getPremise().contains(fileC));
			assertEquals(fileB, rule.getImplication());
			assertEquals(1, rule.getSupport().intValue());
			assertEquals(1, rule.getConfidence().doubleValue(), 0);
			
			rule = changeCouplingRules.get(6);
			assertEquals(2, rule.getPremise().size());
			assertTrue(rule.getPremise().contains(fileA));
			assertTrue(rule.getPremise().contains(fileB));
			assertEquals(fileC, rule.getImplication());
			assertEquals(1, rule.getSupport().intValue());
			assertEquals(0.5, rule.getConfidence().doubleValue(), 0);
			
			rule = changeCouplingRules.get(7);
			assertEquals(1, rule.getPremise().size());
			assertEquals(fileC, rule.getImplication());
			assertEquals(1, rule.getSupport().intValue());
			assertEquals(.5, rule.getConfidence().doubleValue(), 0);
			
			rule = changeCouplingRules.get(8);
			assertEquals(1, rule.getPremise().size());
			assertEquals(fileC, rule.getImplication());
			assertEquals(1, rule.getSupport().intValue());
			assertEquals(.5, rule.getConfidence().doubleValue(), 0);
		} catch (final IOException e) {
			fail();
		}
		
	}
}
