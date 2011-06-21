/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public enum TrackerType {
	BUGZILLA, ISSUEZILLA, JIRA, SOURCEFORGE, GOOGLE;
	
	public static String getHandle() {
		return TrackerType.class.getSimpleName();
	}
}
