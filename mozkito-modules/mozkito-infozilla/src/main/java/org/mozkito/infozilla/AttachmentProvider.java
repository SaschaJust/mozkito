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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.kisa.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaMetadataKeys;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import org.mozkito.infozilla.exceptions.EncodingDeterminationException;
import org.mozkito.infozilla.model.archive.Archive;
import org.mozkito.infozilla.model.archive.Archive.Type;
import org.mozkito.infozilla.model.attachment.Attachment;
import org.mozkito.issues.model.AttachmentEntry;
import org.mozkito.utilities.datastructures.Tuple;
import org.mozkito.utilities.io.FileUtils;

/**
 * The Class AttachmentProvider.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class AttachmentProvider implements IAttachmentProvider {
	
	/** The Constant ENCODING_UNKNOWN. */
	private static final String ENCODING_UNKNOWN = "UNKNOWN";
	
	/**
	 * Determine encoding.
	 * 
	 * @param stream
	 *            the stream
	 * @return the string
	 * @throws EncodingDeterminationException
	 *             the encoding determination exception
	 */
	public static String determineEncoding(final InputStream stream) throws EncodingDeterminationException {
		try {
			final ContentHandler contenthandler = new BodyContentHandler();
			final Metadata metadata = new Metadata();
			metadata.set(TikaMetadataKeys.RESOURCE_NAME_KEY, "filename");
			final Parser parser = new AutoDetectParser();
			final ParseContext context = new ParseContext();
			context.set(Parser.class, parser);
			
			parser.parse(stream, contenthandler, metadata, context);
			
			return metadata.get(HttpHeaders.CONTENT_ENCODING);
		} catch (final IOException e) {
			throw new EncodingDeterminationException(e);
		} catch (final SAXException e) {
			throw new EncodingDeterminationException(e);
		} catch (final TikaException e) {
			throw new EncodingDeterminationException(e);
		}
	}
	
	/**
	 * @param archive
	 * @return
	 */
	private File extract(final Archive archive) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			//
			throw new RuntimeException("Method 'extract' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	public Tuple<Attachment, Map<Archive, Attachment>> initialize(final AttachmentEntry entry) throws IOException {
		final Attachment attachment = new Attachment(entry);
		
		File target = null;
		
		if ((target = locateInCache(attachment)) == null) {
			target = provide(attachment);
		}
		
		final Tuple<Attachment, Map<Archive, Attachment>> tuple = new Tuple<Attachment, Map<Archive, Attachment>>(
		                                                                                                          attachment,
		                                                                                                          new HashMap<Archive, Attachment>());
		
		if (isArchive(target)) {
			Archive.Type type = null;
			final String extension = FilenameUtils.getExtension(attachment.getFilename());
			type = (Type) CollectionUtils.find(Arrays.asList(Archive.Type.values()), new Predicate() {
				
				@Override
				public boolean evaluate(final Object object) {
					final Archive.Type type = (Type) object;
					return type.name().equalsIgnoreCase(extension);
				}
			});
			
			SANITY: {
				assert type != null;
			}
			
			final Archive archive = new Archive(attachment, type);
			
			extract(archive);
			
		}
		
		return tuple;
	}
	
	/**
	 * @param target
	 * @return
	 */
	private boolean isArchive(final File target) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			// return false;
			throw new RuntimeException("Method 'isArchive' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * @param attachment
	 * @return
	 */
	private File locateInCache(final Attachment attachment) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'locateInCache' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.infozilla.IAttachmentProvider#provide(org.mozkito.infozilla.model.attachment.Attachment)
	 */
	@Override
	public File provide(final Attachment attachment) throws IOException {
		final AttachmentEntry entry = attachment.getEntry();
		File file = null;
		
		if (entry != null) {
			if (Logger.logDebug()) {
				Logger.debug("Downloading attachment: " + entry.getLink());
			}
			final HttpClient httpClient = new DefaultHttpClient();
			URI uri;
			try {
				uri = entry.toURI();
			} catch (final URISyntaxException e) {
				throw new IOException(e);
			}
			final HttpGet request = new HttpGet(uri);
			final HttpResponse response = httpClient.execute(request);
			final HttpEntity entity = response.getEntity();
			final ContentType contentType = ContentType.getOrDefault(entity);
			SANITY: {
				assert contentType != null;
			}
			file = FileUtils.createRandomFile("infozilla_attachment.", "_" + attachment.getFilename(),
			                                  FileUtils.FileShutdownAction.KEEP);
			try (InputStream contentInputStream = entity.getContent()) {
				try (OutputStream outputStream = new FileOutputStream(file)) {
					org.apache.commons.io.IOUtils.copy(contentInputStream, outputStream);
				}
			}
			final Charset charset = contentType.getCharset();
			attachment.setEncoding(charset != null
			                                      ? charset.name()
			                                      : Charset.defaultCharset().name());
			attachment.setFilename(entry.getFilename());
		} else if (attachment.getArchive() != null) {
			SANITY: {
				assert attachment.getPath() != null;
				assert attachment.getFilename() != null;
			}
			final File targetDirectory = attachment.getArchive().extractedDataDirectory(this);
			final File targetFile = new File(new File(targetDirectory, attachment.getPath()), attachment.getFilename());
			
			if (!targetFile.exists()) {
				throw new IOException("Couldn't find archived file: " + attachment.getPath() + FileUtils.fileSeparator
				        + attachment.getFilename());
			}
			
			file = targetFile;
			
			try (FileInputStream inputStream = new FileInputStream(file)) {
				try {
					final String encoding = determineEncoding(inputStream);
					
					attachment.setEncoding(encoding != null
					                                       ? encoding
					                                       : ENCODING_UNKNOWN);
				} catch (final EncodingDeterminationException e) {
					attachment.setEncoding(ENCODING_UNKNOWN);
				}
			}
		} else {
			assert false;
			throw new RuntimeException("Either AttachmentEntry or Archive defining the origin must be null");
		}
		
		SANITY: {
			assert file != null;
		}
		
		return file;
	}
}
