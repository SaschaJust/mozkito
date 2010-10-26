/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.subversion;

import java.util.logging.Level;

import org.tmatesoft.svn.util.SVNDebugLogAdapter;
import org.tmatesoft.svn.util.SVNLogType;

import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * Log wrapper class for tmatesofts svnkit to redirect logging to our wrapper
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class SubversionLogger extends SVNDebugLogAdapter {
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tmatesoft.svn.util.ISVNDebugLog#log(org.tmatesoft.svn.util.SVNLogType
	 * , java.lang.String, byte[])
	 */
	@Override
	public void log(SVNLogType logType, String message, byte[] data) {
		if (RepoSuiteSettings.logTrace()) {
			Logger.trace(message, 4);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tmatesoft.svn.util.ISVNDebugLog#log(org.tmatesoft.svn.util.SVNLogType
	 * , java.lang.String, java.util.logging.Level)
	 */
	@Override
	public void log(SVNLogType logType, String message, Level logLevel) {
		if (RepoSuiteSettings.logTrace()) {
			Logger.trace(message, 4);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tmatesoft.svn.util.ISVNDebugLog#log(org.tmatesoft.svn.util.SVNLogType
	 * , java.lang.Throwable, java.util.logging.Level)
	 */
	@Override
	public void log(SVNLogType logType, Throwable th, Level logLevel) {
		if (RepoSuiteSettings.logTrace()) {
			Logger.trace(th.getMessage(), 4);
		}
	}
}
