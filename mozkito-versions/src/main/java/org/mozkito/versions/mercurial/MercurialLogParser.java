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
package org.mozkito.versions.mercurial;

import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import org.mozkito.persistence.model.Person;
import org.mozkito.versions.LogParser;
import org.mozkito.versions.elements.LogEntry;

/**
 * The Class MercurialLogParser.
 */
public class MercurialLogParser implements LogParser {
	
	/**
	 * Pre-filters log lines. Mercurial cannot replace newlines in the log messages. This method replaces newlines
	 * marked with br-tags by br-tags only. This way, each entry in the returned list of strings represents a single,
	 * atomic log entry.
	 * 
	 * @param lines
	 *            the lines (not null)
	 * @return the list
	 */
	@NoneNull
	protected static List<String> preFilterLines(final List<String> lines) {
		final List<String> completeLines = new LinkedList<String>();
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < lines.size(); ++i) {
			final String line = lines.get(i);
			if (line.endsWith("<br/>")
			        && (lines.get(i + 1).split("\\+~\\+").length < MercurialRepository.HG_MAX_LINE_PARTS_LENGTH)) {
				stringBuilder.append(line);
			} else {
				if (stringBuilder.length() > 0) {
					stringBuilder.append(line);
					completeLines.add(stringBuilder.toString());
					stringBuilder = new StringBuilder();
				} else {
					completeLines.add(line);
				}
			}
		}
		return completeLines;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.LogParser#parse(java.util.List)
	 */
	@Override
	public List<LogEntry> parse(@NotNull final List<String> logMessages) {
		// pre-filter lines. hg log might have some entries spanning multiple
		// lines.
		final List<String> lines = preFilterLines(logMessages);
		final List<LogEntry> result = new LinkedList<>();
		for (final String line : lines) {
			final String[] lineParts = line.split("\\+~\\+");
			if (lineParts.length < MercurialRepository.HG_MAX_LINE_PARTS_LENGTH) {
				if (Logger.logError()) {
					Logger.error("hg log could not be parsed. Too less columns in logfile.");
					return null;
				}
			}
			if (lineParts.length > MercurialRepository.HG_MAX_LINE_PARTS_LENGTH) {
				final StringBuilder s = new StringBuilder();
				s.append(lineParts[MercurialRepository.HG_MAX_LINE_PARTS_LENGTH - 1]);
				for (int i = MercurialRepository.HG_MAX_LINE_PARTS_LENGTH; i < lineParts.length; ++i) {
					s.append(":");
					s.append(lineParts[i]);
				}
				lineParts[MercurialRepository.HG_MAX_LINE_PARTS_LENGTH - 1] = s.toString();
			}
			final String revID = lineParts[0];
			final String authorString = lineParts[1];
			
			String authorFullname = null;
			String authorUsername = null;
			String authorEmail = null;
			
			MercurialRepository.AUTHOR_REGEX.find(authorString);
			MercurialRepository.AUTHOR_REGEX.getGroupNames();
			
			if (MercurialRepository.AUTHOR_REGEX.getGroup("plain") != null) {
				authorUsername = MercurialRepository.AUTHOR_REGEX.getGroup("plain").trim();
			} else if ((MercurialRepository.AUTHOR_REGEX.getGroup("lastname") != null)
			        && (MercurialRepository.AUTHOR_REGEX.getGroup("name") != null)) {
				authorFullname = MercurialRepository.AUTHOR_REGEX.getGroup("name").trim() + " "
				        + MercurialRepository.AUTHOR_REGEX.getGroup("lastname").trim();
			} else if (MercurialRepository.AUTHOR_REGEX.getGroup("name") != null) {
				authorUsername = MercurialRepository.AUTHOR_REGEX.getGroup("name").trim();
			}
			if (MercurialRepository.AUTHOR_REGEX.getGroup("email") != null) {
				authorEmail = MercurialRepository.AUTHOR_REGEX.getGroup("email").trim();
			}
			final Person author = new Person(authorUsername, authorFullname, authorEmail);
			
			final String[] dateString = lineParts[2].split(" ");
			
			final DateTime date = new DateTime(
			                                   Long.valueOf(dateString[0]).longValue() * 1000,
			                                   DateTimeZone.forOffsetMillis(Integer.valueOf(dateString[1]).intValue() * 1000));
			
			LogEntry previous = null;
			if (result.size() > 0) {
				previous = result.get(result.size() - 1);
			}
			result.add(new LogEntry(
			                        revID,
			                        previous,
			                        author,
			                        lineParts[MercurialRepository.HG_MAX_LINE_PARTS_LENGTH - 1].replaceAll("<br/>",
			                                                                                               FileUtils.lineSeparator),
			                        date, ""));
		}
		return result;
	}
	
}
