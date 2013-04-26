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
package org.mozkito.versions;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import difflib.Delta;

import org.mozkito.persons.elements.PersonFactory;
import org.mozkito.persons.model.Person;
import org.mozkito.utilities.commons.JavaUtils;
import org.mozkito.versions.elements.AnnotationEntry;
import org.mozkito.versions.elements.ChangeType;
import org.mozkito.versions.elements.LogEntry;
import org.mozkito.versions.elements.LogIterator;
import org.mozkito.versions.elements.RevDependencyGraph;
import org.mozkito.versions.exceptions.RepositoryOperationException;
import org.mozkito.versions.mercurial.MercurialRepository;
import org.mozkito.versions.model.ChangeSet;

/**
 * The Class Repository. Every repository connector that extends this class has to be named [Repotype]Repository. E.g.
 * DarksRepository. Additionally it is mandatory to add a new enum constant in {@link RepositoryType}.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 * 
 */
public abstract class Repository {
	
	/** The uri. */
	private URI                 uri;
	
	/** The start revision. */
	private String              startRevision;
	
	/** The end revision. */
	private String              endRevision;
	
	/** The start transaction. */
	private ChangeSet           startTransaction = null;
	
	/** The main branch name. */
	private String              mainBranchName;
	
	private final PersonFactory personFactory;
	
	/** The Constant unknownPerson. */
	public final Person         UNKNOWN_PERSON;
	
	/**
	 * Instantiates a new repository.
	 * 
	 * @param personFactory
	 *            the person factory
	 */
	public Repository(@NotNull final PersonFactory personFactory) {
		this.personFactory = personFactory;
		assert this.personFactory != null;
		this.UNKNOWN_PERSON = this.personFactory.get("<unknown>", null, null); //$NON-NLS-1$
	}
	
	/**
	 * Annotate file specified by file path at given revision.
	 * 
	 * @param filePath
	 *            the file path to be annotated
	 * @param revision
	 *            the revision the file path will be annotated in
	 * @return List of AnnotationEntry for all lines starting by first line
	 * @throws RepositoryOperationException
	 *             the repository operation exception
	 */
	public abstract List<AnnotationEntry> annotate(String filePath,
	                                               String revision) throws RepositoryOperationException;
	
	/**
	 * Checks out the given relative path in repository and returns the file handle to the checked out file. If the path
	 * is a directory, the file handle will point to the specified directory. If the relative path points to a file, the
	 * file handle will do so too.
	 * 
	 * @param relativeRepoPath
	 *            the relative repository path
	 * @param revision
	 *            the revision
	 * @return The file handle to the checked out, corresponding file or directory. Null if no such file exists or if
	 *         the file could not be checked out.
	 * @throws RepositoryOperationException
	 *             the repository operation exception
	 */
	public abstract File checkoutPath(String relativeRepoPath,
	                                  String revision) throws RepositoryOperationException;
	
	/**
	 * Diff the file in the repository specified by filePath.
	 * 
	 * @param filePath
	 *            the file path to be analyzed. This path must be relative to the repository root and point to a file
	 *            but not to a directory.
	 * @param baseRevision
	 *            the revision used as basis for comparison.
	 * @param revisedRevision
	 *            the revised revision
	 * @return Collection of deltas found between two revision
	 * @throws RepositoryOperationException
	 *             the repository operation exception
	 */
	public abstract Collection<Delta> diff(String filePath,
	                                       String baseRevision,
	                                       String revisedRevision) throws RepositoryOperationException;
	
	/**
	 * Gather tool information.
	 * 
	 * @return a string containing information about the instrumented library/tool (e.g. version, ...)
	 */
	public abstract String gatherToolInformation();
	
	/**
	 * Gets the files that changed within the corresponding transaction.
	 * 
	 * @param revision
	 *            the revision to be analyzed
	 * @return A map associating the changed paths with a ChangeType. Returns Null if an error occurrs.
	 * @throws RepositoryOperationException
	 *             the repository operation exception
	 */
	public abstract Map<String, ChangeType> getChangedPaths(String revision) throws RepositoryOperationException;
	
