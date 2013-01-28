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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.util.AbstractScanner;
import org.codehaus.plexus.util.DirectoryScanner;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderSAX2Factory;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * Goal that generates a persistence.xml file specific for the module that is currently build. You have to specify an
 * inclusion pattern matching all compilation units that shall be added as persistent classes.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
@Mojo (name = "persistence",
       requiresProject = true,
       threadSafe = false,
       defaultPhase = LifecyclePhase.GENERATE_RESOURCES,
       requiresDependencyResolution = ResolutionScope.COMPILE)
public class MozkitoPersistenceMojo extends AbstractMojo {
	
	/**
	 * Project instance, needed for attaching the build info file. Used to add new source directory to the build.
	 */
	@Parameter (defaultValue = "${project}", readonly = true, required = true)
	private MavenProject        project;
	
	/**
	 * Helper class to assist in attaching artifacts to the project instance. project-helper instance, used to make
	 * addition of resources simpler.
	 */
	@Component
	private MavenProjectHelper  projectHelper;
	
	/** The connection URL used in the persistence.xml as default setting. */
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
	
	/** The base directory. */
	@Parameter (defaultValue = "${basedir}", readonly = true, required = true)
	private File                baseDirectory;
	
	/**
	 * The exclusion pattern. This should be something like:
	 * <p>
	 * **\/model\/*_.java
	 * </p>
	 */
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
	
	/**
	 * The OpenJPA options. Every key is prefixed with 'openjpa.' and then added as property to the persistence.xml
	 * file. This is the place where you can add/overwrite things like 'RuntimeUnenhancedClasses -> unsupported'.
	 */
	@Parameter (required = true)
	private Map<String, String> openJPAOptions;
	
	/** The Constant projectPreTag. */
	private static final String PROJECT_PREFIX = "mozkito-";
	
	private static final String SKELETON       = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
	                                                   + "<!--\n"
	                                                   + " Copyright 2011 Kim Herzig, Sascha Just\n"
	                                                   + " \n"
	                                                   + " Licensed under the Apache License, Version 2.0 (the \"License\"); you may not use this file except in compliance with\n"
	                                                   + " the License. You may obtain a copy of the License at\n"
	                                                   + " \n"
	                                                   + " http://www.apache.org/licenses/LICENSE-2.0\n"
	                                                   + " \n"
	                                                   + " Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on\n"
	                                                   + " an \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the\n"
	                                                   + " specific language governing permissions and limitations under the License.\n"
	                                                   + "-->\n"
	                                                   + "<persistence xmlns=\"http://java.sun.com/xml/ns/persistence\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"1.0\">\n"
	                                                   + "  <persistence-unit name=\"persistence\" transaction-type=\"RESOURCE_LOCAL\">\n"
	                                                   + "    <provider>org.apache.openjpa.persistence.PersistenceProviderImpl</provider>\n"
	                                                   + "    <class>org.mozkito.persistence.model.DateTimeTuple</class>\n"
	                                                   + "    <class>org.mozkito.persistence.model.EnumTuple</class>\n"
	                                                   + "    <class>org.mozkito.persistence.model.Person</class>\n"
	                                                   + "    <class>org.mozkito.persistence.model.PersonContainer</class>\n"
	                                                   + "    <class>org.mozkito.persistence.model.PersonTuple</class>\n"
	                                                   + "    <class>org.mozkito.persistence.model.StringTuple</class>\n"
	                                                   + "    <properties>\n"
	                                                   + "      <property name=\"openjpa.Log\" value=\"slf4j\" />\n"
	                                                   + "      <property name=\"openjpa.Multithreaded\" value=\"true\" />\n"
	                                                   + "      <property name=\"openjpa.jdbc.SynchronizeMappings\" value=\"validate\" />\n"
	                                                   + "      <property name=\"openjpa.ConnectionDriverName\" value=\"org.postgresql.Driver\" />\n"
	                                                   + "      <property name=\"openjpa.ConnectionPassword\" value=\"miner\" />\n"
	                                                   + "      <property name=\"openjpa.ConnectionUserName\" value=\"miner\" />\n"
	                                                   + "      <property name=\"openjpa.RuntimeUnenhancedClasses\" value=\"supported\" />\n"
	                                                   + "      <property name=\"openjpa.ConnectionRetainMode\" value=\"transaction\" />\n"
	                                                   + "      <!--<property name=\"openjpa.Log\" value=\"SQL=TRACE\"/>-->\n"
	                                                   + "      <property name=\"openjpa.ConnectionFactoryProperties\" value=\"PrettyPrint=true, PrettyPrintLineLength=72, PrintParameters=True\" />\n"
	                                                   + "      <property name=\"openjpa.ConnectionURL\" value=\"jdbc:postgresql://quentin.cs.uni-saarland.de/mozkito\" />\n"
	                                                   + "    </properties>\n" + "  </persistence-unit>\n"
	                                                   + "</persistence>\n";
	
