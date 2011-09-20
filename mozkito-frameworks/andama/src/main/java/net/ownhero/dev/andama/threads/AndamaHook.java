/**
 * 
 */
package net.ownhero.dev.andama.threads;

import java.lang.reflect.Method;
import java.util.Collection;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class AndamaHook<K, V> implements Hook<K, V> {
	
	/**
	 * @param hooks
	 * @return
	 */
	public static <K, V> boolean allCompleted(final Collection<? extends AndamaHook<K, V>> hooks) {
		for (Hook<K, V> hook : hooks) {
			if (!hook.completed()) {
				return false;
			}
		}
		
		return true;
	}
	
	private boolean                  completed = true;
	
	private final AndamaThread<K, V> thread;
	
	/**
	 * 
	 */
	public AndamaHook(final AndamaThread<K, V> thread) {
		this.thread = thread;
		
		Class<?> clazz = this.getClass();
		
		while ((clazz.getSuperclass() != null) && (clazz.getSuperclass() != AndamaHook.class)) {
			clazz = clazz.getSuperclass();
		}
		
		if (clazz.getSuperclass() != AndamaHook.class) {
			// TODO ERROR
		}
		
		@SuppressWarnings ("unchecked")
		Class<? extends AndamaHook<K, V>> superclass = (Class<? extends AndamaHook<K, V>>) clazz;
		
		try {
			Method method = AndamaThread.class.getDeclaredMethod("add" + superclass.getSimpleName(), superclass);
			method.invoke(thread, this);
		} catch (Exception e) {
			throw new UnrecoverableError(e.getMessage(), e);
		}
	}
	
	/**
	 * @return if the {@link ProcessHook} is done with the current data
	 */
	@Override
	public final boolean completed() {
		return this.completed;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.Hook#getHandle()
	 */
	@Override
	public final String getHandle() {
		return this.getClass().getSimpleName().isEmpty()
		                                                ? this.getClass().getSuperclass().getSimpleName()
		                                                : this.getClass().getSimpleName();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.Hook#getThread()
	 */
	@Override
	public final AndamaThread<K, V> getThread() {
		return this.thread;
	}
	
	/**
	 * @return
	 */
	public final void setCompleted() {
		this.completed = true;
	}
	
	/**
	 * 
	 */
	public final void unsetCompleted() {
		this.completed = false;
	}
	
}
