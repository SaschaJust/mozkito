package de.unisaarland.cs.st.reposuite.settings;

import java.lang.reflect.Method;
import java.util.Map;

import de.unisaarland.cs.st.reposuite.persistence.DatabaseType;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceManager;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.utils.JavaUtils;
import net.ownhero.dev.kisa.Logger;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class DatabaseArguments extends RepoSuiteArgumentSet {
	
	private final RepoSuiteSettings settings;
	
	/**
	 * @param settings
	 * @param isRequired
	 */
	protected DatabaseArguments(final RepoSuiteSettings settings, final boolean isRequired, final String unit) {
		super();
		this.settings = settings;
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
		addArgument(new StringArgument(settings, "database.middleware", "Default: OpenJPA", "OpenJPA", isRequired));
		addArgument(new StringArgument(settings, "database.unit", "The persistence unit config tag used.", unit, true));
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgumentSet#getValue()
	 */
	@Override
	public Boolean getValue() {
		Map<String, RepoSuiteArgument> arguments = getArguments();
		
		if (JavaUtils.AnyNull(arguments.get("database.host").getValue(), arguments.get("database.name").getValue(),
		                      arguments.get("database.user").getValue(), arguments.get("database.password").getValue(),
		                      arguments.get("database.type").getValue(), arguments.get("database.driver").getValue(),
		                      arguments.get("database.middleware").getValue(), arguments.get("database.unit")
		                                                                                .getValue())) {
			return null;
		}
		
		try {
			@SuppressWarnings ("unchecked")
			Class<PersistenceUtil> middlewareClass = (Class<PersistenceUtil>) Class.forName(PersistenceUtil.class.getPackage()
			                                                                                                     .getName()
			        + "." + arguments.get("database.middleware").getValue() + "Util");
			
			Method method = middlewareClass.getMethod("createSessionFactory", String.class, String.class, String.class,
			                                          String.class, String.class, String.class, String.class);
			method.invoke(null, arguments.get("database.host").getValue().toString(), arguments.get("database.name")
			                                                                                   .getValue().toString(),
			              arguments.get("database.user").getValue().toString(), arguments.get("database.password")
			                                                                             .getValue().toString(),
			              arguments.get("database.type").getValue().toString(), arguments.get("database.driver")
			                                                                             .getValue().toString(),
			              arguments.get("database.unit").getValue().toString());
			PersistenceManager.registerMiddleware(middlewareClass);
			
			String toolInfo = PersistenceManager.getUtil().getToolInformation();
			this.settings.addToolInformation(middlewareClass.getSimpleName(), toolInfo);
		} catch (Exception e) {
			if (Logger.logError()) {
				System.err.println("Could not initialize database middleware "
				        + arguments.get("database.middleware").getValue() + ".");
				e.printStackTrace();
				Logger.error("Could not initialize database middleware "
				        + arguments.get("database.middleware").getValue() + ".", e);
			}
			return null;
		}
		
		return true;
	}
}
