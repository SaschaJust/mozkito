package org.se2010.emine.ui.views.markers;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.se2010.emine.artifacts.ProblemArtifact;
import org.se2010.emine.artifacts.ProblemArtifactTypeList;

public class ProblemViewContentProvider implements ITreeContentProvider {

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof List) 
			return ((List) parentElement).toArray();
		return null;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof ProblemArtifact)
			return ((ProblemArtifact) element).getList();
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof List)
			return (!((List) element).isEmpty());
		return false;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}
}
