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
package de.unisaarland.cs.st.reposuite.rcs;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.ClassFinder;
import net.ownhero.dev.ioda.exceptions.WrongClassSearchMethodException;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.exceptions.UnregisteredRepositoryTypeException;
import de.unisaarland.cs.st.reposuite.settings.RepositorySettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public final class RepositoryFactory {
	
	/**
	 * container for repository connector mappings
	 */
	private static Map<RepositoryType, Class<? extends Repository>> repositoryHandlers = new HashMap<RepositoryType, Class<? extends Repository>>();
	
	/**
	 * static registration of all modules extending {@link Repository}
	 */
	static {
		// ======== Repository handlers ========
		try {
			Package package1 = Repository.class.getPackage();
			Collection<Class<? extends Repository>> classesExtendingClass = ClassFinder.getClassesExtendingClass(package1,
			                                                                                                     Repository.class,
			                                                                                                     Modifier.ABSTRACT
			                                                                                                             | Modifier.INTERFACE
			                                                                                                             | Modifier.PRIVATE);
			
			for (Class<? extends Repository> klass : classesExtendingClass) {
				addRepositoryHandler((RepositoryType) klass.getMethod("getRepositoryType", new Class<?>[0])
				                                           .invoke(klass.getConstructor(new Class<?>[0])
				                                                        .newInstance(new Object[0]), new Object[0]),
				                     klass);
			}
		} catch (InvocationTargetException e) {
			if (Logger.logError()) {
				// check if someone missed to add a corresponding enum entry in
				// RepositoryType
				if (e.getCause() instanceof IllegalArgumentException) {
					Logger.error("You probably missed to add an enum constant to " + RepositoryType.getHandle()
					        + ". Error was: " + e.getCause().getMessage(), e.getCause());
				}
			}
			throw new UnrecoverableError(e);
		} catch (ClassNotFoundException e) {
			throw new UnrecoverableError(e);
		} catch (WrongClassSearchMethodException e) {
			throw new UnrecoverableError(e);
		} catch (IOException e) {
			throw new UnrecoverableError(e);
		} catch (IllegalArgumentException e) {
			throw new UnrecoverableError(e);
		} catch (SecurityException e) {
			throw new UnrecoverableError(e);
		} catch (IllegalAccessException e) {
			throw new UnrecoverableError(e);
		} catch (NoSuchMethodException e) {
			throw new UnrecoverableError(e);
		} catch (InstantiationException e) {
			throw new UnrecoverableError(e);
		}
	}
	
	/**
	 * registers a repository to the factory keyed by the {@link RepositoryType}
	 * and version string
	 * 
	 * @param repositoryIdentifier
	 *            not null
	 * @param repositoryClass
	 *            class object implementing {@link Repository}, not null
	 */
	private static void addRepositoryHandler(@NotNull final RepositoryType repositoryIdentifier,
	                                         @NotNull final Class<? extends Repository> repositoryClass) {
		Condition.isNull(repositoryHandlers.get(repositoryIdentifier),
		                 "The should not be a reposiotry with the same identifier already");
		
		if (RepositorySettings.debug) {
			if (Logger.logDebug()) {
				Logger.debug("Adding new RepositoryType handler " + repositoryIdentifier.toString() + ".");
			}
		}
		
		repositoryHandlers.put(repositoryIdentifier, repositoryClass);
		
		Condition.notNull(repositoryHandlers.get(repositoryIdentifier),
		                  "The must be a repository with the identifier just been created and assigned.");
		CompareCondition.equals(repositoryHandlers.get(repositoryIdentifier), repositoryClass,
		                        "The must be a repository with the identifier just been created and assigned.");
	}
	
	/**
	 * returns a repository class object to the corresponding
	 * repositoryIdentifier and version (=default if null)
	 * 
	 * @param repositoryIdentifier
	 *            not null
	 * @return the corresponding {@link Repository} class object
	 * @throws UnregisteredRepositoryTypeException
	 *             if no matching repository class object could be found in the
	 *             registry
	 */
	@NoneNull
	public static Class<? extends Repository> getRepositoryHandler(final RepositoryType repositoryIdentifier) throws UnregisteredRepositoryTypeException {
		if (RepositorySettings.debug) {
			if (Logger.logDebug()) {
				Logger.debug("Requesting repository handler for " + repositoryIdentifier.toString() + ".");
			}
		}
		Class<? extends Repository> repositoryClass = repositoryHandlers.get(repositoryIdentifier);
		
		if (repositoryClass == null) {
			throw new UnregisteredRepositoryTypeException("Unsupported repository type `"
			        + repositoryIdentifier.toString() + "`");
		} else {
			return repositoryClass;
		}
	}
	
	/**
	 * private constructor avoids instantiation
	 */
	private RepositoryFactory() {
	}
	
	/**
	 * @return the simple class name
	 */
	public String getHandle() {
		return this.getClass().getSimpleName();
	}
}
