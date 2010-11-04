package de.unisaarland.cs.st.reposuite.settings;

import java.util.Map;

import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class DatabaseArguments extends RepoSuiteArgumentSet {
	
	protected DatabaseArguments(final RepoSuiteSettings settings, final boolean isRequired) {
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
	public HibernateUtil getValue() {
		Map<String, RepoSuiteArgument> arguments = getArguments();
		
		HibernateUtil.createSessionFactory(arguments.get("dbHost").getValue().toString(), arguments.get("database")
		        .getValue().toString(), arguments.get("dbUser").getValue().toString(), arguments.get("dbPassword")
		        .getValue().toString(), arguments.get("dbType").getValue().toString(), arguments.get("dbDriver")
		        .getValue().toString());
		try {
			HibernateUtil hibernateUtil = HibernateUtil.getInstance();
			return hibernateUtil;
		} catch (UninitializedDatabaseException e) {
			
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return null;
		}
	}
}
