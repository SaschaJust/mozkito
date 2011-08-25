package net.ownhero.dev.andama.settings;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class URIArgument extends AndamaArgument<URI> {
	
	/**
	 * This is similar to FileArgument but requires the file to be a directory
	 * 
	 * @see de.unisaarland.cs.st.reposuite.settings.AndamaArgument
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
	public URIArgument(final AndamaSettings settings, final String name, final String description,
	        final String defaultValue, final boolean isRequired) {
		super(settings, name, description, defaultValue, isRequired);
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument#getValue()
	 */
	@Override
	public URI getValue() {
		if (this.actualValue == null) {
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
			if (uriRegex.find(this.actualValue) == null) {
				err = "URI does not match regex: " + uriRegex.getPattern();
			} else {
				if (uriRegex.getGroup("scheme") == null) {
					
					if (Logger.logWarn()) {
						Logger.warn("Scheme missing when parsing URI:" + this.actualValue + " Guessing scheme: file.");
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
							err = "`path` part of the URI is not set: " + this.actualValue;
						}
					} else {
						err = "`authority` part of the URI is set, but scheme is missing: " + this.actualValue;
					}
				} else {
					uri = new URI(this.actualValue);
				}
			}
			
			if (err != null) {
				throw new UnrecoverableError("When parsing URI string `" + this.actualValue + "` for argument `"
				        + getName() + "`, the following error occurred: " + err);
			} else {
				return uri;
			}
			
		} catch (URISyntaxException e) {
			throw new UnrecoverableError("When parsing URI string `" + this.actualValue + "` for argument `"
			        + getName() + "`, the following error occurred: " + e.getMessage(), e);
		}
	}
}
