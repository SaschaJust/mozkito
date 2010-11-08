/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Preconditions;

/**
 * The {@link RepoSuiteDataStorage} elements are the node of a
 * {@link RepoSuiteToolchain} and used for the communication between the
 * threads.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepoSuiteDataStorage<E> {
	
	private final BlockingQueue<E>                   queue;
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
		this.queue = new ArrayBlockingQueue<E>(this.cacheSize);
	}
	
	/**
	 * @return the next chunk of data in the queue. Can be null if no more data
	 *         is available and there aren't any writers attached to this source
	 *         anymore.
	 * @throws InterruptedException
	 */
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
	
	/**
	 * Registers a writer thread to the queue.
	 * 
	 * @param writerThread
	 *            may not be null
	 */
	public synchronized void registerInput(final RepoSuiteGeneralThread<?, E> writerThread) {
		Preconditions.checkNotNull(writerThread);
		
		this.writers.add(writerThread);
	}
	
	/**
	 * Registers a reader thread to the queue.
	 * 
	 * @param readerThread
	 *            may not be null
	 */
	public synchronized void registerOutput(final RepoSuiteGeneralThread<E, ?> readerThread) {
		Preconditions.checkNotNull(readerThread);
		
		this.readers.add(readerThread);
	}
	
	/**
	 * @return the current size of the queue. This is guaranteed to be &ge; 0.
	 */
	public synchronized int size() {
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
		Preconditions.checkNotNull(writerThread);
		
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
		Preconditions.checkNotNull(readerThread);
		
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
	public synchronized void write(final E data) throws InterruptedException {
		Preconditions.checkNotNull(data);
		
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
