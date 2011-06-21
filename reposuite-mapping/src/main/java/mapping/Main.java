/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/**
 * 
 */
package mapping;

import net.ownhero.dev.kanuni.instrumentation.KanuniAgent;
import de.unisaarland.cs.st.reposuite.mapping.Mapping;
import de.unisaarland.cs.st.reposuite.mapping.Scoring;
import net.ownhero.dev.kisa.Logger;

/**
 * @author just
 * 
 */
public class Main {
	
	static {
		KanuniAgent.initialize();
	}
	
	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		try {
			Scoring scoring = new Scoring();
			scoring.setName(scoring.getClass().getSimpleName());
			scoring.start();
			scoring.join();
			
			Mapping mapping = new Mapping();
			mapping.setName(mapping.getClass().getSimpleName());
			mapping.start();
			mapping.join();
			
			if (Logger.logInfo()) {
				Logger.info("Mappings.Main: All done. cerio!");
			}
		} catch (InterruptedException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new RuntimeException(e);
		}
	}
	
}
