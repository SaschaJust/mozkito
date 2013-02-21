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
package org.mozkito.issues;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.Sink;
import net.ownhero.dev.hiari.settings.Settings;

import org.mozkito.issues.model.Report;

/**
 * The Class TrackerVoidSink.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class TrackerVoidSink extends Sink<Report> {
	
	/**
	 * Instantiates a new tracker void sink.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 */
	public TrackerVoidSink(final Group threadGroup, final Settings settings) {
		super(threadGroup, settings, false);
	}
	
}
