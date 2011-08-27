package net.ownhero.dev.andama.threads;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;

public interface OnlyInputConnectable<T> {
	
	/**
	 * @return
	 */
	public void process(T data) throws UnrecoverableError, Shutdown;
}
