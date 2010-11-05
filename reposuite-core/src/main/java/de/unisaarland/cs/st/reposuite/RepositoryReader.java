/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogEntry;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogIterator;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepositoryReader extends RepoSuiteThread implements RepoSuiteSourceThread<LogEntry> {
	
	private LogIterator             logIterator;
	private final Queue<LogEntry>                   queue    = new LinkedBlockingQueue<LogEntry>();
	
	private final Repository        repository;
	
	private final RepoSuiteSettings settings;
	
	private RepoSuiteTransformerThread<LogEntry, ?> outTransformer;
	
	private RepoSuitePreFilterThread<LogEntry>      outPreFilter;
	
	public RepositoryReader(final RepoSuiteThreadGroup threadGroup, final Repository repository,
			final RepoSuiteSettings settings) {
		super(threadGroup, RepositoryReader.class.getSimpleName());
		this.repository = repository;
		this.settings = settings;
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
		this.knownThreads.add(this.outTransformer);
		this.outTransformer = transformerThread;
		
		if (Logger.logInfo()) {
			Logger.info("[" + getHandle() + "] Linking output connector to: " + transformerThread.getHandle());
		}
		
		if (this.outTransformer.hasInputConnector() && !this.outTransformer.isInputConnected()) {
			this.outTransformer.connectInput(this);
		}
	}
	
	public synchronized LogIterator getIterator() {
		if (this.logIterator == null) {
			try {
				wait();
			} catch (InterruptedException e) {
				
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
			}
		}
		
		return this.logIterator;
	}
	
	@Override
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
		return false;
	}
	
	@Override
	public boolean hasOutputConnector() {
		return true;
	}
	
	@Override
	public boolean isInputConnected() {
		return true;
	}
	
	@Override
	public boolean isOutputConnected() {
		return (this.outTransformer != null) || (this.outPreFilter != null);
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
		
		if (Logger.logInfo()) {
			Logger.info("Requesting logs from " + this.repository);
		}
		
		this.repository.getTransactionCount();
		long cacheSize = (Long) this.settings.getSetting("repository.cachesize").getValue();
		this.logIterator = (LogIterator) this.repository.log(this.repository.getFirstRevisionId(),
				this.repository.getLastRevisionId(), (int) cacheSize);
		
		if (Logger.logInfo()) {
			Logger.info("Created iterator.");
		}
		
		while (!isShutdown() && this.logIterator.hasNext()) {
			
			if (Logger.logTrace()) {
				Logger.trace("filling queue [" + this.queue.size() + "]");
			}
			
			if (this.queue.size() <= cacheSize) {
				this.queue.add(this.logIterator.next());
				wake();
			}
		}
	}
}
