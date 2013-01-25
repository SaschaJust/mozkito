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

package org.mozkito.genealogies;

import java.util.Iterator;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.model.Chain;
import net.ownhero.dev.andama.model.Pool;
import net.ownhero.dev.hiari.settings.ArgumentFactory;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetFactory;
import net.ownhero.dev.hiari.settings.BooleanArgument;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.joda.time.Days;

import org.mozkito.genealogies.ChangeOperationReader.Options;
import org.mozkito.genealogies.core.CoreChangeGenealogy;
import org.mozkito.genealogies.core.TransactionChangeGenealogy;
import org.mozkito.genealogies.settings.GenealogyOptions;
import org.mozkito.settings.DatabaseOptions;
import org.mozkito.versions.model.ChangeSet;

/**
 * The Class GenealogyToolChain.
 */
public class GenealogyToolChain extends Chain<Settings> {
	
	/** The thread pool. */
	private final Pool                                               threadPool;
	
	/** The genealogy args. */
	private final ArgumentSet<CoreChangeGenealogy, GenealogyOptions> genealogyArgs;
	
	/** The genealogy. */
	private CoreChangeGenealogy                                      genealogy;
	
	/** The info argument. */
	private final BooleanArgument                                    infoArgument;
	
	/** The change op reader options. */
	private Options                                                  changeOpReaderOptions;
	
	/**
	 * Instantiates a new genealogy tool chain.
	 * 
	 * @param settings
	 *            the settings
	 */
	public GenealogyToolChain(final Settings settings) {
		super(settings);
		
		this.threadPool = new Pool(GenealogyToolChain.class.getSimpleName(), this);
		try {
			
			this.infoArgument = ArgumentFactory.create(new BooleanArgument.Options(
			                                                                       settings.getRoot(),
			                                                                       "infoOnly",
			                                                                       "Only prints standard genealogy infos",
			                                                                       false, Requirement.required));
			
			ArgumentFactory.create(new BooleanArgument.Options(
			                                                   settings.getRoot(),
			                                                   "ignoreTests",
			                                                   "Set to FALSE if you want to include test cases into the genealogy graph.",
			                                                   true, Requirement.required));
			
			final DatabaseOptions databaseOptions = new DatabaseOptions(settings.getRoot(), Requirement.required,
			                                                            "codeanalysis");
			ArgumentSetFactory.create(databaseOptions);
			final GenealogyOptions genealogyOptions = new GenealogyOptions(settings.getRoot(), Requirement.required,
			                                                               databaseOptions);
			this.genealogyArgs = ArgumentSetFactory.create(genealogyOptions);
			this.changeOpReaderOptions = new ChangeOperationReader.Options(settings.getRoot(), Requirement.required,
			                                                               databaseOptions);
			ArgumentSetFactory.create(this.changeOpReaderOptions);
			
			if (getSettings().helpRequested()) {
				if (Logger.logAlways()) {
					Logger.always(getSettings().getHelpString());
				}
				throw new Shutdown();
			}
			
		} catch (final ArgumentRegistrationException | SettingsParseError | ArgumentSetRegistrationException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			throw new Shutdown();
		} finally {
			// POSTCONDITION
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.model.Chain#setup()
	 */
	@Override
	public void setup() {
		if (this.infoArgument.getValue()) {
			
			this.genealogy = this.genealogyArgs.getValue();
			final TransactionChangeGenealogy transactionLayer = this.genealogy.getChangeSetLayer();
			if (Logger.logInfo()) {
				Logger.info("Statistic on change genealogy graph:");
				Logger.info("Number of vertices: " + this.genealogy.vertexSize());
				Logger.info("Number of edges: " + this.genealogy.edgeSize());
				
				Logger.info("Statistic on change genealogy transaction layer:");
				Logger.info("Number of vertices: " + transactionLayer.vertexSize());
				Logger.info("Number of edges: " + transactionLayer.edgeSize());
				
				final Iterator<ChangeSet> vertexIterator = transactionLayer.vertexIterator();
				final DescriptiveStatistics youngestGapStat = new DescriptiveStatistics();
				while (vertexIterator.hasNext()) {
					final ChangeSet t = vertexIterator.next();
					int dayGap = Integer.MAX_VALUE;
					for (final ChangeSet c : transactionLayer.getAllDependants(t)) {
						final int gap = Math.abs(Days.daysBetween(t.getTimestamp(), c.getTimestamp()).getDays());
						if (gap < dayGap) {
							dayGap = gap;
						}
					}
					youngestGapStat.addValue(dayGap);
				}
				Logger.info("Median number of days between vertex and youngest child (Median youngest child gap): "
				        + youngestGapStat.getPercentile(50));
			}
		} else {
			this.genealogy = this.genealogyArgs.getValue();
			
			final ChangeOperationReader changeOpReader = getSettings().getArgumentSet(this.changeOpReaderOptions)
			                                                          .getValue();
			new GenealogySource(this.threadPool.getThreadGroup(), getSettings(), changeOpReader);
			new GenealogyNodePersister(this.threadPool.getThreadGroup(), getSettings(), this.genealogy);
			new GenealogyDependencyPersister(this.threadPool.getThreadGroup(), getSettings(), this.genealogy);
		}
	}
}
