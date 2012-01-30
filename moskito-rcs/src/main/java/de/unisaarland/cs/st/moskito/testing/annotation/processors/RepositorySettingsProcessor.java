/**
 * 
 */
package de.unisaarland.cs.st.moskito.testing.annotation.processors;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.ioda.CommandExecutor;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.ioda.IOUtils;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.exceptions.TestSettingsError;
import de.unisaarland.cs.st.moskito.rcs.RepositoryType;
import de.unisaarland.cs.st.moskito.testing.annotation.RepositorySetting;
import de.unisaarland.cs.st.moskito.testing.annotation.RepositorySettings;
import de.unisaarland.cs.st.moskito.testing.annotation.type.SourceType;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepositorySettingsProcessor implements MoskitoSettingsProcessor {
	
	/**
	 * @param setting
	 * @return
	 */
	public static String getPathName(final Class<?> aClass,
	                                 final RepositoryType type) {
		RepositorySetting setting = null;
		Annotation annotation = aClass.getAnnotation(RepositorySettings.class);
		if (annotation != null) {
			final RepositorySettings repositorySettings = (RepositorySettings) annotation;
			for (final RepositorySetting repositorySetting : repositorySettings.value()) {
				if (repositorySetting.type().equals(type)) {
					setting = repositorySetting;
					break;
				}
			}
			
		} else {
			annotation = aClass.getAnnotation(RepositorySetting.class);
			if (annotation != null) {
				final RepositorySetting repositorySetting = (RepositorySetting) annotation;
				if (repositorySetting.type().equals(type)) {
					setting = repositorySetting;
				}
			}
		}
		
		if (setting != null) {
			String baseDir = setting.baseDir();
			if ((baseDir.length() == 0) || baseDir.equals("TEMP")) {
				baseDir = System.getProperty("java.io.tmpdir");
			}
			
			// String uri = setting.uri();
			
			final String string = baseDir + File.separator + type.name().toLowerCase() + "_" + aClass.getSimpleName();
			
			return string;
		} else {
			return null;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.testing.annotation.processors.
	 * MoskitoSettingsProcessor#setup(java.lang.annotation.Annotation)
	 */
	@Override
	public void setup(final Class<?> aClass,
	                  final Annotation annotation) throws TestSettingsError {
		final List<RepositorySetting> settings = new LinkedList<RepositorySetting>();
		
		if (annotation.annotationType().equals(RepositorySetting.class)) {
			settings.add((RepositorySetting) annotation);
		} else {
			final RepositorySettings repositorySettings = (RepositorySettings) annotation;
			for (final Annotation repositorySetting : repositorySettings.value()) {
				settings.add((RepositorySetting) repositorySetting);
			}
		}
		
		for (final RepositorySetting repositorySetting : settings) {
			final RepositoryType type = repositorySetting.type();
			String baseDir = repositorySetting.baseDir();
			if ((baseDir.length() == 0) || baseDir.equals("TEMP")) {
				baseDir = System.getProperty("java.io.tmpdir");
			}
			final String uri = repositorySetting.uri();
			final SourceType sourceType = repositorySetting.sourceType();
			URL sourceURL = null;
			
			switch (sourceType) {
				case RESOURCE:
					sourceURL = aClass.getResource("/" + uri);
					break;
				case HTTP:
					try {
						sourceURL = new URL(uri);
					} catch (final MalformedURLException e) {
						throw new TestSettingsError("Could not create repository.", e);
					}
					break;
				default:
					break;
			}
			
			if (sourceURL != null) {
				final String[] split = sourceURL.getPath().split("/|" + File.pathSeparator);
				
				final String repoPath = getPathName(aClass, repositorySetting.type());
				File repoDir;
				try {
					FileUtils.createDir(new File(repoPath), FileShutdownAction.DELETE);
					repoDir = FileUtils.createDir(new File(repoPath + File.separator
					                                      + FileUtils.getUnpackedName(new File(sourceURL.toURI()))),
					                              FileShutdownAction.DELETE);
				} catch (final URISyntaxException e2) {
					throw new TestSettingsError(e2);
					
				}
				final String fileSuffix = split.length > 0
				                                          ? split[split.length - 1]
				                                          : sourceURL.getPath();
				try {
					final File file = IOUtils.getTemporaryCopyOfFile(type.name().toLowerCase(), fileSuffix,
					                                                 sourceURL.toURI());
					try {
						FileUtils.SupportedPackers.valueOf(fileSuffix.replaceFirst(".*\\.", "").toUpperCase());
						if (!FileUtils.unpack(file, new File(repoPath))) {
							throw new TestSettingsError("Could not create repository.");
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
								returnValue += execute.getFirst();
								execute = CommandExecutor.execute("svnadmin",
								                                  new String[] { "load", repoDir.getAbsolutePath() },
								                                  repoDir, file.toURI().toURL().openStream(), null);
								returnValue += execute.getFirst();
							} catch (final IOException e1) {
								throw new TestSettingsError(e);
							}
							
						}
					}
					
				} catch (final IOException e) {
					throw new TestSettingsError("Could not create repository.", e);
				} catch (final URISyntaxException e) {
					throw new TestSettingsError("Could not create repository.", e);
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.testing.annotation.processors.
	 * MoskitoSettingsProcessor#tearDown(java.lang.annotation.Annotation)
	 */
	@Override
	public void tearDown(final Class<?> aClass,
	                     final Annotation annotation) throws TestSettingsError {
		
	}
	
}
