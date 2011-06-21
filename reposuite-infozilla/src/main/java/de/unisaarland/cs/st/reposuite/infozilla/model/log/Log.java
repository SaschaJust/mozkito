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
package de.unisaarland.cs.st.reposuite.infozilla.model.log;

import java.util.ArrayList;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.infozilla.model.Attachable;
import de.unisaarland.cs.st.reposuite.infozilla.model.Inlineable;
import de.unisaarland.cs.st.reposuite.infozilla.model.attachment.Attachment;

public class Log implements Attachable, Inlineable {
	
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
	
	@Override
	public int getEndPosition() {
		// TODO Auto-generated method stub
		return 0;
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
	
	@Override
	public int getStartPosition() {
		// TODO Auto-generated method stub
		return 0;
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
