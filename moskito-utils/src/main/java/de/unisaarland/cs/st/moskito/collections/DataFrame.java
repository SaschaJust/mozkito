package de.unisaarland.cs.st.moskito.collections;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kisa.Logger;


public class DataFrame<T> {
	
	private List<String> colnames = new LinkedList<String>();
	
	private Map<String, T[]> rows     = new HashMap<String, T[]>();
	
	public DataFrame(Collection<String> colnames) {
		colnames.addAll(colnames);
	}
	
	public boolean addRow(String rowname, T[] values) {
		if (values.length != colnames.size()) {
			if (Logger.logError()) {
				Logger.error("Cannot add row into DataFrame! Number of expected values is " + colnames.size()
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
	
	public boolean containsRow(String rowname){
		return rows.containsKey(rowname);
	}
	
	public Collection<String> getColnames() {
		return this.colnames;
	}
	
	public Collection<String> getRownames() {
		return this.rows.keySet();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("rownames");
		for (String colname : colnames) {
			sb.append(",");
			sb.append(colname);
		}
		sb.append(FileUtils.lineSeparator);
		
		for (String rowname : getRownames()) {
			sb.append(rowname);
			T[] row = rows.get(rowname);
			for (int i = 0; i < row.length; ++i) {
				sb.append(",");
				sb.append(row[i]);
			}
			sb.append(FileUtils.lineSeparator);
		}
		
		return sb.toString();
	}
	
}
