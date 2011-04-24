/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.settings;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine;
import de.unisaarland.cs.st.reposuite.mapping.strategies.MappingFinder;
import de.unisaarland.cs.st.reposuite.mapping.strategies.MappingStrategy;
import de.unisaarland.cs.st.reposuite.settings.ListArgument;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgumentSet;
import de.unisaarland.cs.st.reposuite.utils.ClassFinder;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class MappingArguments extends RepoSuiteArgumentSet {
	
	private final Set<MappingEngine>   engines    = new HashSet<MappingEngine>();
	private final Set<MappingStrategy> strategies = new HashSet<MappingStrategy>();
	
	/**
	 * @param isRequired 
	 * @param mappingSettings 
	 * 
	 */
	public MappingArguments(final MappingSettings settings, final boolean isRequired) {
		super();
		
		try {
			Package package1 = MappingEngine.class.getPackage();
			Collection<Class<?>> classesExtendingClass = ClassFinder.getClassesExtendingClass(package1,
			                                                                                  MappingEngine.class);
			
			addArgument(new ListArgument(settings, "mapping.engines", "A list of mapping engines that shall be used.",
			                             buildEngineList(classesExtendingClass), false));
			
			String engines = System.getProperty("mapping.engines");
			Set<String> engineNames = new HashSet<String>();
			
			if (engines != null) {
				for (String engineName : engines.split(",")) {
					engineNames.add(MappingEngine.class.getPackage().getName() + "." + engineName);
				}
				
			}
			
			for (Class<?> klass : classesExtendingClass) {
				if (engineNames.isEmpty() || engineNames.contains(klass.getCanonicalName())) {
					if (Logger.logInfo()) {
						Logger.info("Adding new MappingEngine " + klass.getCanonicalName());
					}
					
					Constructor<?> constructor = klass.getConstructor(MappingSettings.class);
					MappingEngine engine = (MappingEngine) constructor.newInstance(settings);
					engine.register(settings, this, isRequired);
					this.engines.add(engine);
				} else {
					if (Logger.logInfo()) {
						Logger.info("Not loading available engine: " + klass.getSimpleName());
					}
				}
			}
			
			Package package2 = MappingStrategy.class.getPackage();
			Collection<Class<?>> interfaces = ClassFinder.getClassesExtendingClass(package2, MappingStrategy.class);
			addArgument(new ListArgument(settings, "mapping.strategies",
			                             "A list of mapping strategies that shall be used. Available: "
			                                     + buildStrategyList(classesExtendingClass), null, true));
			
			String strategies = System.getProperty("mapping.strategies");
			Set<String> strategyNames = new HashSet<String>();
			
			if (strategies != null) {
				for (String strategyName : strategies.split(",")) {
					strategyNames.add(MappingStrategy.class.getPackage().getName() + "." + strategyName);
				}
			}
			
			for (Class<?> klass : interfaces) {
				if (strategyNames.isEmpty() || strategyNames.contains(klass.getCanonicalName())) {
					if (Logger.logInfo()) {
						Logger.info("Adding new MappingStrategy " + klass.getCanonicalName());
					}
					
					Constructor<?> constructor = klass.getConstructor(MappingSettings.class);
					MappingStrategy strategy = (MappingStrategy) constructor.newInstance(settings);
					strategy.register(settings, this, isRequired);
					this.strategies.add(strategy);
				} else {
					if (Logger.logInfo()) {
						Logger.info("Not loading available strategy: " + klass.getSimpleName());
					}
				}
			}
			
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new RuntimeException();
		}
	}
	
	/**
	 * @param engines
	 * @return
	 */
	private String buildEngineList(final Collection<Class<?>> engines) {
		StringBuilder builder = new StringBuilder();
		for (Class<?> klass : engines) {
			if (builder.length() != 0) {
				builder.append(",");
			}
			builder.append(klass.getSimpleName());
		}
		return builder.toString();
	}
	
	/**
	 * @param strategies
	 * @return
	 */
	private String buildStrategyList(final Collection<Class<?>> strategies) {
		StringBuilder builder = new StringBuilder();
		for (Class<?> klass : strategies) {
			if (builder.length() != 0) {
				builder.append(",");
			}
			builder.append(klass.getSimpleName());
		}
		return builder.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgumentSet#getValue()
	 */
	@Override
	public MappingFinder getValue() {
		MappingFinder finder = new MappingFinder();
		
		for (MappingEngine engine : this.engines) {
			engine.init();
			finder.addEngine(engine);
		}
		
		for (MappingStrategy strategy : this.strategies) {
			strategy.init();
			finder.addStrategy(strategy);
		}
		
		return finder;
	}
	
}
