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
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class ProxyConfig {
	
	private String host;
	private int    port;
	private String username;
	private String password;
	
	public ProxyConfig(final String proxyHost, @NotNull @NotNegative final int proxyPort) {
		setHost(proxyHost);
		setPort(proxyPort);
	}
	
	public ProxyConfig(final String proxyHost, @NotNull @NotNegative final int proxyPort, final String username,
	        final String password) {
		setHost(proxyHost);
		setPort(proxyPort);
		setUsername(username);
		setPassword(password);
	}
	
	public String getHost() {
		// PRECONDITIONS
		
		try {
			return this.host;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public String getPassword() {
		// PRECONDITIONS
		
		try {
			return this.password;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public int getPort() {
		// PRECONDITIONS
		
		try {
			return this.port;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public String getUsername() {
		// PRECONDITIONS
		
		try {
			return this.username;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public void setHost(final String host) {
		// PRECONDITIONS
		try {
			this.host = host;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public void setPassword(final String password) {
		// PRECONDITIONS
		try {
			this.password = password;
		} finally {
			// POSTCONDITIONS
		}
	}
	
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
	
	public void setUsername(final String username) {
		// PRECONDITIONS
		try {
			this.username = username;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public String toString() {
		return "ProxyConfig [host=" + this.host + ", port=" + this.port + ", username=" + this.username + ", password="
		        + this.password + "]";
	}
	
}
