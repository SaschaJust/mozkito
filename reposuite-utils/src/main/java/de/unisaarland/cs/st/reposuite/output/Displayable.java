/**
 * 
 */
package de.unisaarland.cs.st.reposuite.output;

import java.io.OutputStream;

import org.dom4j.Document;
import org.w3c.dom.html.HTMLDocument;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public interface Displayable {
	
	public String toCSV();
	
	public void toCSV(OutputStream stream);
	
	public HTMLDocument toHTML();
	
	public void toHTML(OutputStream stream);
	
	public String toTerm();
	
	public void toTerm(OutputStream stream);
	
	public String toText();
	
	public void toText(OutputStream stream);
	
	public Document toXML();
	
	public void toXML(OutputStream stream);
}
