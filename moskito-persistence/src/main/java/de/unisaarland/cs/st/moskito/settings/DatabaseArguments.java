/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.settings;

import java.lang.reflect.Method;
import java.util.Map;

import net.ownhero.dev.andama.exceptions.ClassLoadingError;
import net.ownhero.dev.andama.settings.AndamaArgument;
import net.ownhero.dev.andama.settings.AndamaArgumentSet;
import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.settings.EnumArgument;
import net.ownhero.dev.andama.settings.MaskedStringArgument;
import net.ownhero.dev.andama.settings.StringArgument;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.persistence.DatabaseType;
import de.unisaarland.cs.st.moskito.persistence.PersistenceManager;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class DatabaseArguments extends AndamaArgumentSet<Boolean> {
	
	private final AndamaSettings settings;
	
	/**
	 * @param settings
	 * @param isRequired
	 */
	public DatabaseArguments(final AndamaSettings settings, final boolean isRequired, final String unit) {
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
	 * @see de.unisaarland.cs.st.moskito.settings.AndamaArgumentSet#getValue()
	 */
	@Override
	public Boolean getValue() {
		Map<String, AndamaArgument<?>> arguments = getArguments();
		
		if (JavaUtils.AnyNull(arguments.get("database.host").getValue(), arguments.get("database.name").getValue(),
				arguments.get("database.user").getValue(), arguments.get("database.password").getValue(),
				arguments.get("database.type").getValue(), arguments.get("database.driver").getValue(),
				arguments.get("database.middleware").getValue(), arguments.get("database.unit")
				.getValue())) {
			return false;
		}
		String className = PersistenceUtil.class.getPackage().getName() + "."
				+ arguments.get("database.middleware").getValue() + "Util";
		
		try {
			@SuppressWarnings ("unchecked")
			Class<PersistenceUtil> middlewareClass = (Class<PersistenceUtil>) Class.forName(className);
			
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
		} catch (ClassNotFoundException e) {
			if (Logger.logError()) {
				Logger.error("Could not initialize database middleware "
						+ arguments.get("database.middleware").getValue() + ".", e);
				Logger.error(new ClassLoadingError(e, className).analyzeFailureCause());
			}
			return false;
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error("Could not initialize database middleware "
						+ arguments.get("database.middleware").getValue() + ".", e);
			}
			return false;
		}
		return true;
	}
}
