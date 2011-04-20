package de.unisaarland.cs.st.reposuite.infozilla.model.log;

import java.util.ArrayList;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.infozilla.model.Attachable;
import de.unisaarland.cs.st.reposuite.infozilla.model.attachment.Attachment;

public class Log implements Attachable {
	
	DateTime            start;
	
	DateTime            end;
	
	ArrayList<LogEntry> entities;
	
	@Override
	public Attachment getAttachment() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * @return the end
	 */
	public DateTime getEnd() {
		return this.end;
	}
	
	/**
	 * @return the entities
	 */
	public ArrayList<LogEntry> getEntities() {
		return this.entities;
	}
	
	/**
	 * @return the start
	 */
	public DateTime getStart() {
		return this.start;
	}
	
	/**
	 * @return
	 */
	public String getText() {
		StringBuilder builder = new StringBuilder();
		for (LogEntry entry : getEntities()) {
			builder.append(entry.getLine());
		}
		return builder.toString();
	}
	
	/**
	 * @param end the end to set
	 */
	public void setEnd(final DateTime end) {
		this.end = end;
	}
	
	/**
	 * @param entities the entities to set
	 */
	public void setEntities(final ArrayList<LogEntry> entities) {
		this.entities = entities;
	}
	
	/**
	 * @param start the start to set
	 */
	public void setStart(final DateTime start) {
		this.start = start;
	}
	
	/**
	 * @return
	 */
	public int size() {
		return getEntities().size();
	}
}
