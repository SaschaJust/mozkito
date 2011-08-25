/**
 * 
 */
package net.ownhero.dev.andama.chain;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaSource;
import net.ownhero.dev.andama.threads.AndamaThread;
import net.ownhero.dev.andama.threads.AndamaThreadable;
import net.ownhero.dev.andama.threads.comparator.AndamaThreadComparator;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kisa.Logger;

import org.kohsuke.graphviz.Edge;
import org.kohsuke.graphviz.Graph;
import org.kohsuke.graphviz.Node;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class AndamaGraphBuilder {
	
	private final Map<String, Node>                nodes       = new HashMap<String, Node>();
	private final Map<Tuple<String, String>, Edge> edges       = new HashMap<Tuple<String, String>, Edge>();
	private final List<AndamaGraph>                graphs      = new LinkedList<AndamaGraph>();
	private final static int                       arrowLength = "-+->".length();
	private final static int                       frameWidth  = "|  |".length();
	
	/**
	 * @param threadGroup
	 * @return
	 */
	private void buildGraph(final AndamaGraph graph,
	                        final Collection<AndamaThread<?, ?>> threads) {
		if (!threads.isEmpty()) {
			LinkedList<AndamaThread<?, ?>> pThreads = new LinkedList<AndamaThread<?, ?>>();
			
			for (AndamaThread<?, ?> thread : threads) {
				pThreads = cloneExcept(threads, thread);
				
				for (AndamaNode node : graph.getMatching(thread)) {
					graph.attach(node, thread);
					buildGraph(graph, pThreads);
					graph.detach(thread);
				}
			}
		} else {
			this.graphs.add(graph);
		}
	}
	
	/**
	 * @param threads
	 * @return
	 */
	public AndamaGraph buildGraph(final LinkedList<AndamaThread<?, ?>> threads) {
		AndamaGraph graph = new AndamaGraph();
		TreeSet<AndamaThread<?, ?>> orderedThreads = new TreeSet<AndamaThread<?, ?>>(new AndamaThreadComparator());
		orderedThreads.addAll(threads);
		LinkedList<AndamaThread<?, ?>> deletes = new LinkedList<AndamaThread<?, ?>>();
		
		for (AndamaThread<?, ?> thread : orderedThreads) {
			if (AndamaSource.class.isAssignableFrom(thread.getClass())) {
				AndamaNode sourceNode = new AndamaNode(thread);
				System.err.println("Adding source " + sourceNode);
				graph.addSource(sourceNode);
				deletes.add(thread);
			}
		}
		
		orderedThreads.removeAll(deletes);
		System.err.println("Adding threads to graph: ");
		for (AndamaThread<?, ?> thread : orderedThreads) {
			System.err.println(thread);
		}
		
		buildGraph(graph, orderedThreads);
		
		// TODO check results first
		if (this.graphs.isEmpty()) {
			return null;
		} else {
			return this.graphs.get(0);
		}
	}
	
	/**
	 * @param <T>
	 * @param list
	 * @param object
	 * @return
	 */
	public <T> LinkedList<T> cloneExcept(final Collection<T> list,
	                                     final T object) {
		LinkedList<T> ret = new LinkedList<T>();
		for (T t : list) {
			if (t != object) {
				ret.add(t);
			}
		}
		return ret;
	}
	
	/**
	 * @param node
	 * @param length
	 * @return
	 */
	private int computeBranchLength(final AndamaNode node,
	                                final int length) {
		int value = length;
		
		for (AndamaNode outputNode : node.getOutputs()) {
			value = computeBranchLength(outputNode, length + outputNode.getName().length() + frameWidth + arrowLength);
		}
		
		if (node.isSink()) {
			return value - arrowLength;
		} else {
			return value;
		}
	}
	
	/**
	 * @param endNode
	 * @return
	 */
	private int computeMaxBranchWidth(final AndamaNode endNode) {
		int value = 0;
		
		endNode.getSources();
		
		// breadth first search
		// remember openbranches
		// compute segment sizes on mergepoint (demux/sink)
		
		return value;
	}
	
	/**
	 * @param endNode
	 * @return
	 */
	private int computeTotalLength(final AndamaNode endNode) {
		int value = 0;
		
		HashSet<AndamaNode> sources = endNode.getSources();
		
		for (AndamaNode sourceNode : sources) {
			value = Math.max(value,
			                 computeBranchLength(sourceNode, sourceNode.getName().length() + frameWidth + arrowLength));
		}
		return value;
	}
	
	/**
	 * @param andamaGraph
	 */
	public void displayGraph(final AndamaGraph andamaGraph) {
		for (AndamaNode andamaNode : andamaGraph.getClosedBranches()) {
			this.edges.clear();
			this.nodes.clear();
			// create a node for this AndamaThread:thread
			Node node = getNode(andamaNode.getName());
			
			processInputs(andamaNode, node);
			processOutputs(andamaNode, node);
			
			Graph graph = new Graph();
			for (Edge edge : this.edges.values()) {
				graph.edge(edge);
			}
			
			List<String> commands = new LinkedList<String>();
			commands.add("/usr/local/bin/dot");
			commands.add("-Tpng");
			commands.add("-oGraph_" + new Date().getTime() + ".png");
			try {
				graph.generateTo(commands, System.err);
			} catch (InterruptedException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
			} catch (IOException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
			}
		}
	}
	
	/**
	 * @param threadGroup
	 */
	public void displayGraph(final AndamaGroup threadGroup) {
		Collection<AndamaThread<?, ?>> threads = threadGroup.getThreads();
		for (AndamaThread<?, ?> thread : threads) {
			// create a node for this AndamaThread:thread
			Node node = getNode(thread.getHandle());
			
			if (thread.isInputConnected()) {
				Collection<?> inputThreads = thread.getInputThreads();
				
				for (Object object : inputThreads) {
					AndamaThreadable<?, ?> inputThread = (AndamaThreadable<?, ?>) object;
					Node iNode = getNode(inputThread.getHandle());
					getEdge(iNode, node);
				}
			}
			
			if (thread.isOutputConnected()) {
				Collection<?> outputThreads = thread.getOutputThreads();
				
				for (Object object : outputThreads) {
					AndamaThreadable<?, ?> outputThread = (AndamaThreadable<?, ?>) object;
					Node iNode = getNode(outputThread.getHandle());
					getEdge(iNode, node);
				}
			}
		}
		
		Graph graph = new Graph();
		for (Edge edge : this.edges.values()) {
			graph.edge(edge);
		}
		
		List<String> commands = new LinkedList<String>();
		commands.add("/usr/local/bin/dot");
		commands.add("-Tpng");
		commands.add("-oGraph_" + new Date().getTime() + ".png");
		
		try {
			graph.generateTo(commands, Logger.debug());
		} catch (InterruptedException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		} catch (IOException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * @param from
	 * @param to
	 * @return
	 */
	public Edge getEdge(final Node from,
	                    final Node to) {
		Tuple<String, String> tuple = new Tuple<String, String>(from.attr("id"), to.attr("id"));
		if (!this.edges.containsKey(tuple)) {
			this.edges.put(tuple, new Edge(from, to));
		}
		
		return this.edges.get(tuple);
	}
	
	/**
	 * @return the graphs
	 */
	public List<AndamaGraph> getGraphs() {
		return this.graphs;
	}
	
	/**
	 * @param name
	 * @return
	 */
	public Node getNode(final String name) {
		if (!this.nodes.containsKey(name)) {
			Node node = new Node();
			node.id(name);
			node.attr("id", name);
			this.nodes.put(name, node);
		}
		return this.nodes.get(name);
	}
	
	/**
	 * @param andamaNode
	 * @param node
	 */
	public void processInputs(final AndamaNode andamaNode,
	                          final Node node) {
		for (AndamaNode inputNode : andamaNode.getInputs()) {
			Node iNode = getNode(inputNode.getName());
			getEdge(iNode, node);
			processInputs(inputNode, iNode);
		}
	}
	
	/**
	 * @param andamaNode
	 * @param node
	 */
	public void processOutputs(final AndamaNode andamaNode,
	                           final Node node) {
		for (AndamaNode outputNode : andamaNode.getOutputs()) {
			Node iNode = getNode(outputNode.getName());
			getEdge(iNode, node);
			processInputs(outputNode, iNode);
		}
	}
	
}
