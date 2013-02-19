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
package org.mozkito.mojo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.MultiMatch;
import net.ownhero.dev.regex.Regex;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.util.DirectoryScanner;

/**
 * Goal that collects meta information such as module version and version archive hash and injects it into the project
 * resources. The generated resource "metadata.properties" can be accessed at runtime to access this information.
 */
@Mojo (name = "nls", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class MozkitoNLSMojo extends AbstractMojo {
	
	/** The base dir. */
	@Parameter (defaultValue = "${basedir}", required = true, readonly = true)
	private File                          baseDir;
	
	/**
	 * The inclusion pattern. This should be something like:
	 * <p>
	 * **\/model\/*
	 * </p>
	 */
	@Parameter (required = true, defaultValue = "**/*.java")
	private List<String>                  includes;
	
	/** The base directory. */
	@Parameter (defaultValue = "${basedir}", readonly = true, required = true)
	private File                          baseDirectory;
	
	/**
	 * The exclusion pattern. This should be something like:
	 * <p>
	 * **\/model\/*_.java
	 * </p>
	 */
	@Parameter
	private List<String>                  excludes;
	
	/** The source directory. */
	@Parameter (defaultValue = "${project.build.sourceDirectory}", required = true, readonly = true)
	private File                          sourceDirectory;
	
	/**
	 * Project instance, needed for attaching the buildinfo file. Used to add new source directory to the build.
	 */
	@Parameter (defaultValue = "${project}", readonly = true, required = true)
	private MavenProject                  project;
	
	/**
	 * Helper class to assist in attaching artifacts to the project instance. project-helper instance, used to make
	 * addition of resources simpler.
	 */
	@Component
	private MavenProjectHelper            projectHelper;
	
	private final Map<String, Properties> messages = new HashMap<>();
	
	private void checkNLSAccess() {
		// PRECONDITIONS
		
		try {
			if (this.sourceDirectory.exists() && this.sourceDirectory.isDirectory()) {
				final DirectoryScanner scanner = new DirectoryScanner();
				scanner.setIncludes(this.includes.toArray(new String[0]));
				if (this.excludes != null) {
					scanner.setExcludes(this.excludes.toArray(new String[0]));
				}
				
				scanner.setBasedir(this.sourceDirectory);
				scanner.setCaseSensitive(false);
				scanner.scan();
				final String[] files = scanner.getIncludedFiles();
				
				for (final String filePath : files) {
					final String simpleClassName = FilenameUtils.removeExtension(FilenameUtils.getBaseName(filePath));
					final Regex xenoRegex = new Regex("Messages\\.getString\\(\"(?!" + simpleClassName + ")");
					final Regex keyRegex = new Regex("Messages\\.getString\\(\"({KEY}[^\"]+)\"");
					List<String> lines;
					
					final File sourceFile = new File(this.sourceDirectory, filePath);
					
					getLog().debug("Parsing source file " + sourceFile.getAbsolutePath());
					getLog().debug("Using xeno access regex /" + xenoRegex.getPattern() + "/.");
					getLog().debug("Using key regex /" + keyRegex.getPattern() + "/.");
					
					try {
						lines = FileUtils.fileToLines(sourceFile);
						int lineNumber = 0;
						for (final String line : lines) {
							++lineNumber;
							
							if (line.contains("Messages.getString")) {
								getLog().debug("Found NLS access: " + line);
							}
							
							MultiMatch multiMatch = null;
							if ((multiMatch = keyRegex.findAll(line)) != null) {
								for (final Match match : multiMatch) {
									final String key = match.getGroup("KEY").getMatch();
									for (final Entry<String, Properties> entry : this.messages.entrySet()) {
										if (!entry.getValue().containsKey(key)) {
											getLog().warn("Missing key '" + key + "' in NLS resource: "
											                      + entry.getKey() + " (from " + filePath + ", line "
											                      + lineNumber + ")");
										}
									}
								}
							}
							
							if (xenoRegex.find(line) != null) {
								getLog().warn("Potential wrong NLS access at line " + lineNumber + " in " + filePath
								                      + ": " + line.trim() + ". Expecting key class qualifier to be: "
								                      + simpleClassName);
							}
						}
					} catch (final IOException e) {
						getLog().error("Could not read source file: " + filePath, e);
					}
				}
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	public void execute() throws MojoExecutionException {
		@SuppressWarnings ("unchecked")
		final List<Resource> resources = this.project.getResources();
		for (final Resource resource : resources) {
			final String directory = resource.getDirectory();
			final File dir = new File(directory);
			if (dir.exists() && dir.isDirectory()) {
				final DirectoryScanner scanner = new DirectoryScanner();
				scanner.setIncludes(new String[] { "**/messages.properties", "**/messages_*_*.properties" });
				scanner.setBasedir(directory);
				scanner.setCaseSensitive(false);
				scanner.scan();
				final String[] files = scanner.getIncludedFiles();
				
				for (final String filePath : files) {
					final Properties properties = new Properties();
					FileReader reader;
					try {
						reader = new FileReader(new File(dir, filePath));
						properties.load(reader);
						this.messages.put(filePath, properties);
					} catch (final IOException e) {
						getLog().error("Cannot read NLS file: " + filePath, e);
					}
				}
			}
		}
		
		checkNLSAccess();
	}
}
