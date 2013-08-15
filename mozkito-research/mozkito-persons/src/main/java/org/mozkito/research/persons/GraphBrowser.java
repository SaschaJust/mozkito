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

package org.mozkito.research.persons;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.Vertex;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetFactory;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.graphs.GraphManager;
import org.mozkito.graphs.settings.GraphOptions;

/**
 * The Class GraphBrowser.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class GraphBrowser implements Runnable {
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(final String[] args) {
		final Thread thread = new Thread(new GraphBrowser());
		thread.start();
	}
	
	/** The graph options. */
	private GraphOptions      graphOptions;
	
	/** The graph manager. */
	private GraphManager      graphManager;
	
	/** The graph. */
	private KeyIndexableGraph graph;
	
	/**
	 * Instantiates a new graph browser.
	 */
	public GraphBrowser() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// body
			try {
				final Settings settings = new Settings();
				this.graphOptions = new GraphOptions(settings.getRoot(), Requirement.required);
				final ArgumentSet<GraphManager, GraphOptions> graphArgument = ArgumentSetFactory.create(this.graphOptions);
				
				if (settings.helpRequested()) {
					if (Logger.logAlways()) {
						Logger.always(settings.getHelpString());
					}
					throw new Shutdown("help requested");
				}
				
				this.graphManager = graphArgument.getValue();
				this.graph = this.graphManager.getGraph();
				
			} catch (final SettingsParseError | ArgumentSetRegistrationException | ArgumentRegistrationException e) {
				throw new UnrecoverableError(e);
			}
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			final Iterable<Vertex> vertices = this.graph.getVertices();
			for (final Vertex vertex : vertices) {
				if (Logger.logAlways()) {
					Logger.always("Person: " + vertex.getId());
				}
				for (final String key : vertex.getPropertyKeys()) {
					if (Logger.logAlways()) {
						Logger.always(String.format("%s: %s", key, vertex.getProperty(key)));
					}
				}
				
				for (final Edge edge : vertex.getEdges(Direction.BOTH)) {
					final Vertex vertex1 = edge.getVertex(Direction.IN);
					final Vertex vertex2 = edge.getVertex(Direction.OUT);
					
					if (Logger.logAlways()) {
						Logger.always(String.format("%s <-> %s", vertex1.getId(), vertex2.getId()));
						Logger.always(String.format("%s: %s", edge.getLabel(), edge.getProperty("confidence")));
					}
				}
			}
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
}
