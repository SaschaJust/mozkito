/**
 * 
 */
package de.unisaarland.cs.st.reposuite.toolchain;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import de.unisaarland.cs.st.reposuite.utils.Condition;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * The {@link RepoSuiteDataStorage} elements are the node of a
 * {@link RepoSuiteToolchain} and used for the communication between the
 * threads.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepoSuiteDataStorage<E> {
	
	private final Queue<E>                           queue;
	private final List<RepoSuiteGeneralThread<?, E>> writers = new LinkedList<RepoSuiteGeneralThread<?, E>>();
	private final List<RepoSuiteGeneralThread<E, ?>> readers = new LinkedList<RepoSuiteGeneralThread<E, ?>>();
	private final int                                cacheSize;
	
	public RepoSuiteDataStorage() {
		this(3000);
	}
	
	/**
	 * @param cacheSize
	 */
	public RepoSuiteDataStorage(final int cacheSize) {
		this.cacheSize = cacheSize;
		this.queue = new ConcurrentLinkedQueue<E>();
	}
	
	/**
	 * @return the next chunk of data in the queue. Can be null if no more data
	 *         is available and there aren't any writers attached to this source
	 *         anymore.
	 * @throws InterruptedException
	 */
	public E read() throws InterruptedException {
		if (Logger.logTrace()) {
			Logger.trace("Entering read method.");
		}
		
		synchronized (this.queue) {
			if (this.queue.size() > 0) {
				E poll = this.queue.poll();
				this.queue.notifyAll();
				return poll;
			} else if (!this.writers.isEmpty()) {
				while (this.queue.isEmpty()) {
					this.queue.wait();
				}
				if (this.queue.size() > 0) {
					E poll = this.queue.poll();
					this.queue.notifyAll();
					return poll;
				}
			} else {
				if (Logger.logWarn()) {
					Logger.warn("No more incoming data. Returning (null).");
				}
			}
			this.queue.notifyAll();
			return null;
		}
	}
	
	/**
	 * Registers a writer thread to the queue.
	 * 
	 * @param writerThread
	 *            may not be null
	 */
	public synchronized void registerInput(final RepoSuiteGeneralThread<?, E> writerThread) {
		Condition.notNull(writerThread);
		
		this.writers.add(writerThread);
	}
	
	/**
	 * Registers a reader thread to the queue.
	 * 
	 * @param readerThread
	 *            may not be null
	 */
	public synchronized void registerOutput(final RepoSuiteGeneralThread<E, ?> readerThread) {
		Condition.notNull(readerThread);
		
		this.readers.add(readerThread);
	}
	
	/**
	 * @return the current size of the queue. This is guaranteed to be &ge; 0.
	 */
	public int size() {
		return this.queue.size();
	}
	
	/**
	 * Unregisters a writer thread from the queue. This happens when the writer
	 * finished its job. This method won't do anything if the writer thread is
	 * not known to the underlying data structure.
	 * 
	 * @param writerThread
	 *            may not be null
	 */
	public synchronized void unregisterInput(final RepoSuiteGeneralThread<?, E> writerThread) {
		Condition.notNull(writerThread);
		
		if (this.writers.contains(writerThread)) {
			this.writers.remove(writerThread);
			notifyAll();
		}
	}
	
	/**
	 * Unregisters a reader thread from the queue. This happens when the reader
	 * finished its job. This method won't do anything if the reader thread is
	 * not known to the underlying data structure.
	 * 
	 * @param readerThread
	 *            may not be null
	 */
	public synchronized void unregisterOutput(final RepoSuiteGeneralThread<E, ?> readerThread) {
		Condition.notNull(readerThread);
		
		if (this.readers.contains(readerThread)) {
			this.readers.remove(readerThread);
			notifyAll();
		}
	}
	
	/**
	 * Writes the next chunk of data to the queue.
	 * 
	 * @param data
	 *            may not be null
	 * @throws InterruptedException
	 */
	public void write(final E data) throws InterruptedException {
		Condition.notNull(data);
		
		if (Logger.logTrace()) {
			Logger.trace("Entering write method.");
		}
		
		synchronized (this.queue) {
			if (this.readers.isEmpty()) {
				if (Logger.logWarn()) {
					Logger.warn("No readers attached to this storage. Void sinking data.");
				}
			} else {
				while (this.queue.size() >= this.cacheSize) {
					this.queue.wait();
				}
				
				this.queue.add(data);
			}
			this.queue.notifyAll();
		}
	}
}
