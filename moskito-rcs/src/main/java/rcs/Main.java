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
package rcs;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.kanuni.instrumentation.KanuniAgent;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.GraphBuilder;
import de.unisaarland.cs.st.moskito.RepositoryToolchain;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
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
			final RepositoryToolchain rCS = new RepositoryToolchain();
			rCS.setName(rCS.getClass().getSimpleName());
			rCS.start();
			rCS.join();
			
			if (Logger.logInfo()) {
				Logger.info("RCS.Main: RepositoryToolChain finished. Starting GraphToolChain ...");
			}
			
			final Thread graphBuilderThread = new Thread(
			                                             new GraphBuilder(rCS.getRepository(), rCS.getPersistenceUtil()));
			graphBuilderThread.start();
			graphBuilderThread.join();
			
			if (Logger.logInfo()) {
				Logger.info("RCS.Main: All done. cerio!");
			}
		} catch (final Shutdown e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		} catch (final InterruptedException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		}
	}
	
}
