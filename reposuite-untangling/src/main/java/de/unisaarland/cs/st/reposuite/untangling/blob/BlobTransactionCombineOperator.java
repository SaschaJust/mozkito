/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.untangling.blob;

import java.util.Arrays;
import java.util.List;

import net.ownhero.dev.ioda.FileUtils;

import org.apache.commons.lang.StringUtils;

import de.unisaarland.cs.st.reposuite.rcs.model.RCSRevision;

/**
 * The Class BlobTransactionCombineOperator.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class BlobTransactionCombineOperator implements CombineOperator<BlobTransaction> {
	
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
	private final int maxPackageDistance;
	
	/**
	 * Instantiates a new blob transaction combine operator.
	 * 
	 * @param maxPackageDistance
	 *            the max package distance
	 */
	public BlobTransactionCombineOperator(final int maxPackageDistance) {
		this.maxPackageDistance = maxPackageDistance;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.untangling.blob.CombineOperator#canBeCombined
	 * (java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean canBeCombined(final BlobTransaction t1,
	                             final BlobTransaction t2) {
		for (RCSRevision rev : t1.getTransaction().getRevisions()) {
			String path = rev.getChangedFile().getPath(t1.getTransaction());
			for (RCSRevision rev2 : t2.getTransaction().getRevisions()) {
				String path2 = rev2.getChangedFile().getPath(t2.getTransaction());
				if (canCombinePaths(path, path2, maxPackageDistance)) {
					return true;
				}
			}
		}
		return false;
	}
}
