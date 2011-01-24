package org.se2010.emine.ui.views.markers;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.se2010.emine.artifacts.SyntaxhighlightingArtifact;


public class SyntaxhighlightingView {

	//TODO colorscala red to transparent
	private static final String RED = "org.se2010.eMine.red_marker";
	private static final String GREEN = "org.se2010.eMine.green_marker";
	private static final String YELLOW = "org.se2010.eMine.yellow_marker";
	
	// at update first deleteMarkers(file); with file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
	
	void updateSyntaxhighlightingView(SyntaxhighlightingArtifact artifact){
		Map<Integer,String> map = artifact.getMap();
		String message = artifact.getMessage();
		IFile file = artifact.getFile();
		for (Map.Entry<Integer,String> mark : map.entrySet()) {
			String markertype;
			if (mark.getValue()=="red"){
				markertype=RED;
			}else if (mark.getValue()=="green"){
				markertype=GREEN;
			}else {
				markertype=YELLOW;
			}
			addMarker(file,markertype,message,mark.getKey());
		}
	}
	
	private void addMarker(IFile file, String marker_type, String message, int lineNumber) {
		try {
			IMarker marker = file.createMarker(marker_type);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (CoreException e) {
		}
	}
	
}
