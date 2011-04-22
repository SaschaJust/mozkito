/**
 * 
 */
package de.unisaarland.cs.st.reposuite.infozilla.model.stacktrace;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class JavaStacktrace extends Stacktrace {
	
	private final String       exception;
	private final String       reason;
	private final List<String> frames;
	private boolean            isCause;
	private int                traceStart;
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
	 * Overloaded Constructor
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
	 * Overloaded Constructor
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
	
	@Override
	public int getEndPosition() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * Getter for the Exception
	 * @return the exception this Stack Trace originated from
	 */
	public String getException() {
		return this.exception;
	}
	
	/**
	 * Getter for the Trace Locations
	 * @return A list of locations where the trace originated from.
	 */
	public List<String> getFrames() {
		return (this.frames);
	}
	
	/**
	 * Joins the text of all Frames in frames
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
	 * Getter for the Reason
	 * @return the reason for this Stack Trace stated in the first Exception. (removed trailing :)
	 */
	public String getReason() {
		if (this.reason.startsWith(": ") || this.reason.startsWith(" :")) {
			return this.reason.substring(2);
		} else {
			return this.reason;
		}
	}
	
	@Override
	public int getStartPosition() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public int getTraceEnd() {
		return this.traceEnd;
	}
	
	public int getTraceStart() {
		return this.traceStart;
	}
	
	/**
	 * Check whether this Stack Trace is a cause or not
	 * @return A boolean value indicating if the Stack Trace is a cause.
	 */
	public boolean isCause() {
		return this.isCause;
	}
	
	/**
	 * Set the Stack Trace to be a cause or not
	 * @param isCause A boolean value if this stack trace is a cause
	 */
	public void setCause(final boolean isCause) {
		this.isCause = isCause;
	}
	
	public void setTraceEnd(final int traceEnd) {
		this.traceEnd = traceEnd;
	}
	
	public void setTraceStart(final int traceStart) {
		this.traceStart = traceStart;
	}
	
}
