package org.se2010.emine.artifacts;

import org.se2010.emine.events.IEMineEvent;


/**
 * This Artifact is sent over the EventBus, if the user changes the back-end-configuration. 
 *
 * @author  Andreas Rau
 * @version 02/2011 1.0
 */
public class ConfigurationArtifact implements IEMineEvent, IArtifact 
{
	private final String projectname;
	private final String Drepository_uri;
	private final String Drepository_user;
	private final String Drepository_password;
	private final int    Dcache_size;
	private final String Ddatabase_type;
	private final String Dlog_level;
	private final String Drepository_type;
	private final String vmArg;
	private final String Ddatabase_user;

	//default values
	private static final String  D_DATABASE_DRIVER   = "org.postgresql.Driver";
	private static final  String D_DATABASE_PASSWORD = "miner";
	private static final  String D_DATABASE_NAME     = "miner";
	private static final  String D_DATABASE_USER     = "miner";

	/**
	 * 
	 * @param projectname
	 * @param Drepository_user
	 * @param Drepository_password
	 * @param Drepository_uri
	 * @param Dcache_size
	 * @param Ddatabase_type
	 * @param Dlog_level
	 * @param Drepository_type
	 * @param vmArg
	 */
	public ConfigurationArtifact(String projectname, String Drepository_user, String Drepository_password, String Drepository_uri, int Dcache_size, String Ddatabase_type, String Dlog_level, String Drepository_type, String vmArg){

		this.projectname = projectname;
		
		if (projectname == null) 
		{
			this.Ddatabase_user = D_DATABASE_USER;
		}
		else
		{
			this.Ddatabase_user = projectname;
		}
		
		this.Drepository_uri      = Drepository_uri;
		this.Drepository_user     = Drepository_user;
		this.Drepository_password = Drepository_password;
		this.Dcache_size          = Dcache_size;
		this.Ddatabase_type       = Ddatabase_type;
		this.Dlog_level           = Dlog_level;
		this.Drepository_type     = Drepository_type;
		this.vmArg                = vmArg;
	}

	
	public String getTitle() {
		return projectname;
	}

	public String getProjectname() {
		return projectname;
	}

	public String getDrepository_uri() {
		return Drepository_uri;
	}

	public String getDrepository_user() {
		return Drepository_user;
	}

	public String getDrepository_password() {
		return Drepository_password;
	}

	public int getDcache_size() {
		return Dcache_size;
	}

	public String getDdatabase_type() {
		return Ddatabase_type;
	}

	public String getDlog_level() {
		return Dlog_level;
	}

	public String getDdatabase_driver() {
		return D_DATABASE_DRIVER;
	}

	public String getDrepository_type() {
		return Drepository_type;
	}

	public String getDdatabase_password() {
		return D_DATABASE_PASSWORD;
	}

	public String getDdatabase_name() {
		return D_DATABASE_NAME;
	}

	public String getDdatabase_user() {
		return this.Ddatabase_user;
	}
	
	public String getVMarg(){
		return this.vmArg;
	}



}
