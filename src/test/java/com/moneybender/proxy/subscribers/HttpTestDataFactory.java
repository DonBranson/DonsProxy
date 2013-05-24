/*
 * Created on Jan 19, 2008
 *
 * Copyright (c), 2008 Don Branson.  All Rights Reserved.
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

package com.moneybender.proxy.subscribers;

class HttpTestDataFactory {

	private String[] request = {
		"GET http://www.google.com/ HTTP/1.1",
		"Host: www.google.com",
		"User-Agent: Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.3) Gecko/20070309 Firefox/2.0.0.3",
		"Accept: text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5",
		"Accept-Language: en-us,en;q=0.5",
		"Accept-Encoding: gzip,deflate",
		"Accept-Charset: ISO-8859-15,utf-8;q=0.7,*;q=0.7",
		"Keep-Alive: 300",
		"Proxy-Connection: keep-alive",
		"Cookie: PREF=ID=6381c71325420e36:FF=4:TB=2:LD=en:NR=10:TM=1144975630:LM=1174609846:DV=AA:GM=1:S=qW8grgABSJjd8WIe; SID=DQAAAHMAAAB-GDt0qg-vb8cj7H0PJU81vf8nH8zhA5cPEnkuL7L0cXwQ2WeiL5eum2gYkCuOdMmt5XYHjNmDfhfpBirMfGFYEoHI-kyPrRXtKeSRSSz_yw92S-T0dnkuKFsWrqI6FlUxfNYwLNpY5zuM_9w6Q5rrowOioAJ3riYV0I2318MXgw; rememberme=true; S=gmail=JJAjZaN7bfRp_2FzITtEFQ:gmail_yj=MuOkYUUir1W7wGFygiV0gw:gmproxy=--rtf3i_SsQ:gmproxy_yj=DhdVRcpbfJw:gmproxy_yj_sub=oM9C7fLJZ6Q; TZ=300",
		"Pragma: no-cache",
		"Cache-Control: no-cache",
		""
	};

	private String[] responseHeaders = {
		"HTTP/1.1 200 OK is what I say",
		"Cache-Control: private",
		"Content-Type: text/html; charset=ISO-8859-1",
		"Set-Cookie: PREF=ID=a6cfe5807c57859e:TM=1201359515:LM=1201359515:S=DlYtteqzlxwTGgYs; expires=Mon, 25-Jan-2010 14:58:35 GMT; path=/; domain=.google.com",
		"Server: gws",
		"Transfer-Encoding: chunked",
		"Date: Sat, 26 Jan 2008 14:58:35 GMT",
		""
	};

	private String[] responseBody = {
		"Lots of really useful stuff",
	};
	
	private String[] postRequestStart = {
		"POST /mail/channel/ HTTP/1.1",
		"Host: mail.google.com",
		"User-Agent: Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.9) Gecko/20071025 Firefox/2.0.0.9 Creative ZENcast v1.02.12",
		"Accept: text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5",
		"Accept-Language: en-us,en;q=0.5",
		"Accept-Encoding: gzip,deflate",
		"Accept-Charset: ISO-8859-15,utf-8;q=0.7,*;q=0.7",
		"Keep-Alive: 300",
		"Connection: keep-alive",
		"Content-Type: application/x-www-form-urlencoded",
		"Referer: https://mail.google.com/mail/?ui=2&view=js&name=js&ids=yada",
	};
	
	private String[] postRequestFinish = {
		"Content-Length: 35",
		"Cookie: __utmz=yada",
		"Pragma: no-cache",
		"Cache-Control: no-cache",
		""
	};
	
	private String[] postRequestPayload = {
		"count=1&req0_type=cf&req0_focused=0",
	};


	protected String getLineSeparator() {
		return System.getProperty("line.separator", "\n");
	}
	
	protected String getPostRequest() {
		String request = addLineSeparators(postRequestStart);
		request += addLineSeparators(postRequestFinish);
		request += addLineSeparators(postRequestPayload);
		return request;
	}
	
	protected String getPostRequestNeat() {
		String request = addLineSeparators(postRequestStart);
		request += addLineSeparators(postRequestFinish);
		return request;
	}
	
	protected String getPostRequestStart() {
		return addLineSeparators(postRequestStart);
	}
	
	protected String getPostRequestFinish() {
		return addLineSeparators(postRequestFinish);
	}
	
	protected String getPostRequestPayload() {
		return addLineSeparators(postRequestPayload);
	}
	
	protected int getPostRequestLineCount() {
		return postRequestStart.length + postRequestFinish.length;
	}
	
	protected String getGetRequest() {
		return addLineSeparators(request);
	}
	
	protected int getGetRequestLineCount() {
		return request.length;
	}
	
	protected String getGetRequestStart() {
		String[] requestStart = new String[3];
		for (int i = 0; i < requestStart.length; i++) {
			requestStart[i] = request[i];
		}
		return addLineSeparators(requestStart);
	}

	protected String getGetRequestFinish() {
		String[] requestFinish = new String[request.length - 3];
		for (int i = 0; i < requestFinish.length; i++) {
			requestFinish[i] = request[i + 3];
		}
		return addLineSeparators(requestFinish);
	}
	
	protected String getConnectRequest() {
		String[] request = {
			"CONNECT mail.google.com:443 HTTP/1.1",
			"User-Agent: Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.3) Gecko/20070309 Firefox/2.0.0.3",
			"Proxy-Connection: keep-alive",
			"Host: mail.google.com",
			""
		};
		
		return addLineSeparators(request);
	}

	protected String getOkayResponse() {
		String response = addLineSeparators(responseHeaders);
		response += addLineSeparators(responseBody);
		return response;
	}
	
	protected int getOkayResponseLineCount() {
		return responseHeaders.length;
	}

	protected String getOkayResponseStart() {
		return addLineSeparators(responseHeaders);
	}
	
	protected String getOkayResponseFinish() {
		return addLineSeparators(responseBody);
	}
	
	protected String getGenericPayload() {
		String[] payload = {
			"payload"
		};

		return addLineSeparators(payload);
	}
	
	protected String[] getKnownHeaderExamples() {
		String[] allHandledHeaders = {
			"GET yadayadayada",
			"HEAD yadayadayada",
			"POST yadayadayada",
			"PUT yadayadayada",
			"DELETE yadayadayada",
			"TRACE yadayadayada",
			"HTTP yadayadayada",
			"HTTP/1.1 yadayadayada",
			"OPTIONS yadayadayada",
			"CONNECT yadayadayada",
			"DELETE yadayadayada",
			"TRACE yadayadayada"
		};

		return allHandledHeaders;
	}
	
	protected String[] getNonHeaderExamples() {
		String[] notHeaders = {
			"notaheader",
			"alsonotaheader",
			"GIT yadayadayada",
			"GETS yadayadayada"
		};

		return notHeaders;
	}
	
	private String addLineSeparators(String[] request) {
		
		String lineSeparator = getLineSeparator();

		String newRequest = "";
		for (int line = 0; line < request.length; line++) {
			newRequest += request[line];
			newRequest += lineSeparator;
		}
		
		return newRequest;
	}

}
