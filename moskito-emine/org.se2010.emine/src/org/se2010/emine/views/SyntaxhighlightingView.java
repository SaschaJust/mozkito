package org.se2010.emine.views;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.se2010.emine.artifacts.HighlightIconType;
import org.se2010.emine.artifacts.SyntaxhighlightingArtifact;


public class SyntaxhighlightingView extends ArtifactView{

	//TODO colorscala red to transparent
	private static final String M_RED = "org.se2010.emine.red_marker";
	private static final String M_GREEN = "org.se2010.emine.green_marker";
	private static final String M_YELLOW = "org.se2010.emine.yellow_marker";
	private static final String M_TRANS0 = "org.se2010.emine.trans0_marker";
	private static final String M_TRANS1 = "org.se2010.emine.trans1_marker";
	private static final String M_TRANS2 = "org.se2010.emine.trans2_marker";
	private static final String M_TRANS3 = "org.se2010.emine.trans3_marker";
	private static final String M_TRANS4 = "org.se2010.emine.trans4_marker";
	private List<IFile> filelist = new LinkedList<IFile>();
		
	public void  updateSyntaxhighlightingView(SyntaxhighlightingArtifact artifact){
		Map<Integer,HighlightIconType> map = artifact.getMap();
		String message = artifact.getMessage();
		IFile file = artifact.getFile();
		if (!filelist.contains(file)) filelist.add(file);
		deleteMarkers(file);
		for (Map.Entry<Integer,HighlightIconType> mark : map.entrySet()) {
			String markertype;
			switch(mark.getValue()){
				case RED : markertype=M_RED;
				case GREEN : markertype=M_GREEN;
				case YELLOW : markertype=M_YELLOW;
				case TRANSPARENT_1 : markertype=M_TRANS1; 
				case TRANSPARENT_2 : markertype=M_TRANS2; 
				case TRANSPARENT_3 : markertype=M_TRANS3; 
				case TRANSPARENT_4 : markertype=M_TRANS4; 
				default : markertype=M_TRANS0;
			}
		addMarker(file,markertype,message,mark.getKey());
		}
	}
	
	private void deleteMarkers(IFile file){
		try {
			file.deleteMarkers(M_RED, false, IResource.DEPTH_ZERO);
			file.deleteMarkers(M_GREEN, false, IResource.DEPTH_ZERO);
			file.deleteMarkers(M_YELLOW, false, IResource.DEPTH_ZERO);
			file.deleteMarkers(M_TRANS0, false, IResource.DEPTH_ZERO);
			file.deleteMarkers(M_TRANS1, false, IResource.DEPTH_ZERO);
			file.deleteMarkers(M_TRANS2, false, IResource.DEPTH_ZERO);
			file.deleteMarkers(M_TRANS3, false, IResource.DEPTH_ZERO);
			file.deleteMarkers(M_TRANS4, false, IResource.DEPTH_ZERO);
		} catch (CoreException e) {
			throw new RuntimeException("error occured while removing the markers of the higlighting");
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
			throw new RuntimeException("error occured while setting markers for highlighting");
		}
	}

	@Override
	protected void checkViewProperties() {
		// not required for prototyp Frontendconfig lookup
	}

	@Override
	public void clear() {
		for(IFile file:filelist){
			deleteMarkers(file);
		}
	}
	
}
