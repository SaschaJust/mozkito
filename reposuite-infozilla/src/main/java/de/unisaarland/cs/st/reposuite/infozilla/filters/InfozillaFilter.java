/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
/**
 * 
 * InfozillaFilter.java
 * 
 * @author Nicolas Bettenburg � 2009-2010, all rights reserved.
 ******************************************************************** 
 *         This file is part of infoZilla. * * InfoZilla is non-free software:
 *         you may not redistribute it * and/or modify it without the permission
 *         of the original author. * * InfoZilla is distributed in the hope that
 *         it will be useful, * but WITHOUT ANY WARRANTY; without even the
 *         implied warranty of * MERCHANTABILITY or FITNESS FOR A PARTICULAR
 *         PURPOSE. *
 ******************************************************************** 
 * 
 */

package de.unisaarland.cs.st.reposuite.infozilla.Filters;

import java.util.List;

import de.unisaarland.cs.st.reposuite.infozilla.settings.InfozillaArguments;
import de.unisaarland.cs.st.reposuite.infozilla.settings.InfozillaSettings;

/**
 * This interface describes the method interface for every infoZilla
 * InfozillaFilter.
 * 
 * @author Nicolas Bettenburg
 * 
 */
public abstract class InfozillaFilter {
	
	public abstract String getOutputText();
	
	public abstract void init();
	
	public abstract void register(InfozillaSettings settings,
	                              InfozillaArguments infozillaArguments,
	                              boolean isRequired);
	
	public abstract List<?> runFilter(String inputText);
	
}
