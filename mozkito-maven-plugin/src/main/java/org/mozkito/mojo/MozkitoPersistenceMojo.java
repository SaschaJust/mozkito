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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.util.DirectoryScanner;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderSAX2Factory;
import org.jdom2.output.XMLOutputter;

/**
 * Goal that generates a persistence.xml file specific for the module that is currently build. You have to specify an
 * inclusion pattern matching all compilation units that shall be added as persistent classes.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
@Mojo (name = "persistence", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class MozkitoPersistenceMojo extends AbstractMojo {
	
	/**
	 * Project instance, needed for attaching the buildinfo file. Used to add new source directory to the build.
	 */
	@Parameter (defaultValue = "${project}", readonly = true, required = true)
	private MavenProject        project;
	
	/**
	 * Helper class to assist in attaching artifacts to the project instance. project-helper instance, used to make
	 * addition of resources simpler.
	 */
	@Component
	private MavenProjectHelper  projectHelper;
	
	/** The connection url. */
	@Parameter (required = true)
	private String              connectionURL;
	
	/** The artifact id. */
	@Parameter (defaultValue = "${project.artifactId}", readonly = true, required = true)
	private String              artifactId;
	
	/**
	 * The inclusion pattern. This should be something like:
	 * <p>
	 * **\/model\/*
	 * </p>
	 */
	@Parameter (required = true)
	private List<String>        includes;
	
	/** The excludes. */
	@Parameter
	private List<String>        excludes;
	
	/** The source directory. */
	@Parameter (defaultValue = "${project.build.sourceDirectory}", required = true, readonly = true)
	private File                sourceDirectory;
	
	/**
	 * Location of the file.
	 */
	@Parameter (defaultValue = "${project.build.directory}", property = "outputDir", required = true, readonly = true)
	private File                outputDirectory;
	
	/** The OpenJPA options. */
	@Parameter (required = true)
	private Map<String, String> openJPAOptions;
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		// PRECONDITIONS
		
		try {
			
			final String moduleName = this.artifactId.replace("mozkito-", "");
			final InputStream stream = MozkitoPersistenceMojo.class.getResourceAsStream("/persistence-skeleton.xml");
			InputStreamReader reader = null;
			Document document = null;
			
			try {
				reader = new InputStreamReader(stream);
				
				final SAXBuilder saxBuilder = new SAXBuilder(
				                                             new XMLReaderSAX2Factory(false,
				                                                                      "org.apache.xerces.parsers.SAXParser"));
				saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
				saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
				document = saxBuilder.build(reader);
				reader.close();
			} catch (IOException | JDOMException e) {
				throw new MojoExecutionException("Could not read persistence skeleton.", e);
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (final IOException ignore) {
						// ignore
					}
				}
			}
			
			final Element persistenceElement = document.getRootElement();
			final Element unitElement = persistenceElement.getChild("persistence-unit",
			                                                        persistenceElement.getNamespace());
			
			// set name of the persistence unit
			unitElement.setAttribute("name", moduleName);
			
			// gather model units from dependencies
			@SuppressWarnings ("unchecked")
			final List<Dependency> dependencies = this.project.getDependencies();
			final File parentBaseDir = this.project.getParent().getBasedir();
			for (final Dependency dependency : dependencies) {
				if (dependency.getArtifactId().startsWith("mozkito-")) {
					final File moduleDir = new File(parentBaseDir, dependency.getArtifactId());
					if (!moduleDir.exists()) {
						throw new MojoExecutionException("Cannot build persistence.xml if not all modules are present.");
					}
					final File targetDir = new File(moduleDir, "target");
					final File persistenceDir = new File(targetDir, "persistence");
					final File persistenceFile = new File(persistenceDir, "persistence.xml");
					
					Document depDocument = null;
					if (persistenceFile.exists()) {
						try {
							reader = new FileReader(persistenceFile);
							
							final SAXBuilder saxBuilder = new SAXBuilder(
							                                             new XMLReaderSAX2Factory(false,
							                                                                      "org.apache.xerces.parsers.SAXParser"));
							saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar",
							                      false);
							saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",
							                      false);
							depDocument = saxBuilder.build(reader);
							reader.close();
						} catch (IOException | JDOMException e) {
							throw new MojoExecutionException("Could not read persistence skeleton.", e);
						} finally {
							if (reader != null) {
								try {
									reader.close();
								} catch (final IOException ignore) {
									// ignore
								}
							}
						}
						
					} else {
						getLog().info("No model units in " + dependency.getArtifactId());
					}
					
					if (depDocument != null) {
						
						final Element root = depDocument.getRootElement();
						final Element e1 = root.getChild("persistence-unit", root.getNamespace());
						final List<Element> children = e1.getChildren("class", root.getNamespace());
						for (final Element child : children) {
							getLog().info("Adding dependency unit: " + child.getValue());
							unitElement.addContent(child.clone());
						}
					}
				}
			}
			
			final DirectoryScanner scanner = new DirectoryScanner();
			scanner.setIncludes(this.includes.toArray(new String[0]));
			if (this.excludes != null) {
				scanner.setExcludes(this.excludes.toArray(new String[0]));
			}
			
			scanner.setBasedir(this.sourceDirectory);
			scanner.setCaseSensitive(false);
			scanner.scan();
			final String[] files = scanner.getIncludedFiles();
			getLog().info("Found " + files.length + " local compilation units to include.");
			
			for (final String filePath : files) {
				final String canonicalClassName = FilenameUtils.removeExtension(filePath)
				                                               .replace(System.getProperty("file.separator"), ".");
				getLog().info("adding compilation unit: " + canonicalClassName);
				final Element classElement = new Element("class", persistenceElement.getNamespace());
				classElement.addContent(canonicalClassName);
				unitElement.addContent(classElement);
			}
			
			final Element propertiesElement = unitElement.getChild("properties", persistenceElement.getNamespace());
			
			Element property = new Element("property", persistenceElement.getNamespace());
			property.setAttribute("name", "openjpa.ConnectionURL");
			property.setAttribute("value", this.connectionURL);
			propertiesElement.addContent(property);
			
			if (this.openJPAOptions != null) {
				for (final Entry<String, String> entry : this.openJPAOptions.entrySet()) {
					
					property = new Element("property", persistenceElement.getNamespace());
					property.setAttribute("name", entry.getKey());
					property.setAttribute("value", entry.getValue());
					propertiesElement.addContent(property);
				}
			}
			
			// write file
			
			final File f = this.outputDirectory;
			
			if (!f.exists()) {
				f.mkdirs();
			}
			
			final File directory = new File(f, "persistence");
			final File file = new File(directory, "persistence.xml");
			
			FileWriter w = null;
			
			try {
				if (getLog().isInfoEnabled()) {
					getLog().info("Generating persistence data file...");
				}
				if (!directory.exists()) {
					directory.mkdirs();
				}
				
				if (file.exists()) {
					file.delete();
				}
				w = new FileWriter(file);
				
				final XMLOutputter outputter = new XMLOutputter();
				outputter.output(document, w);
				w.close();
				
				final List<String> includes = Collections.singletonList("*");
				final List<String> excludes = null;
				
				if (this.projectHelper == null) {
					throw new MojoExecutionException("ProjectHelper is null!");
				}
				if (getLog().isInfoEnabled()) {
					getLog().info("Adding resource to project.");
				}
				
				this.projectHelper.addResource(this.project, directory.getAbsolutePath(), includes, excludes);
			} catch (final IOException e) {
				throw new MojoExecutionException("Error creating file " + file, e);
			} finally {
				if (w != null) {
					try {
						w.close();
					} catch (final IOException e) {
						// ignore
					}
				}
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
}
