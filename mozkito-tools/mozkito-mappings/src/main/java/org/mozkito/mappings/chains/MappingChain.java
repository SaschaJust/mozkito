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
package org.mozkito.mappings.chains;

import java.util.List;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.messages.ErrorEvent;
import net.ownhero.dev.andama.messages.StartupEvent;
import net.ownhero.dev.andama.model.Chain;
import net.ownhero.dev.andama.model.Pool;
import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.INode;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetFactory;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.graphs.GraphManager;
import org.mozkito.graphs.settings.GraphOptions;
import org.mozkito.mappings.chains.converters.CandidatesConverter;
import org.mozkito.mappings.chains.converters.CompositeConverter;
import org.mozkito.mappings.chains.converters.RelationsConverter;
import org.mozkito.mappings.chains.demultiplexers.CandidatesDemux;
import org.mozkito.mappings.chains.filters.EngineProcessor;
import org.mozkito.mappings.chains.filters.FilterProcessor;
import org.mozkito.mappings.chains.filters.StrategyProcessor;
import org.mozkito.mappings.chains.sinks.Graphify;
import org.mozkito.mappings.chains.sinks.Persister;
import org.mozkito.mappings.chains.sources.ReportReader;
import org.mozkito.mappings.chains.sources.TransactionReader;
import org.mozkito.mappings.chains.transformers.ReportFinder;
import org.mozkito.mappings.chains.transformers.TransactionFinder;
import org.mozkito.mappings.engines.Engine;
import org.mozkito.mappings.filters.Filter;
import org.mozkito.mappings.finder.Finder;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Relation;
import org.mozkito.mappings.settings.MappingOptions;
import org.mozkito.mappings.strategies.Strategy;
import org.mozkito.mappings.utils.graph.MappingsGraph;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.settings.DatabaseOptions;
import org.mozkito.utilities.commons.JavaUtils;

/**
 * The Class MappingChain.
 */
public class MappingChain extends Chain<Settings> {
	
	/** The database arguments. */
	private ArgumentSet<PersistenceUtil, DatabaseOptions> databaseArguments;
	
	/** The database options. */
	private DatabaseOptions                               databaseOptions;
	
	/** The mapping arguments. */
	private ArgumentSet<Finder, MappingOptions>           mappingArguments;
	
	/** The mapping options. */
	private MappingOptions                                mappingOptions;
	/** The thread pool. */
	private final Pool                                    threadPool;
	
	/** The graph options. */
	private GraphOptions                                  graphOptions;
	
	/** The graph arguments. */
	private ArgumentSet<GraphManager, GraphOptions>       graphArguments;
	
