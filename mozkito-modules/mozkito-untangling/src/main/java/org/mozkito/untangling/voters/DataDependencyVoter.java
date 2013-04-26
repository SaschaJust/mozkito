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
package org.mozkito.untangling.voters;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.codeanalysis.model.JavaElementLocation.LineCover;
import org.mozkito.utilities.clustering.MultilevelClustering;
import org.mozkito.utilities.clustering.MultilevelClusteringScoreVisitor;
import org.mozkito.utilities.datastructures.Tuple;
import org.mozkito.utilities.execution.CommandExecutor;
import org.mozkito.utilities.io.FileUtils;
import org.mozkito.utilities.io.FileUtils.FileShutdownAction;
import org.mozkito.versions.Repository;
import org.mozkito.versions.exceptions.RepositoryOperationException;
import org.mozkito.versions.model.ChangeSet;

/**
 * The Class CallGraphHandler.
 * 
 * Works only for JavaMethodDefinitions so far.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class DataDependencyVoter implements MultilevelClusteringScoreVisitor<JavaChangeOperation> {
	
	/** The eclipse dir. */
	private final File                     eclipseDir;
	
	/** The checkout dir. */
	private File                           checkoutDir = null;
	
	/** The cache dir. */
	private final File                     cacheDir;
	
	/** The transaction. */
	private final ChangeSet                changeset;
	
	/** The cache. */
	private Map<String, Set<Set<Integer>>> cache       = new HashMap<>();
	
	/** The cache file. */
	private final File                     cacheFile;
	
	/**
	 * Instantiates a new data dependency voter.
	 * 
	 * @param eclipseDir
	 *            the eclipse dir
	 * @param repository
	 *            the repository
	 * @param changeset
	 *            the transaction
	 * @param cacheDir
	 *            the cache file
	 */
	@SuppressWarnings ("unchecked")
	public DataDependencyVoter(@NotNull final File eclipseDir, @NotNull final Repository repository,
	        final ChangeSet changeset, final File cacheDir) {
		this.changeset = changeset;
		this.eclipseDir = eclipseDir;
		this.cacheDir = cacheDir;
		final String cacheFileName = this.changeset.getId() + ".dd";
		this.cacheFile = new File(this.cacheDir.getAbsolutePath() + FileUtils.fileSeparator + cacheFileName);
		assert this.cacheFile != null;
		if (this.cacheFile.exists()) {
			try (final ObjectInputStream objIn = new ObjectInputStream(
			                                                           new BufferedInputStream(
			                                                                                   new FileInputStream(
			                                                                                                       this.cacheFile)));) {
				this.cache = (Map<String, Set<Set<Integer>>>) objIn.readObject();
				if (this.cache == null) {
					this.cache = new HashMap<>();
				}
				objIn.close();
				
			} catch (final IOException | ClassNotFoundException ignore) {
				// ignore
			}
		} else {
			if (this.checkoutDir == null) {
				try {
					this.checkoutDir = repository.checkoutPath("/", changeset.getId());
				} catch (final RepositoryOperationException e) {
					throw new UnrecoverableError(e);
				}
			}
		}
		try {
			this.checkoutDir = repository.checkoutPath("/", changeset.getId());
		} catch (final RepositoryOperationException e) {
			throw new UnrecoverableError(e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.clustering.MultilevelClusteringScoreVisitor#close()
	 */
	@Override
	public void close() {
		// PRECONDITIONS
		
		try (final ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(this.cacheFile))) {
			out.writeObject(this.cache);
			out.close();
		} catch (final IOException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.clustering.MultilevelClusteringScoreVisitor #getMaxPossibleScore()
	 */
	@Override
	public double getMaxPossibleScore() {
		return 1;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.clustering.MultilevelClusteringScoreVisitor #getScore(java.lang.Object, java.lang.Object)
	 */
	@Override
	public double getScore(final JavaChangeOperation op1,
	                       final JavaChangeOperation op2) {
		
		if (Logger.logDebug()) {
			Logger.debug("Computing data dependency score for transaction %s and JavaChangeOperations %s and %s.",
			             this.changeset.getId(), String.valueOf(op1.getId()), String.valueOf(op2.getId()));
		}
		
		Condition.notNull(op1.getChangedElementLocation(), "op1.getChangeElementLocation() must not be NULL.",
		                  new Object[0]);
		Condition.notNull(op1.getChangedElementLocation(), "op2.getChangeElementLocation() must not be NULL.",
		                  new Object[0]);
		
		final String filePath1 = op1.getChangedElementLocation().getFilePath();
		final String filePath2 = op2.getChangedElementLocation().getFilePath();
		
		Condition.notNull(filePath1, "op1.getChangeElementLocation().getFilePath() must not be NULL.", new Object[0]);
		Condition.notNull(filePath2, "op2.getChangeElementLocation().getFilePath() must not be NULL.", new Object[0]);
		
		if (!filePath1.equals(filePath2)) {
			return MultilevelClustering.IGNORE_SCORE;
		}
		
		final String cacheKey = op1.getChangedPath();
		
		if (!this.cache.containsKey(cacheKey)) {
			
			this.cache.put(cacheKey, null);
			
			// build path for file to analyze
			if (this.checkoutDir == null) {
				if ((this.checkoutDir == null) || (!this.checkoutDir.exists())) {
					throw new UnrecoverableError("Could not checkout transaction " + this.changeset.getId());
				}
			}
			
			final File file = new File(this.checkoutDir.getAbsolutePath() + op1.getChangedPath());
			
			if (!file.exists()) {
				if (Logger.logError()) {
					Logger.error("Cannot find checked out file %s. Returning IGNORE_SCORE.", file.getAbsolutePath());
				}
				
				return MultilevelClustering.IGNORE_SCORE;
			}
			
			File eclipseOutFile = null;
			try {
				eclipseOutFile = FileUtils.createRandomFile(FileShutdownAction.DELETE);
			} catch (final IOException e1) {
				throw new UnrecoverableError(e1);
			}
			final String[] arguments = new String[] { "-vmargs", "-Din=" + file.getAbsolutePath(),
			        "-Dout=" + eclipseOutFile.getAbsolutePath() };
			
			// run the data dependency eclipse app on that file
			Tuple<Integer, List<String>> response;
			try {
				response = CommandExecutor.execute(this.eclipseDir.getAbsolutePath() + FileUtils.fileSeparator
				        + "eclipse", arguments, this.eclipseDir, null, new HashMap<String, String>());
			} catch (final IOException e1) {
				response = new Tuple<Integer, List<String>>(-1, new LinkedList<String>());
			}
			
			if (response.getFirst() != 0) {
				if (Logger.logError()) {
					final StringBuilder sb = new StringBuilder();
					sb.append("Could not generate data dependency for file ");
					sb.append(file.getAbsolutePath());
					sb.append(". Reason:");
					sb.append(FileUtils.lineSeparator);
					if (response.getSecond() != null) {
						for (final String s : response.getSecond()) {
							sb.append(s);
							sb.append(FileUtils.lineSeparator);
						}
					}
					Logger.error(sb.toString());
				}
				return MultilevelClustering.IGNORE_SCORE;
			}
			
			try (BufferedReader reader = new BufferedReader(new FileReader(eclipseOutFile));) {
				assert (reader != null);
				
				final Set<Set<Integer>> lineDependencies = new HashSet<Set<Integer>>();
				String line = "";
				while ((line = reader.readLine()) != null) {
					final String[] lineParts = line.split(",");
					final Set<Integer> set = new HashSet<Integer>();
					for (final String s : lineParts) {
						set.add(Integer.valueOf(s));
					}
					lineDependencies.add(set);
				}
				this.cache.put(cacheKey, lineDependencies);
			} catch (final IOException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
			}
			
		}
		
		final Set<Set<Integer>> lineDependencies = this.cache.get(cacheKey);
		if (lineDependencies == null) {
			return MultilevelClustering.IGNORE_SCORE;
		}
		
		for (final Set<Integer> set : lineDependencies) {
			if (!op1.getChangedElementLocation().coversAnyLine(set).equals(LineCover.FALSE)) {
				if (!op2.getChangedElementLocation().coversAnyLine(set).equals(LineCover.FALSE)) {
					return 1;
				}
			}
		}
		
		return 0;
	}
}
