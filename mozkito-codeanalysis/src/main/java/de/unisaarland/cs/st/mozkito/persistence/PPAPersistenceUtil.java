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
package de.unisaarland.cs.st.mozkito.persistence;

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

import net.ownhero.dev.ioda.DateTimeUtils;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import de.unisaarland.cs.st.mozkito.codeanalysis.model.JavaChangeOperation;
import de.unisaarland.cs.st.mozkito.codeanalysis.model.JavaElement;
import de.unisaarland.cs.st.mozkito.persistence.Criteria;
import de.unisaarland.cs.st.mozkito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.mozkito.rcs.model.RCSRevision;
import de.unisaarland.cs.st.mozkito.rcs.model.RCSTransaction;

/**
 * The Class PPAPersistenceUtil.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class PPAPersistenceUtil {
	
	/** The updated queries. */
	private static boolean updatedQueries = false;
	
	/**
	 * Gets the change operation.
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 * @param transaction
	 *            the transaction
	 * @return the change operation
	 */
	public static Collection<JavaChangeOperation> getChangeOperation(@NotNull final PersistenceUtil persistenceUtil,
	                                                                 @NotNull final RCSTransaction transaction) {
		final List<JavaChangeOperation> result = new LinkedList<JavaChangeOperation>();
		
		if (Logger.logDebug()) {
			Logger.debug("Loading change operations for transaction " + transaction.getId() + " from database.");
		}
		
		for (final RCSRevision revision : transaction.getRevisions()) {
			final Criteria<JavaChangeOperation> criteria = persistenceUtil.createCriteria(JavaChangeOperation.class);
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
	 * @param transactionId
	 *            the transaction id
	 * @return the change operation
	 */
	public static Collection<JavaChangeOperation> getChangeOperation(@NotNull final PersistenceUtil persistenceUtil,
	                                                                 @NotNull final String transactionId) {
		final List<JavaChangeOperation> result = new ArrayList<JavaChangeOperation>(0);
		
		final RCSTransaction transaction = persistenceUtil.loadById(transactionId, RCSTransaction.class);
		if (transaction != null) {
			return getChangeOperation(persistenceUtil, transaction);
		}
		
		return result;
	}
	
	/**
	 * Gets ChangeOperations for a RCSTransaction but ignore test cases. Test cases are all files that contain the
	 * string `test` in it's lower case path name. This might not be 100% accurate but is a good approximation.
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 * @param transaction
	 *            the transaction
	 * @return the change operation no test
	 */
	public static Collection<JavaChangeOperation> getChangeOperationNoTest(@NotNull final PersistenceUtil persistenceUtil,
	                                                                       @NotNull final RCSTransaction transaction) {
		final List<JavaChangeOperation> result = new LinkedList<JavaChangeOperation>();
		
		if (Logger.logDebug()) {
			Logger.debug("Loading change operations (without tests) for transaction " + transaction.getId()
			        + " from database.");
		}
		
		for (final RCSRevision revision : transaction.getRevisions()) {
			final String changedPath = revision.getChangedFile().getPath(transaction);
			if (changedPath.toLowerCase().contains("test")) {
				continue;
			}
			final Criteria<JavaChangeOperation> criteria = persistenceUtil.createCriteria(JavaChangeOperation.class);
			criteria.eq("revision", revision);
			result.addAll(persistenceUtil.load(criteria));
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
	public static RCSTransaction getFirstTransactionsChangingElement(@NotNull final PersistenceUtil persistenceUtil,
	                                                                 @NotNull final JavaElement element) {
		updateProcedures(persistenceUtil);
		final StringBuilder query = new StringBuilder();
		query.append("select * from firstElementChange(");
		query.append(element.getGeneratedId());
		query.append(");");
		final Query nativeQuery = persistenceUtil.createNativeQuery(query.toString(), RCSTransaction.class);
		if (nativeQuery != null) {
			final List<RCSTransaction> resultList = nativeQuery.getResultList();
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
		final Criteria<? extends JavaElement> criteria = persistenceUtil.createCriteria(e.getClass());
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
	public static List<RCSTransaction> getTransactionsChangingElement(@NotNull final PersistenceUtil persistenceUtil,
	                                                                  @NotNull final JavaElement element) {
		updateProcedures(persistenceUtil);
		final List<RCSTransaction> result = new LinkedList<RCSTransaction>();
		final StringBuilder query = new StringBuilder();
		query.append("select * from elementChanges(");
		query.append(element.getGeneratedId());
		query.append(");");
		final Query nativeQuery = persistenceUtil.createNativeQuery(query.toString(), RCSTransaction.class);
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
	public static List<RCSTransaction> getTransactionsChangingElement(@NotNull final PersistenceUtil persistenceUtil,
	                                                                  @NotNull final JavaElement element,
	                                                                  @NotNull final DateTime before,
	                                                                  @NotNull final DateTime after) {
		updateProcedures(persistenceUtil);
		final List<RCSTransaction> result = new LinkedList<RCSTransaction>();
		final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
		
		final StringBuilder query = new StringBuilder();
		query.append("select * from elementChangesBetween(");
		query.append(element.getGeneratedId());
		query.append(",'");
		query.append(formatter.print(before));
		query.append("','");
		query.append(formatter.print(after));
		query.append("');");
		final Query nativeQuery = persistenceUtil.createNativeQuery(query.toString(), RCSTransaction.class);
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
	 * Update procedures.
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 */
	private static void updateProcedures(@NotNull final PersistenceUtil persistenceUtil) {
		if (!updatedQueries) {
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
			updatedQueries = true;
		}
	}
}
