package org.se2010.emine.ui.views.markers;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.se2010.emine.artifacts.ProblemArtifact;

public class ProblemViewFilter extends ViewerFilter {
	private String searchString;

	public ProblemViewFilter() {
		// TODO Auto-generated constructor stub
	}

	public void setSearchText(String s) {
		// Search must be a substring of the existing value
		this.searchString = ".*" + s + ".*";
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (searchString == null || searchString.length() == 0) {
			return true;
		}
		ProblemArtifact a = (ProblemArtifact) element;
	
		return a.getMap().containsValue(searchString);
	}

}
