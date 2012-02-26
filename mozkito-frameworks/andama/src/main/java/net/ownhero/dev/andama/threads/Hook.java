/**
 * 
 */
package net.ownhero.dev.andama.threads;

import java.lang.reflect.Method;
import java.util.Collection;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class Hook<K, V> implements IHook<K, V> {
	
	/**
	 * @param hooks
	 * @return
	 */
	public static <K, V> boolean allCompleted(final Collection<? extends Hook<K, V>> hooks) {
		for (final IHook<K, V> hook : hooks) {
			if (!hook.completed()) {
				return false;
			}
		}
		
		return true;
	}
	
	private boolean          completed = true;
	
	private final Node<K, V> thread;
	
	/**
	 * 
	 */
	public Hook(final Node<K, V> thread) {
		this.thread = thread;
		
		Class<?> clazz = this.getClass();
		
		while ((clazz.getSuperclass() != null) && (clazz.getSuperclass() != Hook.class)) {
			clazz = clazz.getSuperclass();
		}
		
		if (clazz.getSuperclass() != Hook.class) {
			// TODO ERROR
		}
		
		@SuppressWarnings ("unchecked")
		final Class<? extends Hook<K, V>> superclass = (Class<? extends Hook<K, V>>) clazz;
		
		try {
			final Method method = Node.class.getDeclaredMethod("add" + superclass.getSimpleName(), superclass);
			method.invoke(thread, this);
		} catch (final Exception e) {
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
	public final Node<K, V> getThread() {
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
