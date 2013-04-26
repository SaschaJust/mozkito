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

package org.mozkito.testing.annotation.processors;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringEscapeUtils;

import org.mozkito.exceptions.TestSettingsError;
import org.mozkito.persons.elements.PersonFactory;
import org.mozkito.testing.DatabaseTest;
import org.mozkito.testing.VersionsTest;
import org.mozkito.testing.annotation.RepositorySetting;
import org.mozkito.testing.annotation.RepositorySettings;
import org.mozkito.testing.annotation.type.SourceType;
import org.mozkito.utilities.commons.JavaUtils;
import org.mozkito.utilities.datastructures.Tuple;
import org.mozkito.utilities.execution.CommandExecutor;
import org.mozkito.utilities.io.FileUtils;
import org.mozkito.utilities.io.FileUtils.FileShutdownAction;
import org.mozkito.utilities.io.IOUtils;
import org.mozkito.utilities.io.exceptions.FilePermissionException;
import org.mozkito.versions.Repository;
import org.mozkito.versions.RepositoryFactory;
import org.mozkito.versions.RepositoryType;
import org.mozkito.versions.exceptions.RepositoryOperationException;
import org.mozkito.versions.exceptions.UnregisteredRepositoryTypeException;

/**
 * The Class RepositorySettingsProcessor.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class RepositoryProcessor implements MozkitoSettingsProcessor {
	
	/**
	 * Gets the path name.
	 * 
	 * @param setting
	 *            the setting
	 * @param test
	 *            the test
	 * @return the path name
	 */
	public static String getPathName(@NotNull final RepositorySetting setting,
	                                 final VersionsTest test) {
		if (setting.id().isEmpty()) {
			throw new TestSettingsError("Empty ID string in " + setting);
		}
		
		String baseDir = setting.baseDir();
		
		if ((baseDir.isEmpty()) || "TEMP".equals(baseDir)) {
			baseDir = System.getProperty("java.io.tmpdir");
		} else {
			final File baseDirFile = new File(baseDir);
			
			if (!baseDirFile.exists()) {
				if (!baseDirFile.mkdirs()) {
					throw new TestSettingsError("Base directory can not be created: " + baseDir);
				}
			} else if (!baseDirFile.isDirectory()) {
				throw new TestSettingsError("Base directory is not a directory: " + baseDir);
			} else if (!baseDirFile.canWrite() || !baseDirFile.canRead()) {
				throw new TestSettingsError("Wrong permissions on base directory: " + baseDir);
			}
			
		}
		
		final StringBuilder builder = new StringBuilder();
		
		builder.append(baseDir).append(File.separator).append(test.getClass().getSimpleName()).append('_')
		       .append(setting.type().name().toLowerCase()).append("_").append(setting.id()).append("_")
		       .append(DigestUtils.md5Hex(setting.uri()));
		
		return builder.toString();
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.testing.annotation.processors. MoskitoSettingsProcessor#setup(java.lang.annotation.Annotation)
	 */
	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends DatabaseTest> void setup(final T test,
	                                           final Annotation annotation) {
		final VersionsTest vTest = (VersionsTest) test;
		final Map<String, Repository> map = new HashMap<String, Repository>();
		final List<RepositorySetting> settings = new LinkedList<RepositorySetting>();
		
		// check if we got a single or multiple setting annotation
		if (annotation.annotationType().equals(RepositorySetting.class)) {
			// add the single repository setting to our list
			settings.add((RepositorySetting) annotation);
		} else {
			final RepositorySettings repositorySettings = (RepositorySettings) annotation;
			
			// add all repository settings to our list
			for (final Annotation repositorySetting : repositorySettings.value()) {
				settings.add((RepositorySetting) repositorySetting);
			}
		}
		
		// create working directories for the repositories
		for (final RepositorySetting repositorySetting : settings) {
			final RepositoryType type = repositorySetting.type();
			
			// determine base directory (defaults to the TMP directory)
			String baseDir = repositorySetting.baseDir();
			if ((baseDir.length() == 0) || "TEMP".equals(baseDir)) {
				baseDir = System.getProperty("java.io.tmpdir");
			}
			
			final String uri = repositorySetting.uri();
			final SourceType sourceType = repositorySetting.sourceType();
			URL sourceURL = null;
			
			// determine repository URL
			switch (sourceType) {
				case RESOURCE:
					sourceURL = test.getClass().getResource("/" + uri);
					break;
				case HTTP:
					try {
						sourceURL = new URL(uri);
					} catch (final MalformedURLException e) {
						throw new TestSettingsError("Could not create URL to the repository.", e);
					}
					break;
				default:
					throw new TestSettingsError("Unsupported resource type: " + sourceType.name());
			}
			
			if (sourceURL != null) {
				final String[] split = sourceURL.getPath()
				                                .split("/|" + StringEscapeUtils.escapeJava(File.pathSeparator));
				
				final String repoPath = getPathName(repositorySetting, vTest);
				File repoDir;
				try {
					final File temporarySourceDirectory = new File(repoPath);
					vTest.addTemporarySourceDirectory(temporarySourceDirectory);
					FileUtils.createDir(temporarySourceDirectory, FileShutdownAction.DELETE);
					repoDir = FileUtils.createDir(new File(repoPath + File.separator
					                                      + FileUtils.getUnpackedName(new File(sourceURL.toURI()))),
					                              FileShutdownAction.DELETE);
				} catch (final URISyntaxException | FilePermissionException e2) {
					throw new TestSettingsError(e2);
					
				}
				final String fileSuffix = split.length > 0
				                                          ? split[split.length - 1]
				                                          : sourceURL.getPath();
				try {
					if (SourceType.RESOURCE.equals(repositorySetting.sourceType())) {
						final File file = IOUtils.getTemporaryCopyOfFile(type.name().toLowerCase(), fileSuffix,
						                                                 sourceURL.toURI());
						try {
							FileUtils.SupportedPackers.valueOf(fileSuffix.replaceFirst(".*\\.", "").toUpperCase());
							
							try {
								FileUtils.unpack(file, new File(repoPath));
							} catch (final FilePermissionException | IOException e) {
								throw new TestSettingsError("Could not create repository.", e);
							}
						} catch (final IllegalArgumentException e) {
							if (type.equals(RepositoryType.SUBVERSION)) {
								try {
									Integer returnValue = 0;
									if (Logger.logDebug()) {
										Logger.debug("Creating " + type.toString() + " repository at: "
										        + repoDir.getAbsolutePath());
									}
									Tuple<Integer, List<String>> execute = CommandExecutor.execute("svnadmin",
									                                                               new String[] {
									                                                                       "create",
									                                                                       "--config-dir",
									                                                                       System.getProperty("user.home")
									                                                                               + FileUtils.fileSeparator
									                                                                               + ".subversion",
									                                                                       repoDir.getAbsolutePath() },
									                                                               repoDir, null, null);
									returnValue = execute.getFirst();
									if (returnValue != 0) {
										throw new TestSettingsError("Creating the temporary " + type.name()
										        + " repository failed with exit code: " + returnValue);
									}
									execute = CommandExecutor.execute("svnadmin",
									                                  new String[] { "load", repoDir.getAbsolutePath() },
									                                  repoDir, file.toURI().toURL().openStream(), null);
									returnValue = execute.getFirst();
									if (returnValue != 0) {
										throw new TestSettingsError("Loading the " + type.name()
										        + " repository failed with exit code: " + returnValue);
									}
									
								} catch (final IOException e1) {
									throw new TestSettingsError(e);
								}
								
							}
						}
					} else {
						throw new UnsupportedOperationException(
						                                        "External repositories aren't yet support in RepositoryTests. Given source type: "
						                                                + repositorySetting.sourceType());
					}
					
					Repository repository = null;
					try {
						
						final Class<? extends Repository> repositoryHandler = RepositoryFactory.getRepositoryHandler(repositorySetting.type());
						final int ln = new Throwable().getStackTrace()[0].getLineNumber();
						final Class<?>[] parameterTypes = new Class<?>[] { PersonFactory.class };
						final Object[] arguments = new Object[] { new PersonFactory() };
						
						SANITY: {
							assert repositoryHandler != null;
							
							// make sure the two arrays above conform to each other.
							assert parameterTypes != null;
							assert arguments != null;
							assert parameterTypes.length == arguments.length;
							
							for (int i = 0; i < arguments.length; ++i) {
								assert ((arguments[i] == null) || parameterTypes[i].isAssignableFrom(arguments[i].getClass())) : "Actual parameter has type '"
								        + arguments[i].getClass() + "' but we expected '" + parameterTypes[i] + "'.";
							}
						}
						
						final Constructor<? extends Repository> repositoryConstructor = repositoryHandler.getConstructor(parameterTypes);
						
						if (repositoryConstructor != null) {
							// create the repository instance
							repository = repositoryConstructor.newInstance(arguments);
							assert repository != null;
						} else {
							// handle error
							final StringBuilder builder = new StringBuilder();
							
							builder.append("Tried to lookup constructor of '")
							       .append(repositoryHandler.getCanonicalName()).append("' with parameter types ")
							       .append(JavaUtils.arrayToString(parameterTypes))
							       .append(", but wasn't able to do so. Please fix '")
							       .append(getClass().getCanonicalName()).append(':').append(ln + 1).append('-')
							       .append(ln + 2).append("' with one of the following alternatives: ");
							
							@SuppressWarnings ("unchecked")
							final Constructor<? extends Repository>[] constructors = (Constructor<? extends Repository>[]) repositoryHandler.getConstructors();
							for (final Constructor<? extends Repository> constructor : constructors) {
								builder.append("Available constructor: ").append(constructor);
								
							}
							throw new TestSettingsError(builder.toString());
						}
					} catch (final InstantiationException | IllegalAccessException
					        | UnregisteredRepositoryTypeException | IllegalArgumentException
					        | InvocationTargetException | NoSuchMethodException | SecurityException e) {
						throw new TestSettingsError(e);
					}
					
					Condition.notNull(repository,
					                  "Variable '%s' in '%s'.", "repository", RepositoryProcessor.class.getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
					final File workingDir = FileUtils.createRandomDir("mozkito_versionstest_"
					                                                          + test.getClass().getSimpleName() + "_"
					                                                          + repositorySetting.id(), null,
					                                                  FileShutdownAction.DELETE);
					vTest.addWorkingDirectory(workingDir);
					repository.setup(repoDir.toURI(), workingDir, "master");
					map.put(repositorySetting.id(), repository);
				} catch (final IOException | URISyntaxException | RepositoryOperationException e) {
					throw new TestSettingsError("Could not create repository.", e);
				}
			} else {
				throw new TestSettingsError("URL to the resource is null while processing: " + repositorySetting);
			}
		}
		
		((VersionsTest) test).setRepositories(map);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.testing.annotation.processors.
	 * MoskitoSettingsProcessor#tearDown(java.lang.annotation.Annotation)
	 */
	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends DatabaseTest> void tearDown(final T test,
	                                              final Annotation annotation) {
		final VersionsTest vTest = (VersionsTest) test;
		for (final File workindDir : vTest.getWorkingDirectories()) {
			try {
				FileUtils.deleteDirectory(workindDir);
			} catch (final IOException e) {
				if (Logger.logWarn()) {
					Logger.warn(e);
				}
			}
		}
		
		for (final File sourceDir : vTest.getTemporarySourceDirectories()) {
			try {
				FileUtils.deleteDirectory(sourceDir);
			} catch (final IOException e) {
				if (Logger.logWarn()) {
					Logger.warn(e);
				}
			}
		}
	}
	
}
