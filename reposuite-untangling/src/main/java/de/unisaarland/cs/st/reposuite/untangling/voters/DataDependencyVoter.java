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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.ioda.CommandExecutor;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.clustering.MultilevelClusteringScoreVisitor;
import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementLocation.LineCover;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

/**
 * The Class CallGraphHandler.
 * 
 * Works only for JavaMethodDefinitions so far.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class DataDependencyVoter implements MultilevelClusteringScoreVisitor<JavaChangeOperation> {
	
	/** The eclipse dir. */
	private final File           eclipseDir;
	
	/** The checkout dir. */
	private File                 checkoutDir        = null;
	
	/** The repository. */
	private final Repository     repository;
	
	/** The transaction. */
	private final RCSTransaction transaction;
	
	/** The default return value. */
	private static Double        defaultReturnValue = 0d;
	
	/**
	 * Instantiates a new data dependency voter.
	 * 
	 * @param eclipseDir
	 *            the eclipse dir
	 * @param repository
	 *            the repository
	 * @param transaction
	 *            the transaction
	 */
	public DataDependencyVoter(final File eclipseDir, final Repository repository, final RCSTransaction transaction) {
		this.eclipseDir = eclipseDir;
		this.repository = repository;
		this.transaction = transaction;
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
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.clustering.MultilevelClusteringScoreVisitor
	 * #getScore(java.lang.Object, java.lang.Object)
	 */
	@Override
	public double getScore(final JavaChangeOperation op1,
	                       final JavaChangeOperation op2) {
		
		if (checkoutDir == null) {
			checkoutDir = repository.checkoutPath("/", transaction.getId());
			if ((checkoutDir == null) || (!checkoutDir.exists())) {
				throw new UnrecoverableError("Could not checkout transaction " + transaction.getId());
			}
		}
		
		String filePath1 = op1.getChangedElementLocation().getFilePath();
		String filePath2 = op2.getChangedElementLocation().getFilePath();
		
		if (!filePath1.equals(filePath2)) {
			return 0;
		}
		
		// build path for file to analyze
		File file = new File(checkoutDir.getAbsolutePath() + op1.getChangedPath());
		if (!file.exists()) {
			if (Logger.logError()) {
				Logger.error("Cannot find checked out file " + file.getAbsolutePath() + ". Returning 0.5.");
			}
			return defaultReturnValue;
		}
		
		File eclipseOutFile = FileUtils.createRandomFile(FileShutdownAction.DELETE);
		
		String[] arguments = new String[] { "-vmargs", "-Din=" + file.getAbsolutePath(),
				"-Dout=" + eclipseOutFile.getAbsolutePath() };
		
		// run the data dependency eclipse app on that file
		Tuple<Integer, List<String>> response = CommandExecutor.execute(eclipseDir.getAbsolutePath()
		                                                                + FileUtils.fileSeparator + "eclipse", arguments, eclipseDir, null, new HashMap<String, String>());
		if (response.getFirst() != 0) {
			if (Logger.logError()) {
				StringBuilder sb = new StringBuilder();
				sb.append("Could not generate data dependency for file ");
				sb.append(file.getAbsolutePath());
				sb.append(". Reason:");
				sb.append(FileUtils.lineSeparator);
				if (response.getSecond() != null) {
					for (String s : response.getSecond()) {
						sb.append(s);
						sb.append(FileUtils.lineSeparator);
					}
				}
				Logger.error(sb.toString());
			}
			return defaultReturnValue;
		}
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(eclipseOutFile));
		} catch (FileNotFoundException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return defaultReturnValue;
		}
		
		Set<Set<Integer>> lineDependencies = new HashSet<Set<Integer>>();
		String line = "";
		try {
			while ((line = reader.readLine()) != null) {
				String[] lineParts = line.split(",");
				Set<Integer> set = new HashSet<Integer>();
				for (String s : lineParts) {
					set.add(Integer.valueOf(s));
				}
				lineDependencies.add(set);
			}
		} catch (IOException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return defaultReturnValue;
		}
		
		for (Set<Integer> set : lineDependencies) {
			if (!op1.getChangedElementLocation().coversAnyLine(set).equals(LineCover.FALSE)) {
				if (!op2.getChangedElementLocation().coversAnyLine(set).equals(LineCover.FALSE)) {
					return 1;
				}
			}
		}
		
		return defaultReturnValue;
	}
}
