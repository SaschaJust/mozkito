/**
 * 
 */
package de.unisaarland.cs.st.reposuite.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.security.MessageDigest;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.exceptions.FetchException;
import de.unisaarland.cs.st.reposuite.exceptions.FilePermissionException;
import de.unisaarland.cs.st.reposuite.exceptions.LoadingException;
import de.unisaarland.cs.st.reposuite.exceptions.StoringException;
import de.unisaarland.cs.st.reposuite.exceptions.UnsupportedProtocolException;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class IOUtils {
	
	/**
	 * @param uri
	 * @return
	 * @throws UnsupportedProtocolException
	 * @throws FetchException
	 */
	public static RawContent fetch(final URI uri) throws UnsupportedProtocolException, FetchException {
		Condition.notNull(uri);
		if (uri.getScheme().equals("http")) {
			return fetchHttp(uri);
		} else if (uri.getScheme().equals("https")) {
			return fetchHttps(uri);
		} else if (uri.getScheme().equals("file")) {
			return fetchFile(uri);
		} else {
			throw new UnsupportedProtocolException("This protocol hasn't been implemented yet: " + uri.getScheme());
		}
	}
	
	/**
	 * @param uri
	 * @param username
	 * @param password
	 * @return
	 * @throws FetchException
	 * @throws UnsupportedProtocolException
	 */
	public static RawContent fetch(final URI uri, final String username, final String password) throws FetchException,
	        UnsupportedProtocolException {
		Condition.notNull(uri);
		if (uri.getScheme().equals("http")) {
			return fetchHttp(uri, username, password);
		} else if (uri.getScheme().equals("https")) {
			return fetchHttps(uri, username, password);
		} else if (uri.getScheme().equals("file")) {
			return fetchFile(uri);
		} else {
			throw new UnsupportedProtocolException("This protocol hasn't been implemented yet: " + uri.getScheme());
		}
	}
	
	/**
	 * @param uri
	 * @return
	 * @throws FetchException
	 */
	public static RawContent fetchFile(final URI uri) throws FetchException {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			StringBuilder builder = new StringBuilder();
			File file = new File(uri.getPath());
			
			FileUtils.ensureFilePermissions(file, FileUtils.READABLE_FILE);
			
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			
			while ((line = reader.readLine()) != null) {
				builder.append(line);
				builder.append(FileUtils.lineSeparator);
			}
			
			reader.close();
			
			return new RawContent(uri, md.digest(builder.toString().getBytes()), new DateTime(file.lastModified()),
			        "xhtml", builder.toString());
			
		} catch (Exception e) {
			throw new FetchException("Providing the " + RawContent.class.getSimpleName() + " of `" + uri.toString()
			        + "` failed.", e);
		}
	}
	
	/**
	 * @param uri
	 * @return
	 * @throws FetchException
	 */
	public static RawContent fetchHttp(final URI uri) throws FetchException {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			
			StringBuilder content = new StringBuilder();
			
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet(uri);
			HttpResponse response = httpClient.execute(request);
			HttpEntity entity = response.getEntity();
			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
			String line;
			
			while ((line = reader.readLine()) != null) {
				content.append(line);
			}
			
			Header contentType = entity.getContentType();
			
			return new RawContent(uri, md.digest(content.toString().getBytes()), new DateTime(),
			        contentType.getValue(), content.toString());
		} catch (Exception e) {
			throw new FetchException("Providing the " + RawContent.class.getSimpleName() + " of `" + uri.toString()
			        + "` failed.", e);
		}
	}
	
	/**
	 * @param uri
	 * @param username
	 * @param password
	 * @return
	 * @throws FetchException
	 */
	public static RawContent fetchHttp(final URI uri, final String username, final String password)
	        throws FetchException {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			
			StringBuilder content = new StringBuilder();
			
			DefaultHttpClient httpClient = new DefaultHttpClient();
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(new AuthScope(uri.getHost(), AuthScope.ANY_PORT),
			        new UsernamePasswordCredentials(username, password));
			httpClient.setCredentialsProvider(credsProvider);
			
			HttpGet request = new HttpGet(uri);
			HttpResponse response = httpClient.execute(request);
			HttpEntity entity = response.getEntity();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
			String line;
			
			while ((line = reader.readLine()) != null) {
				content.append(line);
			}
			
			Header contentType = entity.getContentType();
			
			return new RawContent(uri, md.digest(content.toString().getBytes()), new DateTime(),
			        contentType.getValue(), content.toString());
		} catch (Exception e) {
			throw new FetchException("Providing the " + RawContent.class.getSimpleName() + " of `" + uri.toString()
			        + "` failed.", e);
		}
	}
	
	/**
	 * @param uri
	 * @return
	 * @throws FetchException
	 */
	public static RawContent fetchHttps(final URI uri) throws FetchException {
		return fetchHttp(uri);
	}
	
	/**
	 * @param uri
	 * @param username
	 * @param password
	 * @return
	 * @throws FetchException
	 */
	public static RawContent fetchHttps(final URI uri, final String username, final String password)
	        throws FetchException {
		return fetchHttp(uri, username, password);
	}
	
	/**
	 * @param file
	 * @return
	 * @throws LoadingException
	 * @throws FilePermissionException
	 */
	public static Storable load(final File file) throws LoadingException, FilePermissionException {
		Condition.notNull(file);
		
		Storable object;
		
		FileUtils.ensureFilePermissions(file, FileUtils.READABLE_FILE);
		
		FileInputStream fin;
		ObjectInputStream ois = null;
		
		try {
			fin = new FileInputStream(file);
			ois = new ObjectInputStream(fin);
			object = (Storable) ois.readObject();
			object.setCached(file.getAbsolutePath());
		} catch (FileNotFoundException e) {
			throw new LoadingException("File `" + file.getAbsolutePath()
			        + "` could not be found when trying to load object.");
		} catch (IOException e) {
			throw new LoadingException(e.getClass().getName() + " occurred when reading from file: `"
			        + file.getAbsolutePath() + "`.", e);
		} catch (ClassNotFoundException e) {
			throw new LoadingException("Corresponding class was not found when loading object from file: `"
			        + file.getAbsolutePath() + "`.", e);
		} finally {
			try {
				if (ois != null) {
					ois.close();
				}
				
			} catch (IOException e) {
			}
		}
		return object;
	}
	
	/**
	 * @param object
	 * @param directory
	 * @param overwrite
	 * @throws StoringException
	 * @throws FilePermissionException
	 */
	public static void store(final Storable object, final File directory, final String fileName, final boolean overwrite)
	        throws StoringException, FilePermissionException {
		Condition.notNull(object);
		Condition.notNull(directory);
		
		FileUtils.ensureFilePermissions(directory, FileUtils.ACCESSIBLE_DIR | FileUtils.WRITABLE);
		
		String path = directory.getAbsolutePath() + FileUtils.fileSeparator + fileName;
		File file = new File(path);
		
		FileUtils.ensureFilePermissions(file, FileUtils.WRITABLE);
		
		if (!overwrite) {
			if (file.exists()) {
				throw new StoringException("File `" + path + "` already exists.");
			}
		} else {
			FileUtils.ensureFilePermissions(file, FileUtils.OVERWRITABLE_FILE);
		}
		
		FileOutputStream fout;
		ObjectOutputStream oos = null;
		
		try {
			fout = new FileOutputStream(file);
			oos = new ObjectOutputStream(fout);
			object.setCached(file.getAbsolutePath());
			oos.writeObject(object);
			oos.close();
		} catch (FileNotFoundException e) {
			throw new StoringException("Could not create file `" + fileName + "`.", e);
		} catch (IOException e) {
			throw new StoringException(e.getClass().getSimpleName() + " occurred when trying to write `" + fileName
			        + "`.", e);
		} finally {
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
