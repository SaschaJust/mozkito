/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
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
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.changecouplings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

import net.ownhero.dev.andama.settings.DoubleArgument;
import net.ownhero.dev.andama.settings.EnumArgument;
import net.ownhero.dev.andama.settings.LongArgument;
import net.ownhero.dev.andama.settings.OutputFileArgument;
import net.ownhero.dev.andama.settings.StringArgument;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.changecouplings.model.FileChangeCoupling;
import de.unisaarland.cs.st.reposuite.changecouplings.model.SerialFileChangeCoupling;
import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceManager;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.settings.DatabaseArguments;
import de.unisaarland.cs.st.reposuite.settings.RepositorySettings;

/**
 * The Class ChangeCouplings.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class ChangeCouplings {
	
	private final DatabaseArguments  databaseArgs;
	private final EnumArgument       levelArgument;
	private final StringArgument     transactionArg;
	private final DoubleArgument     minConfArg;
	private final LongArgument       minSupportArg;
	private PersistenceUtil          persistenceUtil;
	private Long                     minSupport;
	private Double                   minConf;
	private final OutputFileArgument outputFileArgument;
	
	public ChangeCouplings() {
		RepositorySettings settings = new RepositorySettings();
		
		this.databaseArgs = settings.setDatabaseArgs(true, "untangling");
		
		this.levelArgument = new EnumArgument(settings, "changecouplings.level",
		                                      "The level to compute change couplings on.", "FILE", true, new String[] {
		                                              "FILE", "METHOD" });
		
		this.transactionArg = new StringArgument(settings, "changecouplings.transaction",
		                                         "The transaction id to compute change couplings for.", null, true);
		
		this.minConfArg = new DoubleArgument(
		                                     settings,
		                                     "changecouplings.minConfidence",
		                                     "Only compute change couplings exceeding the minimal confidence of this value.",
		                                     "0.1", true);
		
		this.minSupportArg = new LongArgument(
		                                      settings,
		                                      "changecouplings.minSupport",
		                                      "Only compute change couplings that exceed a minimal support of this value.",
		                                      "3", true);
		
		this.outputFileArgument = new OutputFileArgument(settings, "changecouplings.out",
		                                                 "Write the serialized change couplings to this file.", null,
		                                                 true, true);
		
		settings.parseArguments();
	}
	
	public void run() {
		RCSTransaction transaction = this.persistenceUtil.loadById(this.transactionArg.getValue(), RCSTransaction.class);
		
		transaction.getParents();
		
		if (this.levelArgument.getValue().equals("FILE")) {
			LinkedList<FileChangeCoupling> fileChangeCouplings = ChangeCouplingRuleFactory.getFileChangeCouplings(transaction,
			                                                                                                      this.minSupport.intValue(),
			                                                                                                      this.minConf.doubleValue(),
			                                                                                                      this.persistenceUtil);
			
			LinkedList<SerialFileChangeCoupling> couplings = new LinkedList<SerialFileChangeCoupling>();
			for (FileChangeCoupling c : fileChangeCouplings) {
				couplings.add(c.serialize(transaction));
			}
			
			if (Logger.logInfo()) {
				Logger.info("Serializing " + couplings.size() + " file change couplings ... ");
			}
			
			File serialFile = this.outputFileArgument.getValue();
			
			try {
				ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(serialFile));
				out.writeObject(couplings);
				out.close();
			} catch (FileNotFoundException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
			} catch (IOException e) {
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
		
		if (!this.databaseArgs.getValue()) {
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
		
		try {
			this.persistenceUtil = PersistenceManager.getUtil();
		} catch (UninitializedDatabaseException e) {
			throw new UnrecoverableError(e.getMessage());
		}
		
	}
	
}
