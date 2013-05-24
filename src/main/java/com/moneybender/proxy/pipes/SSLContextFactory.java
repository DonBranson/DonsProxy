/*
 * Created on Jun 24, 2007
 * 
 * Copyright (c), 2007 Don Branson.  All Rights Reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.moneybender.proxy.pipes;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SSLContextFactory {

	private static SSLContext _instance = null;
	
	private SSLContextFactory(){}

	public static synchronized SSLContext getInstance() throws IOException {
		
		if(_instance != null) {
			return _instance;
		}

		try {
			KeyStore keystore = KeyStore.getInstance("JKS");
			char[] passphrase = "password".toCharArray();

			InputStream keystoreIn = Thread.currentThread().getContextClassLoader().getResourceAsStream("keystore");
			keystore.load(keystoreIn, passphrase);

			KeyManagerFactory keyManagerfactory = KeyManagerFactory.getInstance("SunX509");
			keyManagerfactory.init(keystore, passphrase);

			TrustManager[] iTrustEveryone = new TrustManager[] {
				new X509TrustManager(){

					public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					}

					public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					}

					public X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					
				}
			};

			_instance = SSLContext.getInstance("TLS");
			_instance.init(keyManagerfactory.getKeyManagers(), iTrustEveryone, null);

		} catch (GeneralSecurityException e) {
			throw new IOException(e);
		}

		return _instance;
	}
	
}
