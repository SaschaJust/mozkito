/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.moskito.bugs.tracker;

import net.ownhero.dev.ioda.container.RawContent;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;

import org.jdom.Document;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class XmlReport extends RawContent {
	
	private static final long serialVersionUID = 6524458006854786132L;
	private final Document    document;
	
	/**
	 * @param rawReport
	 * @param document
	 */
	@NoneNull
	public XmlReport(final RawContent rawContent, final Document document) {
		super(rawContent.getUri(), rawContent.getMd5(), rawContent.getFetchTime(), rawContent.getFormat(),
		      rawContent.getContent());
		this.document = document;
	}
	
	/**
	 * @return the document
	 */
	public Document getDocument() {
		return this.document;
	}
	
}
