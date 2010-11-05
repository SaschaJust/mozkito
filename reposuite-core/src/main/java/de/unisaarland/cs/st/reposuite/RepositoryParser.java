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
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepositoryParser extends RepoSuiteThread implements RepoSuiteTransformerThread<LogEntry, RCSTransaction> {
	
	private final Repository                                repository;
	private RCSFileManager              fileManager;
	
	private final Queue<RCSTransaction> queue    = new LinkedBlockingQueue<RCSTransaction>();
	private RepoSuiteSinkThread<RCSTransaction>       outSink;
	private RepoSuitePostFilterThread<RCSTransaction> outPostFilter;
	private RepoSuiteSourceThread<LogEntry>           inSource;
	private RepoSuitePreFilterThread<LogEntry>        inPreFilter;
	@SuppressWarnings ("unused")
	private final RepoSuiteSettings                   settings;
	
	/**
	 * @param reader
	 */
	public RepositoryParser(final RepoSuiteThreadGroup threadGroup, final Repository repository,
			final RepoSuiteSettings settings) {
		super(threadGroup, RepositoryParser.class.getSimpleName());
		this.repository = repository;
		this.settings = settings;
	}
	
	@Override
	public void connectInput(final RepoSuitePreFilterThread<LogEntry> preFilterThread) {
		this.inPreFilter = preFilterThread;
		this.knownThreads.add(preFilterThread);
		
		if (Logger.logInfo()) {
			Logger.info("[" + getHandle() + "] Linking input connector to: " + preFilterThread.getHandle());
		}
		
		if (this.inPreFilter.hasOutputConnector() && !this.inPreFilter.isOutputConnected()) {
			this.inPreFilter.connectOutput(this);
		}
	}
	
	@Override
	public void connectInput(final RepoSuiteSourceThread<LogEntry> sourceThread) {
		this.inSource = sourceThread;
		this.knownThreads.add(sourceThread);
		
		if (Logger.logInfo()) {
			Logger.info("[" + getHandle() + "] Linking input connector to: " + sourceThread.getHandle());
		}
		
		if (this.inSource.hasOutputConnector() && !this.inSource.isOutputConnected()) {
			this.inSource.connectOutput(this);
		}
	}
	
	@Override
	public void connectOutput(final RepoSuitePostFilterThread<RCSTransaction> postFilterThread) {
		this.outPostFilter = postFilterThread;
		this.knownThreads.add(postFilterThread);
		
		if (Logger.logInfo()) {
			Logger.info("[" + getHandle() + "] Linking out connector to: " + postFilterThread.getHandle());
		}
		
		if (this.outPostFilter.hasInputConnector() && !this.outPostFilter.isInputConnected()) {
			this.outPostFilter.connectInput(this);
		}
	}
	
	@Override
	public void connectOutput(final RepoSuiteSinkThread<RCSTransaction> sinkThread) {
		this.outSink = sinkThread;
		this.knownThreads.add(sinkThread);
		
		if (Logger.logInfo()) {
			Logger.info("[" + getHandle() + "] Linking out connector to: " + sinkThread.getHandle());
		}
		
		if (this.outSink.hasInputConnector() && !this.outSink.isInputConnected()) {
			this.outSink.connectInput(this);
		}
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
	
	@Override
	public boolean hasInputConnector() {
		return true;
	}
	
	@Override
	public boolean hasOutputConnector() {
		return true;
	}
	
	@Override
	public boolean isInputConnected() {
		return (this.inPreFilter != null) || (this.inSource != null);
	}
	
	@Override
	public boolean isOutputConnected() {
		return (this.outPostFilter != null) || (this.outSink != null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		if (!checkConnections()) {
			return;
		}
		
		if (!checkNotShutdown()) {
			return;
		}
		
		if (Logger.logInfo()) {
			Logger.info("Starting " + getHandle());
		}
		
		LogEntry entry;
		RCSTransaction previousRcsTransaction = null;
		this.fileManager = new RCSFileManager();
		
		while (!isShutdown()
				&& ((entry = (this.inPreFilter != null ? this.inPreFilter.getNext() : this.inSource.getNext())) != null)) {
			if (Logger.logDebug()) {
				Logger.debug("Parsing " + entry);
			}
			RCSTransaction rcsTransaction = new RCSTransaction(entry.getRevision(), entry.getMessage(),
					entry.getDateTime(), entry.getAuthor(), previousRcsTransaction);
			Map<String, ChangeType> changedPaths = this.repository.getChangedPaths(entry.getRevision());
			for (String fileName : changedPaths.keySet()) {
				RCSFile file;
				
				if (changedPaths.get(fileName).equals(ChangeType.Renamed)) {
					file = this.fileManager.getFile(this.repository.getFormerPathName(rcsTransaction.getId(),
							fileName));
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
			if (Logger.logTrace()) {
				Logger.trace("filling queue [" + this.queue.size() + "]");
			}
			this.queue.add(rcsTransaction);
			wake();
		}
	}
}
