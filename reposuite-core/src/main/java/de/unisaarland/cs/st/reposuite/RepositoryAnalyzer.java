/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogEntry;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepositoryAnalyzer extends RepoSuiteThread implements RepoSuitePreFilterThread<LogEntry> {
	private final List<LogEntry>    entries  = new LinkedList<LogEntry>();
	private final Queue<LogEntry>   queue    = new LinkedBlockingQueue<LogEntry>();
	private final RepoSuiteSettings settings;
	private boolean                 analyze;
	private RepoSuitePreFilterThread<LogEntry>      inPreFilter;
	private RepoSuiteSourceThread<LogEntry>         inSource;
	private RepoSuitePreFilterThread<LogEntry>      outPreFilter;
	private RepoSuiteTransformerThread<LogEntry, ?> outTransformer;
	private final Repository                        repository;
	
	public RepositoryAnalyzer(final RepoSuiteThreadGroup threadGroup, final Repository repository,
			final RepoSuiteSettings settings) {
		super(threadGroup, RepositoryAnalyzer.class.getSimpleName());
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
		
		if (preFilterThread.hasOutputConnector() && !preFilterThread.isOutputConnected()) {
			preFilterThread.connectOutput(this);
		}
		
	}
	
	@Override
	public void connectInput(final RepoSuiteSourceThread<LogEntry> sourceThread) {
		this.inSource = sourceThread;
		this.knownThreads.add(sourceThread);
		
		if (Logger.logInfo()) {
			Logger.info("[" + getHandle() + "] Linking input connector to: " + sourceThread.getHandle());
		}
		
		if (sourceThread.hasOutputConnector() && !sourceThread.isOutputConnected()) {
			sourceThread.connectOutput(this);
		}
	}
	
	@Override
	public void connectOutput(final RepoSuitePreFilterThread<LogEntry> preFilterThread) {
		this.outPreFilter = preFilterThread;
		this.knownThreads.add(this.outPreFilter);
		
		if (Logger.logInfo()) {
			Logger.info("[" + getHandle() + "] Linking output connector to: " + preFilterThread.getHandle());
		}
		
		if (this.outPreFilter.hasInputConnector() && !this.outPreFilter.isInputConnected()) {
			this.outPreFilter.connectInput(this);
		}
	}
	
	@Override
	public void connectOutput(final RepoSuiteTransformerThread<LogEntry, ?> transformerThread) {
		this.outTransformer = transformerThread;
		this.knownThreads.add(transformerThread);
		
		if (Logger.logInfo()) {
			Logger.info("[" + getHandle() + "] Linking output connector to: " + transformerThread.getHandle());
		}
		
		if (this.outTransformer.hasInputConnector() && !this.outTransformer.isInputConnected()) {
			this.outTransformer.connectInput(this);
		}
	}
	
	public synchronized LogEntry getNext() {
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
	
	public Repository getRepository() {
		return this.repository;
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
		return (this.outPreFilter != null) || (this.outTransformer != null);
	}
	
	@Override
	public void run() {
		
		if (!checkConnections()) {
			return;
		}
		
		if (!checkNotShutdown()) {
			return;
		}
		
		this.analyze = (this.settings.getSetting("repository.analyze") != null)
		&& (this.settings.getSetting("repository.analyze").getValue() != null)
		&& (Boolean) this.settings.getSetting("repository.analyze").getValue();
		if (Logger.logInfo()) {
			Logger.info("Starting " + getHandle());
		}
		
		LogEntry entry;
		
		while (!isShutdown()
				&& ((entry = (this.inPreFilter != null ? this.inPreFilter.getNext() : this.inSource.getNext())) != null)) {
			if (this.analyze) {
				if (Logger.logDebug()) {
					Logger.debug("Adding " + entry + " to analysis.");
				}
				this.entries.add(entry);
			}
			
			if (Logger.logTrace()) {
				Logger.trace("filling queue [" + this.queue.size() + "]");
			}
			this.queue.add(entry);
			
			wake();
		}
		
		if (!this.shutdown) {
			this.repository.consistencyCheck(this.entries, ((Boolean) this.settings.getSetting("headless")
					.getValue() == false));
		}
	}
}
