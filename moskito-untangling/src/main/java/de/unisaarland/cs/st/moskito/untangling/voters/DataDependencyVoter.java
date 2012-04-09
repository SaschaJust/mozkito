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
package de.unisaarland.cs.st.moskito.untangling.voters;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.CommandExecutor;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.clustering.MultilevelClustering;
import de.unisaarland.cs.st.moskito.clustering.MultilevelClusteringScoreVisitor;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.ppa.model.JavaElementLocation.LineCover;
import de.unisaarland.cs.st.moskito.rcs.Repository;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

/**
 * The Class CallGraphHandler.
 * 
 * Works only for JavaMethodDefinitions so far.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class DataDependencyVoter implements MultilevelClusteringScoreVisitor<JavaChangeOperation> {
	
	/** The eclipse dir. */
	private final File                     eclipseDir;
	
	/** The checkout dir. */
	private File                           checkoutDir      = null;
	
	/** The cache dir. */
	private final File                     cacheDir;
	
	/** The transaction. */
	private final RCSTransaction           transaction;
	
	/** The cache. */
	private Map<String, Set<Set<Integer>>> cache            = new HashMap<String, Set<Set<Integer>>>();
	
	/** The current cache file. */
	private File                           currentCacheFile = null;
	
	/**
	 * Instantiates a new data dependency voter.
	 * 
	 * @param eclipseDir
	 *            the eclipse dir
	 * @param repository
	 *            the repository
	 * @param transaction
	 *            the transaction
	 * @param cacheFile
	 *            the cache file
	 */
	public DataDependencyVoter(@NotNull final File eclipseDir, @NotNull final Repository repository,
	        final RCSTransaction transaction, final File cacheFile) {
		this.transaction = transaction;
		this.eclipseDir = eclipseDir;
		if (this.checkoutDir == null) {
			this.checkoutDir = repository.checkoutPath("/", transaction.getId());
			if ((this.checkoutDir == null) || (!this.checkoutDir.exists())) {
				throw new UnrecoverableError("Could not checkout transaction " + transaction.getId());
			}
		}
		this.cacheDir = cacheFile;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.clustering.MultilevelClusteringScoreVisitor #getMaxPossibleScore()
	 */
	@Override
	public double getMaxPossibleScore() {
		return 1;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.clustering.MultilevelClusteringScoreVisitor #getScore(java.lang.Object,
	 * java.lang.Object)
	 */
	@SuppressWarnings ("unchecked")
	@Override
	public double getScore(final JavaChangeOperation op1,
	                       final JavaChangeOperation op2) {
		
		final String filePath1 = op1.getChangedElementLocation().getFilePath();
		final String filePath2 = op2.getChangedElementLocation().getFilePath();
		
		if (!filePath1.equals(filePath2)) {
			return MultilevelClustering.IGNORE_SCORE;
		}
		
		// build path for file to analyze
		final File file = new File(this.checkoutDir.getAbsolutePath() + op1.getChangedPath());
		
		final String cacheFileName = this.transaction.getId() + ".dd";
		final File cacheFile = new File(this.cacheDir.getAbsolutePath() + FileUtils.fileSeparator + cacheFileName);
		try {
			if ((this.currentCacheFile != null) && (!this.currentCacheFile.getName().equals(cacheFile.getName()))) {
				if ((this.cacheDir != null) && (cacheFile.exists())) {
					final ObjectInputStream objIn = new ObjectInputStream(
					                                                      new BufferedInputStream(
					                                                                              new FileInputStream(
					                                                                                                  cacheFile)));
					this.cache = (Map<String, Set<Set<Integer>>>) objIn.readObject();
					objIn.close();
				}
			}
		} catch (IOException | ClassNotFoundException ignore) {
			// ignore
		}
		
		if ((!this.cache.containsKey(file.getAbsolutePath())) && (file.exists())) {
			if (!file.exists()) {
				if (Logger.logError()) {
					Logger.error("Cannot find checked out file " + file.getAbsolutePath() + ". Returning IGNORE_SCORE.");
				}
				this.cache.put(file.getAbsolutePath(), null);
			}
			
			final File eclipseOutFile = FileUtils.createRandomFile(FileShutdownAction.DELETE);
			
			final String[] arguments = new String[] { "-vmargs", "-Din=file://" + file.getAbsolutePath(),
			        "-Dout=file://" + eclipseOutFile.getAbsolutePath() };
			
			// run the data dependency eclipse app on that file
			final Tuple<Integer, List<String>> response = CommandExecutor.execute(this.eclipseDir.getAbsolutePath()
			                                                                              + FileUtils.fileSeparator
			                                                                              + "eclipse", arguments,
			                                                                      this.eclipseDir, null,
			                                                                      new HashMap<String, String>());
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
				this.cache.put(file.getAbsolutePath(), null);
			}
			
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(eclipseOutFile));
			} catch (final FileNotFoundException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
				this.cache.put(file.getAbsolutePath(), null);
			}
			
			final Set<Set<Integer>> lineDependencies = new HashSet<Set<Integer>>();
			String line = "";
			try {
				while ((line = reader.readLine()) != null) {
					final String[] lineParts = line.split(",");
					final Set<Integer> set = new HashSet<Integer>();
					for (final String s : lineParts) {
						set.add(Integer.valueOf(s));
					}
					lineDependencies.add(set);
				}
			} catch (final IOException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
				this.cache.put(file.getAbsolutePath(), null);
			}
			
			this.cache.put(file.getAbsolutePath(), lineDependencies);
			
			// store changed cache
			try {
				final ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(cacheFile));
				out.writeObject(this.cache);
				out.close();
				this.currentCacheFile = cacheFile;
			} catch (final FileNotFoundException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
			} catch (final IOException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
			}
		}
		
		final Set<Set<Integer>> lineDependencies = this.cache.get(file.getAbsolutePath());
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
