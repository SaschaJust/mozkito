/**
 * 
 */
package net.ownhero.dev.andama.model;

import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaThreadable;
import net.ownhero.dev.kisa.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class AndamaWatcher extends Thread {
	
	private final AndamaGroup group;
	private boolean           terminate = false;
	
	/**
	 * 
	 */
	public AndamaWatcher(final AndamaGroup group) {
		super(AndamaWatcher.class.getSimpleName());
		
		this.group = group;
	}
	
	/**
	 * @param thread
	 */
	private void checkThread(final Thread thread) {
		AndamaThreadable<?, ?> aThread = (AndamaThreadable<?, ?>) thread;
		
		for (StackTraceElement ste : thread.getStackTrace()) {
			if (ste.getClassName().contains(AndamaGroup.class.getPackage().getName())) {
				if (ste.getMethodName().startsWith("read")) {
					if (aThread.isInputConnected()) {
						for (AndamaThreadable<?, ?> a2Thread : aThread.getInputThreads()) {
							if ((((Thread) a2Thread).getState().equals(State.WAITING) || ((Thread) a2Thread).getState()
							                                                                                .equals(State.BLOCKED))
							        && !((Thread) a2Thread).isInterrupted()) {
								checkThread((Thread) a2Thread);
							}
						}
					} else {
						killThread(thread);
					}
				}
			}
		}
	}
	
	/**
	 * @param thread
	 */
	private void killThread(final Thread thread) {
		try {
			thread.join(3000);
		} catch (InterruptedException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		}
		if (thread.isAlive()) {
			thread.interrupt();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		while (!this.terminate) {
			for (Thread thread : this.group.getThreads()) {
				switch (thread.getState()) {
					case WAITING:
					case BLOCKED:
						checkThread(thread);
						break;
					default:
						break;
				}
			}
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
			}
		}
	}
	
	public void terminate() {
		this.terminate = true;
	}
}
