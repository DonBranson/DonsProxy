/*
 * Created on Mar 4, 2008
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

package com.moneybender.proxy.analyzers;

class RequestInfo implements Comparable<RequestInfo> {

	private String request;
	private String cachingPolicy;
	private String contentType;
	private long responseTime;
	private int requests;

	public RequestInfo(String request){
		this.request = request;
		this.cachingPolicy = null;
		this.contentType = null;
		this.responseTime = 0;
	}
	
	public String getRequest() {
		return request;
	}

	public String getCachingPolicy() {
		return cachingPolicy;
	}

	public void setCachingPolicy(String cachingPolicy) {
		if(this.cachingPolicy == null) {
			this.cachingPolicy = cachingPolicy;
		} else {
			if(this.cachingPolicy.indexOf(cachingPolicy) < 0) {
				this.cachingPolicy += "; " + cachingPolicy;
			}
		}
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public long getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(long responseTime) {
		this.responseTime += responseTime;
		++requests;
	}

	public long getAverageResponseTime() {
		if(requests == 0)
			return 0;
		return responseTime / requests;
	}

	protected int getRequestCount() {
		return requests;
	}

	public int compareTo(RequestInfo other) {
		return request.compareTo(other.getRequest());
	}

}
