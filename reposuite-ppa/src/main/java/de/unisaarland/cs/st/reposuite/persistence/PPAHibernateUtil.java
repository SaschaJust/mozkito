package de.unisaarland.cs.st.reposuite.persistence;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import de.unisaarland.cs.st.reposuite.ppa.model.JavaElement;
import de.unisaarland.cs.st.reposuite.utils.Logger;

public class PPAHibernateUtil {
	
	public static JavaElement getJavaElement(final HibernateUtil hibernateUtil, final JavaElement e) {
		Criteria criteria = hibernateUtil.createCriteria(e.getClass());
		criteria.add(Restrictions.eq("primaryKey", e.getPrimaryKey()));
		@SuppressWarnings("unchecked") List<JavaElement> elements = criteria.list();
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
	
	public static JavaElement getSessionJavaElement(final HibernateUtil hibernateUtil, final JavaElement e) {
		Session session = hibernateUtil.getSession();
		Object cached = session.get(JavaElement.class, e.getPrimaryKey());
		if (cached != null) {
			return (JavaElement) cached;
		}
		return null;
	}
	
}