	/**
	 * Gets the transaction count.
	 * 
	 * @return the total number of revisions in the repository, -1 if error occured
	 * @throws RepositoryOperationException
	 *             the repository operation exception
	 */
	public abstract long getChangeSetCount() throws RepositoryOperationException;
	
	/**
	 * Returns the transaction id string to the transaction determined by the given index.
	 * 
	 * @param index
	 *            Starts at 0
	 * @return the corresponding transaction id (e.g. for reposuite {@link MercurialRepository#getChangeSetId(long)}
	 *         returns 021e7e97724b for 3.
	 * @throws RepositoryOperationException
	 *             the repository operation exception
	 */
	public abstract String getChangeSetId(long index) throws RepositoryOperationException;
	
	/**
	 * Method to retrieve the index of a transaction id within the topological order.
	 * getChangeSetId(getTransactionIndex("abc")).equals("abc")
	 * 
	 * @param changeSetId
	 *            the transaction id
	 * @return the transaction index; return -1 if the changeSetId does not exist
	 * @throws RepositoryOperationException
	 *             the repository operation exception
	 */
	public abstract long getChangeSetIndex(String changeSetId) throws RepositoryOperationException;
	
	/**
	 * Determines the simple class name of the object.
	 * 
	 * @return this.getClass().getSimpleName();
	 */
	public String getClassName() {
		return JavaUtils.getHandle(Repository.class);
	}
	
	/**
	 * Get the last revision to be considered.
	 * 
	 * @return the endRevision
	 */
	public String getEndRevision() {
		return this.endRevision;
	}
	
	/**
	 * Gets the first revision of the repository.
	 * 
	 * @return the first revision id
	 * @throws RepositoryOperationException
	 *             the repository operation exception
	 */
	public abstract String getFirstRevisionId() throws RepositoryOperationException;
	
	/**
	 * Determines the former path name of the file/directory.
	 * 
	 * @param revision
	 *            (not null)
	 * @param pathName
	 *            (not null)
	 * @return Returns the former path name iff the file/directory was renamed. Null otherwise.
	 * @throws RepositoryOperationException
	 *             the repository operation exception
	 */
	public abstract String getFormerPathName(String revision,
	                                         String pathName) throws RepositoryOperationException;
	
	/**
	 * Gets the hEAD.
	 * 
	 * @return the hEAD
	 */
	public String getHEAD() {
		return "HEAD";
	}
	
	/**
	 * Gets the last revision of the repository.
	 * 
	 * @return the last revision id
	 * @throws RepositoryOperationException
	 *             the repository operation exception
	 */
	public abstract String getHEADRevisionId() throws RepositoryOperationException;
	
	/**
	 * Gets the main branch name.
	 * 
	 * @return the main branch name
	 */
	public String getMainBranchName() {
		return this.mainBranchName;
	}
	
