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
package org.mozkito.versions.subversion;

import java.io.File;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.ownhero.dev.ioda.FileUtils;

import org.apache.commons.lang.StringUtils;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.wc.ISVNDiffGenerator;

import difflib.Chunk;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

/**
 * The Class SubversionDiffParser.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
class SubversionDiffParser implements ISVNDiffGenerator {
	
	/**
	 * Gets the line numbers.
	 * 
	 * @param chunk
	 *            the chunk
	 * @return the line numbers
	 */
	public static HashSet<Integer> getLineNumbers(final Chunk chunk) {
		final HashSet<Integer> result = new HashSet<Integer>();
		final int startPos = chunk.getPosition();
		for (int i = 0; i < chunk.getSize(); ++i) {
			result.add(startPos + i + 1);
		}
		return result;
	}
	
	/**
	 * Lines to string.
	 * 
	 * @param lines
	 *            the lines
	 * @return the string
	 */
	public static String linesToString(final List<String> lines) {
		return StringUtils.join(lines, FileUtils.lineSeparator);
	}
	
	/** The deltas. */
	protected HashSet<Delta> deltas;
	
	/**
	 * Instantiates a new subversion diff parser.
	 */
	public SubversionDiffParser() {
		this.deltas = new HashSet<Delta>();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNDiffGenerator#createTempDirectory()
	 */
	@Override
	public File createTempDirectory() throws SVNException {
		// Auto-generated method stub
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNDiffGenerator#displayAddedDirectory(java .lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void displayAddedDirectory(final String arg0,
	                                  final String arg1,
	                                  final String arg2) throws SVNException {
		// Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNDiffGenerator#displayDeletedDirectory(java .lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void displayDeletedDirectory(final String arg0,
	                                    final String arg1,
	                                    final String arg2) throws SVNException {
		// Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNDiffGenerator#displayFileDiff(java.lang .String, java.io.File, java.io.File,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.io.OutputStream)
	 */
	@Override
	public void displayFileDiff(final String path,
	                            final File file1,
	                            final File file2,
	                            final String rev1,
	                            final String rev2,
	                            final String mimeType1,
	                            final String mimeType2,
	                            final OutputStream result) throws SVNException {
		
		if ((file1 == null) || (file2 == null)) {
			return;
		}
		final List<String> original = FileUtils.fileToLines(file1);
		final List<String> revised = FileUtils.fileToLines(file2);
		final Patch patch = DiffUtils.diff(original, revised);
		final Iterator<Delta> deltaIter = patch.getDeltas().iterator();
		while (deltaIter.hasNext()) {
			this.deltas.add(deltaIter.next());
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNDiffGenerator#displayPropDiff(java.lang .String,
	 * org.tmatesoft.svn.core.SVNProperties, org.tmatesoft.svn.core.SVNProperties, java.io.OutputStream)
	 */
	@Override
	public void displayPropDiff(final String arg0,
	                            final SVNProperties arg1,
	                            final SVNProperties arg2,
	                            final OutputStream arg3) throws SVNException {
		// Auto-generated method stub
		
	}
	
	/**
	 * Gets the deltas.
	 * 
	 * @return the deltas
	 */
	public HashSet<Delta> getDeltas() {
		return this.deltas;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNDiffGenerator#getEncoding()
	 */
	@Override
	public String getEncoding() {
		// Auto-generated method stub
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNDiffGenerator#getEOL()
	 */
	@Override
	public byte[] getEOL() {
		// Auto-generated method stub
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNDiffGenerator#init(java.lang.String, java.lang.String)
	 */
	@Override
	public void init(final String arg0,
	                 final String arg1) {
		// Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNDiffGenerator#isDiffAdded()
	 */
	@Override
	public boolean isDiffAdded() {
		// Auto-generated method stub
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNDiffGenerator#isDiffCopied()
	 */
	@Override
	public boolean isDiffCopied() {
		// Auto-generated method stub
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNDiffGenerator#isDiffDeleted()
	 */
	@Override
	public boolean isDiffDeleted() {
		// Auto-generated method stub
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNDiffGenerator#isDiffUnversioned()
	 */
	@Override
	public boolean isDiffUnversioned() {
		// Auto-generated method stub
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNDiffGenerator#isForcedBinaryDiff()
	 */
	@Override
	public boolean isForcedBinaryDiff() {
		// Auto-generated method stub
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNDiffGenerator#setBasePath(java.io.File)
	 */
	@Override
	public void setBasePath(final File arg0) {
		// Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNDiffGenerator#setDiffAdded(boolean)
	 */
	@Override
	public void setDiffAdded(final boolean arg0) {
		// Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNDiffGenerator#setDiffCopied(boolean)
	 */
	@Override
	public void setDiffCopied(final boolean arg0) {
		// Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNDiffGenerator#setDiffDeleted(boolean)
	 */
	@Override
	public void setDiffDeleted(final boolean arg0) {
		// Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNDiffGenerator#setDiffUnversioned(boolean)
	 */
	@Override
	public void setDiffUnversioned(final boolean arg0) {
		// Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNDiffGenerator#setEncoding(java.lang.String)
	 */
	@Override
	public void setEncoding(final String arg0) {
		// Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNDiffGenerator#setEOL(byte[])
	 */
	@Override
	public void setEOL(final byte[] arg0) {
		// Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNDiffGenerator#setForcedBinaryDiff(boolean)
	 */
	@Override
	public void setForcedBinaryDiff(final boolean arg0) {
		// Auto-generated method stub
		
	}
}
