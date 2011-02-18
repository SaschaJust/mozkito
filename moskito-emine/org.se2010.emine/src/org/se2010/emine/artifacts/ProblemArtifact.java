package org.se2010.emine.artifacts;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.TreeColumn;

import java.util.Map;

import org.eclipse.core.resources.IFile;

public class ProblemArtifact implements IArtifact {
	private int id;
	private String resource;

	private String title; // serves as ID
	private Map<String, String> map; // map of String categories->String
										// properties
	private String message; // additional details
	private IFile file; // the file the artifact belongs to

	private ProblemArtifactTypeList myTypeList;
	private List<TreeColumn> myColumnList;

	public ProblemArtifact(String t, Map<String, String> m, String me, IFile f) 
	{
		this.title = t;
		this.map = m;
		this.message = me;
		this.file = f;
	}

	public ProblemArtifact(String t, int id, String me, String resource) {
		this.title = t;
		this.message = me;
		this.setId(id);
		this.setResource(resource);
	}

	public String getMessage() {
		return message;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getResource() {
		return resource;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}
	
	public Map<String, String> getMap()
	{
		return this.map;
	}

	public IFile getFile() {
		return file;
	}

	public void setTypeList(ProblemArtifactTypeList list) {
		myTypeList = list;
	}

	public ProblemArtifactTypeList getList() {
		return myTypeList;
	}

	public String getTitle() {
		return title;
	}

	public String[] getDetails() 
	{
		final String[] arr = new String[map.values().size()];
		return map.values().toArray(arr);
	}

	public List<TreeColumn> getColumnList() {
		return myColumnList;
	}

	public void setColumnList(List<TreeColumn> myColumList) {
		this.myColumnList = myColumList;
	}

}