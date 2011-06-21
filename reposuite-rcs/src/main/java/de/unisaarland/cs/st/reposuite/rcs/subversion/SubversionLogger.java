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
