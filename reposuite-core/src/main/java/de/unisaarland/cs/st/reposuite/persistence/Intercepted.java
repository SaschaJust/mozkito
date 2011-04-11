/**
 * 
 */
package de.unisaarland.cs.st.reposuite.persistence;

import java.util.Collection;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public interface Intercepted<T> {
	
	/**
	 * @param id
	 * @param t
	 */
	public T add(String id,
	             T t);
	
	/**
	 * @param id
	 * @return
	 */
	public T get(String id);
	
	/**
	 * @return
	 */
	public Collection<T> interceptorTargets();
	
	/**
	 * @param from
	 * @param to
	 */
	public void replace(T from,
	                    T to);
}
