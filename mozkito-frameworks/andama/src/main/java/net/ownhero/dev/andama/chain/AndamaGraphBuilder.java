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
	
	private final Map<String, Node>                nodes = new HashMap<String, Node>();
	private final Map<Tuple<String, String>, Edge> edges = new HashMap<Tuple<String, String>, Edge>();
	
	/**
	 * @param threadGroup
	 * @return
	 */
	public boolean buildGraph(final AndamaGroup threadGroup) {
		return false;
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
	public Edge getEdge(final Node from, final Node to) {
		Tuple<String, String> tuple = new Tuple<String, String>(from.attr("id"), to.attr("id"));
		if (!this.edges.containsKey(tuple)) {
			this.edges.put(tuple, new Edge(from, to));
		}
		
		return this.edges.get(tuple);
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
