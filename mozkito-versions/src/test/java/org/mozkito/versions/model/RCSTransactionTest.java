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
		
		final RCSTransaction t_0 = new RCSTransaction("0", "", new DateTime(), person, "");
		final RCSFile rCSFile = new RCSFile("public.java", t_0);
		new RCSRevision(t_0, rCSFile, ChangeType.Added);
		
		final Collection<RCSFile> changedFiles = t_0.getChangedFiles();
		assertEquals(1, changedFiles.size());
		assertTrue(changedFiles.contains(rCSFile));
		
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
		
		final RCSTransaction t_0 = new RCSTransaction("0", "", new DateTime(), person, "");
		final RCSFile rCSFile = new RCSFile("public.java", t_0);
		new RCSRevision(t_0, rCSFile, ChangeType.Added);
		
		final RCSTransaction t_1 = new RCSTransaction("1", "", new DateTime(), person, "");
		new RCSRevision(t_1, rCSFile, ChangeType.Modified);
		t_1.setBranchParent(t_0);
		
		final RCSTransaction t_2 = new RCSTransaction("2", "", new DateTime(), person, "");
		final RCSFile hiddenFile = new RCSFile("hidden.java", t_2);
		new RCSRevision(t_2, hiddenFile, ChangeType.Added);
		t_2.setBranchParent(t_0);
		
		final RCSTransaction t_3 = new RCSTransaction("3", "", new DateTime(), person, "");
		hiddenFile.assignTransaction(t_3, "moreHidden.java");
		final RCSRevision rCSRevision = new RCSRevision(t_3, hiddenFile, ChangeType.Renamed);
		t_3.setBranchParent(t_2);
		
		final RCSTransaction t_4 = new RCSTransaction("4", "", new DateTime(), person, "");
		final RCSRevision revision2 = new RCSRevision(t_4, hiddenFile, ChangeType.Modified);
		t_4.setBranchParent(t_2);
		t_4.setMergeParent(t_3);
		
		final RCSTransaction t_5 = new RCSTransaction("5", "", new DateTime(), person, "");
		new RCSRevision(t_5, rCSFile, ChangeType.Modified);
		t_5.setBranchParent(t_1);
		t_5.setMergeParent(t_4);
		
		assertEquals(null, t_4.getRevisionForPath("hubba"));
		assertEquals(revision2, t_4.getRevisionForPath("moreHidden.java"));
		assertEquals(revision2, t_4.getRevisionForPath("/moreHidden.java"));
		assertEquals(rCSRevision, t_3.getRevisionForPath("moreHidden.java"));
		assertEquals(rCSRevision, t_3.getRevisionForPath("/moreHidden.java"));
	}
	
}
