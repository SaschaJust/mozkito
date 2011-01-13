/*******************************************************************************
 * PPA - Partial Program Analysis for Java
 * Copyright (C) 2008 Barthelemy Dagenais
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this library. If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.txt>
 *******************************************************************************/
package de.unisaarland.cs.st.reposuite.ppa;

public class PPAOptions {
	
	private boolean allowMemberInference = true;
	
	private boolean allowCollectiveMode = false;
	
	private boolean allowTypeInferenceMode = true;
	
	private boolean allowMethodBindingMode = true;
	
	private int maxMISize = -1;
	
	public PPAOptions() {
		super();
	}
	
	public PPAOptions(final boolean allowMemberInference, final boolean allowCollectiveMode,
			final boolean allowTypeInferenceMode, final boolean allowMethodBindingMode, final int maxMISize) {
		super();
		this.allowMemberInference = allowMemberInference;
		this.allowCollectiveMode = allowCollectiveMode;
		this.allowTypeInferenceMode = allowTypeInferenceMode;
		this.allowMethodBindingMode = allowMethodBindingMode;
		this.maxMISize = maxMISize;
	}
	
	public int getMaxMISize() {
		return maxMISize;
	}
	
	public boolean isAllowCollectiveMode() {
		return allowCollectiveMode;
	}
	
	public boolean isAllowMemberInference() {
		return allowMemberInference;
	}
	
	public boolean isAllowMethodBindingMode() {
		return allowMethodBindingMode;
	}
	
	public boolean isAllowTypeInferenceMode() {
		return allowTypeInferenceMode;
	}
	
	public void setAllowCollectiveMode(final boolean allowCollectiveMode) {
		this.allowCollectiveMode = allowCollectiveMode;
	}
	
	public void setAllowMemberInference(final boolean allowMemberInference) {
		this.allowMemberInference = allowMemberInference;
	}
	
	public void setAllowMethodBindingMode(final boolean allowMethodBindingMode) {
		this.allowMethodBindingMode = allowMethodBindingMode;
	}
	
	public void setAllowTypeInferenceMode(final boolean allowTypeInferenceMode) {
		this.allowTypeInferenceMode = allowTypeInferenceMode;
	}
	
	public void setMaxMISize(final int maxMISize) {
		this.maxMISize = maxMISize;
	}
	
	
	
}
