/***********************************************************************************************************************
 * Copyright 2014 Sascha Just
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

package org.mozkito.issues.adaptive;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderSAX2Factory;
import org.jdom2.output.XMLOutputter;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The Class Main.
 *
 * @author Sascha Just
 */
public class Main_bugzilla {
	
	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 * @throws URISyntaxException 
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 * @throws AuthenticationException 
	 * @throws JDOMException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws XPathExpressionException 
	 */
	public static void main(final String[] args) throws KeyManagementException, NoSuchAlgorithmException, IOException, URISyntaxException, AuthenticationException, JDOMException, XPathExpressionException, ParserConfigurationException, SAXException {
		if (args.length < 3) {
			System.err.println("Input arguments: BASE_URL USERNAME PASSWORD");
			System.exit(1);
		}
		
		final Main_bugzilla main = new Main_bugzilla();
		
		final String baseURL = args[0];
		final String user = args[1];
		final String password = args[2];
		
		final ArrayList<String> pathes = new ArrayList<String>();
		
		
			// e.g. "https://issues.mozkito.org"
			main.init(new URI(baseURL));
			main.auth(user, password);
			
			
			//	id von MTEST-6
			String id_to_find = ">MTEST-6<";
			
			//  lastUpdatedTimestamp von MTEST-6
			String lastupTS_to_find = "Yesterday 2:55 PM";
			
			//  personContainer von MTEST-6
			String personContainer_to_find = "Eric Gliemmo";
			
			//  Priority von MTEST-6
			String priority_to_find = "Major";
			
			//  resolution von MTEST-6
			String resolution_to_find = "Unresolved";
			
		    //  resolutionTS
			String resolutionTS_to_find = "";				//gibt es bisher noch nicht
			
		    //  severtiy
			String severity_to_find = "";					//gibt es bisher noch nicht
			
		    //  status von MTEST-6
			String status_to_find = "In Progress";
			
			//  subject
			String subject_to_find = "";					//gibt es bisher noch nicht
			
		    //  typen von MTEST-6
			String type_to_find = "Task";
			
			ArrayList<String> strings_to_find = new ArrayList<String>();
			strings_to_find.add(id_to_find);
			strings_to_find.add(lastupTS_to_find); 
			strings_to_find.add(personContainer_to_find);
			strings_to_find.add(priority_to_find);
			strings_to_find.add(resolution_to_find);
			strings_to_find.add(status_to_find);
			strings_to_find.add(type_to_find);
			
			int size_strings_to_find = strings_to_find.size();
			
			
			Document document_to_compare1 = main.fetch("https://issues.mozkito.org/browse/MTEST-6");
			Document document_to_compare2 = main.fetch("https://issues.mozkito.org/browse/MTEST-2");
			
			String filepath_to_compare1 = "/Users/Eric/mozkito/mozkito-modules/mozkito-issues-adaptive/gen_files/document_to_compare1.xml";
			String filepath_to_compare2 = "/Users/Eric/mozkito/mozkito-modules/mozkito-issues-adaptive/gen_files/document_to_compare2.xml";
			
			parse_test.write_genfile(document_to_compare1, filepath_to_compare1);
			parse_test.write_genfile(document_to_compare2, filepath_to_compare2);
			
			
			
			ArrayList<String> marker_to_find = new ArrayList<String>();
			marker_to_find.add("lobet_den_herren.png");
			marker_to_find.add("commentforreport6");  //problem!!!!
			marker_to_find.add("$$description_marker$$");
			marker_to_find.add("$$environment_marker$$");
			marker_to_find.add("labelforreport6");
			marker_to_find.add("$$title_marker$$");	//problem!!!!
			
			ArrayList<String> url_to_find = new ArrayList<String>();
			url_to_find.add("https://issues.mozkito.org/browse/MTEST-6");
			url_to_find.add("https://issues.mozkito.org/browse/MTEST-6");	//problem!!!!
			url_to_find.add("https://issues.mozkito.org/browse/MTEST-18");
			url_to_find.add("https://issues.mozkito.org/browse/MTEST-17");
			url_to_find.add("https://issues.mozkito.org/browse/MTEST-6");
			url_to_find.add("https://issues.mozkito.org/browse/MTEST-1");	//problem!!!!
			
			int size = marker_to_find.size();
			
			for(int i = 0 ; i < size; i++){
				String string = marker_to_find.get(i);
				Document doc = main.fetch(url_to_find.get(i));
				//System.out.println(url_to_find.get(i));
				
				String filepath = "/Users/Eric/mozkito/mozkito-modules/mozkito-issues-adaptive/gen_files/" + marker_to_find.get(i) + ".xml";  
				parse_test.write_genfile(doc, filepath);													//speichere xml-doc lokal als file
				
				
				ArrayList<String> tmpList = parse_test.initSearch(doc, string);
				
				//TODO!!!!
				if (tmpList == null){
					System.out.println("Nix gefunden" );
				} else {
					//int size2 = tmpList.size();								//hier muss noch ueberlegt werden, wenn die markersuche 2 ergebnisse liefert,
					pathes.add(tmpList.get(0));				//welches genommen wird und nicht einfach nur das erste
					System.out.println(tmpList.get(0));
					
				}
			}
			
			System.out.println("Fertig mit den Markern, jetzt läuft CompareDocuments");
			
			for(int i = 0 ; i < size_strings_to_find; i++){
				ArrayList<String> tompList = parse_test.initSearch(document_to_compare1, strings_to_find.get(i));
				if (tompList == null){
					System.out.println("Nix gefunden" );
				} else {
					pathes.add(tompList.get(0));				//welches genommen wird und nicht einfach nur das erste
					System.out.println(tompList.get(0));
					
				}
			}
			
			//final Document doc = main.fetch("https://issues.mozkito.org/browse/MTEST-2");
			//final String response = main.fetch("https://issues.mozkito.org/browse/MTEST-1");
			//System.out.println(response);
			
			NodeList finalnodes = null;
			int pathes_length = pathes.size();
			ArrayList<String> results = new ArrayList<String>();
			
			//-----------------------write xml files for JIRA issues mozkito---------------------------------------
			
			String[] report_names = {"MTEST-1","MTEST-2","MTEST-3","MTEST-4","MTEST-5","MTEST-6","MTEST-7","MTEST-8",
					"MTEST-9","MTEST-10","MTEST-11","MTEST-13","MTEST-14","MTEST-15","MTEST-16","MTEST-17","MTEST-18"};
			
			int sizerep = report_names.length;
			
			for(int i = 0 ; i < sizerep; i++){
				System.out.println("https://issues.mozkito.org/browse/" + report_names[i]);
				Document doc = main.fetch("https://issues.mozkito.org/browse/" + report_names[i]);
				
				String filepath = "/Users/Eric/mozkito/mozkito-modules/mozkito-issues-adaptive/gen_files/JiraMozkito/" + report_names[i] + ".xml";  
				parse_test.write_genfile(doc, filepath);													//speichere xml-doc lokal als file
				
				
			}
			
			//-----------------------------------------------------------------------------------------------------
			for (int j = 0 ; j < sizerep ; j++){
					
				System.out.println("Wir sind jetzt bei Report " +j+ ":" +report_names[j]+ "als nächstes");
					for (int i = 0 ; i < pathes_length ; i++){
						
						String filepath = "/Users/Eric/mozkito/mozkito-modules/mozkito-issues-adaptive/gen_files/JiraMozkito/" + report_names[j] + ".xml";
						//String filepath = "/Users/Eric/mozkito/mozkito-modules/mozkito-issues-adaptive/gen_files/" + marker_to_find.get(i) + ".xml";
						ArrayList<String> templist = Xpath2Data_with_JAVAX.start_xpath_string(filepath,pathes.get(i),finalnodes);
						
						if (templist.get(0) == ""){
							System.out.println("Nichts gefunden");
							results.add("Nichts gefunden");
						} else {
							results.add(templist.get(0));
							System.out.println(templist.get(0));
						}
					}
			}
			
			
		
	}
	
