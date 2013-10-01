/*******************************************************************************
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
 ******************************************************************************/
package org.mozkito.infozilla;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import net.ownhero.dev.kisa.Logger;

import org.apache.commons.lang.StringEscapeUtils;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.safety.Whitelist;

import org.mozkito.infozilla.elements.Inlineable;
import org.mozkito.infozilla.filters.Filter;
import org.mozkito.infozilla.filters.enumeration.AdaptiveListingFilter;
import org.mozkito.infozilla.filters.enumeration.EnumerationFilter;
import org.mozkito.infozilla.filters.link.LinkFilter;
import org.mozkito.infozilla.filters.log.LogFilter;
import org.mozkito.infozilla.filters.patch.UnifiedDiffPatchFilter;
import org.mozkito.infozilla.filters.sourcecode.JavaSourceCodeFilter;
import org.mozkito.infozilla.filters.stacktrace.JavaStackTraceFilter;
import org.mozkito.infozilla.model.EnhancedReport;
import org.mozkito.issues.model.Comment;
import org.mozkito.issues.model.Report;
import org.mozkito.persons.model.Person;

/**
 * The Class InfozillaFilterChain.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class InlineFilterManager implements IFilterManager {
	
	/**
	 * Strip html.
	 * 
	 * @param string
	 *            the string
	 * @return the string
	 */
	private static String stripHTML(final String string) {
		String input = Jsoup.clean(string, "", Whitelist.none().addTags("br", "p"),
		                           new OutputSettings().prettyPrint(true));;
		input = Jsoup.clean(input, "", Whitelist.none(), new OutputSettings().prettyPrint(false));
		return StringEscapeUtils.unescapeHtml(input);
	}
	
	/** The enhanced report. */
	private EnhancedReport                               enhancedReport;
	
	/** The report. */
	private Report                                       report;
	
	/** The text map. */
	private final Map<Integer, TextRemover>              textMap  = new HashMap<>();
	
	/** The editor. */
	private SimpleEditor                                 editor;
	
	/** The color map. */
	private final Map<Class<? extends Filter<?>>, Color> colorMap = new HashMap<Class<? extends Filter<?>>, Color>() {
		                                                              
		                                                              private static final long serialVersionUID = 1L;
		                                                              
		                                                              {
			                                                              put(JavaStackTraceFilter.class, Color.YELLOW);
			                                                              put(JavaSourceCodeFilter.class, Color.pink);
			                                                              put(LinkFilter.class, Color.orange);
			                                                              put(LogFilter.class, Color.cyan);
			                                                              put(UnifiedDiffPatchFilter.class, Color.GREEN);
			                                                              put(AdaptiveListingFilter.class, Color.red);
			                                                              put(EnumerationFilter.class, Color.red);
		                                                              }
	                                                              };
	
	private CountDownLatch                               latch    = null;
	
	/**
	 * Instantiates a new infozilla filter chain.
	 * 
	 * @param report
	 *            the report
	 * @param editor
	 *            the editor
	 */
	public InlineFilterManager(final Report report, final SimpleEditor editor) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// body
			this.report = report;
			this.enhancedReport = new EnhancedReport(report.getId());
			
			this.textMap.put(-1,
			                 new TextRemover(stripHTML(report.getDescription()), report.getSubmitter(),
			                                 report.getCreationTimestamp()));
			for (final Comment comment : report.getComments()) {
				this.textMap.put(comment.getId(), new TextRemover(stripHTML(comment.getMessage()), comment.getAuthor(),
				                                                  comment.getTimestamp()));
			}
			
			this.editor = editor;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Filize.
	 * 
	 * @param enhancedReport
	 *            the enhanced report
	 */
	@SuppressWarnings ("unused")
	private void filize(final EnhancedReport enhancedReport) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			try (FileWriter writer = new FileWriter(new File("report_" + enhancedReport.getId()))) {
				writer.write(enhancedReport.toString());
				writer.flush();
				writer.close();
			} catch (final IOException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
			}
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the enhanced report.
	 * 
	 * @return the enhancedReport
	 */
	public EnhancedReport getEnhancedReport() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.enhancedReport;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the report.
	 * 
	 * @return the report
	 */
	public Report getReport() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.report;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.infozilla.IFilterManager#parse()
	 */
	@Override
	public EnhancedReport parse() {
		SANITY: {
			assert this.enhancedReport != null;
			assert this.report != null;
			assert this.textMap != null;
		}
		
		final Collection<Filter<? extends Inlineable>> filters = new ArrayList<Filter<? extends Inlineable>>() {
			
			/**
             * 
             */
			private static final long serialVersionUID = 1L;
			
			{
				add(new JavaStackTraceFilter(InlineFilterManager.this.enhancedReport));
				add(new UnifiedDiffPatchFilter(InlineFilterManager.this.enhancedReport));
				add(new JavaSourceCodeFilter(InlineFilterManager.this.enhancedReport));
				add(new LogFilter(InlineFilterManager.this.enhancedReport));
				add(new LinkFilter(InlineFilterManager.this.enhancedReport));
				add(new EnumerationFilter(InlineFilterManager.this.enhancedReport));
			}
		};
		
		performFiltering(filters);
		
		// set new description / comments
		this.enhancedReport.setFilteredDescription(this.textMap.get(-1).strip());
		final List<Comment> filteredComments = new ArrayList<>(this.report.getComments().size());
		
		for (final Comment comment : this.report.getComments()) {
			filteredComments.add(new Comment(comment.getId(), comment.getAuthor(), comment.getTimestamp(),
			                                 this.textMap.get(comment.getId()).strip()));
		}
		
		this.enhancedReport.setFilteredComments(filteredComments);
		
		// filize(this.enhancedReport);
		return this.enhancedReport;
	}
	
	/**
	 * Perform comments filtering.
	 * 
	 * @param filters
	 *            the filters
	 */
	private void performCommentsFiltering(final Collection<Filter<? extends Inlineable>> filters) {
		PRECONDITIONS: {
			if (filters == null) {
				throw new NullPointerException();
			}
		}
		
		try {
			final List<Inlineable> results = new LinkedList<>();
			
			for (final Comment comment : this.report.getComments()) {
				if (this.editor != null) {
					if (this.latch != null) {
						try {
							this.latch.await();
						} catch (final InterruptedException e) {
							throw new RuntimeException(e);
						}
					}
					synchronized (this.editor) {
						this.latch = this.editor.load(this.textMap.get(comment.getId()).getText(),
						                              this.enhancedReport.getId() + " - Comment " + comment.getId());
					}
				}
				
				final String message = this.textMap.get(comment.getId()).prepare();
				final Person author = comment.getAuthor();
				final DateTime timestamp = comment.getTimestamp();
				
				for (final Filter<? extends Inlineable> filter : filters) {
					for (final Inlineable result : filter.filter(message, author, timestamp)) {
						SANITY: {
							assert result.getStartPosition() != null;
							assert result.getEndPosition() != null;
						}
						
						this.textMap.get(comment.getId()).block(result.getStartPosition(), result.getEndPosition());
						
						if (this.colorMap.containsKey(filter.getClass())) {
							final Color color = this.colorMap.get(filter.getClass());
							assert result.getStartPosition() >= 0;
							assert result.getEndPosition() <= message.length();
							if (this.editor != null) {
								synchronized (this.editor) {
									if (result.getStartPosition() < result.getEndPosition()) {
										this.editor.highlight(new Region(result.getStartPosition(),
										                                 result.getEndPosition()), color);
									}
								}
							}
						}
						
						results.add(result);
					}
				}
			}
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Perform description filtering.
	 * 
	 * @param filters
	 *            the filters
	 */
	private void performDescriptionFiltering(final Collection<Filter<? extends Inlineable>> filters) {
		PRECONDITIONS: {
			if (filters == null) {
				throw new NullPointerException();
			}
		}
		
		try {
			if (this.editor != null) {
				synchronized (this.editor) {
					if (this.latch != null) {
						try {
							this.latch.await();
						} catch (final InterruptedException e) {
							throw new RuntimeException(e);
						}
					}
					
					this.latch = this.editor.load(this.textMap.get(-1).getText(), this.enhancedReport.getId()
					        + " - Description");
				}
			}
			
			final List<Inlineable> results = new LinkedList<>();
			
			final String description = this.textMap.get(-1).prepare();
			final Person author = this.textMap.get(-1).getAuthor();
			final DateTime timestamp = this.textMap.get(-1).getTimestamp();
			
			for (final Filter<? extends Inlineable> filter : filters) {
				for (final Inlineable result : filter.filter(description, author, timestamp)) {
					SANITY: {
						assert result.getStartPosition() != null;
						assert result.getEndPosition() != null;
					}
					
					this.textMap.get(-1).block(result.getStartPosition(), result.getEndPosition());
					if (this.colorMap.containsKey(filter.getClass())) {
						final Color color = this.colorMap.get(filter.getClass());
						assert result.getStartPosition() >= 0;
						assert result.getEndPosition() <= description.length();
						if (this.editor != null) {
							synchronized (this.editor) {
								if (result.getStartPosition() < result.getEndPosition()) {
									this.editor.highlight(new Region(result.getStartPosition(), result.getEndPosition()),
									                      color);
								}
							}
						}
					}
					results.add(result);
				}
			}
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Perform filtering.
	 * 
	 * @param filters
	 *            the filters
	 */
	private void performFiltering(final Collection<Filter<? extends Inlineable>> filters) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			performDescriptionFiltering(filters);
			performCommentsFiltering(filters);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
}
