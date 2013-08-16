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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
import org.mozkito.research.persons.engines.GravatarEngine;

/**
 * The Class GraphAnalyzer.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class GraphAnalyzer implements Runnable {
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(final String[] args) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			final Thread t = new Thread(new GraphAnalyzer());
			t.start();
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/** The graph manager. */
	private GraphManager      graphManager;
	
	/** The graph. */
	private KeyIndexableGraph graph;
	
	/** The graph options. */
	private GraphOptions      graphOptions;
	
	/**
	 * Instantiates a new graph analyzer.
	 */
	public GraphAnalyzer() {
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
			new GravatarEngine();
			final Iterable<Vertex> vertices = this.graph.getVertices();
			
			long numPersons = 0;
			long numEmailExists = 0;
			long maxEmails = 0;
			long numTotalEmails = 0;
			long numTotalGravatars = 0;
			long numDifferentGravatars = 0;
			
			final Set<String> emailSet = new HashSet<>();
			final List<Integer> gravatarCodes = new LinkedList<>();
			final Map<String, Integer> gravatarMap = new HashMap<>();
			
			for (final Vertex vertex : vertices) {
				++numPersons;
				
				final List<String> emails = vertex.getProperty(GraphGenerator.EMAILS_KEY);
				
				SANITY: {
					assert emails != null;
				}
				
				if (!emails.isEmpty()) {
					++numEmailExists;
					
					final int eSize = emails.size();
					if (eSize > maxEmails) {
						maxEmails = eSize;
					}
					
					emailSet.addAll(emails);
				}
				
				final Map<String, Integer> gCodes = vertex.getProperty(GraphGenerator.GRAVATARS_KEY);
				if (gCodes != null) {
					gravatarMap.putAll(gCodes);
					gravatarCodes.addAll(gCodes.values());
				}
				
				// for (final String key : vertex.getPropertyKeys()) {
				// if (Logger.logAlways()) {
				// Logger.always(String.format("%s: %s", key, vertex.getProperty(key)));
				// }
				// }
				
				// for (final Edge edge : vertex.getEdges(Direction.BOTH)) {
				// if (gEngine.getName().equals(edge.getLabel())) {
				//
				// }
				// final Vertex vertex1 = edge.getVertex(Direction.IN);
				// final Vertex vertex2 = edge.getVertex(Direction.OUT);
				//
				// if (Logger.logAlways()) {
				// Logger.always(String.format("%s <-> %s", vertex1.getId(), vertex2.getId()));
				// Logger.always(String.format("%s: %s", edge.getLabel(), edge.getProperty("confidence")));
				// }
				// }
			}
			
			numTotalEmails = emailSet.size();
			numTotalGravatars = gravatarCodes.size();
			numDifferentGravatars = new TreeSet<>(gravatarCodes).size();
			
			// number of person entities
			System.out.println(String.format("Number of person entities: %s", numPersons));
			
			// number persons having at least one email associated
			System.out.println(String.format("Number of persons having at least one email associated: %s",
			                                 numEmailExists));
			
			// max number of emails per person
			System.out.println(String.format("Maximum number of emails per person: %s", maxEmails));
			
			// avg numner of emails per person
			System.out.println(String.format("Average number of emails per person: %s", numTotalEmails / numPersons));
			
			// number of gravatars retrieved
			System.out.println(String.format("Total number of gravatars: %s", numTotalGravatars));
			
			// number of different gravatars
			System.out.println(String.format("Number of different gravatars: %s", numDifferentGravatars));
			
			// total number emails referring to gravatars
			System.out.println(String.format("Total number of different emails referring to a gravatar: %s",
			                                 gravatarMap.size()));
			
			System.out.println(String.format("Percentage of emails referring to a gravatar: %s", gravatarMap.size()
			        / numTotalEmails));
			
			// % of different emails that refer to the same gravatar
			final Set<Integer> set = new TreeSet<>(gravatarMap.values());
			System.out.println(String.format("Percentage of different emails referring to the same gravatar: %s",
			                                 set.size() / gravatarMap.keySet().size()));
			
			// % of emails those gravatar displays the same face
			
			// number of emails where the prefix is a person's username
			
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
}
