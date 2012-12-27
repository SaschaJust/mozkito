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
package issues.analysis;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collection;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.hiari.settings.ArgumentFactory;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.StringArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.ClassFinder;
import net.ownhero.dev.ioda.exceptions.WrongClassSearchMethodException;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.issues.analysis.IssuesAnalysis;

/**
 * The Class Main.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class Main {
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(final String[] args) {
		
		try {
			final Settings settings = new Settings();
			
			final StringArgument.Options analysisClassArgOption = new StringArgument.Options(
			                                                                                 settings.getRoot(),
			                                                                                 "analysisClass",
			                                                                                 "Analysis class to be executed after start.",
			                                                                                 null, Requirement.required);
			final StringArgument analysisClassArg = ArgumentFactory.create(analysisClassArgOption);
			
			final Collection<Class<? extends IssuesAnalysis>> classesExtendingClass = ClassFinder.getClassesExtendingClass(IssuesAnalysis.class.getPackage(),
			                                                                                                               IssuesAnalysis.class,
			                                                                                                               Modifier.ABSTRACT
			                                                                                                                       | Modifier.INTERFACE
			                                                                                                                       | Modifier.PRIVATE);
			
			final String analysisClassName = analysisClassArg.getValue();
			
			Class<? extends IssuesAnalysis> analysisClass = null;
			for (final Class<? extends IssuesAnalysis> klass : classesExtendingClass) {
				if (analysisClassName.equals(klass.getName())) {
					analysisClass = klass;
					break;
				}
			}
			
			if (analysisClass == null) {
				if (Logger.logError()) {
					Logger.error("Could not find any analysis class with full qualified class name %s implementing the interface %s.",
					             analysisClassName, IssuesAnalysis.class.getName());
				}
				return;
			}
			
			final Constructor<? extends IssuesAnalysis> constructor = analysisClass.getConstructor(new Class<?>[0]);
			final IssuesAnalysis analysisInstance = constructor.newInstance(new Object[0]);
			
			analysisInstance.setup(settings);
			
			if (settings.helpRequested()) {
				if (Logger.logAlways()) {
					Logger.always(settings.getHelpString());
				}
				throw new Shutdown();
			}
			
			analysisInstance.performAnalysis();
			analysisInstance.tearDown();
			
		} catch (SettingsParseError | ClassNotFoundException | WrongClassSearchMethodException | IOException
		        | ArgumentRegistrationException | ArgumentSetRegistrationException | InstantiationException
		        | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
		        | SecurityException e) {
			new Shutdown(e.getMessage());
		}
	}
}
