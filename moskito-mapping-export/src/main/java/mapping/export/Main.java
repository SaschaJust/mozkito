/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package mapping.export;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import net.ownhero.dev.hiari.settings.ArgumentFactory;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetFactory;
import net.ownhero.dev.hiari.settings.InputFileArgument;
import net.ownhero.dev.hiari.settings.OutputFileArgument;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kisa.Logger;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.model.RCSFile;
import de.unisaarland.cs.st.moskito.settings.DatabaseOptions;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class Main {
	
	public static void main(final String[] args) {
		try {
			final Settings settings = new Settings();
			final DatabaseOptions databaseOptions = new DatabaseOptions(settings.getRoot(), Requirement.required,
			                                                            "mapping");
			final ArgumentSet<PersistenceUtil, DatabaseOptions> databaseArguments = ArgumentSetFactory.create(databaseOptions);
			
			final InputFileArgument.Options inOptions = new InputFileArgument.Options(settings.getRoot(), "input",
			                                                                          "Input file", null,
			                                                                          Requirement.required);
			final InputFileArgument inputFileArg = ArgumentFactory.create(inOptions);
			
			final OutputFileArgument.Options outOptions = new OutputFileArgument.Options(settings.getRoot(), "output",
			                                                                             "Output file", null,
			                                                                             Requirement.required, true);
			final OutputFileArgument outFileArg = ArgumentFactory.create(outOptions);
			
			final PersistenceUtil persistenceUtil = databaseArguments.getValue();
			
			try {
				
				final CSVReader reader = new CSVReader(
				                                       new BufferedReader(
				                                                          new InputStreamReader(
				                                                                                new FileInputStream(
				                                                                                                    inputFileArg.getValue()))),
				                                       ',');
				final CSVWriter writer = new CSVWriter(
				                                       new BufferedWriter(
				                                                          new OutputStreamWriter(
				                                                                                 new FileOutputStream(
				                                                                                                      outFileArg.getValue()))),
				                                       ',');
				
				String[] line = null;
				boolean header = true;
				while ((line = reader.readNext()) != null) {
					if (!header) {
						final RCSFile file = persistenceUtil.loadById(Long.valueOf(line[0]), RCSFile.class);
						line[0] = file.getLatestPath();
						if (!line[0].endsWith(".java")) {
							continue;
						}
					} else {
						header = false;
					}
					writer.writeNext(line);
				}
				reader.close();
				writer.close();
				
			} catch (final IOException e) {
				throw new UnrecoverableError(e);
			}
			
		} catch (final SettingsParseError | ArgumentSetRegistrationException | ArgumentRegistrationException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			
		}
	}
	
}
