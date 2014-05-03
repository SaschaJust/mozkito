/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/
package net.ownhero.dev.andama.model;

import net.ownhero.dev.andama.exceptions.InvalidGraphLayoutException;
import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.threads.Graph;
import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.INode;
import net.ownhero.dev.kisa.Logger;

/**
 * The thread pool manages all threads of the tool chain. Since all {@link AndamaThread}s have to register themselves in
 * the {@link Group} which is owned by the {@link Pool}, the thread pool has control over all threads of the tool chain.
 * Additionally, this class automatically generates a connected graph for the registered threads. See the corresponding
 * methods for details.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Pool {
	
	/** The threads. */
	private final Group threads;
	
	/**
	 * Instantiates a new pool.
	 * 
	 * @param name
	 *            the name of the {@link Group}
	 * @param toolchain
	 *            the toolchain {@link Pool} constructor. Initializes the {@link Group};
	 */
	public Pool(final String name, final Chain<?> toolchain) {
		this.threads = new Group(name, toolchain);
		toolchain.setPool(this);
	}
	
	/**
	 * Connect threads.
	 */
	private void connectThreads() {
		try {
			new Graph(getThreadGroup()).buildGraph();
		} catch (final InvalidGraphLayoutException e) {
			throw new UnrecoverableError(e.getMessage(), e);
			
		}
	}
	
	/**
	 * this method invokes the graph builder (which builds the graph and connects the threads) and starts all threads
	 * afterwards.
	 */
	public void execute() {
		connectThreads();
		
		for (final INode<?, ?> thread : this.threads.getThreads()) {
			if (!thread.checkConnections()) {
				for (final INode<?, ?> thread2 : this.threads.getThreads()) {
					thread2.shutdown();
				}
				shutdown();
				return;
			}
		}
		
		for (final INode<?, ?> thread : this.threads.getThreads()) {
			((Thread) thread).start();
		}
		
		// AndamaWatcher watcher = new AndamaWatcher(getThreadGroup());
		// watcher.start();
		
		for (final INode<?, ?> thread : this.threads.getThreads()) {
			try {
				((Thread) thread).join();
			} catch (final InterruptedException e) {
				
				if (Logger.logError()) {
					Logger.error(e);
				}
				throw new Shutdown();
			}
		}
		
		// watcher.terminate();
		// try {
		// watcher.join(60000);
		// } catch (InterruptedException e) {
		// }
	}
	
	/**
	 * Gets the thread group.
	 * 
	 * @return the inner thread group
	 */
	public Group getThreadGroup() {
		return this.threads;
	}
	
	/**
	 * shuts down all threads.
	 */
	public void shutdown() {
		
		if (Logger.logError()) {
			Logger.error("Terminating " + this.threads.activeCount() + " threads.");
		}
		
		this.threads.shutdown();
		System.exit(0);
	}
}
