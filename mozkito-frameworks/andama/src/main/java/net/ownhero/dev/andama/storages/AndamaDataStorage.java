/**
 * 
 */
package net.ownhero.dev.andama.storages;

import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;

import net.ownhero.dev.andama.model.Chain;
import net.ownhero.dev.andama.threads.INode;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

/**
 * The {@link AndamaDataStorage} elements are the node of a.
 * 
 * @param <E>
 *            the element type {@link Chain} and used for the communication between the threads.
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public final class AndamaDataStorage<E> {
	
	/** The queue. */
	private final Queue<Tuple<E, CountDownLatch>>       queue   = new ConcurrentLinkedQueue<Tuple<E, CountDownLatch>>();
	
	/** The writers. */
	private final BlockingDeque<INode<?, E>> writers = new LinkedBlockingDeque<INode<?, E>>();
	
	/** The readers. */
	private final BlockingDeque<INode<E, ?>> readers = new LinkedBlockingDeque<INode<E, ?>>();
	
	/** The cache size. */
	private final int                                   cacheSize;
	
	/**
	 * Instantiates a new repo suite data storage.
	 */
	public AndamaDataStorage() {
		this(3000);
	}
	
	/**
	 * Instantiates a new repo suite data storage.
	 * 
	 * @param cacheSize
	 *            the cache size
	 */
	public AndamaDataStorage(final int cacheSize) {
		this.cacheSize = cacheSize;
	}
	
	/**
	 * Gets the num readers.
	 * 
	 * @return the num readers
	 */
	public final int getNumReaders() {
		return this.readers.size();
	}
	
	/**
	 * Read.
	 * 
	 * @return the next chunk of data in the queue. Can be null if no more data is available and there aren't any
	 *         writers attached to this source anymore.
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	public final Tuple<E, CountDownLatch> read() throws InterruptedException {
		
		if (Logger.logTrace()) {
			Logger.trace("Entering read method.");
		}
		
		synchronized (this.queue) {
			if (this.queue.size() > 0) {
				final Tuple<E, CountDownLatch> poll = this.queue.poll();
				this.queue.notifyAll();
				return poll;
			} else {
				while (!this.writers.isEmpty() && this.queue.isEmpty()) {
					this.queue.wait();
				}
				
				if (this.queue.size() > 0) {
					final Tuple<E, CountDownLatch> poll = this.queue.poll();
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
	public final void registerInput(@NotNull ("Registering null objects is not allowed.") final INode<?, E> writerThread) {
		
		if (Logger.logInfo()) {
			Logger.info("Registering input " + ((INode<?, E>) writerThread).getName());
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
	public final void registerOutput(@NotNull ("Registering null objects is not allowed.") final INode<E, ?> readerThread) {
		
		if (Logger.logInfo()) {
			Logger.info("Registering output " + ((INode<E, ?>) readerThread).getName());
		}
		
		this.readers.add(readerThread);
		synchronized (this.queue) {
			this.queue.notifyAll();
		}
	}
	
	/**
	 * Size.
	 * 
	 * @return the current size of the queue. This is guaranteed to be &ge; 0.
	 */
	public final int size() {
		return this.queue.size();
	}
	
	/**
	 * Unregisters a writer thread from the queue. This happens when the writer finished its job. This method won't do
	 * anything if the writer thread is not known to the underlying data structure.
	 * 
	 * @param writerThread
	 *            may not be null
	 */
	public final void unregisterInput(@NotNull ("Unregistering null objects is not allowed.") final INode<?, E> writerThread) {
		if (this.writers.contains(writerThread)) {
			this.writers.remove(writerThread);
			
			if (Logger.logInfo()) {
				Logger.info("Unregistering input "
				        + ((INode<?, E>) writerThread).getName()
				        + ". "
				        + (this.writers.isEmpty()
				                                 ? "No remaining input threads."
				                                 : "Remaining input threads: "
				                                         + JavaUtils.collectionToString(this.writers)));
			}
			
			synchronized (this.queue) {
				this.queue.notifyAll();
			}
		}
	}
	
	/**
	 * Unregisters a reader thread from the queue. This happens when the reader finished its job. This method won't do
	 * anything if the reader thread is not known to the underlying data structure.
	 * 
	 * @param readerThread
	 *            may not be null
	 */
	public final void unregisterOutput(@NotNull ("Unregistering null objects is not allowed.") final INode<E, ?> readerThread) {
		if (this.readers.contains(readerThread)) {
			this.readers.remove(readerThread);
			
			if (Logger.logInfo()) {
				Logger.info("Unregistering output "
				        + ((INode<E, ?>) readerThread).getName()
				        + ". "
				        + (this.readers.isEmpty()
				                                 ? "No remaining output threads."
				                                 : "Remaining output threads: "
				                                         + JavaUtils.collectionToString(this.readers)));
			}
			
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
	 * @return the count down latch
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	public final CountDownLatch write(@NotNull ("Writing null data is now allowed.") final E data) throws InterruptedException {
		
		if (Logger.logTrace()) {
			Logger.trace("Entering write method.");
		}
		
		synchronized (this.queue) {
			while (!this.readers.isEmpty() && (this.queue.size() >= this.cacheSize)) {
				this.queue.wait();
			}
			final CountDownLatch countDownLatch = new CountDownLatch(1);
			
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
