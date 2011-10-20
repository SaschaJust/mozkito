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
	private void checkThread(final AndamaThreadable<?, ?> aThread) {
		
		for (StackTraceElement ste : aThread.getStackTrace()) {
			if (ste.getClassName().contains(AndamaGroup.class.getPackage().getName())) {
				if (ste.getMethodName().startsWith("read")) {
					if (aThread.isInputConnected()) {
						for (AndamaThreadable<?, ?> a2Thread : aThread.getInputThreads()) {
							if ((((Thread) a2Thread).getState().equals(State.WAITING) || ((Thread) a2Thread).getState()
							        .equals(State.BLOCKED)) && !(((Thread) a2Thread).isInterrupted())) {
								checkThread(a2Thread);
							}
						}
					} else {
						if (Logger.logWarn()) {
							Logger.warn("Killing thread " + aThread.getName());
						}
						killThread(((Thread) aThread));
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
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		while (!this.terminate) {
			for (AndamaThreadable<?, ?> thread1 : this.group.getThreads()) {
				Thread thread = (Thread) thread1;
				switch (thread.getState()) {
					case WAITING:
					case BLOCKED:
						checkThread(thread1);
						break;
					default:
						break;
				}
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			}
		}
	}
	
	public void terminate() {
		this.terminate = true;
	}
}
