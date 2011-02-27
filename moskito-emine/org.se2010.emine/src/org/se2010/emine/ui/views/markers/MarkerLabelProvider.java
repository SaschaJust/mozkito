package org.se2010.emine.ui.views.markers;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
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

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}
 
 

}
