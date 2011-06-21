/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
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
