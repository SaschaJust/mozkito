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

package org.mozkito.graphs.settings;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.EnumArgument;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.graphs.GraphManager;
import org.mozkito.graphs.GraphType;
import org.mozkito.utilities.loading.classpath.ClassFinder;
import org.mozkito.utilities.loading.classpath.exceptions.WrongClassSearchMethodException;

/**
 * The Class GraphOptions.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class GraphOptions extends ArgumentSetOptions<GraphManager, ArgumentSet<GraphManager, GraphOptions>> {
	
	/** The Constant TAG. */
	private static final String                                       TAG                 = "graph";
	
	/** The Constant DESCRIPTION. */
	private static final String                                       DESCRIPTION         = "Graph database settings";
	
	private EnumArgument.Options<GraphType>                           graphTypeOptions;
	
	private EnumArgument<GraphType>                                   graphTypeArgument;
	
	private final Map<GraphType, ArgumentSetOptions<GraphManager, ?>> graphManagerOptions = new HashMap<>();
	
	/**
	 * Instantiates a new graph options.
	 * 
	 * @param argumentSet
	 *            the argument set
	 * @param requirements
	 *            the required
	 */
	public GraphOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
		super(argumentSet, TAG, DESCRIPTION, requirements);
		
		PRECONDITIONS: {
			// none
		}
		
		try {
			// body
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
	 */
	@Override
	public GraphManager init() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.graphTypeArgument = getSettings().getArgument(this.graphTypeOptions);
			final GraphType graphType = this.graphTypeArgument.getValue();
			
			final ArgumentSetOptions<GraphManager, ?> graphManagerOptionSet = this.graphManagerOptions.get(graphType);
			@SuppressWarnings ("unchecked")
			final ArgumentSet<GraphManager, ?> argumentSet = getSettings().getRawArgumentSet(graphManagerOptionSet);
			
			return argumentSet.getValue();
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
	 */
	@SuppressWarnings ({ "rawtypes", "unchecked" })
	@Override
	public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> argumentSet) throws ArgumentRegistrationException,
	                                                                                    SettingsParseError {
		PRECONDITIONS: {
			// none
		}
		
		try {
			
			final Map<String, IOptions<?, ?>> map = new HashMap<>();
			
			this.graphTypeOptions = new EnumArgument.Options<GraphType>(argumentSet, "provider",
			                                                            "Graph database implementation", null,
			                                                            Requirement.required, GraphType.values());
			map.put(this.graphTypeOptions.getName(), this.graphTypeOptions);
			
			final Collection<Class<? extends GraphManager>> managerClasses = GraphManager.getManagerClasses();
			
			// load their configuration classes—if any
			Collection<Class<? extends ArgumentSetOptions>> managersOptions;
			try {
				managersOptions = ClassFinder.getClassesExtendingClass(GraphOptions.class.getPackage(),
				                                                       ArgumentSetOptions.class, Modifier.ABSTRACT
				                                                               | Modifier.PRIVATE | Modifier.INTERFACE
				                                                               | Modifier.PROTECTED);
				
				for (final Class<? extends ArgumentSetOptions> managerOptionsClass : managersOptions) {
					boolean matchingManager = false;
					
					MATCH: for (final Class<? extends GraphManager> mClass : managerClasses) {
						if (managerOptionsClass.getSimpleName()
						                       .toUpperCase()
						                       .replace(GraphOptions.class.getSimpleName().toUpperCase(), "")
						                       .equals(mClass.getSimpleName()
						                                     .toUpperCase()
						                                     .replace(GraphManager.class.getSimpleName().toUpperCase(),
						                                              ""))) {
							matchingManager = true;
							break MATCH;
						}
						
					}
					if (matchingManager) {
						final String theEnum = managerOptionsClass.getSimpleName()
						                                          .toUpperCase()
						                                          .replace(GraphOptions.class.getSimpleName()
						                                                                     .toUpperCase(), "");
						final GraphType gType = GraphType.valueOf(theEnum);
						assert gType != null;
						// TODO error check
						
						Constructor<? extends ArgumentSetOptions> constructor;
						try {
							constructor = managerOptionsClass.getConstructor(ArgumentSet.class, Requirement.class);
							
							if (constructor != null) {
								final ArgumentSetOptions instance = constructor.newInstance(argumentSet,
								                                                            Requirement.optional);
								this.graphManagerOptions.put(gType, instance);
								map.put(instance.getName(), instance);
							} else {
								// this graphmanaaer does not have its own configuration subset
							}
						} catch (NoSuchMethodException | SecurityException | InstantiationException
						        | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							if (Logger.logWarn()) {
								Logger.warn("Could not instantiate '%s'.", managerOptionsClass.getSimpleName());
							}
						}
					}
				}
				
			} catch (ClassNotFoundException | WrongClassSearchMethodException | IOException e) {
				// TODO Auto-generated catch block
				
			}
			
			return map;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
}
