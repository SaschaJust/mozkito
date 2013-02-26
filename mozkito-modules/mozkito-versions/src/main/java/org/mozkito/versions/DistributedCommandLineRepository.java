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
package org.mozkito.versions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.string.MinLength;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.mozkito.versions.elements.LogEntry;
import org.mozkito.versions.exceptions.RepositoryOperationException;

/**
 * The Class DistributedCommandLineRepository.
 */
public abstract class DistributedCommandLineRepository extends Repository {
	
	/** The log cache. */
	private final HashMap<String, LogEntry> logCache = new HashMap<String, LogEntry>();
	
	/**
	 * Execute log.
	 * 
	 * @param revision
	 *            the revision
	 * @return the tuple
	 * @throws RepositoryOperationException
	 *             the repository operation exception
	 */
	public abstract Tuple<Integer, List<String>> executeLog(String revision) throws RepositoryOperationException;
	
	/**
	 * Execute the log command on the command line.
	 * 
	 * @param fromRevision
	 *            the from revision
	 * @param toRevision
	 *            the to revision
	 * @return a tuple of command line return value and the list of lines printed by the command executed. (see
	 *         CommandLineExecutor.class)
	 * @throws RepositoryOperationException
	 *             the repository operation exception
	 */
	public abstract Tuple<Integer, List<String>> executeLog(String fromRevision,
	                                                        String toRevision) throws RepositoryOperationException;
	
	/**
	 * Gets the log parser.
	 * 
	 * @return the log parser
	 */
	protected abstract LogParser getLogParser();
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#log(java.lang.String, java.lang.String)
	 */
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.versions.Repository#log(java.lang.String, java.lang.String)
	 */
	@Override
	@NoneNull
	public List<LogEntry> log(@MinLength (min = 1) final String fromRevision,
	                          @MinLength (min = 1) final String toRevision) throws RepositoryOperationException {
		String toRev = toRevision;
		if ("HEAD".equals(toRevision)) {
			toRev = getHEADRevisionId();
		}
		final long fromIndex = getChangeSetIndex(fromRevision);
		final long toIndex = getChangeSetIndex(toRev);
		
		Condition.check(fromIndex >= 0, String.format("Start transaction %s for log() is unknown (%s)!", fromRevision,
		                                              this.getClass().getCanonicalName()));
		Condition.check(toIndex >= 0, String.format("End transaction %s for log() is unknown (%s)!", toRevision,
		                                            this.getClass().getCanonicalName()));
		Condition.check(fromIndex <= toIndex, String.format("cannot log from later revision to earlier one (%s)!",
		                                                    this.getClass().getCanonicalName()));
		
		final List<LogEntry> result = new LinkedList<LogEntry>();
		
		final Tuple<Integer, List<String>> response = executeLog(fromRevision, toRevision);
		
		if (response.getFirst() != 0) {
			return null;
		}
		
		final LogParser logParser = getLogParser();
		
		for (final LogEntry e : logParser.parse(response.getSecond())) {
			this.logCache.put(e.getRevision(), e);
		}
		
		for (long i = fromIndex; i <= toIndex; ++i) {
			final String tId = getChangeSetId(i);
			
			if (this.logCache.containsKey(tId)) {
				result.add(this.logCache.get(tId));
			}
			this.logCache.remove(tId);
		}
		return result;
	}
}
