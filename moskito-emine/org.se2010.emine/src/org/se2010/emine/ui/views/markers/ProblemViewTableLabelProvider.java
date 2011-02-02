package org.se2010.emine.ui.views.markers;

import java.util.Arrays;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.se2010.emine.artifacts.ProblemArtifact;
import org.se2010.emine.artifacts.ProblemArtifactTypeList;

public class ProblemViewTableLabelProvider implements ITableLabelProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof ProblemArtifact) {
			ProblemArtifact a = (ProblemArtifact) element;
			switch(columnIndex){
			case 0: return a.getTitle();
			case 1: return a.getList().getType();
			default: 
					return (a.getMap().get(a.getColumnList().get(columnIndex).getText()));
			}
		}
		if (element instanceof ProblemArtifactTypeList &&columnIndex == 0) {
			ProblemArtifactTypeList t = (ProblemArtifactTypeList) element;
			return t.getType();
		}

		return "";
	}

}
