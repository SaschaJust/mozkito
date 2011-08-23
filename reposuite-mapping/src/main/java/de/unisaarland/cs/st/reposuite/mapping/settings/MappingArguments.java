package de.unisaarland.cs.st.reposuite.mapping.settings;

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
import de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine;
import de.unisaarland.cs.st.reposuite.mapping.filters.MappingFilter;
import de.unisaarland.cs.st.reposuite.mapping.finder.MappingFinder;
import de.unisaarland.cs.st.reposuite.mapping.strategies.MappingStrategy;
import de.unisaarland.cs.st.reposuite.settings.ListArgument;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgumentSet;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class MappingArguments extends RepoSuiteArgumentSet {
	
	private final Set<MappingEngine>   engines    = new HashSet<MappingEngine>();
	private final Set<MappingStrategy> strategies = new HashSet<MappingStrategy>();
	private final Set<MappingFilter>   filters    = new HashSet<MappingFilter>();
	
	/**
	 * @param isRequired
	 * @param mappingSettings
	 * 
	 */
	public MappingArguments(final MappingSettings settings, final boolean isRequired) {
		super();
		try {
			Package package1 = MappingEngine.class.getPackage();
			Collection<Class<? extends MappingEngine>> engineClasses = ClassFinder.getClassesExtendingClass(package1,
			        MappingEngine.class, Modifier.ABSTRACT | Modifier.INTERFACE | Modifier.PRIVATE);
			
			addArgument(new ListArgument(settings, "mapping.engines", "A list of mapping engines that shall be used: "
			        + buildEngineList(engineClasses), "[all]", false));
			
			String engines = System.getProperty("mapping.engines");
			Set<String> engineNames = new HashSet<String>();
			
			if (engines != null) {
				for (String engineName : engines.split(",")) {
					engineNames.add(MappingEngine.class.getPackage().getName() + "." + engineName);
				}
				
			}
			
			for (Class<? extends MappingEngine> klass : engineClasses) {
				if (engineNames.isEmpty() || engineNames.contains(klass.getCanonicalName())) {
					if ((klass.getModifiers() & Modifier.ABSTRACT) == 0) {
						if (Logger.logInfo()) {
							Logger.info("Adding new MappingEngine " + klass.getCanonicalName());
						}
						
						MappingEngine engine = klass.newInstance();
						engine.register(settings, this, isRequired);
						this.engines.add(engine);
					}
				} else {
					if (Logger.logInfo()) {
						Logger.info("Not loading available engine: " + klass.getSimpleName());
					}
				}
			}
			
			Package package2 = MappingStrategy.class.getPackage();
			Collection<Class<? extends MappingStrategy>> strategyClasses = ClassFinder.getClassesExtendingClass(
			        package2, MappingStrategy.class, Modifier.ABSTRACT | Modifier.INTERFACE | Modifier.PRIVATE);
			addArgument(new ListArgument(
			        settings,
			        "mapping.strategies",
			        "A list of mapping strategies that shall be used (Strategies are stackable, however it doesn't make much sense for a lot of combinations). Available: "
			                + buildStrategyList(strategyClasses), null, true));
			
			String strategies = System.getProperty("mapping.strategies");
			Set<String> strategyNames = new HashSet<String>();
			
			if (strategies != null) {
				for (String strategyName : strategies.split(",")) {
					strategyNames.add(MappingStrategy.class.getPackage().getName() + "." + strategyName);
				}
			}
			
			for (Class<? extends MappingStrategy> klass : strategyClasses) {
				if (strategyNames.isEmpty() || strategyNames.contains(klass.getCanonicalName())) {
					if (Logger.logInfo()) {
						Logger.info("Adding new MappingStrategy " + klass.getCanonicalName());
					}
					MappingStrategy strategy = klass.newInstance();
					strategy.register(settings, this, isRequired);
					this.strategies.add(strategy);
				} else {
					if (Logger.logInfo()) {
						Logger.info("Not loading available strategy: " + klass.getSimpleName());
					}
				}
			}
			
			MappingFilter.class.getPackage();
			Collection<Class<? extends MappingFilter>> filterClasses = ClassFinder.getClassesExtendingClass(package2,
			        MappingFilter.class, Modifier.ABSTRACT | Modifier.INTERFACE | Modifier.PRIVATE);
			addArgument(new ListArgument(
			        settings,
			        "mapping.filters",
			        "A list of mapping strategies that shall be used (Strategies are stackable, however it doesn't make much sense for a lot of combinations). Available: "
			                + buildFilterList(filterClasses), null, true));
			
			String filters = System.getProperty("mapping.filters");
			Set<String> filterNames = new HashSet<String>();
			
			if (filters != null) {
				for (String filterName : filters.split(",")) {
					strategyNames.add(MappingFilter.class.getPackage().getName() + "." + filterName);
				}
			}
			
			for (Class<? extends MappingFilter> klass : filterClasses) {
				if (filterNames.isEmpty() || filterNames.contains(klass.getCanonicalName())) {
					if (Logger.logInfo()) {
						Logger.info("Adding new MappingFilter " + klass.getCanonicalName());
					}
					MappingFilter filter = klass.newInstance();
					filter.register(settings, this, isRequired);
					this.filters.add(filter);
				} else {
					if (Logger.logInfo()) {
						Logger.info("Not loading available strategy: " + klass.getSimpleName());
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
	private String buildEngineList(final Collection<Class<? extends MappingEngine>> engines) {
		StringBuilder builder = new StringBuilder();
		builder.append(FileUtils.lineSeparator);
		for (Class<? extends MappingEngine> klass : engines) {
			try {
				builder.append('\t').append("  ").append(klass.getSimpleName()).append(": ")
				        .append(klass.newInstance().getDescription());
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			}
			
			if (builder.length() != 0) {
				builder.append(FileUtils.lineSeparator);
			}
		}
		return builder.toString();
	}
	
	/**
	 * @param filters
	 * @return
	 */
	private String buildFilterList(final Collection<Class<? extends MappingFilter>> filters) {
		StringBuilder builder = new StringBuilder();
		builder.append(FileUtils.lineSeparator);
		for (Class<? extends MappingFilter> klass : filters) {
			try {
				builder.append('\t').append("  ").append(klass.getSimpleName()).append(": ")
				        .append(klass.newInstance().getDescription());
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			}
			
			if (builder.length() != 0) {
				builder.append(FileUtils.lineSeparator);
			}
		}
		return builder.toString();
	}
	
	/**
	 * @param strategies
	 * @return
	 */
	private String buildStrategyList(final Collection<Class<? extends MappingStrategy>> strategies) {
		StringBuilder builder = new StringBuilder();
		builder.append(FileUtils.lineSeparator);
		for (Class<? extends MappingStrategy> klass : strategies) {
			try {
				builder.append('\t').append("  ").append(klass.getSimpleName()).append(": ")
				        .append(klass.newInstance().getDescription());
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			}
			
			if (builder.length() != 0) {
				builder.append(FileUtils.lineSeparator);
			}
		}
		return builder.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * 
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
