/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/
package org.mozkito.untangling.blob;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.annotations.compare.GreaterOrEqualInt;
import net.ownhero.dev.kanuni.annotations.simple.NotNegative;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.untangling.blob.combine.CombineOperator;
import org.mozkito.untangling.utils.CollectionUtils;

/**
 * The Interface ArtificialBlobGenerator.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class ArtificialBlobGenerator {
	
	/**
	 * The Enum ArtificialBlobGeneratorStrategy.
	 */
	public enum ArtificialBlobGeneratorStrategy {
		
		/** The package. */
		PACKAGE,
		/** The consecutive. */
		CONSECUTIVE,
		/** The couplings. */
		COUPLINGS;
	}
	
	/** The combine operator. */
	private final CombineOperator<ChangeOperationSet> combineOperator;
	
	/**
	 * Instantiates a new artificial blob generator.
	 * 
	 * @param combineOperator
	 *            the combine operator
	 */
	public ArtificialBlobGenerator(final CombineOperator<ChangeOperationSet> combineOperator) {
		this.combineOperator = combineOperator;
	}
	
	/**
	 * Generate all blobs according to specification.
	 * 
	 * @param changeSets
	 *            the transactions
	 * @param minBlobSize
	 *            the min blob size
	 * @param maxBlobSize
	 *            the max blob size
	 * @return the sets the
	 */
	public Set<ArtificialBlob> generateAll(@NotNull final Collection<ChangeOperationSet> changeSets,
	                                       @GreaterOrEqualInt (ref = 2) @NotNegative final int minBlobSize,
	                                       @GreaterOrEqualInt (ref = -1) final int maxBlobSize) {
		
		// check the more complicated preconditions
		if (maxBlobSize > -1) {
			if (maxBlobSize < minBlobSize) {
				throw new UnrecoverableError(
				                             "The 'maxBlobSize' argument must either be -1 (for unlimited size) or greater or equals than minBlobSize. All other settings make no sense.");
			}
		}
		
		if (Logger.logInfo()) {
			Logger.info("Generating all combinations between %s transactions.", String.valueOf(changeSets.size()));
		}
		final Set<Set<ChangeOperationSet>> allCombinations = CollectionUtils.getAllCombinations(changeSets,
		                                                                               this.combineOperator,
		                                                                               maxBlobSize);
		
		if (Logger.logInfo()) {
			Logger.info("Found %s transaction combinations (may be decreased).", String.valueOf(allCombinations.size()));
		}
		
		// Filter out too small combinations
		final Iterator<Set<ChangeOperationSet>> setIter = allCombinations.iterator();
		while (setIter.hasNext()) {
			final Set<ChangeOperationSet> next = setIter.next();
			if (next.size() < minBlobSize) {
				setIter.remove();
			}
		}
		
		final Set<ArtificialBlob> result = new HashSet<ArtificialBlob>();
		for (final Set<ChangeOperationSet> set : allCombinations) {
			result.add(new ArtificialBlob(set));
		}
		
		if (Logger.logInfo()) {
			Logger.info("Found " + result.size() + " artificial blobs.");
		}
		
		return result;
	}
	
}
