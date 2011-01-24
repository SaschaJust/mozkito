package org.se2010.emine.ui.views.markers;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.internal.dialogs.ViewComparator;
import org.eclipse.ui.internal.registry.ViewRegistry;
import org.se2010.emine.artifacts.Artifact;
import org.se2010.emine.artifacts.ProblemArtifact;

public class ProblemViewComparator extends ViewComparator {

	public ProblemViewComparator(ViewRegistry reg) {
		super(reg);
		this.propertyIndex = 0;
		direction = DESCENDING;
	}

	private int propertyIndex;
	private static final int DESCENDING = 1;
	private int direction = DESCENDING;

	public void setColumn(int column) {
		if (column == this.propertyIndex) {
			// Same column as last sort; toggle the direction
			direction = 1 - direction;
		} else {
			// New column; do an ascending sort
			this.propertyIndex = column;
			direction = DESCENDING;
		}
	}

	/**
	 * This function enables sorting by column. Since a list of ProblemArtifacts
	 * is provided for the view, the column names can be derived from the keys of
	 * the InformationMap of the first Artifact. Thereby columns can be sorted
	 * by comparing the different column values with each other.
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		ProblemArtifact a1 = (ProblemArtifact) e1;
		ProblemArtifact a2 = (ProblemArtifact) e2;
		int rc = 0;

		String compareTo1 = null;
		String compareTo2 = null;

		String Key = null;

		for (int i = 0; i < propertyIndex; i++) {
			Key = a1.getMap().keySet().iterator().next();
		}
		compareTo1 = a1.getMap().get(Key);
		compareTo2 = a2.getMap().get(Key);
		rc = compareTo1.compareTo(compareTo2);
		// If descending order, flip the direction
		if (direction == DESCENDING) {
			rc = -rc;
		}
		return rc;
	}

}
