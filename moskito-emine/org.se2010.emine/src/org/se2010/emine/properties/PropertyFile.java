/**
 * 
 */
package org.se2010.emine.properties;

import org.eclipse.core.internal.resources.Resource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * @author Amras
 *
 */
public class PropertyFile implements IPropertySource, IAdaptable {

	private String user;
	private String password;
	private String reponame;
	private String vmargs;
	private Resource res;
	
	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public Object getEditableValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		// TODO Auto-generated method stub
		return PropertyPage.getDescriptors();
	}

	@Override
	public Object getPropertyValue(Object id) {
		try {
			
			if (user.equals(id)){
				return res.getPersistentProperty(PropertyPage.AUTHOR_PROP_KEY);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public boolean isPropertySet(Object id) {
		// TODO Auto-generated method stub
		return false;
	}

	
	@Override
	public void resetPropertyValue(Object id) {
		// TODO Auto-generated method stub

	}

	
	@Override
	public void setPropertyValue(Object id, Object value) {
		// TODO Auto-generated method stub
		try {
			if (user.equals(id)){
				res.setPersistentProperty(PropertyPage.AUTHOR_PROP_KEY, (String) value); 
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
