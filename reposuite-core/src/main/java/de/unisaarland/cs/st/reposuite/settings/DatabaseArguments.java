package de.unisaarland.cs.st.reposuite.settings;

import java.util.Map;

import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.persistence.DatabaseType;
import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
import de.unisaarland.cs.st.reposuite.utils.JavaUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class DatabaseArguments extends RepoSuiteArgumentSet {
	
	protected DatabaseArguments(final RepositorySettings settings, final boolean isRequired) {
		super();
		addArgument(new StringArgument(settings, "database.name", "Name of the database", null, isRequired));
		addArgument(new MaskedStringArgument(settings, "database.user", "User name for database. Default: miner",
		        "miner", isRequired));
		addArgument(new StringArgument(settings, "database.host", "Name of database host. Default: localhost",
		        "localhost", isRequired));
		addArgument(new MaskedStringArgument(settings, "database.password", "Password for database. Default: miner",
		        "miner", isRequired));
		addArgument(new EnumArgument(settings, "database.type", "Possible values: "
		        + JavaUtils.enumToString(DatabaseType.POSTGRESQL), DatabaseType.POSTGRESQL.toString(), isRequired,
		        JavaUtils.enumToArray(DatabaseType.POSTGRESQL)));
		addArgument(new StringArgument(settings, "database.driver", "Default: org.postgresql.Driver",
		        "org.postgresql.Driver", isRequired));
	}
	
	@Override
	public HibernateUtil getValue() {
		Map<String, RepoSuiteArgument> arguments = getArguments();
		
		if (JavaUtils.AnyNull(arguments.get("database.host").getValue(), arguments.get("database.name").getValue(),
		        arguments.get("database.user").getValue(), arguments.get("database.password").getValue(), arguments
		                .get("database.type").getValue(), arguments.get("database.driver").getValue())) {
			return null;
		}
		
		HibernateUtil.createSessionFactory(arguments.get("database.host").getValue().toString(),
		        arguments.get("database.name").getValue().toString(), arguments.get("database.user").getValue()
		                .toString(), arguments.get("database.password").getValue().toString(),
		        arguments.get("database.type").getValue().toString(), arguments.get("database.driver").getValue()
		                .toString());
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
