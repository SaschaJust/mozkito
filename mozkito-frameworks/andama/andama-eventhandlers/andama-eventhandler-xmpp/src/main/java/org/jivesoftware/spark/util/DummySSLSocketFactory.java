/**
 * $RCSfile: ,v $ $Revision: $ $Date: $
 * 
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.jivesoftware.spark.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * An SSL socket factory that will let any certifacte past, even if it's expired or not singed by a root CA.
 */
public class DummySSLSocketFactory extends SSLSocketFactory {
	
	public static synchronized SocketFactory getDefault() {
		return new DummySSLSocketFactory();
	}
	
	private SSLSocketFactory factory;
	
	public DummySSLSocketFactory() {
		
		try {
			final SSLContext sslcontent = SSLContext.getInstance("TLS");
			sslcontent.init(null, // KeyManager not required
			                new TrustManager[] { new DummyTrustManager() }, new java.security.SecureRandom());
			this.factory = sslcontent.getSocketFactory();
		} catch (final NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (final KeyManagementException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Socket createSocket(final InetAddress inaddr,
	                           final int i) throws IOException {
		return this.factory.createSocket(inaddr, i);
	}
	
	@Override
	public Socket createSocket(final InetAddress inaddr,
	                           final int i,
	                           final InetAddress inaddr2,
	                           final int j) throws IOException {
		return this.factory.createSocket(inaddr, i, inaddr2, j);
	}
	
	@Override
	public Socket createSocket(final Socket socket,
	                           final String s,
	                           final int i,
	                           final boolean flag) throws IOException {
		return this.factory.createSocket(socket, s, i, flag);
	}
	
	@Override
	public Socket createSocket(final String s,
	                           final int i) throws IOException {
		return this.factory.createSocket(s, i);
	}
	
	@Override
	public Socket createSocket(final String s,
	                           final int i,
	                           final InetAddress inaddr,
	                           final int j) throws IOException {
		return this.factory.createSocket(s, i, inaddr, j);
	}
	
	@Override
	public String[] getDefaultCipherSuites() {
		return this.factory.getSupportedCipherSuites();
	}
	
	@Override
	public String[] getSupportedCipherSuites() {
		return this.factory.getSupportedCipherSuites();
	}
}

/**
 * Trust manager which accepts certificates without any validation except date validation.
 */
class DummyTrustManager implements X509TrustManager {
	
	@Override
	public void checkClientTrusted(final X509Certificate[] x509Certificates,
	                               final String s) throws CertificateException {
		// Do nothing for now.
	}
	
	@Override
	public void checkServerTrusted(final X509Certificate[] x509Certificates,
	                               final String s) throws CertificateException {
		// Do nothing for now.
	}
	
	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[0];
	}
	
	public boolean isClientTrusted(final X509Certificate[] cert) {
		return true;
	}
	
	public boolean isServerTrusted(final X509Certificate[] cert) {
		try {
			cert[0].checkValidity();
			return true;
		} catch (final CertificateExpiredException e) {
			return false;
		} catch (final CertificateNotYetValidException e) {
			return false;
		}
	}
}
