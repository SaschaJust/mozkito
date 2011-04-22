package de.unisaarland.cs.st.reposuite.infozilla.model.stacktrace;

import java.util.HashMap;
import java.util.Map;

/**
 * Class representing a Mozilla Talkback Entry
 */
public class TalkBackStacktrace extends Stacktrace {
	
	private String              incident_id;
	private Map<String, String> fields = null;
	private TalkbackTrace       trace  = null;
	
	public TalkBackStacktrace(final String incident_id) {
		this.incident_id = incident_id;
		this.fields = new HashMap<String, String>();
		this.trace = new TalkbackTrace();
	}
	
	public TalkBackStacktrace(final String incident_id, final Map<String, String> fields, final TalkbackTrace trace) {
		super();
		this.incident_id = incident_id;
		this.fields = fields;
		this.trace = trace;
	}
	
	@Override
	public int getEndPosition() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public String getField(final String name) {
		if (this.fields.containsKey(name)) {
			return this.fields.get(name);
		} else {
			return "";
		}
	}
	
	public String getIncident_id() {
		return this.incident_id;
	}
	
	@Override
	public int getStartPosition() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public TalkbackTrace getTrace() {
		return this.trace;
	}
	
	public void setField(final String name,
	                     final String value) {
		this.fields.put(name, value);
	}
	
	public void setIncident_id(final String incident_id) {
		this.incident_id = incident_id;
	}
	
	public void setTrace(final TalkbackTrace trace) {
		this.trace = trace;
	}
}
