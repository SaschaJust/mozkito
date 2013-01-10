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
package persons;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.persons.Persons;

/**
 * The Class Main.
 *
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class Main {
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(final String[] args) {
		try {
			final Settings settings = new Settings();
			final Persons persons = new Persons(settings);
			persons.setName(persons.getClass().getSimpleName());
			persons.start();
			persons.join();
		} catch (final Exception e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			throw new Shutdown();
		}
	}
	
}
