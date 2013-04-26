/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/

package org.mozkito.persistence;

import java.net.InetAddress;
import java.net.UnknownHostException;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.mozkito.utilities.commons.JavaUtils;

/**
 * The Class DatabaseOptions.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class DatabaseEnvironment {
	
	/**
	 * The Class ConfigurationException.
	 * 
	 * @author Sascha Just <sascha.just@mozkito.org>
	 */
	public class ConfigurationException extends Exception {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = -5439325790164101594L;
		
		/**
		 * Instantiates a new configuration exception.
		 */
		public ConfigurationException() {
		}
		
		/**
		 * Instantiates a new configuration exception.
		 * 
		 * @param message
		 *            the message
		 */
		public ConfigurationException(final String message) {
			super(message);
		}
		
		/**
		 * Instantiates a new configuration exception.
		 * 
		 * @param message
		 *            the message
		 * @param cause
		 *            the cause
		 */
		public ConfigurationException(final String message, final Throwable cause) {
			super(message, cause);
		}
		
		/**
		 * Instantiates a new configuration exception.
		 * 
		 * @param message
		 *            the message
		 * @param cause
		 *            the cause
		 * @param enableSuppression
		 *            the enable suppression
		 * @param writableStackTrace
		 *            the writable stack trace
		 */
		public ConfigurationException(final String message, final Throwable cause, final boolean enableSuppression,
		        final boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
		}
		
		/**
		 * Instantiates a new configuration exception.
		 * 
		 * @param cause
		 *            the cause
		 */
		public ConfigurationException(final Throwable cause) {
			super(cause);
		}
		
		/**
		 * Gets the simple name of the class.
		 * 
		 * @return the simple name of the class.
		 */
		public final String getClassName() {
			return JavaUtils.getHandle(ConfigurationException.class);
		}
	}
	
	/** The database type. */
	private final DatabaseType   databaseType;
	
	/** The database name. */
	private final String         databaseName;
	
	/** The database host. */
	private final String         databaseHost;
	
	/** The database username. */
	private final String         databaseUsername;
	
	/** The database password. */
	private final String         databasePassword;
	
	/** The database options. */
	private final ConnectOptions databaseOptions;
	
	/** The database unit. */
	private final String         databaseUnit;
	
	/** The url. */
	private String               url;
	
	/**
	 * Instantiates a new database options.
	 * 
	 * @param databaseType
	 *            the database type
	 * @param databaseName
	 *            the database name
	 * @param databaseHost
	 *            the database host
	 * @param databaseUsername
	 *            the database username
	 * @param databasePassword
	 *            the database password
	 * @param databaseOptions
	 *            the database options
	 * @param databaseUnit
	 *            the database unit
	 * @throws ConfigurationException
	 *             the configuration exception
	 */
	public DatabaseEnvironment(@NotNull final DatabaseType databaseType, @NotNull final String databaseName,
	        final String databaseHost, final String databaseUsername, final String databasePassword,
	        @NotNull final ConnectOptions databaseOptions, @NotNull final String databaseUnit)
	        throws ConfigurationException {
		// PRECONDITIONS
		
		try {
			this.databaseType = databaseType;
			this.databaseName = databaseName;
			this.databaseHost = databaseHost;
			this.databaseUsername = databaseUsername;
			this.databasePassword = databasePassword;
			this.databaseOptions = databaseOptions;
			this.databaseUnit = databaseUnit;
			
			if ((databaseHost != null) && !databaseHost.isEmpty() && !"localhost".equals(databaseHost)) { //$NON-NLS-1$
				try {
					InetAddress.getByName(databaseHost);
				} catch (final UnknownHostException e) {
					throw new ConfigurationException("Hostname can not be resolved: " + databaseHost, e); //$NON-NLS-1$
				}
			}
			
			if ((databasePassword != null) && (databaseUsername == null)) {
				throw new ConfigurationException("Password can't be set without a username."); //$NON-NLS-1$
			}
			
			if (!databaseType.available()) {
				throw new ConfigurationException(
				                                 "Database driver for '" + databaseType + "' is not present. Please add '" + databaseType.getDriver() + "' to your classpath."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			
			if (isLocal()) {
				if (DatabaseType.DERBY.equals(getDatabaseType())) {
					this.url = "jdbc:" + getDatabaseType() + ":" + getDatabaseName() + ";create=true"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				} else {
					throw new ConfigurationException(
					                                 "Could not build local URL for database type: " + getDatabaseType()); //$NON-NLS-1$
				}
			} else {
				this.url = "jdbc:" + getDatabaseType() + "://" + getDatabaseHost() + "/" + getDatabaseName(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the simple name of the class.
	 * 
	 * @return the simple name of the class.
	 */
	public final String getClassName() {
		return JavaUtils.getHandle(DatabaseEnvironment.class);
	}
	
	/**
	 * Gets the database driver.
	 * 
	 * @return the database driver
	 */
	public String getDatabaseDriver() {
		// PRECONDITIONS
		
		try {
			return getDatabaseType().getDriver();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the database host.
	 * 
	 * @return the database host
	 */
	public final String getDatabaseHost() {
		// PRECONDITIONS
		
		try {
			return this.databaseHost;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the database name.
	 * 
	 * @return the database name
	 */
	public final String getDatabaseName() {
		// PRECONDITIONS
		
		try {
			return this.databaseName;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.databaseName, "Field '%s' in '%s'.", "databaseName", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Gets the database options.
	 * 
	 * @return the database options
	 */
	public final ConnectOptions getDatabaseOptions() {
		// PRECONDITIONS
		
		try {
			return this.databaseOptions;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.databaseOptions,
			                  "Field '%s' in '%s'.", "databaseOptions", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Gets the database password.
	 * 
	 * @return the database password
	 */
	public final String getDatabasePassword() {
		// PRECONDITIONS
		
		try {
			return this.databasePassword;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the database type.
	 * 
	 * @return the database type
	 */
	public final DatabaseType getDatabaseType() {
		// PRECONDITIONS
		
		try {
			return this.databaseType;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.databaseType, "Field '%s' in '%s'.", "databaseType", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Gets the database unit.
	 * 
	 * @return the database unit
	 */
	public final String getDatabaseUnit() {
		// PRECONDITIONS
		
		try {
			return this.databaseUnit;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.databaseUnit, "Field '%s' in '%s'.", "databaseUnit", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Gets the database username.
	 * 
	 * @return the database username
	 */
	public final String getDatabaseUsername() {
		// PRECONDITIONS
		
		try {
			return this.databaseUsername;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the url.
	 * 
	 * @return the url
	 */
	public final String getUrl() {
		// PRECONDITIONS
		
		try {
			return this.url;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.url, "Field '%s' in '%s'.", "url", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Checks if is local.
	 * 
	 * @return true, if is local
	 */
	public final boolean isLocal() {
		return (this.databaseHost == null) || this.databaseHost.isEmpty();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("DatabaseOptions [databaseType="); //$NON-NLS-1$
		builder.append(this.databaseType);
		builder.append(", databaseName="); //$NON-NLS-1$
		builder.append(this.databaseName);
		builder.append(", databaseHost="); //$NON-NLS-1$
		builder.append(this.databaseHost);
		builder.append(", databaseUsername="); //$NON-NLS-1$
		builder.append(this.databaseUsername != null
		                                            ? "******" : "(unset)"); //$NON-NLS-1$ //$NON-NLS-2$
		builder.append(", databasePassword="); //$NON-NLS-1$
		builder.append(this.databasePassword != null
		                                            ? "******" : "(unset)"); //$NON-NLS-1$ //$NON-NLS-2$
		builder.append(", databaseOptions="); //$NON-NLS-1$
		builder.append(this.databaseOptions);
		builder.append(", databaseUnit="); //$NON-NLS-1$
		builder.append(this.databaseUnit);
		builder.append("]"); //$NON-NLS-1$
		return builder.toString();
	}
}
