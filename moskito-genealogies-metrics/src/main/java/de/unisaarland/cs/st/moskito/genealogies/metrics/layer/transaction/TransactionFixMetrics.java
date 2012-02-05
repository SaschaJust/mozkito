package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.transaction;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;
import net.ownhero.dev.regex.RegexGroup;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Report;
import de.unisaarland.cs.st.moskito.genealogies.core.TransactionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyTransactionNode;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

public class TransactionFixMetrics extends GenealogyTransactionMetric {
	
	private static String         fixTypeName  = "fixType";
	private static String         numFixesName = "numFixes";
	private final PersistenceUtil persistenceUtil;
	
	public TransactionFixMetrics(final TransactionChangeGenealogy genealogy) {
		super(genealogy);
		this.persistenceUtil = genealogy.getCore().getPersistenceUtil();
	}
	
	private List<String> getBugId(final Regex regex,
	                              final String message) {
		final List<List<RegexGroup>> findAll = regex.findAll(message.toLowerCase());
		final List<String> result = new LinkedList<String>();
		for (final List<RegexGroup> regexGroups : findAll) {
			for (final RegexGroup regexGroup : regexGroups) {
				if (regexGroup.getName().equals("bugids")) {
					if (regexGroup.getMatch() != null) {
						final String[] bugids = regexGroup.getMatch().split(",");
						for (final String bugid : bugids) {
							result.add(bugid.trim());
						}
					}
				}
			}
		}
		return result;
	}
	
	@Override
	public Collection<String> getMetricNames() {
		final HashSet<String> result = new HashSet<String>();
		result.add(fixTypeName);
		result.add(numFixesName);
		return result;
	}
	
	@Override
	public Collection<GenealogyMetricValue> handle(final GenealogyTransactionNode item) {
		if (Logger.logDebug()) {
			Logger.debug(this.getClass().getCanonicalName() + " handles node " + item.getNodeId());
		}
		int numFixes = 0;
		int fixType = -1;
		
		final Collection<GenealogyMetricValue> result = new HashSet<GenealogyMetricValue>();
		
		final RCSTransaction rcsTransaction = item.getNode();
		final String commitMessage = rcsTransaction.getMessage();
		
		// FIXME This should be done using the appropriate argument
		// FIXME This should be based on moskito-mappings
		final String fixPattern = System.getProperty("fix.pattern", "({bugids}\\d+)");
		
		final Regex regex = new Regex(fixPattern.toLowerCase());
		final List<String> reportIds = getBugId(regex, commitMessage);
		for (final String reportId : reportIds) {
			try {
				final long rId = Long.valueOf(reportId);
				final Report report = this.persistenceUtil.loadById(rId, Report.class);
				if (report == null) {
					continue;
				}
				
				final int typeOrdinal = report.getType().ordinal();
				if ((fixType == -1) || (fixType == typeOrdinal)) {
					fixType = typeOrdinal;
				} else {
					fixType = 100;
				}
				++numFixes;
			} catch (final NumberFormatException e) {
				continue;
			}
		}
		
		final String nodeId = this.genealogy.getNodeId(rcsTransaction);
		result.add(new GenealogyMetricValue(numFixesName, nodeId, numFixes));
		result.add(new GenealogyMetricValue(fixTypeName, nodeId, fixType));
		
		return result;
	}
	
}
