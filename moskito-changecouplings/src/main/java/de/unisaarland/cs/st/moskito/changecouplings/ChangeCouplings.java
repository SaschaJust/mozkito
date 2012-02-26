/**
 * ***************************************************************************** Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ***************************************************************************** 
 */
package de.unisaarland.cs.st.moskito.changecouplings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.hiari.settings.arguments.DoubleArgument;
import net.ownhero.dev.hiari.settings.arguments.EnumArgument;
import net.ownhero.dev.hiari.settings.arguments.LongArgument;
import net.ownhero.dev.hiari.settings.arguments.OutputFileArgument;
import net.ownhero.dev.hiari.settings.arguments.StringArgument;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.changecouplings.elements.Level;
import de.unisaarland.cs.st.moskito.changecouplings.model.FileChangeCoupling;
import de.unisaarland.cs.st.moskito.changecouplings.model.SerialFileChangeCoupling;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.moskito.settings.DatabaseArguments;
import de.unisaarland.cs.st.moskito.settings.RepositorySettings;

/**
 * The Class ChangeCouplings.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class ChangeCouplings {
	
	private final DatabaseArguments   databaseArgs;
	private final EnumArgument<Level> levelArgument;
	private final StringArgument      transactionArg;
	private final DoubleArgument      minConfArg;
	private final LongArgument        minSupportArg;
	private PersistenceUtil           persistenceUtil;
	private Long                      minSupport;
	private Double                    minConf;
	private final OutputFileArgument  outputFileArgument;
	private final RepositorySettings  settings;
	
	public ChangeCouplings() {
		this.settings = new RepositorySettings();
		
		try {
			this.databaseArgs = this.settings.setDatabaseArgs(Requirement.required, "untangling");
			
			this.levelArgument = new EnumArgument<Level>(this.settings.getRootArgumentSet(), "changecouplings.level",
			                                             "The level to compute change couplings on.", Level.FILE,
			                                             Requirement.required);
			
			this.transactionArg = new StringArgument(this.settings.getRootArgumentSet(), "changecouplings.transaction",
			                                         "The transaction id to compute change couplings for.", null,
			                                         Requirement.required);
			
			this.minConfArg = new DoubleArgument(
			                                     this.settings.getRootArgumentSet(),
			                                     "changecouplings.minConfidence",
			                                     "Only compute change couplings exceeding the minimal confidence of this value.",
			                                     "0.1", Requirement.required);
			
			this.minSupportArg = new LongArgument(
			                                      this.settings.getRootArgumentSet(),
			                                      "changecouplings.minSupport",
			                                      "Only compute change couplings that exceed a minimal support of this value.",
			                                      "3", Requirement.required);
			
			this.outputFileArgument = new OutputFileArgument(this.settings.getRootArgumentSet(), "changecouplings.out",
			                                                 "Write the serialized change couplings to this file.",
			                                                 null, Requirement.required, true);
		} catch (final net.ownhero.dev.hiari.settings.registerable.ArgumentRegistrationException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new Shutdown(e.getLocalizedMessage(), e);
		}
	}
	
	public void run() {
		final RCSTransaction transaction = this.persistenceUtil.loadById(this.transactionArg.getValue(),
		                                                                 RCSTransaction.class);
		
		transaction.getParents();
		
		if (this.levelArgument.getValue().equals("FILE")) {
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
			
			final File serialFile = this.outputFileArgument.getValue();
			
			try {
				final ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(serialFile));
				out.writeObject(couplings);
				out.close();
			} catch (final FileNotFoundException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
			} catch (final IOException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
			}
			
			if (Logger.logInfo()) {
				Logger.info("done");
			}
			
		} else {
			throw new UnrecoverableError("Not yet implemented!");
		}
	}
	
	public void setup() {
		
		try {
			this.settings.parse();
		} catch (final SettingsParseError e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new Shutdown(e.getLocalizedMessage(), e);
		}
		final PersistenceUtil persistenceUtil = this.databaseArgs.getValue();
		if (persistenceUtil == null) {
			throw new UnrecoverableError("Could not connect to database");
		}
		
		this.minConf = this.minConfArg.getValue();
		if ((this.minConf < 0.1) || (this.minConf > 1)) {
			throw new UnrecoverableError("The minimal confidence value must be between [0.1,1]");
		}
		
		this.minSupport = this.minSupportArg.getValue();
		if (this.minSupport < 1) {
			throw new UnrecoverableError("The minimal support value must be larger or equal than one.");
		}
	}
}
