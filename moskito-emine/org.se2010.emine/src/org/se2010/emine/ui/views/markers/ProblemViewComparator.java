package org.se2010.emine.ui.views.markers;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.se2010.emine.artifacts.ProblemArtifact;
import org.se2010.emine.artifacts.ProblemArtifactTypeList;

public class ProblemViewComparator extends ViewerComparator 
{
	private int propertyIndex;
	private int direction;

	private static final int DESCENDING = 1;
	
	public ProblemViewComparator() 
	{
		this.propertyIndex = 1;
		direction = DESCENDING;
	}



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
	 * This function enables sorting by column.
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		int cat1 = category(e1);
		int cat2 = category(e2);
		int rc = 0;
		String name1 = "";
		String name2 = "";

		if (propertyIndex == 0) {
			if (cat1 != cat2)
				rc = cat1 - cat2;

			if (viewer == null || !(viewer instanceof TreeViewer)) {
				name1 = e1.toString();
				name2 = e2.toString();
			} else {
				IBaseLabelProvider prov = ((TreeViewer) viewer)
						.getLabelProvider();
				if (prov instanceof ITableLabelProvider) {
					ITableLabelProvider lprov = (ITableLabelProvider) prov;
					name1 = lprov.getColumnText(e1, this.propertyIndex);
					name2 = lprov.getColumnText(e2, this.propertyIndex);
				} else {
					name1 = e1.toString();
					name2 = e2.toString();
				}
				rc = name1.compareToIgnoreCase(name2);
			}
		} else {
			if (cat1 == 1) {
				ProblemArtifactTypeList l1 = (ProblemArtifactTypeList) e1;
				ProblemArtifactTypeList l2 = (ProblemArtifactTypeList) e2;

				name1 = l1.getType();
				name2 = l2.getType();
			}
			if (cat1 == 2) {
				ProblemArtifact a1 = (ProblemArtifact) e1;
				ProblemArtifact a2 = (ProblemArtifact) e2;

				String Key = null;

				for (int i = 0; i < propertyIndex; i++) {
					Key = a1.getMap().keySet().iterator().next();
				}
				name1 = a1.getMap().get(Key);
				name2 = a2.getMap().get(Key);
			}

			if (!(name1 == null || name2 == null))
				rc = name1.compareToIgnoreCase(name2);
		}
		// If descending order, flip the direction
		if (direction == DESCENDING) {
			rc = -rc;
		}
		return rc;
	}

	@Override
	public int category(Object o) {
		if (o instanceof ProblemArtifactTypeList)
			return 1;
		if (o instanceof ProblemArtifact)
			return 2;
		return 0;
	}
}