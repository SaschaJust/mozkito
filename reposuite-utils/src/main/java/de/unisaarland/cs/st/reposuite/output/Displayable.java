/**
 * 
 */
package de.unisaarland.cs.st.reposuite.output;

import java.io.IOException;
import java.io.OutputStream;

import org.dom4j.Document;
import org.w3c.dom.html.HTMLDocument;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public interface Displayable {
	
	public String toCSV();
	
	public void toCSV(OutputStream stream) throws IOException;
	
	public HTMLDocument toHTML();
	
	public void toHTML(OutputStream stream) throws IOException;
	
	public String toTerm();
	
	public void toTerm(OutputStream stream) throws IOException;
	
	public String toText();
	
	public void toText(OutputStream stream) throws IOException;
	
	public Document toXML();
	
	public void toXML(OutputStream stream) throws IOException;
}
