/**
 * 
 */
package net.ownhero.dev.andama.threads;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.IOUtils;
import net.ownhero.dev.ioda.exceptions.FilePermissionException;
import net.ownhero.dev.kisa.Logger;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.RelationshipIndex;
import org.neo4j.kernel.EmbeddedGraphDatabase;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class AndamaGraph2 {
	
	private static enum NodeProperty {
		INPUTTYPE, OUTPUTTYPE, NODETYPE, NODENAME, NODEID;
	}
	
	private static final String andamaFileEnding = ".agl";
	
	public static AndamaGraph2 buildGraph(final AndamaGroup threadGroup) {
		return null;
	}
	
	private static File provideResource(final AndamaGroup threadGroup) {
		String cache = null;
		
		if ((threadGroup.getName() != null) && (threadGroup.getName().length() > 0)) {
			cache = threadGroup.getName();
		} else {
			/*
			 * try to determine name from common string prefix of thread names
			 * e.g. RepositoryReader RepositoryAnalyzer RepositoryParser
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
			
			if (cache.length() <= 1) {
				cache = Thread.currentThread().getName().toLowerCase();
			}
		}
		
		cache = cache.toLowerCase();
		
		String fileName = cache + andamaFileEnding;
		AndamaThreadable<?, ?> threadable = threadGroup.getThreads().iterator().next();
		URL resource = threadable.getClass().getResource(FileUtils.fileSeparator + fileName + ".zip");
		
		if (resource != null) {
			try {
				File copyOfFile = IOUtils.getTemporaryCopyOfFile(resource.toURI());
				FileUtils.unzip(copyOfFile, FileUtils.tmpDir);
				File file = new File(FileUtils.tmpDir + FileUtils.fileSeparator + fileName);
				try {
					FileUtils.ensureFilePermissions(file, FileUtils.ACCESSIBLE_DIR);
				} catch (FilePermissionException e) {
					
					if (Logger.logWarn()) {
						Logger.warn("Something went wrong when trying to load graph layout from resource: "
						        + resource.toURI());
						Logger.warn("Causing rebuild of layout.");
					}
					file = new File(fileName);
				}
				return file;
			} catch (IOException e) {
				return new File(fileName);
			} catch (URISyntaxException e) {
				return new File(fileName);
			}
		} else {
			return new File(fileName);
		}
		
	}
	
	private final AndamaGroup                    threadGroup;
	private final File                           dbFile;
	private boolean                              initialized;
	
	private EmbeddedGraphDatabase                graph;
	private final Map<String, Node>              nodes        = new HashMap<String, Node>();
	private final Map<String, RelationshipIndex> colorIndexes = new HashMap<String, RelationshipIndex>();
	private final int                            color        = 0;
	private static final String                  workingColor = "workingcolor";
	
	public AndamaGraph2(final AndamaGroup threadGroup) {
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
		
		for (AndamaThreadable<?, ?> thread : threadGroup.getThreads()) {
			Node node = createNode(thread);
			this.nodes.put(node.getProperty(NodeProperty.NODENAME.name()).toString(), node);
		}
		
		this.colorIndexes.put(workingColor, this.graph.index().forRelationships(AndamaGraph2.workingColor));
	}
	
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
	
	private void shutdown() {
		this.graph.shutdown();
	}
	
}
