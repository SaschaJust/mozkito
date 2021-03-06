/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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

package org.mozkito.datadependency.eclipse;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.hiari.settings.ArgumentFactory;
import net.ownhero.dev.hiari.settings.InputFileArgument;
import net.ownhero.dev.hiari.settings.OutputFileArgument;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.apache.commons.lang.StringUtils;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.mozkito.codeanalysis.utils.PPAUtils;
import org.mozkito.utilities.io.FileUtils;

import ca.mcgill.cs.swevo.ppa.PPAOptions;

/**
 * This class controls all aspects of the application's execution.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class Application implements IApplication {
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	@Override
	public Object start(final IApplicationContext context) throws Exception {
		Settings settings = new Settings();
		
		
		
		InputFileArgument.Options inArg = new InputFileArgument.Options(settings.getRoot(), "in", "The file to be analyzed (full qualified path",
		                                                null, Requirement.required);
		OutputFileArgument.Options outArg = new OutputFileArgument.Options(settings.getRoot(), "out", "The file to write the output to", null,
		                                                   Requirement.required,true);
		
		InputFileArgument inputFileArgument = ArgumentFactory.create(inArg);
		OutputFileArgument outputFileArgument = ArgumentFactory.create(outArg);
		
		
		CompilationUnit cu = PPAUtils.getCU(inputFileArgument.getValue(), new PPAOptions());
		DataDependencyVisitor visitor = new DataDependencyVisitor(cu);
		cu.accept(visitor);
		Map<Integer, Set<Integer>> dependencies = visitor.getVariableAccessesPerLine();
		
		Writer writer = new BufferedWriter(new FileWriter(outputFileArgument.getValue()));
		
		for (Integer vId : dependencies.keySet()) {
			writer.append(StringUtils.join(dependencies.get(vId), ","));
			writer.append(FileUtils.lineSeparator);
		}
		
		dependencies = visitor.getVariableAccessesPerLine();
		
		for (Integer vId : dependencies.keySet()) {
			writer.append(StringUtils.join(dependencies.get(vId), ","));
			writer.append(FileUtils.lineSeparator);
		}
		
		writer.close();
		return IApplication.EXIT_OK;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	@Override
	public void stop() {
		// nothing to do
	}
}
