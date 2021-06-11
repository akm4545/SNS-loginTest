package com.login.sns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KakaoLoginApi {
	private final static String K_CLIENT_ID = "9804053c970b508fadc3297270e68484";
	private final static String K_REDIRECT_URI = "http://localhost:9090/sns/kakao";
	
	//인증 인가 코드 받기 추후 토큰 요청에 쓰임?
	public static String getAuthorizationUrl(HttpSession session) {
		String kakaoUrl = "https://kauth.kakao.com/oauth/authorize?" + "client_id=" + K_CLIENT_ID + "&redirect_uri=" + K_REDIRECT_URI + "&response_type=code";
		return kakaoUrl;
	}
	
	//인증 토큰 발급
	public static JsonNode getAccessToken(String authorize_code) {
		final String RequestUrl = "https://kauth.kakao.com/oauth/token";
		final List<NameValuePair> postParams = new ArrayList<NameValuePair>();
		
		postParams.add(new BasicNameValuePair("grant_type", "authorization_code"));
		postParams.add(new BasicNameValuePair("client_id", "9804053c970b508fadc3297270e68484"));
		postParams.add(new BasicNameValuePair("client_secret", "8NPGC2G1LrwdXEVYAO0qKyrn8vzYOC8q"));
		postParams.add(new BasicNameValuePair("redirect_uri", "http://localhost:9090/sns/kakao"));
		postParams.add(new BasicNameValuePair("code", authorize_code));
		
		final HttpClient client = HttpClientBuilder.create().build();
		final HttpPost post = new HttpPost(RequestUrl);
		JsonNode returnNode = null;
		
		try {
			post.setEntity(new UrlEncodedFormEntity(postParams));
			final HttpResponse response = client.execute(post);
			
			ObjectMapper mapper = new ObjectMapper();
			returnNode = mapper.readTree(response.getEntity().getContent());			
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		return returnNode;
	}
	
	public static JsonNode getKakaoUserInfo(JsonNode accessToken) {
		final String RequestUrl = "https://kapi.kakao.com/v2/user/me";
		final HttpClient client = HttpClientBuilder.create().build();
		final HttpPost post = new HttpPost(RequestUrl);
		
		post.addHeader("Authorization", "Bearer " + accessToken);
		JsonNode returnNode = null;
		try {
			final HttpResponse response = client.execute(post);
			ObjectMapper mapper = new ObjectMapper(); 
			returnNode = mapper.readTree(response.getEntity().getContent());
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		return returnNode;
	}
}
