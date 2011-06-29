package de.unisaarland.cs.st.reposuite.untangling.voters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.ioda.FileUtils;

public class ImpactMatrix implements Serializable {
	
	/**
	 * 
	 */
	private static final long                           serialVersionUID    = 5456334661961732039L;
	private final Map<String, Map<String, Set<String>>> impactMatrix        = new HashMap<String, Map<String, Set<String>>>();
	private final Map<String, Set<String>>              sumSourceChanged    = new HashMap<String, Set<String>>();
	private final Map<String, Set<String>>              sumSourceImpacted   = new HashMap<String, Set<String>>();
	private Map<String, Integer>                        bugs                = new HashMap<String, Integer>();
	private final Map<String, Long>                     impactWeightedChurn = new HashMap<String, Long>();
	
	public ImpactMatrix() {
		
	}
	
	public ImpactMatrix(final Map<String, Integer> bugs) {
		this.bugs = bugs;
	}
	
	public void add(final String changedSource,
			final String impactedSource,
			final String transactionId,
			final long numDiff) {
		if (!impactMatrix.containsKey(changedSource)) {
			impactMatrix.put(changedSource, new HashMap<String, Set<String>>());
		}
		Map<String, Set<String>> innerMap = impactMatrix.get(changedSource);
		if (!innerMap.containsKey(impactedSource)) {
			innerMap.put(impactedSource, new HashSet<String>());
		}
		Set<String> set = innerMap.get(impactedSource);
		set.add(transactionId);
		
		if (!sumSourceChanged.containsKey(changedSource)) {
			sumSourceChanged.put(changedSource, new HashSet<String>());
		}
		sumSourceChanged.get(changedSource).add(transactionId);
		
		if (!sumSourceImpacted.containsKey(impactedSource)) {
			sumSourceImpacted.put(impactedSource, new HashSet<String>());
		}
		sumSourceImpacted.get(impactedSource).add(transactionId);
		
		if (!impactWeightedChurn.containsKey(changedSource)) {
			impactWeightedChurn.put(changedSource, 0l);
		}
		impactWeightedChurn.put(changedSource, impactWeightedChurn.get(changedSource) + numDiff);
	}
	
	public int getOccurence(final String changed, final String impacted){
		if(!impactMatrix.containsKey(changed)){
			return 0;
		}
		if(!impactMatrix.get(changed).containsKey(impacted)){
			return 0;
		}
		return impactMatrix.get(changed).get(impacted).size();
	}
	
	public int getSumChanged(final String changed) {
		if (!sumSourceChanged.containsKey(changed)) {
			return 0;
		}
		return sumSourceChanged.get(changed).size();
	}
	
	public int getSumimpacted(final String impacted) {
		if (!sumSourceImpacted.containsKey(impacted)) {
			return 0;
		}
		return sumSourceImpacted.get(impacted).size();
	}
	
	public String toCSV() {
		StringBuilder sb = new StringBuilder();
		
		List<String> allImpacted = new ArrayList<String>(sumSourceImpacted.keySet().size());
		allImpacted.addAll(sumSourceImpacted.keySet());
		
		// write header row
		for (String impacted : allImpacted) {
			sb.append(",");
			sb.append(impacted);
		}
		sb.append(",SUM_CHANGES,BUGS,IMPACT_WEIGHTED_CHURN");
		sb.append(FileUtils.lineSeparator);
		
		// write body
		for (String changed : impactMatrix.keySet()) {
			sb.append(changed);
			Map<String, Set<String>> innerMap = impactMatrix.get(changed);
			for (String impacted : allImpacted) {
				sb.append(",");
				if (innerMap.containsKey(impacted)) {
					sb.append(innerMap.get(impacted).size());
				} else {
					sb.append(0);
				}
			}
			sb.append(",");
			sb.append(sumSourceChanged.get(changed).size());
			sb.append(",");
			if (bugs.containsKey(changed)) {
				sb.append(bugs.get(changed));
			} else {
				sb.append(0);
			}
			sb.append(",");
			if (impactWeightedChurn.containsKey(changed)) {
				sb.append(impactWeightedChurn.get(changed));
			} else {
				sb.append(0);
			}
			sb.append(FileUtils.lineSeparator);
		}
		
		// write sum impact row
		for (String impacted : allImpacted) {
			sb.append(",");
			sb.append(sumSourceImpacted.get(impacted).size());
		}
		sb.append(",");
		sb.append(",");
		sb.append(",");
		sb.append(FileUtils.lineSeparator);
		
		return sb.toString();
	}
	
}
