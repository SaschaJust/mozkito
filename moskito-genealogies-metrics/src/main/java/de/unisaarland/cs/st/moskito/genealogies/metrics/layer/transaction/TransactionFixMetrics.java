package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.transaction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;
import net.ownhero.dev.regex.RegexGroup;

import org.eclipse.jgit.diff.Edit.Type;

import de.unisaarland.cs.st.moskito.bugs.tracker.model.Report;
import de.unisaarland.cs.st.moskito.genealogies.core.TransactionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyTransactionNode;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

public class TransactionFixMetrics extends GenealogyTransactionMetric {
	
	private static String              fixTypeName  = "fixType";
	private static String              numFixesName = "numFixes";
	private final PersistenceUtil      persistenceUtil;
	private final Map<String, Integer> classifyMap  = new HashMap<String, Integer>();
	
	public TransactionFixMetrics(final TransactionChangeGenealogy genealogy) {
		super(genealogy);
		this.persistenceUtil = genealogy.getCore().getPersistenceUtil();
		final String fixClassFilePath = System.getProperty("fix.classify.file", null);
		if (fixClassFilePath != null) {
			final File fixClassifyFile = new File(fixClassFilePath);
			BufferedReader reader;
			try {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(fixClassifyFile)));
				String line = "";
				while ((line = reader.readLine()) != null) {
					final String[] lineParts = line.split(",");
					if (lineParts.length < 2) {
						continue;
					}
					
					try {
						final Type bugType = Type.valueOf(lineParts[1]);
						this.classifyMap.put(lineParts[0], bugType.ordinal());
					} catch (final IllegalArgumentException e) {
						if (Logger.logWarn()) {
							Logger.warn("Could not map `" + lineParts[1]
							        + "` to bug TYPE. Storing artificial bug type 100.");
						}
						this.classifyMap.put(lineParts[0], 100);
					}
				}
				if (Logger.logInfo()) {
					Logger.info("Added " + this.classifyMap.size() + " external bug classifications.");
				}
			} catch (final FileNotFoundException e) {
				throw new UnrecoverableError(e);
			} catch (final IOException e) {
				throw new UnrecoverableError(e);
			}
			
		}
	}
	
	private List<String> getBugId(final Regex regex,
	                              final String message) {
		final List<List<RegexGroup>> findAll = regex.findAll(message.toLowerCase());
		if (findAll == null) {
			return new ArrayList<String>(0);
		}
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
				
				int typeOrdinal = -1;
				
				final Report report = this.persistenceUtil.loadById(rId, Report.class);
				if (report == null) {
					continue;
				}
				
				typeOrdinal = report.getType().ordinal();
				
				if (!this.classifyMap.isEmpty()) {
					final String sId = String.valueOf(report.getId());
					if (this.classifyMap.containsKey(sId)) {
						typeOrdinal = this.classifyMap.get(sId);
					} else {
						if (Logger.logInfo()) {
							Logger.info("Could not find " + sId + " in external classification map.");
						}
						typeOrdinal = 200;
					}
				}
				
				if ((fixType == -1) || (fixType == typeOrdinal)) {
					fixType = typeOrdinal;
				} else {
					if (Logger.logInfo()) {
						Logger.info("transaction " + rcsTransaction.getId() + " fixes multiple bugs.");
					}
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
