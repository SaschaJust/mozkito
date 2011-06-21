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

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.ioda.container.RawContent;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.annotations.simple.Positive;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RawReport extends RawContent {
	
	private static final long serialVersionUID = -4448381593266361762L;
	private final long        id;
	
	/**
	 * @param id
	 *            not null, &gt;0
	 * @param rawContent
	 *            not null
	 */
	public RawReport(@Positive final long id, @NotNull final RawContent rawContent) {
		super(rawContent.getUri(), rawContent.getMd5(), rawContent.getFetchTime(), rawContent.getFormat(), rawContent
		      .getContent());
		this.id = id;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final RawContent arg0) {
		if (arg0 == null) {
			return 1;
		} else if (arg0 instanceof RawReport) {
			if (this.id > ((RawReport) arg0).id) {
				return 1;
			} else if (this.id < ((RawReport) arg0).id) {
				return -1;
			} else {
				return 1;
			}
		} else {
			return super.compareTo(arg0);
		}
	}
	
	/**
	 * @return the id
	 */
	public long getId() {
		return this.id;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RawReport [id=");
		builder.append(this.id);
		builder.append(", fetchTime=");
		builder.append(getFetchTime());
		builder.append(", format=");
		builder.append(getFormat());
		builder.append(", md5=");
		try {
			builder.append(JavaUtils.byteArrayToHexString(getMd5()));
		} catch (UnsupportedEncodingException e) {
			builder.append(Arrays.toString(getMd5()));
		}
		builder.append(", uri=");
		builder.append(getUri());
		builder.append("]");
		return builder.toString();
	}
	
}
