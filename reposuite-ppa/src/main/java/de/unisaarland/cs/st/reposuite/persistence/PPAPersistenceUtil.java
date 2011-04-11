package de.unisaarland.cs.st.reposuite.persistence;

import java.util.List;

import de.unisaarland.cs.st.reposuite.ppa.model.JavaElement;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * The Class PPAPersistenceUtil.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class PPAPersistenceUtil {
	
	/**
	 * Gets the java element.
	 * 
	 * @param persistenceUtil
	 *            the persistence middleware util
	 * @param e
	 *            the e
	 * @return the java element
	 */
	public static JavaElement getJavaElement(final PersistenceUtil persistenceUtil,
	                                         final JavaElement e) {
		Criteria<? extends JavaElement> criteria = persistenceUtil.createCriteria(e.getClass());
		criteria.eq("primaryKey", e.getPrimaryKey());
		List<? extends JavaElement> elements = persistenceUtil.load(criteria);
		if (elements.isEmpty()) {
			return null;
		}
		if (elements.size() > 1) {
			if (Logger.logError()) {
				Logger.error("Found more that one JavaElement with same primaryKey! This is impossible!");
			}
			return null;
		}
		return elements.get(0);
	}
	
	/**
	 * Gets the session java element.
	 * 
	 * @param persistenceUtil
	 *            the persistence middleware util
	 * @param e
	 *            the e
	 * @return the session java element
	 */
	public static JavaElement getSessionJavaElement(final PersistenceUtil persistenceUtil,
	                                                final JavaElement e) {
		// Object cached = session.get(JavaElement.class, e.getPrimaryKey());
		// if (cached != null) {
		// return (JavaElement) cached;
		// }
		return null;
	}
	
}
