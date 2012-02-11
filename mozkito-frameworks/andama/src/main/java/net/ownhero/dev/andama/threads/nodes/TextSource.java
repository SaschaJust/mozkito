/**
 * 
 */
package net.ownhero.dev.andama.threads.nodes;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import net.ownhero.dev.andama.settings.Settings;
import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.Source;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class TextSource extends Source<String> {
	
	public TextSource(final Group threadGroup, final Settings settings, final boolean parallelizable,
	        final File file) {
		super(threadGroup, settings, parallelizable);
		// TODO process File
		// FILE (txt, zip, gz, bz2, 7z, tar)
		addHooks();
	}
	
	public TextSource(final Group threadGroup, final Settings settings, final boolean parallelizable,
	        final InputStream stream) {
		super(threadGroup, settings, parallelizable);
		// TODO process stream
		addHooks();
	}
	
	public TextSource(final Group threadGroup, final Settings settings, final boolean parallelizable,
	        final URI uri) {
		super(threadGroup, settings, parallelizable);
		// TODO process URI
		// HTTP, HTTPS, FTP, FTPS, FILE
		addHooks();
	}
	
	public TextSource(final Group threadGroup, final Settings settings, final boolean parallelizable,
	        final URL url) {
		super(threadGroup, settings, parallelizable);
		// TODO process URL
		// HTTP, HTTPS, FTP, FTPS, FILE
		addHooks();
	}
	
	private void addHooks() {
		
	}
	
	/**
	 * @param url
	 * @return
	 */
	@SuppressWarnings ("unused")
	private boolean checkAvailability(final URL url) {
		return true;
	}
	
}
