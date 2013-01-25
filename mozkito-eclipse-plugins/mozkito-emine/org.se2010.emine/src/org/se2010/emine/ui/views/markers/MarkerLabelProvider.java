/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


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
