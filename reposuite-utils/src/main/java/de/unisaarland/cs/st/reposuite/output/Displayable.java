/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
