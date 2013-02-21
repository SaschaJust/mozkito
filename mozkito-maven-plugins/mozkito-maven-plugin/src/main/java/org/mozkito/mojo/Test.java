/***********************************************************************************************************************
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
 **********************************************************************************************************************/

package org.mozkito.mojo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import net.ownhero.dev.kisa.Logger;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class Test {
	
	public static void main(final String[] args) {
		final boolean useAuth = true;
		final String feedUrlString = "http://mozkito.org:8085/rss/createAllBuildsRssFeed.action?feedType=rssAll&os_authType=basic";
		URL feedUrl;
		try {
			feedUrl = new URL(feedUrlString);
			final SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = null;
			
			if (useAuth) {
				final HttpURLConnection httpcon = (HttpURLConnection) feedUrl.openConnection();
				final String encoding = new sun.misc.BASE64Encoder().encode("methos:9xchange_tty2".getBytes());
				httpcon.setRequestProperty("Authorization", "Basic " + encoding);
				feed = input.build(new XmlReader(httpcon));
			} else {
				feed = input.build(new XmlReader(feedUrl));
			}
			
			System.err.println(feed);
			// for (final Iterator<SyndEntry> it = feed.getEntries().iterator(); it.hasNext();) {
			// final SyndEntry entry = it.next();
			// System.err.println(entry);
			// }
		} catch (IllegalArgumentException | FeedException | IOException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			
		}
		
	}
}
