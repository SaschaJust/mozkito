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
import java.util.List;
import java.util.Locale;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderSAX2Factory;

/**
 * Goal that checks if calls to NLS are valid, i.e. the key class is the same as the simple classname and the key exists
 * in every localized messages.properties file.
 */
@Mojo (name = "nls-report", defaultPhase = LifecyclePhase.COMPILE)
public class MozkitoNLSReportMojo extends AbstractMavenReport {
	
	/** The base dir. */
	@Parameter (defaultValue = "${basedir}", required = true, readonly = true)
	private File               baseDir;
	
	/**
	 * The inclusion pattern. This should be something like:
	 * <p>
	 * &#42;&#42;/&#42;.java
	 * </p>
	 */
	@Parameter (required = true, defaultValue = "**/*.java")
	private List<String>       includes;
	
	/** The base directory. */
	@Parameter (defaultValue = "${basedir}", readonly = true, required = true)
	private File               baseDirectory;
	
	/**
	 * The exclusion pattern. This can be empty or should be something like:
	 * <p>
	 * &#42;&#42;/Messages_Is_No_NLS_Class/&#42;_.java
	 * </p>
	 */
	@Parameter
	private List<String>       excludes;
	
	/** The source directory. */
	@Parameter (defaultValue = "${project.build.sourceDirectory}", required = true, readonly = true)
	private File               sourceDirectory;
	
	/**
	 * Project instance, needed for attaching the buildinfo file. Used to add new source directory to the build.
	 */
	@Parameter (defaultValue = "${project}", readonly = true, required = true)
	private MavenProject       project;
	
	/**
	 * Helper class to assist in attaching artifacts to the project instance. project-helper instance, used to make
	 * addition of resources simpler.
	 */
	@Component
	private MavenProjectHelper projectHelper;
	
	/** The site renderer. */
	@Component
	Renderer                   siteRenderer;
	
	/** The output directory. */
	@Parameter (defaultValue = "${project.reporting.outputDirectory}", readonly = true, required = true)
	private String             outputDirectory;
	
	@Parameter (defaultValue = "${project.build.directory}", required = true, readonly = true)
	private File               buildOutputDirectory;
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.apache.maven.reporting.AbstractMavenReport#executeReport(java.util.Locale)
	 */
	@Override
	protected void executeReport(final Locale locale) throws MavenReportException {
		PRECONDITIONS: {
			// none
		}
		
		try {
			
			final File file = new File(this.buildOutputDirectory, MozkitoNLSMojo.filename);
			if (file.exists()) {
				try (FileReader reader = new FileReader(file)) {
					final SAXBuilder saxBuilder = new SAXBuilder(
					                                             new XMLReaderSAX2Factory(false,
					                                                                      "org.apache.xerces.parsers.SAXParser"));
					saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
					saxBuilder.setFeature("http://apache." + "org/xml/features/nonvalidating/load-external-dtd", false);
					
					final Document document = saxBuilder.build(reader);
					final Element root = document.getRootElement();
					final Element missings = root.getChild("missings", root.getNamespace());
					final Element namings = root.getChild("namings", root.getNamespace());
					
					getSiteRenderer();
					final Sink sink = getSink();
					sink.head();
					sink.title();
					sink.text("Analysis of NLS usage.");
					sink.title_();
					sink.head_();
					
					sink.body();
					sink.section1();
					
					sink.sectionTitle1();
					sink.text("Missing NLS entries:");
					sink.sectionTitle1_();
					sink.lineBreak();
					sink.lineBreak();
					
					sink.text("List of all calls to NLS strings that cannot be found in one or more resource files.");
					
					if (missings.getChildren().isEmpty()) {
						sink.horizontalRule();
						sink.text("No violations found.");
					} else {
						sink.table();
						sink.tableRow();
						sink.tableHeaderCell();
						sink.text("key");
						sink.tableHeaderCell_();
						sink.tableHeaderCell();
						sink.text("resource");
						sink.tableHeaderCell_();
						sink.tableHeaderCell();
						sink.text("file");
						sink.tableHeaderCell_();
						sink.tableHeaderCell();
						sink.text("lineNumber");
						sink.tableHeaderCell_();
						sink.tableRow_();
						
						for (final Element missing : missings.getChildren()) {
							sink.tableRow();
							sink.tableCell();
							sink.text(missing.getAttributeValue("key"));
							sink.tableCell_();
							
							sink.tableCell();
							sink.text(missing.getAttributeValue("resource"));
							sink.tableCell_();
							
							sink.tableCell();
							sink.text(missing.getAttributeValue("file"));
							sink.tableCell_();
							
							sink.tableCell();
							sink.text(missing.getAttributeValue("lineNumber"));
							sink.tableCell_();
							sink.tableRow_();
						}
						sink.table_();
					}
					
					sink.lineBreak();
					// makeLinks(sink);
					sink.section1_();
					
					sink.section2();
					
					sink.sectionTitle2();
					sink.text("Potential wrong NLS keys:");
					sink.sectionTitle2_();
					sink.lineBreak();
					sink.lineBreak();
					
					sink.text("A list of NLS keys that do not match the naming convention and might be unintendend.");
					
					if (namings.getChildren().isEmpty()) {
						sink.horizontalRule();
						sink.text("No potential errors found.");
					} else {
						sink.table();
						sink.tableRow();
						sink.tableHeaderCell();
						sink.text("expected prefix");
						sink.tableHeaderCell_();
						sink.tableHeaderCell();
						sink.text("file");
						sink.tableHeaderCell_();
						sink.tableHeaderCell();
						sink.text("line");
						sink.tableHeaderCell_();
						sink.tableHeaderCell();
						sink.text("source");
						sink.tableHeaderCell_();
						sink.tableRow_();
						
						for (final Element naming : namings.getChildren()) {
							sink.tableRow();
							sink.tableCell();
							sink.text(naming.getAttributeValue("class"));
							sink.tableCell_();
							
							sink.tableCell();
							sink.text(naming.getAttributeValue("file"));
							sink.tableCell_();
							
							sink.tableCell();
							sink.text(naming.getAttributeValue("lineNumber"));
							sink.tableCell_();
							
							sink.tableCell();
							sink.verbatim(false);
							sink.text(naming.getAttributeValue("code"));
							sink.verbatim_();
							sink.tableCell_();
							sink.tableRow_();
						}
						sink.table_();
					}
					
					sink.lineBreak();
					// makeLinks(sink);
					sink.section2_();
					
					sink.body_();
					sink.flush();
					
					sink.close();
				} catch (IOException | JDOMException e) {
					// error
				} finally {
					// nothing to do here
				}
			}
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.apache.maven.reporting.MavenReport#getDescription(java.util.Locale)
	 */
	@Override
	public String getDescription(final Locale locale) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return "DESCRIPTION";
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.apache.maven.reporting.MavenReport#getName(java.util.Locale)
	 */
	@Override
	public String getName(final Locale locale) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return "NLS Report";
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.apache.maven.reporting.AbstractMavenReport#getOutputDirectory()
	 */
	@Override
	protected String getOutputDirectory() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.outputDirectory;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.apache.maven.reporting.MavenReport#getOutputName()
	 */
	@Override
	public String getOutputName() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return "Output Name";
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.apache.maven.reporting.AbstractMavenReport#getProject()
	 */
	@Override
	protected MavenProject getProject() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.project;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.apache.maven.reporting.AbstractMavenReport#getSiteRenderer()
	 */
	@Override
	protected Renderer getSiteRenderer() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.siteRenderer;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
}
