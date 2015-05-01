package org.mozkito.issues.adaptive;

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


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Class Main.
 *
 * @author Sascha Just
 */
public class PostIssues {
	
	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 * @throws JSONException 
	 * @throws FileNotFoundException 
	 */
	public static void main(final String[] args) throws JSONException, FileNotFoundException {
		if (args.length < 3) {
			System.err.println("Input arguments: BASE_URL USERNAME PASSWORD");
			System.exit(1);
		}
		
		final PostIssues PostIssues = new PostIssues();
		
		InputStream is = null;
		is = new FileInputStream("/Users/Eric/mozkito/mozkito-modules/mozkito-issues-adaptive/gen_files/csv-test.csv");
		String[] reports = CSV_reader.read_in(is);
		int sizerep = reports.length;
		String[][]reports2 = new String[sizerep][sizerep];
		
		int size = reports.length;
		System.out.println(size);
		
		for (int i=0; i<size; i++){
			reports2[i]=reports[i].split(";");
		}
		
//		String[] report1 = reports[0].split(";");
//		String[] report2 = reports[1].split(";");
//		String[] report3 = reports[2].split(";");
//		String[] report4 = reports[3].split(";");
//		String[] report5 = reports[4].split(";");
//		String[] report6 = reports[5].split(";");
//		String[] report7 = reports[6].split(";");
//		String[] report8 = reports[7].split(";");
//		String[] report9 = reports[8].split(";");
//		String[] report10 = reports[9].split(";");
//		String[] report11 = reports[10].split(";");
//		String[] report12 = reports[11].split(";");
//		String[] report13 = reports[12].split(";");
//		String[] report14 = reports[13].split(";");
//		String[] report15 = reports[14].split(";");
//		String[] report16 = reports[15].split(";");
//		String[] report17 = reports[16].split(";");
		

		for (int i=0; i<size; i++){

			for (int j=0; j<size-1; j++){
				System.out.println(reports2[i][j]);
			}
		}
		
		
		final String baseURL = args[0];
		final String user = args[1];
		final String password = args[2];
		
		try {
			// e.g. "https://issues.mozkito.org"
			PostIssues.init(new URI(baseURL));
			PostIssues.auth(user, password);
			final String response = PostIssues.fetch("https://issues.mozkito.org/browse/MTEST-1");
			System.out.println(response);
			
			// create new issue
			
			// the URL we send the POST request to
			final HttpPost post = new HttpPost("https://issues.mozkito.org/rest/api/2/issue/");
			
			// easiest way: create a JSON object containing all the data
			// see
			// https://developer.atlassian.com/jiradev/api-reference/jira-rest-apis/jira-rest-api-tutorials/jira-rest-api-example-create-issue
			// and
			// https://developer.atlassian.com/jiradev/api-reference/jira-rest-apis/jira-rest-api-tutorials/jira-rest-api-example-edit-issues
			// and
			// https://developer.atlassian.com/jiradev/api-reference/jira-rest-apis/jira-rest-api-tutorials/jira-rest-api-example-add-comment
			final JSONObject jsonFields = new JSONObject();
			
			// put values here
			final JSONObject jsonProject = new JSONObject();
			jsonProject.put("key", "MTEST");
			
			jsonFields.put("project", jsonProject);
			jsonFields.put("summary", "This is a test.");
			jsonFields.put("description", "more testing");
			jsonFields.put("customfield_10002", "2011-10-03");
			
			final JSONObject jsonIssueType = new JSONObject();
			jsonIssueType.put("name", "Task");
			jsonFields.put("issuetype", jsonIssueType);
			
			final JSONObject jsonObject = new JSONObject();
			jsonObject.put("fields", jsonFields);
			
			System.err.println(jsonObject.toString(4));
			
			// set the entity content for httpclient
			final StringEntity entity = new StringEntity(jsonObject.toString());
			entity.setContentType("application/json");
			post.setEntity(entity);
			
			// send the request
			final HttpResponse httpresponse = PostIssues.httpClient.execute(post);
			
			// check returned data
			final BufferedReader rd = new BufferedReader(new InputStreamReader(httpresponse.getEntity().getContent()));
			String line;
			while ((line = rd.readLine()) != null) {
				System.out.println(line);
			}
		} catch (AuthenticationException | URISyntaxException | KeyManagementException | NoSuchAlgorithmException
		        | IOException e) {
			e.printStackTrace(System.err);
		}
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
	 */
	public String fetch(final String uri) throws IOException {
		final HttpGet request = new HttpGet(uri);
		final HttpResponse response = this.httpClient.execute(request);
		final HttpEntity entity = response.getEntity();
		
		final StringBuilder content = new StringBuilder();
		final BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
		String line;
		
		while ((line = reader.readLine()) != null) {
			content.append(line);
		}
		
		entity.getContentType();
		
		return content.toString();
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
