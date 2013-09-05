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
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import org.mozkito.utilities.datastructures.Tuple;
import org.mozkito.utilities.execution.CommandExecutor;

/**
 * Goal that collects meta information such as module version and version archive hash and injects it into the project
 * resources. The generated resource "metadata.properties" can be accessed at runtime to access this information.
 */
@Mojo (name = "meta", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class MozkitoMetaMojo extends AbstractMojo {
	
	/** The base dir. */
	@Parameter (defaultValue = "${basedir}", required = true, readonly = true)
	private File               baseDir;
	
	/**
	 * Location of the file.
	 */
	@Parameter (defaultValue = "${project.build.directory}", property = "outputDir", required = true, readonly = true)
	private File               outputDirectory;
	
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
	
	/** The project version. */
	@Parameter (defaultValue = "${project.version}", required = true, readonly = true)
	private String             projectVersion;
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	public void execute() throws MojoExecutionException {
		final File f = this.outputDirectory;
		
		if (!f.exists()) {
			f.mkdirs();
		}
		FileWriter w = null;
		final File directory = new File(f, "metadata");
		final File file = new File(directory, "metadata.properties");
		
		try {
			final Pattern pattern = Pattern.compile("^\\p{XDigit}+$");
			final String ls = System.getProperty("line.separator");
			
			final File gitDir = new File(this.baseDir, ".git");
			final boolean isClone = gitDir.exists() && gitDir.isDirectory();
			String head = null;
			boolean modified = false;
			
			if (isClone) {
				final Tuple<Integer, List<String>> execution = CommandExecutor.execute("git", new String[] { "log",
				        "HEAD^..HEAD", "--pretty=format:%H" }, this.baseDir, null, new HashMap<String, String>());
				final List<String> output = execution.getSecond();
				
				if (output.isEmpty()) {
					getLog().warn("Getting git head revision failed (output from git command was empty).");
					return;
				} else {
					final String firstLine = output.iterator().next();
					if ((execution.getFirst() != 0) || firstLine.startsWith("fatal:")) {
						getLog().warn("Getting git head revision failed: " + firstLine);
					} else {
						final Matcher matcher = pattern.matcher(firstLine);
						if (matcher.matches()) {
							head = output.iterator().next();
						} else {
							getLog().warn("Getting git head revision failed. Output is not a hash: " + firstLine);
						}
					}
				}
				
				if (head != null) {
					final Tuple<Integer, List<String>> diff = CommandExecutor.execute("git", new String[] { "diff" },
					                                                                  this.baseDir, null,
					                                                                  new HashMap<String, String>());
					modified = !diff.getFirst().equals(0) || !diff.getSecond().isEmpty();
				}
			}
			
			if (getLog().isInfoEnabled()) {
				getLog().info("Generating meta data file...");
			}
			if (!directory.exists()) {
				directory.mkdirs();
			}
			
			if (file.exists()) {
				file.delete();
			}
			
			w = new FileWriter(file);
			
			if (head != null) {
				w.write("head=");
				w.write(head);
				if (modified) {
					w.write(" (modified)");
				}
				w.write(ls);
			}
			
			w.write("version=");
			w.write(this.projectVersion);
			w.write(ls);
			w.flush();
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
	}
}
