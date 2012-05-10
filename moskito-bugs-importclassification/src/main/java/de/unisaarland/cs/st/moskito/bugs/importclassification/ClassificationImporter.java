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
package de.unisaarland.cs.st.moskito.bugs.importclassification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.hiari.settings.ArgumentFactory;
import net.ownhero.dev.hiari.settings.ArgumentSetFactory;
import net.ownhero.dev.hiari.settings.BooleanArgument;
import net.ownhero.dev.hiari.settings.InputFileArgument;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Type;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.EnhancedReport;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Report;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.settings.DatabaseOptions;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class ClassificationImporter {
	
	@SuppressWarnings ("deprecation")
	public static void run() {
		PersistenceUtil persistenceUtil = null;
		try {
			final Settings settings = new Settings();
			final InputFileArgument.Options cvsFileOptions = new InputFileArgument.Options(
			                                                                               settings.getRoot(),
			                                                                               "importFile",
			                                                                               "The CSV file containing the report classification to be imported (only the two first column will be used",
			                                                                               null, Requirement.required);
			final BooleanArgument.Options overwriteOptions = new BooleanArgument.Options(
			                                                                             settings.getRoot(),
			                                                                             "overwrite",
			                                                                             "Set to TRUE if you want to override any previous classification",
			                                                                             false, Requirement.required);
			final DatabaseOptions databaseOptions = new DatabaseOptions(settings.getRoot(), Requirement.required,
			                                                            "bugs");
			
			persistenceUtil = ArgumentSetFactory.create(databaseOptions).getValue();
			final File csvFile = ArgumentFactory.create(cvsFileOptions).getValue();
			final Boolean overwrite = ArgumentFactory.create(overwriteOptions).getValue();
			
			final BufferedReader csvReader = new BufferedReader(new FileReader(csvFile));
			String line = null;
			int lineCounter = 0;
			
			persistenceUtil.beginTransaction();
			
			while ((line = csvReader.readLine()) != null) {
				++lineCounter;
				final String[] lineParts = line.split(",");
				if (lineParts.length < 2) {
					throw new UnrecoverableError(
					                             String.format("The csv file to import `%s` contains an invalid formatted line. In line %s the number of colums is %s. At least two columns were expected.",
					                                           csvFile.getAbsolutePath(), String.valueOf(lineCounter),
					                                           String.valueOf(lineParts.length)));
				}
				final String bugId = lineParts[0];
				Type bugType = null;
				try {
					bugType = Type.valueOf(lineParts[1].trim().toUpperCase());
				} catch (final IllegalArgumentException e) {
					throw new UnrecoverableError(
					                             String.format("The csv file to import `%s` contains an invalid formatted line. Line %s contains the unknown report type %s.",
					                                           csvFile.getAbsolutePath(), String.valueOf(lineCounter),
					                                           lineParts[1].toUpperCase()));
				}
				final Report report = persistenceUtil.loadById(bugId, Report.class);
				if (report == null) {
					if (Logger.logError()) {
						Logger.error("Could not find Report with id %s referenced from line %s in file %s.", bugId,
						             String.valueOf(lineCounter), csvFile.getAbsolutePath());
						continue;
					}
				}
				EnhancedReport eReport = persistenceUtil.loadById(report, EnhancedReport.class);
				if (eReport != null) {
					if (eReport.getClassifiedType().equals(bugType)) {
						if (Logger.logDebug()) {
							Logger.debug("Skipping Report %s because it was classified before with the same type.");
						}
						continue;
					}
					if (overwrite) {
						eReport.setClassifiedType(bugType);
					}
				} else {
					eReport = new EnhancedReport(report);
					eReport.setClassifiedType(bugType);
				}
				persistenceUtil.saveOrUpdate(eReport);
			}
			if (persistenceUtil.activeTransaction()) {
				persistenceUtil.commitTransaction();
			}
		} catch (ArgumentRegistrationException | ArgumentSetRegistrationException | IOException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			if (persistenceUtil != null) {
				persistenceUtil.rollbackTransaction();
			}
		} catch (final SettingsParseError e) {
			throw new Shutdown();
		} finally {
			//
		}
	}
}
