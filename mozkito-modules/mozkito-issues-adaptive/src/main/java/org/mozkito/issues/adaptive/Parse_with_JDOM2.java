package org.mozkito.issues.adaptive;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathConstants;

import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderSAX2Factory;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPath;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.jdom2.xpath.XPathHelper;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * @author Eric Gliemmo
 *
 */
public class Parse_with_JDOM2 {
	
	
	public static void main(String[] args) throws Exception{
		
		final SAXBuilder saxBuilder = new SAXBuilder(new XMLReaderSAX2Factory(false,
		                                                                      "org.ccil.cowan.tagsoup.Parser"));
		//final Document document = saxBuilder.build(new URL("https://bugzilla.mozilla.org/show_bug.cgi?id=828871"));
		//final Document document = saxBuilder.build(new URL("http://feeds.bbci.co.uk/news/technology/rss.xml?edition=int"));
		// final Document document = saxBuilder.build(new URL("https://issues.mozkito.org/browse/MOZKITO-113"));
																//baut das JDOM2 Document aus der URL, also das XML Dokument
		final Document document = saxBuilder.build(new URL("https://jira.codehaus.org/browse/XSTR-752"));
		
		XMLOutputter out = new XMLOutputter();
		out.output( document, System.out );						// gibt die XML aus
		
		//String StringXML = document.toString();
		//StringXML.replace("xmlns[^\"]*\"[^\"]*\"","");
		//System.out.println(StringXML);
		
		System.out.println("+++++++++++++++++++++++");
		
		int level = 1;											//Tiefe der Tags
		Element root = document.getRootElement();				//nimmt sich das Rootelement
		System.out.println(root);
		
		
		ArrayList<String> MarkerList_text = new ArrayList<String>();						//Gibt den Inhalt des Tags mit dem Marker aus
		ArrayList<String> Predecessors = new ArrayList<String>();							//Liste aller Tags die abgesucht werden
		ArrayList<String> finalPredecessors = new ArrayList<String>();						//Liste der Tags die Vorgänger des gesuchten sind
		searchForMarkers(root, level, MarkerList_text, Predecessors, finalPredecessors);	//ruft die Methode zum suchen der Marker auf
		System.out.println(MarkerList_text);
		System.out.println(finalPredecessors);
		
		System.out.println("+++++++++++++++++++++++");
		
		//xpath1b(document);			//mit dieser Methode versuche ich per XPath daten aus meinem geparsten Document zu lesen
		if (finalPredecessors.size()>0){
				String XPath = make_xpath_query(finalPredecessors);
				System.out.println(XPath);														//funktioniert nicht wirklich, nur für //*
		}
		
		
		
	}
	private static void xpath1b(Document document) throws MalformedURLException, IOException, JDOMException  {
		  XPathFactory xpathFactory = XPathFactory.instance();
		  
		  
		  
		  XPathExpression<Element> xpath = 
				   XPathFactory.instance().compile("//rss/channel/title", Filters.element());
		  System.out.println(xpath);
			List<Element> elements = xpath.evaluate(document);
		  System.out.println(elements);
			for (Element emt : elements) {
			    //System.out.println("XPath has result: " + emt.getName());
			}
		  
		  
	 
		 /* String titelTextPath = "//rss//channel//title";
		  XPathExpression<Object> expr = xpathFactory.compile(titelTextPath);
		  System.out.println(expr);
		  System.out.println("Kompliliert hab ich");
		  List<Object> buchtitels = expr.evaluate(document);
		  System.out.println("Liste erstellt");
		  System.out.println(buchtitels);
		  for (int i = 0; i < buchtitels.size(); i++)
		  {
		     Content content = (Content) buchtitels.get(i);
		     //System.out.println(content.getValue());
		  }*/
	  
	}
	
