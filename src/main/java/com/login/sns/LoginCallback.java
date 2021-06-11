package com.login.sns;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.google.api.client.auth.openidconnect.IdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.util.Utils;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;

@Controller
public class LoginCallback {
	
	@RequestMapping("/naver")
	public void naverLoginCallback(HttpServletRequest request, HttpServletResponse response, String code, String state, HttpSession session) throws Exception{
		NaverLoginUtil naverLoginUtil = new NaverLoginUtil();
		
		System.out.println("콜백 진입");
		
		PrintWriter writer = response.getWriter();
		
		int cnt = 0;
		
		OAuth2AccessToken oauthToken = naverLoginUtil.getAccessToken(session, code, state);
		
		//로그인 사용자 정보
		String apiResult = naverLoginUtil.getUserProfile(oauthToken);
		
		//Json 형변환
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = (JSONObject) parser.parse(apiResult);
		
		JSONObject responseObj = (JSONObject) jsonObject.get("response");
		
		// 네이버에서 주는 고유 ID
		String naverIfId = (String)responseObj.get("id");
		System.out.println("고유 ID" + naverIfId);
		// 네이버에서 설정된 사용자 이름
		String naverName = (String)responseObj.get("name");
		System.out.println("이름" + naverName);
		// 네이버에서 설정된 사용자 별명
		String naverNickname = (String)responseObj.get("nickname");
		System.out.println("별명" + naverNickname);
		// 네이버에서 설정된 이메일
		String naverEmail = (String)responseObj.get("email");
		System.out.println("이메일" + naverEmail);
		// 네이버에서 설정된 사용자 프로필 사진
		String naverProfileImage = (String)responseObj.get("profile_image");
		System.out.println("사진" + naverProfileImage);
		
		// 랜덤숫자 자리수 초기화(2자리)
		DecimalFormat decimal2Format = new DecimalFormat("00");
		// 랜덤숫자 자리수 초기화(3자리)
		DecimalFormat decimal3Format = new DecimalFormat("000");
		// 랜덤숫자 자리수 초기화(4자리)
		DecimalFormat decimal4Format = new DecimalFormat("0000");
		
		/*
			// 회원정보 세팅 DB연동시 memberVO나 네이버 회원용 VO에 값 세팅
			AivSignUpVO aivSignUpVO = new AivSignUpVO();
			aivSignUpVO.getAivSignUpBean().setUserName(naverName);
			aivSignUpVO.getAivSignUpBean().setUserEmail(naverEmail);
			aivSignUpVO.getAivSignUpBean().setUserProfileUrl(naverProfileImage);
			aivSignUpVO.getAivSignUpBean().setUserIfInfo("NAVER");
			aivSignUpVO.getAivSignUpBean().setUserIfId(naverIfId);
			
			
			// 네이버에서 주는 고유 ID의 중복여부 체크(AIV_EMPLOYEE 테이블의 USER_IF_ID의 중복체크)
			제공하는 서비스에서 사용하는 DB에서 회원이 존재하는지 체크
			int existsUserIfIdCnt = aivSignUpService.selectExistsUserIfId(aivSignUpVO);
		 */
		boolean emailExists = false;
		
		//위에서 중복 id가 없을시는 int 가 0 이므로 if문에 삽입 테스트용으로 true 값을 줌
		//회원가입
		if(true) {
			//이메일 중복체크 왜하지?
			//int existEmailCnt = aivSignUpService.selectExistsUserEmail(aivSignUpVO);
			//if(existEmailCnt > 0) {
			if(false) {
				emailExists = true;
			}else {
				/*	
				 	// 네이버에서 주는 고유 ID 세팅
					aivSignUpVO.getAivSignUpBean().setUserId(naverIfId);
				 * */
				// USER_ID의 중복된 값이 있을 경우 반복문을 통하여 랜덤수를 뒤에 부여한 뒤 USER_ID를 새로 세팅해 줌
				for(int i = 0; i < 10; i++) {
					if(i > 0) {
						// USER_ID가 중복된 경우 랜덤수 두 자리를 뒤에 부여해줌
						naverIfId = naverIfId + "_" + decimal2Format.format(Math.random() * 99);
						//aivSignUpVO.getAivSignUpBean().setUserId(naverIfId);
					}
					// USER_ID 중복 여부 체크
					//int existsUserIdCnt = aivSignUpService.selectExistsUserId(aivSignUpVO);
					
					// USER_ID 중복이 안 될 경우 USER_NICK_NAME의 중복을 체크하기 위하여 아래 구문을 실행
					//if(existsUserIdCnt == 0) {
					if(true) {
						//따로 구현한 금지어 목록 체크하는 클래스인듯
						/* boolean nickChk = StringUtil.checkNickName(naverNickname); */
						// USER_NICK_NAME이 사용 불가능한 단어가 포함되어 있으면 닉네임_1234(랜덤숫자 4자리를 뒤에 붙임)으로 세팅시켜 줌
						//if(!nickChk) {
						//	naverNickname = "닉네임_" + decimal4Format.format(Math.random() * 9999);
						//}
						//aivSignUpVO.getAivSignUpBean().setUserNickName(naverNickname);
						//이후로 반복을 통해 사용 불가능한 닉네임을 변경한 것이 기존의 닉네임이랑 충돌하는지 테스트 코드
						// 기본 데이터 입력(EMP_ID 새로 생성처리)
						//aivSignUpVO.getAivSignUpBean().setEmpId(aivSignUpVO.getAivSignUpBean().ID_PREFIX + StringUtil.generateKey());
						// 회원데이터 저장
						//cnt = aivSignUpService.insertIFSignUpSave(aivSignUpVO);
					}
				}
			}
		}
		if(!emailExists) {
			System.out.println("로그인 로직");
		}
	}
	
