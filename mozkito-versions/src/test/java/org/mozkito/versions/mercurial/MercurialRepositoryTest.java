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
package org.mozkito.versions.mercurial;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.ioda.DateTimeUtils;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.kanuni.instrumentation.KanuniAgent;

import org.junit.Before;
import org.junit.Test;

import difflib.Delta;

import org.mozkito.versions.BranchFactory;
import org.mozkito.versions.elements.AnnotationEntry;
import org.mozkito.versions.elements.ChangeType;
import org.mozkito.versions.elements.LogEntry;

// TODO: Auto-generated Javadoc
/**
 * The Class MercurialRepositoryTest.
 */
public class MercurialRepositoryTest {
	
	static {
		KanuniAgent.initialize();
	}
	
	/** The Constant _67635fe9efeb2fd3751df9ea67650c71e59e3df1. */
	private static final String _67635fe9efeb2fd3751df9ea67650c71e59e3df1 = "2d18bf2cbffbdc6b5ad321dc2fc5e57dc810e4a9";
	
	/** The Constant _E52DEF97EBC1F78C9286B1E7C36783AA67604439. */
	private static final String _E52DEF97EBC1F78C9286B1E7C36783AA67604439 = "caac84e3edc88a6f3ec2b71bbcd6ad78445ef985";
	
	/** The Constant USER_NAME. */
	private static final String USER_NAME                                 = "kim";
	
	/** The Constant _96A9F105774B50F1FA3361212C4D12AE057A4285. */
	private static final String _96A9F105774B50F1FA3361212C4D12AE057A4285 = "b01759380f692e073bdb63d79f5238f1982d0bbd";
	
	/** The Constant _41A40FB23B54A49E91EB4CEE510533EEF810EC68. */
	private static final String _41A40FB23B54A49E91EB4CEE510533EEF810EC68 = "bc67c4d57423730912fda92ff8849d0e78d44c6f";
	
	/** The Constant _CBCC33D919A27B9450D117F211A5F4F45615CAB9. */
	private static final String _CBCC33D919A27B9450D117F211A5F4F45615CAB9 = "2398c6b6da51e317c951dba74e9d205db17fce02";
	
	/** The Constant _637ACF68104E7BDFF8235FB2E1A254300FFEA3CB. */
	private static final String _637ACF68104E7BDFF8235FB2E1A254300FFEA3CB = "c6b358dfd0e99376c15038d4f90d8fe2c1a766af";
	
	/** The Constant _D9A5542FE1B5A755502320BA38FDF180011B40DF. */
	private static final String _D9A5542FE1B5A755502320BA38FDF180011B40DF = "d9a5542fe1b5a755502320ba38fdf180011b40df";
	
	/** The Constant _9BE561B3657E2B1DA2B09D675DDDD5F45C47F57C. */
	private static final String _9BE561B3657E2B1DA2B09D675DDDD5F45C47F57C = "e17ec8036b3bf49667ec1bb7fe474e65451a98b0";
	
	/** The Constant _376ADC0F9371129A76766F8030F2E576165358C1. */
	private static final String _376ADC0F9371129A76766F8030F2E576165358C1 = "ffaaa326e3b8cd68c2396b21eb49b26a1cb3835c";
	
	/** The Constant _98D5C40EF3C14503A472BA4133AE3529C7578E30. */
	private static final String _98D5C40EF3C14503A472BA4133AE3529C7578E30 = "8c9bcc8c558d701ae5c091dcad4d12faf3b0b5da";
	
	/** The Constant _AE94D7FA81437CBBD723049E3951F9DAAA62A7C0. */
	private static final String _AE94D7FA81437CBBD723049E3951F9DAAA62A7C0 = "c601aab93720e3062c7335f970ccfafcae9d1822";
	
	/** The Constant _8273C1E51992A4D7A1DA012DBB416864C2749A7F. */
	private static final String _8273C1E51992A4D7A1DA012DBB416864C2749A7F = "2d60cac0c0f5b8861cba3a9f8e415fbcb99dc28a";
	
	/** The Constant _927478915F2D8FB9135EB33D21CB8491C0E655BE. */
	private static final String _927478915F2D8FB9135EB33D21CB8491C0E655BE = "df6bde3bc282ec4815e98877e150a6881efc059e";
	
