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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.graph.RelationType;
import net.ownhero.dev.andama.threads.comparator.AndamaThreadComparator;
import net.ownhero.dev.ioda.CommandExecutor;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.IOUtils;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.ioda.exceptions.FilePermissionException;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.RelationshipIndex;
import org.neo4j.kernel.EmbeddedGraphDatabase;

/**
 * @author Sascha Just <sascha.just@own-hero.net>
 * 
 */
public class AndamaGraph {
	
	private enum NodeProperty {
		INPUTTYPE, NODEID, NODENAME, NODETYPE, OUTPUTTYPE;
	}
	
	private static final class SolutionFound extends Exception {
		
		/**
         * 
         */
		private static final long serialVersionUID = 4021185862022628517L;
		
	}
	
	private static final String andamaFileEnding = ".agl";
	private static final String relation         = "connection_id";
	private static final String workingMarker    = "workingmarker";
	
	/**
	 * @param andamaGroup
	 * @return
	 */
	public static AndamaGraph buildGraph(final AndamaGroup andamaGroup) {
		final AndamaGraph andamaGraph = new AndamaGraph(andamaGroup);
		if (Logger.logInfo()) {
			Logger.info("Graph building requested for: " + andamaGroup.getName());
		}
		
		try {
			if (!andamaGraph.built()) {
				try {
					final LinkedList<Node> openBranches = new LinkedList<Node>();
					
					final PriorityQueue<AndamaThreadable<?, ?>> threads = new PriorityQueue<AndamaThreadable<?, ?>>(
					                                                                                                andamaGroup.getThreads()
					                                                                                                           .size(),
					                                                                                                new AndamaThreadComparator());
					threads.addAll(andamaGroup.getThreads());
					
					if (Logger.logInfo()) {
						Logger.info("Building graph for: " + JavaUtils.collectionToString(threads));
					}
					
					AndamaThreadable<?, ?> thread = null;
					
					while (((thread = threads.poll()) != null)
					        && AndamaSource.class.isAssignableFrom(thread.getClass())) {
						Node newNode = andamaGraph.getNode(thread);
						openBranches.add(newNode);
					}
					
					// re-add thread the didn't pass the test
					threads.add(thread);
					buildGraph(threads, openBranches, andamaGraph);
				} catch (final SolutionFound sf) {
					andamaGraph.reconnect();
					return andamaGraph;
				}
				
				throw new UnrecoverableError("Could not create graph: "
				        + JavaUtils.collectionToString(andamaGroup.getThreads()));
			} else {
				andamaGraph.reconnect();
			}
		} finally {
			andamaGraph.shutdown();
		}
		
		return andamaGraph;
		
	}
	
