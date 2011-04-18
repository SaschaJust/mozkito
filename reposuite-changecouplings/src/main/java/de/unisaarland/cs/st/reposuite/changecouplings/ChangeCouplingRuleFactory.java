package de.unisaarland.cs.st.reposuite.changecouplings;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Query;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import de.unisaarland.cs.st.reposuite.changecouplings.model.ChangeCouplingRule;
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
	public static List<ChangeCouplingRule> getChangeCouplingRules(final RCSTransaction transaction,
	                                                              final int minSupport,
	                                                              final int minConfidence,
	                                                              final PersistenceUtil persistenceUtil) {
		List<ChangeCouplingRule> result = new LinkedList<ChangeCouplingRule>();
		
		if (!persistenceUtil.getType().toLowerCase().equals("postgresql")) {
			throw new UnrecoverableError("ChangeCouplings are currently only supported on Postgres databases! (given: "
			        + persistenceUtil.getType() + ").");
		}
		
		// URL sqlFunctionURL =
		// ChangeCouplingRuleFactory.class.getResource(System
		// .getProperty("file.separator")
		// + "change_couplings.psql");
		// Condition.notNull(sqlFunctionURL);
		//
		// File sqlFile = new File(sqlFunctionURL.toURI());
		// Condition.notNull(sqlFile);
		//
		// StringBuilder sql = new StringBuilder();
		// BufferedReader reader = new BufferedReader(new
		// FileReader(sqlFile));
		// String line = null;
		// while ((line = reader.readLine()) != null) {
		// sql.append(line);
		// sql.append(FileUtils.lineSeparator);
		// }
		// reader.close();
		// // persistenceUtil.executeQuery("CREATE LANGUAGE plpython2u");
		// persistenceUtil.executeQuery(sql.toString());
		String tablename = new BigInteger(130, new SecureRandom()).toString(32).toString();
		tablename = "reposuite_cc_" + tablename;
		
		persistenceUtil.executeNativeQuery("select reposuite_changecouplings('" + transaction.getId() + "','"
		        + tablename + "')");
		Query ccRulesQuery = persistenceUtil.createQuery("select array_to_string(premise,',') AS premise, implication, support, confidence FROM "
		        + tablename);
		List<Object[]> list = ccRulesQuery.getResultList();
		if (list == null) {
			return null;
		}
		for (Object[] row : list) {
			int support = (Integer) row[2];
			double confidence = (Double) row[3];
			if ((support >= minSupport) && (confidence >= minConfidence)) {
				String[] premises = row[0].toString().split(",");
				Integer[] premise = new Integer[premises.length];
				for (int i = 0; i < premises.length; ++i) {
					premise[i] = Integer.valueOf(premises[i]);
				}
				result.add(new ChangeCouplingRule(premise, (Integer) row[1], support, confidence));
			}
		}
		
		persistenceUtil.executeNativeQuery("DROP TABLE " + tablename);
		
		Collections.sort(result);
		return result;
	}
}
