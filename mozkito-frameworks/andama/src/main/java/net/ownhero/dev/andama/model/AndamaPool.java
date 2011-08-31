/**
 * 
 */
package net.ownhero.dev.andama.model;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.graph.AndamaGraph;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaThread;
import net.ownhero.dev.kisa.Logger;

/**
 * The thread pool manages all threads of the tool chain. Since all
 * {@link AndamaThread}s have to register themselves in the {@link AndamaGroup}
 * which is owned by the {@link AndamaPool}, the thread pool has control over
 * all threads of the tool chain. Additionally, this class automatically
 * generates a connected graph for the registered threads. See the corresponding
 * methods for details.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class AndamaPool {
	
	private final AndamaGroup threads;
	
	/**
	 * {@link AndamaPool} constructor. Initializes the {@link AndamaGroup};
	 * 
	 * @param name
	 *            the name of the {@link AndamaGroup}
	 */
	public AndamaPool(final String name, final AndamaChain toolchain) {
		this.threads = new AndamaGroup(name, toolchain);
	}
	
	/**
	 * 
	 */
	private void connectThreads() {
		AndamaGraph.buildGraph(getThreadGroup());
	}
	
	/**
	 * this method invokes the graph builder (which builds the graph and
	 * connects the threads) and starts all threads afterwards.
	 */
	public void execute() {
		connectThreads();
		
		for (Thread thread : this.threads.getThreads()) {
			thread.start();
		}
		
		for (Thread thread : this.threads.getThreads()) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				throw new Shutdown();
			}
		}
	}
	
	/**
	 * @return the inner thread group
	 */
	public AndamaGroup getThreadGroup() {
		return this.threads;
	}
	
	/**
	 * shuts down all threads
	 */
	public void shutdown() {
		
		if (Logger.logError()) {
			Logger.error("Terminating " + this.threads.activeCount() + " threads.");
		}
		
		this.threads.shutdown();
		System.exit(0);
	}
}
