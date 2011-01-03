/**
 * 
 */
package de.unisaarland.cs.st.reposuite.toolchain;

import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;

import de.unisaarland.cs.st.reposuite.utils.Condition;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Tuple;

/**
 * The {@link RepoSuiteDataStorage} elements are the node of a
 * {@link RepoSuiteToolchain} and used for the communication between the
 * threads.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepoSuiteDataStorage<E> {
	
	private final Queue<Tuple<E, CountDownLatch>>             queue   = new ConcurrentLinkedQueue<Tuple<E, CountDownLatch>>();
	private final BlockingDeque<RepoSuiteGeneralThread<?, E>> writers = new LinkedBlockingDeque<RepoSuiteGeneralThread<?, E>>();
	private final BlockingDeque<RepoSuiteGeneralThread<E, ?>> readers = new LinkedBlockingDeque<RepoSuiteGeneralThread<E, ?>>();
	private final int                                         cacheSize;
	
	public RepoSuiteDataStorage() {
		this(3000);
	}
	
	/**
	 * @param cacheSize
	 */
	public RepoSuiteDataStorage(final int cacheSize) {
		this.cacheSize = cacheSize;
	}
	
	/**
	 * @return the next chunk of data in the queue. Can be null if no more data
	 *         is available and there aren't any writers attached to this source
	 *         anymore.
	 * @throws InterruptedException
	 */
	public Tuple<E, CountDownLatch> read() throws InterruptedException {
		if (Logger.logTrace()) {
			Logger.trace("Entering read method.");
		}
		
		synchronized (this.queue) {
			if (this.queue.size() > 0) {
				Tuple<E, CountDownLatch> poll = this.queue.poll();
				this.queue.notifyAll();
				return poll;
			} else {
				while (!this.writers.isEmpty() && this.queue.isEmpty()) {
					this.queue.wait();
				}
				
				if (this.queue.size() > 0) {
					Tuple<E, CountDownLatch> poll = this.queue.poll();
					this.queue.notifyAll();
					return poll;
				} else {
					if (Logger.logWarn()) {
						Logger.warn("No more incoming data. Returning (null).");
					}
					this.queue.notifyAll();
					return null;
				}
			}
		}
	}
	
	/**
	 * Registers a writer thread to the queue.
	 * 
	 * @param writerThread
	 *            may not be null
	 */
	public void registerInput(final RepoSuiteGeneralThread<?, E> writerThread) {
		Condition.notNull(writerThread, "Registering null objects is not allowed.");
		
		if (Logger.logInfo()) {
			Logger.info("Registering input " + ((RepoSuiteThread<?, E>) writerThread).getName());
		}
		
		this.writers.add(writerThread);
		synchronized (this.queue) {
			this.queue.notifyAll();
		}
	}
	
	/**
	 * Registers a reader thread to the queue.
	 * 
	 * @param readerThread
	 *            may not be null
	 */
	public void registerOutput(final RepoSuiteGeneralThread<E, ?> readerThread) {
		Condition.notNull(readerThread, "Registering null objects is not allowed.");
		
		if (Logger.logInfo()) {
			Logger.info("Registering output " + ((RepoSuiteThread<E, ?>) readerThread).getName());
		}
		
		this.readers.add(readerThread);
		synchronized (this.queue) {
			this.queue.notifyAll();
		}
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
	public void unregisterInput(final RepoSuiteGeneralThread<?, E> writerThread) {
		Condition.notNull(writerThread);
		
		if (this.writers.contains(writerThread)) {
			if (Logger.logInfo()) {
				Logger.info("Unregistering input " + ((RepoSuiteThread<?, E>) writerThread).getName());
			}
			
			this.writers.remove(writerThread);
			synchronized (this.queue) {
				this.queue.notifyAll();
			}
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
	public void unregisterOutput(final RepoSuiteGeneralThread<E, ?> readerThread) {
		Condition.notNull(readerThread);
		
		if (this.readers.contains(readerThread)) {
			
			if (Logger.logInfo()) {
				Logger.info("Unregistering output " + ((RepoSuiteThread<E, ?>) readerThread).getName());
			}
			
			this.readers.remove(readerThread);
			synchronized (this.queue) {
				this.queue.notifyAll();
			}
		}
	}
	
	/**
	 * Writes the next chunk of data to the queue.
	 * 
	 * @param data
	 *            may not be null
	 * @throws InterruptedException
	 */
	public CountDownLatch write(final E data) throws InterruptedException {
		Condition.notNull(data, "Writing null data is not allowed.");
		
		if (Logger.logTrace()) {
			Logger.trace("Entering write method.");
		}
		
		synchronized (this.queue) {
			while (!this.readers.isEmpty() && (this.queue.size() >= this.cacheSize)) {
				this.queue.wait();
			}
			CountDownLatch countDownLatch = new CountDownLatch(1);
			
			if (!this.readers.isEmpty()) {
				this.queue.add(new Tuple<E, CountDownLatch>(data, countDownLatch));
			} else {
				if (Logger.logWarn()) {
					Logger.warn("No more readers attached. Void sinking data and returning.");
				}
			}
			this.queue.notifyAll();
			return countDownLatch;
		}
	}
}
