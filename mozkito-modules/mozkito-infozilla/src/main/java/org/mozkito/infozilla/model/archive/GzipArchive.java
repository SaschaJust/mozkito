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
/**
 * 
 */
package org.mozkito.infozilla.model.archive;

import java.io.File;
import java.io.IOException;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.ioda.exceptions.FilePermissionException;

import org.mozkito.infozilla.model.attachment.Attachment;

/**
 * The Class GzipArchive.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class GzipArchive extends Archive {
	
	/**
	 * Instantiates a new gzip archive.
	 * 
	 * @param attachment
	 *            the attachment
	 */
	public GzipArchive(final Attachment attachment) {
		super(attachment);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.infozilla.model.archive.Archive#extract()
	 */
	@Override
	public File extract() throws IOException, FilePermissionException {
		final File file = FileUtils.createRandomFile(FileShutdownAction.DELETE);
		FileUtils.dump(getAttachment().getData(), file);
		final File dir = FileUtils.createRandomDir("test", "bleh", FileShutdownAction.DELETE);
		FileUtils.gunzip(file, dir);
		return dir;
	}
	
}