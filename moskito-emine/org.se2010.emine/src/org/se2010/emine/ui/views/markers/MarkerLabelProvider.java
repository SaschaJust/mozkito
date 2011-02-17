package org.se2010.emine.ui.views.markers;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.se2010.emine.*;
import org.se2010.emine.artifacts.ProblemArtifact;

 

public class MarkerLabelProvider extends LabelProvider implements ITableLabelProvider{
	
	/**
	   * Returns column specific text
	   * 
	   * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	   */
	  public String getColumnText(Object obj, int index) {

		ProblemArtifact marker = (ProblemArtifact)obj;
	    switch (index) 
	    {
	    case 3 :
	    	return marker.getResource();
	    
	      case 2 :
	         return marker.getMessage();

	      case 0 :

	    	  return marker.getTitle();
	      case 1 :
	       return String.valueOf(marker.getId());

	      default :
	        return " " + getText(obj);
	    }

	  }

	  /**
	   * A common image is returned for all elements but only for the first column.
	   * 
	   * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	   */
	 /* public Image getColumnImage(Object obj, int index) {
	    if (index < 1) {
	      return createImageFor("icons/sample.gif");
	    }
	    return null;
	  }*/

	  private Image createImageFor(String gifFile) {
	      
	    // FIXED - removed deprecation
	    // URL url = MarkerViewPlugin.getDefault().find(new Path(gifFile));
	    URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path(gifFile), null);      
	    ImageDescriptor id = ImageDescriptor.createFromURL(url);
	    return id.createImage();
	  }

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}
 
 

}
