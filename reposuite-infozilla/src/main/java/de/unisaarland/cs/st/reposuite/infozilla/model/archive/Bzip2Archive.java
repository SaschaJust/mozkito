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
public class Bzip2Archive extends Archive {
	
	public Bzip2Archive(final Attachment attachment) {
		super(attachment);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public File extract() throws IOException {
		File file = FileUtils.createRandomFile(FileShutdownAction.DELETE);
		FileUtils.dump(getAttachment().getData(), file);
		File dir = FileUtils.createRandomDir("test", "bleh", FileShutdownAction.DELETE);
		FileUtils.bunzip2(file, dir);
		return dir;
	}
	
}
