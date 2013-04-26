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
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

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
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import org.mozkito.utilities.io.FileUtils;

/**
 * Goal that checks if calls to NLS are valid, i.e. the key class is the same as the simple classname and the key exists
 * in every localized messages.properties file.
 */
@Mojo (name = "nls", defaultPhase = LifecyclePhase.COMPILE)
public class MozkitoNLSMojo extends AbstractMojo {
	
	/** The base dir. */
	@Parameter (defaultValue = "${basedir}", required = true, readonly = true)
	private File                          baseDir;
	
	/**
	 * The inclusion pattern. This should be something like:
	 * <p>
	 * &#42;&#42;/&#42;.java
	 * </p>
	 */
	@Parameter (required = true, defaultValue = "**/*.java")
	private List<String>                  includes;
	
	/** The base directory. */
	@Parameter (defaultValue = "${basedir}", readonly = true, required = true)
	private File                          baseDirectory;
	
	/**
	 * The exclusion pattern. This can be empty or should be something like:
	 * <p>
	 * &#42;&#42;/Messages_Is_No_NLS_Class/&#42;_.java
	 * </p>
	 */
	@Parameter
	private List<String>                  excludes;
	
	static final String                   filename = "nls-report.xml";
	
	/** The source directory. */
	@Parameter (defaultValue = "${project.build.sourceDirectory}", required = true, readonly = true)
	private File                          sourceDirectory;
	
	@Parameter (defaultValue = "${project.build.directory}", required = true, readonly = true)
	private File                          outputDirectory;
	
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
	
	/** The messages. */
	private final Map<String, Properties> messages = new HashMap<>();
	
	/**
	 * Check nls access.
	 */
	private void checkNLSAccess() {
		// PRECONDITIONS
		
		try {
			if (this.sourceDirectory.exists() && this.sourceDirectory.isDirectory()) {
				try {
					final File file = openXMLFile();
					final FileWriter writer = new FileWriter(file);
					final XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
					
					final Document document = new Document();
					final Element element = new Element("violations");
					
					final Element missings = new Element("missings", element.getNamespace());
					element.addContent(missings);
					
					final Element namings = new Element("namings", element.getNamespace());
					element.addContent(namings);
					
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
												warnMissing(missings, key, entry.getKey(), filePath, lineNumber);
											}
										}
									}
								}
								
								if (xenoRegex.find(line) != null) {
									warnNaming(namings, lineNumber, filePath, line.trim(), simpleClassName);
								}
							}
						} catch (final IOException e) {
							getLog().error("Could not read source file: " + filePath, e);
						}
					}
					
					document.addContent(element);
					outputter.output(document, writer);
					writer.close();
				} catch (final IOException e) {
					getLog().error("Write data.", e);
					
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
	
	private File openXMLFile() throws IOException {
		final File file = new File(this.outputDirectory, filename);
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				FileUtils.deleteDirectory(file);
			} else {
				throw new IOException("Cannot create file `" + filename + "` at `"
				        + this.outputDirectory.getAbsolutePath() + "`.");
			}
		}
		file.createNewFile();
		return file;
	}
	
	private void warnMissing(final Element missings,
	                         final String key,
	                         final String resource,
	                         final String filePath,
	                         final int lineNumber) {
		getLog().warn("Missing key '" + key + "' in NLS resource: " + resource + " (from " + filePath + ", line "
		                      + lineNumber + ")");
		final Element missing = new Element("missing", missings.getNamespace());
		missing.setAttribute("key", key);
		missing.setAttribute("resource", resource);
		missing.setAttribute("file", filePath);
		missing.setAttribute("lineNumber", lineNumber + "");
		missings.addContent(missing);
	}
	
	/**
	 * @param namings
	 * @param lineNumber
	 * @param filePath
	 * @param trim
	 * @param simpleClassName
	 */
	private void warnNaming(final Element namings,
	                        final int lineNumber,
	                        final String filePath,
	                        final String line,
	                        final String simpleClassName) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			getLog().warn("Potential wrong NLS access at line " + lineNumber + " in " + filePath + ": " + line
			                      + ". Expecting key class qualifier to be: " + simpleClassName);
			final Element naming = new Element("naming", namings.getNamespace());
			naming.setAttribute("file", filePath);
			naming.setAttribute("lineNumber", lineNumber + "");
			naming.setAttribute("code", line);
			naming.setAttribute("class", simpleClassName);
			namings.addContent(naming);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
}
