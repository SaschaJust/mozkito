/**
 * 
 */
package net.ownhero.dev.ioda;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.ioda.container.RawContent;
import net.ownhero.dev.ioda.exceptions.FetchException;
import net.ownhero.dev.ioda.exceptions.FilePermissionException;
import net.ownhero.dev.ioda.exceptions.LoadingException;
import net.ownhero.dev.ioda.exceptions.StoringException;
import net.ownhero.dev.ioda.exceptions.UnsupportedProtocolException;
import net.ownhero.dev.ioda.exceptions.UnsupportedSchemaException;
import net.ownhero.dev.ioda.interfaces.Storable;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.DateTime;

/**
 * The Class IOUtils.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class IOUtils {
	
	public static class MySSLSocketFactory extends SSLSocketFactory {
		
		SSLContext sslContext = SSLContext.getInstance("TLS");
		
		public MySSLSocketFactory(final KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException,
		        KeyStoreException, UnrecoverableKeyException {
			super(truststore);
			
			final TrustManager tm = new X509TrustManager() {
				
				@Override
				public void checkClientTrusted(final X509Certificate[] chain,
				                               final String authType) throws CertificateException {
					return;
				}
				
				@Override
				public void checkServerTrusted(final X509Certificate[] chain,
				                               final String authType) throws CertificateException {
					return;
				}
				
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			
			this.sslContext.init(null, new TrustManager[] { tm }, null);
		}
		
		@Override
		public Socket createSocket() throws IOException {
			return this.sslContext.getSocketFactory().createSocket();
		}
		
		@Override
		public Socket createSocket(final Socket socket,
		                           final String host,
		                           final int port,
		                           final boolean autoClose) throws IOException, UnknownHostException {
			return this.sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
		}
	}
	
	/**
	 * Binaryfetch.
	 * 
	 * @param uri
	 *            the uri
	 * @return the byte[]
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws UnsupportedProtocolException
	 *             the unsupported protocol exception
	 * @throws FetchException
	 *             the fetch exception
	 */
	public static byte[] binaryfetch(final URI uri) throws IOException, UnsupportedProtocolException, FetchException {
		if (uri.getScheme().equals("http")) {
			return binaryfetchHttp(uri);
		} else if (uri.getScheme().equals("https")) {
			return binaryfetchHttps(uri);
		} else if (uri.getScheme().equals("file")) {
			return binaryfetchFile(uri);
		} else {
			throw new UnsupportedProtocolException("This protocol hasn't been implemented yet: " + uri.getScheme());
		}
	}
	
	/**
	 * Binaryfetch.
	 * 
	 * @param uri
	 *            the uri
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @return the byte[]
	 * @throws UnsupportedProtocolException
	 *             the unsupported protocol exception
	 * @throws FetchException
	 *             the fetch exception
	 */
	public static byte[] binaryfetch(final URI uri,
	                                 final String username,
	                                 final String password) throws UnsupportedProtocolException, FetchException {
		try {
			if (uri.getScheme().equals("http")) {
				return binaryfetchHttp(uri, username, password);
			} else if (uri.getScheme().equals("https")) {
				return binaryfetchHttps(uri, username, password);
				
			} else if (uri.getScheme().equals("file")) {
				return binaryfetchFile(uri);
			} else {
				throw new UnsupportedProtocolException("This protocol hasn't been implemented yet: " + uri.getScheme());
			}
		} catch (final IOException e) {
			throw new FetchException(e.getMessage(), e);
		}
	}
	
	/**
	 * Binaryfetch file.
	 * 
	 * @param uri
	 *            the uri
	 * @return the byte[]
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws UnsupportedSchemaException
	 */
	private static byte[] binaryfetchFile(final URI uri) throws IOException {
		if (uri.getScheme().equals("file")) {
			FileInputStream inputStream = null;
			inputStream = new FileInputStream(uri.getPath());
			return org.apache.commons.io.IOUtils.toByteArray(inputStream);
		}
		throw new IOException(
		                      String.format("URI schema `%s` is yet not supported in %s.binaryfetchFile(final URI uri)",
		                                    uri.getScheme(), IOUtils.class.getSimpleName()));
	}
	
	/**
	 * Binaryfetch http.
	 * 
	 * @param uri
	 *            the uri
	 * @return the byte[]
	 * @throws FetchException
	 *             the fetch exception
	 */
	private static byte[] binaryfetchHttp(final URI uri) throws FetchException {
		try {
			final HttpClient httpClient = new DefaultHttpClient();
			final HttpGet request = new HttpGet(uri);
			final HttpResponse response = httpClient.execute(request);
			final HttpEntity entity = response.getEntity();
			final byte[] data = readbinaryData(entity);
			httpClient.getConnectionManager().shutdown();
			return data;
		} catch (final Exception e) {
			throw new FetchException("Providing the binary data of `" + uri.toString() + "` failed.", e);
		}
	}
	
	/**
	 * Binaryfetch http.
	 * 
	 * @param uri
	 *            the uri
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @return the byte[]
	 * @throws ClientProtocolException
	 *             the client protocol exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static byte[] binaryfetchHttp(final URI uri,
	                                      final String username,
	                                      final String password) throws ClientProtocolException, IOException {
		final DefaultHttpClient httpClient = new DefaultHttpClient();
		final CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(uri.getHost(), AuthScope.ANY_PORT),
		                             new UsernamePasswordCredentials(username, password));
		httpClient.setCredentialsProvider(credsProvider);
		
		final HttpGet request = new HttpGet(uri);
		final HttpResponse response = httpClient.execute(request);
		final HttpEntity entity = response.getEntity();
		final byte[] data = readbinaryData(entity);
		httpClient.getConnectionManager().shutdown();
		return data;
	}
	
	/**
	 * Binaryfetch https.
	 * 
	 * @param uri
	 *            the uri
	 * @return the byte[]
	 * @throws FetchException
	 *             the fetch exception
	 */
	private static byte[] binaryfetchHttps(final URI uri) throws FetchException {
		return binaryfetchHttp(uri);
	}
	
	/**
	 * Binaryfetch https.
	 * 
	 * @param uri
	 *            the uri
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @return the byte[]
	 * @throws ClientProtocolException
	 *             the client protocol exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static byte[] binaryfetchHttps(final URI uri,
	                                       final String username,
	                                       final String password) throws ClientProtocolException, IOException {
		return binaryfetchHttp(uri, username, password);
	}
	
	private static SSLSocketFactory buildSSLSocketFactory() {
		final TrustStrategy ts = new TrustStrategy() {
			
			@Override
			public boolean isTrusted(final X509Certificate[] x509Certificates,
			                         final String s) throws CertificateException {
				return true; // heck yea!
			}
		};
		
		SSLSocketFactory sf = null;
		
		try {
			/* build socket factory with hostname verification turned off. */
			sf = new SSLSocketFactory(ts, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		} catch (final NoSuchAlgorithmException | KeyManagementException | KeyStoreException
		        | UnrecoverableKeyException e) {
			if (Logger.logError()) {
				Logger.error(e, "Failed to initialize SSL handling.");
			}
		}
		return sf;
	}
	
	private static void configureAuthentification(@NotNull final DefaultHttpClient hc,
	                                              final String username,
	                                              final String password) {
		
		if (Logger.logDebug()) {
			Logger.debug("Configuring authentification for http fetch using usernamee=`%s` and password=`******`.",
			             username);
		}
		
		final CredentialsProvider credsProvider = new BasicCredentialsProvider();
		if ((username != null) && (password != null)) {
			credsProvider.setCredentials(new AuthScope(AuthScope.ANY), new UsernamePasswordCredentials(username,
			                                                                                           password));
		}
		hc.setCredentialsProvider(credsProvider);
	}
	
	private static void configureProxy(@NotNull final DefaultHttpClient hc,
	                                   @NotNull final ProxyConfig proxyConfig) {
		
		if (Logger.logDebug()) {
			Logger.debug("Configuring proxy %s for http fetch.", proxyConfig);
		}
		
		final HttpHost proxyHost = new HttpHost(proxyConfig.getHost(), proxyConfig.getPort());
		
		if (proxyConfig.getUsername() != null) {
			hc.getCredentialsProvider().setCredentials(new AuthScope(proxyConfig.getHost(), proxyConfig.getPort()),
			                                           new UsernamePasswordCredentials(proxyConfig.getUsername(),
			                                                                           proxyConfig.getPassword()));
		}
		
		hc.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxyHost);
	}
	
	private static void configureSSLHandling(@NotNull final HttpClient hc) {
		if (Logger.logDebug()) {
			Logger.debug("Configuring SSL handling for http fetch.");
		}
		final Scheme http = new Scheme("http", 80, PlainSocketFactory.getSocketFactory());
		final SSLSocketFactory sf = buildSSLSocketFactory();
		final Scheme https = new Scheme("https", 443, sf);
		final SchemeRegistry sr = hc.getConnectionManager().getSchemeRegistry();
		sr.register(http);
		sr.register(https);
	}
	
	/**
	 * Copy input stream.
	 * 
	 * @param in
	 *            the in
	 * @param out
	 *            the out
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static final void copyInputStream(final InputStream in,
	                                         final OutputStream out) throws IOException {
		final byte[] buffer = new byte[1024];
		int len;
		
		while ((len = in.read(buffer)) >= 0) {
			out.write(buffer, 0, len);
		}
		
		in.close();
		out.close();
	}
	
	/**
	 * Fetch.
	 * 
	 * @param uri
	 *            the uri
	 * @return the raw content
	 * @throws UnsupportedProtocolException
	 *             the unsupported protocol exception
	 * @throws FetchException
	 *             the fetch exception
	 */
	public static RawContent fetch(@NotNull final URI uri) throws UnsupportedProtocolException, FetchException {
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
	 * Fetch proxy.
	 * 
	 * @param uri
	 *            the uri
	 * @param proxyConfig
	 *            the proxy config
	 * @return the raw content
	 * @throws UnsupportedProtocolException
	 *             the unsupported protocol exception
	 * @throws FetchException
	 *             the fetch exception
	 */
	public static RawContent fetch(@NotNull final URI uri,
	                               @NotNull final ProxyConfig proxyConfig) throws UnsupportedProtocolException,
	                                                                      FetchException {
		if (uri.getScheme().equals("http")) {
			return fetchHttp(uri, null, null, proxyConfig);
		} else if (uri.getScheme().equals("https")) {
			return fetchHttps(uri, null, null, proxyConfig);
		} else {
			if (Logger.logWarn()) {
				Logger.warn("Proxy for protocol %s is not yet supported. Fetching ignoring proxy.", uri.getScheme());
			}
			return fetch(uri);
		}
	}
	
	/**
	 * Fetch.
	 * 
	 * @param uri
	 *            the uri
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @return the raw content
	 * @throws FetchException
	 *             the fetch exception
	 * @throws UnsupportedProtocolException
	 *             the unsupported protocol exception
	 */
	public static RawContent fetch(@NotNull final URI uri,
	                               final String username,
	                               final String password) throws FetchException, UnsupportedProtocolException {
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
	 * Fetch file.
	 * 
	 * @param uri
	 *            the uri
	 * @return the raw content
	 * @throws FetchException
	 *             the fetch exception
	 */
	public static RawContent fetchFile(final URI uri) throws FetchException {
		
		final StringBuilder builder = new StringBuilder();
		final File file = new File(uri.getPath());
		
		try (final BufferedReader reader = new BufferedReader(new FileReader(file))) {
			FileUtils.ensureFilePermissions(file, FileUtils.READABLE_FILE);
			String line;
			
			while ((line = reader.readLine()) != null) {
				builder.append(line);
				builder.append(FileUtils.lineSeparator);
			}
			
			reader.close();
			
			return new RawContent(uri, HashUtils.getMD5(builder.toString()), new DateTime(file.lastModified()),
			                      "xhtml", builder.toString());
			
		} catch (final Exception e) {
			throw new FetchException("Providing the " + RawContent.class.getSimpleName() + " of `" + uri.toString()
			        + "` failed.", e);
		}
	}
	
	/**
	 * Fetch http.
	 * 
	 * @param uri
	 *            the uri
	 * @return the raw content
	 * @throws FetchException
	 *             the fetch exception
	 */
	public static RawContent fetchHttp(final URI uri) throws FetchException {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			
			final StringBuilder content = new StringBuilder();
			
			final HttpClient httpClient = new DefaultHttpClient();
			final HttpGet request = new HttpGet(uri);
			final HttpResponse response = httpClient.execute(request);
			final HttpEntity entity = response.getEntity();
			final BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
			String line;
			
			while ((line = reader.readLine()) != null) {
				content.append(line);
			}
			
			final Header contentType = entity.getContentType();
			
			return new RawContent(uri, md.digest(content.toString().getBytes()), new DateTime(),
			                      contentType.getValue(), content.toString());
		} catch (final Exception e) {
			throw new FetchException("Providing the " + RawContent.class.getSimpleName() + " of `" + uri.toString()
			        + "` failed.", e);
		}
	}
	
	/**
	 * Fetch http.
	 * 
	 * @param uri
	 *            the uri
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @param httpClient
	 *            the http client
	 * @return the raw content
	 * @throws FetchException
	 *             the fetch exception
	 */
	public static RawContent fetchHttp(@NotNull final URI uri,
	                                   @NotNull final HttpClient httpClient) throws FetchException {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			
			final StringBuilder content = new StringBuilder();
			
			final HttpGet request = new HttpGet(uri);
			final HttpResponse response = httpClient.execute(request);
			final HttpEntity entity = response.getEntity();
			
			final BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
			String line;
			
			while ((line = reader.readLine()) != null) {
				content.append(line);
			}
			
			final Header contentType = entity.getContentType();
			
			return new RawContent(uri, md.digest(content.toString().getBytes()), new DateTime(),
			                      contentType.getValue(), content.toString());
		} catch (final Exception e) {
			throw new FetchException("Providing the " + RawContent.class.getSimpleName() + " of `" + uri.toString()
			        + "` failed.", e);
		}
	}
	
	/**
	 * Fetch http.
	 * 
	 * @param uri
	 *            the uri
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @return the raw content
	 * @throws FetchException
	 *             the fetch exception
	 */
	public static RawContent fetchHttp(final URI uri,
	                                   final String username,
	                                   final String password) throws FetchException {
		try {
			final DefaultHttpClient httpClient = new DefaultHttpClient();
			configureAuthentification(httpClient, username, password);
			return fetchHttp(uri, httpClient);
		} catch (final Exception e) {
			throw new FetchException("Providing the " + RawContent.class.getSimpleName() + " of `" + uri.toString()
			        + "` failed.", e);
		}
	}
	
	public static RawContent fetchHttp(@NotNull final URI uri,
	                                   final String username,
	                                   final String password,
	                                   @NotNull final ProxyConfig proxyConfig) throws FetchException {
		
		final DefaultHttpClient hc = new DefaultHttpClient();
		configureProxy(hc, proxyConfig);
		configureAuthentification(hc, username, password);
		return fetchHttp(uri, hc);
	}
	
	/**
	 * Fetch https.
	 * 
	 * @param uri
	 *            the uri
	 * @return the raw content
	 * @throws FetchException
	 *             the fetch exception
	 */
	public static RawContent fetchHttps(final URI uri) throws FetchException {
		return fetchHttp(uri);
	}
	
	/**
	 * Fetch https.
	 * 
	 * @param uri
	 *            the uri
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @return the raw content
	 * @throws FetchException
	 *             the fetch exception
	 */
	public static RawContent fetchHttps(final URI uri,
	                                    final String username,
	                                    final String password) throws FetchException {
		return fetchHttp(uri, username, password);
	}
	
	/**
	 * Fetch http proxy.
	 * 
	 * @param uri
	 *            the uri
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @param proxyConfig
	 *            the proxy config
	 * @return the raw content
	 * @throws FetchException
	 *             the fetch exception
	 */
	public static RawContent fetchHttps(@NotNull final URI uri,
	                                    final String username,
	                                    final String password,
	                                    @NotNull final ProxyConfig proxyConfig) throws FetchException {
		final DefaultHttpClient hc = new DefaultHttpClient();
		configureProxy(hc, proxyConfig);
		configureAuthentification(hc, username, password);
		configureSSLHandling(hc);
		return fetchHttp(uri, hc);
	}
	
	/**
	 * Gets the temporary copy of file.
	 * 
	 * @param file
	 *            the file
	 * @param uri
	 *            the uri
	 * @return the temporary copy of file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static File getTemporaryCopyOfFile(final File file,
	                                          final URI uri) throws IOException {
		try {
			FileUtils.ensureFilePermissions(file, FileUtils.READABLE_FILE);
		} catch (final FilePermissionException e1) {
			throw new IOException(e1);
		}
		
		byte[] data = new byte[0];
		OutputStream outputStream = null;
		
		try {
			outputStream = new FileOutputStream(file);
			
			if (uri.getScheme().equals("http")) {
				data = binaryfetchHttp(uri);
			} else if (uri.getScheme().equals("https")) {
				data = binaryfetchHttps(uri);
			} else if (uri.getScheme().equals("file")) {
				if (uri.getPath().contains(".jar!" + FileUtils.fileSeparator) && !new File(uri).exists()) {
					final String jarFilePath = uri.getPath()
					                              .substring(0,
					                                         uri.getPath().indexOf(".jar!" + FileUtils.fileSeparator) + 4);
					final File plainJarFile = new File(jarFilePath);
					
					if (plainJarFile.exists()) {
						final ZipFile jarFile = new ZipFile(plainJarFile);
						final String entryName = uri.getPath().substring(jarFilePath.length());
						final ZipEntry entry = jarFile.getEntry(entryName);
						copyInputStream(jarFile.getInputStream(entry), outputStream);
						outputStream.close();
						return file;
					}
					throw new IOException("JAR file for resource does not exist: " + jarFilePath);
				}
				data = binaryfetchFile(uri);
			} else {
				throw new UnsupportedProtocolException("This protocol hasn't been implemented yet: " + uri.getScheme());
			}
			
			outputStream.write(data);
			outputStream.close();
		} catch (final FetchException e) {
			throw new IOException(e);
		} catch (final UnsupportedProtocolException e) {
			throw new IOException(e);
		} finally {
			try {
				if (outputStream != null) {
					outputStream.flush();
					outputStream.close();
				}
			} catch (final Exception e) {
				throw new IOException(e);
			}
		}
		
		return file;
	}
	
	/**
	 * Gets the temporary copy of file.
	 * 
	 * @param prefix
	 *            the prefix
	 * @param suffix
	 *            the suffix
	 * @param uri
	 *            the uri
	 * @return the temporary copy of file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static File getTemporaryCopyOfFile(final String prefix,
	                                          final String suffix,
	                                          final URI uri) throws IOException {
		final File file = FileUtils.createRandomFile(prefix, suffix, FileShutdownAction.DELETE);
		return getTemporaryCopyOfFile(file, uri);
	}
	
	/**
	 * Gets the temporary copy of file.
	 * 
	 * @param uri
	 *            the uri
	 * @return the temporary copy of file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static File getTemporaryCopyOfFile(final URI uri) throws IOException {
		final File file = FileUtils.createRandomFile(FileShutdownAction.DELETE);
		return getTemporaryCopyOfFile(file, uri);
	}
	
	/**
	 * Load.
	 * 
	 * @param file
	 *            the file
	 * @return the storable
	 * @throws LoadingException
	 *             the loading exception
	 * @throws FilePermissionException
	 *             the file permission exception
	 */
	public static Storable load(@NotNull final File file) throws LoadingException, FilePermissionException {
		Storable object;
		
		FileUtils.ensureFilePermissions(file, FileUtils.READABLE_FILE);
		
		FileInputStream fin;
		ObjectInputStream ois = null;
		
		try {
			fin = new FileInputStream(file);
			ois = new ObjectInputStream(fin);
			object = (Storable) ois.readObject();
			object.setCached(file.getAbsolutePath());
		} catch (final FileNotFoundException e) {
			throw new LoadingException("File `" + file.getAbsolutePath()
			        + "` could not be found when trying to load object.");
		} catch (final IOException e) {
			throw new LoadingException(e.getClass().getName() + " occurred when reading from file: `"
			        + file.getAbsolutePath() + "`.", e);
		} catch (final ClassNotFoundException e) {
			throw new LoadingException("Corresponding class was not found when loading object from file: `"
			        + file.getAbsolutePath() + "`.", e);
		} finally {
			try {
				if (ois != null) {
					ois.close();
				}
				
			} catch (final IOException ignore) { // ignore
			}
		}
		return object;
	}
	
	/**
	 * Readbinary data.
	 * 
	 * @param entity
	 *            the entity
	 * @return the byte[]
	 * @throws IllegalStateException
	 *             the illegal state exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static byte[] readbinaryData(final HttpEntity entity) throws IllegalStateException, IOException {
		// XXX BUG FIXME rewrite from scratch
		final InputStream stream = entity.getContent();
		return org.apache.commons.io.IOUtils.toByteArray(stream);
	}
	
	/**
	 * Store.
	 * 
	 * @param object
	 *            the object
	 * @param directory
	 *            the directory
	 * @param fileName
	 *            the file name
	 * @param overwrite
	 *            the overwrite
	 * @throws StoringException
	 *             the storing exception
	 * @throws FilePermissionException
	 *             the file permission exception
	 */
	public static void store(@NotNull final Storable object,
	                         @NotNull final File directory,
	                         final String fileName,
	                         final boolean overwrite) throws StoringException, FilePermissionException {
		FileUtils.ensureFilePermissions(directory, FileUtils.ACCESSIBLE_DIR | FileUtils.WRITABLE);
		
		final String path = directory.getAbsolutePath() + FileUtils.fileSeparator + fileName;
		final File file = new File(path);
		
		FileUtils.ensureFilePermissions(file, FileUtils.WRITABLE_FILE);
		
		if (!overwrite) {
			if (file.exists()) {
				throw new StoringException("File `" + path + "` already exists.");
			}
		}
		
		FileOutputStream fout;
		ObjectOutputStream oos = null;
		
		try {
			fout = new FileOutputStream(file);
			oos = new ObjectOutputStream(fout);
			object.setCached(file.getAbsolutePath());
			oos.writeObject(object);
			oos.close();
			fout.close();
		} catch (final FileNotFoundException e) {
			throw new StoringException("Could not create file `" + fileName + "`.", e);
		} catch (final IOException e) {
			throw new StoringException(e.getClass().getSimpleName() + " occurred when trying to write `" + fileName
			        + "`.", e);
		} finally {
			if (oos != null) {
				try {
					oos.close();
				} catch (final IOException ignore) { // ignore
				}
			}
		}
	}
}
