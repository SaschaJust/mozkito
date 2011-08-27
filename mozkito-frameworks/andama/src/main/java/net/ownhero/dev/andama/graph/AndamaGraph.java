/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.ownhero.dev.andama.graph;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

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
import net.ownhero.dev.ioda.CommandExecutor;
import net.ownhero.dev.ioda.FileUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.RelationshipIndex;
import org.neo4j.kernel.EmbeddedGraphDatabase;

/**
 * @author Sascha Just <sascha.just@own-hero.net>
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
		        .size(), new AndamaThreadComparator());
		threads.addAll(threadGroup.getThreads());
		final AndamaGraph andamaGraph = new AndamaGraph(threadGroup);
		
		buildGraph(threads, openBranches, andamaGraph);
		
		System.err.println("Found " + andamaGraph.alternatives() + " alternatives.");
		System.err.println("Pruned " + andamaGraph.prune() + " alternatives.");
		System.err.println("Keeping " + andamaGraph.alternatives() + " alternatives.");
		
		if (andamaGraph.alternatives() > 1) {
			System.err.println("Please choose alternatives: ");
			for (Integer color : andamaGraph.getColors()) {
				System.err.println("Identifier " + color + ": ");
				andamaGraph.display(color);
			}
			
			try {
				boolean done = false;
				
				while (!done) {
					byte read = (byte) System.in.read();
					String string = new String(new byte[] { read });
					
					try {
						int color = Integer.parseInt(string);
						done = true;
						andamaGraph.connectThreads(color);
					} catch (NumberFormatException e) {
						done = true;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		andamaGraph.shutdown();
		
		return andamaGraph;
	}
	
	/**
	 * @param color
	 */
	public void display(int color) {
		IndexHits<Relationship> query = this.graph.index().forRelationships("color" + color).query(this.relation, "*");
		StringBuilder builder = new StringBuilder();
		
		while (query.hasNext()) {
			Relationship relationship = query.next();
			builder.append(FileUtils.lineSeparator).append(getNodeTag(relationship.getStartNode())).append(" --> ")
			        .append(getNodeTag(relationship.getEndNode()));
		}
		
		System.err.println(builder);
		CommandExecutor.execute("graph-easy", new String[0], FileUtils.tmpDir, new ByteArrayInputStream(builder
		        .toString().getBytes()), null);
	}
	
	/**
	 * @param node
	 * @return
	 */
	private String getNodeTag(Node node) {
		String inputType = node.getProperty(NodeProperty.INPUTTYPE.name()).toString();
		String outputType = node.getProperty(NodeProperty.OUTPUTTYPE.name()).toString();
		String nodeName = node.getProperty(NodeProperty.NODENAME.name()).toString();
		String nodeType = node.getProperty(NodeProperty.NODETYPE.name()).toString();
		
		StringBuilder tag = new StringBuilder();
		
		tag.append("[ ");
		tag.append(nodeName).append(" ");
		tag.append("(");
		if (nodeType.equals(AndamaSource.class.getCanonicalName())) {
			tag.append(outputType).append(":");
		} else if (nodeType.equals(AndamaFilter.class.getCanonicalName())
		        || (nodeType.equals(AndamaTransformer.class.getCanonicalName()))) {
			tag.append(inputType).append(":").append(outputType);
		} else if (nodeType.equals(AndamaMultiplexer.class.getCanonicalName())) {
			tag.append("-").append(inputType).append("[");
		} else if (nodeType.equals(AndamaDemultiplexer.class.getCanonicalName())) {
			tag.append("]").append(inputType).append("-");
		} else if (nodeType.equals(AndamaSink.class.getCanonicalName())) {
			tag.append(":").append(inputType);
		}
		tag.append(") ");
		tag.append("] ");
		
		return null;
	}
	
	/**
	 * @param threads
	 * @param openBranches
	 * @param andamaGraph
	 */
	private static void buildGraph(final PriorityQueue<AndamaThread<?, ?>> threads,
	        final LinkedList<Node> openBranches, final AndamaGraph andamaGraph) {
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
					
					if (!fromNode.getProperty(NodeProperty.NODETYPE.name()).equals(
					        AndamaMultiplexer.class.getCanonicalName())) {
						// only remove fromNode from openBranches if we don't
						// attach to a multiplexer
						openBranches.remove(fromNode);
					}
					
					PriorityQueue<AndamaThread<?, ?>> threadsNew = new PriorityQueue<AndamaThread<?, ?>>(threads);
					threadsNew.remove(thread);
					
					buildGraph(threadsNew, openBranches, andamaGraph);
					
					andamaGraph.disconnectNode(newNode);
					openBranches.remove(newNode);
					
					if (!fromNode.getProperty(NodeProperty.NODETYPE.name()).equals(
					        AndamaMultiplexer.class.getCanonicalName())) {
						// only remove fromNode from openBranches if we don't
						// attach to a multiplexer
						openBranches.add(fromNode);
					}
				}
			} else if (AndamaMultiplexer.class.isAssignableFrom(thread.getClass())) {
				for (Node fromNode : getCandidates(thread, openBranches, andamaGraph)) {
					if (!fromNode.getProperty(NodeProperty.NODETYPE.name()).equals(
					        AndamaMultiplexer.class.getCanonicalName())
					        && !fromNode.getProperty(NodeProperty.NODETYPE.name()).equals(
					                AndamaDemultiplexer.class.getCanonicalName())) {
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
						return ((Node) object).getProperty(NodeProperty.NODETYPE.name()).equals(
						        AndamaMultiplexer.class.getCanonicalName())
						        || ((Node) object).getProperty(NodeProperty.NODETYPE.name()).equals(
						                AndamaDemultiplexer.class.getCanonicalName());
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
	
	private static void checkCompleted(final LinkedList<Node> openBranches, final AndamaGraph andamaGraph) {
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
	private static Collection<Node> getCandidates(final AndamaThread<?, ?> thread, final List<Node> openBranches,
	        final AndamaGraph graph) {
		final String inputType = graph.getProperty(NodeProperty.INPUTTYPE, thread).toString();
		
		@SuppressWarnings("unchecked") Collection<Node> collection = CollectionUtils.select(openBranches,
		        new Predicate() {
			        
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
	private final List<Integer>        colors       = new LinkedList<Integer>();
	
	private List<Integer> getColors() {
		return colors;
	}
	
	public AndamaGraph(final AndamaGroup threadGroup) {
		this.threadGroup = threadGroup;
		
		File dbFile = new File("/tmp/test.db");
		if (dbFile.exists()) {
			// delete it
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
		return this.colors.size();
	}
	
	/**
	 * @param from
	 * @param to
	 * @return
	 */
	public boolean connectNode(final Node from, final Node to) {
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
	@SuppressWarnings("unchecked")
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
	public Object getProperty(final NodeProperty property, final AndamaThread<?, ?> thread) {
		switch (property) {
			case INPUTTYPE:
				return thread.hasInputConnector() ? thread.getInputType().getCanonicalName() : null;
			case OUTPUTTYPE:
				return thread.hasOutputConnector() ? thread.getOutputType().getCanonicalName() : null;
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
	public Object getProperty(final NodeProperty property, final Node node) {
		return node.getProperty(property.name());
	}
	
	/**
	 * @return
	 */
	private boolean paint() {
		AndamaGraph andamaGraph = this;
		String colorName = "color" + andamaGraph.color;
		
		if (andamaGraph.graph.index().existsForRelationships(andamaGraph.workingColor)) {
			Transaction tx = this.graph.beginTx();
			
			RelationshipIndex relationships = andamaGraph.graph.index().forRelationships(andamaGraph.workingColor);
			IndexHits<Relationship> query = relationships.query(this.relation, "*");
			
			while (query.hasNext()) {
				Relationship rel = query.next();
				andamaGraph.graph
				        .index()
				        .forRelationships(colorName)
				        .add(rel, andamaGraph.relation,
				                rel.getStartNode().getProperty("NODEID") + "_" + rel.getEndNode().getProperty("NODEID"));
			}
			query.close();
			tx.success();
			tx.finish();
			
			this.colors.add(this.color);
			andamaGraph.color++;
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * @return
	 */
	private int prune() {
		Map<String, Set<Relationship>> graphs = new HashMap<String, Set<Relationship>>();
		
		for (int c = 0; c < this.color; ++c) {
			IndexHits<Relationship> query = this.graph.index().forRelationships("color" + c).query(this.relation, "*");
			HashSet<Relationship> relations = new HashSet<Relationship>();
			
			while (query.hasNext()) {
				relations.add(query.next());
			}
			
			graphs.put("color" + c, relations);
		}
		
		HashSet<Integer> prunedColors = new HashSet<Integer>();
		
		int i = 0;
		while (i < this.color) {
			while (prunedColors.contains(i)) {
				++i;
			}
			
			int j = i + 1;
			
			while (j < this.color) {
				
				while (prunedColors.contains(j)) {
					++j;
				}
				
				if ((i >= this.color) || (j >= this.color)) {
					break;
				} else {
					if (CollectionUtils.isEqualCollection(graphs.get("color" + i), graphs.get("color" + j))) {
						prunedColors.add(j);
					}
				}
				++j;
			}
			
			++i;
		}
		
		if (!prunedColors.isEmpty()) {
			for (Integer col : prunedColors) {
				unpaint(col);
			}
			this.colors.removeAll(prunedColors);
		}
		
		return prunedColors.size();
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
	
	/**
	 * @param i
	 */
	private boolean unpaint(final int i) {
		String colorName = "color" + i;
		
		if (this.graph.index().existsForRelationships(colorName)) {
			Transaction tx = this.graph.beginTx();
			
			RelationshipIndex relationships = this.graph.index().forRelationships(colorName);
			IndexHits<Relationship> query = relationships.query(this.relation, "*");
			
			while (query.hasNext()) {
				Relationship rel = query.next();
				relationships.remove(rel);
			}
			query.close();
			tx.success();
			tx.finish();
			
			return true;
		}
		
		return false;
	}
}
