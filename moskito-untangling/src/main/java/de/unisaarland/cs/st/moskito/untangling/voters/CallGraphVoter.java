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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.DirectoryArgument;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.CommandExecutor;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.lang.StringUtils;

import de.unisaarland.cs.st.moskito.callgraph.model.CallGraph;
import de.unisaarland.cs.st.moskito.callgraph.model.CallGraphEdge;
import de.unisaarland.cs.st.moskito.callgraph.model.MethodVertex;
import de.unisaarland.cs.st.moskito.callgraph.model.VertexFactory;
import de.unisaarland.cs.st.moskito.clustering.MultilevelClustering;
import de.unisaarland.cs.st.moskito.clustering.MultilevelClusteringScoreVisitor;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.ppa.model.JavaElement;
import de.unisaarland.cs.st.moskito.ppa.model.JavaMethodDefinition;
import de.unisaarland.cs.st.moskito.rcs.Repository;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.moskito.settings.DatabaseOptions;
import de.unisaarland.cs.st.moskito.settings.RepositoryOptions;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;

/**
 * The Class CallGraphHandler.
 * 
 * Works only for JavaMethodDefinitions so far.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class CallGraphVoter implements MultilevelClusteringScoreVisitor<JavaChangeOperation> {
	
	public static class Factory extends MultilevelClusteringScoreVisitorFactory<CallGraphVoter> {
		
		private final File     eclipseDir;
		private final File     cacheDir;
		private final String[] eclipseArguments;
		
		protected Factory(final File eclipseDir, final String[] eclipseArguments, final File cacheDir) {
			this.eclipseDir = eclipseDir;
			this.cacheDir = cacheDir;
			this.eclipseArguments = eclipseArguments;
		}
		
		@Override
		public CallGraphVoter createVoter(final RCSTransaction transaction) {
			return new CallGraphVoter(this.eclipseDir, this.eclipseArguments, transaction, this.cacheDir);
		}
		
	}
	
	/**
	 * The Class Options.
	 */
	public static class Options extends
	        ArgumentSetOptions<CallGraphVoter.Factory, ArgumentSet<CallGraphVoter.Factory, Options>> {
		
		private net.ownhero.dev.hiari.settings.DirectoryArgument.Options callgraphEclipseOptions;
		private net.ownhero.dev.hiari.settings.DirectoryArgument.Options callGraphCacheDirOptions;
		private final RepositoryOptions                                  repositoryOptions;
		
		/**
		 * @param argumentSet
		 * @param name
		 * @param description
		 * @param requirements
		 */
		public Options(final ArgumentSet<?, ?> argumentSet, final Requirement requirements,
		        final RepositoryOptions repositoryOptions) {
			super(argumentSet, "callGraphVoter", "CallGraphVoter options.", requirements);
			this.repositoryOptions = repositoryOptions;
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
		 */
		@Override
		public CallGraphVoter.Factory init() {
			// PRECONDITIONS
			final File callgraphEclipse = getSettings().getArgument(this.callgraphEclipseOptions).getValue();
			final File callGraphCacheDir = getSettings().getArgument(this.callGraphCacheDirOptions).getValue();
			final Repository repository = getSettings().getArgumentSet(this.repositoryOptions).getValue();
			
			final DatabaseOptions databaseOptions = this.repositoryOptions.getDatabaseOptions();
			final ArgumentSet<PersistenceUtil, DatabaseOptions> databaseArgs = getSettings().getArgumentSet(databaseOptions);
			
			final List<String> eclipseArgs = new LinkedList<String>();
			eclipseArgs.add("-vmargs");
			eclipseArgs.add(" -Dppa");
			eclipseArgs.add(" -Drepository.uri=" + repository.getUri().toASCIIString());
			eclipseArgs.add(" -Ddabase.host=" + databaseArgs.getArgument(databaseOptions.getDatabaseHost()).getValue());
			eclipseArgs.add(" -Ddabase.user=" + databaseArgs.getArgument(databaseOptions.getDatabaseUser()).getValue());
			eclipseArgs.add(" -Ddatabase.driver="
			        + databaseArgs.getArgument(databaseOptions.getDatabaseDriver()).getValue());
			eclipseArgs.add(" -Ddatabase.middleware="
			        + databaseArgs.getArgument(databaseOptions.getDatabaseMiddleware()).getValue());
			eclipseArgs.add(" -Ddatabase.name="
			        + databaseArgs.getArgument(databaseOptions.getDatabaseName()).getValue());
			eclipseArgs.add(" -Ddatabase.password="
			        + databaseArgs.getArgument(databaseOptions.getDatabasePassword()).getValue());
			eclipseArgs.add(" -Ddatabase.type="
			        + databaseArgs.getArgument(databaseOptions.getDatabaseType()).getValue().toString());
			eclipseArgs.add(" -Ddatabase.unit="
			        + databaseArgs.getArgument(databaseOptions.getDatabaseUnit()).getValue());
			
			return new CallGraphVoter.Factory(callgraphEclipse, eclipseArgs.toArray(new String[eclipseArgs.size()]),
			                                  callGraphCacheDir);
			
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
			map.put(this.repositoryOptions.getName(), this.repositoryOptions);
			this.callgraphEclipseOptions = new DirectoryArgument.Options(
			                                                             argumentSet,
			                                                             "eclipseHome",
			                                                             "Home directory of the reposuite callgraph applcation (must contain ./eclipse executable).",
			                                                             null, Requirement.required, false);
			map.put(this.callgraphEclipseOptions.getName(), this.callgraphEclipseOptions);
			
			this.callGraphCacheDirOptions = new DirectoryArgument.Options(
			                                                              argumentSet,
			                                                              "cacheDir",
			                                                              "Cache directory containing call graphs using the naming converntion <transactionId>.cg",
			                                                              null, Requirement.required, false);
			map.put(this.callGraphCacheDirOptions.getName(), this.callGraphCacheDirOptions);
			
			return map;
		}
	}
	
	/** The call graph. */
	private CallGraph                    callGraph;
	
	/** The dijkstra transformer. */
	private static EdgeWeightTransformer dijkstraTransformer = new EdgeWeightTransformer();
	
	/** The used graph file. */
	private File                         usedGraphFile;
	
	/**
	 * Instantiates a new call graph handler.
	 * 
	 * @param eclipseDir
	 *            the eclipse dir
	 * @param eclipseArguments
	 *            the eclipse arguments
	 * @param transaction
	 *            the transaction
	 * @param cacheDir
	 *            the cache dir
	 */
	protected CallGraphVoter(final File eclipseDir, final String[] eclipseArguments, final RCSTransaction transaction,
	        final File cacheDir) {
		
		if ((cacheDir != null) && (cacheDir.isDirectory()) && (cacheDir.canRead())) {
			final File serialFile = new File(cacheDir.getAbsolutePath() + FileUtils.fileSeparator + transaction.getId()
			        + ".cg");
			if (serialFile.exists()) {
				this.callGraph = CallGraph.unserialize(serialFile);
				this.usedGraphFile = serialFile;
			}
		}
		if (this.callGraph == null) {
			final List<String> arguments = new LinkedList<String>();
			for (final String arg : eclipseArguments) {
				arguments.add(arg);
			}
			
			final File eclipseExecDir = FileUtils.createRandomDir("mozkito", "callgraph_exec", FileShutdownAction.KEEP);
			final File callGraphFile = FileUtils.createRandomFile(FileShutdownAction.DELETE);
			
			arguments.add("-DtransactionId=" + transaction.getId());
			arguments.add("-Doutput=" + callGraphFile.getAbsolutePath());
			
			// generate call graph
			final HashMap<String, String> environment = new HashMap<String, String>();
			// environment.put("PATH", eclipseDir.getAbsolutePath() + ":$PATH");
			if (Logger.logDebug()) {
				Logger.debug("Firing command: %s/eclipse %s ", eclipseDir.getAbsolutePath(),
				             StringUtils.join(arguments.toArray(new String[arguments.size()]), " "));
			}
			final Tuple<Integer, List<String>> response = CommandExecutor.execute(eclipseDir.getAbsolutePath()
			                                                                              + FileUtils.fileSeparator
			                                                                              + "eclipse",
			                                                                      arguments.toArray(new String[arguments.size()]),
			                                                                      eclipseDir,
			                                                                      null, environment);
			try {
				FileUtils.forceDelete(eclipseExecDir);
			} catch (final IOException ignore) {
				//
			}
			if (response.getFirst() != 0) {
				if (Logger.logError()) {
					final StringBuilder sb = new StringBuilder();
					sb.append("Could not generate call graph for transaction ");
					sb.append(transaction);
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
			} else {
				this.callGraph = CallGraph.unserialize(callGraphFile);
				this.usedGraphFile = callGraphFile;
			}
		}
	}
	
	/**
	 * Distance.
	 * 
	 * @param path
	 *            the path
	 * @return the double
	 */
	private double distance(final List<CallGraphEdge> path) {
		Double result = null;
		for (final CallGraphEdge e : path) {
			double d = e.getWeight();
			final CallGraphEdge reverseEdge = this.callGraph.findEdge(this.callGraph.getDest(e),
			                                                          this.callGraph.getSource(e));
			
			if (reverseEdge != null) {
				double occ = 1d / d;
				occ += (1d / reverseEdge.getWeight());
				d = (1d / occ);
			}
			if (result == null) {
				result = Double.valueOf(d);
			} else {
				result += Double.valueOf(d);
			}
			
		}
		if (result == null) {
			return Double.MAX_VALUE;
		}
		return result.doubleValue();
	}
	
	/**
	 * Distance.
	 * 
	 * @param v1
	 *            the v1
	 * @param v2
	 *            the v2
	 * @return the double
	 */
	private double distance(final MethodVertex v1,
	                        final MethodVertex v2) {
		
		if (v1.equals(v2)) {
			return 0;
		}
		
		if ((!this.callGraph.containsVertex(v1)) || (!this.callGraph.containsVertex(v2))) {
			return Double.MAX_VALUE;
		}
		
		final DijkstraShortestPath<MethodVertex, CallGraphEdge> dijkstra = new DijkstraShortestPath<MethodVertex, CallGraphEdge>(
		                                                                                                                         this.callGraph,
		                                                                                                                         dijkstraTransformer);
		
		final List<CallGraphEdge> sp1 = dijkstra.getPath(v1, v2);
		double d1 = Double.MAX_VALUE;
		if (sp1 != null) {
			d1 = this.distance(sp1);
		}
		final List<CallGraphEdge> sp2 = dijkstra.getPath(v2, v1);
		double d2 = Double.MAX_VALUE;
		if (sp2 != null) {
			d2 = this.distance(sp2);
		}
		return Math.min(d1, d2);
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
	@Override
	public double getScore(final JavaChangeOperation op1,
	                       final JavaChangeOperation op2) {
		
		if (this.callGraph == null) {
			if (Logger.logError()) {
				Logger.error("Callgraph ot found! Returning zero as score.");
			}
			return MultilevelClustering.IGNORE_SCORE;
		}
		
		final JavaElement e1 = op1.getChangedElementLocation().getElement();
		final JavaElement e2 = op2.getChangedElementLocation().getElement();
		
		if ((e1 instanceof JavaMethodDefinition) && (e2 instanceof JavaMethodDefinition)) {
			final MethodVertex v1 = VertexFactory.createMethodVertex(e1.getFullQualifiedName(), "");
			final MethodVertex v2 = VertexFactory.createMethodVertex(e2.getFullQualifiedName(), "");
			
			if (!(this.callGraph.containsVertex(v1)) && (!this.callGraph.containsVertex(v2))) {
				if (Logger.logWarn()) {
					final StringBuilder sb = new StringBuilder();
					sb.append("Could not found any vertex in the call graph. This should never happen:\n");
					sb.append("Vertex1: ");
					sb.append(v1.getFullQualifiedMethodName());
					sb.append("\nVertex2: ");
					sb.append(v2.getFullQualifiedMethodName());
					sb.append("\ngraph file: ");
					sb.append(this.usedGraphFile.getAbsolutePath());
					Logger.warn(sb.toString());
				}
			}
			
			double distance = this.distance(v1, v2);
			if (distance == Double.MAX_VALUE) {
				// no path found.
				return 0;
			}
			distance = Math.min(2d, distance);
			final double result = 1d - (distance / 2d);
			Condition.check(result <= 1, "The returned distance must be a value between 0 and 1, but was: " + distance);
			Condition.check(result >= 0, "The returned distance must be a value between 0 and 1, but was: " + distance);
			return result;
		}
		return MultilevelClustering.IGNORE_SCORE;
	}
}
