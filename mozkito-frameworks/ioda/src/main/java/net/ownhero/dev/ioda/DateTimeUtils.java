/**
 * 
 */
package net.ownhero.dev.ioda;

import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.compare.GreaterInt;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.Regex;
import net.ownhero.dev.regex.RegexGroup;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class DateTimeUtils {
	
	private static final Regex               timestampRegex = new Regex(
	                                                                    "({yyyy}\\d{4})[-:/_]({MM}[0-2]\\d)[-:/_]({dd}[0-3]\\d)\\s+({HH}[0-2]\\d)[-:/_]({mm}[0-5]\\d)([-:/_]({ss}[0-5]\\d))?(({z}\\s+[A-Za-z]{3,4})|({Z}\\s+[+-]\\d{4}))?");
	
	static {
		DateTimeZone.setDefault(DateTimeZone.UTC);
	}
	
	/**
	 * @see <a href=http://en.wikipedia.org/wiki/List_of_time_zone_abbreviations> Wikipedia:List of time zone
	 *      abbreviations</a>
	 */
	private static final Map<String, String> timezones      = new HashMap<String, String>() {
		                                                        
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
	
	public static DateTime parseDate(final String dateTimeString) {
		return parseDate(dateTimeString, timestampRegex);
	}
	
	/**
	 * Parses the date in form `yyyy-MM-dd HH:mm:ss Z` where Z can also be represented as non-offset. Z and :ss do not
	 * have to be present.
	 * 
	 * @param dateTimeString
	 *            the datetime string, not null
	 * @return the date time
	 */
	@NoneNull
	public static DateTime parseDate(final String dateTimeString,
	                                 final Regex pattern) {
		// Condition.greaterOrEqual(dateTimeString.length(),
		// "yyyy-MM-dd HH:mm".length());
		
		Match find;
		DateTime d = null;
		
		if ((find = pattern.find(dateTimeString)) != null) {
			// with time zone abbreviation
			if (Logger.logTrace()) {
				Logger.trace("Parsing date `" + find.get(0).getMatch() + "` with: " + find);
			}
			final StringBuilder patternBuilder = new StringBuilder();
			final StringBuilder dateBuilder = new StringBuilder();
			for (final RegexGroup group : find) {
				if (!group.getName().equals("") && (group.getMatch() != null)) {
					if (group.getName().equals("z")) {
						final String offset = DateTimeUtils.timeZoneAbbreviationToUTCOffset(group.getMatch().trim());
						patternBuilder.append("Z");
						dateBuilder.append(offset);
					} else {
						patternBuilder.append(group.getName());
						dateBuilder.append(group.getMatch().trim());
					}
					patternBuilder.append(" ");
					dateBuilder.append(" ");
				}
			}
			
			if (Logger.logTrace()) {
				Logger.trace("GOT: " + dateTimeString + ", formatted to: " + dateBuilder.toString() + ", pattern: "
				        + patternBuilder.toString());
			}
			
			final DateTimeFormatter dtf = DateTimeFormat.forPattern(patternBuilder.toString());
			d = dtf.parseDateTime(dateBuilder.toString());
		}
		
		if (d == null) {
			if (Logger.logError()) {
				Logger.error("Could not parse date: " + dateTimeString);
			}
		}
		return d;
	}
	
	/**
	 * Takes a time zone abbreviation and returns the corresponding offset relative to UTC
	 * 
	 * @param timezone
	 *            the abbreviation
	 * @return the offset to UTC in +/-#### format
	 */
	public static final String timeZoneAbbreviationToUTCOffset(@NotNull @GreaterInt (ref = 2) final String timezone) {
		
		return timezones.get(timezone);
	}
}
