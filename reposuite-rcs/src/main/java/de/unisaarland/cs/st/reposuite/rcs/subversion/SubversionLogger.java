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
package de.unisaarland.cs.st.reposuite.rcs.subversion;

import java.util.logging.Level;

import org.tmatesoft.svn.util.SVNDebugLogAdapter;
import org.tmatesoft.svn.util.SVNLogType;

import net.ownhero.dev.kisa.Logger;

/**
 * Log wrapper class for tmatesofts svnkit to redirect logging to our wrapper
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class SubversionLogger extends SVNDebugLogAdapter {
	
	/*
	 * (non-Javadoc)
	 * @see
	 * org.tmatesoft.svn.util.ISVNDebugLog#log(org.tmatesoft.svn.util.SVNLogType
	 * , java.lang.String, byte[])
	 */
	@Override
	public void log(SVNLogType logType, String message, byte[] data) {
		if (Logger.logTrace()) {
			Logger.trace(message, 4);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * org.tmatesoft.svn.util.ISVNDebugLog#log(org.tmatesoft.svn.util.SVNLogType
	 * , java.lang.String, java.util.logging.Level)
	 */
	@Override
	public void log(SVNLogType logType, String message, Level logLevel) {
		if (Logger.logTrace()) {
			Logger.trace(message, 4);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * org.tmatesoft.svn.util.ISVNDebugLog#log(org.tmatesoft.svn.util.SVNLogType
	 * , java.lang.Throwable, java.util.logging.Level)
	 */
	@Override
	public void log(SVNLogType logType, Throwable th, Level logLevel) {
		if (Logger.logTrace()) {
			Logger.trace(th.getMessage(), 4);
		}
	}
}
