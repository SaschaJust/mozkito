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
package genealogies;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.kanuni.instrumentation.KanuniAgent;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.genealogies.GenealogyToolChain;

/**
 * The Class Main.
 *
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class Main {
	
	static {
		KanuniAgent.initialize();
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(final String[] args) {
		GenealogyToolChain genealogies;
		try {
			final Settings settings = new Settings();
			genealogies = new GenealogyToolChain(settings);
			genealogies.run();
			if (Logger.logInfo()) {
				Logger.info("All done. Cerio.");
			}
		} catch (final Shutdown e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
		} catch (final SettingsParseError e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
		}
	}
}
