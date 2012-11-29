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
package org.mozkito.versions.elements;

import java.util.Iterator;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;

import org.mozkito.versions.Repository;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class LogIterator implements Iterator<LogEntry> {
	
	private final Iterator<LogEntry> logIterator;
	
	public LogIterator(@NotNull final Repository repository, final String startRevision, final String endRevision) {
		this.logIterator = repository.log(startRevision, endRevision).iterator();
	}
	
	@Override
	public boolean hasNext() {
		return this.logIterator.hasNext();
	}
	
	@Override
	public LogEntry next() {
		return this.logIterator.next();
	}
	
	@Override
	public void remove() {
		
	}
	
}
