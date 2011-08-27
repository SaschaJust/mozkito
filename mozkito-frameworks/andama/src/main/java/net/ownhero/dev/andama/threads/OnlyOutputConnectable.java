package net.ownhero.dev.andama.threads;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;

public interface OnlyOutputConnectable<T> {
	
	/**
	 * @return
	 */
	public T process() throws UnrecoverableError, Shutdown;
}
