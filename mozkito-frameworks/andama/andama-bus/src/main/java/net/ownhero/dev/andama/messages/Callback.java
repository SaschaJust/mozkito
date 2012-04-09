/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package net.ownhero.dev.andama.messages;

import java.util.concurrent.CountDownLatch;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

/**
 * The Class Callback.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public abstract class Callback {
	
	/** The processed. */
	private boolean              processed = false;
	
	/** The latch. */
	private final CountDownLatch latch     = new CountDownLatch(1);
	
	/**
	 * Await.
	 */
	public final void await() {
		// PRECONDITIONS
		Condition.notNull(this.latch, "Field '%s' in '%s'.", "latch", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			try {
				this.latch.await();
			} catch (final InterruptedException e) {
				if (Logger.logWarn()) {
					Logger.warn(e);
				}
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Execute.
	 * 
	 * @param event
	 *            the event
	 * @return true, if successful
	 */
	public abstract boolean execute(@NotNull IEvent event);
	
	/**
	 * Gets the handle.
	 * 
	 * @return the handle
	 */
	public String getHandle() {
		// PRECONDITIONS
		
		try {
			return getClass().getSimpleName();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Process.
	 * 
	 * @param event
	 *            the event
	 */
	final void process(@NotNull final IEvent event) {
		if (!this.processed) {
			this.processed = true;
			execute(event);
			this.latch.countDown();
		}
	}
	
}
