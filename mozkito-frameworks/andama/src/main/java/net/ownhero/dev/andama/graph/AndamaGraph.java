/**
 * 
 */
package net.ownhero.dev.andama.graph;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
	
	private int                        color    = 0;
	private final GraphDatabaseService graph;
	private final String               relation = "connection_id";
	private final AndamaGroup          threadGroup;
	
	public AndamaGraph(AndamaGroup threadGroup) {
		this.threadGroup = threadGroup;
		File dbFile = new File("/tmp/test.db");
		
		graph = new EmbeddedGraphDatabase(dbFile.getAbsolutePath());
	}
	
	/**
	 * @param threadGroup
	 * @return
	 */
	public static AndamaGraph buildGraph(final AndamaGroup threadGroup) {
		
		LinkedList<Node> openBranches = new LinkedList<Node>();
		
		PriorityQueue<AndamaThread<?, ?>> threads = new PriorityQueue<AndamaThread<?, ?>>(threadGroup.getThreads()
		        .size(), new AndamaThreadComparator());
		
		final AndamaGraph andamaGraph = new AndamaGraph(threadGroup);
		
		buildGraph(threads, openBranches, andamaGraph);
		
		return andamaGraph;
	}
	
	/**
	 * @param threads
	 * @param openBranches
	 * @param andamaGraph
	 */
	private static void buildGraph(PriorityQueue<AndamaThread<?, ?>> threads, LinkedList<Node> openBranches,
	        AndamaGraph andamaGraph) {
		for (final AndamaThread<?, ?> thread : threads) {
			if (AndamaSource.class.isAssignableFrom(thread.getClass())) {
				openBranches.add(andamaGraph.createNode(thread));
				break;
			} else if (AndamaFilter.class.isAssignableFrom(thread.getClass())
			        || AndamaTransformer.class.isAssignableFrom(thread.getClass())) {
				for (Node fromNode : getCandidates(thread, openBranches, andamaGraph)) {
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
				
			} else if (AndamaDemultiplexer.class.isAssignableFrom(thread.getClass())) {
				// check for demultiplexer
				// check for multiplexer
				// attach to all
				// remove all from openBranches
			} else {
				// sink
				// attach and remove
			}
		}
		
		// did we build a complete graph, i.e. no open branches left or only branches that end on attached multiplexer
		if (openBranches.isEmpty()) {
			andamaGraph.paint();
		} else if (!CollectionUtils.exists(openBranches, new Predicate() {
			
			@Override
			public boolean evaluate(Object object) {
				return !(AndamaSink.class.isAssignableFrom(object.getClass()));
			}
		})) {
			if (!CollectionUtils.exists(openBranches, new Predicate() {
				
				@Override
				public boolean evaluate(Object object) {
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
	private static Collection<Node> getCandidates(final AndamaThread<?, ?> thread, final List<Node> openBranches,
	        final AndamaGraph graph) {
		final Class<?> inputType = (Class<?>) graph.getProperty(NodeProperty.INPUTTYPE, thread);
		
		@SuppressWarnings("unchecked") Collection<Node> collection = CollectionUtils.predicatedCollection(openBranches,
		        new Predicate() {
			        
			        @Override
			        public boolean evaluate(Object object) {
				        return graph.getProperty(NodeProperty.OUTPUTTYPE, thread).equals(inputType);
			        }
		        });
		
		return collection;
	}
	
	/**
	 * @return
	 */
	public boolean unique() {
		return color == 1; // just painted 1 times
	}
	
	/**
	 * @return
	 */
	public int alternatives() {
		return color;
	}
	
	/**
	 * 
	 */
	public void shutdown() {
		graph.shutdown();
	}
	
	/**
	 * @param edgeColor
	 */
	@SuppressWarnings("unchecked")
	public <V> void connectThreads(int edgeColor) {
		Transaction tx = graph.beginTx();
		
		Iterator<Relationship> iterator = graph.index().forRelationships("color" + edgeColor).query("*").iterator();
		
		while (iterator.hasNext()) {
			Relationship relationship = iterator.next();
			String fromName = getProperty(NodeProperty.NODENAME, relationship.getStartNode()).toString();
			String toName = getProperty(NodeProperty.NODENAME, relationship.getEndNode()).toString();
			AndamaThreadable<?, V> fromThread = null;
			AndamaThreadable<V, ?> toThread = null;
			
			for (AndamaThread<?, ?> thread : threadGroup.getThreads()) {
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
		Transaction tx = graph.beginTx();
		Node node = graph.createNode();
		
		node.setProperty(NodeProperty.INPUTTYPE.name(), getProperty(NodeProperty.INPUTTYPE, thread));
		node.setProperty(NodeProperty.OUTPUTTYPE.name(), getProperty(NodeProperty.OUTPUTTYPE, thread));
		node.setProperty(NodeProperty.NODETYPE.name(), getProperty(NodeProperty.NODETYPE, thread));
		node.setProperty(NodeProperty.NODENAME.name(), getProperty(NodeProperty.NODENAME, thread));
		node.setProperty(NodeProperty.NODEID.name(), getProperty(NodeProperty.NODEID, thread));
		
		tx.success();
		tx.finish();
		return node;
	}
	
	/**
	 * @param property
	 * @param node
	 * @return
	 */
	public Object getProperty(NodeProperty property, final Node node) {
		return node.getProperty(property.name());
	}
	
	/**
	 * @param property
	 * @param thread
	 * @return
	 */
	public Object getProperty(NodeProperty property, final AndamaThread<?, ?> thread) {
		switch (property) {
			case INPUTTYPE:
				return thread.getInputType().getCanonicalName();
			case OUTPUTTYPE:
				return thread.getOutputType().getCanonicalName();
			case NODETYPE:
				return thread.getClass().getSuperclass();
			case NODENAME:
				return thread.getHandle();
			case NODEID:
				return thread.hashCode();
			default:
				return null;
		}
	}
	
	/**
	 * @param from
	 * @param to
	 * @return
	 */
	public boolean connectNode(final Node from, final Node to) {
		Transaction tx = graph.beginTx();
		Relationship rel = from.createRelationshipTo(to, RelationType.KNOWS);
		
		graph.index().forRelationships("workingcolor")
		        .add(rel, relation, from.getProperty("NODEID") + "_" + to.getProperty("NODEID"));
		
		tx.success();
		tx.finish();
		return true;
	}
	
	/**
	 * @param from
	 * @param to
	 * @return
	 */
	public boolean disconnectNode(final Node node) {
		Transaction tx = graph.beginTx();
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
	 * @return
	 */
	public boolean paint() {
		String colorName = "color" + color;
		
		if (graph.index().existsForRelationships("workingcolor")) {
			IndexHits<Relationship> query = graph.index().forRelationships("workingcolor").query("*");
			while (query.hasNext()) {
				Relationship rel = query.next();
				graph.index()
				        .forRelationships(colorName)
				        .add(rel, relation,
				                rel.getStartNode().getProperty("NODEID") + "_" + rel.getEndNode().getProperty("NODEID"));
			}
			
			color++;
		}
		
		return true;
	}
}
