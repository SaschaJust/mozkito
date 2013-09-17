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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import net.ownhero.dev.kisa.Logger;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

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

import org.mozkito.infozilla.exceptions.EncodingDeterminationException;
import org.mozkito.infozilla.exceptions.MIMETypeDeterminationException;
import org.mozkito.issues.model.AttachmentEntry;
import org.mozkito.persistence.Annotated;
import org.mozkito.utilities.commons.JavaUtils;
import org.mozkito.utilities.io.IOUtils;
import org.mozkito.utilities.io.exceptions.FetchException;
import org.mozkito.utilities.io.exceptions.UnsupportedProtocolException;

/**
 * The Class Attachment.
 */
@Entity
public class Attachment implements Annotated {
	
	/** The Constant MD5_SIZE. */
	private static final int  MD5_SIZE         = 16;
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3626906010864829890L;
	
	/** The Constant SHA1_SIZE. */
	private static final int  SHA1_SIZE        = 32;
	
	/**
	 * Compute m d5.
	 * 
	 * @param data
	 *            the data
	 * @return the byte[]
	 */
	private static byte[] computeMD5(final byte[] data) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			return md.digest(data);
		} catch (final NoSuchAlgorithmException e) {
			if (Logger.logWarn()) {
				Logger.warn(e.getMessage());
			}
			return null;
		}
		
	}
	
	/**
	 * Compute sh a1.
	 * 
	 * @param data
	 *            the data
	 * @return the byte[]
	 */
	private static byte[] computeSHA1(final byte[] data) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA1");
			return md.digest(data);
		} catch (final NoSuchAlgorithmException e) {
			if (Logger.logWarn()) {
				Logger.warn(e.getMessage());
			}
			return null;
		}
	}
	
	/**
	 * Determine encoding.
	 * 
	 * @param data
	 *            the data
	 * @return the string
	 * @throws EncodingDeterminationException
	 *             the encoding determination exception
	 */
	private static String determineEncoding(final byte[] data) throws EncodingDeterminationException {
		try {
			final ContentHandler contenthandler = new BodyContentHandler();
			final Metadata metadata = new Metadata();
			metadata.set(TikaMetadataKeys.RESOURCE_NAME_KEY, "filename");
			final ByteArrayInputStream inputStream = new ByteArrayInputStream(data, 0, data.length);
			final Parser parser = new AutoDetectParser();
			final ParseContext context = new ParseContext();
			context.set(Parser.class, parser);
			
			parser.parse(inputStream, contenthandler, metadata, context);
			
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
	 * Determine mime.
	 * 
	 * @param data
	 *            the data
	 * @return the string
	 * @throws MIMETypeDeterminationException
	 *             the mIME type determination exception
	 */
	private static String determineMIME(final byte[] data) throws MIMETypeDeterminationException {
		MagicMatch match;
		try {
			match = Magic.getMagicMatch(data);
			return match.getMimeType();
		} catch (final MagicParseException e) {
			throw new MIMETypeDeterminationException(e);
		} catch (final MagicMatchNotFoundException e) {
			throw new MIMETypeDeterminationException(e);
		} catch (final MagicException e) {
			throw new MIMETypeDeterminationException(e);
		}
	}
	
	/**
	 * Fetch.
	 * 
	 * @param entry
	 *            the entry
	 * @return the attachment
	 * @throws FetchException
	 *             the fetch exception
	 */
	public static Attachment fetch(final AttachmentEntry entry) throws FetchException {
		try {
			final byte[] data = IOUtils.binaryfetch(entry.toURI());
			
			final Attachment attachment = new Attachment(entry, data);
			
			try {
				attachment.setMime(determineMIME(data));
				attachment.setType(guessType(attachment));
			} catch (final MIMETypeDeterminationException e) {
				if (Logger.logWarn()) {
					Logger.warn("Could not determine MIME type from data for " + entry + ": " + e.getMessage());
				}
			}
			
			try {
				attachment.setEncoding(determineEncoding(data));
			} catch (final EncodingDeterminationException e) {
				if (Logger.logWarn()) {
					Logger.warn("Could not determine encoding from data for " + entry + ": " + e.getMessage());
				}
			}
			
			attachment.setMd5(computeMD5(data));
			attachment.setSha1(computeSHA1(data));
			
			return attachment;
		} catch (final UnsupportedProtocolException e) {
			throw new FetchException(e);
		} catch (final IOException e) {
			throw new FetchException(e);
		}
	}
	
	/**
	 * Guess type.
	 * 
	 * @param attachment
	 *            the attachment
	 * @return the attachment type
	 */
	private static AttachmentType guessType(final Attachment attachment) {
		AttachmentType guessedType = AttachmentType.UNKNOWN;
		
		// Rules for Screenshot
		if (attachment.getMime().contains("image") || attachment.getMime().contains("jpg")
		        || attachment.getMime().contains("gif") || attachment.getMime().contains("jpeg")
		        || attachment.getMime().contains("bmp") || attachment.getMime().contains("png")
		        || attachment.getMime().contains("video")
		        || attachment.getEntry().getDescription().toLowerCase().contains("screenshot")
		        || attachment.getEntry().getFilename().toLowerCase().contains("screenshot")) {
			guessedType = AttachmentType.IMAGE;
		}
		
		// Rules for Patches
		if (attachment.getMime().contains("patch") || attachment.getMime().contains("diff")
		        || attachment.getEntry().getDescription().toLowerCase().contains("patch")
		        || attachment.getEntry().getDescription().toLowerCase().contains("diff")
		        || attachment.getEntry().getFilename().toLowerCase().contains("patch")
		        || attachment.getEntry().getFilename().toLowerCase().contains("diff")) {
			guessedType = AttachmentType.PATCH;
		}
		
		// Rules for Source Code
		if (attachment.getMime().contains("text/java")
		        || attachment.getMime().contains("text/java source")
		        || attachment.getMime().contains("text/x-java")
		        || attachment.getMime().contains("text/x-java source")
		        || (attachment.getEntry().getDescription().toLowerCase().contains("source") && attachment.getEntry()
		                                                                                                 .getDescription()
		                                                                                                 .toLowerCase()
		                                                                                                 .contains("code"))
		        || attachment.getEntry().getFilename().toLowerCase().contains(".java")) {
			guessedType = AttachmentType.SOURCECODE;
		}
		
		// Rules for Stack Traces
		if (attachment.getMime().contains("text/log")
		        || attachment.getMime().contains("text/x-log")
		        || (attachment.getEntry().getDescription().toLowerCase().contains("stack") && attachment.getEntry()
		                                                                                                .getDescription()
		                                                                                                .toLowerCase()
		                                                                                                .contains("trace"))
		        || attachment.getEntry().getFilename().toLowerCase().contains("stacktrace")) {
			guessedType = AttachmentType.STACKTRACE;
		}
		
		return guessedType;
		
	}
	
	/** The data. */
	private byte[]          data;
	
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
	
	/**
	 * Instantiates a new attachment.
	 * 
	 * @param entry
	 *            the entry
	 * @param data
	 *            the data
	 */
	public Attachment(final AttachmentEntry entry, final byte[] data) {
		setEntry(entry);
		setData(data);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.Annotated#getHandle()
	 */
	@Override
	public final String getClassName() {
		return JavaUtils.getHandle(Attachment.class);
	}
	
	/**
	 * Gets the data.
	 * 
	 * @return the data
	 */
	@Basic
	public byte[] getData() {
		return this.data;
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
	 * Sets the data.
	 * 
	 * @param data
	 *            the data to set
	 */
	public void setData(final byte[] data) {
		this.data = data;
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
}
