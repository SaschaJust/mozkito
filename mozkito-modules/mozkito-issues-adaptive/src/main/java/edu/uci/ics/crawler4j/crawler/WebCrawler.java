package edu.uci.ics.crawler4j.crawler;

import org.jdom2.JDOMException;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mozkito.issues.adaptive.Parse_with_JDOM2;

import java.io.IOException;

/**
 * Example program to list links from a URL.
 */
public class WebCrawler {
    public static void main(String[] args) throws IOException, JDOMException {
    	String url = "https://jira.codehaus.org/browse/XSTR-752";
    	Validate.isTrue(true, url);
        //print("https://jira.codehaus.org/browse/XSTR-752", url);

        Parse_with_JDOM2.start_parse(url);
        
        Document doc = Jsoup.connect(url).get();
        Elements links = doc.select("a[href]");
        int tmp = 1;
        
        for (Element link : links) {
            try {
            	//System.out.println(link.attr("abs:href"));
            	System.out.println("Link Nummer" + tmp);
				Parse_with_JDOM2.start_parse(link.attr("abs:href"));
			} catch (Exception FileNotFoundException) {
				System.out.println("Mit diesem Link ist es nicht moeglich das entsprechende xml zu parsen!!");
			}
            tmp++;
        }

        print("\nLinks: (%d)", links.size());
        int tmp2 = 1;
        for (Element link : links) {
            print(tmp2 + " * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
            tmp2++;
        }
    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }
}