	/**
	 * @param threads
	 * @param openBranches
	 * @param andamaGraph
	 * @throws SolutionFound
	 */
	private static void buildGraph(final PriorityQueue<AndamaThreadable<?, ?>> threads,
	                               final LinkedList<Node> openBranches,
	                               final AndamaGraph andamaGraph) throws SolutionFound {
		
		for (final AndamaThreadable<?, ?> thread : getPossibleThreads(openBranches, threads, andamaGraph)) {
			if (AndamaSource.class.isAssignableFrom(thread.getClass())) {
				final Node newNode = andamaGraph.getNode(thread);
				
				openBranches.add(newNode);
				final PriorityQueue<AndamaThreadable<?, ?>> threadsNew = new PriorityQueue<AndamaThreadable<?, ?>>(
				                                                                                                   threads);
				threadsNew.remove(thread);
				
				buildGraph(threadsNew, openBranches, andamaGraph);
				
				openBranches.remove(newNode);
			} else if (AndamaFilter.class.isAssignableFrom(thread.getClass())
			        || AndamaTransformer.class.isAssignableFrom(thread.getClass())) {
				for (final Node fromNode : getCandidates(thread, openBranches, andamaGraph)) {
					final Node newNode = andamaGraph.getNode(thread);
					andamaGraph.connectNode(fromNode, newNode);
					openBranches.add(newNode);
					
					if (!fromNode.getProperty(NodeProperty.NODETYPE.name())
					             .equals(AndamaMultiplexer.class.getCanonicalName())) {
						// only remove fromNode from openBranches if we don't
						// attach to a multiplexer
						openBranches.remove(fromNode);
					}
					
					final PriorityQueue<AndamaThreadable<?, ?>> threadsNew = new PriorityQueue<AndamaThreadable<?, ?>>(
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
				for (final Node fromNode : getCandidates(thread, openBranches, andamaGraph)) {
					if (!fromNode.getProperty(NodeProperty.NODETYPE.name())
					             .equals(AndamaMultiplexer.class.getCanonicalName())
					        && !fromNode.getProperty(NodeProperty.NODETYPE.name())
					                    .equals(AndamaDemultiplexer.class.getCanonicalName())) {
						// only attach to non-mux nodes
						final Node newNode = andamaGraph.getNode(thread);
						andamaGraph.connectNode(fromNode, newNode);
						openBranches.remove(fromNode);
						openBranches.add(newNode);
						final PriorityQueue<AndamaThreadable<?, ?>> threadsNew = new PriorityQueue<AndamaThreadable<?, ?>>(
						                                                                                                   threads);
						threadsNew.remove(thread);
						buildGraph(threadsNew, openBranches, andamaGraph);
						
						andamaGraph.disconnectNode(newNode);
						openBranches.remove(newNode);
						openBranches.add(fromNode);
					}
				}
			} else if (AndamaDemultiplexer.class.isAssignableFrom(thread.getClass())) {
				final Node newNode = andamaGraph.getNode(thread);
				final Collection<Node> candidates = getCandidates(thread, openBranches, andamaGraph);
				if ((candidates.size() > 1) && !CollectionUtils.exists(candidates, new Predicate() {
					
					@Override
					public boolean evaluate(final Object object) {
						return ((Node) object).getProperty(NodeProperty.NODETYPE.name())
						                      .equals(AndamaMultiplexer.class.getCanonicalName())
						        || ((Node) object).getProperty(NodeProperty.NODETYPE.name())
						                          .equals(AndamaDemultiplexer.class.getCanonicalName());
					}
				})) {
					
					for (final Node fromNode : candidates) {
						andamaGraph.connectNode(fromNode, newNode);
						openBranches.remove(fromNode);
					}
					openBranches.add(newNode);
					final PriorityQueue<AndamaThreadable<?, ?>> threadsNew = new PriorityQueue<AndamaThreadable<?, ?>>(
					                                                                                                   threads);
					threadsNew.remove(thread);
					
					buildGraph(threadsNew, openBranches, andamaGraph);
					
					for (final Node fromNode : candidates) {
						andamaGraph.disconnectNode(newNode);
						openBranches.add(fromNode);
					}
					openBranches.remove(newNode);
				}
			} else {
				final Node newNode = andamaGraph.getNode(thread);
				final Collection<Node> candidates = getCandidates(thread, openBranches, andamaGraph);
				
				for (final Node fromNode : candidates) {
					andamaGraph.connectNode(fromNode, newNode);
					
					if (!fromNode.getProperty(NodeProperty.NODETYPE.name())
					             .equals(AndamaMultiplexer.class.getCanonicalName())) {
						// only remove fromNode from openBranches if we don't
						// attach to a multiplexer
						openBranches.remove(fromNode);
					}
					
					final PriorityQueue<AndamaThreadable<?, ?>> threadsNew = new PriorityQueue<AndamaThreadable<?, ?>>(
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
	 * @throws SolutionFound
	 */
	private static void checkCompleted(final Collection<AndamaThreadable<?, ?>> threads,
	                                   final LinkedList<Node> openBranches,
	                                   final AndamaGraph andamaGraph) throws SolutionFound {
		// did we build a complete graph, i.e. no open branches left or only
		// branches that end on attached multiplexer
		if (threads.isEmpty()) {
			if (openBranches.isEmpty()) {
				// this will be only the case if there weren't any multiplexer
				// invoked
				andamaGraph.paint();
				throw new SolutionFound();
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
					throw new SolutionFound();
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
		final Collection<Node> collection = CollectionUtils.select(openBranches, new Predicate() {
			
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
		final HashSet<Object> set = new HashSet<Object>();
		final LinkedList<AndamaThreadable<?, ?>> list = new LinkedList<AndamaThreadable<?, ?>>();
		
		for (final Node node : openBranches) {
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
	 * @param andamaGroup
	 * @return
	 */
	private static File provideResource(final AndamaGroup andamaGroup) {
		String cache = andamaGroup.getName();
		
		if ((cache == null) || (cache.length() <= 1)) {
			// Try to determine from masterthread name
			cache = Thread.currentThread().getName().toLowerCase();
			
			if ((cache == null) || (cache.length() <= 1)) {
				/*
				 * try to determine name from common string prefix of thread
				 * names e.g. RepositoryReader RepositoryAnalyzer
				 * RepositoryParser RepositoryPersister result in "Repository"
				 * -> repository.agl
				 */
				for (final AndamaThreadable<?, ?> thread : andamaGroup.getThreads()) {
					if (cache == null) {
						cache = thread.getHandle();
					} else if (cache.length() == 0) {
						break;
					} else {
						final String tmp = thread.getHandle();
						int i = cache.length();
						
						while ((i >= 0) && !tmp.startsWith(cache.substring(0, i))) {
							--i;
						}
						
						if (i >= 0) {
							cache = cache.substring(0, i);
						}
					}
				}
				
				if ((cache != null) && (cache.length() > 0)) {
					cache = cache.toLowerCase();
				} else {
					cache = "andamaGraphLayout";
				}
			}
		} else {
			cache = cache.toLowerCase();
		}
		
		final String fileName = cache + andamaFileEnding;
		final AndamaThreadable<?, ?> threadable = andamaGroup.getThreads().iterator().next();
		final URL resource = threadable.getClass().getResource(FileUtils.fileSeparator + fileName + ".zip");
		
		try {
			if (resource != null) {
				try {
					final File copyOfFile = IOUtils.getTemporaryCopyOfFile(resource.toURI());
					FileUtils.unzip(copyOfFile, FileUtils.tmpDir);
					File file = new File(FileUtils.tmpDir + FileUtils.fileSeparator + fileName);
					try {
						FileUtils.ensureFilePermissions(file, FileUtils.ACCESSIBLE_DIR);
					} catch (final FilePermissionException e) {
						
						if (Logger.logWarn()) {
							Logger.warn("Something went wrong when trying to load graph layout from resource: "
							        + resource.toURI());
							Logger.warn("Causing rebuild of layout.");
						}
						file = new File(fileName);
					}
					return file;
				} catch (final IOException e) {
					return new File(fileName);
				} catch (final URISyntaxException e) {
					return new File(fileName);
				}
			} else {
				return new File(fileName);
			}
		} finally {
			if (Logger.logInfo()) {
				Logger.info("Using database: " + fileName);
			}
		}
	}
	
	private final AndamaGroup                    andamaGroup;
	
	private final File                           dbFile;
	
	private GraphDatabaseService                 graph;
	// private int marker = 0;
	
	private final Map<String, RelationshipIndex> markerIndexes = new HashMap<String, RelationshipIndex>();
	
	private final List<Integer>                  markers       = new LinkedList<Integer>();
	private final Map<String, Node>              nodes         = new HashMap<String, Node>();
	private final String                         markerPrefix  = "marker";
	private Integer                              solutionMarker;
	
	private RelationshipIndex                    workingMarkerRelationship;
	
	public AndamaGraph(final AndamaGroup andamaGroup) {
		this.andamaGroup = andamaGroup;
		this.dbFile = provideResource(andamaGroup);
		
		// if database already exists, check if we can access the data
		if (this.dbFile.exists()) {
			try {
				FileUtils.ensureFilePermissions(this.dbFile, FileUtils.ACCESSIBLE_DIR);
			} catch (final FilePermissionException e) {
				if (Logger.logWarn()) {
					Logger.warn("Cannot access andama graph layout database, at: " + this.dbFile.getAbsolutePath(), e);
				}
				throw new UnrecoverableError("Andama graph building aborted.");
			}
		}
		
		// try to access or create database
		try {
			this.graph = new EmbeddedGraphDatabase(this.dbFile.getAbsolutePath());
		} catch (final Error e) {
			throw new UnrecoverableError("Gathering graph layout failed. Source: " + this.dbFile.getAbsolutePath(), e);
		}
		
		// Check for available solutions
		this.solutionMarker = findSolutionMarker(andamaGroup);
		
		Transaction tx = this.graph.beginTx();
		// clean up working colors
		this.workingMarkerRelationship = this.graph.index().forRelationships(AndamaGraph.workingMarker);
		this.workingMarkerRelationship.delete();
		tx.success();
		tx.finish();
		
		String[] markerNames = this.graph.index().relationshipIndexNames();
		for (String markerName : markerNames) {
			if (markerName.startsWith(this.markerPrefix)) {
				this.markers.add(Integer.parseInt(markerName.substring(this.markerPrefix.length())));
			}
		}
		
		this.workingMarkerRelationship = this.graph.index().forRelationships(AndamaGraph.workingMarker);
	}
	
	/**
	 * @return
	 */
	private boolean built() {
		return this.solutionMarker != null;
	}
	
	/**
	 * @param andamaGroup
	 * @return
	 */
	private String composeIdentifier(final AndamaGroup andamaGroup) {
		return JavaUtils.collectionToString(CollectionUtils.transformedCollection(andamaGroup.getThreads(),
		                                                                          new Transformer() {
			                                                                          
			                                                                          @Override
			                                                                          public Object transform(final Object input) {
				                                                                          return ((AndamaThreadable<?, ?>) input).getName();
			                                                                          }
		                                                                          }));
	}
	
	private boolean connectNode(final Node from,
	                            final Node to) {
		final Transaction tx = this.graph.beginTx();
		final Iterable<Relationship> relationships = from.getRelationships(RelationType.KNOWS, Direction.OUTGOING);
		Relationship relation = null;
		
		for (final Relationship rel : relationships) {
			if (rel.getEndNode().equals(to)) {
				// found relation
				relation = rel;
				break;
			}
		}
		
		if (relation == null) {
			relation = from.createRelationshipTo(to, RelationType.KNOWS);
		}
		
		this.workingMarkerRelationship.add(relation, AndamaGraph.relation,
		                                   from.getProperty("NODEID") + "_" + to.getProperty("NODEID"));
		
		tx.success();
		tx.finish();
		return true;
	}
	
	private <V> void connectThreads(final int marker) {
		connectThreads("marker" + marker);
	}
	
	/**
	 * @param marker
	 */
	@SuppressWarnings ("unchecked")
	private <V> void connectThreads(final String marker) {
		final Transaction tx = this.graph.beginTx();
		if (!this.markerIndexes.containsKey(marker)) {
			this.markerIndexes.put(marker, this.graph.index().forRelationships(marker));
		}
		
		final RelationshipIndex relationships = this.markerIndexes.get(marker);
		
		final IndexHits<Relationship> query = relationships.query(AndamaGraph.relation, "*");
		
		while (query.hasNext()) {
			final Relationship relationship = query.next();
			final String fromName = getProperty(NodeProperty.NODENAME, relationship.getStartNode()).toString();
			final String toName = getProperty(NodeProperty.NODENAME, relationship.getEndNode()).toString();
			AndamaThreadable<?, V> fromThread = null;
			AndamaThreadable<V, ?> toThread = null;
			
			for (final AndamaThreadable<?, ?> thread : this.andamaGroup.getThreads()) {
				if (thread.getHandle().equals(fromName)) {
					fromThread = (AndamaThreadable<?, V>) thread;
					if ((fromThread != null) && (toThread != null)) {
						break;
					}
				} else if (thread.getHandle().equals(toName)) {
					toThread = (AndamaThreadable<V, ?>) thread;
					if ((fromThread != null) && (toThread != null)) {
						break;
					}
				}
			}
			
			fromThread.connectOutput(toThread);
		}
		
		query.close();
		tx.success();
		tx.finish();
	}
	
	/**
	 * @param from
	 * @param to
	 * @return
	 */
	private boolean disconnectNode(final Node node) {
		final Transaction tx = this.graph.beginTx();
		final Iterable<Relationship> relationships = node.getRelationships(Direction.INCOMING);
		
		final Iterator<Relationship> iterator = relationships.iterator();
		while (iterator.hasNext()) {
			this.workingMarkerRelationship.remove(iterator.next());
		}
		
		tx.success();
		tx.finish();
		return true;
	}
	
	/**
	 * @param marker
	 * @param stdout
	 */
	public void display(final int marker,
	                    final boolean stdout) {
		display("marker" + marker, stdout);
	}
	
	/**
	 * @param marker
	 */
	private void display(final String edgeMarker,
	                     final boolean stdout) {
		final Transaction tx = this.graph.beginTx();
		
		if (!this.markerIndexes.containsKey(edgeMarker)) {
			this.markerIndexes.put(edgeMarker, this.graph.index().forRelationships(edgeMarker));
		}
		
		final RelationshipIndex relationships = this.markerIndexes.get(edgeMarker);
		
		final IndexHits<Relationship> query = relationships.query(AndamaGraph.relation, "*");
		final StringBuilder builder = new StringBuilder();
		
		while (query.hasNext()) {
			final Relationship relationship = query.next();
			builder.append(System.getProperty("line.separator"));
			builder.append(getNodeTag(relationship.getStartNode()));
			builder.append(" --> ");
			builder.append(getNodeTag(relationship.getEndNode()));
		}
		
		query.close();
		tx.success();
		tx.finish();
		
		final Tuple<Integer, List<String>> execute = CommandExecutor.execute("graph-easy",
		                                                                     null,
		                                                                     FileUtils.tmpDir,
		                                                                     new ByteArrayInputStream(
		                                                                                              builder.toString()
		                                                                                                     .getBytes()),
		                                                                     null);
		
		if (stdout) {
			for (final String output : execute.getSecond()) {
				System.out.println(output);
			}
		} else {
			
			if (Logger.logInfo()) {
				for (final String output : execute.getSecond()) {
					Logger.info(output);
				}
			}
		}
		
	}
	
	/**
	 * @param andamaGroup
	 * @return
	 */
	private Integer findSolutionMarker(final AndamaGroup andamaGroup) {
		final Transaction tx = this.graph.beginTx();
		
		try {
			final Index<Node> index = this.graph.index().forNodes("solution");
			final IndexHits<Node> hits = index.get("name", composeIdentifier(andamaGroup));
			
			if (hits.size() > 0) {
				final Integer marker = (Integer) hits.iterator().next().getProperty("marker");
				return marker;
			} else {
				return null;
			}
		} catch (NotFoundException e) {
			return null;
		} finally {
			tx.success();
			tx.finish();
		}
	}
	
	/**
	 * @return
	 */
	private Integer getNextMarker() {
		Integer marker = 0;
		while (this.markers.contains(marker)) {
			++marker;
		}
		return marker;
	}
	
	/**
	 * @param thread
	 * @return
	 */
	private Node getNode(final AndamaThreadable<?, ?> thread) {
		if (this.nodes.containsKey(getProperty(NodeProperty.NODENAME, thread))) {
			return this.nodes.get(getProperty(NodeProperty.NODENAME, thread));
		} else {
			final Transaction tx = this.graph.beginTx();
			
			final Index<Node> index = this.graph.index().forNodes("graph");
			final IndexHits<Node> hits = index.get(NodeProperty.NODENAME.name(),
			                                       getProperty(NodeProperty.NODENAME, thread));
			
			Node node = null;
			
			if (hits.size() > 0) {
				node = hits.iterator().next();
			} else {
				node = this.graph.createNode();
				if (getProperty(NodeProperty.INPUTTYPE, thread) != null) {
					node.setProperty(NodeProperty.INPUTTYPE.name(), getProperty(NodeProperty.INPUTTYPE, thread));
				}
				if (getProperty(NodeProperty.OUTPUTTYPE, thread) != null) {
					node.setProperty(NodeProperty.OUTPUTTYPE.name(), getProperty(NodeProperty.OUTPUTTYPE, thread));
				}
				node.setProperty(NodeProperty.NODETYPE.name(), getProperty(NodeProperty.NODETYPE, thread));
				node.setProperty(NodeProperty.NODENAME.name(), getProperty(NodeProperty.NODENAME, thread));
				node.setProperty(NodeProperty.NODEID.name(), getProperty(NodeProperty.NODEID, thread));
			}
			
			tx.success();
			tx.finish();
			this.nodes.put((String) getProperty(NodeProperty.NODENAME, thread), node);
			return node;
		}
	}
	
	/**
	 * @param node
	 * @return
	 */
	private Object getNodeTag(final Node node) {
		final Regex regex = new Regex("[a-zA-Z0-9.]+\\.([^ <>]+)");
		String inputType = null;
		String outputType = null;
		
		if (getProperty(NodeProperty.INPUTTYPE, node) != null) {
			inputType = (String) getProperty(NodeProperty.INPUTTYPE, node);
			inputType = regex.replaceAll(inputType, "$1");
		}
		
		if (getProperty(NodeProperty.OUTPUTTYPE, node) != null) {
			outputType = (String) getProperty(NodeProperty.OUTPUTTYPE, node);
			outputType = regex.replaceAll(outputType, "$1");
		}
		
		final String nodeName = (String) getProperty(NodeProperty.NODENAME, node);;
		final String nodeType = (String) getProperty(NodeProperty.NODETYPE, node);;
		
		final StringBuilder tag = new StringBuilder();
		
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
	 * @return
	 */
	private boolean paint() {
		final AndamaGraph andamaGraph = this;
		Integer marker = getNextMarker();
		final String markerName = "marker" + marker;
		
		if (Logger.logInfo()) {
			Logger.info("Saving solution: " + marker);
		}
		
		if (andamaGraph.graph.index().existsForRelationships(AndamaGraph.workingMarker)) {
			final Transaction tx = this.graph.beginTx();
			
			final IndexHits<Relationship> query = this.workingMarkerRelationship.query(AndamaGraph.relation, "*");
			
			while (query.hasNext()) {
				final Relationship rel = query.next();
				
				if (Logger.logDebug()) {
					Logger.debug("Painting with marker " + marker + " " + rel.getStartNode().getProperty("NODEID")
					        + "_" + rel.getEndNode().getProperty("NODEID"));
				}
				
				if (!this.markerIndexes.containsKey(markerName)) {
					this.markerIndexes.put(markerName, this.graph.index().forRelationships(markerName));
				}
				
				this.markerIndexes.get(markerName).add(rel,
				                                       AndamaGraph.relation,
				                                       rel.getStartNode().getProperty("NODEID") + "_"
				                                               + rel.getEndNode().getProperty("NODEID"));
			}
			query.close();
			
			this.markers.add(marker);
			display(marker, false);
			
			// create solution node
			final Node node = this.graph.createNode();
			final String groupIdentifier = composeIdentifier(this.andamaGroup);
			node.setProperty("name", groupIdentifier);
			this.graph.index().forNodes("solution").add(node, "name", groupIdentifier);
			
			tx.success();
			tx.finish();
			
			this.solutionMarker = marker;
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * @return
	 */
	private boolean reconnect() {
		if (built()) {
			final Transaction tx = this.graph.beginTx();
			
			connectThreads(this.solutionMarker);
			display(this.solutionMarker, false);
			
			tx.success();
			tx.finish();
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 
	 */
	private void shutdown() {
		this.graph.shutdown();
	}
	
}
