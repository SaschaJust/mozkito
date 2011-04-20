package de.unisaarland.cs.st.reposuite.changecouplings;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;

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
	                                                              final int minSupport,
	                                                              final int minConfidence,
	                                                              final PersistenceUtil persistenceUtil) {
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
	
	public static List<MethodChangeCoupling> getMethodChangeCouplings(final RCSTransaction transaction,
	                                                                  final int minSupport,
	                                                                  final int minConfidence,
	                                                                  final Set<String> relevantMethodNames,
	                                                                  final PersistenceUtil persistenceUtil) {
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
}
