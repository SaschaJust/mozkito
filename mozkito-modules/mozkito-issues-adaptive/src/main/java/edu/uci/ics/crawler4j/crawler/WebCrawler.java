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
        print("https://jira.codehaus.org/browse/XSTR-752", url);

        Parse_with_JDOM2.start_parse(url);
        
        Document doc = Jsoup.connect(url).get();
        Elements links = doc.select("a[href]");
        Elements media = doc.select("[src]");
        Elements imports = doc.select("link[href]");
        
        for (Element link : links) {
            try {
            	System.out.println(link.attr("abs:href"));
				Parse_with_JDOM2.start_parse(link.attr("abs:href"));
			} catch (Exception FileNotFoundException) {
				// TODO Auto-generated catch block
				FileNotFoundException.printStackTrace();
			}
        }

        print("\nMedia: (%d)", media.size());
        for (Element src : media) {
            if (src.tagName().equals("img"))
                print(" * %s: <%s> %sx%s (%s)",
                        src.tagName(), src.attr("abs:src"), src.attr("width"), src.attr("height"),
                        trim(src.attr("alt"), 20));
            else
                print(" * %s: <%s>", src.tagName(), src.attr("abs:src"));
        }

        print("\nImports: (%d)", imports.size());
        for (Element link : imports) {
            print(" * %s <%s> (%s)", link.tagName(),link.attr("abs:href"), link.attr("rel"));
        }

        print("\nLinks: (%d)", links.size());
        for (Element link : links) {
            print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
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
