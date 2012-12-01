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
 ******************************************************************************/

package org.mozkito.changeimpact;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.ioda.FileUtils;

/**
 * The Class ImpactMatrix.
 */
public class ImpactMatrix implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long                           serialVersionUID    = 5456334661961732039L;
	
	/** The impact matrix. */
	private final Map<String, Map<String, Set<String>>> impactMatrix        = new HashMap<String, Map<String, Set<String>>>();
	
	/** The sum source changed. */
	private final Map<String, Set<String>>              sumSourceChanged    = new HashMap<String, Set<String>>();
	
	/** The sum source impacted. */
	private final Map<String, Set<String>>              sumSourceImpacted   = new HashMap<String, Set<String>>();
	
	/** The bugs. */
	private Map<String, Integer>                        bugs                = new HashMap<String, Integer>();
	
	/** The impact weighted churn. */
	private final Map<String, Long>                     impactWeightedChurn = new HashMap<String, Long>();
	
	/**
	 * Instantiates a new impact matrix.
	 */
	public ImpactMatrix() {
		
	}
	
	/**
	 * Instantiates a new impact matrix.
	 * 
	 * @param bugs
	 *            the bugs
	 */
	public ImpactMatrix(final Map<String, Integer> bugs) {
		this.bugs = bugs;
	}
	
	/**
	 * Adds the.
	 * 
	 * @param changedSource
	 *            the changed source
	 * @param impactedSource
	 *            the impacted source
	 * @param transactionId
	 *            the transaction id
	 * @param numDiff
	 *            the num diff
	 */
	public void add(final String changedSource,
	                final String impactedSource,
	                final String transactionId,
	                final long numDiff) {
		if (!this.impactMatrix.containsKey(changedSource)) {
			this.impactMatrix.put(changedSource, new HashMap<String, Set<String>>());
		}
		final Map<String, Set<String>> innerMap = this.impactMatrix.get(changedSource);
		if (!innerMap.containsKey(impactedSource)) {
			innerMap.put(impactedSource, new HashSet<String>());
		}
		final Set<String> set = innerMap.get(impactedSource);
		set.add(transactionId);
		
		if (!this.sumSourceChanged.containsKey(changedSource)) {
			this.sumSourceChanged.put(changedSource, new HashSet<String>());
		}
		this.sumSourceChanged.get(changedSource).add(transactionId);
		
		if (!this.sumSourceImpacted.containsKey(impactedSource)) {
			this.sumSourceImpacted.put(impactedSource, new HashSet<String>());
		}
		this.sumSourceImpacted.get(impactedSource).add(transactionId);
		
		if (!this.impactWeightedChurn.containsKey(changedSource)) {
			this.impactWeightedChurn.put(changedSource, 0l);
		}
		this.impactWeightedChurn.put(changedSource, this.impactWeightedChurn.get(changedSource) + numDiff);
	}
	
	/**
	 * Gets the occurence.
	 * 
	 * @param changed
	 *            the changed
	 * @param impacted
	 *            the impacted
	 * @return the occurence
	 */
	public int getOccurence(final String changed,
	                        final String impacted) {
		if (!this.impactMatrix.containsKey(changed)) {
			return 0;
		}
		if (!this.impactMatrix.get(changed).containsKey(impacted)) {
			return 0;
		}
		return this.impactMatrix.get(changed).get(impacted).size();
	}
	
	/**
	 * Gets the sum changed.
	 * 
	 * @param changed
	 *            the changed
	 * @return the sum changed
	 */
	public int getSumChanged(final String changed) {
		if (!this.sumSourceChanged.containsKey(changed)) {
			return 0;
		}
		return this.sumSourceChanged.get(changed).size();
	}
	
	/**
	 * Gets the sumimpacted.
	 * 
	 * @param impacted
	 *            the impacted
	 * @return the sumimpacted
	 */
	public int getSumimpacted(final String impacted) {
		if (!this.sumSourceImpacted.containsKey(impacted)) {
			return 0;
		}
		return this.sumSourceImpacted.get(impacted).size();
	}
	
	/**
	 * To csv.
	 * 
	 * @return the string
	 */
	public String toCSV() {
		final StringBuilder sb = new StringBuilder();
		
		final List<String> allImpacted = new ArrayList<String>(this.sumSourceImpacted.keySet().size());
		allImpacted.addAll(this.sumSourceImpacted.keySet());
		
		// write header row
		for (final String impacted : allImpacted) {
			sb.append(",");
			sb.append(impacted);
		}
		sb.append(",SUM_CHANGES,BUGS,IMPACT_WEIGHTED_CHURN");
		sb.append(FileUtils.lineSeparator);
		
		// write body
		for (final String changed : this.impactMatrix.keySet()) {
			sb.append(changed);
			final Map<String, Set<String>> innerMap = this.impactMatrix.get(changed);
			for (final String impacted : allImpacted) {
				sb.append(",");
				if (innerMap.containsKey(impacted)) {
					sb.append(innerMap.get(impacted).size());
				} else {
					sb.append(0);
				}
			}
			sb.append(",");
			sb.append(this.sumSourceChanged.get(changed).size());
			sb.append(",");
			if (this.bugs.containsKey(changed)) {
				sb.append(this.bugs.get(changed));
			} else {
				sb.append(0);
			}
			sb.append(",");
			if (this.impactWeightedChurn.containsKey(changed)) {
				sb.append(this.impactWeightedChurn.get(changed));
			} else {
				sb.append(0);
			}
			sb.append(FileUtils.lineSeparator);
		}
		
		// write sum impact row
		for (final String impacted : allImpacted) {
			sb.append(",");
			sb.append(this.sumSourceImpacted.get(impacted).size());
		}
		sb.append(",");
		sb.append(",");
		sb.append(",");
		sb.append(FileUtils.lineSeparator);
		
		return sb.toString();
	}
	
}
