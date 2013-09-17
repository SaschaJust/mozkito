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

package org.mozkito.mappings.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.mozkito.issues.model.Report;
import org.mozkito.mappings.model.Mapping;
import org.mozkito.persistence.DatabaseEnvironment;
import org.mozkito.persistence.OpenJPAUtil;
import org.mozkito.persistence.PersistenceManager;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.exceptions.NoSuchHandleException;
import org.mozkito.versions.model.ChangeSet;
import org.mozkito.versions.model.Handle;
import org.mozkito.versions.model.Revision;

/**
 * The Class Exporter.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class Exporter {
	
	/** The Constant lineSeparator. */
	private static final String        lineSeparator = System.getProperty("line.separator");
	
	/** The environment. */
	private static DatabaseEnvironment environment   = null;
	
	/**
	 * Creates a writable file at the given path.
	 * 
	 * @param path
	 *            the path
	 * @return the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static File createWritableFile(final String path) throws IOException {
		final File csvFile = new File(path);
		
		if (csvFile.exists()) {
			throw new IOException("File already exists.");
		} else {
			if (!csvFile.createNewFile()) {
				throw new IOException("File creation failed.");
			}
			
			if (!csvFile.canWrite()) {
				throw new IOException("Can not write to newly created file.");
			}
		}
		assert csvFile != null : "File must not be null at this point.";
		
		return csvFile;
	}
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(final String[] args) {
		// requires the path to the CSV file.
		if (args.length < 1) {
			System.err.println("You have to specify the path to the output CSV file.");
			return;
		}
		
		final String csvFilePath = args[0];
		File csvFile = null;
		
		// make sure we can create a new writable file at the given path
		try {
			csvFile = createWritableFile(csvFilePath);
		} catch (final IOException e) {
			e.printStackTrace(System.err);
			return;
		}
		
		assert csvFile != null;
		
		// crate a PersistenceUtil to access the database
		final PersistenceUtil util = PersistenceManager.createUtil(environment, OpenJPAUtil.class);
		// load the mappings from the database
		final List<Mapping> list = util.load(util.createCriteria(Mapping.class));
		
		// we will use a map from file path to the set of associated bug IDs here
		final Map<String, Set<String>> bugs2Files = new HashMap<>();
		
		for (final Mapping mapping : list) {
			// we assume, that we created a mapping from Reports to ChangeSets
			assert mapping.getFrom().getClass() == Report.class;
			assert mapping.getTo().getClass() == ChangeSet.class;
			
			final MappableChangeSet mChangeSet = (MappableChangeSet) mapping.getTo();
			assert mChangeSet != null;
			
			final ChangeSet changeSet = mChangeSet.getChangeSet();
			final Collection<Revision> revisions = changeSet.getRevisions();
			
			// we iterate over all revisions in the ChangeSet to assign the bug to all changed files.
			for (final Revision revision : revisions) {
				final Handle changedFile = revision.getChangedFile();
				
				// we want to use the most recent name of the file (might have changed throughout the history)
				String fileName = null;
				try {
					fileName = changedFile.getLatestPath();
				} catch (final NoSuchHandleException e) {
					// file does not exist anymore. Do not count bugs for non-existent files.
				}
				
				// only count, if the file still exists
				if (fileName != null) {
					Set<String> bugs = null;
					
					// create a new entry in the map if there is none yet
					if (!bugs2Files.containsKey(fileName)) {
						bugs = bugs2Files.put(fileName, new HashSet<String>());
					}
					
					if (bugs == null) {
						bugs = bugs2Files.get(fileName);
						assert bugs != null;
					}
					
					// assign the bug ID to the file
					bugs.add(mapping.getFrom().getId());
				}
			}
		}
		
		// write the map to the CSV file
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
			for (final Entry<String, Set<String>> csvEntry : bugs2Files.entrySet()) {
				writer.append(csvEntry.getKey()).append(',').append(csvEntry.getValue().size() + "")
				      .append(lineSeparator);
			}
		} catch (final IOException e) {
			e.printStackTrace(System.err);
			return;
		}
	}
}
