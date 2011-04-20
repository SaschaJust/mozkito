package de.unisaarland.cs.st.reposuite.infozilla.model;

import java.io.IOException;
import java.net.URISyntaxException;
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
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.AttachmentEntry;
import de.unisaarland.cs.st.reposuite.exceptions.FetchException;
import de.unisaarland.cs.st.reposuite.exceptions.UnsupportedProtocolException;
import de.unisaarland.cs.st.reposuite.infozilla.exceptions.EncodingDeterminationException;
import de.unisaarland.cs.st.reposuite.infozilla.exceptions.MIMETypeDeterminationException;
import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.utils.IOUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;

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
	 * @param data
	 * @return
	 * @throws EncodingDeterminationException
	 */
	private static String determineEncoding(final byte[] data) throws EncodingDeterminationException {
		try {
			ContentHandler contenthandler = new BodyContentHandler();
			Metadata metadata = new Metadata();
			metadata.set(Metadata.RESOURCE_NAME_KEY, "filename");
			ByteInputStream inputStream = new ByteInputStream(data, 0);
			Parser parser = new AutoDetectParser();
			
			parser.parse(inputStream, contenthandler, metadata);
			
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
			byte data[] = IOUtils.binaryfetch(entry.getLink().toURI());
			
			Attachment attachment = new Attachment(entry, data);
			
			try {
				attachment.setMime(determineMIME(data));
				attachment.setType(guessType(attachment.getMime()));
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
		} catch (URISyntaxException e) {
			throw new FetchException(e);
		}
		return null;
	}
	
	/**
	 * @param attachment
	 * @return
	 */
	private static Attachable createAttachable(Attachment attachment) {
	    // TODO Auto-generated method stub
	    return null;
    }

	/**
	 * @param mime
	 * @return
	 */
	private static AttachmentType guessType(final String mime) {
		// TODO Auto-generated method stub
		return null;
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

	/**
     * @param attachable the attachable to set
     */
    public void setAttachable(Attachable attachable) {
	    this.attachable = attachable;
    }

	/**
     * @return the attachable
     */
    public Attachable getAttachable() {
	    return attachable;
    }
}
