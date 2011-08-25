/**
 * 
 */
package net.ownhero.dev.andama.chain;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaThread;
import net.ownhero.dev.andama.threads.AndamaThreadable;
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
	
	private final Map<String, Node>                nodes  = new HashMap<String, Node>();
	private final Map<Tuple<String, String>, Edge> edges  = new HashMap<Tuple<String, String>, Edge>();
	private final List<AndamaGraph>                graphs = new LinkedList<AndamaGraph>();
	
	/**
	 * @param threadGroup
	 * @return
	 */
	public void buildGraph(final AndamaGraph graph,
	                       final LinkedList<AndamaThread<?, ?>> threads) {
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
	 * @param <T>
	 * @param list
	 * @param object
	 * @return
	 */
	public <T> LinkedList<T> cloneExcept(final List<T> list,
	                                     final T object) {
		LinkedList<T> ret = new LinkedList<T>();
		for (T t : list) {
			if (t != object) {
				ret.add(t);
			}
		}
		return ret;
	}
	
	public void displayGraph(final AndamaGraph andamaGraph) {
		
		for (AndamaNode andamaNode : andamaGraph.getClosedBranches()) {
			// create a node for this AndamaThread:thread
			Node node = getNode(andamaNode.getName());
			
			for (AndamaNode inputNode : andamaNode.getInputs()) {
				Node iNode = getNode(inputNode.getName());
				getEdge(iNode, node);
			}
			for (AndamaNode outputNode : andamaNode.getOutputs()) {
				Node iNode = getNode(outputNode.getName());
				getEdge(node, iNode);
			}
			
		}
		
		Graph graph = new Graph();
		for (Edge edge : this.edges.values()) {
			graph.edge(edge);
		}
		
		List<String> commands = new LinkedList<String>();
		commands.add("-Tplain");
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
		commands.add("-Tplain");
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
			this.nodes.put(name, node);
		}
		return this.nodes.get(name);
	}
	
}
