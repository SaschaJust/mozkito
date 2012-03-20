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
package de.unisaarland.cs.st.moskito.infozilla.model.log;

import java.util.ArrayList;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.moskito.infozilla.model.Attachable;
import de.unisaarland.cs.st.moskito.infozilla.model.Inlineable;
import de.unisaarland.cs.st.moskito.infozilla.model.attachment.Attachment;

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
	 * @param end
	 *            the end to set
	 */
	public void setEnd(final DateTime end) {
		this.end = end;
	}
	
	/**
	 * @param entities
	 *            the entities to set
	 */
	public void setEntities(final ArrayList<LogEntry> entities) {
		this.entities = entities;
	}
	
	/**
	 * @param start
	 *            the start to set
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
