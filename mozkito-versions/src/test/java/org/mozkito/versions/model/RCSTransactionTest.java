package org.mozkito.versions.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mozkito.persistence.model.Person;
import org.mozkito.versions.elements.ChangeType;

public class RCSTransactionTest {
	
	@Test
	public void testGetChangedFiles() {
		final Person person = new Person("kim", "", "");
		
		final Transaction t_0 = new Transaction("0", "", new DateTime(), person, "");
		final File file = new File("public.java", t_0);
		new Revision(t_0, file, ChangeType.Added);
		
		final Collection<File> changedFiles = t_0.getChangedFiles();
		assertEquals(1, changedFiles.size());
		assertTrue(changedFiles.contains(file));
		
	}
	
	@Test
	public void testGetRevisionForPath() {
		/*
		 * @formatter:off
		 * 
		 *    *   5
	     *    |\
	     *    | *   4
	     *    | |\
	     *    | | * 3 <<---- Renaming file Hidden.java to moreHidden.java
	     *    | |/
	     *    | * 2 <<---- Adding file Hidden.java
	     *    * | 1
	     *    |/
	     *    * 0
		 * 
		 */
		
		//@formatter:on
		
		final Person person = new Person("kim", "", "");
		
		final Transaction t_0 = new Transaction("0", "", new DateTime(), person, "");
		final File file = new File("public.java", t_0);
		new Revision(t_0, file, ChangeType.Added);
		
		final Transaction t_1 = new Transaction("1", "", new DateTime(), person, "");
		new Revision(t_1, file, ChangeType.Modified);
		t_1.setBranchParent(t_0);
		
		final Transaction t_2 = new Transaction("2", "", new DateTime(), person, "");
		final File hiddenFile = new File("hidden.java", t_2);
		new Revision(t_2, hiddenFile, ChangeType.Added);
		t_2.setBranchParent(t_0);
		
		final Transaction t_3 = new Transaction("3", "", new DateTime(), person, "");
		hiddenFile.assignTransaction(t_3, "moreHidden.java");
		final Revision revision = new Revision(t_3, hiddenFile, ChangeType.Renamed);
		t_3.setBranchParent(t_2);
		
		final Transaction t_4 = new Transaction("4", "", new DateTime(), person, "");
		final Revision revision2 = new Revision(t_4, hiddenFile, ChangeType.Modified);
		t_4.setBranchParent(t_2);
		t_4.setMergeParent(t_3);
		
		final Transaction t_5 = new Transaction("5", "", new DateTime(), person, "");
		new Revision(t_5, file, ChangeType.Modified);
		t_5.setBranchParent(t_1);
		t_5.setMergeParent(t_4);
		
		assertEquals(null, t_4.getRevisionForPath("hubba"));
		assertEquals(revision2, t_4.getRevisionForPath("moreHidden.java"));
		assertEquals(revision2, t_4.getRevisionForPath("/moreHidden.java"));
		assertEquals(revision, t_3.getRevisionForPath("moreHidden.java"));
		assertEquals(revision, t_3.getRevisionForPath("/moreHidden.java"));
	}
	
}
