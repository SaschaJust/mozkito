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
package org.mozkito.issues.tracker.settings;

import java.net.URI;

import net.ownhero.dev.ioda.ProxyConfig;

/**
 * The Interface ITrackerOptions.
 *
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public interface ITrackerOptions {
	
	/**
	 * Setup.
	 *
	 * @param trackerUri the tracker uri
	 * @param trackerUser the tracker user
	 * @param trackerPassword the tracker password
	 * @param proxyConfig the proxy config
	 */
	void setup(URI trackerUri,
	           String trackerUser,
	           String trackerPassword,
	           ProxyConfig proxyConfig);
	
}