	/** The Constant _1AC6AAA05EB6D55939B20E70EC818BB413417757. */
	private static final String _1AC6AAA05EB6D55939B20E70EC818BB413417757 = "ef28e5ceba3b2d8d30999c4fea4301771802d550";
	// private static final String _D98B5A8740DBBE912B711E3A29DCC4FA3D3890E9 =
	// "5be9fd4d8b5653706de0f21bf0c481515890c329";
	/** The repo. */
	private MercurialRepository repo;
	
	/**
	 * Setup.
	 */
	@Before
	public void setup() {
		final URL zipURL = MercurialRepositoryTest.class.getResource(FileUtils.fileSeparator + "testHg.zip");
		assertNotNull(zipURL);
		try {
			final File tmpDir = FileUtils.createRandomDir("mozkito", "testHg", FileShutdownAction.DELETE);
			FileUtils.unzip(new File(zipURL.toURI()), tmpDir);
			if ((!tmpDir.exists()) || (!tmpDir.isDirectory())) {
				fail();
			}
			final BranchFactory branchFactory = new BranchFactory(null);
			this.repo = new MercurialRepository();
			this.repo.setup(new URI("file://" + tmpDir.getAbsolutePath() + FileUtils.fileSeparator + "testHg"),
			                branchFactory, null, "master");
		} catch (final Exception e) {
			fail();
		}
	}
	
	/**
	 * Test annotate.
	 */
	@Test
	public void testAnnotate() {
		List<AnnotationEntry> annotate = this.repo.annotate("3.txt",
		                                                    MercurialRepositoryTest._637ACF68104E7BDFF8235FB2E1A254300FFEA3CB);
		
		assertEquals(3, annotate.size());
		AnnotationEntry line0 = annotate.get(0);
		assertNotNull(line0);
		assertFalse(line0.hasAlternativePath());
		assertNull(line0.getAlternativeFilePath());
		assertEquals("changing 3", line0.getLine());
		assertEquals(MercurialRepositoryTest._CBCC33D919A27B9450D117F211A5F4F45615CAB9, line0.getRevision());
		assertTrue(DateTimeUtils.parseDate("2010-11-22 20:30:52 +0100").isEqual(line0.getTimestamp()));
		assertEquals(MercurialRepositoryTest.USER_NAME, line0.getUsername());
		
		final AnnotationEntry line1 = annotate.get(1);
		assertNotNull(line1);
		assertFalse(line1.hasAlternativePath());
		assertNull(line1.getAlternativeFilePath());
		assertEquals("changing 3", line0.getLine());
		assertEquals(MercurialRepositoryTest._41A40FB23B54A49E91EB4CEE510533EEF810EC68, line1.getRevision());
		assertTrue(DateTimeUtils.parseDate("2011-01-20 12:03:24 +0100").isEqual(line1.getTimestamp()));
		assertEquals(MercurialRepositoryTest.USER_NAME, line1.getUsername());
		
		final AnnotationEntry line2 = annotate.get(2);
		assertNotNull(line2);
		assertFalse(line2.hasAlternativePath());
		assertNull(line2.getAlternativeFilePath());
		assertEquals("changing 3", line2.getLine());
		assertEquals(MercurialRepositoryTest._41A40FB23B54A49E91EB4CEE510533EEF810EC68, line2.getRevision());
		assertTrue(DateTimeUtils.parseDate("2011-01-20 12:03:24 +0100").isEqual(line2.getTimestamp()));
		assertEquals(MercurialRepositoryTest.USER_NAME, line2.getUsername());
		
		annotate = this.repo.annotate("3_renamed.txt",
		                              MercurialRepositoryTest._96A9F105774B50F1FA3361212C4D12AE057A4285);
		line0 = annotate.get(0);
		assertNotNull(line0);
		// because we would have to use `hg mv` explicitly.
		assertFalse(line0.hasAlternativePath());
		assertEquals("changing 3", line0.getLine());
		assertEquals(MercurialRepositoryTest._96A9F105774B50F1FA3361212C4D12AE057A4285, line0.getRevision());
		assertTrue(DateTimeUtils.parseDate("2012-11-28 11:26:15 +0100").isEqual(line0.getTimestamp()));
		assertEquals(MercurialRepositoryTest.USER_NAME, line0.getUsername());
		
		annotate = this.repo.annotate("2_renamed.txt",
		                              MercurialRepositoryTest._D9A5542FE1B5A755502320BA38FDF180011B40DF);
		line0 = annotate.get(0);
		assertNotNull(line0);
		assertTrue(line0.hasAlternativePath());
		assertEquals("2.txt", line0.getAlternativeFilePath());
	}
	
