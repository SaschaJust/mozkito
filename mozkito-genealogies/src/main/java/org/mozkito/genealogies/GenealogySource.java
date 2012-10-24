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
package org.mozkito.genealogies;

import org.mozkito.genealogies.utils.OperationCollection;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Source;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class GenealogySource extends Source<OperationCollection> {
	
	public GenealogySource(final Group threadGroup, final Settings settings, final ChangeOperationReader reader) {
		super(threadGroup, settings, false);
		
		new ProcessHook<OperationCollection, OperationCollection>(this) {
			
			@Override
			public void process() {
				if (reader.hasNext()) {
					providePartialOutputData(new OperationCollection(reader.next()));
					if (!reader.hasNext()) {
						if (Logger.logDebug()) {
							Logger.debug("SET COMPLETED!");
						}
						setCompleted();
					}
				}
			}
		};
	}
}
