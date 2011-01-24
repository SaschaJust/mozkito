package org.se2010.emine.ui.views.markers;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;


public class SyntaxhighlightingView {

	private static final String RED = "org.se2010.eMine.red_marker";
	private static final String GREEN = "org.se2010.eMine.green_marker";
	private static final String YELLOW = "org.se2010.eMine.yellow_marker";
	
	// at update first deleteMarkers(file); with file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
	
	private void addMarker(IFile file, String marker_type, String message, int lineNumber,int severity) {
		try {
			IMarker marker = file.createMarker(marker_type);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (CoreException e) {
		}
	}
	
}
