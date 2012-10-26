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
package atomicchanges;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.settings.DatabaseOptions;
import org.mozkito.versions.atomic.AtomicTransactionImporter;

import net.ownhero.dev.hiari.settings.ArgumentFactory;
import net.ownhero.dev.hiari.settings.ArgumentSetFactory;
import net.ownhero.dev.hiari.settings.InputFileArgument;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kisa.Logger;

/**
 * @author Kim Herzig <herzig@mozkito.org>
 * 
 */
public class Main {
	
	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		// PRECONDITIONS
		
		try {
			final Settings settings = new Settings();
			final DatabaseOptions dbOptions = new DatabaseOptions(settings.getRoot(), Requirement.required, "rcs");
			final PersistenceUtil persistenceUtil = ArgumentSetFactory.create(dbOptions).getValue();
			
			final InputFileArgument.Options importFileOptions = new InputFileArgument.Options(
			                                                                                  settings.getRoot(),
			                                                                                  "importFile",
			                                                                                  "The file containing an transaction id to be marked as atomic ich each line.",
			                                                                                  null,
			                                                                                  Requirement.required);
			final File importFile = ArgumentFactory.create(importFileOptions).getValue();
			persistenceUtil.beginTransaction();
			try (final BufferedReader csvReader = new BufferedReader(new FileReader(importFile));) {
				String line = null;
				while ((line = csvReader.readLine()) != null) {
					if (!AtomicTransactionImporter.markTransactionIdAsAtomic(line.trim(), persistenceUtil)) {
						if (Logger.logError()) {
							Logger.error("Could not mark RCSTransaction with id %s as atomic. Transaction does not exist in DB.",
							             line.trim());
						}
					}
				}
				persistenceUtil.commitTransaction();
				
			} catch (final IOException e) {
				persistenceUtil.rollbackTransaction();
				throw new UnrecoverableError(e);
			}
		} catch (final SettingsParseError | ArgumentSetRegistrationException | ArgumentRegistrationException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			
		}
	}
	
}
