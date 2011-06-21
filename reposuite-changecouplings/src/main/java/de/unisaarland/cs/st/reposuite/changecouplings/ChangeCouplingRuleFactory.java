/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.changecouplings;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.compare.LessOrEqualDouble;
import net.ownhero.dev.kanuni.annotations.simple.Positive;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.lang.StringUtils;

import de.unisaarland.cs.st.reposuite.changecouplings.model.FileChangeCoupling;
import de.unisaarland.cs.st.reposuite.changecouplings.model.MethodChangeCoupling;
import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

/**
 * A factory for creating ChangeCouplingRule objects.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class ChangeCouplingRuleFactory {
	
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
	 * @return the change coupling rules (sorted descending by confidence and
	 *         support)
	 */
	@SuppressWarnings ("unchecked")
	@NoneNull
	public static List<FileChangeCoupling> getFileChangeCouplings(final RCSTransaction transaction,
	                                                              @Positive final int minSupport,
	                                                              @LessOrEqualDouble (ref = 1d) @Positive final double minConfidence,
	                                                              final PersistenceUtil persistenceUtil) {
		
		updateProcedures(persistenceUtil);
		
		List<FileChangeCoupling> result = new LinkedList<FileChangeCoupling>();
		
		if (!persistenceUtil.getType().toLowerCase().equals("postgresql")) {
			throw new UnrecoverableError("ChangeCouplings are currently only supported on Postgres databases! (given: "
			                             + persistenceUtil.getType() + ").");
		}
		
		String tablename = new BigInteger(130, new SecureRandom()).toString(32).toString();
		tablename = "reposuite_cc_" + tablename;
		persistenceUtil.commitTransaction();
		
		persistenceUtil.executeNativeQuery("select reposuite_file_changecouplings('" + transaction.getId() + "','"
		                                   + tablename + "')");
		List<Object[]> list = persistenceUtil.executeNativeSelectQuery("select premise, implication, support, confidence FROM "
		                                                               + tablename);
		if (list == null) {
			return null;
		}
		for (Object[] row : list) {
			int support = (Integer) row[2];
			double confidence = (Double) row[3];
			if ((support >= minSupport) && (confidence >= minConfidence)) {
				String[] premises = row[0].toString().replaceAll("\\{", "").replaceAll("\\}", "").split(",");
				Integer[] premise = new Integer[premises.length];
				for (int i = 0; i < premises.length; ++i) {
					premise[i] = Integer.valueOf(premises[i]);
				}
				result.add(new FileChangeCoupling(premise, (Integer) row[1], support, confidence, persistenceUtil));
			}
		}
		
		persistenceUtil.executeNativeQuery("DROP TABLE " + tablename);
		
		Collections.sort(result);
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
	public static List<MethodChangeCoupling> getMethodChangeCouplings(final RCSTransaction transaction,
	                                                                  @Positive final int minSupport,
	                                                                  @LessOrEqualDouble (ref = 1d) @Positive final double minConfidence,
	                                                                  final Set<String> relevantMethodNames,
	                                                                  final PersistenceUtil persistenceUtil) {
		updateProcedures(persistenceUtil);

		List<MethodChangeCoupling> result = new LinkedList<MethodChangeCoupling>();
		
		if (!persistenceUtil.getType().toLowerCase().equals("postgresql")) {
			throw new UnrecoverableError("ChangeCouplings are currently only supported on Postgres databases! (given: "
			                             + persistenceUtil.getType() + ").");
		}
		
		String tablename = new BigInteger(130, new SecureRandom()).toString(32).toString();
		tablename = "reposuite_method_cc_" + tablename;
		persistenceUtil.commitTransaction();
		
		StringBuilder query = new StringBuilder();
		query.append("select reposuite_method_changecouplings('");
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
		
		persistenceUtil.executeNativeQuery(query.toString());
		
		@SuppressWarnings ("unchecked")
		List<Object[]> list = persistenceUtil.executeNativeSelectQuery("select premise, implication, support, confidence FROM "
		                                                               + tablename);
		if (list == null) {
			return null;
		}
		for (Object[] row : list) {
			int support = (Integer) row[2];
			double confidence = (Double) row[3];
			if ((support >= minSupport) && (confidence >= minConfidence)) {
				
				// {org.joda.time.convert.ConverterManager.ConverterManager(),org.joda.time.convert.ReadableIntervalConverter.getDurationMillis(Object)}
				
				String[] premises = row[0].toString().replaceAll("\\{", "").replaceAll("\\}", "").split(",");
				result.add(new MethodChangeCoupling(premises, row[1].toString(), support, confidence, persistenceUtil));
			}
		}
		
		persistenceUtil.executeNativeQuery("DROP TABLE " + tablename);
		
		Collections.sort(result);
		return result;
	}
	
	private static void updateProcedures(final PersistenceUtil persistenceUtil){
		if (!updatedQueries) {
			try {
				URL sqlURL = ChangeCouplingRuleFactoryTest.class.getResource(FileUtils.fileSeparator
				                                                             + "change_file_couplings.psql");
				File sqlFile = new File(sqlURL.toURI());
				String query = FileUtils.readFileToString(sqlFile);
				persistenceUtil.executeNativeQuery("CREATE LANGUAGE plpythonu;");
				persistenceUtil.executeNativeQuery("CREATE LANGUAGE plpython2u;");
				persistenceUtil.executeNativeQuery(query);
				
				sqlURL = ChangeCouplingRuleFactoryTest.class.getResource(FileUtils.fileSeparator
				                                                         + "change_method_couplings.psql");
				sqlFile = new File(sqlURL.toURI());
				query = FileUtils.readFileToString(sqlFile);
				persistenceUtil.executeNativeQuery(query);
				
			} catch (URISyntaxException e) {
				if (Logger.logWarn()) {
					Logger.warn("Could not update the stored procedure to compute change couplings! Reason: "
					            + e.getMessage());
				}
			} catch (IOException e) {
				if (Logger.logWarn()) {
					Logger.warn("Could not update the stored procedure to compute change couplings! Reason: "
					            + e.getMessage());
				}
			}
			updatedQueries = true;
		}
	}
}
