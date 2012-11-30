/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *******************************************************************************/
package org.mozkito.changecouplings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kisa.Logger;

import org.joda.time.DateTime;
import org.junit.Test;

import org.mozkito.changecouplings.model.FileChangeCoupling;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.persistence.model.Person;
import org.mozkito.testing.DatabaseTest;
import org.mozkito.testing.annotation.DatabaseSettings;
import org.mozkito.versions.elements.ChangeType;
import org.mozkito.versions.elements.RCSFileManager;
import org.mozkito.versions.model.File;
import org.mozkito.versions.model.Revision;
import org.mozkito.versions.model.Transaction;

/**
 * The Class ChangeCouplingRuleFactoryTest.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
@DatabaseSettings (unit = "versions")
public class IGNORE_OWNHERO_ChangeCouplingRuleFactory_MozkitoTest extends DatabaseTest {
	
	/** The persistence util. */
	private static PersistenceUtil persistenceUtil = null;
	
	public IGNORE_OWNHERO_ChangeCouplingRuleFactory_MozkitoTest() {
		try {
			
			persistenceUtil = getPersistenceUtil();
			URL sqlURL = IGNORE_OWNHERO_ChangeCouplingRuleFactory_MozkitoTest.class.getResource(FileUtils.fileSeparator
			        + "change_file_couplings.psql");
			
			java.io.File sqlFile = new java.io.File(sqlURL.toURI());
			String query = FileUtils.readFileToString(sqlFile);
			persistenceUtil.executeNativeQuery("CREATE LANGUAGE plpythonu;");
			persistenceUtil.executeNativeQuery("CREATE LANGUAGE plpython2u;");
			persistenceUtil.executeNativeQuery(query);
			sqlURL = IGNORE_OWNHERO_ChangeCouplingRuleFactory_MozkitoTest.class.getResource(FileUtils.fileSeparator
			        + "change_file_couplings.psql");
			
			sqlFile = new java.io.File(sqlURL.toURI());
			query = FileUtils.readFileToString(sqlFile);
			persistenceUtil.executeNativeQuery(query);
		} catch (final IOException e) {
			if (Logger.logWarn()) {
				Logger.warn(e, "Could not set or update change coupling functions. Trying to continue ... ");
			}
		} catch (final URISyntaxException e) {
			if (Logger.logWarn()) {
				Logger.warn(e, "Could not set or update change coupling functions. Trying to continue ... ");
			}
		} finally {
			persistenceUtil.commitTransaction();
		}
		
	}
	
	/**
	 * Test change couplings.
	 */
	@Test
	public void testChangeCouplings() {
		
		persistenceUtil.beginTransaction();
		
		final RCSFileManager fileManager = new RCSFileManager();
		final Person person = new Person("kim", "", "");
		
		// ###transaction 1
		
		final DateTime now = new DateTime();
		final Transaction rcsTransaction = new Transaction("0", "", now, person, "");
		final File fileA = fileManager.createFile("A.java", rcsTransaction);
		fileA.assignTransaction(rcsTransaction, "A.java");
		new Revision(rcsTransaction, fileA, ChangeType.Added);
		
		final File fileB = fileManager.createFile("B.java", rcsTransaction);
		fileB.assignTransaction(rcsTransaction, "B.java");
		new Revision(rcsTransaction, fileB, ChangeType.Added);
		
		final File fileC = fileManager.createFile("C.java", rcsTransaction);
		fileC.assignTransaction(rcsTransaction, "C.java");
		new Revision(rcsTransaction, fileC, ChangeType.Added);
		
		persistenceUtil.saveOrUpdate(rcsTransaction);
		
		// ### transaction 2
		
		final Transaction rcsTransaction2 = new Transaction("1", "", now.plus(10000), person, "");
		new Revision(rcsTransaction2, fileA, ChangeType.Modified);
		new Revision(rcsTransaction2, fileB, ChangeType.Added);
		final File fileD = fileManager.createFile("D.java", rcsTransaction);
		// fileC.assignTransaction(rcsTransaction2, "D.java");
		new Revision(rcsTransaction2, fileD, ChangeType.Added);
		persistenceUtil.saveOrUpdate(rcsTransaction2);
		
		// ### transaction 3
		
		final Transaction rcsTransaction3 = new Transaction("2", "", now.plus(20000), person, "");
		new Revision(rcsTransaction3, fileA, ChangeType.Modified);
		
		fileC.assignTransaction(rcsTransaction3, "C.java");
		new Revision(rcsTransaction3, fileC, ChangeType.Modified);
		new Revision(rcsTransaction3, fileB, ChangeType.Added);
		persistenceUtil.saveOrUpdate(rcsTransaction3);
		
		// ### transaction 4
		
		final Transaction rcsTransaction4 = new Transaction("3", "", now.plus(30000), person, "");
		new Revision(rcsTransaction4, fileA, ChangeType.Modified);
		new Revision(rcsTransaction4, fileC, ChangeType.Modified);
		new Revision(rcsTransaction4, fileB, ChangeType.Modified);
		persistenceUtil.saveOrUpdate(rcsTransaction4);
		
		persistenceUtil.commitTransaction();
		
		final List<FileChangeCoupling> changeCouplingRules = ChangeCouplingRuleFactory.getFileChangeCouplings(rcsTransaction3,
		                                                                                                      1, 0,
		                                                                                                      persistenceUtil);
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
		
	}
}