	private static void searchForMarkers (Element node, int level, ArrayList<String> MarkerList_text, ArrayList<String> Predecessors,
			ArrayList<String> finalPredecessors) {

		if (Predecessors.size() != level) {						//testet, ob die Tiefe der Suche gleich der Anzahl der 
			Predecessors.add(node.getName());					//gelisteten VorgaengerTags ist. Wenn ja, löschen wir das letzte
		} else {												//Element und fügen das neue ein, ansonsten fügen einfach nur
			Predecessors.remove(level-1);						//das neue ein
			Predecessors.add(node.getName());
		}
		
		System.out.println("LEVEL:" + level);
		
		level ++;															//erhoehen das Level um 1
		
		List<Element> Children = build_children_list(node);					//Liste der Kinder
		List<String> Children_text = build_children_list_text(node);		//Liste mit Text der Kinder
		System.out.println(Children);
		System.out.println(Children_text);
		int y = Children.size();											//Größe der Liste
		System.out.println("Anzahl der Kinder des Knoten: " + y);
		for (int x = 0; x<y; x++) {	
			System.out.println("Wir befinden uns bei:" + Children.get(x).getName());
			String string = Children_text.get(x);							//gehe durch alle Kinder und prüfe
			if (string != "hasChildren"){									// wenn das Kind keine Kinder hat
				String marker = checkString(string);						//ob wir dann im Text unseren Marker finden
				if (marker != "notcontain") {
					System.out.println("FOUND");							//wenn ja gib diesen aus und merk ihn dir 
					int tmp = Predecessors.size();
					for (int z = 0; z<tmp; z++) {
						finalPredecessors.add(Predecessors.get(z));			//übernehme die aktuelle Predecessorliste als final
					}														//und füge auch noch den jetzigen Tag dazu
					Namespace NS = Children.get(x).getNamespace();
					System.out.println("ACHTUNG!! NAMESPACE IST: " + NS);
					finalPredecessors.add(Children.get(x).getName());
					MarkerList_text.add(marker);							//schreibt den gefundenen marker in die liste
					Children.get(x).setAttribute("MyEricMy", "lastTag");
					break;
				}
				System.out.println("Keine Kinder aber nichts passendes gefunden");
			} else {												//wenn nicht dann nicht und starte mit diesem Kind die Methode neu
				System.out.println("Starte Methode neu im Tag: " + Children.get(x).getName());				
				searchForMarkers (Children.get(x), level, MarkerList_text, Predecessors, finalPredecessors);
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
	//Gibt zu einem Element die Liste der Inhalte der Kinder zurück
	private static ArrayList<String> build_children_list_text (Element parent){
		
		List<Element> childrenList = parent.getChildren();				//liste der kinder
		ArrayList<String> childrenList_text = new ArrayList<String>();
		int i = childrenList.size();
		
		
		if (!childrenList.isEmpty()) {
			for(int j = 0; j < i; j++){
				
				if (childrenList.get(j).getChildren().isEmpty()){			//prüft für jedes kind, ob es kinder hat
					childrenList_text.add(childrenList.get(j).getText());   // wenn ja, dann "haschildren"
				} else {													// anders gibt es den zugehörigen text aus
					childrenList_text.add("hasChildren");
				}
			}
		}	
			
		return childrenList_text;		
		
	}
	
	//Testet ob der Inhalt eines Tags einen Marker enthält
	private static String checkString (String string){
		
		//if (string.contains("Ed Morley")) {
		if (string.contains("Facebook")) {	
			
			return string;
		}
		if (string.contains("$$title_marker$$")) {
			
			return "title";
		} else {
			return "notcontain";
		}
		
	}
	
	//liefert zu einer finalPredecessors Liste den entsprechenden XPath_Query
	private static String make_xpath_query (ArrayList<String> finalPredecessors){
		
		int size = finalPredecessors.size();
		String Xpath_query = "//" + finalPredecessors.get(0);
		
		for(int tmp = 1; tmp < size; tmp++){
			Xpath_query = Xpath_query + "/" + finalPredecessors.get(tmp);
		}
		
		return Xpath_query;
	}
}
