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
package de.unisaarland.cs.st.reposuite.bugs.tracker;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;

import org.jdom.Document;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class XmlReport extends RawReport {
	
	private static final long serialVersionUID = 6524458006854786132L;
	private final Document    document;
	
	/**
	 * @param rawReport
	 * @param document
	 */
	@NoneNull
	public XmlReport(final RawReport rawReport, final Document document) {
		super(rawReport.getId(), rawReport);
		this.document = document;
	}
	
	/**
	 * @return the document
	 */
	public Document getDocument() {
		return this.document;
	}
	
}
