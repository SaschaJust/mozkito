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
package org.mozkito.untangling.blob.combine;

import java.util.Arrays;
import java.util.List;

import net.ownhero.dev.kisa.Logger;

import org.apache.commons.lang.StringUtils;

import org.mozkito.untangling.blob.ChangeOperationSet;
import org.mozkito.utilities.io.FileUtils;
import org.mozkito.versions.exceptions.NoSuchHandleException;
import org.mozkito.versions.model.Revision;

/**
 * The Class BlobTransactionCombineOperator.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class PackageDistanceCombineOperator implements CombineOperator<ChangeOperationSet> {
	
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
	 */
	public PackageDistanceCombineOperator(final Long maxPackageDistance) {
		this.maxPackageDistance = maxPackageDistance;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.untangling.blob.CombineOperator#canBeCombined (java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean canBeCombined(final ChangeOperationSet t1,
	                             final ChangeOperationSet t2) {
		
		for (final Revision rev : t1.getChangeSet().getRevisions()) {
			try {
				final String path = rev.getChangedFile().getPath(t1.getChangeSet());
				for (final Revision rev2 : t2.getChangeSet().getRevisions()) {
					try {
						final String path2 = rev2.getChangedFile().getPath(t2.getChangeSet());
						if (Logger.logDebug()) {
							Logger.debug("Trying to combine %s and %s using max package distance of %d ...", path,
							             path2, this.maxPackageDistance.intValue());
						}
						if (canCombinePaths(path, path2, this.maxPackageDistance.intValue())) {
							if (Logger.logDebug()) {
								Logger.debug("OK");
							}
							return true;
						}
						if (Logger.logDebug()) {
							Logger.debug("FAILED");
						}
					} catch (final NoSuchHandleException e) {
						if (Logger.logError()) {
							Logger.error("Could not determine file name of %s as of %s.", rev2.getChangedFile()
							                                                                  .toString(),
							             t2.toString());
						}
						return false;
					}
				}
			} catch (final NoSuchHandleException e) {
				if (Logger.logError()) {
					Logger.error("Could not determine file name of %s as of %s.", rev.getChangedFile().toString(),
					             t1.toString());
				}
				return false;
			}
		}
		return false;
	}
}