	/**
	 * @return the personFactory
	 */
	public final PersonFactory getPersonFactory() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.personFactory;
		} finally {
			POSTCONDITIONS: {
				Condition.notNull(this.personFactory,
				                  "Field '%s' in '%s'.", "personFactory", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	/**
	 * Gets the repository type.
	 * 
	 * @return the {@link RepositoryType} of the connector class determined by naming convention. See the java-doc of
	 * @author Sascha Just <sascha.just@mozkito.org> {@link Repository} for details.
	 */
	public final RepositoryType getRepositoryType() {
		return RepositoryType.valueOf(this.getClass()
		                                  .getSimpleName()
		                                  .substring(0,
		                                             this.getClass().getSimpleName().length()
		                                                     - Repository.class.getSimpleName().length()).toUpperCase());
	}
	
	/**
	 * Gets the rev dependency graph.
	 * 
	 * @return the rev dependency graph
	 * @throws RepositoryOperationException
	 *             the repository operation exception
	 */
	public abstract RevDependencyGraph getRevDependencyGraph() throws RepositoryOperationException;
	
	/**
	 * Gets the start revision.
	 * 
	 * @return the startRevision
	 */
	public String getStartRevision() {
		return this.startRevision;
	}
	
	/**
	 * Gets the start transaction.
	 * 
	 * @return the startTransaction
	 */
	public ChangeSet getStartTransaction() {
		return this.startTransaction;
	}
	
	/**
	 * @return the uNKNOWN_PERSON
	 */
	public final Person getUNKNOWN_PERSON() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.UNKNOWN_PERSON;
		} finally {
			POSTCONDITIONS: {
				Condition.notNull(this.UNKNOWN_PERSON,
				                  "Field '%s' in '%s'.", "UNKNOWN_PERSON", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	/**
	 * Gets the uri.
	 * 
	 * @return the uri
	 */
	public URI getUri() {
		return this.uri;
	}
	
	/**
	 * Returns the path of the directory that contains the local copy/clone/checkout of the repository (the working
	 * copy).
	 * 
	 * @return the woking copy location
	 * @throws RepositoryOperationException
	 *             the repository operation exception
	 */
	public abstract File getWorkingCopyLocation() throws RepositoryOperationException;
	
	/**
	 * Extract a log from the repository.
	 * 
	 * @param fromRevision
	 *            the from revision
	 * @param toRevision
	 *            the to revision
	 * @return the list of log entries. The first entry is the oldest log entry.
	 * @throws RepositoryOperationException
	 *             the repository operation exception
	 */
	public abstract List<LogEntry> log(String fromRevision,
	                                   String toRevision) throws RepositoryOperationException;
	
	/**
	 * Extract a log from the repository spanning between two revisions.
	 * 
	 * @param fromRevision
	 *            the from revision
	 * @param toRevision
	 *            the to revision
	 * @param cacheSize
	 *            the cache size
	 * @return Iterator running from <code>fromRevisions</code> to <code>toRevision</code>
	 * @throws RepositoryOperationException
	 *             the repository operation exception
	 */
	public Iterator<LogEntry> log(final String fromRevision,
	                              final String toRevision,
	                              final int cacheSize) throws RepositoryOperationException {
		return new LogIterator(this, fromRevision, toRevision);
	}
	
	/**
	 * Sets the end revision.
	 * 
	 * @param endRevision
	 *            the endRevision to set
	 */
	protected void setEndRevision(final String endRevision) {
		this.endRevision = endRevision;
	}
	
	/**
	 * Sets the main branch name.
	 * 
	 * @param mainBranchName
	 *            the new main branch name
	 */
	public void setMainBranchName(final String mainBranchName) {
		this.mainBranchName = mainBranchName;
	}
	
	/**
	 * Sets the start revision.
	 * 
	 * @param startRevision
	 *            the startRevision to set
	 */
	protected void setStartRevision(final String startRevision) {
		this.startRevision = startRevision;
	}
	
	/**
	 * Sets the start transaction.
	 * 
	 * @param startTransaction
	 *            the startTransaction to set
	 */
	public void setStartTransaction(final ChangeSet startTransaction) {
		this.startTransaction = startTransaction;
	}
	
	/**
	 * Connect to repository at URI address.
	 * 
	 * @param address
	 *            the address the repository can be found
	 * @param tmpDir
	 *            the tmp dir
	 * @param mainBranchName
	 *            the main branch name
	 * @throws RepositoryOperationException
	 *             the repository operation exception
	 */
	public abstract void setup(URI address,
	                           File tmpDir,
	                           String mainBranchName) throws RepositoryOperationException;
	
	/**
	 * Connect to repository at URI address using user name and password.
	 * 
	 * @param address
	 *            the address
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @param tmpDir
	 *            the tmp dir
	 * @param mainBranchName
	 *            the main branch name
	 * @throws RepositoryOperationException
	 *             the repository operation exception
	 */
	public abstract void setup(URI address,
	                           String username,
	                           String password,
	                           File tmpDir,
	                           String mainBranchName) throws RepositoryOperationException;
	
	/**
	 * Sets the uri.
	 * 
	 * @param uri
	 *            the new uri
	 */
	public void setUri(final URI uri) {
		this.uri = uri;
	}
	
}
