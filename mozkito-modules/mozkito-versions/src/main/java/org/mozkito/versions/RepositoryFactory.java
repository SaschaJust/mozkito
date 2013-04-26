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
package org.mozkito.versions;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.ClassLoadingError;
import net.ownhero.dev.andama.exceptions.NoSuchConstructorError;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.persons.elements.PersonFactory;
import org.mozkito.utilities.loading.classpath.ClassFinder;
import org.mozkito.utilities.loading.classpath.exceptions.WrongClassSearchMethodException;
import org.mozkito.versions.concurrent.ConcurrentRepository;
import org.mozkito.versions.exceptions.UnregisteredRepositoryTypeException;

/**
 * A factory for creating Repository objects.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public final class RepositoryFactory {
	
	/** The Constant OMITTED_REPOSITORIES. */
	private static final Set<Class<? extends Repository>>                 OMITTED_REPOSITORIES = new HashSet<Class<? extends Repository>>() {
		                                                                                           
		                                                                                           /**
         * 
         */
		                                                                                           private static final long serialVersionUID = 8573860782538109069L;
		                                                                                           
		                                                                                           {
			                                                                                           add(ConcurrentRepository.class);
		                                                                                           }
	                                                                                           };
	
	/** container for repository connector mappings. */
	private static final Map<RepositoryType, Class<? extends Repository>> REPOSITORY_HANDLERS  = new HashMap<RepositoryType, Class<? extends Repository>>();
	
	/**
	 * static registration of all modules extending {@link Repository}
	 */
	static {
		// ======== Repository handlers ========
		final Package package1 = Repository.class.getPackage();
		Collection<Class<? extends Repository>> classesExtendingClass;
		try {
			classesExtendingClass = ClassFinder.getClassesExtendingClass(package1, Repository.class, Modifier.ABSTRACT
			        | Modifier.INTERFACE | Modifier.PRIVATE);
		} catch (final ClassNotFoundException e) {
			throw new ClassLoadingError(e, null);
		} catch (final WrongClassSearchMethodException | IOException e1) {
			throw new UnrecoverableError(e1);
		}
		
		Method getRepositoryType = null;
		final Class<?>[] constructorSignature = new Class<?>[] { PersonFactory.class };
		Constructor<? extends Repository> constructor = null;
		final Object[] constructorArguments = new Object[] { new PersonFactory() };
		Repository repository = null;
		RepositoryType repositoryType = null;
		
		REPOSITORY_CLASSES: for (final Class<? extends Repository> klass : classesExtendingClass) {
			try {
				// skip omitted repository classes
				if (OMITTED_REPOSITORIES.contains(klass)) {
					continue REPOSITORY_CLASSES;
				}
				
				getRepositoryType = klass.getMethod("getRepositoryType", new Class<?>[0]); //$NON-NLS-1$
				constructor = klass.getConstructor(constructorSignature);
				repository = constructor.newInstance(constructorArguments);
				repositoryType = (RepositoryType) getRepositoryType.invoke(repository, new Object[0]);
				addRepositoryHandler(repositoryType, klass);
			} catch (final InvocationTargetException e) {
				if (Logger.logError()) {
					// check if someone missed to add a corresponding enum entry in
					// RepositoryType
					if (e.getCause() instanceof IllegalArgumentException) {
						Logger.error(e.getCause(), "You probably missed to add an enum constant to '%s'.",
						             RepositoryType.getClassName());
					}
				}
				throw new UnrecoverableError(e);
			} catch (final IllegalArgumentException e) {
				throw new net.ownhero.dev.andama.exceptions.InstantiationError(e, klass, constructor,
				                                                               constructorArguments);
			} catch (final SecurityException | IllegalAccessException e) {
				throw new UnrecoverableError(e);
			} catch (final NoSuchMethodException e) {
				throw new NoSuchConstructorError(e, klass, constructorSignature);
			} catch (final InstantiationException e) {
				throw new net.ownhero.dev.andama.exceptions.InstantiationError(e, klass, constructor,
				                                                               constructorArguments);
			}
		}
		
	}
	
	/**
	 * registers a repository to the factory keyed by the {@link RepositoryType} and version string.
	 * 
	 * @param repositoryIdentifier
	 *            not null
	 * @param repositoryClass
	 *            class object implementing {@link Repository}, not null
	 */
	private static void addRepositoryHandler(@NotNull final RepositoryType repositoryIdentifier,
	                                         @NotNull final Class<? extends Repository> repositoryClass) {
		Condition.isNull(RepositoryFactory.REPOSITORY_HANDLERS.get(repositoryIdentifier),
		                 "The should not be a reposiotry with the same identifier already");
		
		if (Logger.logDebug()) {
			Logger.debug("Adding new RepositoryType handler " + repositoryIdentifier.toString() + ".");
		}
		
		RepositoryFactory.REPOSITORY_HANDLERS.put(repositoryIdentifier, repositoryClass);
		
		Condition.notNull(RepositoryFactory.REPOSITORY_HANDLERS.get(repositoryIdentifier),
		                  "The must be a repository with the identifier just been created and assigned.");
		CompareCondition.equals(RepositoryFactory.REPOSITORY_HANDLERS.get(repositoryIdentifier), repositoryClass,
		                        "The must be a repository with the identifier just been created and assigned.");
	}
	
	/**
	 * returns a repository class object to the corresponding repositoryIdentifier and version (=default if null).
	 * 
	 * @param repositoryIdentifier
	 *            not null
	 * @return the corresponding {@link Repository} class object
	 * @throws UnregisteredRepositoryTypeException
	 *             if no matching repository class object could be found in the registry
	 */
	@NoneNull
	public static Class<? extends Repository> getRepositoryHandler(final RepositoryType repositoryIdentifier) throws UnregisteredRepositoryTypeException {
		if (Logger.logDebug()) {
			Logger.debug("Requesting repository handler for " + repositoryIdentifier.toString() + ".");
		}
		
		final Class<? extends Repository> repositoryClass = RepositoryFactory.REPOSITORY_HANDLERS.get(repositoryIdentifier);
		
		if (repositoryClass == null) {
			throw new UnregisteredRepositoryTypeException("Unsupported repository type `"
			        + repositoryIdentifier.toString() + "`");
		}
		return repositoryClass;
	}
	
	/**
	 * private constructor avoids instantiation.
	 */
	private RepositoryFactory() {
	}
	
	/**
	 * Gets the handle.
	 * 
	 * @return the simple class name
	 */
	public String getClassName() {
		return this.getClass().getSimpleName();
	}
}
