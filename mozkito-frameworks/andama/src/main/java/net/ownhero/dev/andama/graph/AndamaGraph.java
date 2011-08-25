/**
 * 
 */
package net.ownhero.dev.andama.graph;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import net.ownhero.dev.andama.threads.AndamaDemultiplexer;
import net.ownhero.dev.andama.threads.AndamaFilter;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaMultiplexer;
import net.ownhero.dev.andama.threads.AndamaSink;
import net.ownhero.dev.andama.threads.AndamaSource;
import net.ownhero.dev.andama.threads.AndamaThread;
import net.ownhero.dev.andama.threads.AndamaThreadable;
import net.ownhero.dev.andama.threads.AndamaTransformer;
import net.ownhero.dev.andama.threads.comparator.AndamaThreadComparator;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.kernel.EmbeddedGraphDatabase;

/**
 * @author just
 * 
 */
public class AndamaGraph {
	
	private enum NodeProperty {
		INPUTTYPE, OUTPUTTYPE, NODETYPE, NODENAME, NODEID;
	}
	
	/**
	 * @param threadGroup
	 * @return
	 */
	public static AndamaGraph buildGraph(final AndamaGroup threadGroup) {
		
		LinkedList<Node> openBranches = new LinkedList<Node>();
		
		PriorityQueue<AndamaThread<?, ?>> threads = new PriorityQueue<AndamaThread<?, ?>>(threadGroup.getThreads()
		                                                                                             .size(),
		                                                                                  new AndamaThreadComparator());
		threads.addAll(threadGroup.getThreads());
		final AndamaGraph andamaGraph = new AndamaGraph(threadGroup);
		
		buildGraph(threads, openBranches, andamaGraph);
		
		return andamaGraph;
	}
	
	/**
	 * @param threads
	 * @param openBranches
	 * @param andamaGraph
	 */
	private static void buildGraph(final PriorityQueue<AndamaThread<?, ?>> threads,
	                               final LinkedList<Node> openBranches,
	                               final AndamaGraph andamaGraph) {
		for (final AndamaThread<?, ?> thread : threads) {
			if (AndamaSource.class.isAssignableFrom(thread.getClass())) {
				Node newNode = andamaGraph.getNode(thread);
				openBranches.add(newNode);
				PriorityQueue<AndamaThread<?, ?>> threadsNew = new PriorityQueue<AndamaThread<?, ?>>(threads);
				threadsNew.remove(thread);
				
				buildGraph(threadsNew, openBranches, andamaGraph);
				
				openBranches.remove(newNode);
				break;
			} else if (AndamaFilter.class.isAssignableFrom(thread.getClass())
			        || AndamaTransformer.class.isAssignableFrom(thread.getClass())) {
				for (Node fromNode : getCandidates(thread, openBranches, andamaGraph)) {
					Node newNode = andamaGraph.getNode(thread);
					andamaGraph.connectNode(fromNode, newNode);
					openBranches.add(newNode);
					
					if (!fromNode.getProperty(NodeProperty.NODETYPE.name())
					             .equals(AndamaMultiplexer.class.getCanonicalName())) {
						// only remove fromNode from openBranches if we don't
						// attach to a multiplexer
						openBranches.remove(fromNode);
					}
					
					PriorityQueue<AndamaThread<?, ?>> threadsNew = new PriorityQueue<AndamaThread<?, ?>>(threads);
					threadsNew.remove(thread);
					
					buildGraph(threadsNew, openBranches, andamaGraph);
					
					andamaGraph.disconnectNode(newNode);
					openBranches.remove(newNode);
					
					if (!fromNode.getProperty(NodeProperty.NODETYPE.name())
					             .equals(AndamaMultiplexer.class.getCanonicalName())) {
						// only remove fromNode from openBranches if we don't
						// attach to a multiplexer
						openBranches.add(fromNode);
					}
					
					// check for multiplexer
					// |- yes? don't attach multiplexer/demultiplexer
					// |- yes? don't remove multiplexer from openbranches
					// |- no? remove multiplexer from openbranches
					
					// check for demultiplexer
					// |- yes? don't attach multiplexer/demultiplexer
					
					// attach
					
					// recursion
					
					// detach
				}
			} else if (AndamaMultiplexer.class.isAssignableFrom(thread.getClass())) {
				for (Node fromNode : getCandidates(thread, openBranches, andamaGraph)) {
					if (!fromNode.getProperty(NodeProperty.NODETYPE.name())
					             .equals(AndamaMultiplexer.class.getCanonicalName())
					        && !fromNode.getProperty(NodeProperty.NODETYPE.name())
					                    .equals(AndamaDemultiplexer.class.getCanonicalName())) {
						// only attach to non-mux nodes
						Node newNode = andamaGraph.getNode(thread);
						andamaGraph.connectNode(fromNode, newNode);
						openBranches.remove(fromNode);
						openBranches.add(newNode);
						PriorityQueue<AndamaThread<?, ?>> threadsNew = new PriorityQueue<AndamaThread<?, ?>>(threads);
						threadsNew.remove(thread);
						buildGraph(threadsNew, openBranches, andamaGraph);
						
						andamaGraph.disconnectNode(newNode);
						openBranches.remove(newNode);
						openBranches.add(fromNode);
					}
				}
			} else if (AndamaDemultiplexer.class.isAssignableFrom(thread.getClass())) {
				
				// FIXME check candidates to not contain any mux/demux
				Node newNode = andamaGraph.getNode(thread);
				Collection<Node> candidates = getCandidates(thread, openBranches, andamaGraph);
				if (!CollectionUtils.exists(candidates, new Predicate() {
					
					@Override
					public boolean evaluate(final Object object) {
						return ((Node) object).getProperty(NodeProperty.NODETYPE.name())
						                      .equals(AndamaMultiplexer.class.getCanonicalName())
						        || ((Node) object).getProperty(NodeProperty.NODETYPE.name())
						                          .equals(AndamaDemultiplexer.class.getCanonicalName());
					}
				})) {
					
					for (Node fromNode : candidates) {
						andamaGraph.connectNode(fromNode, newNode);
						openBranches.remove(fromNode);
					}
					openBranches.add(newNode);
					PriorityQueue<AndamaThread<?, ?>> threadsNew = new PriorityQueue<AndamaThread<?, ?>>(threads);
					threadsNew.remove(thread);
					
					buildGraph(threadsNew, openBranches, andamaGraph);
					
					for (Node fromNode : candidates) {
						andamaGraph.disconnectNode(newNode);
						openBranches.add(fromNode);
					}
					openBranches.remove(newNode);
				}
			} else {
				Node newNode = andamaGraph.getNode(thread);
				Collection<Node> candidates = getCandidates(thread, openBranches, andamaGraph);
				
				for (Node fromNode : candidates) {
					andamaGraph.connectNode(fromNode, newNode);
					openBranches.remove(fromNode);
					
					PriorityQueue<AndamaThread<?, ?>> threadsNew = new PriorityQueue<AndamaThread<?, ?>>(threads);
					threadsNew.remove(thread);
					buildGraph(threadsNew, openBranches, andamaGraph);
					checkCompleted(openBranches, andamaGraph);
					
					andamaGraph.disconnectNode(newNode);
					openBranches.add(fromNode);
				}
			}
		}
		
	}
	
