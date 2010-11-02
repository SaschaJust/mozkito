/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogEntry;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSFile;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSFileManager;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSRevision;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepositoryParser extends Thread {
	
	/**
	 * @return the simple class name
	 */
	public static String getHandle() {
		return RepositoryParser.class.getSimpleName();
	}
	
	private final Repository            repository;
	private final RepositoryAnalyzer    analyzer;
	private RCSFileManager              fileManager;
	
	private final Queue<RCSTransaction> queue = new LinkedBlockingQueue<RCSTransaction>();
	
	/**
	 * @param reader
	 */
	public RepositoryParser(final RepositoryAnalyzer analyzer) {
		this.analyzer = analyzer;
		this.repository = analyzer.getRepository();
	}
	
	public synchronized RCSTransaction getNext() {
		if (this.queue.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
			}
		}
		
		if (this.queue.isEmpty()) {
			return null;
		} else {
			return this.queue.poll();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		if (Logger.logInfo()) {
			Logger.info("Starting " + getHandle());
		}
		
		LogEntry entry;
		RCSTransaction previousRcsTransaction = null;
		this.fileManager = new RCSFileManager();
		
		while ((entry = this.analyzer.getNext()) != null) {
			if (Logger.logInfo()) {
				Logger.info("Parsing " + entry);
			}
			RCSTransaction rcsTransaction = new RCSTransaction(entry.getRevision(), entry.getMessage(),
			        entry.getDateTime(), entry.getAuthor(), previousRcsTransaction);
			Map<String, ChangeType> changedPaths = this.repository.getChangedPaths(entry.getRevision());
			for (String fileName : changedPaths.keySet()) {
				RCSFile file;
				
				if (changedPaths.get(fileName).equals(ChangeType.Renamed)) {
					file = this.fileManager
					        .getFile(this.repository.getFormerPathName(rcsTransaction.getId(), fileName));
					if (file == null) {
						
						if (Logger.logWarn()) {
							Logger.warn("Found renaming of unknown file. Assuming type `added` instead of `renamed`: "
							        + changedPaths.get(fileName));
						}
						file = this.fileManager.getFile(fileName);
						
						if (file == null) {
							file = this.fileManager.createFile(fileName, rcsTransaction);
						}
					} else {
						file.assignTransaction(rcsTransaction, fileName);
					}
				} else {
					file = this.fileManager.getFile(fileName);
					
					if (file == null) {
						file = this.fileManager.createFile(fileName, rcsTransaction);
					}
				}
				
				rcsTransaction.addRevision(new RCSRevision(rcsTransaction, file, changedPaths.get(fileName),
				        previousRcsTransaction));
			}
			this.queue.add(rcsTransaction);
			wake();
		}
		
	}
	
	private synchronized void wake() {
		notifyAll();
	}
	
}
