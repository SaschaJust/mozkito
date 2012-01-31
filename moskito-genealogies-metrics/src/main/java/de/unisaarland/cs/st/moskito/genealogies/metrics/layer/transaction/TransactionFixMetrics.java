package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.transaction;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;
import net.ownhero.dev.regex.RegexGroup;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Report;
import de.unisaarland.cs.st.moskito.genealogies.layer.TransactionChangeGenealogy;
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
		final String fixPattern = System.getProperty("fix.pattern", "(\\d+)");
		
		final Regex regex = new Regex(fixPattern);
		final List<List<RegexGroup>> regexHits = regex.findAll(commitMessage);
		
		for (final List<RegexGroup> hit : regexHits) {
			final String reportId = hit.get(0).getMatch();
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
