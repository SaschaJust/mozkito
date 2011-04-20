/**
 * 
 */
package de.unisaarland.cs.st.reposuite.infozilla.model.archive;

import java.io.File;
import java.io.IOException;

import de.unisaarland.cs.st.reposuite.infozilla.model.attachment.Attachment;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.FileUtils.FileShutdownAction;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class LZMAArchive extends Archive {
	
	public LZMAArchive(final Attachment attachment) {
		super(attachment);
	}
	
	@Override
	public File extract() throws IOException {
		File file = FileUtils.createRandomFile(FileShutdownAction.DELETE);
		FileUtils.dump(getAttachment().getData(), file);
		File dir = FileUtils.createRandomDir("test", "bleh", FileShutdownAction.DELETE);
		FileUtils.unlzma(file, dir);
		return dir;
	}
	
}
