/*******************************************************************************
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
 ******************************************************************************/
package org.mozkito.persistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.codeanalysis.model.JavaElement;
import org.mozkito.utilities.datetime.DateTimeUtils;
import org.mozkito.utilities.io.FileUtils;
import org.mozkito.versions.exceptions.NoSuchHandleException;
import org.mozkito.versions.model.ChangeSet;
import org.mozkito.versions.model.Revision;

/**
 * The Class PPAPersistenceUtil.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class PPAPersistenceUtil {
	
	/** The updated queries. */
	private static boolean updatedQueries = false;
	
	/**
	 * Gets the change operation.
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 * @param changeSet
	 *            the transaction
	 * @return the change operation
	 */
	public static Collection<JavaChangeOperation> getChangeOperation(@NotNull final PersistenceUtil persistenceUtil,
	                                                                 @NotNull final ChangeSet changeSet) {
		final List<JavaChangeOperation> result = new LinkedList<JavaChangeOperation>();
		
		if (Logger.logDebug()) {
			Logger.debug("Loading change operations for transaction " + changeSet.getId() + " from database.");
		}
		
		for (final Revision revision : changeSet.getRevisions()) {
			final JPACriteria<JavaChangeOperation> criteria = persistenceUtil.createCriteria(JavaChangeOperation.class);
			criteria.eq("revision", revision);
			result.addAll(persistenceUtil.load(criteria));
		}
		return result;
	}
	
	/**
	 * Gets the change operation.
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 * @param changeSetId
	 *            the transaction id
	 * @return the change operation
	 */
	public static Collection<JavaChangeOperation> getChangeOperation(@NotNull final PersistenceUtil persistenceUtil,
	                                                                 @NotNull final String changeSetId) {
		final List<JavaChangeOperation> result = new ArrayList<JavaChangeOperation>(0);
		
		final ChangeSet changeSet = persistenceUtil.loadById(changeSetId, ChangeSet.class);
		if (changeSet != null) {
			return getChangeOperation(persistenceUtil, changeSet);
		}
		
		return result;
	}
	
	/**
	 * Gets ChangeOperations for a Transaction but ignore test cases. Test cases are all files that contain the string
	 * `test` in it's lower case path name. This might not be 100% accurate but is a good approximation.
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 * @param changeSet
	 *            the transaction
	 * @return the change operation no test
	 */
	public static Collection<JavaChangeOperation> getChangeOperationNoTest(@NotNull final PersistenceUtil persistenceUtil,
	                                                                       @NotNull final ChangeSet changeSet) {
		final List<JavaChangeOperation> result = new LinkedList<JavaChangeOperation>();
		
		if (Logger.logDebug()) {
			Logger.debug("Loading change operations (without tests) for transaction " + changeSet.getId()
			        + " from database.");
		}
		
		for (final Revision revision : changeSet.getRevisions()) {
			try {
				final String changedPath = revision.getChangedFile().getPath(changeSet);
				
				if (changedPath.toLowerCase().contains("test")) {
					continue;
				}
				final JPACriteria<JavaChangeOperation> criteria = persistenceUtil.createCriteria(JavaChangeOperation.class);
				criteria.eq("revision", revision);
				result.addAll(persistenceUtil.load(criteria));
			} catch (final NoSuchHandleException e) {
				if (Logger.logWarn()) {
					Logger.warn("Could not determine path name for Handle %s in ChangeSet %s.",
					            revision.getChangedFile().toString(), changeSet.toString());
				}
			}
		}
		return result;
	}
	
	/**
	 * Gets the transactions that changed the specified JavaElement.
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 * @param element
	 *            the element
	 * @return the transactions changing element. The collection is empty if no such transactions exist.
	 */
	@SuppressWarnings ("unchecked")
	@NoneNull
	public static List<ChangeSet> getChangeSetChangingElement(@NotNull final PersistenceUtil persistenceUtil,
	                                                          @NotNull final JavaElement element) {
		updateProcedures(persistenceUtil);
		final List<ChangeSet> result = new LinkedList<ChangeSet>();
		final StringBuilder query = new StringBuilder();
		query.append("select * from elementChanges(");
		query.append(element.getGeneratedId());
		query.append(");");
		final Query nativeQuery = persistenceUtil.createNativeQuery(query.toString(), ChangeSet.class);
		if (nativeQuery != null) {
			result.addAll(nativeQuery.getResultList());
		} else {
			if (Logger.logError()) {
				Logger.error("Could not create native query: " + query.toString());
			}
		}
		return result;
	}
	
	/**
	 * Gets the collection of transactions changing element the given JavaElement within the given time window.
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 * @param element
	 *            the element
	 * @param before
	 *            the before
	 * @param after
	 *            the after
	 * @return the transactions changing element. The collection is empty if no such transactions exist.
	 */
	@SuppressWarnings ("unchecked")
	@NoneNull
	public static List<ChangeSet> getChangeSetChangingElement(@NotNull final PersistenceUtil persistenceUtil,
	                                                          @NotNull final JavaElement element,
	                                                          @NotNull final DateTime before,
	                                                          @NotNull final DateTime after) {
		updateProcedures(persistenceUtil);
		final List<ChangeSet> result = new LinkedList<ChangeSet>();
		final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
		
		final StringBuilder query = new StringBuilder();
		query.append("select * from elementChangesBetween(");
		query.append(element.getGeneratedId());
		query.append(",'");
		query.append(formatter.print(before));
		query.append("','");
		query.append(formatter.print(after));
		query.append("');");
		final Query nativeQuery = persistenceUtil.createNativeQuery(query.toString(), ChangeSet.class);
		if (nativeQuery != null) {
			result.addAll(nativeQuery.getResultList());
		} else {
			if (Logger.logError()) {
				Logger.error("Could not create native query: " + query.toString());
			}
		}
		return result;
		
	}
	
	/**
	 * Gets the first timestamp changing element.
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 * @param element
	 *            the element
	 * @return the first timestamp changing element
	 */
	public static DateTime getFirstTimestampChangingElement(@NotNull final PersistenceUtil persistenceUtil,
	                                                        @NotNull final JavaElement element) {
		updateProcedures(persistenceUtil);
		final StringBuilder query = new StringBuilder();
		query.append("select timestamp from firstelementchanges WHERE element_generatedid = ");
		query.append(element.getGeneratedId());
		@SuppressWarnings ("rawtypes")
		final List result = persistenceUtil.executeNativeSelectQuery(query.toString());
		if (result.isEmpty()) {
			return null;
		}
		return DateTimeUtils.parseDate(result.get(0).toString());
	}
	
	/**
	 * Gets the first transactions changing the specified JavaElement.
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 * @param element
	 *            the element
	 * @return the first transactions changing element. Returns NULL if no such transaction exists.
	 */
	@SuppressWarnings ("unchecked")
	@NoneNull
	public static ChangeSet getFirstTransactionsChangingElement(@NotNull final PersistenceUtil persistenceUtil,
	                                                            @NotNull final JavaElement element) {
		updateProcedures(persistenceUtil);
		final StringBuilder query = new StringBuilder();
		query.append("select * from firstElementChange(");
		query.append(element.getGeneratedId());
		query.append(");");
		final Query nativeQuery = persistenceUtil.createNativeQuery(query.toString(), ChangeSet.class);
		if (nativeQuery != null) {
			final List<ChangeSet> resultList = nativeQuery.getResultList();
			if (!resultList.isEmpty()) {
				return resultList.get(0);
			}
		} else {
			if (Logger.logError()) {
				Logger.error("Could not create native query: " + query.toString());
			}
		}
		return null;
		
	}
	
	/**
	 * Gets the java element.
	 * 
	 * @param persistenceUtil
	 *            the persistence middleware util
	 * @param e
	 *            the e
	 * @return the java element
	 */
	public static JavaElement getJavaElement(@NotNull final PersistenceUtil persistenceUtil,
	                                         @NotNull final JavaElement e) {
		final JPACriteria<? extends JavaElement> criteria = persistenceUtil.createCriteria(e.getClass());
		final CriteriaBuilder cb = criteria.getBuilder();
		final Root<? extends JavaElement> root = criteria.getRoot();
		final Predicate predicate = cb.and(cb.equal(root.get("fullQualifiedName"), e.getFullQualifiedName()),
		                                   cb.equal(root.get("elementType"), e.getElementType()));
		criteria.getQuery().where(predicate);
		
		final List<? extends JavaElement> elements = persistenceUtil.load(criteria);
		if (elements.isEmpty()) {
			return null;
		}
		if (elements.size() > 1) {
			if (Logger.logError()) {
				Logger.error("Found more that one JavaElement with same primaryKey! This is impossible!");
			}
			return null;
		}
		return elements.get(0);
	}
	
	/**
	 * Update procedures.
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 */
	private static void updateProcedures(@NotNull final PersistenceUtil persistenceUtil) {
		if (!PPAPersistenceUtil.updatedQueries) {
			try {
				BufferedReader reader = new BufferedReader(
				                                           new InputStreamReader(
				                                                                 PPAPersistenceUtil.class.getResourceAsStream(FileUtils.fileSeparator
				                                                                         + "elementChangesBetween.psql")));
				
				StringBuilder query = new StringBuilder();
				String line = "";
				while ((line = reader.readLine()) != null) {
					query.append(line);
					query.append(FileUtils.lineSeparator);
				}
				persistenceUtil.executeNativeQuery(query.toString());
				
				reader = new BufferedReader(
				                            new InputStreamReader(
				                                                  PPAPersistenceUtil.class.getResourceAsStream(FileUtils.fileSeparator
				                                                          + "firstElementChange.psql")));
				query = new StringBuilder();
				line = "";
				while ((line = reader.readLine()) != null) {
					query.append(line);
					query.append(FileUtils.lineSeparator);
				}
				persistenceUtil.executeNativeQuery(query.toString());
				
				reader = new BufferedReader(
				                            new InputStreamReader(
				                                                  PPAPersistenceUtil.class.getResourceAsStream(FileUtils.fileSeparator
				                                                          + "elementChanges.psql")));
				query = new StringBuilder();
				line = "";
				while ((line = reader.readLine()) != null) {
					query.append(line);
					query.append(FileUtils.lineSeparator);
				}
				persistenceUtil.executeNativeQuery(query.toString());
				
			} catch (final IOException e) {
				if (Logger.logWarn()) {
					Logger.warn("Could not update the stored procedure to compute change couplings! Reason: "
					        + e.getMessage());
				}
			}
			PPAPersistenceUtil.updatedQueries = true;
		}
	}
}
