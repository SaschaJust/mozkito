package de.unisaarland.cs.st.reposuite.datadependency.eclipse;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.ioda.FileUtils;

import org.apache.commons.lang.StringUtils;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jdt.core.dom.CompilationUnit;

import ca.mcgill.cs.swevo.ppa.PPAOptions;
import de.unisaarland.cs.st.reposuite.ppa.utils.PPAUtils;
import de.unisaarland.cs.st.reposuite.ppa.visitors.DataDependencyVisitor;
import de.unisaarland.cs.st.reposuite.settings.InputFileArgument;
import de.unisaarland.cs.st.reposuite.settings.OutputFileArgument;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;

/**
 * This class controls all aspects of the application's execution.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class Application implements IApplication {
	
	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	@Override
	public Object start(final IApplicationContext context) throws Exception {
		RepoSuiteSettings settings = new RepoSuiteSettings();
		
		InputFileArgument inArg = new InputFileArgument(settings, "in", "The file to be analyzed (full qualified path",
		                                                null, true);
		OutputFileArgument outArg = new OutputFileArgument(settings, "out", "The file to write the output to", null,
		                                                   true, true);
		
		settings.parseArguments();
		
		CompilationUnit cu = PPAUtils.getCU(inArg.getValue(), new PPAOptions());
		DataDependencyVisitor visitor = new DataDependencyVisitor(cu);
		cu.accept(visitor);
		Map<Integer, Set<Integer>> dependencies = visitor.getVariableAccessesPerLine();
		
		Writer writer = new BufferedWriter(new FileWriter(outArg.getValue()));
		
		for (Integer vId : dependencies.keySet()) {
			writer.append(StringUtils.join(dependencies.get(vId), ","));
			writer.append(FileUtils.lineSeparator);
		}
		
		dependencies = visitor.getFieldAccessesPerLine();
		
		for (Integer vId : dependencies.keySet()) {
			writer.append(StringUtils.join(dependencies.get(vId), ","));
			writer.append(FileUtils.lineSeparator);
		}
		
		writer.close();
		return IApplication.EXIT_OK;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	@Override
	public void stop() {
		// nothing to do
	}
}