	private static void checkCompleted(final LinkedList<Node> openBranches,
	                                   final AndamaGraph andamaGraph) {
		// did we build a complete graph, i.e. no open branches left or only
		// branches that end on attached multiplexer
		if (openBranches.isEmpty()) {
			andamaGraph.paint();
		} else if (!CollectionUtils.exists(openBranches, new Predicate() {
			
			@Override
			public boolean evaluate(final Object object) {
				return !(AndamaSink.class.isAssignableFrom(object.getClass()));
			}
		})) {
			if (!CollectionUtils.exists(openBranches, new Predicate() {
				
				@Override
				public boolean evaluate(final Object object) {
					return !((Node) object).hasRelationship(Direction.OUTGOING);
				}
			})) {
				andamaGraph.paint();
			}
		}
	}
	
	/**
	 * @param thread
	 * @param openBranches
	 * @param graph
	 * @return
	 */
	private static Collection<Node> getCandidates(final AndamaThread<?, ?> thread,
	                                              final List<Node> openBranches,
	                                              final AndamaGraph graph) {
		final String inputType = graph.getProperty(NodeProperty.INPUTTYPE, thread).toString();
		for (Node node : openBranches) {
			System.out.println("Node: " + node);
			System.out.println("OUTPUTTYPE: " + graph.getProperty(NodeProperty.OUTPUTTYPE, node));
		}
		@SuppressWarnings ("unchecked")
		Collection<Node> collection = CollectionUtils.select(openBranches, new Predicate() {
			
			@Override
			public boolean evaluate(final Object object) {
				return inputType.equals(graph.getProperty(NodeProperty.OUTPUTTYPE, (Node) object));
			}
		});
		
		return collection;
	}
	
	private int                        color        = 0;
	private final String               workingColor = "workingcolor";
	private final GraphDatabaseService graph;
	
	private final String               relation     = "connection_id";
	
	private final AndamaGroup          threadGroup;
	
	private final Map<String, Node>    nodes        = new HashMap<String, Node>();
	
	public AndamaGraph(final AndamaGroup threadGroup) {
		this.threadGroup = threadGroup;
		
		File dbFile = new File("/tmp/test.db");
		if (dbFile.exists()) {
			dbFile.delete();
		}
		
		this.graph = new EmbeddedGraphDatabase(dbFile.getAbsolutePath());
		
		for (AndamaThread<?, ?> thread : threadGroup.getThreads()) {
			Node node = createNode(thread);
			this.nodes.put(node.getProperty(NodeProperty.NODENAME.name()).toString(), node);
		}
		
	}
	
	/**
	 * @return
	 */
	public int alternatives() {
		return this.color;
	}
	
