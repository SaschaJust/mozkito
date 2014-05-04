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

package org.mozkito.codechanges.mapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mozkito.codechanges.lightweightparser.Project;
import org.mozkito.codechanges.lightweightparser.structure.Function;
import org.mozkito.codechanges.mapping.model.ClassDefinition;
import org.mozkito.codechanges.mapping.model.MethodDefinition;
import org.mozkito.versions.exceptions.NoSuchHandleException;
import org.mozkito.versions.model.ChangeSet;
import org.mozkito.versions.model.Revision;

/**
 * The Class RevisionAnalyzer.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class RevisionAnalyzer {
	
	/** The file to classes. */
	private final Map<String, List<String>>    fileToClasses         = new HashMap<>();
	
	/** The class name to definition. */
	private final Map<String, ClassDefinition> classNameToDefinition = new HashMap<>();
	
	/** The path to revision. */
	private final Map<String, Revision>        pathToRevision        = new HashMap<>();
	
	/**
	 * Adds the path.
	 * 
	 * @param path
	 *            the path
	 * @param changeSet
	 *            the change set
	 * @return the revision
	 */
	private Revision addPath(final String path,
	                         final ChangeSet changeSet) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			for (final Revision revision : changeSet.getRevisions()) {
				try {
					final String latestPath = revision.getChangedFile().getPath(changeSet);
					if (path.equals(latestPath)) {
						this.pathToRevision.put(path, revision);
						return revision;
					}
				} catch (final NoSuchHandleException e) {
					throw new RuntimeException();
				}
			}
			
			throw new RuntimeException();
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Analyze.
	 * 
	 * @param project
	 *            the project
	 * @param changeSet
	 *            the change set
	 */
	public void analyze(final Project project,
	                    final ChangeSet changeSet) {
		for (final Function function : project.getFunctions()) {
			final String location = function.getLocation();
			final String path = location.substring(0, location.lastIndexOf(':'));
			final Revision revision = this.pathToRevision.containsKey(path)
			                                                               ? this.pathToRevision.get(path)
			                                                               : addPath(path, changeSet);
			new MethodDefinition(function.getName(), revision, revision.getChangedFile(), function.getBegin(),
			                     function.getEnd(), null);
		}
	}
}
