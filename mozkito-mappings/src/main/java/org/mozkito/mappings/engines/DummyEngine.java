/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just - mozkito.org
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
package org.mozkito.mappings.engines;

import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.mappings.model.Relation;
import org.mozkito.mappings.requirements.Expression;

/**
 * The Class DummyEngine.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class DummyEngine extends Engine {
	
	/**
	 * The Class Options.
	 */
	public static final class Options extends ArgumentSetOptions<DummyEngine, ArgumentSet<DummyEngine, Options>> {
		
		/**
		 * Instantiates a new options.
		 * 
		 * @param argumentSet
		 *            the argument set
		 * @param requirements
		 *            the requirements
		 */
		public Options(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
			super(argumentSet, DummyEngine.class.getSimpleName(), DummyEngine.DESCRIPTION, requirements);
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
		 */
		@Override
		public DummyEngine init() {
			// PRECONDITIONS
			
			try {
				return new DummyEngine();
			} finally {
				// POSTCONDITIONS
			}
		}
		
		/*
		 * (non-Javadoc)
		 * @see
		 * net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
		 */
		@Override
		public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> argumentSet) throws ArgumentRegistrationException,
		                                                                                    SettingsParseError {
			// PRECONDITIONS
			
			try {
				return new HashMap<String, IOptions<?, ?>>();
			} finally {
				// POSTCONDITIONS
			}
		}
		
	}
	
	/** The Constant DESCRIPTION. */
	private static final String DESCRIPTION = "DUMMY"; //$NON-NLS-1$
	                                                   
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.register.Node#getDescription()
	 */
	@Override
	public String getDescription() {
		// PRECONDITIONS
		
		try {
			return DummyEngine.DESCRIPTION;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.engines.Engine#score(org.mozkito.mappings.mappable.model.MappableEntity,
	 * org.mozkito.mappings.mappable.model.MappableEntity, org.mozkito.mappings.model.Relation)
	 */
	@Override
	public void score(final MappableEntity from,
	                  final MappableEntity to,
	                  final Relation score) {
		// PRECONDITIONS
		
		try {
			addFeature(score, 1.0d, "DUMMY", "DUMMY", "DUMMY", "DUMMY", "DUMMY", "DUMMY"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.engines.Engine#supported()
	 */
	@Override
	public Expression supported() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
}
