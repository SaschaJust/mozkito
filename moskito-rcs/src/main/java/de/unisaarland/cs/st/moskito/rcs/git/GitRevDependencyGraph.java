package de.unisaarland.cs.st.moskito.rcs.git;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotEmpty;
import net.ownhero.dev.kanuni.annotations.string.NotEmptyString;
import net.ownhero.dev.kisa.Logger;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.IRevDependencyGraph;
import de.unisaarland.cs.st.moskito.rcs.model.RCSBranch;

/**
 * The Class GitRevDependencyGraph.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
class GitRevDependencyGraph implements IRevDependencyGraph {
	
	/** The Constant NODE_ID. */
	private static final String        NODE_ID = "revhash";
	
	/** The Constant BRANCH. */
	private static final String        BRANCH  = "branch";
	
	/** The Constant TAG. */
	private static final String        TAG     = "tag";
	
	/** The graph. */
	private final GraphDatabaseService graph;
	
	/** The index manager. */
	private final IndexManager         indexManager;
	
	/** The node index. */
	private final Index<Node>          nodeIndex;
	
	/** The repository. */
	private final GitRepository        repository;
	
	/**
	 * Instantiates a new git rev dependency graph.
	 * 
	 * @param repository
	 *            the repository
	 */
	@NoneNull
	GitRevDependencyGraph(final GitRepository repository) {
		this.repository = repository;
		final File dbFile = FileUtils.createRandomDir("moskito", "git_rev_dep_graph", FileShutdownAction.DELETE);
		this.graph = new EmbeddedGraphDatabase(dbFile.getAbsolutePath());
		this.indexManager = this.graph.index();
		this.nodeIndex = this.indexManager.forNodes(NODE_ID);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			
			@Override
			public void run() {
				GitRevDependencyGraph.this.graph.shutdown();
			}
		});
	}
	
	/**
	 * Adds the branch.
	 * 
	 * @param v
	 *            the v
	 * @param branchName
	 *            the branch name
	 * @return true, if successful
	 */
	@NoneNull
	private boolean addBranch(@NotEmptyString final String v,
	                          @NotEmptyString final String branchName) {
		// PRECONDITIONS
		
		try {
			final Node node = getNode(v);
			if (node == null) {
				return false;
			}
			final Transaction tx = this.graph.beginTx();
			node.setProperty(BRANCH, branchName);
			tx.success();
			tx.finish();
			return true;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Adds the edge.
	 * 
	 * @param parent
	 *            the parent
	 * @param child
	 *            the child
	 * @param edgeType
	 *            the edge type
	 * @return true, if successful
	 */
	@NoneNull
	protected boolean addEdge(@NotEmptyString final String parent,
	                          @NotEmptyString final String child,
	                          final GitRevDependencyType edgeType) {
		// PRECONDITIONS
		
		try {
			if (!hasVertex(child)) {
				addVertex(child);
			}
			if (!hasVertex(parent)) {
				addVertex(parent);
			}
			
			if (containsEdge(child, parent)) {
				if (Logger.logError()) {
					Logger.error("An edge between " + child + " <-- " + parent + " already exists.");
				}
				return false;
			}
			
			final Node parentNode = getNode(parent);
			final Node childNode = getNode(child);
			
			final Transaction tx = this.graph.beginTx();
			final Relationship relationship = parentNode.createRelationshipTo(childNode, edgeType);
			if (relationship == null) {
				tx.failure();
				tx.finish();
				return false;
			}
			tx.success();
			tx.finish();
			return true;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Adds the vertex.
	 * 
	 * @param v
	 *            the v
	 * @return true, if successful
	 */
	@NoneNull
	protected boolean addVertex(@NotEmptyString final String v) {
		// PRECONDITIONS
		
		try {
			if (hasVertex(v)) {
				if (Logger.logWarn()) {
					Logger.warn("Revision with id `" + v + "` already exists");
				}
				return false;
			}
			final Transaction tx = this.graph.beginTx();
			final Node node = this.graph.createNode();
			if (node == null) {
				tx.failure();
				tx.finish();
				return false;
			}
			node.setProperty(NODE_ID, v);
			
			this.nodeIndex.add(node, NODE_ID, node.getProperty(NODE_ID));
			
			tx.success();
			tx.finish();
			return true;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Contains edge.
	 * 
	 * @param node
	 *            the node
	 * @param parent
	 *            the parent
	 * @return true, if successful
	 */
	@NoneNull
	private boolean containsEdge(@NotEmptyString final String node,
	                             @NotEmptyString final String parent) {
		// PRECONDITIONS
		
		try {
			final GitRevDependencyType result = getEdge(node, parent);
			return result != null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.rcs.IRevDependencyGraph#createFromRepository()
	 */
	@Override
	public boolean createFromRepository() {
		// PRECONDITIONS
		
		final Map<String, String> branchHeads = new HashMap<String, String>();
		final Map<String, Set<String>> tags = new HashMap<String, Set<String>>();
		
		try {
			// use `git ls-remote .` to get all branches and their HEADs
			final List<String> lsRemote = this.repository.getLsRemote();
			for (final String line : lsRemote) {
				final String[] lineParts = line.split("\\s+");
				String branchName = lineParts[1];
				if (Logger.logDebug()) {
					Logger.debug("Found branch reference: " + branchName);
				}
				if (branchName.startsWith("refs/heads/")) {
					branchName = branchName.substring(11);
					if (branchName.equals("master")) {
						branchName = RCSBranch.MASTER_BRANCH_NAME;
					}
				} else if (branchName.startsWith("refs/remotes/")) {
					branchName = branchName.substring(13);
					if (branchName.equals("origin/HEAD") || branchName.equals("origin/master")) {
						continue;
					}
				} else if (branchName.startsWith("refs/pull/")) {
					branchName = branchName.substring(10);
				} else if (branchName.startsWith("refs/tags/")) {
					branchName = branchName.substring(10).replace("^{}", "");
					if (!tags.containsKey(lineParts[0])) {
						tags.put(lineParts[0], new HashSet<String>());
					}
					tags.get(lineParts[0]).add(branchName);
					continue;
				} else {
					continue;
				}
				if (Logger.logDebug()) {
					Logger.debug("Adding branch head for " + branchName + ": " + lineParts[0]);
				}
				branchHeads.put(lineParts[0], branchName);
			}
			
			// use `git rev-list` to get revs and their children: <commit> <branch child> <children ...>
			final List<String> revListParents = this.repository.getRevListParents();
			for (final String line : revListParents) {
				final String[] lineParts = line.split("\\s+");
				if (lineParts.length < 1) {
					throw new UnrecoverableError("Cannot process rev-list --parents. Detected line with no entires.");
				}
				final String child = lineParts[0];
				if (!hasVertex(child)) {
					addVertex(child);
				}
				
				if (tags.containsKey(child)) {
					setTags(child, tags.get(child).toArray(new String[tags.get(child).size()]));
				}
				if (branchHeads.containsKey(child)) {
					addBranch(child, branchHeads.get(child));
				}
				
				if (lineParts.length > 1) {
					final String branchParent = lineParts[1];
					addEdge(branchParent, child, GitRevDependencyType.BRANCH_EDGE);
					for (int i = 2; i < lineParts.length; ++i) {
						addEdge(lineParts[i], child, GitRevDependencyType.MERGE_EDGE);
					}
				}
			}
			
			return true;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.rcs.IRevDependencyGraph#getBranchParent(java.lang.String)
	 */
	@Override
	public String getBranchParent(final String hash) {
		// PRECONDITIONS
		
		try {
			final Node node = getNode(hash);
			if (node == null) {
				throw new UnrecoverableError("Requsting branch parent of node not contained by GitRevDependencyGraph.");
			}
			final Iterable<Relationship> relationships = node.getRelationships(Direction.INCOMING,
			                                                                   GitRevDependencyType.BRANCH_EDGE);
			
			int counter = 0;
			String result = null;
			for (final Relationship relation : relationships) {
				if (result == null) {
					result = relation.getStartNode().getProperty(NODE_ID).toString();
				}
				++counter;
			}
			if (counter > 1) {
				throw new UnrecoverableError("Node " + hash
				        + " has more than one branch parent. This should never occur.");
			}
			return result;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the edge.
	 * 
	 * @param node
	 *            the node
	 * @param parent
	 *            the parent
	 * @return the edge
	 */
	@NoneNull
	public GitRevDependencyType getEdge(@NotEmptyString final String node,
	                                    @NotEmptyString final String parent) {
		final Node nodeNode = getNode(node);
		final Node parentNode = getNode(parent);
		if ((nodeNode == null) || (parentNode == null)) {
			if (Logger.logWarn()) {
				Logger.warn("You cannot retrieve edges for NULL vertices. Returning empty null.");
			}
			return null;
		}
		
		final Iterable<Relationship> relationships = parentNode.getRelationships(Direction.OUTGOING,
		                                                                         GitRevDependencyType.BRANCH_EDGE,
		                                                                         GitRevDependencyType.MERGE_EDGE);
		
		for (final Relationship rel : relationships) {
			if (rel.getEndNode().equals(nodeNode)) {
				final RelationshipType relationshipType = rel.getType();
				return GitRevDependencyType.valueOf(relationshipType.toString());
			}
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.rcs.IRevDependencyGraph#getMergeParent(java.lang.String)
	 */
	@Override
	@NoneNull
	public String getMergeParent(@NotEmptyString final String hash) {
		// PRECONDITIONS
		
		try {
			final Node node = getNode(hash);
			if (node == null) {
				throw new UnrecoverableError("Requsting branch parent of node not contained by GitRevDependencyGraph.");
			}
			final Iterable<Relationship> relationships = node.getRelationships(Direction.INCOMING,
			                                                                   GitRevDependencyType.MERGE_EDGE);
			
			int counter = 0;
			String result = null;
			for (final Relationship relation : relationships) {
				if (result == null) {
					result = relation.getStartNode().getProperty(NODE_ID).toString();
				}
				++counter;
			}
			if (counter > 1) {
				throw new UnrecoverableError("Node " + hash
				        + " has more than one merge parent. This should never occur.");
			}
			return result;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the node.
	 * 
	 * @param node
	 *            the node
	 * @return the node
	 */
	@NoneNull
	private Node getNode(@NotEmptyString final String node) {
		// PRECONDITIONS
		
		try {
			final IndexHits<Node> indexHits = this.nodeIndex.query(NODE_ID, node);
			if (!indexHits.hasNext()) {
				indexHits.close();
				return null;
			}
			final Node nodeNode = indexHits.next();
			indexHits.close();
			return nodeNode;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.rcs.IRevDependencyGraph#getTags(java.lang.String)
	 */
	@Override
	@NoneNull
	public Set<String> getTags(@NotEmptyString final String hash) {
		// PRECONDITIONS
		
		try {
			final Node node = getNode(hash);
			if (node == null) {
				throw new UnrecoverableError("Requsting branch parent of node not contained by GitRevDependencyGraph.");
			}
			final Set<String> resultSet = new HashSet<String>();
			if (node.hasProperty(TAG)) {
				final String[] result = (String[]) node.getProperty(TAG);
				for (final String r : result) {
					resultSet.add(r);
				}
			}
			return resultSet;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public Iterable<String> getVertices() {
		// PRECONDITIONS
		
		try {
			final IndexHits<Node> indexHits = this.nodeIndex.query(NODE_ID, "*");
			
			final Set<String> result = new HashSet<String>();
			for (final Node node : indexHits) {
				result.add(node.getProperty(NODE_ID).toString());
			}
			indexHits.close();
			return new Iterable<String>() {
				
				@Override
				public Iterator<String> iterator() {
					// PRECONDITIONS
					
					try {
						return result.iterator();
					} finally {
						// POSTCONDITIONS
					}
				}
				
			};
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.rcs.IRevDependencyGraph#hasVertex(java.lang.String)
	 */
	@Override
	@NoneNull
	public boolean hasVertex(@NotEmptyString final String hash) {
		// PRECONDITIONS
		
		try {
			return (getNode(hash) != null);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.rcs.IRevDependencyGraph#isBranchHead(java.lang.String)
	 */
	@Override
	@NoneNull
	public String isBranchHead(@NotEmptyString final String hash) {
		// PRECONDITIONS
		
		try {
			final Node node = getNode(hash);
			if (node == null) {
				throw new UnrecoverableError("Requsting branch parent of node not contained by GitRevDependencyGraph.");
			}
			if (node.hasProperty(BRANCH)) {
				return node.getProperty(BRANCH).toString();
			} else {
				return null;
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.rcs.IRevDependencyGraph#readFromDB(de.unisaarland.cs.st.moskito.persistence.
	 * PersistenceUtil)
	 */
	@Override
	@NoneNull
	public boolean readFromDB(final PersistenceUtil persistenceUtil) {
		// PRECONDITIONS
		
		try {
			// TODO to be implemented. Will need stored procedures.
			return false;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the tags.
	 * 
	 * @param v
	 *            the v
	 * @param tagNames
	 *            the tag names
	 * @return true, if successful
	 */
	@NoneNull
	protected boolean setTags(@NotEmptyString final String v,
	                          @NotEmpty final String[] tagNames) {
		final Node node = getNode(v);
		if (node == null) {
			return false;
		}
		final Transaction tx = this.graph.beginTx();
		node.setProperty(TAG, tagNames);
		tx.success();
		tx.finish();
		return true;
	}
	
}
