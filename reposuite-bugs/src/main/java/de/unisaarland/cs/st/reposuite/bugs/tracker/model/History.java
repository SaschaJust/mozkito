/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.model;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.utils.Tuple;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class History extends TreeSet<HistoryElement> implements Annotated {
	
	private static final long serialVersionUID = 2040085335352389362L;
	
	public History after(final DateTime dateTime) {
		History history = new History();
		Iterator<HistoryElement> iterator = iterator();
		while (iterator.hasNext()) {
			HistoryElement element = iterator.next();
			if (element.getTimestamp().isAfter(dateTime)) {
				history.add(element);
			}
		}
		return history;
	}
	
	public History before(final DateTime dateTime) {
		History history = new History();
		Iterator<HistoryElement> iterator = iterator();
		while (iterator.hasNext()) {
			HistoryElement element = iterator.next();
			if (element.getTimestamp().isBefore(dateTime)) {
				history.add(element);
			}
		}
		return history;
	}
	
	public History get(final DateTime from, final DateTime to) {
		History history = new History();
		Iterator<HistoryElement> iterator = iterator();
		while (iterator.hasNext()) {
			HistoryElement element = iterator.next();
			if ((element.getTimestamp().compareTo(from) >= 0) && (element.getTimestamp().compareTo(to) <= 0)) {
				history.add(element);
			}
		}
		return history;
	}
	
	public History get(final Field field) {
		History history = new History();
		Iterator<HistoryElement> iterator = iterator();
		while (iterator.hasNext()) {
			HistoryElement element = iterator.next();
			Map<Field, Tuple<Object, Object>> changedValues = element.getChangedValues();
			if (changedValues.keySet().contains(field)) {
				history.add(new HistoryElement(element.getAuthor(), element.getBugReport(), field, changedValues.get(
				        field).getFirst(), changedValues.get(field).getSecond(), element.getTimestamp()));
			}
		}
		return history;
	}
	
	public History get(final long bugId) {
		History history = new History();
		Iterator<HistoryElement> iterator = iterator();
		while (iterator.hasNext()) {
			HistoryElement element = iterator.next();
			if (element.getBugReport().getId() == bugId) {
				history.add(element);
			}
		}
		return history;
	}
	
	public History get(final Person author) {
		History history = new History();
		Iterator<HistoryElement> iterator = iterator();
		while (iterator.hasNext()) {
			HistoryElement element = iterator.next();
			if (element.getAuthor().equals(author)) {
				history.add(element);
			}
		}
		return history;
	}
	
	@Override
	public Collection<Annotated> getSaveFirst() {
		return null;
	}
}
