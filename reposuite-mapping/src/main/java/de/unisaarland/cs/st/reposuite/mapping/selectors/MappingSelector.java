/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.selectors;

import java.util.List;

import de.unisaarland.cs.st.reposuite.mapping.register.Registered;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public abstract class MappingSelector<K, V> extends Registered {
	
	/**
	 * @param element
	 * @return
	 */
	public abstract List<V> parse(K element);
	
}
