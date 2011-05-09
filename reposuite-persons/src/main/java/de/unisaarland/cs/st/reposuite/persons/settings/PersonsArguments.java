/**
 * 
 */
package de.unisaarland.cs.st.reposuite.persons.settings;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.ioda.ClassFinder;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.exceptions.WrongClassSearchMethodException;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.persons.engine.MergingEngine;
import de.unisaarland.cs.st.reposuite.persons.processing.MergingProcessor;
import de.unisaarland.cs.st.reposuite.settings.ListArgument;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgumentSet;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class PersonsArguments extends RepoSuiteArgumentSet {
	
	private final Set<MergingEngine> engines = new HashSet<MergingEngine>();
	
	/**
	 * 
	 */
	public PersonsArguments(final PersonsSettings settings, final boolean isRequired) {
		super();
		try {
			Package package1 = MergingEngine.class.getPackage();
			Collection<Class<? extends MergingEngine>> engineClasses = ClassFinder.getClassesExtendingClass(package1,
			                                                                                                MergingEngine.class,
			                                                                                                Modifier.ABSTRACT
			                                                                                                        | Modifier.INTERFACE
			                                                                                                        | Modifier.PRIVATE);
			
			addArgument(new ListArgument(settings, "persons.engines", "A list of merging engines that shall be used: "
			        + buildEngineList(engineClasses), "[all]", false));
			
			String engines = System.getProperty("persons.engines");
			Set<String> engineNames = new HashSet<String>();
			
			if (engines != null) {
				for (String engineName : engines.split(",")) {
					engineNames.add(MergingEngine.class.getPackage().getName() + "." + engineName);
				}
				
			}
			
			for (Class<? extends MergingEngine> klass : engineClasses) {
				if (engineNames.isEmpty() || engineNames.contains(klass.getCanonicalName())) {
					if ((klass.getModifiers() & Modifier.ABSTRACT) == 0) {
						if (Logger.logInfo()) {
							Logger.info("Adding new MergingEngine " + klass.getCanonicalName());
						}
						
						MergingEngine engine = klass.newInstance();
						engine.register(settings, this, isRequired);
						this.engines.add(engine);
					}
				} else {
					if (Logger.logInfo()) {
						Logger.info("Not loading available engine: " + klass.getSimpleName());
					}
				}
			}
		} catch (IllegalArgumentException e) {
			throw new UnrecoverableError(e);
		} catch (InstantiationException e) {
			throw new UnrecoverableError(e);
		} catch (IllegalAccessException e) {
			throw new UnrecoverableError(e);
		} catch (ClassNotFoundException e) {
			throw new UnrecoverableError(e);
		} catch (WrongClassSearchMethodException e) {
			throw new UnrecoverableError(e);
		} catch (IOException e) {
			throw new UnrecoverableError(e);
		}
	}
	
	/**
	 * @param engines
	 * @return
	 */
	private String buildEngineList(final Collection<Class<? extends MergingEngine>> engines) {
		StringBuilder builder = new StringBuilder();
		builder.append(FileUtils.lineSeparator);
		for (Class<? extends MergingEngine> klass : engines) {
			try {
				builder.append('\t').append("  ").append(klass.getSimpleName()).append(": ")
				       .append(klass.newInstance().getDescription());
			} catch (InstantiationException e) {
				if (Logger.logWarn()) {
					Logger.warn(e.getMessage(), e);
				}
			} catch (IllegalAccessException e) {
				if (Logger.logWarn()) {
					Logger.warn(e.getMessage(), e);
				}
			}
			
			if (builder.length() != 0) {
				builder.append(FileUtils.lineSeparator);
			}
		}
		return builder.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgumentSet#getValue()
	 */
	@Override
	public MergingProcessor getValue() {
		MergingProcessor finder = new MergingProcessor();
		
		for (MergingEngine engine : this.engines) {
			engine.init();
			finder.addEngine(engine);
		}
		
		return finder;
	}
	
}
