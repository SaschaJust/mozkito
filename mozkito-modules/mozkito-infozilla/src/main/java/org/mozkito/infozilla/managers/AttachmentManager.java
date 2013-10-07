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

package org.mozkito.infozilla.managers;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.kisa.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import org.mozkito.infozilla.AttachmentProvider;
import org.mozkito.infozilla.IAttachmentProvider;
import org.mozkito.infozilla.TextRemover;
import org.mozkito.infozilla.elements.Attachable;
import org.mozkito.infozilla.elements.Inlineable;
import org.mozkito.infozilla.filters.IFilter;
import org.mozkito.infozilla.filters.enumeration.AdaptiveListingFilter;
import org.mozkito.infozilla.filters.link.LinkFilter;
import org.mozkito.infozilla.filters.log.LogFilter;
import org.mozkito.infozilla.filters.patch.UnifiedDiffPatchFilter;
import org.mozkito.infozilla.filters.sourcecode.JavaSourceCodeFilter;
import org.mozkito.infozilla.filters.stacktrace.JavaStackTraceFilter;
import org.mozkito.infozilla.model.EnhancedReport;
import org.mozkito.infozilla.model.archive.Archive;
import org.mozkito.infozilla.model.archive.Archive.Type;
import org.mozkito.infozilla.model.attachment.Attachment;
import org.mozkito.infozilla.model.itemization.Listing;
import org.mozkito.infozilla.model.link.Link;
import org.mozkito.infozilla.model.log.Log;
import org.mozkito.infozilla.model.patch.Patch;
import org.mozkito.infozilla.model.source.SourceCode;
import org.mozkito.infozilla.model.stacktrace.Stacktrace;
import org.mozkito.issues.model.AttachmentEntry;
import org.mozkito.issues.model.Report;
import org.mozkito.utilities.io.exceptions.FilePermissionException;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class AttachmentManager implements IManager {
	
	private Report                      report;
	private EnhancedReport              enhancedReport;
	private List<AttachmentEntry>       attachmentEntries;
	private final Map<URI, TextRemover> textMap = new HashMap<>();
	private IAttachmentProvider          attachmentProvider;
	
	/**
	 * Instantiates a new attachment filter manager.
	 * 
	 * @param enhancedReport
	 *            the enhanced report
	 */
	public AttachmentManager(final EnhancedReport enhancedReport) {
		PRECONDITIONS: {
			if (enhancedReport.getReport() == null) {
				throw new NullPointerException();
			}
		}
		
		try {
			// body
			this.report = enhancedReport.getReport();
			this.enhancedReport = enhancedReport;
			this.attachmentEntries = enhancedReport.getReport().getAttachmentEntries();
			this.attachmentProvider = new AttachmentProvider();
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	private Attachment analyzeAttachment(final Attachment attachment) {
		SANITY: {
			assert this.enhancedReport != null;
			assert this.report != null;
			assert this.textMap != null;
		}
		
		STACKTRACES: {
			final IFilter<Stacktrace> filter = new JavaStackTraceFilter();
			filter.setEnhancedReport(this.enhancedReport);
			performFiltering(attachment, filter);
		}
		
		PATCHES: {
			final IFilter<Patch> filter = new UnifiedDiffPatchFilter();
			filter.setEnhancedReport(this.enhancedReport);
			performFiltering(attachment, filter);
		}
		
		SOURCECODE: {
			final IFilter<SourceCode> filter = new JavaSourceCodeFilter();
			filter.setEnhancedReport(this.enhancedReport);
			performFiltering(attachment, filter);
		}
		
		LOGS: {
			final IFilter<Log> filter = new LogFilter();
			filter.setEnhancedReport(this.enhancedReport);
			performFiltering(attachment, filter);
		}
		
		LINKS: {
			final IFilter<Link> filter = new LinkFilter();
			filter.setEnhancedReport(this.enhancedReport);
			performFiltering(attachment, filter);
		}
		
		LISTINGS: {
			final IFilter<Listing> filter = new AdaptiveListingFilter();
			filter.setEnhancedReport(this.enhancedReport);
			performFiltering(attachment, filter);
		}
		
		return attachment;
	}
	
	private boolean checkMimeType(final Attachment attachment) {
		PRECONDITIONS: {
			if (attachment == null) {
				throw new NullPointerException();
			}
		}
		
		return (attachment.getMime() != null) && attachment.getMime().startsWith("text");
	}
	
	private List<Attachment> createFromArchive(final File directory,
	                                           final Archive archive) {
		final List<Attachment> entries = new LinkedList<>();
		
		SANITY: {
			assert directory != null;
			assert directory.isDirectory();
		}
		
		final Iterator<File> filesIterator = org.apache.commons.io.FileUtils.iterateFiles(directory,
		                                                                                  new IOFileFilter() {
			                                                                                  
			                                                                                  @Override
			                                                                                  public boolean accept(final File file) {
				                                                                                  return true;
			                                                                                  }
			                                                                                  
			                                                                                  @Override
			                                                                                  public boolean accept(final File dir,
			                                                                                                        final String name) {
				                                                                                  return true;
				                                                                                  
			                                                                                  }
		                                                                                  }, new IOFileFilter() {
			                                                                                  
			                                                                                  @Override
			                                                                                  public boolean accept(final File file) {
				                                                                                  return true;
			                                                                                  }
			                                                                                  
			                                                                                  @Override
			                                                                                  public boolean accept(final File dir,
			                                                                                                        final String name) {
				                                                                                  return true;
				                                                                                  
			                                                                                  }
		                                                                                  });
		
		while (filesIterator.hasNext()) {
			final File next = filesIterator.next();
			
			SANITY: {
				assert next.getAbsolutePath().startsWith(directory.getAbsolutePath());
			}
			
			String relativePath = null;
			
			if (next.getParentFile().equals(directory)) {
				relativePath = ".";
			} else {
				relativePath = next.getAbsolutePath()
				                   .substring(directory.getAbsolutePath().length()
				                                      + org.mozkito.utilities.io.FileUtils.fileSeparator.length());
				relativePath = relativePath.substring(0, relativePath.length() - next.getName().length()
				        - org.mozkito.utilities.io.FileUtils.fileSeparator.length());
			}
			
			entries.add(new Attachment(next.getName(), relativePath, archive));
		}
		
		return entries;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.infozilla.managers.IManager#getEnhancedReport()
	 */
	@Override
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
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.infozilla.managers.IManager#getReport()
	 */
	@Override
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
	 * @see org.mozkito.infozilla.managers.IManager#parse()
	 */
	@Override
	public EnhancedReport parse() {
		PRECONDITIONS: {
			// none
		}
		
		SANITY: {
			assert this.enhancedReport != null;
			assert this.report != null;
			assert this.textMap != null;
		}
		
		try {
			
			Archive.Type type = null;
			for (final AttachmentEntry entry : this.attachmentEntries) {
				try {
					final Attachment attachment = new Attachment(entry);
					
					if (checkMimeType(attachment)) {
						// hand-over to filters
						if (Logger.logInfo()) {
							Logger.info("Filtering attachment: " + attachment);
						}
						analyzeAttachment(attachment);
					} else {
						final String extension = FilenameUtils.getExtension(attachment.getFilename());
						type = (Type) CollectionUtils.find(Arrays.asList(Archive.Type.values()), new Predicate() {
							
							@Override
							public boolean evaluate(final Object object) {
								final Archive.Type type = (Type) object;
								return type.name().equalsIgnoreCase(extension);
							}
						});
						
						if (type != null) {
							final Archive archive = new Archive(attachment, type);
							if (Logger.logInfo()) {
								Logger.info("Found archive: " + archive);
							}
							
							File directory;
							try {
								directory = archive.extractedDataDirectory(this.attachmentProvider);
								
								SANITY: {
									assert directory != null;
									assert directory.exists();
									assert directory.isDirectory();
								}
								
								final List<Attachment> attachmentList = createFromArchive(directory, archive);
								for (final Attachment innerAttachment : attachmentList) {
									if (checkMimeType(innerAttachment)) {
										analyzeAttachment(innerAttachment);
									}
								}
							} catch (final FilePermissionException e) {
								throw new RuntimeException(e);
							}
							
						}
					}
				} catch (final IOException e) {
					throw new RuntimeException(e);
				}
			}
			
			return this.enhancedReport;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * @param javaStackTraceFilter
	 */
	private <T extends Inlineable> void performFiltering(final Attachment attachment,
	                                                     final IFilter<T> filter) {
		PRECONDITIONS: {
			if (filter == null) {
				throw new NullPointerException();
			}
		}
		
		final List<T> results = new LinkedList<>();
		
		try {
			URI uri;
			try {
				uri = attachment.getEntry().toURI();
			} catch (final URISyntaxException e) {
				assert false : e.getMessage();
				throw new IllegalArgumentException(e);
			}
			
			final String text = this.textMap.get(uri).prepare();
			
			for (final T result : filter.filter(text, attachment.getEntry().getAuthor(), attachment.getEntry()
			                                                                                       .getTimestamp())) {
				SANITY: {
					assert result.getStartPosition() != null;
					assert result.getEndPosition() != null;
				}
				
				this.textMap.get(-1).block(result.getStartPosition(), result.getEndPosition());
				
				SANITY: {
					assert Attachable.class.isAssignableFrom(result.getClass());
				}
				
				((Attachable) result).setOrigin(attachment);
				results.add(result);
			}
			
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
}
