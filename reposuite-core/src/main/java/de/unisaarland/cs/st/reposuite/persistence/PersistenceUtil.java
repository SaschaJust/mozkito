/**
 * 
 */
package de.unisaarland.cs.st.reposuite.persistence;

import java.util.List;

import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public interface PersistenceUtil {
	
	public void beginTransaction();
	
	public void commitTransaction();
	
	public Criteria<?> createCriteria(final Class<?> clazz);
	
	public void delete(final Annotated object);
	
	public void executeQuery(final String query);
	
	public RCSTransaction fetchRCSTransaction(final String id);
	
	public String getToolInformation();
	
	public String getType();
	
	public List<?> load(final Criteria<?> criteria);
	
	public void save(final Annotated object);
	
	public void saveOrUpdate(final Annotated object);
	
	public void shutdown();
	
	public void update(final Annotated object);
}
