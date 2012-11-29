package org.mozkito.versions.mercurial;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

import org.junit.Before;
import org.junit.Test;
import org.mozkito.versions.BranchFactory;
import org.mozkito.versions.elements.AnnotationEntry;
import org.mozkito.versions.elements.ChangeType;

import difflib.Delta;

public class MercurialRepositoryTest {
	
	private static final String _67635fe9efeb2fd3751df9ea67650c71e59e3df1 = "2d18bf2cbffbdc6b5ad321dc2fc5e57dc810e4a9";
	private static final String _E52DEF97EBC1F78C9286B1E7C36783AA67604439 = "caac84e3edc88a6f3ec2b71bbcd6ad78445ef985";
	private static final String USER_NAME                                 = "kim";
	private static final String _96A9F105774B50F1FA3361212C4D12AE057A4285 = "b01759380f692e073bdb63d79f5238f1982d0bbd";
	private static final String _41A40FB23B54A49E91EB4CEE510533EEF810EC68 = "bc67c4d57423730912fda92ff8849d0e78d44c6f";
	private static final String _CBCC33D919A27B9450D117F211A5F4F45615CAB9 = "2398c6b6da51e317c951dba74e9d205db17fce02";
	private static final String _637ACF68104E7BDFF8235FB2E1A254300FFEA3CB = "c6b358dfd0e99376c15038d4f90d8fe2c1a766af";
	private static final String _D9A5542FE1B5A755502320BA38FDF180011B40DF = "d9a5542fe1b5a755502320ba38fdf180011b40df";
	private static final String _9BE561B3657E2B1DA2B09D675DDDD5F45C47F57C = "e17ec8036b3bf49667ec1bb7fe474e65451a98b0";
	private static final String _376ADC0F9371129A76766F8030F2E576165358C1 = "ffaaa326e3b8cd68c2396b21eb49b26a1cb3835c";
	private MercurialRepository repo;
	
