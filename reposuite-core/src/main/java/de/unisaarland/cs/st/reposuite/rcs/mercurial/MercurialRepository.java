/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.mercurial;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.List;

import de.unisaarland.cs.st.reposuite.rcs.AnnotationEntry;
import de.unisaarland.cs.st.reposuite.rcs.LogEntry;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import difflib.Delta;

/**
 * @author just
 * 
 */
/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class MercurialRepository extends Repository {
	
	@Override
	public List<AnnotationEntry> annotate(String filePath, String revision) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public File checkoutPath(String relativeRepoPath, String revision) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Collection<Delta> diff(String filePath, String baseRevision, String revisedRevision) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getFirstRevisionId() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getLastRevisionId() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<LogEntry> log() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setup(URI address) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setup(URI address, String username, String password) {
		// TODO Auto-generated method stub
		
	}
	
}
