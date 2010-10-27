package de.unisaarland.cs.st.reposuite.settings;

import java.util.Map;

import org.hibernate.SessionFactory;

import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class DatabaseArguments extends RepoSuiteArgumentSet {
	
	protected DatabaseArguments(RepoSuiteSettings settings, boolean isRequired) {
		super();
		addArgument(new StringArgument(settings, "database", "Name of the database", null, isRequired));
		addArgument(new StringArgument(settings, "dbUser", "User name for database. Default: miner", "miner",
		        isRequired));
		addArgument(new StringArgument(settings, "dbHost", "Name of database host. Default: localhost", "localhost",
		        isRequired));
		addArgument(new StringArgument(settings, "dbPassword", "Password for database. Default: miner", "miner",
		        isRequired));
		addArgument(new EnumArgument(settings, "dbType", "Possible values: Postgresql, MySQL", "PostgreSQL",
		        isRequired, new String[] { "PostgreSQL", "MySQL" }));
		addArgument(new StringArgument(settings, "dbDriver", "Default: org.postgresql.Driver", "org.postgresql.Driver",
		        isRequired));
	}
	
	@Override
	public SessionFactory getValue() {
		Map<String, RepoSuiteArgument> arguments = getArguments();
		
		return HibernateUtil.createSessionFactory(arguments.get("dbHost").getValue().toString(),
		        arguments.get("database").getValue().toString(), arguments.get("dbUser").getValue().toString(),
		        arguments.get("dbPassword").getValue().toString(), arguments.get("dbType").getValue().toString(),
		        arguments.get("dbDriver").getValue().toString());
	}
}
