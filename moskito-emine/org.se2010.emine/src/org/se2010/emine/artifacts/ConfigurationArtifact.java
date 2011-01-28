package org.se2010.emine.artifacts;

public class ConfigurationArtifact implements Artifact {

	private String projectname;
	private String Drepository_uri;
	private String Drepository_user;
	private String Drepository_password;
	private int Dcache_size;
	private String Ddatabase_type;
	private String Dlog_level;
	private String Drepository_type;
	private String vmArg;


	//default values
	private String Ddatabase_driver = "org.postgresql.Driver";
	private String Ddatabase_password = "miner";
	private String Ddatabase_name= "miner";
	private String Ddatabase_user= "miner";

	public ConfigurationArtifact(String projectname, String Drepository_user, String Drepository_password, String Drepository_uri, int Dcache_size, String Ddatabase_type, String Dlog_level, String Drepository_type, String vmArg){

		this.projectname = projectname;
		if (projectname != null) this.Ddatabase_user= projectname;
		
		this.Drepository_uri = Drepository_uri;
		this.Drepository_user = Drepository_user;
		this.Drepository_password = Drepository_password;
		this.Dcache_size = Dcache_size;
		this.Ddatabase_type = Ddatabase_type;
		this.Dlog_level = Dlog_level;
		this.Drepository_type = Drepository_type;
		this.vmArg = vmArg;
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
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
		return Ddatabase_driver;
	}

	public String getDrepository_type() {
		return Drepository_type;
	}

	public String getDdatabase_password() {
		return Ddatabase_password;
	}

	public String getDdatabase_name() {
		return Ddatabase_name;
	}

	public String getDdatabase_user() {
		return Ddatabase_user;
	}



}
