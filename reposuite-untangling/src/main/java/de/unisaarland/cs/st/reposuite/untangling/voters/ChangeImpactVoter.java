/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.untangling.voters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.clustering.MultilevelClusteringScoreVisitor;
import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.persistence.Criteria;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElement;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementLocation;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaMethodDefinition;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.untangling.voters.elements.ImpactCSVRow;
import de.unisaarland.cs.st.reposuite.untangling.voters.elements.ImpactMatrix;

/**
 * The Class ChangeImpactVoter.
 * 
 * Works only for JavaMethodDefinitions and SVN (or converted) repositories!!!
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class ChangeImpactVoter implements MultilevelClusteringScoreVisitor<JavaChangeOperation> {
	
	/** The impact matrix. */
	private final ImpactMatrix<String, String> impactMatrix = new ImpactMatrix<String, String>();
	
	/**
	 * Instantiates a new change impact voter.
	 * 
	 * @param transaction
	 *            the transaction
	 * @param impactDataFile
	 *            the impact data file
	 * @param persistenceUtil
	 *            the persistence util
	 */
	public ChangeImpactVoter(final RCSTransaction transaction, final File impactDataFile,
	                         final PersistenceUtil persistenceUtil) {
		
		Map<String, ImpactCSVRow> impactData = parseImpactCSV(impactDataFile);
		
		// fill the impact matrix
		Iterator<RCSTransaction> transactionIter = transaction.getPreviousTransactions();
		while (transactionIter.hasNext()) {
			RCSTransaction parent = transactionIter.next();
			if ((parent.getOriginalId() == null) || (parent.getOriginalId().trim().equals(""))) {
				throw new UnrecoverableError("This class works only for SVN or SVN converted repositories. Sorry.");
			}
			ImpactCSVRow impactCSVRow = impactData.get(parent.getOriginalId());
			
			Criteria<JavaChangeOperation> criteria = persistenceUtil.createCriteria(JavaChangeOperation.class)
			.in("revision_revisionid", parent.getRevisions());
			for (JavaChangeOperation op : persistenceUtil.load(criteria)) {
				JavaElement element = op.getChangedElementLocation().getElement();
				if (element instanceof JavaMethodDefinition) {
					String elementName = getMethodName((JavaMethodDefinition) element);
					if (impactCSVRow == null) {
						impactMatrix.addChurn(elementName);
					} else {
						for (String impactElement : impactCSVRow.getDiffMeth()) {
							impactMatrix.addRelation(elementName, impactElement, impactCSVRow.getNumDiff());
						}
					}
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.clustering.MultilevelClusteringScoreVisitor
	 * #getMaxPossibleScore()
	 */
	@Override
	public double getMaxPossibleScore() {
		return 1;
	}
	
	/**
	 * Gets the method name.
	 * 
	 * @param def
	 *            the def
	 * @return the method name
	 */
	private String getMethodName(final JavaMethodDefinition def) {
		String result = def.getFullQualifiedName();
		String signature = JavaMethodDefinition.getSignatureString(def.getSignature());
		result = result.replace(signature, "");
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.clustering.MultilevelClusteringScoreVisitor
	 * #getScore(java.lang.Object, java.lang.Object)
	 */
	@Override
	public double getScore(final JavaChangeOperation o1,
	                       final JavaChangeOperation o2) {
		double score = 0;
		
		JavaElementLocation l1 = o1.getChangedElementLocation();
		JavaElementLocation l2 = o2.getChangedElementLocation();
		
		if ((l1 == null) || (l2 == null)) {
			if (Logger.logError()) {
				Logger.error("Cannot compute score for corrupted change operations (at least one location was null)");
			}
			return 0;
		}
		
		JavaElement e1 = l1.getElement();
		JavaElement e2 = l2.getElement();
		
		if ((e1 == null) || (e2 == null)) {
			if (Logger.logError()) {
				Logger.error("Cannot compute score for corrupted change operations (at least one element was null)");
			}
			return 0;
		}
		
		if ((!(e1 instanceof JavaMethodDefinition)) || (!(e2 instanceof JavaMethodDefinition))) {
			if (Logger.logError()) {
				Logger.error(ChangeCouplingVoter.class.getCanonicalName()
				             + " currently support JavaMethodDefinition only! Returning 0.");
			}
			return 0;
		}
		
		JavaMethodDefinition d1 = (JavaMethodDefinition) e1;
		JavaMethodDefinition d2 = (JavaMethodDefinition) e2;
		
		double c1 = impactMatrix.getImpactConfidence(getMethodName(d1), getMethodName(d2));
		double c2 = impactMatrix.getImpactConfidence(getMethodName(d2), getMethodName(d1));
		
		score = Math.max(c1, c2);
		
		Condition.check(score <= 1d, "The returned distance must be a value between 0 and 1, but was: " + score);
		Condition.check(score >= 0d, "The returned distance must be a value between 0 and 1, but was: " + score);
		return score;
	}
	
	/**
	 * Parses the impact csv and returns a mapping between nextRev ipact row
	 * field and the impact row itself.
	 * 
	 * @param csvFile
	 *            the csv file
	 * @return the list
	 */
	public Map<String, ImpactCSVRow> parseImpactCSV(final File csvFile) {
		
		Map<String, ImpactCSVRow> result = new HashMap<String, ImpactCSVRow>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(csvFile));
			String line = "";
			int counter = 0;
			while ((line = reader.readLine()) != null) {
				++counter;
				if (line.trim().equals("")) {
					continue;
				}
				String[] lineParts = line.split("\\[");
				if (lineParts.length != 2) {
					if (counter == 1) {
						continue;
					}
					if (Logger.logError()) {
						throw new UnrecoverableError("Impact line must contain `[` character (in line " + counter
						                             + "): " + line);
					}
				}
				String diffMethodsCSV = lineParts[1].trim();
				diffMethodsCSV = diffMethodsCSV.substring(0, diffMethodsCSV.length() - 1);
				String[] diffMethods = new String[0];
				if (diffMethodsCSV.contains(",")) {
					diffMethods = diffMethodsCSV.split(",");
				}
				
				lineParts = lineParts[0].split(",");
				if (lineParts.length < 3) {
					if (Logger.logError()) {
						throw new UnrecoverableError("Malformatted impact line (in line " + counter + "): " + line);
					}
				}
				try {
					Long rev = new Long(lineParts[0]);
					Long nextRev = new Long(lineParts[1]);
					Long numDiff = new Long(lineParts[2]);
					result.put(nextRev.toString(), new ImpactCSVRow(rev, nextRev, numDiff, diffMethods));
				} catch (NumberFormatException e) {
					throw new UnrecoverableError(
					                             "One of the long values in impact file could not be converted from String to Long (in line "
					                             + counter + "): " + line);
				}
			}
		} catch (IOException e) {
			throw new UnrecoverableError(e);
		}
		return result;
	}
	
}