	/**
	 * Authentication Code를 전달 받는 엔드포인트
	 **/
	@RequestMapping(value = "/google", method = RequestMethod.POST)
	public void googleLoginCallBack(Model model, String idtoken) throws Exception{
		System.out.println(idtoken);
		
		HttpTransport transport = Utils.getDefaultTransport();
		JsonFactory jsonFactory = Utils.getDefaultJsonFactory();
		// 백엔드에 액세스하는 앱의 CLIENT_ID를 지정합니다.
		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
				.setAudience(Collections.singletonList("696747606302-i83ht0g77q059bjba5et37kgr4e3gptj.apps.googleusercontent.com")).build();
		// 또는 여러 클라이언트가 백엔드에 액세스하는 경우 :
	    //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
		
		JSONObject json = new JSONObject();
		
		// (HTTPS POST로 idTokenString 수신)
		GoogleIdToken idToken = verifier.verify(idtoken);
		if (idToken != null) {
			Payload payload = idToken.getPayload();
			
			// Print user identifier
			String userId = payload.getSubject();
			System.out.println("User ID: " + userId);
			
			// Get profile information from payload
			String email = (String) payload.get("email");
			System.out.println(email);
			String name = (String) payload.get("name");
			String pictureUrl = (String) payload.get("picture");
			String locale = (String) payload.get("locale");
			String familyName = (String) payload.get("family_name");
			String givenName = (String) payload.get("given_name");

			  // Use or store profile information
			  // ...

			/*
			if (dupId((String) payload.get("email")).contains("false")) { //회원가입이 안 되어 있는 경우 로직을 작성해서 DB에 넣자
				SocialJoinVO sjVO = new SocialJoinVO();
				sjVO.setId((String) payload.get("email"));
				sjVO.setAuth_email((String) payload.get("email"));
				sjVO.setNickname((String) payload.get("given_name"));
				sjVO.setBlog_name((String) payload.get("given_name"));
				sjVO.setProfile_img((String) payload.get("picture"));
				sjVO.setPlatform("google");
				sjVO.setAccess_token(idtoken);
				
				new MemberService().googleJoin(sjVO);
			}//end if
			*/
			
			model.addAttribute("id", (String) payload.get("email"));
			json.put("login_result", "success");
			//토근 및 DB에 사용자가 있는지 확인 후 세션 생성
		} else { //유효하지 않은 토큰
			json.put("login_result", "fail");
		}//end else
		/* return json.toJSONString(); */
	}
	
	@RequestMapping(value = "/kakao", produces = "application/json", method = { RequestMethod.GET, RequestMethod.POST })
	public void kakaoLogin(String code, HttpSession session) throws Exception{
		KakaoLoginApi kakao = new KakaoLoginApi();
		
		JsonNode node = kakao.getAccessToken(code);
		//토큰에 사용자 정보가 있음
		JsonNode accessToken = node.get("access_token");
		//사용자 정보
		JsonNode userInfo = kakao.getKakaoUserInfo(accessToken);
		
		JsonNode properties = userInfo.path("properties");
		JsonNode kakao_account = userInfo.path("kakao_account");
		
		System.out.println(kakao_account.path("email").asText());
		System.out.println(properties.path("nickname").asText());
		System.out.println(kakao_account.path("gender").asText());
		System.out.println(kakao_account.path("birthday").asText());
		System.out.println(kakao_account.path("age_range").asText());
		
	}
}
