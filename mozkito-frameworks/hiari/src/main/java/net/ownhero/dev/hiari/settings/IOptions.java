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
package net.ownhero.dev.hiari.settings;

import net.ownhero.dev.hiari.settings.requirements.Requirement;

/**
 * The Interface IOptions.
 * 
 * @param <T>
 *            the generic type
 * @param <X>
 *            the generic type
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public interface IOptions<T, X> extends Comparable<IOptions<?, ?>> {
	
	/**
	 * Gets the additional help string.
	 * 
	 * @return the additional help string
	 */
	String getAdditionalHelpString();
	
	/**
	 * Gets the argument set.
	 * 
	 * @return the argument set
	 */
	ArgumentSet<?, ?> getArgumentSet();
	
	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	String getDescription();
	
	/**
	 * Gets the handle.
	 * 
	 * @return the handle
	 */
	String getHandle();
	
	/**
	 * Gets the help string.
	 * 
	 * @param keyWidth
	 *            the key width
	 * @return the help string
	 */
	String getHelpString(final int keyWidth);
	
	/**
	 * Gets the help string.
	 * 
	 * @param keyWidth
	 *            the key width
	 * @param indentation
	 *            the indentation
	 * @return the help string
	 */
	String getHelpString(final int keyWidth,
	                     final int indentation);
	
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	String getName();
	
	/**
	 * Gets the parent.
	 * 
	 * @return the parent
	 */
	ArgumentSet<?, ?> getParent();
	
	/**
	 * Gets the requirements.
	 * 
	 * @return the requirements
	 */
	Requirement getRequirements();
	
	/**
	 * Gets the settings.
	 * 
	 * @return the settings
	 */
	ISettings getSettings();
	
	/**
	 * Gets the tag.
	 * 
	 * @return the tag
	 */
	public String getTag();
	
	/**
	 * Required.
	 * 
	 * @return true, if successful
	 */
	boolean required();
}
