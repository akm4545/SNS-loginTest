package com.login.sns;

import java.io.IOException;
import java.util.UUID;

import javax.annotation.Generated;
import javax.servlet.http.HttpSession;

import org.springframework.util.StringUtils;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

public class NaverLoginUtil {
	/* 인증 요청문을 구성하는 파라미터 */
	//client_id: 애플리케이션 등록 후 발급받은 클라이언트 아이디
	//response_type: 인증 과정에 대한 구분값. code로 값이 고정돼 있습니다.
	//redirect_uri: 네이버 로그인 인증의 결과를 전달받을 콜백 URL(URL 인코딩). 애플리케이션을 등록할 때 Callback URL에 설정한 정보입니다.
	//state: 애플리케이션이 생성한 상태 토큰
	private final static String CLIENT_ID = "4nczPT1urHFL_bkxPGuA";
	private final static String CLIENT_SECRET = "4uCobNpwr5";
	private final static String REDIRECT_URI = "http://localhost:9090/sns/naver";
	private final static String SESSION_STATE = "oauth_state";
	
	/* 프로필 조회 API URL */
	private final static String PROFILE_API_URL = "https://openapi.naver.com/v1/nid/me";
	
	/* 네이버 아이디로 인증 URL 생성 method */
	public String getAuthorizationUrl(HttpSession session) {
		
		/* 세션 유효성 검증 난수 생성*/
		String state = UUID.randomUUID().toString();
		
		/* 세션 유효성 난수 저장 */
		session.setAttribute(SESSION_STATE, state);
		
		/* Scribe에서 제공하는 인증 URL 생성 기능을 이용하여 네아로 인증 URL 생성  = 추후 공부 
			사용자가 가입된 서비스의 API에 접근하기 위해서는 사용자로부터 권한을 위임 받아야 합니다. 이 때 사용자의 패스워드 없이도 권한을 위임 받을 수 있는 방법이 필요합니다. 
			이를 위해서 고안된 기술이 OAuth입니다. 
			오늘날 많은 API들이 OAuth를 통해서 상호 연동을 지원하고 있습니다. 
		 * */
		OAuth20Service oauthService = new ServiceBuilder()
				.apiKey(CLIENT_ID)
				.apiSecret(CLIENT_SECRET)
				.callback(REDIRECT_URI)
				.state(state) //난수값을 인증 URL 생성시 사용
				.build(NaverLoginApi.instance());
		
		System.out.println("네이버 아이디 인증 URL" + oauthService.getAuthorizationUrl());
		return oauthService.getAuthorizationUrl();
	}
	
	/* 네이버 아이디로 callback 처리 및 AccessToken 획득 method */
	public OAuth2AccessToken getAccessToken(HttpSession session, String code, String state) throws IOException{
		/* callback으로 전달받은 세션검증용 난수값과 세션에 저장되어있는 값이 일치하는지 확인 */
		String sessionState = (String)session.getAttribute(SESSION_STATE);
		
		/*StringUtils - 자바의 String 클래스가 제공하는 문자열 관련 기능을 강화한 클래스.
			pathEquals 경로 비교 메서드인듯
		 * */
		if(StringUtils.pathEquals(sessionState, state)) {
			OAuth20Service oauthService = new ServiceBuilder()
					.apiKey(CLIENT_ID)
					.apiSecret(CLIENT_SECRET)
					.callback(REDIRECT_URI)
					.state(state)
					.build(NaverLoginApi.instance());
			
			/* Scribe에서 제공하는 AccessToken 획득 기능으로 네아로 Access Token 생성*/
			System.out.println("토큰 발급 " + oauthService.getAuthorizationUrl());
			OAuth2AccessToken accessToken = oauthService.getAccessToken(code);
			
			return accessToken;
		}
		
		return null;
	}

	/* Access Token을 이용하여 네이버 사용자 프로필 API를 호출 */
	public String getUserProfile(OAuth2AccessToken oauthToken) throws IOException{
		OAuth20Service oauthService = new ServiceBuilder()
				.apiKey(CLIENT_ID)
				.apiSecret(CLIENT_SECRET)
				.callback(REDIRECT_URI)
				.build(NaverLoginApi.instance());
		
		//전송방식, url, 토큰 검사 순으로 넣는듯함
		OAuthRequest request = new OAuthRequest(Verb.GET, PROFILE_API_URL, oauthService);
		//쿼리 파라미터에 추가?
		oauthService.signRequest(oauthToken, request);
		//응답을 받아옴
		Response response = request.send();
		
		System.out.println("프로필" + oauthService.getAuthorizationUrl());
		System.out.println(response.getBody().toString());
		return response.getBody();
	}
	
}
