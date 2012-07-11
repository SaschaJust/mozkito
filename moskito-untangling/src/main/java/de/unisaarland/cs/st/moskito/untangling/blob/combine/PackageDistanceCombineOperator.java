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
 *******************************************************************************/
package de.unisaarland.cs.st.moskito.untangling.blob.combine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.LongArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.FileUtils;

import org.apache.commons.lang.StringUtils;

import de.unisaarland.cs.st.moskito.rcs.model.RCSRevision;
import de.unisaarland.cs.st.moskito.untangling.blob.ChangeSet;

/**
 * The Class BlobTransactionCombineOperator.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class PackageDistanceCombineOperator implements CombineOperator<ChangeSet> {
	
	/**
	 * The Class Options.
	 */
	public static class Options extends
	        ArgumentSetOptions<PackageDistanceCombineOperator, ArgumentSet<PackageDistanceCombineOperator, Options>> {
		
		private net.ownhero.dev.hiari.settings.LongArgument.Options maxPackageDistanceOptions;
		
		/**
		 * @param argumentSet
		 * @param name
		 * @param description
		 * @param requirements
		 */
		public Options(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
			super(argumentSet, "ccCombineOp", "ChangeCouplingCombineOperator options.", requirements);
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
		 */
		@Override
		public PackageDistanceCombineOperator init() {
			// PRECONDITIONS
			final Long maxPackageDistance = getSettings().getArgument(this.maxPackageDistanceOptions).getValue();
			return new PackageDistanceCombineOperator(maxPackageDistance);
			
		}
		
		/*
		 * (non-Javadoc)
		 * @see
		 * net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
		 */
		@Override
		public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> argumentSet) throws ArgumentRegistrationException,
		                                                                                    SettingsParseError {
			// PRECONDITIONS
			final Map<String, IOptions<?, ?>> map = new HashMap<>();
			
			this.maxPackageDistanceOptions = new LongArgument.Options(
			                                                          argumentSet,
			                                                          "maxPackageDistance",
			                                                          "The maximal allowed distance between packages allowed when generating blobs using package distances.",
			                                                          0l, Requirement.required);
			map.put(this.maxPackageDistanceOptions.getName(), this.maxPackageDistanceOptions);
			return map;
		}
	}
	
	/**
	 * Can combine paths.
	 * 
	 * @param pathA
	 *            the path a
	 * @param pathB
	 *            the path b
	 * @param packageDistance
	 *            the package distance
	 * @return true, if successful
	 */
	protected static boolean canCombinePaths(final String pathA,
	                                         final String pathB,
	                                         final int packageDistance) {
		List<String> pathAParts = Arrays.asList(StringUtils.removeEnd(pathA, FileUtils.fileSeparator)
		                                                   .split(FileUtils.fileSeparator));
		List<String> pathBParts = Arrays.asList(StringUtils.removeEnd(pathB, FileUtils.fileSeparator)
		                                                   .split(FileUtils.fileSeparator));
		
		// ignore the last packageDistance parts.
		final int pathLengthDist = Math.abs(pathAParts.size() - pathBParts.size());
		if (pathLengthDist != 0) {
			// different long paths
			if (pathAParts.size() > pathBParts.size()) {
				pathAParts = pathAParts.subList(0, pathAParts.size() - packageDistance);
				final int bIndex = packageDistance - pathLengthDist;
				if (bIndex > 0) {
					pathBParts = pathBParts.subList(0, pathBParts.size() - bIndex);
				}
			} else {
				pathBParts = pathBParts.subList(0, pathBParts.size() - packageDistance);
				final int aIndex = packageDistance - pathLengthDist;
				if (aIndex > 0) {
					pathAParts = pathAParts.subList(0, pathAParts.size() - aIndex);
				}
			}
		} else {
			pathAParts = pathAParts.subList(0, pathAParts.size() - packageDistance);
			pathBParts = pathBParts.subList(0, pathBParts.size() - packageDistance);
		}
		return pathAParts.equals(pathBParts);
	}
	
	/** The max package distance. */
	private final Long maxPackageDistance;
	
	/**
	 * Instantiates a new blob transaction combine operator.
	 * 
	 * @param maxPackageDistance
	 *            the max package distance
	 * @param timeWindowSize
	 *            the time window size
	 */
	protected PackageDistanceCombineOperator(final Long maxPackageDistance) {
		this.maxPackageDistance = maxPackageDistance;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.untangling.blob.CombineOperator#canBeCombined (java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public boolean canBeCombined(final ChangeSet t1,
	                             final ChangeSet t2) {
		
		for (final RCSRevision rev : t1.getTransaction().getRevisions()) {
			final String path = rev.getChangedFile().getPath(t1.getTransaction());
			for (final RCSRevision rev2 : t2.getTransaction().getRevisions()) {
				final String path2 = rev2.getChangedFile().getPath(t2.getTransaction());
				if (canCombinePaths(path, path2, this.maxPackageDistance.intValue())) {
					return true;
				}
			}
		}
		return false;
	}
}
