/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.sourceforge;

import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.dom4j.Document;

import de.unisaarland.cs.st.reposuite.bugs.exceptions.UnsupportedProtocolException;
import de.unisaarland.cs.st.reposuite.bugs.tracker.DocumentIterator;
import de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.BugReport;
import de.unisaarland.cs.st.reposuite.utils.Tuple;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class SourceforgeTracker extends Tracker {
	
	@Override
	public boolean checkRAW(final String rawReport) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean checkXML(final Document xmlReport) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public Document createDocument(final String rawReport) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public DocumentIterator fetch() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String fetch(final Long id) {
		List<NameValuePair> params = URLEncodedUtils.parse(this.linkUri, "UTF-8");
		params.add(new BasicNameValuePair("atid", id + ""));
		try {
			URI bugURI = URIUtils.createURI(this.linkUri.getScheme(), this.linkUri.getHost(), this.linkUri.getPort(),
			        this.linkUri.getPath(), URLEncodedUtils.format(params, "UTF-8"), this.linkUri.getFragment());
			Tuple<String, String> bugReportString = fetchSource(bugURI);
			new StringReader(bugReportString.getSecond());
			
			if (bugReportString.getFirst().equals("XML")) {
				// SAXReader reader = new SAXReader(true);
				// Document document = reader.read(dataStream);
				// return document;
				return "";
			} else if (false) {
				// sdkf
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public URI getLinkFromId(final Long bugId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Long getNextId() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public BugReport parse(final Document document) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
