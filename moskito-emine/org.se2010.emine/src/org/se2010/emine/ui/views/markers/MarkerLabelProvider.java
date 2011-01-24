package org.se2010.emine.ui.views.markers;

import java.net.URL;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.se2010.emine.*;

 

public class MarkerLabelProvider extends LabelProvider implements ITableLabelProvider{
	
	/**
	   * Returns column specific text
	   * 
	   * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	   */
	  public String getColumnText(Object obj, int index) {
	    IMarker marker = (IMarker) obj;
	    switch (index) {
	      case 2 :
	        if (marker.getAttribute(
	            eMineProblemViewMarker.MARKER_ATTRIBUTES[2], false))
	          return "Transient Marker";
	        else
	          return "Persistent Marker";

	      case 1 :
	        return marker.getAttribute(IMarker.MESSAGE, "no message");

	      case 0 :
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
	  public Image getColumnImage(Object obj, int index) {
	    if (index < 1) {
	      return createImageFor("icons/sample.gif");
	    }
	    return null;
	  }

	  private Image createImageFor(String gifFile) {
	      
	    // FIXED - removed deprecation
	    // URL url = MarkerViewPlugin.getDefault().find(new Path(gifFile));
	    URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path(gifFile), null);      
	    ImageDescriptor id = ImageDescriptor.createFromURL(url);
	    return id.createImage();
	  }

 

}
