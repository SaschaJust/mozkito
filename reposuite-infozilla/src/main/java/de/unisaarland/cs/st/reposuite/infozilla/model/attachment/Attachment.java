package de.unisaarland.cs.st.reposuite.infozilla.model.attachment;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.AttachmentEntry;
import de.unisaarland.cs.st.reposuite.exceptions.FetchException;
import de.unisaarland.cs.st.reposuite.exceptions.UnsupportedProtocolException;
import de.unisaarland.cs.st.reposuite.infozilla.exceptions.EncodingDeterminationException;
import de.unisaarland.cs.st.reposuite.infozilla.exceptions.MIMETypeDeterminationException;
import de.unisaarland.cs.st.reposuite.infozilla.model.Attachable;
import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.utils.IOUtils;
import net.ownhero.dev.kisa.Logger;

@Entity
public class Attachment implements Annotated {
	
	private static final long serialVersionUID = 3626906010864829890L;
	
	/**
	 * @param data
	 * @return
	 */
	private static byte[] computeMD5(final byte[] data) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			return md.digest(data);
		} catch (NoSuchAlgorithmException e) {
			if (Logger.logWarn()) {
				Logger.warn(e.getMessage());
			}
			return null;
		}
		
	}
	
	/**
	 * @param data
	 * @return
	 */
	private static byte[] computeSHA1(final byte[] data) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA1");
			return md.digest(data);
		} catch (NoSuchAlgorithmException e) {
			if (Logger.logWarn()) {
				Logger.warn(e.getMessage());
			}
			return null;
		}
	}
	
	/**
	 * @param attachment
	 * @return
	 */
	private static Attachable createAttachable(final Attachment attachment) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * @param data
	 * @return
	 * @throws EncodingDeterminationException
	 */
	private static String determineEncoding(final byte[] data) throws EncodingDeterminationException {
		try {
			ContentHandler contenthandler = new BodyContentHandler();
			Metadata metadata = new Metadata();
			metadata.set(Metadata.RESOURCE_NAME_KEY, "filename");
			ByteArrayInputStream inputStream = new ByteArrayInputStream(data, 0, data.length);
			Parser parser = new AutoDetectParser();
			ParseContext context = new ParseContext();
			context.set(Parser.class, parser);
			
			parser.parse(inputStream, contenthandler, metadata, context);
			
			return metadata.get(Metadata.CONTENT_ENCODING);
		} catch (IOException e) {
			throw new EncodingDeterminationException(e);
		} catch (SAXException e) {
			throw new EncodingDeterminationException(e);
		} catch (TikaException e) {
			throw new EncodingDeterminationException(e);
		}
	}
	
	/**
	 * @param data
	 * @return
	 * @throws MIMETypeDeterminationException
	 */
	private static String determineMIME(final byte[] data) throws MIMETypeDeterminationException {
		MagicMatch match;
		try {
			match = Magic.getMagicMatch(data);
			return match.getMimeType();
		} catch (MagicParseException e) {
			throw new MIMETypeDeterminationException(e);
		} catch (MagicMatchNotFoundException e) {
			throw new MIMETypeDeterminationException(e);
		} catch (MagicException e) {
			throw new MIMETypeDeterminationException(e);
		}
	}
	
	/**
	 * @param entry
	 * @return
	 * @throws FetchException
	 */
	public static Attachment fetch(final AttachmentEntry entry) throws FetchException {
		try {
			byte data[] = IOUtils.binaryfetch(entry.toURI());
			
			Attachment attachment = new Attachment(entry, data);
			
			try {
				attachment.setMime(determineMIME(data));
				attachment.setType(guessType(attachment));
			} catch (MIMETypeDeterminationException e) {
				if (Logger.logWarn()) {
					Logger.warn("Could not determine MIME type from data for " + entry + ": " + e.getMessage());
				}
			}
			
			try {
				attachment.setEncoding(determineEncoding(data));
			} catch (EncodingDeterminationException e) {
				if (Logger.logWarn()) {
					Logger.warn("Could not determine encoding from data for " + entry + ": " + e.getMessage());
				}
			}
			
			attachment.setMd5(computeMD5(data));
			attachment.setSha1(computeSHA1(data));
			
			attachment.setAttachable(createAttachable(attachment));
		} catch (UnsupportedProtocolException e) {
			throw new FetchException(e);
		}
		return null;
	}
	
	/**
	 * @param mime
	 * @return
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
	
	private byte[]          data;
	private String          encoding;
	private AttachmentEntry entry;
	private String          filename;
	private long            generatedId;
	private String          mime;
	private AttachmentType  type = AttachmentType.UNKNOWN;
	private byte[]          md5  = new byte[16];
	private byte[]          sha1 = new byte[32];
	private Attachable      attachable;
	
	public Attachment(final AttachmentEntry entry, final byte[] data) {
		setEntry(entry);
		setData(data);
	}
	
	/**
	 * @return the attachable
	 */
	public Attachable getAttachable() {
		return this.attachable;
	}
	
	/**
	 * @return the data
	 */
	public byte[] getData() {
		return this.data;
	}
	
	/**
	 * @return the encoding
	 */
	public String getEncoding() {
		return this.encoding;
	}
	
	/**
	 * @return the entry
	 */
	@OneToOne (cascade = {}, fetch = FetchType.LAZY)
	public AttachmentEntry getEntry() {
		return this.entry;
	}
	
	/**
	 * @return the filename
	 */
	public String getFilename() {
		return this.filename;
	}
	
	/**
	 * @return the generatedId
	 */
	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE)
	public long getGeneratedId() {
		return this.generatedId;
	}
	
	/**
	 * @return the md5 
	 */
	public byte[] getMd5() {
		return this.md5;
	}
	
	/**
	 * @return the mime
	 */
	public String getMime() {
		return this.mime;
	}
	
	/**
	 * @return the sha1
	 */
	public byte[] getSha1() {
		return this.sha1;
	}
	
	/**
	 * @return the type
	 */
	@Enumerated (EnumType.ORDINAL)
	public AttachmentType getType() {
		return this.type;
	}
	
	/**
	 * @param attachable the attachable to set
	 */
	public void setAttachable(final Attachable attachable) {
		this.attachable = attachable;
	}
	
	/**
	 * @param data the data to set
	 */
	public void setData(final byte[] data) {
		this.data = data;
	}
	
	/**
	 * @param encoding the encoding to set
	 */
	public void setEncoding(final String encoding) {
		this.encoding = encoding;
	}
	
	/**
	 * @param entry the entry to set
	 */
	public void setEntry(final AttachmentEntry entry) {
		this.entry = entry;
	}
	
	/**
	 * @param filename the filename to set
	 */
	public void setFilename(final String filename) {
		this.filename = filename;
	}
	
	/**
	 * @param generatedId the generatedId to set
	 */
	public void setGeneratedId(final long generatedId) {
		this.generatedId = generatedId;
	}
	
	/**
	 * @param md5 the md5 to set
	 */
	public void setMd5(final byte[] md5) {
		this.md5 = md5;
	}
	
	/**
	 * @param mime the mime to set
	 */
	public void setMime(final String mime) {
		this.mime = mime;
	}
	
	/**
	 * @param sha1 the sha1 to set
	 */
	public void setSha1(final byte[] sha1) {
		this.sha1 = sha1;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(final AttachmentType type) {
		this.type = type;
	}
}