	public static ArrayList<String> supply_data(String filepath, ArrayList<String> pathes){
		//TODO liefert zu dem Miningplan (pathes liste) die daten als Stringliste!!
		
		return pathes;
	}
	
	/** The http client. */
	private DefaultHttpClient    httpClient = null;
	
	/** The Constant URL_SUFFIX. */
	private static final String  URL_SUFFIX = "/login";
	
	/** The Constant DEBUG. */
	private static final boolean DEBUG      = false;
	
	/** The tracker uri. */
	private URI                  trackerUri = null;
	
	/**
	 * Auth.
	 *
	 * @param user
	 *            the user
	 * @param password
	 *            the password
	 * @return true, if successful
	 * @throws AuthenticationException
	 *             the authentication exception
	 */
	public boolean auth(final String user,
	                    final String password) throws AuthenticationException {
		if (password != null) {
			if (user == null) {
				throw new AuthenticationException("Password set, but no username given.");
			}
			final String authURL = this.trackerUri + URL_SUFFIX;
			
			final HttpPost post = new HttpPost(authURL);
			
			try {
				final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
				nameValuePairs.add(new BasicNameValuePair("os_username", user));
				nameValuePairs.add(new BasicNameValuePair("os_password", password));
				nameValuePairs.add(new BasicNameValuePair("username", user));
				nameValuePairs.add(new BasicNameValuePair("password", password));
				nameValuePairs.add(new BasicNameValuePair("os_cookie", "true"));
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				
				final HttpResponse response = this.httpClient.execute(post);
				final BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				String line = null;
				
				while ((line = rd.readLine()) != null) {
					if (DEBUG) {
						System.err.println(line);
					}
				}
				
				final List<Cookie> cookies = this.httpClient.getCookieStore().getCookies();
				if (DEBUG) {
					System.err.println(String.format("Received %s cookies.", cookies.size()));
				}
				
				if (DEBUG) {
					for (final Cookie cookie : cookies) {
						System.err.println(cookie.toString());
					}
				}
			} catch (final IOException e) {
				e.printStackTrace();
				return false;
			}
			
		}
		
		return true;
	}
	
