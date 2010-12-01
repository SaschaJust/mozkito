package de.unisaarland.cs.st.reposuite.changecouplings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;

import de.unisaarland.cs.st.reposuite.changecouplings.model.ChangeCouplingRule;
import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
import de.unisaarland.cs.st.reposuite.rcs.RepositoryTest;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.utils.Condition;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.specification.NoneNull;

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
	public static Collection<ChangeCouplingRule> getChangeCouplingRules(final RCSTransaction transaction,
			final int minSupport, final int minConfidence) {
		
		Condition.notNull(transaction);
		
		try {
			
			List<ChangeCouplingRule> result = new LinkedList<ChangeCouplingRule>();
			HibernateUtil hibernateUtil = HibernateUtil.getInstance();
			
			if (!HibernateUtil.getType().toLowerCase().equals("postgresql")) {
				throw new UnrecoverableError("ChangeCouplings are currently only supported on Postgres databases!");
			}
			
			URL sqlFunctionURL = RepositoryTest.class.getResource(System.getProperty("file.separator")
					+ "change_couplings.psql");
			File sqlFile = new File(sqlFunctionURL.toURI());
			StringBuilder sql = new StringBuilder();
			BufferedReader reader = new BufferedReader(new FileReader(sqlFile));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sql.append(line);
				sql.append(FileUtils.lineSeparator);
			}
			reader.close();
			hibernateUtil.executeQuery("CREATE LANGUAGE plpython2u");
			hibernateUtil.executeQuery(sql.toString());
			String tablename = new BigInteger(130, new SecureRandom()).toString(32).toString();
			
			hibernateUtil.executeQuery("select reposuite_changecouplings('" + transaction.getId() + "','" + tablename
					+ "')");
			SQLQuery ccRulesQuery = hibernateUtil.createSQLQuery("SELECT premise, implication, support, confidence FROM " + tablename,
					ChangeCouplingRule.class);
			@SuppressWarnings ("rawtypes") List list = ccRulesQuery.list();
			if (list == null) {
				return null;
			}
			result.addAll(list);
			Collections.sort(result);
			return result;
		} catch (UninitializedDatabaseException e) {
			throw new UnrecoverableError(e.getMessage(), e);
		} catch (URISyntaxException e) {
			throw new UnrecoverableError(e.getMessage(), e);
		} catch (IOException e) {
			throw new UnrecoverableError(e.getMessage(), e);
		} catch (HibernateException e) {
			throw new UnrecoverableError(e.getMessage(), e);
		} catch (SQLException e) {
			throw new UnrecoverableError(e.getMessage(), e);
		}
	}
}
