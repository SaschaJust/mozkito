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
package callgraph;

import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.callgraph.CallGraphToolChain;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Main {
	
	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		CallGraphToolChain toolChain = new CallGraphToolChain();
		// toolChain.setName(toolChain.getClass().getSimpleName());
		// toolChain.start();
		// toolChain.join();
		toolChain.run();
		if (Logger.logInfo()) {
			Logger.info("PPA.Main: All done. cerio!");
		}
	}
}
