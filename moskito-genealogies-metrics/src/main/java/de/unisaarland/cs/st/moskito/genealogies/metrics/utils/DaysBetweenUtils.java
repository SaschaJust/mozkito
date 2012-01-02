package de.unisaarland.cs.st.moskito.genealogies.metrics.utils;

import java.util.Collection;

import org.joda.time.DateTime;
import org.joda.time.Days;

import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;


public class DaysBetweenUtils {
	
	public static int getDaysBetween(Collection<JavaChangeOperation> p1, Collection<JavaChangeOperation> p2) {
		DateTime p1EarlyTime = null;
		DateTime p1LateTime = null;
		
		DateTime p2EarlyTime = null;
		DateTime p2LateTime = null;
		
		for (JavaChangeOperation p1Op : p1) {
			DateTime tmpTime = p1Op.getRevision().getTransaction().getTimestamp();
			if (p1EarlyTime == null) {
				p1EarlyTime = tmpTime;
			} else if (tmpTime.isBefore(p1EarlyTime)) {
				p1EarlyTime = tmpTime;
			}
			if (p1LateTime == null) {
				p1LateTime = tmpTime;
			} else if (tmpTime.isAfter(p1LateTime)) {
				p1LateTime = tmpTime;
			}
		}
		
		for (JavaChangeOperation p2Op : p2) {
			DateTime tmpTime = p2Op.getRevision().getTransaction().getTimestamp();
			if (p2EarlyTime == null) {
				p2EarlyTime = tmpTime;
			} else if (tmpTime.isBefore(p2EarlyTime)) {
				p2EarlyTime = tmpTime;
			}
			if (p2LateTime == null) {
				p2LateTime = tmpTime;
			} else if (tmpTime.isAfter(p2LateTime)) {
				p2LateTime = tmpTime;
			}
		}
		
		int diff = Days.daysBetween(p1LateTime, p2EarlyTime).getDays();
		int diff2 = Days.daysBetween(p2LateTime, p1EarlyTime).getDays();
		return Math.min(diff, diff2);
		
	}
	
	public static int getDaysBetween(JavaChangeOperation op1, JavaChangeOperation op2) {
		return Days.daysBetween(op1.getRevision().getTransaction().getTimestamp(),
				op2.getRevision().getTransaction().getTimestamp()).getDays();
	}
	
	public static int getDaysBetween(RCSTransaction t1, RCSTransaction t2) {
		return Days.daysBetween(t1.getTimestamp(), t2.getTimestamp()).getDays();
	}
	
}
