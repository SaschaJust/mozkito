package org.se2010.emine.ui.views.markers;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class ProblemViewLabelProvider extends LabelProvider implements ILabelProvider{
	 
	public String getColumnText(Object obj, int index){
         return getText(obj);
      }
 
      public Image getColumnImage(Object obj, int index){
         return getImage(obj);
      }
 
      public Image getImage(Object obj){
         return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
         }

}
