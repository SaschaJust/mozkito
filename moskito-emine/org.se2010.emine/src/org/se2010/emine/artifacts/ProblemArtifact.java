package org.se2010.emine.artifacts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.swt.widgets.TreeColumn;


/**
 * This Artifact is the object model that is diplayed in the problem View. 
 *
 * @author  Kaushik Mukerjee
 * @version 02/2011 1.0
 */

public class ProblemArtifact implements IArtifact 
{
	private int 					id;
	private String 					resource;
	private String 					title;
	private Map<String, String> 	map; 
	private String 					message; 
	private List<TreeColumn> 		myColumnList;
	
	private String groupName;
 
	
    /**
     * 
     * @param id unique indentifier of the Artifact
     * @param title message displayed in identifier
     * @param message message to be displayed
     * @param resource name of the file or method being edited
     * @param map is a properties to store the type vs the artifact meant for extension of problem view
     */
	private ProblemArtifact(final int id, final String title, final String message, final String resource, final Map<String, String> map)
	{
		this.id           = id;
		this.title        = title;
		this.message      = message;
		this.resource     = resource;
		this.map          = map;
		this.groupName    = "None";
		this.myColumnList = new ArrayList<TreeColumn>();
	}
	
	public ProblemArtifact(final String title, final Map<String,String> map, final String message, final String resource) 
	{
		this(-1, title, message, resource, map);
	}
	
	
	public ProblemArtifact(final String title, final int id, final String message, final String resource) 
	{
		this(id, title, message, resource, new HashMap<String, String>());
	}

	
	public String getGroupName()
	{
		return this.groupName;
	}
	
	public void setGroupName(final String groupName)
	{
		this.groupName = groupName;
	}
	
	
	public String getMessage() 
	{
		return message;
	}

	public void setId(int id) 
	{
		this.id = id;
	}

	public int getId() 
	{
		return id;
	}

	public void setResource(String resource) 
	{
		this.resource = resource;
	}

	public String getResource() 
	{
		return resource;
	}

	public void setMap(final Map<String, String> map) 
	{
		this.map = map;
	}

	
	public Map<String, String> getMap()
	{
		return new HashMap<String, String>(map);
	}
	
	public String getTitle() 
	{
		return title;
	}

	public String[] getDetails() 
	{
		final String[] arr = new String[map.values().size()];
		return map.values().toArray(arr);
	}
	
	public void setColumnList(final List<TreeColumn> myColumList) 
	{
		this.myColumnList = myColumList;
	}

	public List<TreeColumn> getColumnList() 
	{
		return new ArrayList<TreeColumn>(myColumnList);
	}
}