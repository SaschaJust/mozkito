/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.engines;

import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotEmpty;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import de.unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.mapping.register.Registered;
import de.unisaarland.cs.st.reposuite.mapping.storages.MappingStorage;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class MappingEngine extends Registered {
	
	public static final String unused          = "(unused)";
	public static final String unknown         = "(unknown)";
	public static final String defaultNegative = "-1";
	public static final String defaultPositive = "1";
	
	private final boolean      registered      = false;
	private final boolean      initialized     = false;
	
	public MappingEngine() {
		
	}
	
	/**
	 * @return
	 */
	@Override
	public abstract String getDescription();
	
	/**
	 * @param score
	 * @param confidence
	 * @param transactionFieldName
	 * @param transactionFieldContent
	 * @param transactionSubstring
	 * @param reportFieldName
	 * @param reportFieldContent
	 * @param reportSubstring
	 */
	
	public void addFeature(@NotNull final MapScore score, final double confidence,
	        @NotNull @NotEmpty final String transactionFieldName, final Object transactionFieldContent,
	        final Object transactionSubstring, @NotNull @NotEmpty final String reportFieldName,
	        final Object reportFieldContent, final Object reportSubstring) {
		score.addFeature(confidence, truncate(transactionFieldName),
		        truncate(transactionFieldContent != null ? transactionFieldContent.toString() : unused),
		        truncate(transactionSubstring != null ? transactionSubstring.toString()
		                : truncate(transactionFieldContent != null ? transactionFieldContent.toString() : unused)),
		        truncate(reportFieldName),
		        truncate(reportFieldContent != null ? reportFieldContent.toString() : unused),
		        truncate(reportSubstring != null ? reportSubstring.toString()
		                : truncate(reportFieldContent != null ? reportFieldContent.toString() : unused)), getClass());
	}
	
	public abstract Set<Class<?>> supported();
	
	/**
	 * @param transaction
	 * @param report
	 * @param score
	 */
	@NoneNull
	public abstract void score(final MappableEntity element1, final MappableEntity element2, final MapScore score);
	
	/**
	 * @return
	 */
	@Override
	public Set<Class<? extends MappingStorage>> storageDependency() {
		return new HashSet<Class<? extends MappingStorage>>();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MappingEngine [class=");
		builder.append(this.getClass().getSimpleName());
		builder.append("registered=");
		builder.append(this.registered);
		builder.append(", initialized=");
		builder.append(this.initialized);
		builder.append("]");
		return builder.toString();
	}
	
}