	/**
	 * Fetch.
	 *
	 * @param uri
	 *            the uri
	 * @return the string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JDOMException 
	 */
	public Document fetch(final String uri) throws IOException, JDOMException {
		final HttpGet request = new HttpGet(uri);
		final HttpResponse response = this.httpClient.execute(request);
		final HttpEntity entity = response.getEntity();
		
		final StringBuilder content = new StringBuilder();
		final BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
		
		SAXBuilder sBuilder = new SAXBuilder(new XMLReaderSAX2Factory(false,
                "org.ccil.cowan.tagsoup.Parser"));
		Document document = sBuilder.build(reader);
		
		//XMLOutputter out = new XMLOutputter();
		//out.output( document, System.out );	
		
		String line;
		
		while ((line = reader.readLine()) != null) {
			content.append(line);
		}
		
		entity.getContentType();
		
		return document;
	}
	
	/**
	 * Inits the.
	 *
	 * @param projectKey
	 *            the project key
	 * @param trackerUri
	 *            the tracker uri
	 * @throws NoSuchAlgorithmException
	 *             the no such algorithm exception
	 * @throws KeyManagementException
	 *             the key management exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void init(final URI trackerUri) throws NoSuchAlgorithmException, KeyManagementException, IOException {
		this.trackerUri = trackerUri;
		
		final SSLContext sslcontext = SSLContext.getInstance("SSL");
		sslcontext.init(null, new TrustManager[] { new X509TrustManager() {
			
			public void checkClientTrusted(final X509Certificate[] certs,
			                               final String authType) {
				if (DEBUG) {
					System.err.println("checkClientTrusted =============");
				}
			}
			
			public void checkServerTrusted(final X509Certificate[] certs,
			                               final String authType) {
				if (DEBUG) {
					System.err.println("checkServerTrusted =============");
				}
			}
			
			public X509Certificate[] getAcceptedIssuers() {
				if (DEBUG) {
					System.err.println("getAcceptedIssuers =============");
				}
				return null;
			}
		} }, new SecureRandom());
		
		final SSLSocketFactory sf = new SSLSocketFactory(sslcontext);
		final Scheme httpsScheme = new Scheme("https", 443, sf);
		final SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(httpsScheme);
		
		final ClientConnectionManager cm = new BasicClientConnectionManager(schemeRegistry);
		this.httpClient = new DefaultHttpClient(cm);
		
		this.httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
		this.httpClient.setCookieStore(new BasicCookieStore());
	}
}
