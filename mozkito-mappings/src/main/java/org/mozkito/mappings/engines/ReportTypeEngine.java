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
package org.mozkito.mappings.engines;

import java.util.HashMap;
import java.util.Map;

import org.mozkito.issues.tracker.elements.Type;
import org.mozkito.issues.tracker.model.Report;
import org.mozkito.mappings.mappable.FieldKey;
import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.mappings.mappable.model.MappableReport;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Relation;
import org.mozkito.mappings.requirements.Atom;
import org.mozkito.mappings.requirements.Expression;
import org.mozkito.mappings.requirements.Index;
import org.mozkito.mappings.requirements.Or;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.DoubleArgument;
import net.ownhero.dev.hiari.settings.EnumArgument;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.conditions.Condition;

/**
 * The Class ReportTypeEngine.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class ReportTypeEngine extends MappingEngine {
	
	/**
	 * The Class Options.
	 */
	public static final class Options extends
	        ArgumentSetOptions<ReportTypeEngine, ArgumentSet<ReportTypeEngine, Options>> {
		
		/** The confidence option. */
		private DoubleArgument.Options                                    confidenceOption;
		private net.ownhero.dev.hiari.settings.EnumArgument.Options<Type> typeOption;
		
		/**
		 * Instantiates a new options.
		 * 
		 * @param argumentSet
		 *            the argument set
		 * @param requirements
		 *            the requirements
		 */
		public Options(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
			super(argumentSet, ReportTypeEngine.class.getSimpleName(), "...", requirements);
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
		 */
		@Override
		public ReportTypeEngine init() {
			// PRECONDITIONS
			
			try {
				final DoubleArgument confidenceArgument = getSettings().getArgument(this.confidenceOption);
				final EnumArgument<Type> typeArgument = getSettings().getArgument(this.typeOption);
				
				return new ReportTypeEngine(confidenceArgument.getValue(), typeArgument.getValue());
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
				final Map<String, IOptions<?, ?>> map = new HashMap<>();
				this.confidenceOption = new DoubleArgument.Options(
				                                                   argumentSet,
				                                                   "confidence", //$NON-NLS-1$
				                                                   Messages.getString("AuthorEqualityEngine.confidenceDescription"), //$NON-NLS-1$
				                                                   getDefaultConfidence(), Requirement.required);
				map.put(this.confidenceOption.getName(), this.confidenceOption);
				
				this.typeOption = new EnumArgument.Options<Type>(argumentSet,
				                                                 "type", //$NON-NLS-1$
				                                                 Messages.getString("ReportTypeEngine.typeDescription"), //$NON-NLS-1$
				                                                 getDefaultType(), Requirement.required);
				map.put(this.typeOption.getName(), this.typeOption);
				return map;
			} finally {
				// POSTCONDITIONS
			}
		}
		
	}
	
	/**
	 * Gets the default confidence.
	 * 
	 * @return the defaultconfidence
	 */
	private static final Double getDefaultConfidence() {
		// PRECONDITIONS
		
		try {
			return DEAFULT_CONFIDENCE;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(DEAFULT_CONFIDENCE, "Field '%s' in '%s'.", "defaultConfidence", //$NON-NLS-1$ //$NON-NLS-2$
			                  ReportTypeEngine.class.getSimpleName());
		}
	}
	
	/**
	 * Gets the default type.
	 * 
	 * @return the defaultType
	 */
	private static final Type getDefaultType() {
		// PRECONDITIONS
		
		try {
			return DEFAULT_TYPE;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(DEFAULT_TYPE,
			                  "Field '%s' in '%s'.", "defaultType", ReportTypeEngine.class.getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/** The type. */
	private Type                type;
	
	/** The constant description. */
	private static final String DESCRIPTION        = "";      //$NON-NLS-1$
	                                                           
	/** The constant defaultConfidence. */
	private static final Double DEAFULT_CONFIDENCE = 1d;
	
	/** The default type. */
	private static final Type   DEFAULT_TYPE       = Type.BUG;
	
	/** The confidence. */
	private Double              confidence;
	
	/**
	 * @param value
	 * @param value2
	 */
	public ReportTypeEngine(final Double confidence, final Type type) {
		// PRECONDITIONS
		
		try {
			this.type = type;
			this.confidence = confidence;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the confidence.
	 * 
	 * @return the confidence
	 */
	private final Double getConfidence() {
		// PRECONDITIONS
		
		try {
			return this.confidence;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.confidence, "Field '%s' in '%s'.", "confidence", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	@Override
	public String getDescription() {
		return DESCRIPTION;
	}
	
	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public Type getType() {
		return this.type;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.engines.MappingEngine#score(de
	 * .unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity,
	 * org.mozkito.mapping.mappable.MappableEntity, org.mozkito.mapping.model.Mapping)
	 */
	@Override
	public void score(final MappableEntity element1,
	                  final MappableEntity element2,
	                  final Relation score) {
		if (element1 instanceof MappableReport) {
			if (element1.get(FieldKey.TYPE) == getType()) {
				addFeature(score, getConfidence(), FieldKey.TYPE.name(), element1.get(FieldKey.TYPE).toString(),
				           element1.get(FieldKey.TYPE).toString(), getUnused(), getUnknown(), getUnknown());
			}
		} else if (element2 instanceof MappableReport) {
			if (element2.get(FieldKey.TYPE) == getType()) {
				addFeature(score, getConfidence(), FieldKey.TYPE.name(), element2.get(FieldKey.TYPE).toString(),
				           element2.get(FieldKey.TYPE).toString(), getUnused(), getUnknown(), getUnknown());
			}
		} else {
			addFeature(score, -getConfidence(), getUnused(), getUnknown(), getUnknown(), getUnused(), getUnknown(),
			           getUnknown());
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.engines.MappingEngine#supported()
	 */
	@Override
	public Expression supported() {
		return new Or(new Atom(Index.FROM, Report.class), new Atom(Index.TO, Report.class));
		
	}
	
}
