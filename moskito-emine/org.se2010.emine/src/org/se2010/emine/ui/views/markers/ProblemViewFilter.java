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
