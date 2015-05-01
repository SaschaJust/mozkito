package org.mozkito.issues.adaptive;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderSAX2Factory;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class parse_test {
	
	final static Lock lock = new ReentrantLock();

	
	public static void main(String[] args) throws Exception{

		final SAXBuilder saxBuilder = new SAXBuilder(new XMLReaderSAX2Factory(false,"org.ccil.cowan.tagsoup.Parser"));
		//final Document document = saxBuilder.build(new URL("https://bugzilla.mozilla.org/show_bug.cgi?id=828871"));
		//final Document document = saxBuilder.build(new URL("http://feeds.bbci.co.uk/news/technology/rss.xml?edition=int"));
		// final Document document = saxBuilder.build(new URL("https://issues.mozkito.org/browse/MOZKITO-113"));
		//baut das JDOM2 Document aus der URL, also das XML Dokument
		final Document document = saxBuilder.build(new URL("https://jira.codehaus.org/browse/XSTR-734"));
		//final Document document = saxBuilder.build(new URL(url));
		
		String tofind = "14/Jan/14 3:57 AM";
		
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
		
		ArrayList<String> XPath = initSearch(document, tofind);
		
		//System.out.println("+++++++++++++++++++++++");
		
		
		//starte die datensuche mit dem filepath und dem xpathquery
		NodeList nodes = null;
		ArrayList<String> result = new ArrayList<String>();
		try {
			result = Xpath2Data_with_JAVAX.start_xpath(filepath, XPath, nodes);
		} catch (XPathExpressionException e) {
			System.out.println("No path found, no query available");
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			System.out.println(result);
	}
		//sucht einen bestimmten marker und merkt sich den pfad dorthin
	public static void find_markers (Element root, int deep, ArrayList<String> finalPredecessors, ArrayList<String> finalAttributes, boolean found, String tofind){
		
		
		List<Element> Children = build_children_list(root);
		
		//System.out.println(Children);
		//System.out.println("LEVEL:" + deep);
		if (!Children.isEmpty()){
		int tmp = 1;
		
		
//		Attribute att = null;
//		for (Element child : Children) {
//			att = child.getAttribute("language");
//			if (att != null){
//				tmp --;
//			} else {
//		child.setAttribute("number", String.valueOf(tmp)); 
//			}
//			
			
			for (Element child : Children) {
				//System.out.println(child.getNamespacePrefix() + tmp);
				if (child.getNamespacePrefix().contains("atom")){
					tmp--;
				} else {
					child.setAttribute("number", String.valueOf(tmp)); 
				}
			//versehe jedes element mit der nummer an der es steht
			//System.out.println("Child:" + child.getName());
				if ((checkString(child.getText(), tofind)||checkString(child.toString(), tofind))){
					//clearList(finalPredecessors);																//leert die Listen, die dann 
					//clearList(finalAttributes);																	//quasi ueberschrieben werden
//					System.out.println("Child:" + child.getName());
//					System.out.println("Child:" + child.getValue());
//					System.out.println("Child:" + child.getAttribute("id"));
//					
					lock.lock();
					finalPredecessors.add("$$break$$");
					finalAttributes.add("$$break$$");
					for(int x=0; x<deep+1; x++){
						finalPredecessors.add(child.getAttributeValue("number"));  		//gebe nummer des kindes aus
						finalAttributes.add(child.getAttributeValue("id"));										//schreibt das id attribut des knotens
						if (child.getParentElement()!=null){
							child = child.getParentElement();
						}
					}
					lock.unlock();
					found=true;
					//System.out.println(finalPredecessors);
					//System.out.println(finalAttributes);
				}
			
				if(found==false){
					find_markers(child,deep+1,finalPredecessors,finalAttributes,found, tofind);
				}
				tmp++;
			
				if (found == true){
					break;
				}
			}
		}
	}
		
	public static ArrayList<String> initSearch(Document document, String tofind){
			
			
			int deep = 1;
			boolean found = false;											
			Element root = document.getRootElement();
			root.setAttribute("number", String.valueOf(1));				//root-element mit atrribut 0 versehen//nimmt sich das Rootelement
			//System.out.println(root + " name: " + root.getName() + " text: " + root.getText());
			
			ArrayList<String> finalPredecessors = new ArrayList<String>();						//Liste der Tags die Vorgänger des gesuchten sind
			ArrayList<String> finalAttributes = new ArrayList<String>();						//liste der attribute der tags für xpath
			find_markers(root, deep, finalPredecessors, finalAttributes, found, tofind);//ruft die Methode zum suchen der Marker auf
			Collections.reverse(finalPredecessors);
			Collections.reverse(finalAttributes);
			//System.out.println(finalPredecessors);
			//System.out.println("+++++++++++++++++++++++");
			
			ArrayList<String> XPath = new ArrayList<String>();
			XPath = null;
			if (finalPredecessors.size()>0){
				//System.out.println(finalPredecessors);
				//System.out.println(finalAttributes);
				XPath = make_xpath_query(finalPredecessors, finalAttributes);
//				int XPath_size = XPath.size();
//				for (int j=0; j<XPath_size; j++){
//					System.out.println(XPath.get(j));
//				}
			} else {
			//System.out.println("nothing found");
			}
			return XPath;
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
//		private static boolean hasChildren (Element parent){
//		
//		List<Element> childrenList = parent.getChildren();				//liste der kinder
//		//hier werden die linebreaks rausgefiltert, damit der text gefunen werden kann
//		if (!childrenList.isEmpty()) {
//			for (Element child : childrenList) {
//				if (child.getName() == "br") {
//					childrenList.remove(child);
//				} 
//			}	
//		}	
//		//if (!childrenList.isEmpty()) {
//		//for (Element child : childrenList) {
//		//if (child.getName() == "br"){
//		//childrenList.remove(child);
//		//}
//		//
//		//}
//		//}
//		if (!childrenList.isEmpty()) {
//		return true;
//		}	
//		return false;		
//		}
		
		//Testet ob der Inhalt eines Tags einen Marker enthält
	private static boolean checkString (String string, String tofind){
		
		if (string.contains("title_marker")) {	
			return true;
		}
		if (string.contains(tofind)) {
			return true;
		} else {
			return false;
		}
	}
		
		//liefert zu einer finalPredecessors Liste und der finalAttributes liste den entsprechenden XPath_Query
	static ArrayList<String> make_xpath_query (ArrayList<String> finalPredecessors, ArrayList<String> finalAttributes){
		
		int size = finalPredecessors.size();
		//System.out.println(size);
		int tmp = 0;									//zählt die $$break$$ in dem finalPredecessors array
		int array1index = 0;
		int array1index2 = 0;
		int array2index = 0;
		int array2index2 = 0;
			
		for (int i=0; i < size; i++){
			if(finalPredecessors.get(i)== "$$break$$"){
				tmp++;
			}
		}
		//System.out.println(tmp);
		int[] intarray = new int[tmp];
		
		String[][] array1 = new String[tmp][size];					//legt einen string array an, bei dem in jedes feld ein pfad eingetragen wird
		String[][] array2 = new String[tmp][size];					//legt ein string array an, bei dem in jedes feld die attributes eingetragen werden
		
		for (int i=0; i < size; i++){
			if(finalPredecessors.get(i)== "$$break$$"){			//hier werden die finalpredecessors gesplittet anhand von $$break$$
				intarray[array1index] = array1index2;			//müsste angeben, wie lang der i-te pfad ist
				array1index++;									//und in das string array jeweils eingetragen
				array1index2 = 0;
			} else {
				array1[array1index][array1index2] = finalPredecessors.get(i);
				array1index2++;
			}
		}
		
		for (int i=0; i < size; i++){
			if(finalAttributes.get(i)== "$$break$$"){			//hier werden die finalattributes gesplittet anhand von $$break$$
				array2index++;									//und in das string array jeweils eingetragen
				array2index2 = 0;
			} else {
				array2[array2index][array2index2] = finalAttributes.get(i);
				array2index2++;
			}
		}
		
		//System.out.println(array1);
		//System.out.println(array2);
			
		ArrayList<String> Xpath_query = new ArrayList<String>();
		String Xpath_query2 = new String();
		
		for(int i=0; i<tmp; i++){
		
			if (array2[i][0] == null || array2[i][0].contains("comment") || array2[i][0].contains("labels-")){
			Xpath_query2 = "//ns:*["+array1[i][0]+"]";
			
			} else {
			Xpath_query2 = "//ns:*[@id ='"+array2[i][0]+"']";
			//Xpath_query2 = "//ns:*[" + array1[i][0] + "]";
			}
			
			for(int tmp2 = 1; tmp2 < intarray[i]; tmp2++){
				if (array2[i][tmp2] == null || array2[i][tmp2].contains("comment") || array2[i][tmp2].contains("labels-")){
						Xpath_query2 = Xpath_query2 + "/ns:*["+array1[i][tmp2]+"]";
				} else {
						Xpath_query2 = Xpath_query2 + "/ns:*[@id ='"+array2[i][tmp2]+"']";
						//Xpath_query2 = Xpath_query2 + "/ns:*[" + array1[i][tmp2] + "]";
				}
			
			}
			Xpath_query2 = Xpath_query2 + "/text()";
			//System.out.println(Xpath_query2);
			Xpath_query.add(Xpath_query2);

		}	
		
		return Xpath_query;
		
	}
		
		//liefert zu einer finalPredecessors Liste den entsprechenden XPath_Query
	static void write_genfile (Document doc, String string){
		
		XMLOutputter xmlOutput = new XMLOutputter();  
		
		//try {
		//xmlOutput.output(doc, System.out);
		//} catch (IOException e) {
		//// TODO Auto-generated catch block
		//e.printStackTrace();
		//}  
		
		xmlOutput.setFormat(Format.getPrettyFormat());  
		try {
			xmlOutput.output(doc, new FileWriter(string));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	public static void clearList(ArrayList<String> ButtonList){
	        for (int i = 0; i < ButtonList.size(); i++) {
	            ButtonList.remove(i);
	        }
	}
				
				
}