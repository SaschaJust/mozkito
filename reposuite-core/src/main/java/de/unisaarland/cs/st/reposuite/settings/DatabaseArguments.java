package de.unisaarland.cs.st.reposuite.settings;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;

import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;

public class DatabaseArguments extends RepoSuiteArgumentSet {
	
	protected DatabaseArguments(RepoSuiteSettings settings, boolean isRequired) {
		super();
		try {
			addArgument(new StringArgument(settings, "database", "Name of the database", null, isRequired));
			addArgument(new StringArgument(settings, "dbUser", "User name for database. Default: miner", "miner",
			        isRequired));
			addArgument(new StringArgument(settings, "dbHost", "Name of database host. Default: localhost",
			        "localhost", isRequired));
			addArgument(new StringArgument(settings, "dbPassword", "Password for database. Default: miner", "miner",
			        isRequired));
			addArgument(new EnumArgument(settings, "dbType", "Possible values: PSQL, MYSQL", "PSQL", isRequired,
			        new String[] { "PSQL", "MYSQL" }));
		} catch (DuplicateArgumentException e) {
			Logger.getLogger(DatabaseArguments.class).error(e.getMessage(), e);
			throw new RuntimeException();
		}
	}
	
	@Override
	public SessionFactory getValue() {
		if (arguments.get("dbType").getValue().toString().equals("PSQL")) {
			return HibernateUtil.getPSQLSessionFactory(arguments.get("dbHost").getValue().toString(),
			        arguments.get("database").getValue().toString(), arguments.get("dbUser").getValue().toString(),
			        arguments.get("dbPassword").getValue().toString());
		} else if (arguments.get("dbType").getValue().toString().equals("MYSQL")) {
			return HibernateUtil.getMYSQLSessionFactory(arguments.get("dbHost").getValue().toString(),
			        arguments.get("database").getValue().toString(), arguments.get("dbUser").getValue().toString(),
			        arguments.get("dbPassword").getValue().toString());
		} else {
			Logger.getLogger(DatabaseArguments.class).error("Unsupported db type found! Abort!");
			throw new RuntimeException();
		}
	}
}
