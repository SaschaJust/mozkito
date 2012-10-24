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

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.wc.ISVNDiffGenerator;

import net.ownhero.dev.ioda.FileUtils;
import difflib.Chunk;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
class SubversionDiffParser implements ISVNDiffGenerator {
	
	/**
	 * @param chunk
	 * @return
	 */
	public static HashSet<Integer> getLineNumbers(Chunk chunk) {
		HashSet<Integer> result = new HashSet<Integer>();
		int startPos = chunk.getPosition();
		for (int i = 0; i < chunk.getSize(); ++i) {
			result.add(startPos + i + 1);
		}
		return result;
	}
	
	/**
	 * @param lines
	 * @return
	 */
	public static String linesToString(List<String> lines) {
		StringBuilder sbuilder = new StringBuilder();
		for (int i = 0; i < lines.size(); ++i) {
			sbuilder.append(lines.get(i));
			sbuilder.append("\n");
		}
		return sbuilder.toString();
	}
	
	protected HashSet<Delta> deltas;
	
	/**
	 * 
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
	public void displayAddedDirectory(String arg0,
	                                  String arg1,
	                                  String arg2) throws SVNException {
		// Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNDiffGenerator#displayDeletedDirectory(java .lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void displayDeletedDirectory(String arg0,
	                                    String arg1,
	                                    String arg2) throws SVNException {
		// Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNDiffGenerator#displayFileDiff(java.lang .String, java.io.File, java.io.File,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.io.OutputStream)
	 */
	@Override
	public void displayFileDiff(String path,
	                            File file1,
	                            File file2,
	                            String rev1,
	                            String rev2,
	                            String mimeType1,
	                            String mimeType2,
	                            OutputStream result) throws SVNException {
		
		if ((file1 == null) || (file2 == null)) {
			return;
		}
		List<String> original = FileUtils.fileToLines(file1);
		List<String> revised = FileUtils.fileToLines(file2);
		Patch patch = DiffUtils.diff(original, revised);
		Iterator<Delta> deltaIter = patch.getDeltas().iterator();
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
	public void displayPropDiff(String arg0,
	                            SVNProperties arg1,
	                            SVNProperties arg2,
	                            OutputStream arg3) throws SVNException {
		// Auto-generated method stub
		
	}
	
	/**
	 * @return
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
	public void init(String arg0,
	                 String arg1) {
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
	public void setBasePath(File arg0) {
		// Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNDiffGenerator#setDiffAdded(boolean)
	 */
	@Override
	public void setDiffAdded(boolean arg0) {
		// Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNDiffGenerator#setDiffCopied(boolean)
	 */
	@Override
	public void setDiffCopied(boolean arg0) {
		// Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNDiffGenerator#setDiffDeleted(boolean)
	 */
	@Override
	public void setDiffDeleted(boolean arg0) {
		// Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNDiffGenerator#setDiffUnversioned(boolean)
	 */
	@Override
	public void setDiffUnversioned(boolean arg0) {
		// Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNDiffGenerator#setEncoding(java.lang.String)
	 */
	@Override
	public void setEncoding(String arg0) {
		// Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNDiffGenerator#setEOL(byte[])
	 */
	@Override
	public void setEOL(byte[] arg0) {
		// Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNDiffGenerator#setForcedBinaryDiff(boolean)
	 */
	@Override
	public void setForcedBinaryDiff(boolean arg0) {
		// Auto-generated method stub
		
	}
}
