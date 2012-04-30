/*******************************************************************************
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
 ******************************************************************************/
package net.ownhero.dev.hiari.settings;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.requirements.Optional;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

/**
 * The Class ArgumentSet.
 * 
 * @param <TYPE>
 *            the generic type
 * @param <ARGSETOPTIONS>
 *            the generic type
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class ArgumentSet<TYPE, ARGSETOPTIONS extends ArgumentSetOptions<TYPE, ? extends ArgumentSet<TYPE, ?>>>
        implements IArgument<TYPE, ARGSETOPTIONS> {
	
	/** The arguments. */
	@SuppressWarnings ("rawtypes")
	private final HashMap<String, Argument>    arguments    = new HashMap<String, Argument>();
	
	/** The argument sets. */
	@SuppressWarnings ("rawtypes")
	private final HashMap<String, ArgumentSet> argumentSets = new HashMap<String, ArgumentSet>();
	/** The name. */
	private final String                       name;
	
	/** The description. */
	private final String                       description;
	
	/** The requirements. */
	private final Requirement                  requirements;
	
	/** The settings. */
	private final ISettings                    settings;
	
	/** The cached value. */
	private TYPE                               cachedValue  = null;
	
	/** The argument set. */
	private final ArgumentSet<?, ?>            argumentSet;
	
	/** The configurator. */
	private ARGSETOPTIONS                      configurator;
	
	/** The initialized. */
	private boolean                            initialized  = false;
	
	/**
	 * Instantiates a new argument set.
	 * 
	 * @param options
	 *            the options
	 * @throws ArgumentSetRegistrationException
	 *             the argument set registration exception
	 */
	ArgumentSet(final @NotNull ARGSETOPTIONS options) throws ArgumentSetRegistrationException {
		// PRECONDITIONS
		
		try {
			if (Logger.logTrace()) {
				Logger.trace(String.format("Setting name to: %s", options.getName()));
			}
			this.name = options.getName();
			
			if (Logger.logTrace()) {
				Logger.trace(String.format("Setting description to: %s", options.getName()));
			}
			this.description = options.getDescription();
			
			if (Logger.logTrace()) {
				Logger.trace(String.format("Setting parent to: %s", options.getArgumentSet()));
			}
			this.argumentSet = options.getArgumentSet();
			
			if (Logger.logTrace()) {
				Logger.trace(String.format("Setting settings field."));
			}
			this.settings = options.getArgumentSet().getSettings();
			
			if (Logger.logTrace()) {
				Logger.trace(String.format("Setting requirements to: %s", options.getRequirements()));
			}
			this.requirements = options.getRequirements();
			
			if (Logger.logTrace()) {
				Logger.trace(String.format("Setting configurator to: %s", options));
			}
			this.configurator = options;
			
			if (!this.argumentSet.addArgument(this)) {
				if (Logger.logTrace()) {
					Logger.trace(String.format("Attaching this ArgumentSet (tag: '%s') to the parent (tag: '%s') failed.",
					                           options.getTag(), getParent().getTag()));
				}
				throw new ArgumentSetRegistrationException("Could not register argument set " + getHandle() + ".",
				                                           this, options);
			}
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.argumentSet, "Field '%s' in %s.", "argumentSet", getHandle());
			Condition.notNull(this.name, "Field '%s' in %s.", "name", getHandle());
			Condition.notNull(this.description, "Field '%s' in %s.", "description", getHandle());
			Condition.notNull(this.settings, "Field '%s' in %s.", "settings", getHandle());
			Condition.notNull(this.requirements, "Field '%s' in %s.", "requirements", getHandle());
			Condition.notNull(this.configurator, "Field '%s' in %s.", "configurator", getHandle());
		}
	}
	
	/**
	 * Instantiates a new argument set (for root argument set only).
	 * 
	 * @param settings
	 *            the settings
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 */
	@Deprecated
	ArgumentSet(final ISettings settings, final String name, final String description) {
		if (Logger.logTrace()) {
			Logger.trace("Deprecated constructor used. This should only be done by Settings to create the ROOT element. Element: "
			        + name);
		}
		
		this.name = name;
		this.settings = settings;
		this.description = description;
		this.requirements = Requirement.required;
		this.argumentSet = null;
		this.configurator = null;
	}
	
	/**
	 * Call this method to add an argument to the set of arguments. But be aware that you have to set all arguments
	 * before adding it to the MinerSettings!
	 * 
	 * @param argument
	 *            MinerArgument to be added
	 * @return <code>true</code> if the argument could be added. <code>false</code> otherwise.
	 */
	boolean addArgument(@SuppressWarnings ("rawtypes") @NotNull final Argument argument) {
		try {
			if (Logger.logTrace()) {
				Logger.trace(">>> addArgument(argument)");
			}
			
			if (argument.getName().contains(".")) {
				if (Logger.logWarn()) {
					Logger.warn("Argument tags may never contain '.'. Cannot register argument: " + argument.getName()
					        + ":" + argument.getHandle());
				}
				// names may not contain the tag of the set
				return false;
			}
			
			if (Logger.logTrace()) {
				Logger.trace(String.format("Checking if the Argument (tag: '%s') is already known to the current (parent) set (this.tag: '%s').",
				                           argument.getTag(), getTag()));
			}
			if (this.arguments.containsKey(argument.getName())) {
				if (Logger.logWarn()) {
					Logger.warn(String.format("Argument (tag: '%s') already known to the current (parent) (this.tag: '%s'). Argument in its current state renders as follows: %s",
					                          argument.getTag(), getTag(), argument));
				}
				return false;
			}
			
			if (Logger.logTrace()) {
				Logger.trace(String.format("Adding the Argument (tag: '%s') to the current (parent) set (tag: '%s').",
				                           argument.getTag(), getTag()));
			}
			this.arguments.put(argument.getName(), argument);
			
			if (Logger.logTrace()) {
				Logger.trace(String.format("Communicating new argument mapping for the Argument (tag: '%s') to the settings entity.",
				                           argument.getTag()));
			}
			// tell settings who is responsible for this artifact
			if (!((Settings) getSettings()).addArgumentMapping(argument.getTag(), this)) {
				if (Logger.logWarn()) {
					Logger.warn(String.format("Settings denied registration of the new argument mapping for the Argument (tag: '%s') to the settings entity.",
					                          argument.getTag()));
				}
				return false;
			}
			
			if (Logger.logTrace()) {
				Logger.trace(String.format("Adding the new Argument (tag: '%s') was successful.", argument.getTag()));
			}
			return true;
		} finally {
			if (Logger.logTrace()) {
				Logger.trace("<<< addArgument(argument)");
			}
		}
	}
	
	/**
	 * Adds the argument.
	 * 
	 * @param argumentSet
	 *            the argument
	 * @return true, if successful
	 */
	@SuppressWarnings ("rawtypes")
	private final boolean addArgument(@NotNull final ArgumentSet argumentSet) {
		try {
			if (Logger.logTrace()) {
				Logger.trace(">>> addArgument(argumentSet)");
			}
			
			if (Logger.logTrace()) {
				Logger.trace(String.format("Registering new ArgumentSet (tag: '%s')", argumentSet.getTag()));
			}
			
			if (Logger.logTrace()) {
				Logger.trace(String.format("Checking if the Argument (tag: '%s') is already known to the settings entity.",
				                           argumentSet.getTag()));
			}
			if (getSettings().hasSetting(argumentSet.getTag())) {
				if (Logger.logWarn()) {
					Logger.warn(String.format("ArgumentSet (tag: '%s') already known to settings. ArgumentSet in its current state renders as follows: %s",
					                          argumentSet.getTag(), argumentSet));
				}
				return false;
			}
			
			if (Logger.logTrace()) {
				Logger.trace(String.format("Checking if current ArgumentSet (this.tag: '%s') already knows about the ArgumentSet under suspect (tag: '%s').",
				                           getTag(), argumentSet.getTag()));
			}
			if (this.argumentSets.containsKey(argumentSet.getName())) {
				if (Logger.logWarn()) {
					Logger.warn(String.format("ArgumentSet (tag: '%s') already known to the parent set (tag: '%s'). ArgumentSet in its current state renders as follows: %s",
					                          getTag(), argumentSet.getTag(), argumentSet));
				}
				return false;
			}
			
			if (Logger.logTrace()) {
				Logger.trace(String.format("Adding the ArgumentSet (tag: '%s') to the local mapping (within tag: '%s').",
				                           argumentSet.getTag(), getTag()));
			}
			this.argumentSets.put(argumentSet.getName(), argumentSet);
			
			if (Logger.logTrace()) {
				Logger.trace(String.format("Communicating new argument mapping for the ArgumentSet (tag: '%s') to the settings entity.",
				                           argumentSet.getTag()));
			}
			if (!((Settings) getSettings()).addArgumentMapping(argumentSet.getTag(), argumentSet)) {
				if (Logger.logWarn()) {
					Logger.warn(String.format("Settings denied registration of the new argument mapping for the ArgumentSet (tag: '%s') to the settings entity.",
					                          argumentSet.getTag()));
				}
			}
			
			if (Logger.logTrace()) {
				Logger.trace(String.format("Adding the new ArgumentSet (tag: '%s') was successful.",
				                           argumentSet.getTag()));
			}
			return true;
		} finally {
			if (Logger.logTrace()) {
				Logger.trace("<<< addArgument(argumentSet)");
			}
		}
	}
	
	/**
	 * Compare to.
	 * 
	 * @param arg0
	 *            the arg0
	 * @return the int
	 */
	@Override
	public final int compareTo(final IArgument<?, ?> arg0) {
		if (this == arg0) {
			return 0;
		}
		
		final Set<IOptions<?, ?>> dependencies = getDependencies();
		
		if (dependencies.contains(arg0.getOptions())) {
			return 1;
		} else if (dependencies.contains(this)) {
			return 0;
		} else {
			int ret = -1;
			for (final IOptions<?, ?> argX : dependencies) {
				ret = argX.compareTo(arg0.getOptions());
				if (ret != 0) {
					return ret;
				}
			}
			return ret;
		}
	}
	
	/**
	 * Gets the argument.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param <X>
	 *            the generic type
	 * @param <Y>
	 *            the generic type
	 * @param option
	 *            the option
	 * @return the argument
	 */
	public <T, X extends ArgumentOptions<T, Y>, Y extends Argument<T, X>> Y getArgument(final IArgumentOptions<T, Y> option) {
		if (Logger.logTrace()) {
			Logger.trace(String.format("Looking up Argument (tag: '%s').", option.getTag()));
		}
		
		if (getArguments().containsKey(option.getName())) {
			@SuppressWarnings ("unchecked")
			final Y set = (Y) getArguments().get(option.getName());
			
			if (Logger.logTrace()) {
				Logger.trace(String.format("Found Argument (tag: '%s').", option.getTag()));
			}
			return set;
		}
		if (Logger.logTrace()) {
			Logger.trace(String.format("Could not find Argument (tag: '%s').", option.getTag()));
		}
		return null;
	}
	
	/**
	 * Return the arguments held within the set.
	 * 
	 * @return the arguments
	 */
	@SuppressWarnings ("rawtypes")
	private final Map<String, Argument> getArguments() {
		return this.arguments;
	}
	
	/**
	 * Gets the argument set.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param <X>
	 *            the generic type
	 * @param <Y>
	 *            the generic type
	 * @param option
	 *            the option
	 * @return the argument set
	 */
	public <T, X extends ArgumentSetOptions<T, Y>, Y extends ArgumentSet<T, X>> Y getArgumentSet(final IArgumentSetOptions<T, Y> option) {
		if (Logger.logTrace()) {
			Logger.trace(String.format("Looking up ArgumentSet (tag: '%s').", option.getTag()));
		}
		
		if (getArgumentSets().containsKey(option.getName())) {
			@SuppressWarnings ("unchecked")
			final Y set = (Y) getArgumentSets().get(option.getName());
			
			if (Logger.logTrace()) {
				Logger.trace(String.format("Found ArgumentSet (tag: '%s').", option.getTag()));
			}
			return set;
		}
		if (Logger.logTrace()) {
			Logger.trace(String.format("Could not find ArgumentSet (tag: '%s').", option.getTag()));
		}
		return null;
		
	}
	
	/**
	 * Gets the argument sets.
	 * 
	 * @return the argument sets
	 */
	@SuppressWarnings ("rawtypes")
	Map<String, ArgumentSet> getArgumentSets() {
		return this.argumentSets;
	}
	
	/**
	 * Gets the cached value.
	 * 
	 * @return the cached value
	 */
	protected final TYPE getCachedValue() {
		return this.cachedValue;
	}
	
	/**
	 * Gets the children.
	 * 
	 * @return the children
	 */
	@SuppressWarnings ("rawtypes")
	public Collection<IArgument> getChildren() {
		final LinkedList<IArgument> list = new LinkedList<IArgument>();
		list.addAll(getArguments().values());
		list.addAll(getArgumentSets().values());
		return list;
	}
	
	/**
	 * Gets the dependencies.
	 * 
	 * @return the dependees
	 */
	@Override
	public final Set<IOptions<?, ?>> getDependencies() {
		return this.requirements.getDependencies();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#getDescription()
	 */
	@Override
	public final String getDescription() {
		return this.description;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#getHandle()
	 */
	@Override
	public final String getHandle() {
		return getClass().getSimpleName();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String getHelpString() {
		return getHelpString(0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#toString(int)
	 */
	/**
	 * Gets the help string.
	 * 
	 * @param indentation
	 *            the indentation
	 * @return the help string
	 */
	public String getHelpString(final int indentation) {
		int maxWidth = 0;
		for (final IArgument<?, ? extends IOptions<?, IArgument<?, ?>>> argument : getChildren()) {
			final Tuple<Integer, Integer> tuple = argument.getKeyValueSpan();
			if (tuple.getFirst() > maxWidth) {
				maxWidth = tuple.getFirst();
			}
		}
		
		return getHelpString(maxWidth, indentation);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.IArgument#getHelpString(int, int)
	 */
	@Override
	public String getHelpString(final int keyWidth,
	                            final int indentation) {
		// PRECONDITIONS
		
		try {
			final StringBuilder builder = new StringBuilder();
			
			final StringBuilder indent = new StringBuilder();
			final StringBuilder header = new StringBuilder();
			
			for (int i = 0, j = 0; i < indentation; ++i, ++j) {
				indent.append("| ");
				if ((j + 1) < indentation) {
					header.append("| ");
				}
			}
			
			builder.append(header).append("|-[ ").append(getName()).append(" ]").append(FileUtils.lineSeparator);
			builder.append(header).append("| `-Description: ").append(getDescription());
			builder.append(required()
			                         ? " (Required, due to " + getRequirements() + ")"
			                         : "");
			
			for (final IArgument<?, ? extends IOptions<?, IArgument<?, ?>>> argument : getChildren()) {
				builder.append(FileUtils.lineSeparator).append(indent)
				       .append(argument.getHelpString(keyWidth, indentation + 1));
			}
			
			return builder.toString();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#getKeyValueSpan()
	 */
	@Override
	public Tuple<Integer, Integer> getKeyValueSpan() {
		final Tuple<Integer, Integer> tuple = new Tuple<Integer, Integer>(0, 0);
		
		for (final IArgument<?, ?> arg : getChildren()) {
			final Tuple<Integer, Integer> span = arg.getKeyValueSpan();
			
			if (span.getFirst() > tuple.getFirst()) {
				tuple.setFirst(span.getFirst());
			}
			
			if (span.getSecond() > tuple.getSecond()) {
				tuple.setSecond(span.getSecond());
			}
		}
		
		return new Tuple<Integer, Integer>(tuple.getFirst(), tuple.getSecond());
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.IArgument#getOptions()
	 */
	@Override
	public ARGSETOPTIONS getOptions() {
		// PRECONDITIONS
		
		try {
			return this.configurator;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.IArgument#getParent()
	 */
	@Override
	public ArgumentSet<?, ?> getParent() {
		// PRECONDITIONS
		
		try {
			return this.argumentSet;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#getRequirements()
	 */
	@Override
	public Requirement getRequirements() {
		return this.requirements;
	}
	
	/**
	 * Gets the settings.
	 * 
	 * @return the settings
	 */
	@Override
	public final ISettings getSettings() {
		return this.settings;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.IArgument#getParent()
	 */
	@Override
	public String getTag() {
		final StringBuilder builder = new StringBuilder();
		final LinkedList<String> list = new LinkedList<String>();
		ArgumentSet<?, ?> parent = this;
		
		while (((parent = parent.getParent()) != null)
		        && !parent.getName().equals(Settings.RootArgumentSet.Options.TAG)) {
			list.add(parent.getName());
		};
		
		final Iterator<String> iterator = list.descendingIterator();
		
		while (iterator.hasNext()) {
			if (builder.length() > 0) {
				builder.append('.');
			}
			
			builder.append(iterator.next());
		}
		
		if (builder.length() > 0) {
			builder.append('.');
		}
		builder.append(getName());
		
		return builder.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#getValue()
	 */
	@Override
	public final TYPE getValue() {
		return this.getCachedValue();
	}
	
	// /*
	// * (non-Javadoc)
	// * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#parse()
	// */
	// @SuppressWarnings ("rawtypes")
	// @Override
	// public void parse() throws SettingsParseError {
	// LinkedList<IArgument> list = new LinkedList<IArgument>(this.arguments.values());
	// list.addAll(this.argumentSets.values());
	// LinkedList<IArgument> list2 = new LinkedList<IArgument>();
	//
	// while (!list.isEmpty()) {
	// for (final IArgument<?, ?> argument : list) {
	// argument.parse();
	//
	// Condition.notNull(argument.getRequirements().getFailedChecks(),
	// "Field argument.getRequirements().getFailedChecks() must not be null. Please return empty list.");
	// if (!argument.getRequirements().getFailedChecks().isEmpty()) {
	// list2.add(argument);
	// }
	// }
	//
	// if (list2.isEmpty()) {
	// break;
	// } else if (list.size() == list2.size()) {
	// for (final IArgument<?, ?> argument : list) {
	// if (argument.required()) {
	// throw new SettingsParseError(
	// "Could not resolved dependencies. Arguments have unresolved dependencies: ",
	// list2.iterator().next());
	//
	// }
	// if (Logger.logWarn()) {
	// Logger.warn("Skipping: " + argument);
	// }
	// }
	// break;
	// } else {
	// list = list2;
	// list2 = new LinkedList<IArgument>();
	// }
	// }
	//
	// this.initialized = true;
	// }
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#required()
	 */
	@Override
	public boolean required() {
		return ((getParent() == null) || getParent().required()) && getRequirements().check()
		        && !(getRequirements() instanceof Optional);
	}
	
	/**
	 * Sets the cached value.
	 * 
	 * @param cachedValue
	 *            the new cached value
	 */
	protected final void setCachedValue(final TYPE cachedValue) {
		this.initialized = true;
		this.cachedValue = cachedValue;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final Tuple<Integer, Integer> span = getKeyValueSpan();
		return toString(span.getFirst(), span.getSecond());
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#toString(int)
	 */
	@Override
	public String toString(final int keyWidth,
	                       final int valueWidth) {
		final StringBuilder builder = new StringBuilder();
		
		if (!this.initialized && (this instanceof Settings.RootArgumentSet)) {
			builder.append(getSettings().getHandle())
			       .append(" are uninitialized. You might wanna call help (-Dhelp) and display the helpString instead.")
			       .append(FileUtils.lineSeparator);
		}
		
		builder.append("[").append(getName()).append("] ");
		
		if (!this.initialized) {
			builder.append("  <## NOT INITIALIZED ##>  ");
		}
		
		builder.append(getDescription());
		
		for (final IArgument<?, ?> arg : getChildren()) {
			builder.append(FileUtils.lineSeparator).append(arg.toString(keyWidth, valueWidth));
		}
		
		return builder.toString();
	}
	
}
