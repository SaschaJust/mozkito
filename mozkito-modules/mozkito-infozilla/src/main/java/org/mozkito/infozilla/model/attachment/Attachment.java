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
package org.mozkito.infozilla.model.attachment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import net.ownhero.dev.kisa.Logger;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaMetadataKeys;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import org.mozkito.infozilla.elements.AttachmentType;
import org.mozkito.infozilla.exceptions.EncodingDeterminationException;
import org.mozkito.infozilla.model.archive.Archive;
import org.mozkito.issues.model.AttachmentEntry;
import org.mozkito.persistence.Annotated;
import org.mozkito.utilities.commons.JavaUtils;
import org.mozkito.utilities.io.FileUtils;

import serp.util.Strings;

/**
 * The Class Attachment.
 */
@Entity
public class Attachment implements Annotated {
	
	/** The Constant MD5_SIZE. */
	private static final int    MD5_SIZE         = 16;
	
	/** The Constant serialVersionUID. */
	private static final long   serialVersionUID = 3626906010864829890L;
	
	/** The Constant SHA1_SIZE. */
	private static final int    SHA1_SIZE        = 32;
	
	private static final String MIMETYPE_UNKNOWN = "UNKNOWN";
	
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
	private static String determineEncoding(final InputStream stream) throws EncodingDeterminationException {
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
	
	//
	// /**
	// * Fetch.
	// *
	// * @param entry
	// * the entry
	// * @return the attachment
	// * @throws FetchException
	// * the fetch exception
	// */
	// public static Attachment fetch(final AttachmentEntry entry) throws FetchException {
	// try {
	// final byte[] data = IOUtils.binaryfetch(entry.toURI());
	//
	// final Attachment attachment = new Attachment(entry, data);
	//
	// try {
	// attachment.setMime(determineMIME(data));
	// attachment.setType(guessType(attachment));
	// } catch (final MIMETypeDeterminationException e) {
	// if (Logger.logWarn()) {
	// Logger.warn("Could not determine MIME type from data for " + entry + ": " + e.getMessage());
	// }
	// }
	//
	// try {
	// attachment.setEncoding(determineEncoding(data));
	// } catch (final EncodingDeterminationException e) {
	// if (Logger.logWarn()) {
	// Logger.warn("Could not determine encoding from data for " + entry + ": " + e.getMessage());
	// }
	// }
	//
	// attachment.setMd5(computeMD5(data));
	// attachment.setSha1(computeSHA1(data));
	//
	// return attachment;
	// } catch (final UnsupportedProtocolException e) {
	// throw new FetchException(e);
	// } catch (final IOException e) {
	// throw new FetchException(e);
	// }
	// }
	//
	/**
	 * Guess type.
	 * 
	 * @param attachment
	 *            the attachment
	 * @return the attachment type
	 */
	private static AttachmentType guessType(final Attachment attachment) {
		PRECONDITIONS: {
			if (attachment == null) {
				throw new NullPointerException();
			}
		}
		
		AttachmentType guessedType = AttachmentType.UNKNOWN;
		final String description = ((attachment.getEntry() != null) && (attachment.getEntry().getDescription() != null)
		                                                                                                               ? attachment.getEntry()
		                                                                                                                           .getDescription()
		                                                                                                               : "").toLowerCase();
		final String mime = attachment.getMime().toLowerCase();
		final String fileName = attachment.getFilename();
		
		SANITY: {
			assert description != null;
			assert mime != null;
			assert fileName != null;
		}
		
		// Rules for Screenshot
		if (mime.contains("image") || mime.contains("jpg") || mime.contains("gif") || mime.contains("jpeg")
		        || mime.contains("bmp") || mime.contains("png") || mime.contains("video")
		        || description.contains("screenshot") || fileName.toLowerCase().contains("screenshot")) {
			guessedType = AttachmentType.IMAGE;
		}
		
		// Rules for Patches
		if (mime.contains("patch") || mime.contains("diff") || description.contains("patch")
		        || description.contains("diff") || fileName.toLowerCase().contains("patch")
		        || fileName.toLowerCase().contains("diff")) {
			guessedType = AttachmentType.PATCH;
		}
		
		// Rules for Source Code
		if (mime.contains("text/java") || mime.contains("text/java source") || mime.contains("text/x-java")
		        || mime.contains("text/x-java source")
		        || (description.contains("source") && description.contains("code"))
		        || fileName.toLowerCase().contains(".java")) {
			guessedType = AttachmentType.SOURCECODE;
		}
		
		// Rules for Stack Traces
		if (mime.contains("text/log") || mime.contains("text/x-log")
		        || (description.contains("stack") && description.contains("trace"))
		        || fileName.toLowerCase().contains("stacktrace")) {
			guessedType = AttachmentType.STACKTRACE;
		}
		
		return guessedType;
		
	}
	
	/** The file. */
	private File            file = null;
	
	/** The encoding. */
	private String          encoding;
	
	/** The entry. */
	private AttachmentEntry entry;
	
	/** The filename. */
	private String          filename;
	
	/** The generated id. */
	private long            generatedId;
	
	/** The md5. */
	private byte[]          md5  = new byte[Attachment.MD5_SIZE];
	
	/** The mime. */
	private String          mime;
	
	/** The sha1. */
	private byte[]          sha1 = new byte[Attachment.SHA1_SIZE];
	
	/** The type. */
	private AttachmentType  type = AttachmentType.UNKNOWN;
	
	/** The archive. */
	private Archive         archive;
	
	/** The path. */
	private String          path = null;
	
	/**
	 * Instantiates a new attachment.
	 * 
	 * @deprecated must only be used by JPA
	 */
	@Deprecated
	public Attachment() {
		// stub
	}
	
	/**
	 * Instantiates a new attachment.
	 * 
	 * @param entry
	 *            the entry
	 */
	public Attachment(final AttachmentEntry entry) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// body
			try {
				this.entry = entry;
				this.filename = entry.getFilename();
				this.path = ".";
				getFile();
				this.type = guessType(this);
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Instantiates a new attachment.
	 * 
	 * @param filename
	 *            the filename
	 * @param path
	 *            the path
	 * @param archive
	 *            the archive
	 */
	public Attachment(final String filename, final String path, final Archive archive) {
		this.filename = filename;
		this.archive = archive;
		this.path = path;
		
		try {
			getFile();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		
		this.type = guessType(this);
	}
	
	/**
	 * Gets the archive.
	 * 
	 * @return the archive
	 */
	@ManyToOne
	public Archive getArchive() {
		return this.archive;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.persistence.Annotated#getClassName()
	 */
	@Override
	@Transient
	public final String getClassName() {
		return JavaUtils.getHandle(Attachment.class);
	}
	
	/**
	 * Gets the content.
	 * 
	 * @return the content
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Transient
	public String getContent() throws IOException {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO do this in an appropriate way
			return Strings.join(IOUtils.readLines(new FileInputStream(getFile())).toArray(new String[0]),
			                    FileUtils.lineSeparator);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the encoding.
	 * 
	 * @return the encoding
	 */
	@Basic
	public String getEncoding() {
		return this.encoding;
	}
	
	/**
	 * Gets the entry.
	 * 
	 * @return the entry
	 */
	@OneToOne (cascade = {}, fetch = FetchType.LAZY)
	public AttachmentEntry getEntry() {
		return this.entry;
	}
	
	/**
	 * Gets the file.
	 * 
	 * @return the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Transient
	public synchronized final File getFile() throws IOException {
		PRECONDITIONS: {
			assert (getEntry() != null) || (getArchive() != null);
		}
		
		if (this.file == null) {
			if (getEntry() != null) {
				if (Logger.logDebug()) {
					Logger.debug("Downloading attachment: " + getEntry().getLink());
				}
				final HttpClient httpClient = new DefaultHttpClient();
				URI uri;
				try {
					uri = getEntry().toURI();
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
				this.file = FileUtils.createRandomFile("infozilla_attachment.", "_" + getFilename(),
				                                       FileUtils.FileShutdownAction.KEEP);
				try (InputStream contentInputStream = entity.getContent()) {
					try (OutputStream outputStream = new FileOutputStream(this.file)) {
						org.apache.commons.io.IOUtils.copy(contentInputStream, outputStream);
					}
				}
				final Charset charset = contentType.getCharset();
				setEncoding(charset != null
				                           ? charset.name()
				                           : Charset.defaultCharset().name());
				setFilename(getEntry().getFilename());
			} else if (getArchive() != null) {
				SANITY: {
					assert getPath() != null;
					assert getFilename() != null;
				}
				final File targetDirectory = getArchive().extractedDataDirectory();
				final File targetFile = new File(new File(targetDirectory, getPath()), getFilename());
				
				if (!targetFile.exists()) {
					throw new IOException("Couldn't find archived file: " + getPath() + FileUtils.fileSeparator
					        + getFilename());
				}
				
				this.file = targetFile;
				
				try (FileInputStream inputStream = new FileInputStream(this.file)) {
					try {
						final String encoding = determineEncoding(inputStream);
						
						setEncoding(encoding != null
						                            ? encoding
						                            : ENCODING_UNKNOWN);
					} catch (final EncodingDeterminationException e) {
						setEncoding(ENCODING_UNKNOWN);
					}
				}
			} else {
				assert false;
				throw new RuntimeException("Either AttachmentEntry or Archive defining the origin must be null");
			}
		}
		
		SANITY: {
			assert this.file != null;
		}
		
		try (FileInputStream inputStream = new FileInputStream(this.file)) {
			setMd5(DigestUtils.md5(inputStream));
		}
		
		try (FileInputStream inputStream = new FileInputStream(this.file)) {
			setSha1(DigestUtils.sha(inputStream));
		}
		
		final String mimeType = Files.probeContentType(this.file.toPath());
		setMime(mimeType != null
		                        ? mimeType
		                        : MIMETYPE_UNKNOWN);
		
		POSTCONDITIONS: {
			assert this.file != null;
			assert getMime() != null;
			assert getMd5() != null;
			assert getSha1() != null;
			assert getFilename() != null;
			assert getEncoding() != null;
			assert getPath() != null;
		}
		
		return this.file;
		
	}
	
	/**
	 * Gets the filename.
	 * 
	 * @return the filename
	 */
	@Basic
	public String getFilename() {
		return this.filename;
	}
	
	/**
	 * Gets the generated id.
	 * 
	 * @return the generatedId
	 */
	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE)
	public long getGeneratedId() {
		return this.generatedId;
	}
	
	/**
	 * Gets the md5.
	 * 
	 * @return the md5
	 */
	@Basic
	public byte[] getMd5() {
		return this.md5;
	}
	
	/**
	 * Gets the mime.
	 * 
	 * @return the mime
	 */
	@Basic
	public String getMime() {
		return this.mime;
	}
	
	/**
	 * Gets the path.
	 * 
	 * @return the path
	 */
	@Basic
	public String getPath() {
		return this.path;
	}
	
	/**
	 * Gets the sha1.
	 * 
	 * @return the sha1
	 */
	@Basic
	public byte[] getSha1() {
		return this.sha1;
	}
	
	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	@Enumerated (EnumType.STRING)
	public AttachmentType getType() {
		return this.type;
	}
	
	/**
	 * Sets the archive.
	 * 
	 * @param archive
	 *            the archive to set
	 */
	public void setArchive(final Archive archive) {
		this.archive = archive;
	}
	
	/**
	 * Sets the encoding.
	 * 
	 * @param encoding
	 *            the encoding to set
	 */
	public void setEncoding(final String encoding) {
		this.encoding = encoding;
	}
	
	/**
	 * Sets the entry.
	 * 
	 * @param entry
	 *            the entry to set
	 */
	public void setEntry(final AttachmentEntry entry) {
		this.entry = entry;
	}
	
	/**
	 * Sets the filename.
	 * 
	 * @param filename
	 *            the filename to set
	 */
	public void setFilename(final String filename) {
		this.filename = filename;
	}
	
	/**
	 * Sets the generated id.
	 * 
	 * @param generatedId
	 *            the generatedId to set
	 */
	public void setGeneratedId(final long generatedId) {
		this.generatedId = generatedId;
	}
	
	/**
	 * Sets the md5.
	 * 
	 * @param md5
	 *            the md5 to set
	 */
	public void setMd5(final byte[] md5) {
		this.md5 = md5;
	}
	
	/**
	 * Sets the mime.
	 * 
	 * @param mime
	 *            the mime to set
	 */
	public void setMime(final String mime) {
		this.mime = mime;
	}
	
	/**
	 * Sets the path.
	 * 
	 * @param path
	 *            the path to set
	 */
	public void setPath(final String path) {
		this.path = path;
	}
	
	/**
	 * Sets the sha1.
	 * 
	 * @param sha1
	 *            the sha1 to set
	 */
	public void setSha1(final byte[] sha1) {
		this.sha1 = sha1;
	}
	
	/**
	 * Sets the type.
	 * 
	 * @param type
	 *            the type to set
	 */
	public void setType(final AttachmentType type) {
		this.type = type;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Attachment [filename=");
		builder.append(getFilename());
		builder.append("type=");
		builder.append(getType());
		builder.append(", encoding=");
		builder.append(getEncoding());
		builder.append(", mime=");
		builder.append(getMime());
		builder.append(", md5=");
		builder.append(Arrays.toString(getMd5()));
		builder.append(", sha1=");
		builder.append(Arrays.toString(getSha1()));
		builder.append("]");
		return builder.toString();
	}
	
}
