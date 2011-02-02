package org.se2010.emine.artifacts;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.TreeColumn;

public class ProblemArtifact implements Artifact {
	private String title; // serves as ID
	private Map<String, String> map; // map of String categories->String
										// properties
	private String message; // additional details
	private IFile file; // the file the artifact belongs to

	private String path;
	private ProblemArtifactTypeList myTypeList;
	private List<TreeColumn> myColumnList;

	public ProblemArtifact(String t, Map<String, String> m, String me, IFile f,
			String path) {
		this.title = t;
		this.map = m;
		this.message = me;
		this.file = f;
		this.path = path;
	}

	public Map<String, String> getMap() {
		return map;
	}

	public String getMessage() {
		return message;
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

	public String[] getDetails() {
		return (String[]) map.values().toArray();
	}

	public List<TreeColumn> getColumnList() {
		return myColumnList;
	}

	public void setColumnList(List<TreeColumn> myColumList) {
		this.myColumnList = myColumList;
	}

	@Override
	public String toString() {
		return getTitle();
	}
}
