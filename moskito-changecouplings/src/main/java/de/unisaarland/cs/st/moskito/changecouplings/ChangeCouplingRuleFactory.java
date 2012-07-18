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
package de.unisaarland.cs.st.moskito.changecouplings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.compare.LessOrEqualDouble;
import net.ownhero.dev.kanuni.annotations.simple.Positive;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.lang.StringUtils;

import au.com.bytecode.opencsv.CSVReader;
import de.unisaarland.cs.st.moskito.changecouplings.model.FileChangeCoupling;
import de.unisaarland.cs.st.moskito.changecouplings.model.MethodChangeCoupling;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

/**
 * A factory for creating ChangeCouplingRule objects.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class ChangeCouplingRuleFactory {
	
	/** The updated queries. */
	private static boolean updatedQueries = false;
	
	/**
	 * Gets the change coupling rules.
	 * 
	 * @param transaction
	 *            the transaction
	 * @param minSupport
	 *            the min support
	 * @param minConfidence
	 *            the min confidence
	 * @param persistenceUtil
	 *            the persistence util
	 * @return the change coupling rules (sorted descending by confidence and support)
	 */
	@SuppressWarnings ("unchecked")
	@NoneNull
	public static LinkedList<FileChangeCoupling> getFileChangeCouplings(final RCSTransaction transaction,
	                                                                    @Positive final int minSupport,
	                                                                    final double minConfidence,
	                                                                    final PersistenceUtil persistenceUtil) {
		
		updateProcedures(persistenceUtil);
		
		final LinkedList<FileChangeCoupling> result = new LinkedList<FileChangeCoupling>();
		
		if (!persistenceUtil.getType().toLowerCase().equals("postgresql")) {
			throw new UnrecoverableError("ChangeCouplings are currently only supported on Postgres databases! (given: "
			        + persistenceUtil.getType() + ").");
		}
		
		String tablename = new BigInteger(130, new SecureRandom()).toString(32).toString();
		tablename = "mozkito_cc_" + tablename;
		persistenceUtil.commitTransaction();
		
		final String query = "select mozkito_file_changecouplings('" + transaction.getId() + "','" + tablename + "')";
		
		if (Logger.logTrace()) {
			Logger.trace("Selecting file change couplings: " + query);
		}
		
		persistenceUtil.executeNativeQuery(query);
		final List<Object[]> list = persistenceUtil.executeNativeSelectQuery("select premise, implication, support, confidence FROM "
		        + tablename);
		
		if (Logger.logTrace()) {
			Logger.trace("Found %s file change couplings for %s.", String.valueOf(list.size()), transaction.getId());
		}
		
		if (list == null) {
			return null;
		}
		for (final Object[] row : list) {
			final int support = (Integer) row[2];
			final double confidence = (Double) row[3];
			if ((support >= minSupport) && (confidence >= minConfidence)) {
				final String[] premises = row[0].toString().replaceAll("\\{", "").replaceAll("\\}", "").split(",");
				final Integer[] premise = new Integer[premises.length];
				for (int i = 0; i < premises.length; ++i) {
					premise[i] = Integer.valueOf(premises[i]);
				}
				result.add(new FileChangeCoupling(premise, (Integer) row[1], support, confidence, persistenceUtil));
			}
		}
		
		persistenceUtil.executeNativeQuery("DROP TABLE " + tablename);
		Collections.sort(result);
		
		if (Logger.logDebug()) {
			Logger.debug("Found %s file change couplings for %s with minSupport=%s and minConfidence=%s.",
			             String.valueOf(result.size()), transaction.getId(), String.valueOf(minSupport),
			             String.valueOf(minConfidence));
		}
		
		return result;
	}
	
	/**
	 * Gets the method change couplings.
	 * 
	 * @param transaction
	 *            the transaction
	 * @param minSupport
	 *            the min support
	 * @param minConfidence
	 *            the min confidence
	 * @param relevantMethodNames
	 *            the relevant method names
	 * @param persistenceUtil
	 *            the persistence util
	 * @return the method change couplings
	 */
	@NoneNull
	public static synchronized LinkedList<MethodChangeCoupling> getMethodChangeCouplings(final RCSTransaction transaction,
	                                                                                     @Positive final int minSupport,
	                                                                                     @LessOrEqualDouble (ref = 1d) @Positive final double minConfidence,
	                                                                                     final Set<String> relevantMethodNames,
	                                                                                     final PersistenceUtil persistenceUtil) {
		updateProcedures(persistenceUtil);
		
		final LinkedList<MethodChangeCoupling> result = new LinkedList<MethodChangeCoupling>();
		
		if (!persistenceUtil.getType().toLowerCase().equals("postgresql")) {
			throw new UnrecoverableError("ChangeCouplings are currently only supported on Postgres databases! (given: "
			        + persistenceUtil.getType() + ").");
		}
		
		String tablename = new BigInteger(130, new SecureRandom()).toString(32).toString();
		tablename = "mozkito_method_cc_" + tablename;
		persistenceUtil.commitTransaction();
		
		final StringBuilder query = new StringBuilder();
		query.append("select mozkito_method_changecouplings('");
		query.append(transaction.getId());
		query.append("','");
		query.append(tablename);
		query.append("', ARRAY[");
		if (relevantMethodNames.size() > 0) {
			query.append("'");
			query.append(StringUtils.join(relevantMethodNames, "','"));
			query.append("'");
		}
		query.append("]::text[])");
		
		if (Logger.logTrace()) {
			Logger.trace("Firing native query: %s.", query.toString());
		}
		
		persistenceUtil.executeNativeQuery(query.toString());
		
		@SuppressWarnings ("unchecked")
		final List<Object[]> list = persistenceUtil.executeNativeSelectQuery("select premise, implication, support, confidence FROM "
		        + tablename);
		if (list == null) {
			return null;
		}
		for (final Object[] row : list) {
			final int support = (Integer) row[2];
			final double confidence = (Double) row[3];
			if ((support >= minSupport) && (confidence >= minConfidence)) {
				
				String premiseStr = row[0].toString();
				premiseStr = premiseStr.substring(1, premiseStr.length() - 1);
				try (StringReader sReader = new StringReader(premiseStr);
				        final CSVReader csvReader = new CSVReader(sReader, ',', '"');) {
					final String[] premises = csvReader.readNext();
					result.add(new MethodChangeCoupling(premises, row[1].toString(), support, confidence,
					                                    persistenceUtil));
				} catch (final IOException e) {
					//
				}
			}
		}
		
		persistenceUtil.executeNativeQuery("DROP TABLE " + tablename);
		
		Collections.sort(result);
		return result;
	}
	
	/**
	 * Update procedures.
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 */
	private static void updateProcedures(final PersistenceUtil persistenceUtil) {
		if (!updatedQueries) {
			try {
				BufferedReader reader = new BufferedReader(
				                                           new InputStreamReader(
				                                                                 ChangeCouplingRuleFactory.class.getResourceAsStream(FileUtils.fileSeparator
				                                                                         + "change_file_couplings.psql")));
				
				StringBuilder query = new StringBuilder();
				String line = "";
				while ((line = reader.readLine()) != null) {
					query.append(line);
					query.append(FileUtils.lineSeparator);
				}
				persistenceUtil.executeNativeQuery("CREATE LANGUAGE plpythonu;");
				persistenceUtil.executeNativeQuery("CREATE LANGUAGE plpython2u;");
				persistenceUtil.executeNativeQuery(query.toString());
				
				reader = new BufferedReader(
				                            new InputStreamReader(
				                                                  ChangeCouplingRuleFactory.class.getResourceAsStream(FileUtils.fileSeparator
				                                                          + "change_method_couplings.psql")));
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
