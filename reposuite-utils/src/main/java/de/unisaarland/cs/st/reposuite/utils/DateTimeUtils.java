/**
 * 
 */
package de.unisaarland.cs.st.reposuite.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class DateTimeUtils {
	
	private static final Regex               timestampRegexZone = new Regex(
	"\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2} ([:alpha:]{3,4})");
	private static final Regex               timestampRegexLong = new Regex("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
	
	/**
	 * from {@linkplain http
	 * ://en.wikipedia.org/wiki/List_of_time_zone_abbreviations}
	 */
	private static final Map<String, String> timezones = new HashMap<String, String>() {
		
		private static final long serialVersionUID = -6173432592405068889L;
		
		{
			put("ACDT", "+1030");
			put("ACST", "+0930");
			put("ACT", "+0800");
			put("ADT", "-0300");
			put("AEDT", "+1100");
			put("AEST", "+1000");
			put("AFT", "+0430");
			put("AKDT", "-0800");
			put("AKST", "-0900");
			put("AMST", "+0500");
			put("AMT", "+0400");
			put("ART", "-0300");
			put("AST", "+0300");
			put("AST", "+0400");
			put("AST", "+0300");
			put("AST", "-0400");
			put("AWDT", "+0900");
			put("AWST", "+0800");
			put("AZOST", "-0100");
			put("AZT", "+0400");
			put("BDT", "+0800");
			put("BIOT", "+0600");
			put("BIT", "-1200");
			put("BOT", "-0400");
			put("BRT", "-0300");
			put("BST", "+0600");
			put("BST", "+0100");
			put("BTT", "+0600");
			put("CAT", "+0200");
			put("CCT", "+0630");
			put("CDT", "-0500");
			put("CEDT", "+0200");
			put("CEST", "+0200");
			put("CET", "+0100");
			put("CHAST", "+1245");
			put("CIST", "-0800");
			put("CKT", "-1000");
			put("CLST", "-0300");
			put("CLT", "-0400");
			put("COST", "-0400");
			put("COT", "-0500");
			put("CST", "-0600");
			put("CST", "+0800");
			put("CVT", "-0100");
			put("CXT", "+0700");
			put("ChST", "+1000");
			put("DFT", "+0100");
			put("EAST", "-0600");
			put("EAT", "+0300");
			put("ECT", "-0400");
			put("ECT", "-0500");
			put("EDT", "-0400");
			put("EEDT", "+0300");
			put("EEST", "+0300");
			put("EET", "+0200");
			put("EST", "-0500");
			put("FJT", "+1200");
			put("FKST", "-0300");
			put("FKT", "-0400");
			put("GALT", "-0600");
			put("GET", "+0400");
			put("GFT", "-0300");
			put("GILT", "+1200");
			put("GIT", "-0900");
			put("GMT", "+0000");
			put("GST", "-0200");
			put("GYT", "-0400");
			put("HADT", "-0900");
			put("HAST", "-1000");
			put("HKT", "+0800");
			put("HMT", "+0500");
			put("HST", "-1000");
			put("IRKT", "+0800");
			put("IRST", "+0330");
			put("IST", "+0530");
			put("IST", "+0100");
			put("IST", "+0200");
			put("JST", "+0900");
			put("KRAT", "+0700");
			put("KST", "+0900");
			put("LHST", "+1030");
			put("LINT", "+1400");
			put("MAGT", "+1100");
			put("MDT", "-0600");
			put("MIT", "-0930");
			put("MSD", "+0400");
			put("MSK", "+0300");
			put("MST", "+0800");
			put("MST", "-0700");
			put("MST", "+0630");
			put("MUT", "+0400");
			put("NDT", "-0230");
			put("NFT", "+1130");
			put("NPT", "+0545");
			put("NST", "-0330");
			put("NT", "-0330");
			put("OMST", "+0600");
			put("PDT", "-0700");
			put("PETT", "+1200");
			put("PHOT", "+1300");
			put("PKT", "+0500");
			put("PST", "-0800");
			put("PST", "+0800");
			put("RET", "+0400");
			put("SAMT", "+0400");
			put("SAST", "+0200");
			put("SBT", "+1100");
			put("SCT", "+0400");
			put("SLT", "+0530");
			put("SST", "-1100");
			put("SST", "+0800");
			put("TAHT", "-1000");
			put("THA", "+0700");
			put("UTC", "+0000");
			put("UYST", "-0200");
			put("UYT", "-0300");
			put("VET", "-0430");
			put("VLAT", "+1000");
			put("WAT", "+0100");
			put("WEDT", "+0100");
			put("WEST", "+0100");
			put("WET", "+0000");
			put("YAKT", "+0900");
			put("YEKT", "+0500");
		}
	};
	
	/**
	 * Parses the date in form `yyyy-MM-dd HH:mm:ss Z` where Z can also be
	 * represented as non-offset
	 * 
	 * @param s
	 *            the s
	 * @return the date time
	 */
	public static DateTime parseDate(final String s) {
		List<RegexGroup> find = timestampRegexZone.find(s);
		DateTime d;
		
		if (find != null) {
			// with time zone abbreviation
			String offset = DateTimeUtils.timeZoneAbbreviationToUTCOffset(find.get(1).getMatch());
			String timestampWithoutZone = s.substring(0, s.length() - find.get(1).getMatch().length());
			System.out.println("Found timezone: " + find.get(1).getMatch() + " mapped to " + offset);
			DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss z");
			d = dtf.parseDateTime(timestampWithoutZone + offset);
		} else if ((find = timestampRegexLong.find(s)) != null) {
			// without
			DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
			d = dtf.parseDateTime(s);
		} else {
			DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
			d = dtf.parseDateTime(s);
		}
		
		if (d == null) {
			
			if (Logger.logError()) {
				Logger.error("Could not parse date: " + s);
			}
			System.exit(1);
		}
		return d;
	}
	
	public static final String timeZoneAbbreviationToUTCOffset(final String timezone) {
		Condition.notNull(timezone);
		Condition.greater(timezone.length(), 2);
		
		return timezones.get(timezone);
	}
}
