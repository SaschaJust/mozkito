package de.unisaarland.cs.st.reposuite.untangling.voters;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.ioda.CommandExecutor;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.callgraph.model.CallGraph;
import de.unisaarland.cs.st.reposuite.callgraph.model.CallGraphEdge;
import de.unisaarland.cs.st.reposuite.callgraph.model.MethodVertex;
import de.unisaarland.cs.st.reposuite.callgraph.model.VertexFactory;
import de.unisaarland.cs.st.reposuite.clustering.MultilevelClusteringScoreVisitor;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElement;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaMethodDefinition;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;

/**
 * The Class CallGraphHandler.
 * 
 * Works only for JavaMethodDefinitions so far.
 */
public class CallGraphVoter implements MultilevelClusteringScoreVisitor<JavaChangeOperation> {
	
	/** The call graph. */
	private CallGraph                    callGraph;
	
	/** The dijkstra transformer. */
	private static EdgeWeightTransformer dijkstraTransformer = new EdgeWeightTransformer();
	
	/**
	 * Instantiates a new call graph handler.
	 * 
	 * @param eclipseDir
	 *            the eclipse dir
	 * @param eclipseArguments
	 *            the eclipse arguments
	 * @param file
	 * @param transactionId
	 *            the transaction id
	 */
	public CallGraphVoter(final File eclipseDir, final String[] eclipseArguments, final RCSTransaction transaction,
	                      final File cacheDir) {
		
		if ((cacheDir != null) && (cacheDir.isDirectory()) && (cacheDir.canRead())) {
			File serialFile = new File(cacheDir.getAbsolutePath() + FileUtils.fileSeparator + transaction.getId()
			        + ".cg");
			if (serialFile.exists()) {
				callGraph = CallGraph.unserialize(serialFile);
			}
		}
		if (callGraph == null) {
			List<String> arguments = new LinkedList<String>();
			for (String arg : eclipseArguments) {
				arguments.add(arg);
			}
			
			File callGraphFile = FileUtils.createRandomFile(FileShutdownAction.DELETE);
			
			arguments.add("-Doutput=" + callGraphFile.getAbsolutePath());
			
			// generate call graph
			Tuple<Integer, List<String>> response = CommandExecutor.execute("eclipse",
			                                                                arguments.toArray(new String[arguments.size()]),
			                                                                eclipseDir, null, new HashMap<String, String>());
			if (response.getFirst() != 0) {
				if (Logger.logError()) {
					StringBuilder sb = new StringBuilder();
					sb.append("Could not generate call graph for transaction ");
					sb.append(transaction);
					sb.append(". Reason:");
					sb.append(FileUtils.lineSeparator);
					for (String s : response.getSecond()) {
						sb.append(s);
						sb.append(FileUtils.lineSeparator);
					}
					Logger.error(sb.toString());
				}
			} else {
				callGraph = CallGraph.unserialize(callGraphFile);
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
		for (CallGraphEdge e : path) {
			double d = e.getWeight();
			CallGraphEdge reverseEdge = callGraph.findEdge(callGraph.getDest(e), callGraph.getSource(e));
			
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
		
		DijkstraShortestPath<MethodVertex, CallGraphEdge> dijkstra = new DijkstraShortestPath<MethodVertex, CallGraphEdge>(
				callGraph,
				dijkstraTransformer);
		List<CallGraphEdge> sp1 = dijkstra.getPath(v1, v2);
		double d1 = Double.MAX_VALUE;
		if (sp1 != null) {
			d1 = this.distance(sp1);
		}
		List<CallGraphEdge> sp2 = dijkstra.getPath(v2, v1);
		double d2 = Double.MAX_VALUE;
		if (sp2 != null) {
			d2 = this.distance(sp2);
		}
		return Math.min(d1, d2);
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
		
		if (callGraph == null) {
			if (Logger.logError()) {
				Logger.error("Callgraph ot found! Returning zero as score.");
			}
			return 0d;
		}
		
		JavaElement e1 = op1.getChangedElementLocation().getElement();
		JavaElement e2 = op2.getChangedElementLocation().getElement();
		
		if ((e1 instanceof JavaMethodDefinition) && (e2 instanceof JavaMethodDefinition)) {
			MethodVertex v1 = VertexFactory.createMethodVertex(e1.getFullQualifiedName(), "");
			MethodVertex v2 = VertexFactory.createMethodVertex(e2.getFullQualifiedName(), "");
			double distance = this.distance(v1, v2);
			if (distance == Double.MAX_VALUE) {
				// no path found. Return 0
				return 0;
			} else {
				distance = Math.min(2d, distance);
				Condition.check(distance <= 1, "The returned distance must be a value between 0 and 1, but was: "
				                + distance);
				Condition.check(distance >= 0, "The returned distance must be a value between 0 and 1, but was: "
				                + distance);
				return 1d - (distance / 2d);
			}
		}
		return 0;
	}
}
