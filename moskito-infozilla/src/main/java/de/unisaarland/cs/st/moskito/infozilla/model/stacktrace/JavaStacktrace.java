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
/**
 * 
 */
package de.unisaarland.cs.st.moskito.infozilla.model.stacktrace;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class JavaStacktrace.
 *
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class JavaStacktrace extends Stacktrace {
	
	/** The exception. */
	private final String       exception;
	
	/** The reason. */
	private final String       reason;
	
	/** The frames. */
	private final List<String> frames;
	
	/** The is cause. */
	private boolean            isCause;
	
	/** The trace start. */
	private int                traceStart;
	
	/** The trace end. */
	private int                traceEnd;
	
	/**
	 * Default Constructor - sets attributes to predefined values.
	 */
	public JavaStacktrace() {
		this.traceStart = 0;
		this.traceEnd = 0;
		this.exception = "Not specified";
		this.reason = "No reason given";
		this.frames = new ArrayList<String>();
		this.isCause = false;
	}
	
	/**
	 * Overloaded Constructor.
	 *
	 * @param exception The Reason given in the Exception.
	 * @param reason The list of locations the trace originated from.
	 */
	public JavaStacktrace(final String exception, final String reason) {
		this.traceStart = 0;
		this.traceEnd = 0;
		this.exception = exception;
		this.reason = reason;
		this.frames = new ArrayList<String>();
		this.isCause = false;
	}
	
	/**
	 * Overloaded Constructor.
	 *
	 * @param exception The Exception resulting in this Stack Trace.
	 * @param reason The Reason given in the Exception.
	 * @param frames The list of locations the trace originated from.
	 */
	public JavaStacktrace(final String exception, final String reason, final List<String> frames) {
		this.traceStart = 0;
		this.traceEnd = 0;
		this.exception = exception;
		this.reason = reason;
		this.frames = frames;
		this.isCause = false;
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.infozilla.model.Inlineable#getEndPosition()
	 */
	@Override
	public int getEndPosition() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * Getter for the Exception.
	 *
	 * @return the exception this Stack Trace originated from
	 */
	public String getException() {
		return this.exception;
	}
	
	/**
	 * Getter for the Trace Locations.
	 *
	 * @return A list of locations where the trace originated from.
	 */
	public List<String> getFrames() {
		return (this.frames);
	}
	
	/**
	 * Joins the text of all Frames in frames.
	 *
	 * @return a single String with all frames concattenated
	 */
	public String getFramesText() {
		String framesText = "";
		for (String frame : this.frames) {
			framesText = framesText + "at " + frame + System.getProperty("line.separator");
		}
		return framesText;
	}
	
	/**
	 * Getter for the Reason.
	 *
	 * @return the reason for this Stack Trace stated in the first Exception. (removed trailing :)
	 */
	public String getReason() {
		if (this.reason.startsWith(": ") || this.reason.startsWith(" :")) {
			return this.reason.substring(2);
		} else {
			return this.reason;
		}
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.infozilla.model.Inlineable#getStartPosition()
	 */
	@Override
	public int getStartPosition() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * Gets the trace end.
	 *
	 * @return the trace end
	 */
	public int getTraceEnd() {
		return this.traceEnd;
	}
	
	/**
	 * Gets the trace start.
	 *
	 * @return the trace start
	 */
	public int getTraceStart() {
		return this.traceStart;
	}
	
	/**
	 * Check whether this Stack Trace is a cause or not.
	 *
	 * @return A boolean value indicating if the Stack Trace is a cause.
	 */
	public boolean isCause() {
		return this.isCause;
	}
	
	/**
	 * Set the Stack Trace to be a cause or not.
	 *
	 * @param isCause A boolean value if this stack trace is a cause
	 */
	public void setCause(final boolean isCause) {
		this.isCause = isCause;
	}
	
	/**
	 * Sets the trace end.
	 *
	 * @param traceEnd the new trace end
	 */
	public void setTraceEnd(final int traceEnd) {
		this.traceEnd = traceEnd;
	}
	
	/**
	 * Sets the trace start.
	 *
	 * @param traceStart the new trace start
	 */
	public void setTraceStart(final int traceStart) {
		this.traceStart = traceStart;
	}
	
}
