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

import net.ownhero.dev.ioda.CommandExecutor;
import net.ownhero.dev.ioda.Tuple;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

/**
 * Goal which touches a timestamp file.
 */
@Mojo (name = "meta", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class MozkitoMetaMojo extends AbstractMojo {
	
	/** The project version. */
	@Parameter (defaultValue = "${project.version}", required = true, readonly = true)
	private String             projectVersion;
	
	/**
	 * Helper class to assist in attaching artifacts to the project instance. project-helper instance, used to make
	 * addition of resources simpler.
	 * 
	 * @component
	 * @required
	 * @readonly
	 */
	@Component
	private MavenProjectHelper projectHelper;
	
	/**
	 * Project instance, needed for attaching the buildinfo file. Used to add new source directory to the build.
	 */
	@Parameter (defaultValue = "${project}", readonly = true, required = true)
	private MavenProject       project;
	
	/** The base dir. */
	@Parameter (defaultValue = "${basedir}", required = true, readonly = true)
	private File               baseDir;
	
	/**
	 * Location of the file.
	 */
	@Parameter (defaultValue = "${project.build.directory}", property = "outputDir", required = true, readonly = true)
	private File               outputDirectory;
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	public void execute() throws MojoExecutionException {
		final Pattern pattern = Pattern.compile("^\\p{XDigit}+$");
		final String ls = System.getProperty("line.separator");
		final Tuple<Integer, List<String>> execution = CommandExecutor.execute("git", new String[] { "log",
		        "HEAD^..HEAD", "--pretty=format:%H" }, this.baseDir, null, new HashMap<String, String>());
		final List<String> output = execution.getSecond();
		String head = null;
		if (output.isEmpty()) {
			getLog().error("Getting git head revision failed.");
			return;
		} else {
			final String firstLine = output.iterator().next();
			if (firstLine.startsWith("fatal:")) {
				getLog().error("Getting git head revision failed: " + firstLine);
			} else {
				final Matcher matcher = pattern.matcher(firstLine);
				if (matcher.matches()) {
					head = output.iterator().next();
				} else {
					getLog().error("Getting git head revision failed. Output is not a hash: " + firstLine);
				}
			}
		}
		
		final File f = this.outputDirectory;
		
		if (!f.exists()) {
			f.mkdirs();
		}
		
		final File directory = new File(f, "metadata");
		final File file = new File(directory, "metadata.properties");
		
		FileWriter w = null;
		try {
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
