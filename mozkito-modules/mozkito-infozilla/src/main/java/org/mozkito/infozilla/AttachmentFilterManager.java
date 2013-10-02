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

package org.mozkito.infozilla;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.kisa.Logger;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import org.mozkito.infozilla.elements.Attachable;
import org.mozkito.infozilla.elements.Inlineable;
import org.mozkito.infozilla.filters.Filter;
import org.mozkito.infozilla.filters.enumeration.AdaptiveListingFilter;
import org.mozkito.infozilla.filters.link.LinkFilter;
import org.mozkito.infozilla.filters.log.LogFilter;
import org.mozkito.infozilla.filters.patch.UnifiedDiffPatchFilter;
import org.mozkito.infozilla.filters.sourcecode.JavaSourceCodeFilter;
import org.mozkito.infozilla.filters.stacktrace.JavaStackTraceFilter;
import org.mozkito.infozilla.model.EnhancedReport;
import org.mozkito.infozilla.model.archive.Archive;
import org.mozkito.infozilla.model.attachment.Attachment;
import org.mozkito.issues.model.AttachmentEntry;
import org.mozkito.issues.model.Report;
import org.mozkito.utilities.io.FileUtils;
import org.mozkito.utilities.io.exceptions.FilePermissionException;
import org.mozkito.utilities.mime.MimeUtils;
import org.mozkito.utilities.mime.exceptions.MIMETypeDeterminationException;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class AttachmentFilterManager implements IFilterManager {
	
	private Report                      report;
	private EnhancedReport              enhancedReport;
	private List<AttachmentEntry>       attachmentEntries;
	private final Map<URI, TextRemover> textMap          = new HashMap<>();
	
	private static final String[]       MIMETYPE_TARBALL = new String[] { "application/tar", "application/x-tar",
	        "applicaton/x-gtar", "multipart/x-tar", "application/x-compress", "application/x-compressed" };
	
	/**
	 * Instantiates a new attachment filter manager.
	 * 
	 * @param enhancedReport
	 *            the enhanced report
	 */
	public AttachmentFilterManager(final EnhancedReport enhancedReport) {
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
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	private Attachment analyzeAttachment(final Attachment attachment) {
		attachment.getContent();
		SANITY: {
			assert this.enhancedReport != null;
			assert this.report != null;
			assert this.textMap != null;
		}
		
		STACKTRACES: {
			performFiltering(attachment, new JavaStackTraceFilter(this.enhancedReport));
		}
		
		PATCHES: {
			performFiltering(attachment, new UnifiedDiffPatchFilter(this.enhancedReport));
		}
		
		SOURCECODE: {
			performFiltering(attachment, new JavaSourceCodeFilter(this.enhancedReport));
		}
		
		LOGS: {
			performFiltering(attachment, new LogFilter(this.enhancedReport));
		}
		
		LINKS: {
			performFiltering(attachment, new LinkFilter(this.enhancedReport));
		}
		
		LISTINGS: {
			performFiltering(attachment, new AdaptiveListingFilter(this.enhancedReport));
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
	
	private Attachment createFromArchive(final File file,
	                                     final Archive archive) {
		final List<Attachment> entries = new LinkedList<>();
		final Attachment innerAttachment = new Attachment(archive);
		archive.setEntries(entries);
		return innerAttachment;
		
	}
	
	/**
	 * Download.
	 * 
	 * @param entry
	 *            the entry
	 * @return the attachment
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public Attachment download(final AttachmentEntry entry) throws IOException {
		final HttpClient httpClient = new DefaultHttpClient();
		final HttpGet request = new HttpGet(entry.toURI());
		final HttpResponse response = httpClient.execute(request);
		final HttpEntity entity = response.getEntity();
		final ContentType contentType = ContentType.getOrDefault(entity);
		final byte[] data = EntityUtils.toByteArray(entity);
		final Attachment attachment = new Attachment(entry, data);
		
		attachment.setEncoding(contentType.getCharset().name());
		attachment.setFilename(entry.getFilename());
		attachment.setMd5(DigestUtils.md5(data));
		attachment.setSha1(DigestUtils.sha(data));
		
		try {
			attachment.setMime(MimeUtils.determineMIME(data));
		} catch (final MIMETypeDeterminationException e) {
			// ignore
		}
		
		return attachment;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.infozilla.IFilterManager#getEnhancedReport()
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
	 * @see org.mozkito.infozilla.IFilterManager#getReport()
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
	 * @see org.mozkito.infozilla.IFilterManager#parse()
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
			for (final AttachmentEntry entry : this.attachmentEntries) {
				try {
					final Attachment attachment = download(entry);
					final String mimeType = attachment.getMime();
					
					if (checkMimeType(attachment)) {
						// hand-over to filters
						if (Logger.logInfo()) {
							Logger.info("Filtering attachment: " + attachment);
						}
						analyzeAttachment(attachment);
					} else if (CollectionUtils.exists(Arrays.asList(AttachmentFilterManager.MIMETYPE_TARBALL),
					                                  new org.apache.commons.collections.Predicate() {
						                                  
						                                  @Override
						                                  public boolean evaluate(final Object object) {
							                                  return mimeType.equals(object);
						                                  }
					                                  })) {
						final Archive archive = new Archive(attachment);
						if (Logger.logInfo()) {
							Logger.info("Found archive: " + archive);
						}
						
						File extract;
						try {
							extract = archive.extract();
							
							if (extract.isFile()) {
								final Attachment attachment2 = createFromArchive(extract, archive);
								if (checkMimeType(attachment2)) {
									analyzeAttachment(attachment2);
								}
							} else {
								final Collection<File> files = FileUtils.listFiles(extract, null, true);
								Attachment attachment2 = null;
								
								for (final File file : files) {
									attachment2 = createFromArchive(file, archive);
									if (checkMimeType(attachment2)) {
										analyzeAttachment(attachment2);
									}
								}
							}
						} catch (final FilePermissionException e) {
							throw new RuntimeException(e);
						}
						
					}
				} catch (final IOException e) {
					throw new RuntimeException();
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
	                                                     final Filter<T> filter) {
		PRECONDITIONS: {
			if (filter == null) {
				throw new NullPointerException();
			}
		}
		
		final List<T> results = new LinkedList<>();
		
		try {
			final URI uri = attachment.getEntry().toURI();
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