	@Before
	public void setup() {
		final URL zipURL = MercurialRepositoryTest.class.getResource(FileUtils.fileSeparator + "testHg.zip");
		assertTrue(zipURL != null);
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
	
	@Test
	public void testAnnotate() {
		List<AnnotationEntry> annotate = this.repo.annotate("3.txt", _637ACF68104E7BDFF8235FB2E1A254300FFEA3CB);
		
		assertEquals(3, annotate.size());
		AnnotationEntry line0 = annotate.get(0);
		assertTrue(line0 != null);
		assertFalse(line0.hasAlternativePath());
		assertTrue(line0.getAlternativeFilePath() == null);
		assertEquals("changing 3", line0.getLine());
		assertEquals(_CBCC33D919A27B9450D117F211A5F4F45615CAB9, line0.getRevision());
		assertTrue(DateTimeUtils.parseDate("2010-11-22 20:30:52 +0100").isEqual(line0.getTimestamp()));
		assertEquals(USER_NAME, line0.getUsername());
		
		final AnnotationEntry line1 = annotate.get(1);
		assertTrue(line1 != null);
		assertFalse(line1.hasAlternativePath());
		assertTrue(line1.getAlternativeFilePath() == null);
		assertEquals("changing 3", line0.getLine());
		assertEquals(_41A40FB23B54A49E91EB4CEE510533EEF810EC68, line1.getRevision());
		assertTrue(DateTimeUtils.parseDate("2011-01-20 12:03:24 +0100").isEqual(line1.getTimestamp()));
		assertEquals(USER_NAME, line1.getUsername());
		
		final AnnotationEntry line2 = annotate.get(2);
		assertTrue(line2 != null);
		assertFalse(line2.hasAlternativePath());
		assertTrue(line2.getAlternativeFilePath() == null);
		assertEquals("changing 3", line2.getLine());
		assertEquals(_41A40FB23B54A49E91EB4CEE510533EEF810EC68, line2.getRevision());
		assertTrue(DateTimeUtils.parseDate("2011-01-20 12:03:24 +0100").isEqual(line2.getTimestamp()));
		assertEquals(USER_NAME, line2.getUsername());
		
		annotate = this.repo.annotate("3_renamed.txt", _96A9F105774B50F1FA3361212C4D12AE057A4285);
		line0 = annotate.get(0);
		assertTrue(line0 != null);
		// because we would have to use `hg mv` explicitly.
		assertFalse(line0.hasAlternativePath());
		assertEquals("changing 3", line0.getLine());
		assertEquals(_96A9F105774B50F1FA3361212C4D12AE057A4285, line0.getRevision());
		assertTrue(DateTimeUtils.parseDate("2012-11-28 11:26:15 +0100").isEqual(line0.getTimestamp()));
		assertEquals(USER_NAME, line0.getUsername());
		
		annotate = this.repo.annotate("2_renamed.txt", _D9A5542FE1B5A755502320BA38FDF180011B40DF);
		line0 = annotate.get(0);
		assertTrue(line0 != null);
		assertTrue(line0.hasAlternativePath());
		assertEquals("2.txt", line0.getAlternativeFilePath());
	}
	
	@Test
	public void testCheckoutPathFail() {
		assertTrue(this.repo.checkoutPath("3.txt", _96A9F105774B50F1FA3361212C4D12AE057A4285) == null);
	}
	
	@Test
	public void testCheckoutPathSuccess() {
		final File file = this.repo.checkoutPath("3.txt", _637ACF68104E7BDFF8235FB2E1A254300FFEA3CB);
		assertTrue(file != null);
		assertTrue(file.exists());
	}
	
	@Test
	public void testDiff() {
		final Collection<Delta> diff = this.repo.diff("3.txt", _637ACF68104E7BDFF8235FB2E1A254300FFEA3CB,
		                                              _9BE561B3657E2B1DA2B09D675DDDD5F45C47F57C);
		assertEquals(1, diff.size());
		final Delta delta = diff.iterator().next();
		assertEquals(0, delta.getOriginal().getSize());
		assertEquals(3, delta.getRevised().getSize());
		for (final Object line : delta.getRevised().getLines()) {
			assertEquals("changing 3", line.toString());
		}
	}
	
	@Test
	public void testGetChangesPaths() {
		Map<String, ChangeType> changedPaths = this.repo.getChangedPaths(_376ADC0F9371129A76766F8030F2E576165358C1);
		assertEquals(1, changedPaths.size());
		assertTrue(changedPaths.containsKey("/1.txt"));
		assertEquals(ChangeType.Modified, changedPaths.get("/1.txt"));
		
		changedPaths = this.repo.getChangedPaths(_96A9F105774B50F1FA3361212C4D12AE057A4285);
		assertEquals(2, changedPaths.size());
		assertTrue(changedPaths.containsKey("/3.txt"));
		assertEquals(ChangeType.Deleted, changedPaths.get("/3.txt"));
		assertTrue(changedPaths.containsKey("/3_renamed.txt"));
		assertEquals(ChangeType.Added, changedPaths.get("/3_renamed.txt"));
		
		changedPaths = this.repo.getChangedPaths(_D9A5542FE1B5A755502320BA38FDF180011B40DF);
		assertEquals(2, changedPaths.size());
		assertTrue(changedPaths.containsKey("/2.txt"));
		assertEquals(ChangeType.Deleted, changedPaths.get("/2.txt"));
		assertTrue(changedPaths.containsKey("/2_renamed.txt"));
		assertEquals(ChangeType.Added, changedPaths.get("/2_renamed.txt"));
	}
	
	@Test
	public void testGetFormerPathName() {
		String formerPathName = this.repo.getFormerPathName(_96A9F105774B50F1FA3361212C4D12AE057A4285, "3_renamed.txt");
		assertTrue(formerPathName == null);
		
		formerPathName = this.repo.getFormerPathName(_D9A5542FE1B5A755502320BA38FDF180011B40DF, "2_renamed.txt");
		assertTrue(formerPathName != null);
		assertEquals("2.txt", formerPathName);
	}
	
	@Test
	public void testGetLog() {
		// TODO write this test
	}
	
	@Test
	public void testGetRelativeTransactionId() {
		// TODO write this test
	}
	
	@Test
	public void testGetTransactionCount() {
		assertEquals(22, this.repo.getTransactionCount());
	}
	
	@Test
	public void testGetTransactionId() {
		assertEquals(_E52DEF97EBC1F78C9286B1E7C36783AA67604439, this.repo.getTransactionId(0));
		assertEquals(_CBCC33D919A27B9450D117F211A5F4F45615CAB9, this.repo.getTransactionId(6));
		assertEquals(_67635fe9efeb2fd3751df9ea67650c71e59e3df1, this.repo.getTransactionId(18));
		assertEquals(_96A9F105774B50F1FA3361212C4D12AE057A4285, this.repo.getTransactionId(19));
	}
}
