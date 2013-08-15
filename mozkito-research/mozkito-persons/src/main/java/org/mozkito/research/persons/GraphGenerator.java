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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import org.mozkito.graphs.GraphManager;
import org.mozkito.graphs.settings.GraphOptions;
import org.mozkito.persistence.Criteria;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.persons.model.Person;
import org.mozkito.research.persons.engines.Engine;
import org.mozkito.settings.DatabaseOptions;
import org.mozkito.utilities.loading.classpath.ClassFinder;
import org.mozkito.utilities.loading.classpath.exceptions.WrongClassSearchMethodException;

/**
 * The Class GraphGenerator.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class GraphGenerator implements Runnable {
	
	/**
	 * The Class Monitor.
	 */
	public static final class Monitor extends Thread {
		
		/** The generator. */
		private GraphGenerator generator;
		
		/**
		 * Instantiates a new monitor.
		 * 
		 * @param generator
		 *            the generator
		 */
		public Monitor(final GraphGenerator generator) {
			PRECONDITIONS: {
				// none
			}
			
			try {
				// body
				this.generator = generator;
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
		
		/**
		 * {@inheritDoc}
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			final DateTime start = new DateTime();
			
			while (true) {
				try {
					Thread.sleep(10 * 60 * 1000);
				} catch (final InterruptedException ignore) {
					// ignore
				}
				
				final double percentage = (this.generator.getProgress() * 100) / this.generator.getEntries();
				if (Logger.logAlways()) {
					Logger.always("Current progress is '%s%%'. Running since %s.", percentage,
					              start.toString(DateTimeFormat.fullDateTime()));
				}
			}
		};
	}
	
	/** The Constant USERNAMES_KEY. */
	private static final String USERNAMES_KEY = "usernames";
	
	/** The Constant FULLNAMES_KEY. */
	private static final String FULLNAMES_KEY = "fullnames";
	
	/** The Constant EMAILS_KEY. */
	private static final String EMAILS_KEY    = "emails";
	
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
			try {
				final Thread thread = new Thread(new GraphGenerator());
				thread.run();
			} catch (final Shutdown s) {
				if (Logger.logAlways()) {
					Logger.always("Shutting down.");
				}
			}
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/** The graph options. */
	private GraphOptions      graphOptions    = null;
	
	/** The engines. */
	private final Set<Engine> engines         = new HashSet<>();
	
	/** The graph manager. */
	private GraphManager      graphManager    = null;
	
	/** The graph. */
	private KeyIndexableGraph graph           = null;
	
	/** The database options. */
	private DatabaseOptions   databaseOptions = null;
	
	/** The persistence util. */
	private PersistenceUtil   persistenceUtil = null;
	
	/** The edge counter. */
	private long              edgeCounter     = 0;
	
	/** The entries. */
	private long              entries         = 0;
	
	/** The progress. */
	private long              progress        = 0;
	
	/**
	 * Instantiates a new graph generator.
	 */
	public GraphGenerator() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// body
			try {
				final Settings settings = new Settings();
				this.graphOptions = new GraphOptions(settings.getRoot(), Requirement.required);
				this.databaseOptions = new DatabaseOptions(settings.getRoot(), Requirement.required, "persons");
				final ArgumentSet<GraphManager, GraphOptions> graphArgument = ArgumentSetFactory.create(this.graphOptions);
				final ArgumentSet<PersistenceUtil, DatabaseOptions> databaseArgument = ArgumentSetFactory.create(this.databaseOptions);
				
				if (settings.helpRequested()) {
					if (Logger.logAlways()) {
						Logger.always(settings.getHelpString());
					}
					throw new Shutdown("help requested");
				}
				
				final String path = settings.getProperty("graph.titandb.directory");
				if (path != null) {
					final File directory = new File(path);
					if (directory.exists() && directory.isDirectory()) {
						try {
							FileUtils.deleteDirectory(directory);
						} catch (final IOException e) {
							throw new UnrecoverableError(e);
						}
					}
				}
				
				this.graphManager = graphArgument.getValue();
				this.graph = this.graphManager.getGraph();
				
				this.persistenceUtil = databaseArgument.getValue();
				
				Collection<Class<? extends Engine>> classes = null;
				try {
					classes = ClassFinder.getClassesExtendingClass(Engine.class.getPackage(), Engine.class,
					                                               Modifier.ABSTRACT | Modifier.INTERFACE
					                                                       | Modifier.PRIVATE);
				} catch (ClassNotFoundException | WrongClassSearchMethodException | IOException e1) {
					throw new UnrecoverableError(e1);
				}
				
				for (final Class<? extends Engine> clazz : classes) {
					try {
						final Engine engine = clazz.newInstance();
						this.engines.add(engine);
					} catch (InstantiationException | IllegalAccessException e) {
						if (Logger.logError()) {
							Logger.error(e);
						}
					}
				}
			} catch (final SettingsParseError | ArgumentSetRegistrationException | ArgumentRegistrationException e) {
				throw new UnrecoverableError(e);
			}
		} finally {
			POSTCONDITIONS: {
				// none
				assert this.graphManager != null;
			}
		}
	}
	
	/**
	 * Gets the edge counter.
	 * 
	 * @return the edgeCounter
	 */
	public final long getEdgeCounter() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.edgeCounter;
		} finally {
			POSTCONDITIONS: {
				Condition.notNull(this.edgeCounter, "Field '%s' in '%s'.", "edgeCounter", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	/**
	 * Gets the entries.
	 * 
	 * @return the entries
	 */
	public final long getEntries() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.entries;
		} finally {
			POSTCONDITIONS: {
				Condition.notNull(this.entries, "Field '%s' in '%s'.", "entries", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	/**
	 * Gets the progress.
	 * 
	 * @return the progress
	 */
	public final long getProgress() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.progress;
		} finally {
			POSTCONDITIONS: {
				Condition.notNull(this.progress, "Field '%s' in '%s'.", "progress", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	/**
	 * Hash code.
	 * 
	 * @param p1
	 *            the p1
	 * @param p2
	 *            the p2
	 * @param engine
	 *            the engine
	 * @return the int
	 */
	public Integer hashCode(final Person p1,
	                        final Person p2,
	                        final Engine engine) {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((p1 == null)
		                                         ? 0
		                                         : p1.hashCode());
		result = (prime * result) + ((p2 == null)
		                                         ? 0
		                                         : p2.hashCode());
		result = (prime * result) + ((engine == null)
		                                             ? 0
		                                             : engine.getName().hashCode());
		return result;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		PRECONDITIONS: {
			assert this.persistenceUtil != null;
			assert this.graph != null;
			assert this.engines != null;
			assert !this.engines.isEmpty();
			assert this.edgeCounter == 0;
			assert this.progress == 0;
		}
		
		try {
			final Criteria<Person> criteria = this.persistenceUtil.createCriteria(Person.class);
			
			if (Logger.logInfo()) {
				Logger.info("Loading person entities from database.");
			}
			final List<Person> list = this.persistenceUtil.load(criteria);
			
			if (Logger.logInfo()) {
				Logger.info("Fetching persons from database into array.");
			}
			final ArrayList<Person> persons = new ArrayList<>(list);
			
			if (Logger.logInfo()) {
				Logger.info("Done.");
			}
			
			this.entries = persons.size();
			int logBarrier = 10;
			if (Logger.logInfo()) {
				Logger.info("Performing confidence matching on %s entries.", this.entries);
			}
			
			final Monitor monitor = new Monitor(this);
			monitor.setDaemon(true);
			monitor.run();
			
			int i = 0;
			for (final Person p1 : persons) {
				if (Logger.logInfo()) {
					Logger.info("Creating new vertex '%s'.", p1);
				}
				
				final Vertex vertex = this.graph.addVertex(p1.getGeneratedId());
				vertex.setProperty(USERNAMES_KEY, p1.getUsernames());
				vertex.setProperty(EMAILS_KEY, p1.getEmailAddresses());
				vertex.setProperty(FULLNAMES_KEY, p1.getFullnames());
				
				if (Logger.logInfo()) {
					Logger.info("Vertex created.");
				}
				
				for (int j = i + 1; j < persons.size(); ++j) {
					final Person p2 = persons.get(j);
					
					if (Logger.logTrace()) {
						Logger.trace("Checking against person " + p2);
					}
					
					if (!p1.equals(p2)) {
						for (final Engine engine : this.engines) {
							if (Logger.logTrace()) {
								Logger.trace("Using engine '%s'", engine.getName());
							}
							final Double confidence = engine.confidence(p1, p2);
							
							if (confidence > 0d) {
								++this.edgeCounter;
								if (Logger.logInfo()) {
									Logger.info("Adding new edge (%s)", ++this.edgeCounter);
								}
								
								final Vertex vertex2 = this.graph.getVertex(p2.getGeneratedId());
								final Edge edge = this.graph.addEdge(hashCode(p1, p2, engine), vertex, vertex2,
								                                     engine.getName());
								edge.setProperty("confidence", confidence);
							}
						}
					}
				}
				
				++this.progress;
				final double percentage = (this.progress * 100) / this.entries;
				if (percentage >= logBarrier) {
					logBarrier += 10;
					if (Logger.logInfo()) {
						Logger.info("%s%% done", percentage);
					}
				}
				
				++i;
			}
			
			if (Logger.logInfo()) {
				Logger.info("Created %s edges.", this.edgeCounter);
			}
			
			if (Logger.logInfo()) {
				Logger.info("Shutting down graph database.", this.edgeCounter);
			}
			this.graph.shutdown();
			
			if (Logger.logInfo()) {
				Logger.info("Shutting down persistence util.", this.edgeCounter);
			}
			this.persistenceUtil.shutdown();
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
}
