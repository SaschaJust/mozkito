/**
 * 
 */
package net.ownhero.dev.andama.threads.nodes;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaSource;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class TextSource extends AndamaSource<String> {
	
	public TextSource(final AndamaGroup threadGroup, final AndamaSettings settings, final boolean parallelizable,
	        final File file) {
		super(threadGroup, settings, parallelizable);
		// TODO process File
		// FILE (txt, zip, gz, bz2, 7z, tar)
	}
	
	public TextSource(final AndamaGroup threadGroup, final AndamaSettings settings, final boolean parallelizable,
	        final InputStream stream) {
		super(threadGroup, settings, parallelizable);
		// TODO process stream
	}
	
	public TextSource(final AndamaGroup threadGroup, final AndamaSettings settings, final boolean parallelizable,
	        final URI uri) {
		super(threadGroup, settings, parallelizable);
		// TODO process URI
		// HTTP, HTTPS, FTP, FTPS, FILE
	}
	
	public TextSource(final AndamaGroup threadGroup, final AndamaSettings settings, final boolean parallelizable,
	        final URL url) {
		super(threadGroup, settings, parallelizable);
		// TODO process URL
		// HTTP, HTTPS, FTP, FTPS, FILE
		
		org.dom4j.DocumentFactory.getInstance().createDocument();
	}
	
}
