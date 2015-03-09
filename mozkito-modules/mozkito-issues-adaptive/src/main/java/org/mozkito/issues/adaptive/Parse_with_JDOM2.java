package org.mozkito.issues.adaptive;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import net.ownhero.dev.kisa.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jdom2.DocType;
import org.jdom2.Element;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderSAX2Factory;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.SAXException;


/**
 * @author Eric Gliemmo
 *
 */
public class Parse_with_JDOM2 {
	
	private DefaultHttpClient     httpClient   = null;
	private String BASIC_URL = "https://issues.mozkito.org";
	
	public static void main(String[] args) throws Exception{
		//String url = "https://bugzilla.mozilla.org/show_bug.cgi?id=828871";
	}
	
	public void test() throws ClientProtocolException, IOException, JDOMException {
		
		this.httpClient = new DefaultHttpClient();
		
	   	httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
			httpClient.setCookieStore(new BasicCookieStore());
			
			String authUrl = BASIC_URL + "/login"; 
			
			final HttpPost post = new HttpPost(authUrl);
			
			try {
				final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
				nameValuePairs.add(new BasicNameValuePair("os_username", "egliemmo"));
				nameValuePairs.add(new BasicNameValuePair("os_password", "legindametalevel"));
				nameValuePairs.add(new BasicNameValuePair("username", "egliemmo"));
				nameValuePairs.add(new BasicNameValuePair("password", "legindametalevel"));
				nameValuePairs.add(new BasicNameValuePair("os_cookie", "true"));
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				
				final HttpResponse response = this.httpClient.execute(post);
				final BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				String line = null;
				
				while ((line = rd.readLine()) != null) {
					if (Logger.logDebug()) {
						Logger.debug(line);
					}
				}
				
				final List<Cookie> cookies = this.httpClient.getCookieStore().getCookies();
				if (Logger.logInfo()) {
					Logger.info("Received %s cookies.", cookies.size());
				}
				
				if (Logger.logDebug()) {
					for (final Cookie cookie : cookies) {
						Logger.debug(cookie.toString());
					}
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
			
	   	// authenticated 
			final HttpGet request = new HttpGet(authUrl);
			final HttpResponse response = httpClient.execute(request);
			final HttpEntity entity = response.getEntity();
			
			final BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
			SAXBuilder sBuilder = new SAXBuilder(new XMLReaderSAX2Factory(false,
                    "org.ccil.cowan.tagsoup.Parser"));
			Document document = sBuilder.build(reader);
			start_searching(document);
	}
	
	public static void start_searching(Document document) throws MalformedURLException, JDOMException, IOException{
		
		
		//final SAXBuilder saxBuilder = new SAXBuilder(new XMLReaderSAX2Factory(false,
		                                                                     // "org.ccil.cowan.tagsoup.Parser"));
		//final Document document = saxBuilder.build(new URL("https://bugzilla.mozilla.org/show_bug.cgi?id=828871"));
		//final Document document = saxBuilder.build(new URL("http://feeds.bbci.co.uk/news/technology/rss.xml?edition=int"));
		// final Document document = saxBuilder.build(new URL("https://issues.mozkito.org/browse/MOZKITO-113"));
																//baut das JDOM2 Document aus der URL, also das XML Dokument
		//final Document document = saxBuilder.build(new URL("https://jira.codehaus.org/browse/XSTR-752"));
		//final Document document = saxBuilder.build(new URL(url));
		
		XMLOutputter out = new XMLOutputter();
		out.output( document, System.out );						// gibt die XML aus
		
		//bearbeite den doctype (vor allem bei bugzilla kann die public id nicht geparst werden)
		DocType doctype = document.getDocType();
		try {
			doctype.setPublicID(null);
			doctype.setSystemID(null);
		} catch (NullPointerException e) {
			System.out.println("bei diesem document existiert kein doctype!!");
		}
		
		
		String filepath = "/Users/Eric/mozkito/mozkito-modules/mozkito-issues-adaptive/gen_files/file2.xml";  
		write_genfile(document, filepath);													//speichere xml-doc lokal als file
		
		//System.out.println("+++++++++++++++++++++++");
		
		int deep = 1;
		boolean found = false;											
		Element root = document.getRootElement();
		root.setAttribute("number", String.valueOf(1));				//root-element mit atrribut 0 versehen//nimmt sich das Rootelement
		//System.out.println(root + " name: " + root.getName() + " text: " + root.getText());
		
		ArrayList<String> finalPredecessors = new ArrayList<String>();						//Liste der Tags die Vorgänger des gesuchten sind
		find_markers(root, deep, finalPredecessors, found);									//ruft die Methode zum suchen der Marker auf
		//System.out.println(finalPredecessors);
		//System.out.println("+++++++++++++++++++++++");
		
		String XPath = "";
		if (finalPredecessors.size()>0){
				XPath = make_xpath_query(finalPredecessors);
				System.out.println(XPath);														//funktioniert nicht wirklich, nur für //*
		} else {
			System.out.println("nothing found");
		}
		
		//starte die datensuche mit dem filepath und dem xpathquery
		try {
			Xpath2Data_with_JAVAX.start_xpath(filepath, XPath);
		} catch (XPathExpressionException e) {
			System.out.println("No path found, no query available");
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//sucht einen bestimmten marker und merkt sich den pfad dorthin
	private static void find_markers (Element root, int deep, ArrayList<String> finalPredecessors,boolean found){
		List<Element> Children = build_children_list(root);
		//System.out.println(Children);
		//System.out.println("LEVEL:" + deep);
		if (!Children.isEmpty()){
			int tmp = 1;
			for (Element child : Children) {
				child.setAttribute("number", String.valueOf(tmp));    //versehe jedes element mit der nummer an der es steht
				//System.out.println("Child:" + child.getName());
	            if (!hasChildren(child) && checkString(child.getText())){
	            	for(int x=0; x<deep+1; x++){
	            		finalPredecessors.add(/*child.getName() + */ child.getAttributeValue("number"));  		//gebe nummer des kindes aus
	            	    if (child.getParentElement()!=null){
	            	    	child = child.getParentElement();
	            	    }
	            	}
	            	Collections.reverse(finalPredecessors);
	            	found=true;
	            }
	            if(found==false){
	            	find_markers(child,deep+1,finalPredecessors,found);
	            }
	            tmp++;
	        }
		}
	}
	//Gibt zu einem Element die Liste der Kinder zurück
	private static List<Element> build_children_list (Element parent){
		
		List<Element> childrenList = parent.getChildren();     //baut die Liste der Kinder zusammen
		
		if (childrenList == null) {
			return null;
		} else {
			return childrenList;							// falls nicht null gibt diese aus
		}
		
		
	}
	//testet ob ein element kinder hat
	private static boolean hasChildren (Element parent){
		
		List<Element> childrenList = parent.getChildren();				//liste der kinder
		
//		if (!childrenList.isEmpty()) {
//			for (Element child : childrenList) {
//				if (child.getName() == "br"){
//					childrenList.remove(child);
//				}
//				
//	        }
//		}
		if (!childrenList.isEmpty()) {
			return true;
		}	
		return false;		
	}
	
	//Testet ob der Inhalt eines Tags einen Marker enthält
	private static boolean checkString (String string){
		
		if (string.contains("title_marker")) {	
			
			return true;
		}
		if (string.contains("$$title_marker$$")) {
			
			return true;
		} else {
			return false;
		}
	}
	
	//liefert zu einer finalPredecessors Liste den entsprechenden XPath_Query
	private static String make_xpath_query (ArrayList<String> finalPredecessors){
		
		int size = finalPredecessors.size();
		String Xpath_query = "//ns:*[" + finalPredecessors.get(0) + "]";
		
		for(int tmp = 1; tmp < size; tmp++){
			Xpath_query = Xpath_query + "/ns:*[" + finalPredecessors.get(tmp) + "]";
		}
		Xpath_query = Xpath_query + "/text()";
		
		return Xpath_query;
	}
	
	//liefert zu einer finalPredecessors Liste den entsprechenden XPath_Query
	private static void write_genfile (Document doc, String string){
		
		XMLOutputter xmlOutput = new XMLOutputter();  
		  
//		try {
//			xmlOutput.output(doc, System.out);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}  
		  
		xmlOutput.setFormat(Format.getPrettyFormat());  
		try {
			xmlOutput.output(doc, new FileWriter(string));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
