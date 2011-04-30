package de.unisaarland.cs.st.reposuite.settings;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import de.unisaarland.cs.st.reposuite.exceptions.Shutdown;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Regex;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class URIArgument extends RepoSuiteArgument {
	
	/**
	 * This is similar to FileArgument but requires the file to be a directory
	 * 
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument
	 * 
	 * @param settings
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param isRequired
	 * @param create
	 *            Attempts to create directory if not exist
	 * @throws DuplicateArgumentException
	 */
	public URIArgument(final RepoSuiteSettings settings, final String name, final String description,
	        final String defaultValue, final boolean isRequired) {
		super(settings, name, description, defaultValue, isRequired);
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument#getValue()
	 */
	@Override
	public URI getValue() {
		if (this.stringValue == null) {
			return null;
		}
		
		URI uri = null;
		// see RFC2396
		// String lowalpha = "a-z";
		// String upalpha = "A-Z";
		// String alpha = lowalpha + upalpha;
		// String digit = "0-9";
		// String alphanum = alpha + digit;
		// String reserved = ";/?:@&=+$,";
		// String mark = "-_.!Z*'()";
		// String unreserved = alphanum + mark;
		// String hex = digit + "A-Fa-f";
		// String escaped = "%" + "[" + hex + "]{2}";
		// String delims = "<>#%\"";
		// String unwise = "{}|\\\\^\\[\\]`";
		//
		// String reg_name = "[" + unreserved + escaped + "$,;:@&=+]+";
		// String userinfo = unreserved + escaped + ";:&=+$,";
		// String port = "[" + digit + "]*";
		// String domainlabel = "([" + alphanum + "]|[" + alphanum + "][" +
		// alphanum + "-]*["+ alphanum + "])";
		// String toplabel = "([" + alpha + "]|[" + alpha + "][" + alphanum +
		// "-]*["+ alphanum + "])";
		// String hostname = "(" + domainlabel + "\\.)*" + toplabel+ "\\.?";
		// String ipv4address = "[" + digit + "]+\\." + "[" + digit + "]+\\." +
		// "[" + digit + "]+\\." + "[" + digit + "]+";
		// String host = "({host}" + hostname + "|" + ipv4address + ")";
		// String hostport = host + "(:" + port +")?";
		// String server = "[" + userinfo +"]?" + hostport;
		//
		// String authority = "({authority}" + server + reg_name + ")";
		// String scheme = "({scheme}[" + alpha + "]" + "[" + alpha + digit +
		// "+-." + "])";
		// String path = "({path}" + abs_path + "|" + opaque_part + ")";
		// String query = "({query})";
		//
		// Regex uriRegex = new Regex(scheme + ":" +authority+path+"\\?" +query
		// );
		
		String err = null;
		Regex uriRegex = new Regex(
		                           "^(({scheme}[^:/?#]+):)?(//({authority}[^/?#]*))?({path}[^?#]*)(\\?({query}[^#]*))?(#({fragment}.*))?");
		try {
			if (uriRegex.find(this.stringValue) == null) {
				err = "URI does not match regex: " + uriRegex.getPattern();
			} else {
				if (uriRegex.getGroup("scheme") == null) {
					
					if (Logger.logWarn()) {
						Logger.warn("Scheme missing when parsing URI:" + this.stringValue + " Guessing scheme: file.");
					}
					
					if (uriRegex.getGroup("authority") == null) {
						if (uriRegex.getGroup("path") != null) {
							// guess file
							File file = new File(uriRegex.getGroup("path"));
							if (file.exists() && file.canRead()) {
								
								if (Logger.logInfo()) {
									Logger.info("Found readable " + (file.isDirectory()
									                                                   ? "directory"
									                                                   : "file") + " at location: "
									        + file.getAbsolutePath());
								}
								uri = new URI("file", null, file.getAbsolutePath(), null);
							} else {
								err = "Local path does not reference an existing, readable file/dir: "
								        + file.getAbsolutePath();
							}
						} else {
							err = "`path` part of the URI is not set: " + this.stringValue;
						}
					} else {
						err = "`authority` part of the URI is set, but scheme is missing: " + this.stringValue;
					}
				} else {
					uri = new URI(this.stringValue);
				}
			}
			
			if (err != null) {
				if (Logger.logError()) {
					Logger.error("When parsing URI string `" + this.stringValue + "` for argument `" + getName()
					        + "`, the following error occurred: " + err);
				}
				throw new Shutdown();
			} else {
				return uri;
			}
		} catch (URISyntaxException e) {
			if (Logger.logError()) {
				Logger.error("When parsing URI string `" + this.stringValue + "` for argument `" + getName()
				        + "`, the following error occurred: " + e.getMessage());
			}
			throw new Shutdown();
		}
	}
}
