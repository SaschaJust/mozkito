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
package de.unisaarland.cs.st.moskito.untangling.blob;

import java.util.Arrays;
import java.util.List;

import net.ownhero.dev.ioda.FileUtils;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Days;

import de.unisaarland.cs.st.moskito.rcs.model.RCSRevision;

/**
 * The Class BlobTransactionCombineOperator.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class BlobTransactionCombineOperator implements CombineOperator<AtomicTransaction> {
	
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
		int pathLengthDist = Math.abs(pathAParts.size() - pathBParts.size());
		if (pathLengthDist != 0) {
			// different long paths
			if (pathAParts.size() > pathBParts.size()) {
				pathAParts = pathAParts.subList(0, pathAParts.size() - packageDistance);
				int bIndex = packageDistance - pathLengthDist;
				if (bIndex > 0) {
					pathBParts = pathBParts.subList(0, pathBParts.size() - bIndex);
				}
			} else {
				pathBParts = pathBParts.subList(0, pathBParts.size() - packageDistance);
				int aIndex = packageDistance - pathLengthDist;
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
	private final int  maxPackageDistance;
	
	/** The time window size. */
	private final Long timeWindowSize;
	
	/**
	 * Instantiates a new blob transaction combine operator.
	 *
	 * @param maxPackageDistance the max package distance
	 * @param timeWindowSize the time window size
	 */
	public BlobTransactionCombineOperator(final int maxPackageDistance, final Long timeWindowSize) {
		this.maxPackageDistance = maxPackageDistance;
		this.timeWindowSize = timeWindowSize;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.untangling.blob.CombineOperator#canBeCombined (java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public boolean canBeCombined(final AtomicTransaction t1,
	                             final AtomicTransaction t2) {
		
		if (this.timeWindowSize > -1) {
			Days daysBetween = Days.daysBetween(t1.getTransaction().getTimestamp(), t2.getTransaction().getTimestamp());
			if (daysBetween.getDays() > this.timeWindowSize) {
				return false;
			}
		}
		
		for (RCSRevision rev : t1.getTransaction().getRevisions()) {
			String path = rev.getChangedFile().getPath(t1.getTransaction());
			for (RCSRevision rev2 : t2.getTransaction().getRevisions()) {
				String path2 = rev2.getChangedFile().getPath(t2.getTransaction());
				if (canCombinePaths(path, path2, this.maxPackageDistance)) {
					return true;
				}
			}
		}
		return false;
	}
}
