package de.unisaarland.cs.st.reposuite.infozilla.Elements.StackTraces.Talkback;

import java.util.HashMap;
import java.util.Map;

/**
 * Class representing a Mozilla Talkback Entry
 */
public class TalkBack {
	
	private String              incident_id;
	private Map<String, String> fields = null;
	private TalkbackTrace       trace  = null;
	
	public TalkBack(String incident_id) {
		this.incident_id = incident_id;
		this.fields = new HashMap<String, String>();
		this.trace = new TalkbackTrace();
	}
	
	public TalkBack(String incident_id, Map<String, String> fields, TalkbackTrace trace) {
		super();
		this.incident_id = incident_id;
		this.fields = fields;
		this.trace = trace;
	}
	
	public void setField(String name,
	                     String value) {
		this.fields.put(name, value);
	}
	
	public String getField(String name) {
		if (this.fields.containsKey(name)) {
			return fields.get(name);
		} else {
			return "";
		}
	}
	
	public String getIncident_id() {
		return incident_id;
	}
	
	public void setIncident_id(String incident_id) {
		this.incident_id = incident_id;
	}
	
	public TalkbackTrace getTrace() {
		return trace;
	}
	
	public void setTrace(TalkbackTrace trace) {
		this.trace = trace;
	}
}