	/**
	 * @param from
	 * @param to
	 * @return
	 */
	public boolean connectNode(final Node from,
	                           final Node to) {
		Transaction tx = this.graph.beginTx();
		Relationship rel = from.createRelationshipTo(to, RelationType.KNOWS);
		
		this.graph.index().forRelationships(this.workingColor)
		          .add(rel, this.relation, from.getProperty("NODEID") + "_" + to.getProperty("NODEID"));
		
		tx.success();
		tx.finish();
		return true;
	}
	
	/**
	 * @param edgeColor
	 */
	@SuppressWarnings ("unchecked")
	public <V> void connectThreads(final int edgeColor) {
		Transaction tx = this.graph.beginTx();
		
		Iterator<Relationship> iterator = this.graph.index().forRelationships("color" + edgeColor).query("*")
		                                            .iterator();
		
		while (iterator.hasNext()) {
			Relationship relationship = iterator.next();
			String fromName = getProperty(NodeProperty.NODENAME, relationship.getStartNode()).toString();
			String toName = getProperty(NodeProperty.NODENAME, relationship.getEndNode()).toString();
			AndamaThreadable<?, V> fromThread = null;
			AndamaThreadable<V, ?> toThread = null;
			
			for (AndamaThread<?, ?> thread : this.threadGroup.getThreads()) {
				if (thread.getHandle().equals(fromName)) {
					fromThread = (AndamaThreadable<?, V>) thread;
				} else if (thread.getHandle().equals(toName)) {
					toThread = (AndamaThreadable<V, ?>) thread;
				}
			}
			
			// TODO error handling
			fromThread.connectOutput(toThread);
		}
		
		tx.success();
		tx.finish();
	}
	
	/**
	 * @param thread
	 * @return
	 */
	public Node createNode(final AndamaThread<?, ?> thread) {
		Transaction tx = this.graph.beginTx();
		Node node = this.graph.createNode();
		
		if (getProperty(NodeProperty.INPUTTYPE, thread) != null) {
			node.setProperty(NodeProperty.INPUTTYPE.name(), getProperty(NodeProperty.INPUTTYPE, thread));
		}
		if (getProperty(NodeProperty.OUTPUTTYPE, thread) != null) {
			node.setProperty(NodeProperty.OUTPUTTYPE.name(), getProperty(NodeProperty.OUTPUTTYPE, thread));
		}
		node.setProperty(NodeProperty.NODETYPE.name(), getProperty(NodeProperty.NODETYPE, thread));
		node.setProperty(NodeProperty.NODENAME.name(), getProperty(NodeProperty.NODENAME, thread));
		node.setProperty(NodeProperty.NODEID.name(), getProperty(NodeProperty.NODEID, thread));
		
		tx.success();
		tx.finish();
		return node;
	}
	
	/**
	 * @param from
	 * @param to
	 * @return
	 */
	public boolean disconnectNode(final Node node) {
		Transaction tx = this.graph.beginTx();
		Iterable<Relationship> relationships = node.getRelationships(Direction.INCOMING);
		
		Iterator<Relationship> iterator = relationships.iterator();
		while (iterator.hasNext()) {
			iterator.next().delete();
		}
		
		tx.success();
		tx.finish();
		return true;
	}
	
	/**
	 * @param thread
	 * @return
	 */
	private Node getNode(final AndamaThread<?, ?> thread) {
		return this.nodes.get(getProperty(NodeProperty.NODENAME, thread));
	}
	
	/**
	 * @param property
	 * @param thread
	 * @return
	 */
	public Object getProperty(final NodeProperty property,
	                          final AndamaThread<?, ?> thread) {
		switch (property) {
			case INPUTTYPE:
				return thread.hasInputConnector()
				                                 ? thread.getInputType().getCanonicalName()
				                                 : null;
			case OUTPUTTYPE:
				return thread.hasOutputConnector()
				                                  ? thread.getOutputType().getCanonicalName()
				                                  : null;
			case NODETYPE:
				return thread.getClass().getSuperclass().getCanonicalName();
			case NODENAME:
				return thread.getHandle();
			case NODEID:
				return thread.hashCode();
			default:
				return null;
		}
	}
	
	/**
	 * @param property
	 * @param node
	 * @return
	 */
	public Object getProperty(final NodeProperty property,
	                          final Node node) {
		return node.getProperty(property.name());
	}
	
	/**
	 * @return
	 */
	public boolean paint() {
		String colorName = "color" + this.color;
		
		if (this.graph.index().existsForRelationships(this.workingColor)) {
			IndexHits<Relationship> query = this.graph.index().forRelationships(this.workingColor).query("*");
			while (query.hasNext()) {
				Relationship rel = query.next();
				this.graph.index()
				          .forRelationships(colorName)
				          .add(rel, this.relation,
				               rel.getStartNode().getProperty("NODEID") + "_" + rel.getEndNode().getProperty("NODEID"));
			}
			
			this.color++;
		}
		
		return true;
	}
	
	/**
	 * 
	 */
	public void shutdown() {
		this.graph.shutdown();
	}
	
	/**
	 * @return
	 */
	public boolean unique() {
		return this.color == 1; // just painted 1 times
	}
}
