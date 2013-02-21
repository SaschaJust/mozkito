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
package org.mozkito.untangling.msr2013.impact;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.ArgumentFactory;
import net.ownhero.dev.hiari.settings.InputFileArgument;
import net.ownhero.dev.hiari.settings.OutputFileArgument;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kisa.Logger;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class MapBugsToUntangledChangeSets {
	
	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		try {
			final Settings settings = new Settings();
			final InputFileArgument.Options untanglingFileOptions = new InputFileArgument.Options(
			                                                                                      settings.getRoot(),
			                                                                                      "untanglingOutFile",
			                                                                                      "The CSV file created by the untangling process.",
			                                                                                      null,
			                                                                                      Requirement.required);
			final InputFileArgument.Options cls2BugsFileOptions = new InputFileArgument.Options(
			                                                                                    settings.getRoot(),
			                                                                                    "cls2Bugs",
			                                                                                    "The CSV mapping tangled cls to bug reports.",
			                                                                                    null,
			                                                                                    Requirement.required);
			
			final InputFileArgument.Options noPartFileOptions = new InputFileArgument.Options(
			                                                                                  settings.getRoot(),
			                                                                                  "noPartitionMapping",
			                                                                                  "The CSV containing bugs2file for cls without partitions.",
			                                                                                  null,
			                                                                                  Requirement.required);
			
			final InputFileArgument.Options cos2FilesOptions = new InputFileArgument.Options(
			                                                                                 settings.getRoot(),
			                                                                                 "cos2Files",
			                                                                                 "The CSV containing changeoperation IDs associated with changed file IDs.",
			                                                                                 null, Requirement.required);
			final OutputFileArgument.Options outFileOptions = new OutputFileArgument.Options(
			                                                                                 settings.getRoot(),
			                                                                                 "out",
			                                                                                 "File to write output to.",
			                                                                                 null,
			                                                                                 Requirement.required, true);
			
			final File untanglingFile = ArgumentFactory.create(untanglingFileOptions).getValue();
			final File cls2BugsFile = ArgumentFactory.create(cls2BugsFileOptions).getValue();
			final File noPartFile = ArgumentFactory.create(noPartFileOptions).getValue();
			final File cos2FilesFile = ArgumentFactory.create(cos2FilesOptions).getValue();
			final File outFile = ArgumentFactory.create(outFileOptions).getValue();
			
			// parse no part file
			final Map<Long, Set<String>> noPartMappings = new HashMap<>();
			try (BufferedReader noPartReader = new BufferedReader(new FileReader(noPartFile))) {
				String line = null;
				boolean header = true;
				while ((line = noPartReader.readLine()) != null) {
					if (header) {
						header = false;
						continue;
					}
					final String[] lineParts = line.split(",");
					final Long changedFileId = Long.valueOf(lineParts[0]);
					if (!noPartMappings.containsKey(changedFileId)) {
						noPartMappings.put(changedFileId, new HashSet<String>());
					}
					noPartMappings.get(changedFileId).add(lineParts[2]);
				}
			} catch (final IOException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
			}
			
			// parse the cls2Bugs file
			final Map<String, Tuple<Set<String>, Boolean>> cls2Bugs = new HashMap<>();
			try (BufferedReader cls2BugsReader = new BufferedReader(new FileReader(cls2BugsFile))) {
				String line = null;
				boolean header = true;
				while ((line = cls2BugsReader.readLine()) != null) {
					if (header) {
						header = false;
						continue;
					}
					
					final String[] lineParts = line.split(",");
					Boolean mixed = true;
					if ((lineParts.length < 3) || lineParts[2].isEmpty()) {
						mixed = false;
					}
					cls2Bugs.put(lineParts[0],
					             new Tuple<Set<String>, Boolean>(
					                                             new HashSet<String>(
					                                                                 Arrays.asList(lineParts[1].split(":"))),
					                                             mixed));
				}
			} catch (final IOException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
			}
			
			// parse the cos2Files file
			final Map<Long, Tuple<Long, Boolean>> cos2Files = new HashMap<>();
			try (BufferedReader cos2FilesReader = new BufferedReader(new FileReader(cos2FilesFile))) {
				String line = null;
				boolean header = true;
				while ((line = cos2FilesReader.readLine()) != null) {
					if (header) {
						header = false;
						continue;
					}
					final String[] lineParts = line.split(",");
					final Long coID = Long.valueOf(lineParts[0]);
					final Long fileID = Long.valueOf(lineParts[1]);
					final Boolean methDefOp = Boolean.valueOf(lineParts[2]);
					cos2Files.put(coID, new Tuple<Long, Boolean>(fileID, methDefOp));
				}
			} catch (final IOException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
			}
			
			// parse the untagling_out file
			final Map<String, Set<Partition>> partitions = new HashMap<>();
			try (BufferedReader untanglingReader = new BufferedReader(new FileReader(untanglingFile))) {
				String line = null;
				boolean header = true;
				while ((line = untanglingReader.readLine()) != null) {
					if (header) {
						header = false;
						continue;
					}
					
					final String[] lineParts = line.split(",");
					final String[] operationIdsString = lineParts[2].split(":");
					final Set<Long> operationIds = new HashSet<>();
					for (final String element : operationIdsString) {
						operationIds.add(Long.valueOf(element));
					}
					if (!cls2Bugs.containsKey(lineParts[0])) {
						throw new UnrecoverableError("Cannot find partition in cls2Bugs!");
					}
					final Tuple<Set<String>, Boolean> tuple = cls2Bugs.get(lineParts[0]);
					if (!partitions.containsKey(lineParts[0])) {
						partitions.put(lineParts[0], new HashSet<Partition>());
					}
					
					final Partition partition = new Partition(lineParts[0], Integer.valueOf(lineParts[1]).intValue(),
					                                          operationIds, tuple.getFirst(), tuple.getSecond(),
					                                          cos2Files);
					partitions.get(lineParts[0]).add(partition);
				}
			} catch (final IOException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
			}
			
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFile))) {
				
				// CREATE MAPPING
				for (final Entry<String, Set<Partition>> entry : partitions.entrySet()) {
					final List<Partition> pList = new ArrayList<>();
					
					final List<String> bugIDs = new ArrayList<String>();
					bugIDs.addAll(entry.getValue().iterator().next().getBugIds());
					
					if (entry.getValue().iterator().next().isMixed()) {
						final List<Partition> mixedPartitions = new ArrayList<>();
						mixedPartitions.addAll(entry.getValue());
						Collections.sort(mixedPartitions);
						pList.addAll(mixedPartitions.subList(0, bugIDs.size()));
					} else {
						pList.addAll(entry.getValue());
					}
					if (pList.size() < bugIDs.size()) {
						if (Logger.logError()) {
							Logger.error("pList.size < bigIDs.size() for %s", entry.getKey());
						}
						continue;
					}
					for (int i = 0; i < bugIDs.size(); ++i) {
						final String bugId = bugIDs.get(i);
						// Write mapping
						final Partition p = pList.get(i);
						for (final Long fId : p.getFileIds()) {
							final StringBuilder sb = new StringBuilder();
							sb.append(fId);
							sb.append(",");
							sb.append(p.getCLId());
							sb.append(",");
							sb.append(bugId);
							sb.append(",");
							sb.append("BUG");
							sb.append(FileUtils.lineSeparator);
							writer.write(sb.toString());
						}
					}
				}
			} catch (final IOException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
			}
			
		} catch (final Shutdown | SettingsParseError | ArgumentRegistrationException | ArgumentSetRegistrationException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			
		}
	}
}
