package net.ownhero.dev.andama.threads;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;

public interface InputOutputConnectable<K, V> {
	
	/**
	 * @return
	 */
	public V process(K data) throws UnrecoverableError, Shutdown;
}
