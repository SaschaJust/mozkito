package de.unisaarland.cs.st.reposuite.rcs;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;

import difflib.Delta;

/**
 * The Class Repository.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public abstract class Repository {
	
	/**
	 * Annotate file specified by file path at given revision.
	 * 
	 * @param filePath
	 *            the file path to be annotated
	 * @param revision
	 *            the revision the file path will be annotated in
	 * @return Map of lines -> AnnotationEntry
	 */
	public abstract HashMap<Integer, AnnotationEntry> annotate(String filePath, String revision);
	
	/**
	 * Checks out the given relative path in repository and returns the file
	 * handle to the checked out file. If the path is a directory, the file
	 * handle will point to the specified directory. If the relative path points
	 * to a file, the file handle will do so too.
	 * 
	 * @param relativeRepoPath
	 *            the relative repo path
	 * @param revision
	 *            the revision
	 * @return The file handle to the checked out, corresponding file or
	 *         directory.
	 */
	public abstract File checkoutPath(String relativeRepoPath, String revision);
	
	/**
	 * Diff the file in the repository specified by filePath.
	 * 
	 * @param filePath
	 *            the file path to be analyzed. This path must be relative.
	 * @param baseRevision
	 *            the revision used as basis for comparison.
	 * @param revisedRevision
	 *            the revised revision
	 * @return Collection of deltas found between two revision
	 */
	public abstract Collection<Delta> diff(String filePath, String baseRevision, String revisedRevision);
	
	/**
	 * Gets the first revision of the repository.
	 * 
	 * @return the first revision id
	 */
	public abstract String getFirstRevisionId();
	
	/**
	 * Gets the last revision of the repository.
	 * 
	 * @return the last revision id
	 */
	public abstract String getLastRevisionId();
	
	/**
	 * Connect to repository at URI address.
	 * 
	 * @param address
	 *            the address the repository can be found
	 */
	public abstract void setup(URI address);
	
	/**
	 * Connect to repository at URI address using user name and password.
	 * 
	 * @param address
	 *            the address
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 */
	public abstract void setup(URI address, String username, String password);
	
}