	/**
	 * Test checkout path fail.
	 */
	@Test
	public void testCheckoutPathFail() {
		assertTrue(this.repo.checkoutPath("3.txt", MercurialRepositoryTest._96A9F105774B50F1FA3361212C4D12AE057A4285) == null);
	}
	
	/**
	 * Test checkout path success.
	 */
	@Test
	public void testCheckoutPathSuccess() {
		final File file = this.repo.checkoutPath("3.txt",
		                                         MercurialRepositoryTest._637ACF68104E7BDFF8235FB2E1A254300FFEA3CB);
		assertNotNull(file);
		assertTrue(file.exists());
	}
	
	/**
	 * Test diff.
	 */
	@Test
	public void testDiff() {
		final Collection<Delta> diff = this.repo.diff("3.txt",
		                                              MercurialRepositoryTest._637ACF68104E7BDFF8235FB2E1A254300FFEA3CB,
		                                              MercurialRepositoryTest._9BE561B3657E2B1DA2B09D675DDDD5F45C47F57C);
		assertEquals(1, diff.size());
		final Delta delta = diff.iterator().next();
		assertEquals(0, delta.getOriginal().getSize());
		assertEquals(3, delta.getRevised().getSize());
		for (final Object line : delta.getRevised().getLines()) {
			assertEquals("changing 3", line.toString());
		}
	}
	
	/**
	 * Test get changes paths.
	 */
	@Test
	public void testGetChangesPaths() {
		Map<String, ChangeType> changedPaths = this.repo.getChangedPaths(MercurialRepositoryTest._376ADC0F9371129A76766F8030F2E576165358C1);
		assertEquals(1, changedPaths.size());
		assertTrue(changedPaths.containsKey("/1.txt"));
		assertEquals(ChangeType.Modified, changedPaths.get("/1.txt"));
		
		changedPaths = this.repo.getChangedPaths(MercurialRepositoryTest._96A9F105774B50F1FA3361212C4D12AE057A4285);
		assertEquals(2, changedPaths.size());
		assertTrue(changedPaths.containsKey("/3.txt"));
		assertEquals(ChangeType.Deleted, changedPaths.get("/3.txt"));
		assertTrue(changedPaths.containsKey("/3_renamed.txt"));
		assertEquals(ChangeType.Added, changedPaths.get("/3_renamed.txt"));
		
		changedPaths = this.repo.getChangedPaths(MercurialRepositoryTest._D9A5542FE1B5A755502320BA38FDF180011B40DF);
		assertEquals(2, changedPaths.size());
		assertTrue(changedPaths.containsKey("/2.txt"));
		assertEquals(ChangeType.Deleted, changedPaths.get("/2.txt"));
		assertTrue(changedPaths.containsKey("/2_renamed.txt"));
		assertEquals(ChangeType.Added, changedPaths.get("/2_renamed.txt"));
	}
	
	/**
	 * Test get former path name.
	 */
	@Test
	public void testGetFormerPathName() {
		String formerPathName = this.repo.getFormerPathName(MercurialRepositoryTest._96A9F105774B50F1FA3361212C4D12AE057A4285,
		                                                    "3_renamed.txt");
		assertTrue(formerPathName == null);
		
		formerPathName = this.repo.getFormerPathName(MercurialRepositoryTest._D9A5542FE1B5A755502320BA38FDF180011B40DF,
		                                             "2_renamed.txt");
		assertTrue(formerPathName != null);
		assertEquals("2.txt", formerPathName);
	}
	
