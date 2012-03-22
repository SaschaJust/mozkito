/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
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
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Type;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Report;
import de.unisaarland.cs.st.moskito.genealogies.core.TransactionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyTransactionNode;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

/**
 * The Class TransactionFixMetrics.
 *
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class TransactionFixMetrics extends GenealogyTransactionMetric {
	
	/** The fix type name. */
	private static String              fixTypeName  = "fixType";
	
	/** The num fixes name. */
	private static String              numFixesName = "numFixes";
	
	/** The persistence util. */
	private final PersistenceUtil      persistenceUtil;
	
	/** The classify map. */
	private final Map<String, Integer> classifyMap  = new HashMap<String, Integer>();
	
	/**
	 * Instantiates a new transaction fix metrics.
	 *
	 * @param genealogy the genealogy
	 */
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
	
	/**
	 * Gets the bug id.
	 *
	 * @param regex the regex
	 * @param message the message
	 * @return the bug id
	 */
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
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetric#getMetricNames()
	 */
	@Override
	public Collection<String> getMetricNames() {
		final HashSet<String> result = new HashSet<String>();
		result.add(fixTypeName);
		result.add(numFixesName);
		return result;
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetric#handle(java.lang.Object)
	 */
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
