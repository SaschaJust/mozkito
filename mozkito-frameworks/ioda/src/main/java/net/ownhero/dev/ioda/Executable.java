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

package net.ownhero.dev.ioda;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * The Interface Executable.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public interface Executable extends Runnable {
	
	/**
	 * Connect standard in.
	 * 
	 * @param inputStream
	 *            the input stream
	 * @return true, if successful
	 */
	boolean connectStandardIn(InputStream inputStream);
	
	/**
	 * Exit value.
	 * 
	 * @return the int
	 */
	Integer exitValue();
	
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	String getName();
	
	/**
	 * Gets the standard err.
	 * 
	 * @return the standard err
	 */
	InputStream getStandardErr();
	
	/**
	 * Gets the standard in.
	 * 
	 * @return the standard in
	 */
	OutputStream getStandardIn();
	
	/**
	 * Gets the standard out.
	 * 
	 * @return the standard out
	 */
	InputStream getStandardOut();
	
	/**
	 * Pipe from.
	 * 
	 * @param executable
	 *            the executable
	 * @return true, if successful
	 */
	boolean pipeFrom(Executable executable);
	
	/**
	 * Pipe to.
	 * 
	 * @param executable
	 *            the executable
	 * @return true, if successful
	 */
	boolean pipeTo(Executable... executable);
	
	/**
	 * @param err
	 */
	void printStats(PrintStream err);
	
	/**
	 * Redirect standard error.
	 * 
	 * @param redirect
	 *            the redirect
	 */
	void redirectStandardError(boolean redirect);
	
	/**
	 * Sets the logger.
	 * 
	 * @param logStream
	 *            the log stream
	 * @return true, if successful
	 */
	boolean setLogger(PrintStream logStream);
	
	/**
	 * Start.
	 */
	void start();
	
	/**
	 * Wait for.
	 * 
	 * @return the int
	 * @throws InterruptedException
	 */
	int waitFor() throws InterruptedException;
}
