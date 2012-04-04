/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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
 *******************************************************************************/
package de.unisaarland.cs.st.moskito.changecouplings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

import net.ownhero.dev.hiari.settings.ArgumentFactory;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetFactory;
import net.ownhero.dev.hiari.settings.DoubleArgument;
import net.ownhero.dev.hiari.settings.EnumArgument;
import net.ownhero.dev.hiari.settings.LongArgument;
import net.ownhero.dev.hiari.settings.OutputFileArgument;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.StringArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.changecouplings.elements.Level;
import de.unisaarland.cs.st.moskito.changecouplings.model.FileChangeCoupling;
import de.unisaarland.cs.st.moskito.changecouplings.model.SerialFileChangeCoupling;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.moskito.settings.DatabaseOptions;

/**
 * The Class ChangeCouplings.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class ChangeCouplings {
	
	/** The persistence util. */
	private PersistenceUtil                               persistenceUtil;
	
	/** The min support. */
	private Long                                          minSupport;
	
	/** The min conf. */
	private Double                                        minConf;
	
	/** The granularity argument. */
	private EnumArgument<Level>                           granularityArgument;
	
	/** The transaction id argument. */
	private StringArgument                                transactionIdArgument;
	
	/** The min confidence argument. */
	private DoubleArgument                                minConfidenceArgument;
	
	/** The min support argument. */
	private LongArgument                                  minSupportArgument;
	
	/** The out argument. */
	private OutputFileArgument                            outArgument;
	
	/** The database arguments. */
	private ArgumentSet<PersistenceUtil, DatabaseOptions> databaseArguments;
	
	/**
	 * Instantiates a new change couplings.
	 *
	 * @param settings the settings
	 */
	public ChangeCouplings(final Settings settings) {
		try {
			
			final DatabaseOptions databaseOptions = new DatabaseOptions(settings.getRoot(), Requirement.required,
			                                                            "untangling");
			this.databaseArguments = ArgumentSetFactory.create(databaseOptions);
			
			this.granularityArgument = ArgumentFactory.create(new EnumArgument.Options<Level>(
			                                                                                  settings.getRoot(),
			                                                                                  "granularity",
			                                                                                  "The level to compute change couplings on.",
			                                                                                  Level.FILE,
			                                                                                  Requirement.required));
			
			this.transactionIdArgument = ArgumentFactory.create(new StringArgument.Options(
			                                                                               settings.getRoot(),
			                                                                               "transaction",
			                                                                               "The transaction id to compute change couplings for.",
			                                                                               null, Requirement.required));
			
			this.minConfidenceArgument = ArgumentFactory.create(new DoubleArgument.Options(
			                                                                               settings.getRoot(),
			                                                                               "minConfidence",
			                                                                               "Only compute change couplings exceeding the minimal confidence of this value.",
			                                                                               null, Requirement.required));
			
			this.minSupportArgument = ArgumentFactory.create(new LongArgument.Options(
			                                                                          settings.getRoot(),
			                                                                          "minSupport",
			                                                                          "Only compute change couplings that exceed a minimal support of this value.",
			                                                                          3l, Requirement.required));
			
			this.outArgument = ArgumentFactory.create(new OutputFileArgument.Options(
			                                                                         settings.getRoot(),
			                                                                         "out",
			                                                                         "Write the serialized change couplings to this file.",
			                                                                         null, Requirement.required, true));
			
		} catch (final SettingsParseError e) {
			throw new UnrecoverableError(e);
		} catch (final ArgumentSetRegistrationException e) {
			throw new UnrecoverableError(e);
		} catch (final ArgumentRegistrationException e) {
			throw new UnrecoverableError(e);
		} finally {
			
		}
	}
	
	/**
	 * Run.
	 */
	public void run() {
		final RCSTransaction transaction = this.persistenceUtil.loadById(this.transactionIdArgument.getValue(),
		                                                                 RCSTransaction.class);
		
		if (this.granularityArgument.getValue().equals(Level.FILE)) {
			final LinkedList<FileChangeCoupling> fileChangeCouplings = ChangeCouplingRuleFactory.getFileChangeCouplings(transaction,
			                                                                                                            this.minSupport.intValue(),
			                                                                                                            this.minConf.doubleValue(),
			                                                                                                            this.persistenceUtil);
			
			final LinkedList<SerialFileChangeCoupling> couplings = new LinkedList<SerialFileChangeCoupling>();
			for (final FileChangeCoupling c : fileChangeCouplings) {
				couplings.add(c.serialize(transaction));
			}
			
			if (Logger.logInfo()) {
				Logger.info("Serializing " + couplings.size() + " file change couplings ... ");
			}
			
			final File serialFile = this.outArgument.getValue();
			
			try {
				final ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(serialFile));
				out.writeObject(couplings);
				out.close();
			} catch (final FileNotFoundException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
			} catch (final IOException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
			}
			
			if (Logger.logInfo()) {
				Logger.info("done");
			}
			
		} else {
			throw new UnrecoverableError("Not yet implemented!");
		}
	}
	
	/**
	 * Setup.
	 */
	public void setup() {
		
		final PersistenceUtil persistenceUtil = this.databaseArguments.getValue();
		if (persistenceUtil == null) {
			throw new UnrecoverableError("Could not connect to database");
		}
		
		this.minConf = this.minConfidenceArgument.getValue();
		if ((this.minConf < 0.1) || (this.minConf > 1)) {
			throw new UnrecoverableError("The minimal confidence value must be between [0.1,1]");
		}
		
		this.minSupport = this.minSupportArgument.getValue();
		if (this.minSupport < 1) {
			throw new UnrecoverableError("The minimal support value must be larger or equal than one.");
		}
	}
}