	/**
	 * Test get log.
	 */
	@Test
	public void testGetLog() {
		final List<LogEntry> log = this.repo.log(MercurialRepositoryTest._98D5C40EF3C14503A472BA4133AE3529C7578E30,
		                                         MercurialRepositoryTest._376ADC0F9371129A76766F8030F2E576165358C1);
		assertEquals(6, log.size());
		LogEntry logEntry = log.get(0);
		assertEquals(MercurialRepositoryTest._98D5C40EF3C14503A472BA4133AE3529C7578E30, logEntry.getRevision());
		assertTrue(logEntry.getDateTime().isEqual(DateTimeUtils.parseDate("2010-11-22 20:26:24 +0100")));
		assertEquals("changing 1.txt", logEntry.getMessage());
		assertTrue(logEntry.getOriginalId().isEmpty());
		
		logEntry = log.get(1);
		assertEquals(MercurialRepositoryTest._AE94D7FA81437CBBD723049E3951F9DAAA62A7C0, logEntry.getRevision());
		assertTrue(logEntry.getDateTime().isEqual(DateTimeUtils.parseDate("2010-11-22 20:32:19 +0100")));
		assertEquals("Merge file:///tmp/testGit into testBranchName", logEntry.getMessage());
		assertTrue(logEntry.getOriginalId().isEmpty());
		
		logEntry = log.get(2);
		assertEquals(MercurialRepositoryTest._8273C1E51992A4D7A1DA012DBB416864C2749A7F, logEntry.getRevision());
		assertTrue(logEntry.getDateTime().isEqual(DateTimeUtils.parseDate("2010-11-22 20:34:03 +0100")));
		assertEquals("Merge branch 'testBranchName'", logEntry.getMessage());
		assertTrue(logEntry.getOriginalId().isEmpty());
		
		logEntry = log.get(3);
		assertEquals(MercurialRepositoryTest._927478915F2D8FB9135EB33D21CB8491C0E655BE, logEntry.getRevision());
		assertTrue(logEntry.getDateTime().isEqual(DateTimeUtils.parseDate("2011-01-20 12:01:23 +0100")));
		assertEquals("changing 1.txt", logEntry.getMessage());
		assertTrue(logEntry.getOriginalId().isEmpty());
		
		logEntry = log.get(4);
		assertEquals(MercurialRepositoryTest._1AC6AAA05EB6D55939B20E70EC818BB413417757, logEntry.getRevision());
		assertTrue(logEntry.getDateTime().isEqual(DateTimeUtils.parseDate("2011-01-20 12:02:30 +0100")));
		assertEquals("chaging 2.txt", logEntry.getMessage());
		assertTrue(logEntry.getOriginalId().isEmpty());
		
		logEntry = log.get(5);
		assertEquals(MercurialRepositoryTest._376ADC0F9371129A76766F8030F2E576165358C1, logEntry.getRevision());
		assertTrue(logEntry.getDateTime().isEqual(DateTimeUtils.parseDate("2011-01-20 12:03:59 +0100")));
		assertEquals("changing 1.txt", logEntry.getMessage());
		assertTrue(logEntry.getOriginalId().isEmpty());
	}
	
	/**
	 * Test get transaction count.
	 */
	@Test
	public void testGetTransactionCount() {
		assertEquals(22, this.repo.getTransactionCount());
	}
	
	/**
	 * Test get transaction id.
	 */
	@Test
	public void testGetTransactionId() {
		assertEquals(MercurialRepositoryTest._E52DEF97EBC1F78C9286B1E7C36783AA67604439, this.repo.getTransactionId(0));
		assertEquals(MercurialRepositoryTest._CBCC33D919A27B9450D117F211A5F4F45615CAB9, this.repo.getTransactionId(6));
		assertEquals(MercurialRepositoryTest._67635fe9efeb2fd3751df9ea67650c71e59e3df1, this.repo.getTransactionId(18));
		assertEquals(MercurialRepositoryTest._96A9F105774B50F1FA3361212C4D12AE057A4285, this.repo.getTransactionId(19));
	}
	
	/**
	 * Test get transaction index.
	 */
	@Test
	public void testGetTransactionIndex() {
		assertEquals(21, this.repo.getTransactionIndex("HEAD"));
		assertEquals(6,
		             this.repo.getTransactionIndex(MercurialRepositoryTest._CBCC33D919A27B9450D117F211A5F4F45615CAB9));
	}
}
