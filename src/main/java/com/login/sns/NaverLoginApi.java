package com.login.sns;

import com.github.scribejava.core.builder.api.DefaultApi20;

//URL 반환 클래스인듯 나중에 공부 필요
public class NaverLoginApi extends DefaultApi20{
	
	protected NaverLoginApi() {}
	
	private static class InstanceHolder {
		private static final NaverLoginApi INSTANCE = new NaverLoginApi();
	}
	
	public static NaverLoginApi instance() {
		return InstanceHolder.INSTANCE;
	}
	
	@Override
	public String getAccessTokenEndpoint() {
		return "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code";
	}

	@Override
	protected String getAuthorizationBaseUrl() {
		return "https://nid.naver.com/oauth2.0/authorize";
	}

}
