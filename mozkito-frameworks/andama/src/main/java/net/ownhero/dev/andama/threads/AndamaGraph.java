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
package net.ownhero.dev.andama.threads;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.graph.RelationType;
import net.ownhero.dev.andama.threads.comparator.AndamaThreadComparator;
import net.ownhero.dev.ioda.CommandExecutor;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kisa.Logger;

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
	
	private static final String andamaFileEnding = ".agl";
	
	/**
	 * @param threadGroup
	 * @return
	 */
	public static AndamaGraph buildGraph(final AndamaGroup threadGroup) {
		
		LinkedList<Node> openBranches = new LinkedList<Node>();
		
		PriorityQueue<AndamaThreadable<?, ?>> threads = new PriorityQueue<AndamaThreadable<?, ?>>(
		                                                                                          threadGroup.getThreads()
		                                                                                                     .size(),
		                                                                                          new AndamaThreadComparator());
		threads.addAll(threadGroup.getThreads());
		final AndamaGraph andamaGraph = new AndamaGraph(threadGroup);
		
		if (!andamaGraph.isInitialized()) {
			
			if (Logger.logWarn()) {
				Logger.warn("Toolchain layout has not been created yet. Building graph... (This might take a while)");
			}
			
			prepareGraph(threads, openBranches, andamaGraph);
			buildGraph(threads, openBranches, andamaGraph);
			
			andamaGraph.unpaint(AndamaGraph.workingColor);
			
			if (Logger.logDebug()) {
				Logger.debug("Found " + andamaGraph.alternatives() + " alternatives.");
				Logger.debug("Pruned " + andamaGraph.prune() + " alternatives.");
				Logger.debug("Keeping " + andamaGraph.alternatives() + " alternatives.");
			}
			
			andamaGraph.process();
			andamaGraph.shutdown();
		} else {
			andamaGraph.reconnect();
			
			andamaGraph.shutdown();
			if (andamaGraph.initialized) {
				try {
					FileUtils.deleteDirectory(andamaGraph.dbFile);
				} catch (IOException e) {
					if (Logger.logWarn()) {
						Logger.warn("Could not delete temporary graph database: "
						        + andamaGraph.dbFile.getAbsolutePath());
					}
				}
			}
		}
		
		return andamaGraph;
	}
	
	/**
	 * @param threads
	 * @param openBranches
	 * @param andamaGraph
	 */
	private static void buildGraph(final PriorityQueue<AndamaThreadable<?, ?>> threads,
	                               final LinkedList<Node> openBranches,
	                               final AndamaGraph andamaGraph) {
		
		for (AndamaThreadable<?, ?> thread : getPossibleThreads(openBranches, threads, andamaGraph)) {
			if (AndamaSource.class.isAssignableFrom(thread.getClass())) {
				Node newNode = andamaGraph.getNode(thread);
				
				openBranches.add(newNode);
				PriorityQueue<AndamaThreadable<?, ?>> threadsNew = new PriorityQueue<AndamaThreadable<?, ?>>(threads);
				threadsNew.remove(thread);
				
				buildGraph(threadsNew, openBranches, andamaGraph);
				
				openBranches.remove(newNode);
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
					
					PriorityQueue<AndamaThreadable<?, ?>> threadsNew = new PriorityQueue<AndamaThreadable<?, ?>>(
					                                                                                             threads);
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
						PriorityQueue<AndamaThreadable<?, ?>> threadsNew = new PriorityQueue<AndamaThreadable<?, ?>>(
						                                                                                             threads);
						threadsNew.remove(thread);
						buildGraph(threadsNew, openBranches, andamaGraph);
						
						andamaGraph.disconnectNode(newNode);
						openBranches.remove(newNode);
						openBranches.add(fromNode);
					}
				}
			} else if (AndamaDemultiplexer.class.isAssignableFrom(thread.getClass())) {
				Node newNode = andamaGraph.getNode(thread);
				Collection<Node> candidates = getCandidates(thread, openBranches, andamaGraph);
				if ((candidates.size() > 1) && !CollectionUtils.exists(candidates, new Predicate() {
					
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
					PriorityQueue<AndamaThreadable<?, ?>> threadsNew = new PriorityQueue<AndamaThreadable<?, ?>>(
					                                                                                             threads);
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
					
					if (!fromNode.getProperty(NodeProperty.NODETYPE.name())
					             .equals(AndamaMultiplexer.class.getCanonicalName())) {
						// only remove fromNode from openBranches if we don't
						// attach to a multiplexer
						openBranches.remove(fromNode);
					}
					
					PriorityQueue<AndamaThreadable<?, ?>> threadsNew = new PriorityQueue<AndamaThreadable<?, ?>>(
					                                                                                             threads);
					threadsNew.remove(thread);
					buildGraph(threadsNew, openBranches, andamaGraph);
					checkCompleted(threadsNew, openBranches, andamaGraph);
					
					andamaGraph.disconnectNode(newNode);
					if (!fromNode.getProperty(NodeProperty.NODETYPE.name())
					             .equals(AndamaMultiplexer.class.getCanonicalName())) {
						// only remove fromNode from openBranches if we don't
						// attach to a multiplexer
						openBranches.add(fromNode);
					}
				}
			}
		}
	}
	
	/**
	 * @param threads
	 * @param openBranches
	 * @param andamaGraph
	 */
	private static void checkCompleted(final Collection<AndamaThreadable<?, ?>> threads,
	                                   final LinkedList<Node> openBranches,
	                                   final AndamaGraph andamaGraph) {
		// did we build a complete graph, i.e. no open branches left or only
		// branches that end on attached multiplexer
		if (threads.isEmpty()) {
			if (openBranches.isEmpty()) {
				// this will be only the case if there weren't any multiplexer
				// invoked
				andamaGraph.paint();
			} else if (!CollectionUtils.exists(openBranches, new Predicate() {
				
				// check if open branches do no contain anything else than
				// multiplexer
				@Override
				public boolean evaluate(final Object object) {
					return !(AndamaMultiplexer.class.getCanonicalName().equals(andamaGraph.getProperty(NodeProperty.NODETYPE,
					                                                                                   (Node) object)));
				}
			})) {
				if (!CollectionUtils.exists(openBranches, new Predicate() {
					
					// all multiplexer in the open branches list have to be
					// output connected.
					// otherwise the graph wouldn't be continuous.
					@Override
					public boolean evaluate(final Object object) {
						return !((Node) object).hasRelationship(Direction.OUTGOING);
					}
				})) {
					andamaGraph.paint();
				}
			}
		}
	}
	
	/**
	 * @param thread
	 * @param openBranches
	 * @param graph
	 * @return
	 */
	private static Collection<Node> getCandidates(final AndamaThreadable<?, ?> thread,
	                                              final List<Node> openBranches,
	                                              final AndamaGraph graph) {
		final String inputType = graph.getProperty(NodeProperty.INPUTTYPE, thread).toString();
		
		@SuppressWarnings ("unchecked")
		Collection<Node> collection = CollectionUtils.select(openBranches, new Predicate() {
			
			@Override
			public boolean evaluate(final Object object) {
				return inputType.equals(graph.getProperty(NodeProperty.OUTPUTTYPE, (Node) object));
			}
		});
		
		return collection;
	}
	
	/**
	 * @param openBranches
	 * @param threads
	 * @param graph
	 * @return
	 */
	@SuppressWarnings ("unchecked")
	private static Collection<AndamaThreadable<?, ?>> getPossibleThreads(final List<Node> openBranches,
	                                                                     final Collection<AndamaThreadable<?, ?>> threads,
	                                                                     final AndamaGraph graph) {
		HashSet<Object> set = new HashSet<Object>();
		LinkedList<AndamaThreadable<?, ?>> list = new LinkedList<AndamaThreadable<?, ?>>();
		
		for (Node node : openBranches) {
			set.add(graph.getProperty(NodeProperty.OUTPUTTYPE, node));
		}
		
		for (final Object o : set) {
			list.addAll(CollectionUtils.select(threads, new Predicate() {
				
				@Override
				public boolean evaluate(final Object object) {
					return graph.getProperty(NodeProperty.INPUTTYPE, (AndamaThreadable<?, ?>) object).equals(o);
				}
			}));
		}
		
		return list;
	}
	
	/**
	 * @param threads
	 * @param openBranches
	 * @param andamaGraph
	 */
	private static void prepareGraph(final PriorityQueue<AndamaThreadable<?, ?>> threads,
	                                 final LinkedList<Node> openBranches,
	                                 final AndamaGraph andamaGraph) {
		AndamaThreadable<?, ?> thread = null;
		
		while (((thread = threads.poll()) != null) && AndamaSource.class.isAssignableFrom(thread.getClass())) {
			Node newNode = andamaGraph.getNode(thread);
			openBranches.add(newNode);
		}
		
		// re-add thread the didn't pass the test
		threads.add(thread);
	}
	
	private int                 color        = 0;
	
	private static final String workingColor = "workingcolor";
	
	/**
	 * @param threadGroup
	 * @return
	 */
	private static File provideResource(final AndamaGroup threadGroup) {
		String cache = null;
		
		/*
		 * try to determine name from common string prefix of thread names e.g.
		 * RepositoryReader RepositoryAnalyzer RepositoryParser
		 * RepositoryPersister result in "Repository" -> repository.agl
		 */
		for (AndamaThreadable<?, ?> thread : threadGroup.getThreads()) {
			if (cache == null) {
				cache = thread.getHandle();
			} else if (cache.length() == 0) {
				break;
			} else {
				String tmp = thread.getHandle();
				int i = cache.length();
				
				while ((i >= 0) && !tmp.startsWith(cache.substring(0, i))) {
					--i;
				}
				
				if (i >= 0) {
					cache = cache.substring(0, i);
				}
			}
		}
		
		/*
		 * remove trailing uppercase characters to avoid situations like this:
		 * AndamaGraph AndamaGroup AndamaGadget resulting in AndamaG, but should
		 * result in Andama -> andama.agl
		 */
		while ((cache.length() > 0) && (Character.isUpperCase(cache.charAt(cache.length() - 1)))) {
			cache.substring(0, cache.length() - 1);
		}
		
		if (cache.length() <= 1) {
			// Try to determine from masterthread name
			cache = Thread.currentThread().getName().toLowerCase();
		} else {
			cache = cache.toLowerCase();
		}
		
		String fileName = cache + andamaFileEnding;
		AndamaThreadable<?, ?> threadable = threadGroup.getThreads().iterator().next();
		URL resource = threadable.getClass().getResource(FileUtils.fileSeparator + fileName + ".zip");
		
		// BUG #13 https://dev.own-hero.net/issues/13 and
		// https://dev.own-hero.net/issues/12
		if (resource != null) {
			FileUtils.unzip(new File(resource.getPath()), FileUtils.tmpDir);
			return new File(FileUtils.tmpDir + FileUtils.fileSeparator + fileName);
		} else {
			return new File(fileName);
		}
		
	}
	
	private boolean                 initialized = false;
	private GraphDatabaseService    graph;
	
	private static final String     relation    = "connection_id";
	
	private final AndamaGroup       threadGroup;
	private final Map<String, Node> nodes       = new HashMap<String, Node>();
	
	private final List<Integer>     colors      = new LinkedList<Integer>();
	
	private final File              dbFile;
	
	/**
	 * @param threadGroup
	 */
	public AndamaGraph(final AndamaGroup threadGroup) {
		this.threadGroup = threadGroup;
		
		this.dbFile = provideResource(threadGroup);
		
		if (Logger.logDebug()) {
			Logger.debug("Using db file: " + this.dbFile);
		}
		
		if (this.dbFile.exists()) {
			this.initialized = true;
		}
		
		try {
			this.graph = new EmbeddedGraphDatabase(this.dbFile.getAbsolutePath());
		} catch (Error e) {
			throw new UnrecoverableError("Gathering graph layout failed. Source: " + this.dbFile.getAbsolutePath(), e);
		}
		
		if (!this.initialized) {
			for (AndamaThreadable<?, ?> thread : threadGroup.getThreads()) {
				Node node = createNode(thread);
				this.nodes.put(node.getProperty(NodeProperty.NODENAME.name()).toString(), node);
			}
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
	private boolean connectNode(final Node from,
	                            final Node to) {
		Transaction tx = this.graph.beginTx();
		Iterable<Relationship> relationships = from.getRelationships(RelationType.KNOWS, Direction.OUTGOING);
		Relationship relation = null;
		
		for (Relationship rel : relationships) {
			if (rel.getStartNode().equals(from) && rel.getEndNode().equals(to)) {
				// found relation
				relation = rel;
			}
		}
		
		if (relation == null) {
			relation = from.createRelationshipTo(to, RelationType.KNOWS);
		}
		
		this.graph.index().forRelationships(workingColor)
		          .add(relation, AndamaGraph.relation, from.getProperty("NODEID") + "_" + to.getProperty("NODEID"));
		
		tx.success();
		tx.finish();
		return true;
	}
	
	private <V> void connectThreads(final int edgeColor) {
		connectThreads("color" + edgeColor);
	}
	
	/**
	 * @param edgeColor
	 */
	@SuppressWarnings ("unchecked")
	private <V> void connectThreads(final String edgeColor) {
		Transaction tx = this.graph.beginTx();
		RelationshipIndex relationships = this.graph.index().forRelationships(edgeColor);
		IndexHits<Relationship> query = relationships.query(AndamaGraph.relation, "*");
		
		while (query.hasNext()) {
			Relationship relationship = query.next();
			String fromName = getProperty(NodeProperty.NODENAME, relationship.getStartNode()).toString();
			String toName = getProperty(NodeProperty.NODENAME, relationship.getEndNode()).toString();
			AndamaThreadable<?, V> fromThread = null;
			AndamaThreadable<V, ?> toThread = null;
			
			for (AndamaThreadable<?, ?> thread : this.threadGroup.getThreads()) {
				if (thread.getHandle().equals(fromName)) {
					fromThread = (AndamaThreadable<?, V>) thread;
				} else if (thread.getHandle().equals(toName)) {
					toThread = (AndamaThreadable<V, ?>) thread;
				}
			}
			
			fromThread.connectOutput(toThread);
		}
		
		query.close();
		tx.success();
		tx.finish();
	}
	
	/**
	 * @param thread
	 * @return
	 */
	private Node createNode(final AndamaThreadable<?, ?> thread) {
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
	private boolean disconnectNode(final Node node) {
		Transaction tx = this.graph.beginTx();
		Iterable<Relationship> relationships = node.getRelationships(Direction.INCOMING);
		
		Iterator<Relationship> iterator = relationships.iterator();
		while (iterator.hasNext()) {
			this.graph.index().forRelationships(AndamaGraph.workingColor).remove(iterator.next());
		}
		
		tx.success();
		tx.finish();
		return true;
	}
	
	/**
	 * @param color
	 * @param stdout
	 */
	public void display(final int color,
	                    final boolean stdout) {
		display("color" + color, stdout);
	}
	
	/**
	 * @param color
	 */
	private void display(final String colorName,
	                     final boolean stdout) {
		Transaction tx = this.graph.beginTx();
		
		IndexHits<Relationship> query = this.graph.index().forRelationships(colorName).query(AndamaGraph.relation, "*");
		StringBuilder builder = new StringBuilder();
		
		while (query.hasNext()) {
			Relationship relationship = query.next();
			builder.append(System.getProperty("line.separator"));
			builder.append(getNodeTag(relationship.getStartNode()));
			builder.append(" --> ");
			builder.append(getNodeTag(relationship.getEndNode()));
		}
		
		query.close();
		tx.success();
		tx.finish();
		
		Tuple<Integer, List<String>> execute = CommandExecutor.execute("graph-easy", null, FileUtils.tmpDir,
		                                                               new ByteArrayInputStream(builder.toString()
		                                                                                               .getBytes()),
		                                                               null);
		
		if (stdout) {
			for (String output : execute.getSecond()) {
				System.out.println(output);
			}
		} else {
			
			if (Logger.logInfo()) {
				for (String output : execute.getSecond()) {
					Logger.info(output);
				}
			}
		}
		
	}
	
	/**
	 * @return
	 */
	private List<Integer> getColors() {
		return this.colors;
	}
	
	/**
	 * @param thread
	 * @return
	 */
	private Node getNode(final AndamaThreadable<?, ?> thread) {
		return this.nodes.get(getProperty(NodeProperty.NODENAME, thread));
	}
	
	/**
	 * @param node
	 * @return
	 */
	private String getNodeTag(final Node node) {
		String inputType = null;
		String outputType = null;
		
		if (getProperty(NodeProperty.INPUTTYPE, node) != null) {
			String[] inputSplit = ((String) getProperty(NodeProperty.INPUTTYPE, node)).split("\\.");
			inputType = inputSplit[inputSplit.length - 1];
		}
		
		if (getProperty(NodeProperty.OUTPUTTYPE, node) != null) {
			String[] outputSplit = ((String) getProperty(NodeProperty.OUTPUTTYPE, node)).split("\\.");
			outputType = outputSplit[outputSplit.length - 1];
		}
		
		String nodeName = (String) getProperty(NodeProperty.NODENAME, node);;
		String nodeType = (String) getProperty(NodeProperty.NODETYPE, node);;
		
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
			tag.append("-").append(inputType).append("<");
		} else if (nodeType.equals(AndamaDemultiplexer.class.getCanonicalName())) {
			tag.append(">").append(inputType).append("-");
		} else if (nodeType.equals(AndamaSink.class.getCanonicalName())) {
			tag.append(":").append(inputType);
		}
		tag.append(") ");
		tag.append("] ");
		
		return tag.toString();
	}
	
	/**
	 * @param property
	 * @param thread
	 * @return
	 */
	private Object getProperty(final NodeProperty property,
	                           final AndamaThreadable<?, ?> thread) {
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
				Class<?> clazz = thread.getClass();
				
				while ((clazz.getSuperclass() != null) && (clazz.getSuperclass() != AndamaThread.class)) {
					clazz = clazz.getSuperclass();
				}
				
				if (clazz.getSuperclass() != AndamaThread.class) {
					// TODO ERROR
				}
				
				return clazz.getCanonicalName();
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
	private Object getProperty(final NodeProperty property,
	                           final Node node) {
		return node.hasProperty(property.name())
		                                        ? node.getProperty(property.name())
		                                        : null;
	}
	
	/**
	 * @return the initialized
	 */
	public boolean isInitialized() {
		return this.initialized;
	}
	
	/**
	 * @return
	 */
	private boolean paint() {
		AndamaGraph andamaGraph = this;
		String colorName = "color" + andamaGraph.color;
		
		if (Logger.logInfo()) {
			Logger.info("Saving alternative: " + andamaGraph.color);
		}
		
		if (andamaGraph.graph.index().existsForRelationships(AndamaGraph.workingColor)) {
			Transaction tx = this.graph.beginTx();
			
			RelationshipIndex relationships = andamaGraph.graph.index().forRelationships(AndamaGraph.workingColor);
			IndexHits<Relationship> query = relationships.query(AndamaGraph.relation, "*");
			
			while (query.hasNext()) {
				Relationship rel = query.next();
				
				if (Logger.logDebug()) {
					Logger.debug("Painting with color " + this.color + " " + rel.getStartNode().getProperty("NODEID")
					        + "_" + rel.getEndNode().getProperty("NODEID"));
				}
				andamaGraph.graph.index()
				                 .forRelationships(colorName)
				                 .add(rel,
				                      AndamaGraph.relation,
				                      rel.getStartNode().getProperty("NODEID") + "_"
				                              + rel.getEndNode().getProperty("NODEID"));
			}
			query.close();
			tx.success();
			tx.finish();
			
			this.colors.add(this.color);
			display(andamaGraph.color, false);
			
			andamaGraph.color++;
			
			return true;
		}
		
		return false;
	}
	
	private void process() {
		if (alternatives() > 1) {
			System.out.println("Please choose alternatives: ");
			for (Integer color : getColors()) {
				System.out.println("Identifier " + color + ": ");
				display(color, true);
			}
			
			try {
				boolean done = false;
				
				while (!done) {
					byte read = (byte) System.in.read();
					String string = new String(new byte[] { read });
					
					try {
						int color = Integer.parseInt(string);
						done = true;
						connectThreads(color);
					} catch (NumberFormatException e) {
						done = true;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			
			if (Logger.logInfo()) {
				Logger.info("Using graph: ");
				display(0, false);
			}
			connectThreads(0);
		}
	}
	
	/**
	 * @return
	 */
	private int prune() {
		Map<String, Set<Relationship>> graphs = new HashMap<String, Set<Relationship>>();
		Transaction tx = this.graph.beginTx();
		
		for (int c = 0; c < this.color; ++c) {
			IndexHits<Relationship> query = this.graph.index().forRelationships("color" + c)
			                                          .query(AndamaGraph.relation, "*");
			HashSet<Relationship> relations = new HashSet<Relationship>();
			
			while (query.hasNext()) {
				relations.add(query.next());
			}
			query.close();
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
						this.graph.index().forRelationships("color" + j).delete();
					}
				}
				++j;
			}
			
			++i;
		}
		
		tx.success();
		tx.finish();
		
		if (!prunedColors.isEmpty()) {
			for (Integer col : prunedColors) {
				unpaint(col);
			}
			this.colors.removeAll(prunedColors);
		}
		
		return prunedColors.size();
	}
	
	private void reconnect() {
		Transaction tx = this.graph.beginTx();
		
		String[] indexNames = this.graph.index().relationshipIndexNames();
		
		if (indexNames.length > 1) {
			process();
		} else {
			connectThreads(indexNames[0]);
			display(indexNames[0], false);
		}
		
		tx.success();
		tx.finish();
	}
	
	/**
	 * 
	 */
	private void shutdown() {
		this.graph.shutdown();
	}
	
	/**
	 * @return
	 */
	public boolean unique() {
		return this.color == 1; // just painted 1 times
	}
	
	private boolean unpaint(final int i) {
		return unpaint("color" + i);
	}
	
	/**
	 * @param i
	 */
	private boolean unpaint(final String colorName) {
		
		if (Logger.logDebug()) {
			Logger.debug("Unpainting: " + colorName);
		}
		
		if (this.graph.index().existsForRelationships(colorName)) {
			Transaction tx = this.graph.beginTx();
			
			RelationshipIndex relationships = this.graph.index().forRelationships(colorName);
			relationships.delete();
			
			tx.success();
			tx.finish();
			
			return true;
		}
		
		return false;
	}
}
