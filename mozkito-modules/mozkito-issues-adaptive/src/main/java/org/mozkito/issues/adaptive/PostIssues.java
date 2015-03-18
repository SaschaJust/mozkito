package org.mozkito.issues.adaptive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import net.ownhero.dev.kisa.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderSAX2Factory;
import org.jdom2.output.XMLOutputter;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class PostIssues {
	
	public static void main(String[] args) throws ClientProtocolException, IOException, JDOMException {
		test();
	}
	
	public static void test() throws ClientProtocolException, IOException, JDOMException {
		
		String BASIC_URL = "https://issues.mozkito.org";
		
		final DefaultHttpClient httpClient = new DefaultHttpClient();
		
	   	httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
			httpClient.setCookieStore(new BasicCookieStore());
			
			String authUrl = BASIC_URL + "/secure/CreateIssueDetails.jspa" ;
			
			final HttpPost post = new HttpPost(authUrl);
			
			try {
				final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
				nameValuePairs.add(new BasicNameValuePair("pid", "1000"));
				nameValuePairs.add(new BasicNameValuePair("issuetype", "1"));
				nameValuePairs.add(new BasicNameValuePair("summary", "issue+created%20via+link"));
				nameValuePairs.add(new BasicNameValuePair("priority", "1"));
				nameValuePairs.add(new BasicNameValuePair("duedate", "15-Dec-2005"));
				nameValuePairs.add(new BasicNameValuePair("components", "1010"));
				nameValuePairs.add(new BasicNameValuePair("versions", "1011"));
				nameValuePairs.add(new BasicNameValuePair("fixVersions", "1011"));
				nameValuePairs.add(new BasicNameValuePair("assignee", "egliemmo"));
				nameValuePairs.add(new BasicNameValuePair("reporter", "egliemmo"));
				nameValuePairs.add(new BasicNameValuePair("environment", "this+is+the+environment"));
				nameValuePairs.add(new BasicNameValuePair("description", "this+is+the+description"));
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				
				final HttpResponse response = httpClient.execute(post);
				final BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				String line = null;
				
				while ((line = rd.readLine()) != null) {
					if (Logger.logDebug()) {
						Logger.debug(line);
					}
				}
				
				final List<Cookie> cookies = httpClient.getCookieStore().getCookies();
				if (Logger.logInfo()) {
					Logger.info("Received %s cookies.", cookies.size());
				}
				
				if (Logger.logDebug()) {
					for (final Cookie cookie : cookies) {
						Logger.debug(cookie.toString());
					}
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
			
	   	// authenticated 
			final HttpGet request = new HttpGet(authUrl);
			final HttpResponse response = httpClient.execute(request);
			final HttpEntity entity = response.getEntity();
			
			final BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
			SAXBuilder sBuilder = new SAXBuilder(new XMLReaderSAX2Factory(false,
                    "org.ccil.cowan.tagsoup.Parser"));
			Document document = sBuilder.build(reader);
			
			XMLOutputter out = new XMLOutputter();
			out.output( document, System.out );						// gibt die XML aus
			
			//start_searching(document);
	}
}
