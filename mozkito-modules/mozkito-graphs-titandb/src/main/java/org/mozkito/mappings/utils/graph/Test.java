/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/

package org.mozkito.mappings.utils.graph;

import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.INDEX_BACKEND_KEY;
import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.STORAGE_DIRECTORY_KEY;

import java.io.File;
import java.io.IOException;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanKey;
import com.thinkaurelius.titan.core.attribute.Geoshape;
import com.thinkaurelius.titan.example.GraphOfTheGodsFactory;
import com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.FileUtils;

/**
 * The Class Test.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class Test {
	
	/** The Constant INDEX_NAME. */
	public static final String INDEX_NAME = "something";
	
	/**
	 * Creates the.
	 * 
	 * @param directory
	 *            the directory
	 * @return the titan graph
	 */
	public static TitanGraph create(final String directory) {
		
		final BaseConfiguration config = new BaseConfiguration();
		final Configuration storage = config.subset(GraphDatabaseConfiguration.STORAGE_NAMESPACE);
		// configuring local backend
		storage.setProperty(GraphDatabaseConfiguration.STORAGE_BACKEND_KEY, "local");
		storage.setProperty(GraphDatabaseConfiguration.STORAGE_DIRECTORY_KEY, directory);
		// configuring elastic search index
		final Configuration index = storage.subset(GraphDatabaseConfiguration.INDEX_NAMESPACE).subset(INDEX_NAME);
		index.setProperty(INDEX_BACKEND_KEY, "elasticsearch");
		// index.setProperty(INDEX_BACKEND_KEY, "lucene");
		index.setProperty("local-mode", true);
		index.setProperty("client-only", false);
		index.setProperty(STORAGE_DIRECTORY_KEY, directory + File.separator + "es");
		
		final TitanGraph graph = TitanFactory.open(config);
		
		graph.makeType().name("name").dataType(String.class).indexed(Vertex.class).unique(Direction.BOTH)
		     .makePropertyKey();
		graph.makeType().name("age").dataType(Integer.class).indexed(INDEX_NAME, Vertex.class).unique(Direction.OUT)
		     .makePropertyKey();
		graph.makeType().name("type").dataType(String.class).unique(Direction.OUT).makePropertyKey();
		
		final TitanKey time = graph.makeType().name("time").dataType(Integer.class).unique(Direction.OUT)
		                           .makePropertyKey();
		final TitanKey reason = graph.makeType().name("reason").dataType(String.class).indexed(INDEX_NAME, Edge.class)
		                             .unique(Direction.OUT).makePropertyKey();
		graph.makeType().name("place").dataType(Geoshape.class).indexed(INDEX_NAME, Edge.class).unique(Direction.OUT)
		     .makePropertyKey();
		
		graph.makeType().name("father").unique(Direction.OUT).makeEdgeLabel();
		graph.makeType().name("mother").unique(Direction.OUT).makeEdgeLabel();
		graph.makeType().name("battled").primaryKey(time).makeEdgeLabel();
		graph.makeType().name("lives").signature(reason).makeEdgeLabel();
		graph.makeType().name("pet").makeEdgeLabel();
		graph.makeType().name("brother").makeEdgeLabel();
		
		graph.commit();
		System.err.println("Created database and indexes and shit.");
		
		// vertices
		final Vertex saturn = graph.addVertex(null);
		saturn.setProperty("name", "saturn");
		saturn.setProperty("age", 10000);
		saturn.setProperty("place", Geoshape.point(38.1f, 23.7f));
		saturn.setProperty("type", "titan");
		graph.commit();
		System.err.println("Created one single vertex.");
		
		return graph;
	}
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(final String[] args) {
		final File dir = new File("database");
		if (dir.exists()) {
			try {
				FileUtils.deleteDirectory(dir);
			} catch (final IOException e) {
				e.printStackTrace(System.err);
				return;
			}
		}
		
		final TitanGraph graph = GraphOfTheGodsFactory.create(new File("database").getAbsolutePath());
		final Iterable<Vertex> vertices = graph.getVertices("name", "hercules");
		
		for (final Vertex vertex : vertices) {
			for (final String key : vertex.getPropertyKeys()) {
				System.err.println(key + " -> " + vertex.getProperty(key));
			}
		}
		graph.shutdown();
	}
}