	/**
	 * Adds the resources directory.
	 * 
	 * @param directory
	 *            the directory
	 */
	private void addResourcesDirectory(final File directory) {
		if (getLog().isInfoEnabled()) {
			getLog().info("Adding resource to project.");
		}
		
		final List<String> includes = Collections.singletonList("*");
		final List<String> excludes = null;
		
		this.projectHelper.addResource(this.project, directory.getAbsolutePath(), includes, excludes);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		// PRECONDITIONS
		
		try {
			if (this.projectHelper == null) {
				throw new MojoExecutionException("ProjectHelper is null!");
			}
			
			if (this.project.getParent() == null) {
				if (getLog().isInfoEnabled()) {
					getLog().info("Omitting generation of persistence.xml for master module: "
					                      + this.project.getArtifactId());
				}
				return;
			}
			
			boolean foundOpenJPADependency = false;
			@SuppressWarnings ("unchecked")
			final Set<Artifact> artifacts = this.project.getArtifacts();
			OPENJPA_DEPENDENCY: for (final Artifact artifact : artifacts) {
				if (artifact.getArtifactId().startsWith("openjpa")) {
					foundOpenJPADependency = true;
					break OPENJPA_DEPENDENCY;
				}
			}
			
			if (!foundOpenJPADependency) {
				getLog().info("Omitting generation of persistence.xml since no dependencies to OpenJPA have been found.");
				return;
			}
			
			final String moduleName = this.artifactId.replace(PROJECT_PREFIX, "");
			
			if (getLog().isInfoEnabled()) {
				getLog().info("Gathering data to generate persistence.xml for module: " + moduleName);
			}
			
			if (getLog().isDebugEnabled()) {
				getLog().debug("Reading skeleton file.");
			}
			final Document skeleton = readSkeleton();
			
			if (getLog().isDebugEnabled()) {
				getLog().debug("Determining resource directory.");
			}
			final File directory = getResourceDirectory();
			
			if (getLog().isDebugEnabled()) {
				getLog().debug("Looking up anchor element.");
			}
			// get anchor to add module children
			final Element anchor = getAnchor(skeleton, moduleName);
			
			final Element propertiesElement = anchor.getChild("properties", anchor.getNamespace()).clone();
			anchor.removeChild("properties", anchor.getNamespace());
			
			// gather inherited models
			if (getLog().isDebugEnabled()) {
				getLog().debug("Gathering inherited models.");
			}
			
			gatherInheritedModels(anchor);
			
			// gather local models
			if (getLog().isDebugEnabled()) {
				getLog().debug("Gathering local models.");
			}
			
			gatherLocalModels(anchor);
			
			// inject settings from configuration
			if (getLog().isDebugEnabled()) {
				getLog().debug("Applying settings.");
			}
			
			injectSettings(anchor, propertiesElement);
			
			// write file
			
			final File f = this.outputDirectory;
			
			if (!f.exists()) {
				f.mkdirs();
			}
			
			final File file = new File(directory, "persistence.xml");
			
			FileWriter w = null;
			
			try {
				if (getLog().isInfoEnabled()) {
					getLog().info("Generating persistence data file...");
				}
				
				boolean doAddResourceDir = false;
				if (!directory.exists()) {
					doAddResourceDir = true;
					directory.mkdirs();
				}
				
				if (file.exists()) {
					file.delete();
				}
				w = new FileWriter(file);
				
				final XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
				outputter.output(skeleton, w);
				w.close();
				
				if (getLog().isDebugEnabled()) {
					getLog().debug("Adding persistence.xml to module resources.");
				}
				
				if (doAddResourceDir) {
					addResourcesDirectory(directory);
				}
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
	
	/**
	 * Gather inherited models.
	 * 
	 * @param anchor
	 *            the anchor
	 * @throws MojoExecutionException
	 *             the mojo execution exception
	 */
	private void gatherInheritedModels(final Element anchor) throws MojoExecutionException {
		// gather model units from dependencies
		@SuppressWarnings ("unchecked")
		final Set<Artifact> artifacts = this.project.getArtifacts();
		for (final Artifact artifact : artifacts) {
			getLog().debug("Checking dependency artifact: " + artifact.getArtifactId());
			if (artifact.getArtifactId().startsWith(PROJECT_PREFIX)) {
				getLog().debug("Processing dependency artifact: " + artifact.getArtifactId());
				
				final File file = artifact.getFile();
				
				if (file.isFile() && file.getAbsolutePath().toLowerCase().endsWith(".jar")) {
					assert file.getAbsolutePath().endsWith(".jar");
					JarFile jFile = null;
					try {
						jFile = new JarFile(file);
						final Enumeration<JarEntry> entries = jFile.entries();
						while (entries.hasMoreElements()) {
							final JarEntry entry = entries.nextElement();
							boolean include = false;
							INCLUDE: for (final String includePattern : this.includes) {
								if (AbstractScanner.match(includePattern, entry.getName())
								        && entry.getName().endsWith(".class")) {
									if (this.excludes != null) {
										for (final String excludePattern : this.excludes) {
											getLog().info("Probing exclusion pattern " + excludePattern
											                      + " against candidate " + entry.getName());
											if (AbstractScanner.match(excludePattern, entry.getName())) {
												break INCLUDE;
											}
										}
										include = true;
										break INCLUDE;
									}
								}
							}
							
							if (include) {
								final String path = FilenameUtils.removeExtension(entry.getName());
								
								final String fqn = path.replace(File.separator, ".");
								
								final Element element = new Element("class", anchor.getNamespace());
								element.setText(fqn);
								getLog().info("Adding dependency unit: " + element.getValue());
								anchor.addContent(element);
							}
						}
						
						jFile.close();
						jFile = null;
					} catch (final IOException e) {
						throw new MojoExecutionException("Could not open dependency: " + file.getAbsolutePath(), e);
					} finally {
						if (jFile != null) {
							try {
								jFile.close();
							} catch (final IOException ignore) {
								// ignore
							}
						}
					}
				} else if (file.isDirectory()) {
					// process directory
					final DirectoryScanner scanner = new DirectoryScanner();
					scanner.setIncludes(this.includes.toArray(new String[0]));
					if (this.excludes != null) {
						scanner.setExcludes(this.excludes.toArray(new String[0]));
					}
					
					scanner.setBasedir(file);
					scanner.setCaseSensitive(false);
					scanner.scan();
					final String[] files = scanner.getIncludedFiles();
					for (final String entry : files) {
						final String path = FilenameUtils.removeExtension(entry);
						
						final String fqn = path.replace(File.separator, ".");
						
						final Element element = new Element("class", anchor.getNamespace());
						element.setText(fqn);
						getLog().info("Adding dependency unit: " + element.getValue());
						anchor.addContent(element);
					}
				} else {
					getLog().warn("Skipping unsupported dependency resource: " + file.getAbsolutePath());
				}
			}
		}
		
	}
	
	/**
	 * Gather local models.
	 * 
	 * @param anchor
	 *            the anchor
	 */
	private void gatherLocalModels(final Element anchor) {
		// PRECONDITIONS
		
		try {
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
				final Element classElement = new Element("class", anchor.getNamespace());
				classElement.addContent(canonicalClassName);
				anchor.addContent(classElement);
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the anchor.
	 * 
	 * @param skeleton
	 *            the skeleton
	 * @param moduleName
	 *            the module name
	 * @return the anchor
	 * @throws MojoExecutionException
	 *             the mojo execution exception
	 */
	private Element getAnchor(final Document skeleton,
	                          final String moduleName) throws MojoExecutionException {
		// PRECONDITIONS
		
		try {
			final Element persistenceElement = skeleton.getRootElement();
			final Element unitElement = persistenceElement.getChild("persistence-unit",
			                                                        persistenceElement.getNamespace());
			
			if (unitElement == null) {
				throw new MojoExecutionException("Invalid structure in skeleton file.");
			}
			
			// set name of the persistence unit
			unitElement.setAttribute("name", moduleName);
			
			return unitElement;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	private Element getPropertyElement(final Element propertiesElement,
	                                   final String name) {
		final Namespace ns = propertiesElement.getNamespace();
		final List<Element> children = propertiesElement.getChildren("property", ns);
		Element property = null;
		EXISTING_CHILDREN: for (final Element existing : children) {
			final Attribute attribute = existing.getAttribute("name");
			if ((attribute != null) && name.equals(attribute.getValue())) {
				property = existing;
				break EXISTING_CHILDREN;
			}
		}
		
		if (property == null) {
			property = new Element("property", ns);
			propertiesElement.addContent(property);
		}
		
		return property;
	}
	
	/**
	 * Gets the resource directory.
	 * 
	 * @return the resource directory
	 */
	private File getResourceDirectory() {
		@SuppressWarnings ("unchecked")
		final List<Resource> resources = this.project.getResources();
		
		if (resources.isEmpty()) {
			final File srcDir = new File(this.baseDirectory, "src");
			if (!srcDir.exists()) {
				srcDir.mkdir();
			}
			
			final File mainDir = new File(srcDir, "main");
			if (!mainDir.exists()) {
				mainDir.mkdir();
			}
			
			final File resourcesDir = new File(mainDir, "resources");
			if (!resourcesDir.exists()) {
				resourcesDir.mkdir();
			}
			return resourcesDir;
		} else {
			final Resource resource = resources.iterator().next();
			final String directoryString = resource.getDirectory();
			final File directory = new File(directoryString);
			assert directory.exists();
			assert directory.isDirectory();
			return directory;
		}
	}
	
	/**
	 * Inject settings.
	 * 
	 * @param anchor
	 *            the anchor
	 */
	private void injectSettings(final Element anchor,
	                            final Element propertiesElement) {
		// PRECONDITIONS
		
		try {
			
			Element property = getPropertyElement(propertiesElement, "openjpa.ConnectionURL");
			
			property.setAttribute("name", "openjpa.ConnectionURL");
			property.setAttribute("value", this.connectionURL);
			
			if (this.openJPAOptions != null) {
				for (final Entry<String, String> entry : this.openJPAOptions.entrySet()) {
					
					property = getPropertyElement(propertiesElement, "openjpa." + entry.getKey());
					property.setAttribute("value", entry.getValue());
					propertiesElement.addContent(property);
				}
			}
			
			anchor.addContent(propertiesElement);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Read skeleton.
	 * 
	 * @return the document
	 * @throws MojoExecutionException
	 *             the mojo execution exception
	 */
	private Document readSkeleton() throws MojoExecutionException {
		// getLog().warn(System.getProperty("java.class.path"));
		// getLog().warn("Looking up resource results in: "
		// + Thread.currentThread().getContextClassLoader().getResource("/persistence-skeleton.xml"));
		final InputStream stream = new ByteArrayInputStream(SKELETON.getBytes());
		InputStreamReader reader = null;
		Document document = null;
		
		try {
			reader = new InputStreamReader(stream);
			
			final SAXBuilder saxBuilder = new SAXBuilder(
			                                             new XMLReaderSAX2Factory(false,
			                                                                      "org.apache.xerces.parsers.SAXParser"));
			saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			saxBuilder.setFeature("http://apache." + "org/xml/features/nonvalidating/load-external-dtd", false);
			
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
		
		return document;
	}
	
}
