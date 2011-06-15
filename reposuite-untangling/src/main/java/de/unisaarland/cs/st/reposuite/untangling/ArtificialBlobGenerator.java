package de.unisaarland.cs.st.reposuite.untangling;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNegative;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElement;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementLocation;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaMethodCall;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaMethodDefinition;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

public class ArtificialBlobGenerator {
	
	protected static boolean canCombine(final String pathA, final String pathB, final int packageDistance){
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
	
	/**
	 * Generate all artificial blobs from the set of atomicChanges supplied.
	 * This algorithm combines all transactions that change files within the
	 * same file path directory or files whose file paths are not more that
	 * <code>packageDistance</code> file path directories apart. The distance
	 * between two file paths is determined as follows: 1) compute the longest
	 * common sub-path between both file paths. 2) split the remaining file
	 * paths by '<FileUtils.fileSeparator>' 3) The longest array of split
	 * remaining file path segments defines the distance.
	 * 
	 * @param atomicChangeOperations
	 *            the atomic change operations
	 * @param packageDistance
	 *            The maximal number of path segements allowed to combine two
	 *            transactions. E.g. <code>a/b/x</code> and
	 *            <code>a/b/c/d/y</code> have a distance of two, while
	 *            <code>a/b/x</code> and <code>a/c/d/e/y</code> have a distance
	 *            of three.
	 * @param minBlobSize
	 *            generate no artificial blobs smaller than
	 *            <code>minBlobSize</code> transactions.
	 * @param maxBlobSize
	 *            generate no artificial blobs larger than
	 *            <code>maxBlobSize</code> transactions.
	 * @return the set of generated artificial blobs
	 */
	@NoneNull
	// FIXME @GreaterOrEqual (ref = 2) for minBlobSize
	// FIXME @GreaterOrEqual (ref = -1) for maxBlobSize
	public static Set<ArtificialBlob> generateAll(final Map<RCSTransaction, List<JavaChangeOperation>> atomicChangeOperations,
	                                              @NotNegative final int packageDistance,
	                                              @NotNegative final int minBlobSize,
	                                              final int maxBlobSize) {
		
		Set<ArtificialBlob> result = new HashSet<ArtificialBlob>();
		
		// check the more complicated preconditions
		if (maxBlobSize > -1) {
			if (maxBlobSize < minBlobSize) {
				throw new UnrecoverableError(
				"The 'maxBlobSize' argument must either be -1 (for unlimited size) or greater or equals than minBlobSize. All other settings make no sense.");
			}
		}
		
		// map directories to transactions
		Map<String, Set<RCSTransaction>> paths = new HashMap<String, Set<RCSTransaction>>();
		for (RCSTransaction t : atomicChangeOperations.keySet()) {
			for (JavaChangeOperation op : atomicChangeOperations.get(t)) {
				JavaElementLocation elementLocation = op.getChangedElementLocation();
				JavaElement element = elementLocation.getElement();
				if ((element instanceof JavaMethodDefinition) || (element instanceof JavaMethodCall)) {
					String filePath = elementLocation.getFilePath();
					filePath.substring(0, filePath.lastIndexOf(FileUtils.fileSeparator) - 1);
					if (!paths.containsKey(filePath)) {
						paths.put(filePath, new HashSet<RCSTransaction>());
					}
					paths.get(filePath).add(t);
				}
			}
		}
		
		// check with packages satisfy the packageDistance criteria
		Set<Set<String>> newAdded = new HashSet<Set<String>>();
		
		String[] pathArray = paths.keySet().toArray(new String[paths.keySet().size()]);
		for (int i = 0; i < pathArray.length; ++i) {
			for (int j = i + 1; j < paths.keySet().size(); ++j) {
				String pathA = pathArray[i];
				String pathB = pathArray[j];
				if (canCombine(pathA, pathB, packageDistance)) {
					Set<String> l = new HashSet<String>();
					l.add(pathA);
					l.add(pathB);
					newAdded.add(l);
				}
			}
		}
		
		if (Logger.logDebug()) {
			Logger.debug("Found " + newAdded.size() + " path sets that can be combined.");
		}

		Set<Set<String>> pathsToCombine = transitiveClosure(newAdded);
		
		/*
		 * for each transaction touching packages satisfying the
		 * packageDistanceCriteria (and all packages touching the same package),
		 * combine the transactions into blobs (respecting the minBlobSize and
		 * maxBlobSize arguments).
		 */
		for (Set<String> pathSet : pathsToCombine) {
			Set<RCSTransaction> transactions = new HashSet<RCSTransaction>();
			for (String p : pathSet) {
				transactions.addAll(paths.get(p));
			}
			if (transactions.size() >= 10) {
				if (Logger.logWarn()) {
					Logger.warn("Generated artificial blob with more that 9 transactions. Powerset would be too big. Skipping ... ");
				}
				continue;
			}
			Map<RCSTransaction, List<JavaChangeOperation>> blobData = new HashMap<RCSTransaction, List<JavaChangeOperation>>();
			for (RCSTransaction t : transactions) {
				blobData.put(t, atomicChangeOperations.get(t));
			}
			result.add(new ArtificialBlob(blobData));
		}
		
		
		
		return result;
	}
	
	public static String getLongestCommonPath(final String x,
	                                          final String y) {
		int M = x.length();
		int N = y.length();
		
		// opt[i][j] = length of LCS of x[i..M] and y[j..N]
		int[][] opt = new int[M + 1][N + 1];
		
		// compute length of LCS and all subproblems via dynamic programming
		for (int i = M - 1; i >= 0; i--) {
			for (int j = N - 1; j >= 0; j--) {
				if (x.charAt(i) == y.charAt(j)) {
					opt[i][j] = opt[i + 1][j + 1] + 1;
				} else {
					opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);
				}
			}
		}
		
		StringBuilder sb = new StringBuilder();
		
		// recover LCS itself and print it to standard output
		int i = 0, j = 0;
		while ((i < M) && (j < N)) {
			if (x.charAt(i) == y.charAt(j)) {
				sb.append(x.charAt(i));
				i++;
				j++;
			} else if (opt[i + 1][j] >= opt[i][j + 1]) {
				i++;
			} else {
				j++;
			}
		}
		return sb.toString();
	}
	
	protected static Set<Set<String>> transitiveClosure(final Set<Set<String>> original) {
		Set<Set<String>> newAdded = new HashSet<Set<String>>();
		newAdded.addAll(original);
		Set<Set<String>> pathsToCombine = new HashSet<Set<String>>();
		pathsToCombine.addAll(newAdded);
		while (!newAdded.isEmpty()) {
			Set<Set<String>> tmp = new HashSet<Set<String>>();
			tmp.addAll(newAdded);
			newAdded.clear();
			for (Set<String> l : pathsToCombine) {
				for (Set<String> t : tmp) {
					if(CollectionUtils.containsAny(l, t) && (!t.containsAll(l))){
						Set<String> newL = new HashSet<String>();
						newL.addAll(t);
						newL.addAll(l);
						newAdded.add(newL);
					}
				}
			}
			pathsToCombine.addAll(tmp);
		}
		return pathsToCombine;
	}
	
}
