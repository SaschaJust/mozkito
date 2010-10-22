package de.unisaarland.cs.st.reposuite.settings;

import java.util.Map;

import org.hibernate.SessionFactory;

import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
import de.unisaarland.cs.st.reposuite.utils.Logger;

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
		addArgument(new EnumArgument(settings, "dbType", "Possible values: PSQL, MYSQL", "PSQL", isRequired,
		        new String[] { "PSQL", "MYSQL" }));
	}
	
	@Override
	public SessionFactory getValue() {
		Map<String, RepoSuiteArgument> arguments = getArguments();
		if (arguments.get("dbType").getValue().toString().equals("PSQL")) {
			return HibernateUtil.getPSQLSessionFactory(arguments.get("dbHost").getValue().toString(),
			        arguments.get("database").getValue().toString(), arguments.get("dbUser").getValue().toString(),
			        arguments.get("dbPassword").getValue().toString());
		} else if (arguments.get("dbType").getValue().toString().equals("MYSQL")) {
			return HibernateUtil.getMYSQLSessionFactory(arguments.get("dbHost").getValue().toString(),
			        arguments.get("database").getValue().toString(), arguments.get("dbUser").getValue().toString(),
			        arguments.get("dbPassword").getValue().toString());
		} else {
			if (RepoSuiteSettings.logError()) {
				Logger.error("Unsupported db type found! Abort!");
			}
			throw new RuntimeException();
		}
	}
}
