/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepoSuiteDataStorage<E> {
	
	private final BlockingQueue<E>                   queue;
	private final List<RepoSuiteGeneralThread<?, E>> writers = new LinkedList<RepoSuiteGeneralThread<?, E>>();
	private final List<RepoSuiteGeneralThread<E, ?>> readers = new LinkedList<RepoSuiteGeneralThread<E, ?>>();
	private final int                                cacheSize;
	
	public RepoSuiteDataStorage(final int cacheSize) {
		this.cacheSize = cacheSize;
		this.queue = new ArrayBlockingQueue<E>(this.cacheSize);
	}
	
	public synchronized E read() throws InterruptedException {
		if (this.queue.size() > 0) {
			E poll = this.queue.poll();
			notifyAll();
			return poll;
		} else if (!this.writers.isEmpty()) {
			while (this.queue.isEmpty()) {
				wait();
			}
			if (this.queue.size() > 0) {
				E poll = this.queue.poll();
				notifyAll();
				return poll;
			}
		} else {
			if (Logger.logWarn()) {
				Logger.warn("No more incoming data. Returning (null).");
			}
		}
		notifyAll();
		return null;
	}
	
	public synchronized void registerInput(final RepoSuiteGeneralThread<?, E> writerThread) {
		this.writers.add(writerThread);
	}
	
	public synchronized void registerOutput(final RepoSuiteGeneralThread<E, ?> readerThread) {
		this.readers.add(readerThread);
	}
	
	public synchronized int size() {
		return this.queue.size();
	}
	
	public synchronized void unregisterInput(final RepoSuiteGeneralThread<?, E> writerThread) {
		if (this.writers.contains(writerThread)) {
			this.writers.remove(writerThread);
		}
	}
	
	public synchronized void unregisterOutput(final RepoSuiteGeneralThread<E, ?> readerThread) {
		if (this.readers.contains(readerThread)) {
			this.readers.remove(readerThread);
		}
	}
	
	public synchronized void write(final E data) throws InterruptedException {
		if (this.readers.isEmpty()) {
			if (Logger.logWarn()) {
				Logger.warn("No readers attached to this storage. Void sinking data.");
			}
			notifyAll();
		} else {
			
			while (this.queue.size() >= this.cacheSize) {
				wait();
			}
			
			if (this.queue.size() < this.cacheSize) {
				this.queue.add(data);
			}
		}
		notifyAll();
	}
}
