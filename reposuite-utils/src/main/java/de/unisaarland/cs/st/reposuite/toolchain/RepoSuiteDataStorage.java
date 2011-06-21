/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.reposuite.toolchain;

import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.ioda.Tuple;

/**
 * The {@link RepoSuiteDataStorage} elements are the node of a.
 * 
 * @param <E>
 *            the element type {@link RepoSuiteToolchain} and used for the
 *            communication between the threads.
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class RepoSuiteDataStorage<E> {
	
	/** The queue. */
	private final Queue<Tuple<E, CountDownLatch>>             queue   = new ConcurrentLinkedQueue<Tuple<E, CountDownLatch>>();
	
	/** The writers. */
	private final BlockingDeque<RepoSuiteGeneralThread<?, E>> writers = new LinkedBlockingDeque<RepoSuiteGeneralThread<?, E>>();
	
	/** The readers. */
	private final BlockingDeque<RepoSuiteGeneralThread<E, ?>> readers = new LinkedBlockingDeque<RepoSuiteGeneralThread<E, ?>>();
	
	/** The cache size. */
	private final int                                         cacheSize;
	
	/**
	 * Instantiates a new repo suite data storage.
	 */
	public RepoSuiteDataStorage() {
		this(3000);
	}
	
	/**
	 * Instantiates a new repo suite data storage.
	 * 
	 * @param cacheSize
	 *            the cache size
	 */
	public RepoSuiteDataStorage(final int cacheSize) {
		this.cacheSize = cacheSize;
	}
	
	/**
	 * Gets the num readers.
	 * 
	 * @return the num readers
	 */
	public int getNumReaders() {
		return this.readers.size();
	}
	
	/**
	 * Read.
	 * 
	 * @return the next chunk of data in the queue. Can be null if no more data
	 *         is available and there aren't any writers attached to this source
	 *         anymore.
	 * @throws InterruptedException
	 *             the interrupted exception
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
	public void registerInput(@NotNull ("Registering null objects is not allowed.") final RepoSuiteGeneralThread<?, E> writerThread) {
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
	public void registerOutput(@NotNull ("Registering null objects is not allowed.") final RepoSuiteGeneralThread<E, ?> readerThread) {
		if (Logger.logInfo()) {
			Logger.info("Registering output " + ((RepoSuiteThread<E, ?>) readerThread).getName());
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
	public void unregisterInput(@NotNull ("Unregistering null objects is not allowed.") final RepoSuiteGeneralThread<?, E> writerThread) {
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
	public void unregisterOutput(@NotNull ("Unregistering null objects is not allowed.") final RepoSuiteGeneralThread<E, ?> readerThread) {
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
	 * @return the count down latch
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	public CountDownLatch write(@NotNull ("Writing null data is now allowed.") final E data) throws InterruptedException {
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
