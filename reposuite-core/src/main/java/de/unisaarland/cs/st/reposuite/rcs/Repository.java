package de.unisaarland.cs.st.reposuite.rcs;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Collection;
import java.util.List;

import de.unisaarland.cs.st.reposuite.exceptions.InvalidProtocolType;
import de.unisaarland.cs.st.reposuite.exceptions.InvalidRepositoryURI;
import de.unisaarland.cs.st.reposuite.exceptions.UnsupportedProtocolType;
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
	 * @return List of AnnotationEntry for all lines starting by first line
	 */
	public abstract List<AnnotationEntry> annotate(String filePath, String revision);
	
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
	 * determines the simple classname of the object
	 * 
	 * @return this.getClass().getSimpleName();
	 */
	public String getHandle() {
		return this.getClass().getSimpleName();
	}
	
	/**
	 * Gets the last revision of the repository.
	 * 
	 * @return the last revision id
	 */
	public abstract String getLastRevisionId();
	
	/**
	 * Extract a log from the repository.
	 * 
	 * @return the list of log entries. The first entry is the newest log entry.
	 */
	public abstract List<LogEntry> log();
	
	/**
	 * Connect to repository at URI address.
	 * 
	 * @param address
	 *            the address the repository can be found
	 * @throws InvalidRepositoryURI
	 * @throws InvalidProtocolType
	 * @throws MalformedURLException
	 * @throws UnsupportedProtocolType
	 */
	public abstract void setup(URI address) throws MalformedURLException, InvalidProtocolType, InvalidRepositoryURI,
	        UnsupportedProtocolType;
	
	/**
	 * Connect to repository at URI address using user name and password.
	 * 
	 * @param address
	 *            the address
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @throws MalformedURLException
	 * @throws InvalidProtocolType
	 * @throws InvalidRepositoryURI
	 * @throws UnsupportedProtocolType
	 */
	public abstract void setup(URI address, String username, String password) throws MalformedURLException,
	        InvalidProtocolType, InvalidRepositoryURI, UnsupportedProtocolType;
	
}
