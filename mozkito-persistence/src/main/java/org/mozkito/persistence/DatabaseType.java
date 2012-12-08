/*******************************************************************************
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
 ******************************************************************************/
/**
 * 
 */
package org.mozkito.persistence;

/**
 * The Enum DatabaseType.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public enum DatabaseType {
	
	/** The postgresql. */
	POSTGRESQL ("org.postgresql.Driver"), //$NON-NLS-1$
	/** The mysql. */
	MYSQL ("com.mysql.jdbc.Driver"), //$NON-NLS-1$
	/** The derby. */
	DERBY ("org.apache.derby.jdbc.EmbeddedDriver"); //$NON-NLS-1$
	
	/** The driver. */
	private String driver;
	
	/**
	 * Instantiates a new database type.
	 * 
	 * @param driver
	 *            the driver
	 */
	private DatabaseType(final String driver) {
		this.driver = driver;
	}
	
	/**
	 * Available.
	 * 
	 * @return true, if successful
	 */
	public boolean available() {
		try {
			return Class.forName(getDriver()) != null;
		} catch (final Exception e) {
			return false;
		}
	}
	
	/**
	 * Gets the driver.
	 * 
	 * @return the driver
	 */
	public String getDriver() {
		return this.driver;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
