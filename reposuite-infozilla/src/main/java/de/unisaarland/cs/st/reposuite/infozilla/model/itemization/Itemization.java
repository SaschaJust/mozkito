package de.unisaarland.cs.st.reposuite.infozilla.model.itemization;

import java.util.ArrayList;
import java.util.List;

/**
 * This class encapsulates an Itemization as created by FilterEnumerations filter.
 * @author Nicolas Bettenburg
 *
 */
public class Itemization {
	
	private List<String> enumeration_items;
	private int          start;
	private int          end;
	private int          enumStart;
	private int          enumEnd;
	
	public int getEnumStart() {
		return enumStart;
	}
	
	public void setEnumStart(int enumStart) {
		this.enumStart = enumStart;
	}
	
	public int getEnumEnd() {
		return enumEnd;
	}
	
	public void setEnumEnd(int enumEnd) {
		this.enumEnd = enumEnd;
	}
	
	public Itemization(List<String> items, int start, int end) {
		this.start = start;
		this.end = end;
		this.enumeration_items = items;
	}
	
	public Itemization() {
		this.enumeration_items = new ArrayList<String>();
	}
	
	public int getStart() {
		return start;
	}
	
	public void setStart(int start) {
		this.start = start;
	}
	
	public int getEnd() {
		return end;
	}
	
	public void setEnd(int end) {
		this.end = end;
	}
	
	public List<String> getEnumeration_items() {
		return enumeration_items;
	}
	
	public void setEnumeration_items(List<String> enumeration_items) {
		this.enumeration_items = enumeration_items;
	}
	
}
