/**
 * 
 */
package de.unisaarland.cs.st.reposuite.infozilla.model.archive;

import java.io.File;
import java.io.IOException;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import de.unisaarland.cs.st.reposuite.infozilla.model.attachment.Attachment;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class ZipArchive extends Archive {
	
	/**
	 * 
	 */
	public ZipArchive(final Attachment attachment) {
		super(attachment);
	}
	
	@Override
	public File extract() throws IOException {
		File file = FileUtils.createRandomFile(FileShutdownAction.DELETE);
		FileUtils.dump(getAttachment().getData(), file);
		File dir = FileUtils.createRandomDir("test", "bleh", FileShutdownAction.DELETE);
		FileUtils.unzip(file, dir);
		return dir;
	}
	
}