	/**
	 * Instantiates a new mapping chain.
	 * 
	 * @param settings
	 *            the settings
	 */
	public MappingChain(final Settings settings) {
		super(settings, "mappings"); //$NON-NLS-1$
		this.threadPool = new Pool(Relation.class.getSimpleName(), this);
		
		try {
			this.databaseOptions = new DatabaseOptions(getSettings().getRoot(), Requirement.required, getName());
			this.graphOptions = new GraphOptions(getSettings().getRoot(), Requirement.required);
			this.graphArguments = ArgumentSetFactory.create(this.graphOptions);
			this.databaseArguments = ArgumentSetFactory.create(this.databaseOptions);
			this.mappingOptions = new MappingOptions(getSettings().getRoot(), Requirement.required,
			                                         this.databaseOptions, this.graphOptions);
			this.mappingArguments = ArgumentSetFactory.create(this.mappingOptions);
		} catch (final ArgumentRegistrationException e) {
			throw new Shutdown(e.getMessage(), e);
		} catch (final ArgumentSetRegistrationException e) {
			throw new Shutdown(e.getMessage(), e);
		} catch (final SettingsParseError e) {
			throw new Shutdown(e.getMessage(), e);
		}
		
		Condition.notNull(this.threadPool, "Field '%s' in '%s'.", "threadPool", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * Gets the simple name of the class.
	 * 
	 * @return the simple name of the class.
	 */
	public String getClassName() {
		return JavaUtils.getHandle(this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.model.Chain#setup()
	 */
	/**
	 * {@inheritDoc}
	 * 
	 * @see net.ownhero.dev.andama.model.Chain#setup()
	 */
	@Override
	public void setup() {
		// PRECONDITIONS
		Condition.notNull(this.databaseArguments, "Field '%s' in '%s'.", "databaseArguments", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		Condition.notNull(this.mappingArguments, "Field '%s' in '%s'.", "mappingArguments", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		Logger.updateClassLevels();
		try {
			final Finder finder = this.mappingArguments.getValue();
			
			if (finder == null) {
				getEventBus().fireEvent(new ErrorEvent(Messages.getString("MappingChain.finderInit"))); //$NON-NLS-1$
				if (Logger.logAlways()) {
					Logger.always(getSettings().getHelpString());
				}
				
				shutdown();
				return;
			}
			
			Condition.notNull(finder, "Local variable '%s' in '%s'.", "finder", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			
			final PersistenceUtil persistenceUtil = this.databaseArguments.getValue();
			
			if (persistenceUtil == null) {
				getEventBus().fireEvent(new ErrorEvent(Messages.getString("MappingChain.dbInit"))); //$NON-NLS-1$
				if (Logger.logError()) {
					Logger.error(getSettings().getHelpString());
				}
				
				shutdown();
				return;
			}
			
			Condition.notNull(persistenceUtil,
			                  "Local variable '%s' in '%s:'.", "persistenceUtil", getClassName(), "setup()"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			
			finder.loadData();
			final Group group = this.threadPool.getThreadGroup();
			
			// load sources
			new ReportReader(group, getSettings(), persistenceUtil);
			new TransactionReader(group, getSettings(), persistenceUtil);
			
			// load transformers that produce candidates
			new TransactionFinder(group, getSettings(), finder, persistenceUtil);
			new ReportFinder(group, getSettings(), finder, persistenceUtil);
			
			// demultiplex the candidates into one stream
			new CandidatesDemux(group, getSettings());
			
			// convert the candidates into Relations
			new CandidatesConverter(group, getSettings());
			
			// create the filter nodes for the engines
			for (final Engine engine : finder.getEngines().values()) {
				if (Logger.logInfo()) {
					Logger.info(Messages.getString("MappingChain.nodeCreate", engine)); //$NON-NLS-1$
				}
				new EngineProcessor(group, getSettings(), finder, engine);
			}
			
			// convert the Relations into Composites
			new RelationsConverter(group, getSettings());
			
			// create the filter nodes for the strategies
			for (final Strategy strategy : finder.getStrategies().values()) {
				if (Logger.logInfo()) {
					Logger.info(Messages.getString("MappingChain.nodeCreate", strategy)); //$NON-NLS-1$
				}
				new StrategyProcessor(group, getSettings(), finder, strategy);
			}
			
			// convert the Composites into Mappings
			new CompositeConverter(group, getSettings());
			
			// create the filter nodes for the filters
			for (final Filter filter : finder.getFilters().values()) {
				if (Logger.logInfo()) {
					Logger.info(Messages.getString("MappingChain.nodeCreate", filter)); //$NON-NLS-1$
				}
				new FilterProcessor(group, getSettings(), finder, filter);
			}
			
			// save the results in the database
			new Persister(group, getSettings(), persistenceUtil);
			// save the results in the graph
			new Graphify(group, getSettings(), new MappingsGraph(this.graphArguments.getValue()));
			
			final List<INode<?, ?>> nodes = group.getThreads();
			
			for (final INode<?, ?> node : nodes) {
				if (Logger.logDebug()) {
					Logger.debug(node.toString());
				}
			}
			
			// final IRCThread t = new IRCThread("mapping");
			// t.start();
			//
			getEventBus().fireEvent(new StartupEvent(Messages.getString("MappingChain.started", getName()))); //$NON-NLS-1$
		} finally {
			// POSTCONDITIONS
		}
	}
}
