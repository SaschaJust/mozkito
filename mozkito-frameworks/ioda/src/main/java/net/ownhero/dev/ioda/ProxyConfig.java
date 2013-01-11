/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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
package net.ownhero.dev.ioda;

import net.ownhero.dev.kanuni.annotations.simple.NotNegative;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.CompareCondition;

/**
 * The Class ProxyConfig.
 *
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class ProxyConfig {
	
	/** The host. */
	private String  host;
	
	/** The port. */
	private int     port;
	
	/** The username. */
	private String  username;
	
	/** The password. */
	private String  password;
	
	/** The use socks. */
	private boolean useSocks;
	
	/**
	 * Instantiates a new proxy config.
	 *
	 * @param proxyHost the proxy host
	 * @param proxyPort the proxy port
	 * @param useSocks the use socks
	 */
	public ProxyConfig(final String proxyHost, @NotNull @NotNegative final int proxyPort, final boolean useSocks) {
		setHost(proxyHost);
		setPort(proxyPort);
		setUseSocks(useSocks);
	}
	
	/**
	 * Instantiates a new proxy config.
	 *
	 * @param proxyHost the proxy host
	 * @param proxyPort the proxy port
	 * @param username the username
	 * @param password the password
	 * @param useSocks the use socks
	 */
	public ProxyConfig(final String proxyHost, @NotNull @NotNegative final int proxyPort, final String username,
	        final String password, final boolean useSocks) {
		setHost(proxyHost);
		setPort(proxyPort);
		setUsername(username);
		setPassword(password);
		setUseSocks(useSocks);
	}
	
	/**
	 * Gets the host.
	 *
	 * @return the host
	 */
	public String getHost() {
		// PRECONDITIONS
		
		try {
			return this.host;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public String getPassword() {
		// PRECONDITIONS
		
		try {
			return this.password;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the port.
	 *
	 * @return the port
	 */
	public int getPort() {
		// PRECONDITIONS
		
		try {
			return this.port;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the username.
	 *
	 * @return the username
	 */
	public String getUsername() {
		// PRECONDITIONS
		
		try {
			return this.username;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the host.
	 *
	 * @param host the new host
	 */
	public void setHost(final String host) {
		// PRECONDITIONS
		try {
			this.host = host;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the password.
	 *
	 * @param password the new password
	 */
	public void setPassword(final String password) {
		// PRECONDITIONS
		try {
			this.password = password;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the port.
	 *
	 * @param port the new port
	 */
	public void setPort(@NotNegative final int port) {
		// PRECONDITIONS
		try {
			this.port = port;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.port, port,
			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter.");
		}
	}
	
	/**
	 * Sets the username.
	 *
	 * @param username the new username
	 */
	public void setUsername(final String username) {
		// PRECONDITIONS
		try {
			this.username = username;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the use socks.
	 *
	 * @param b the new use socks
	 */
	public void setUseSocks(final boolean b) {
		this.useSocks = b;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ProxyConfig [host=" + this.host + ", port=" + this.port + ", username=" + this.username + ", password="
		        + this.password + " useSocks=" + this.useSocks + "]";
	}
	
	/**
	 * Use socks.
	 *
	 * @return true, if successful
	 */
	public boolean useSocks() {
		return this.useSocks;
	}
	
}
