/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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

package de.unisaarland.cs.st.moskito.collections;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kisa.Logger;

public class DataFrame<T> {
	
	private final List<String>     colnames = new LinkedList<String>();
	
	private final Map<String, T[]> rows     = new HashMap<String, T[]>();
	
	public DataFrame(final Collection<String> colnames) {
		colnames.addAll(colnames);
	}
	
	public boolean addRow(final String rowname,
	                      final T[] values) {
		if (values.length != this.colnames.size()) {
			if (Logger.logError()) {
				Logger.error("Cannot add row into DataFrame! Number of expected values is " + this.colnames.size()
				        + " but was " + values.length);
			}
			return false;
		}
		if (containsRow(rowname)) {
			if (Logger.logError()) {
				Logger.error("Cannot add row with same name more than once: " + rowname);
			}
			return false;
		}
		
		this.rows.put(rowname, values);
		return true;
	}
	
	public boolean containsRow(final String rowname) {
		return this.rows.containsKey(rowname);
	}
	
	public Collection<String> getColnames() {
		return this.colnames;
	}
	
	public Collection<String> getRownames() {
		return this.rows.keySet();
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("rownames");
		for (final String colname : this.colnames) {
			sb.append(",");
			sb.append(colname);
		}
		sb.append(FileUtils.lineSeparator);
		
		for (final String rowname : getRownames()) {
			sb.append(rowname);
			final T[] row = this.rows.get(rowname);
			for (int i = 0; i < row.length; ++i) {
				sb.append(",");
				sb.append(row[i]);
			}
			sb.append(FileUtils.lineSeparator);
		}
		
		return sb.toString();
	}
	
}
