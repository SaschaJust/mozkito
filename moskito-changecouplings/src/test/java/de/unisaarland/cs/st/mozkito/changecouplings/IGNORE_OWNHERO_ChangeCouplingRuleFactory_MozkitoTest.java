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
package de.unisaarland.cs.st.mozkito.changecouplings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kisa.Logger;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unisaarland.cs.st.mozkito.changecouplings.ChangeCouplingRuleFactory;
import de.unisaarland.cs.st.mozkito.changecouplings.model.FileChangeCoupling;
import de.unisaarland.cs.st.mozkito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.mozkito.persistence.model.Person;
import de.unisaarland.cs.st.mozkito.testing.MoskitoTest;
import de.unisaarland.cs.st.mozkito.testing.annotation.DatabaseSettings;
import de.unisaarland.cs.st.mozkito.versions.elements.ChangeType;
import de.unisaarland.cs.st.mozkito.versions.elements.RCSFileManager;
import de.unisaarland.cs.st.mozkito.versions.model.RCSFile;
import de.unisaarland.cs.st.mozkito.versions.model.RCSRevision;
import de.unisaarland.cs.st.mozkito.versions.model.RCSTransaction;

/**
 * The Class ChangeCouplingRuleFactoryTest.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class IGNORE_OWNHERO_ChangeCouplingRuleFactory_MozkitoTest extends MoskitoTest {
	
	/** The persistence util. */
	private static PersistenceUtil persistenceUtil = null;
	
	/**
	 * Before class.
	 */
	@BeforeClass
	public static void beforeClass() {
		try {
			
			persistenceUtil = getPersistenceUtil();
			URL sqlURL = IGNORE_OWNHERO_ChangeCouplingRuleFactory_MozkitoTest.class.getResource(FileUtils.fileSeparator
			        + "change_file_couplings.psql");
			
			File sqlFile = new File(sqlURL.toURI());
			String query = FileUtils.readFileToString(sqlFile);
			persistenceUtil.executeNativeQuery("CREATE LANGUAGE plpythonu;");
			persistenceUtil.executeNativeQuery("CREATE LANGUAGE plpython2u;");
			persistenceUtil.executeNativeQuery(query);
			sqlURL = IGNORE_OWNHERO_ChangeCouplingRuleFactory_MozkitoTest.class.getResource(FileUtils.fileSeparator
			        + "change_file_couplings.psql");
			
			sqlFile = new File(sqlURL.toURI());
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
	@DatabaseSettings (unit = "rcs")
	public void testChangeCouplings() {
		
		persistenceUtil.beginTransaction();
		
		final RCSFileManager fileManager = new RCSFileManager();
		final Person person = new Person("kim", "", "");
		
		// ###transaction 1
		
		final DateTime now = new DateTime();
		final RCSTransaction rcsTransaction = RCSTransaction.createTransaction("0", "", now, person, "");
		final RCSFile fileA = fileManager.createFile("A.java", rcsTransaction);
		fileA.assignTransaction(rcsTransaction, "A.java");
		new RCSRevision(rcsTransaction, fileA, ChangeType.Added);
		
		final RCSFile fileB = fileManager.createFile("B.java", rcsTransaction);
		fileB.assignTransaction(rcsTransaction, "B.java");
		new RCSRevision(rcsTransaction, fileB, ChangeType.Added);
		
		final RCSFile fileC = fileManager.createFile("C.java", rcsTransaction);
		fileC.assignTransaction(rcsTransaction, "C.java");
		new RCSRevision(rcsTransaction, fileC, ChangeType.Added);
		
		persistenceUtil.saveOrUpdate(rcsTransaction);
		
		// ### transaction 2
		
		final RCSTransaction rcsTransaction2 = RCSTransaction.createTransaction("1", "", now.plus(10000), person, "");
		new RCSRevision(rcsTransaction2, fileA, ChangeType.Modified);
		new RCSRevision(rcsTransaction2, fileB, ChangeType.Added);
		final RCSFile fileD = fileManager.createFile("D.java", rcsTransaction);
		// fileC.assignTransaction(rcsTransaction2, "D.java");
		new RCSRevision(rcsTransaction2, fileD, ChangeType.Added);
		persistenceUtil.saveOrUpdate(rcsTransaction2);
		
		// ### transaction 3
		
		final RCSTransaction rcsTransaction3 = RCSTransaction.createTransaction("2", "", now.plus(20000), person, "");
		new RCSRevision(rcsTransaction3, fileA, ChangeType.Modified);
		
		fileC.assignTransaction(rcsTransaction3, "C.java");
		new RCSRevision(rcsTransaction3, fileC, ChangeType.Modified);
		new RCSRevision(rcsTransaction3, fileB, ChangeType.Added);
		persistenceUtil.saveOrUpdate(rcsTransaction3);
		
		// ### transaction 4
		
		final RCSTransaction rcsTransaction4 = RCSTransaction.createTransaction("3", "", now.plus(30000), person, "");
		new RCSRevision(rcsTransaction4, fileA, ChangeType.Modified);
		new RCSRevision(rcsTransaction4, fileC, ChangeType.Modified);
		new RCSRevision(rcsTransaction4, fileB, ChangeType.Modified);
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
