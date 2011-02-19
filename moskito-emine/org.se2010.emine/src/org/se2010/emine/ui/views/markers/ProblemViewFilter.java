package org.se2010.emine.ui.views.markers;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.dialogs.PatternFilter;
import org.se2010.emine.artifacts.ProblemArtifact;

public class ProblemViewFilter extends PatternFilter {

	@Override
	protected boolean isLeafMatch(Viewer viewer, Object element) {
		if (element instanceof ProblemArtifact) {
			ProblemArtifact artifact = (ProblemArtifact) element;
			for (int i = 0; i < artifact.getColumnList().size(); i++) {
				String labelText = ((ITableLabelProvider) ((StructuredViewer) viewer)
						.getLabelProvider()).getColumnText(element, i);
				if (labelText == null) {
					return false;
				}
				if (wordMatches(labelText))
					return true;
			}
		}
		return false;
	}

}