/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.subversion;

import java.io.File;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.wc.ISVNDiffGenerator;

import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import difflib.Chunk;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
class SubversionDiffParser implements ISVNDiffGenerator {
	
	public static HashSet<Integer> getLineNumbers(Chunk chunk) {
		HashSet<Integer> result = new HashSet<Integer>();
		int startPos = chunk.getPosition();
		for (int i = 0; i < chunk.getSize(); ++i) {
			result.add(startPos + i + 1);
		}
		return result;
	}
	
	public static String linesToString(List<String> lines) {
		StringBuilder sbuilder = new StringBuilder();
		for (int i = 0; i < lines.size(); ++i) {
			sbuilder.append(lines.get(i));
			sbuilder.append("\n");
		}
		return sbuilder.toString();
	}
	
	protected HashSet<Delta> deltas;
	
	public SubversionDiffParser() {
		this.deltas = new HashSet<Delta>();
	}
	
	@Override
	public File createTempDirectory() throws SVNException {
		// Auto-generated method stub
		return null;
	}
	
	@Override
	public void displayAddedDirectory(String arg0, String arg1, String arg2) throws SVNException {
		// Auto-generated method stub
		
	}
	
	@Override
	public void displayDeletedDirectory(String arg0, String arg1, String arg2) throws SVNException {
		// Auto-generated method stub
		
	}
	
	@Override
	public void displayFileDiff(String path, File file1, File file2, String rev1, String rev2, String mimeType1,
	        String mimeType2, OutputStream result) throws SVNException {
		
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
	
	@Override
	public void displayPropDiff(String arg0, SVNProperties arg1, SVNProperties arg2, OutputStream arg3)
	        throws SVNException {
		// Auto-generated method stub
		
	}
	
	public HashSet<Delta> getDeltas() {
		return this.deltas;
	}
	
	@Override
	public String getEncoding() {
		// Auto-generated method stub
		return null;
	}
	
	@Override
	public byte[] getEOL() {
		// Auto-generated method stub
		return null;
	}
	
	@Override
	public void init(String arg0, String arg1) {
		// Auto-generated method stub
		
	}
	
	@Override
	public boolean isDiffAdded() {
		// Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean isDiffCopied() {
		// Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean isDiffDeleted() {
		// Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean isDiffUnversioned() {
		// Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean isForcedBinaryDiff() {
		// Auto-generated method stub
		return false;
	}
	
	@Override
	public void setBasePath(File arg0) {
		// Auto-generated method stub
		
	}
	
	@Override
	public void setDiffAdded(boolean arg0) {
		// Auto-generated method stub
		
	}
	
	@Override
	public void setDiffCopied(boolean arg0) {
		// Auto-generated method stub
		
	}
	
	@Override
	public void setDiffDeleted(boolean arg0) {
		// Auto-generated method stub
		
	}
	
	@Override
	public void setDiffUnversioned(boolean arg0) {
		// Auto-generated method stub
		
	}
	
	@Override
	public void setEncoding(String arg0) {
		// Auto-generated method stub
		
	}
	
	@Override
	public void setEOL(byte[] arg0) {
		// Auto-generated method stub
		
	}
	
	@Override
	public void setForcedBinaryDiff(boolean arg0) {
		// Auto-generated method stub
		
	}
}
