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
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

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
	public boolean checkRAW(final String rawString) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean checkXML(final Document second) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public Document createDocument(final String second) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public DocumentIterator fetch() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Document fetch(final String id) {
		List<NameValuePair> params = URLEncodedUtils.parse(this.uri, "UTF-8");
		params.add(new BasicNameValuePair("atid", id));
		try {
			URI bugURI = URIUtils.createURI(this.uri.getScheme(), this.uri.getHost(), this.uri.getPort(),
			        this.uri.getPath(), URLEncodedUtils.format(params, "UTF-8"), this.uri.getFragment());
			Tuple<String, String> bugReportString = fetchSource(bugURI);
			StringReader dataStream = new StringReader(bugReportString.getSecond());
			
			if (bugReportString.getFirst().equals("XML")) {
				SAXReader reader = new SAXReader(true);
				Document document = reader.read(dataStream);
				return document;
			} else if (false) {
				// sdkf
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public BugReport parse(final Document document) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setup(final URI uri, final String baseUrl, final String filter, final String username,
	        final String password, final String startAt, final String stopAt) {
		// TODO Auto-generated method stub
		super.setup(uri, baseUrl, filter, username, password, startAt, stopAt);
	}
	
}
