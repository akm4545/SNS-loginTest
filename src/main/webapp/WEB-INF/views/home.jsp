<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>LoginTest</title>
	<!-- google-signin-client_id 메타 요소를 사용하여 Google Developers Console에서 앱용으로 만든 클라이언트 ID를 지정합니다. -->
	<meta name="google-signin-client_id" content="696747606302-i83ht0g77q059bjba5et37kgr4e3gptj.apps.googleusercontent.com">
	<!-- 네이버용 javascript라이브러리 -->
	<script type="text/javascript" src="https://static.nid.naver.com/js/naverLogin_implicit-1.0.2.js" charset="utf-8"></script>
	<!-- Google 로그인을 통합하는 웹 페이지에 Google Platform 라이브러리를 포함해야합니다. -->
	<script src="https://apis.google.com/js/platform.js" async defer></script>
	<script type="text/javascript" src="http://code.jquery.com/jquery-1.11.3.min.js"></script>
	<style type="text/css">
	  html, div, body,h3{
	  	margin: 0;
	  	padding: 0;
	  }
	  h3{
	  	display: inline-block;
	  	padding: 0.6em;
	  }
	</style>
</head>
<body>
	<div style="background-color:#15a181; width: 100%; height: 50px;text-align: center; color: white; "><h3>SIST Login</h3></div>
	<br>
	<!-- 네이버 로그인 화면으로 이동 시키는 URL -->
	<!-- 네이버 로그인 화면에서 ID, PW를 올바르게 입력하면 callback 메소드 실행 요청 -->
	<div id="naver_id_login" style="text-align:center">
		<a href="${url}">
			<img width="223" src="${pageContext.request.contextPath}/resources/img/test.png"/>
		</a>
		<div class="g-signin2" data-onsuccess="onSignIn"></div>
	</div>
	<fieldset>
		<label>로그인</label> <br>
		<div id="googleLoginBtn" style="cursor: pointer">
			<img id="googleLoginImg" src="${pageContext.request.contextPath}/resources/img/test.png">
		</div>
	</fieldset>
	<br>
	<script>
		/* 구글 프로필 정보 얻기 */
		function onSignIn(googleUser) {
			var profile = googleUser.getBasicProfile();
			//정보를 잘 가져오는지 테스트로 찍어보는 코드 
			console.log('ID: ' + profile.getId()); // Do not send to your backend! Use an ID token instead.
			console.log('Name: ' + profile.getName());
			console.log('Image URL: ' + profile.getImageUrl());
			console.log('Email: ' + profile.getEmail()); // This is null if the 'email' scope is not present.
			var id_token = googleUser.getAuthResponse().id_token;
			console.log(id_token);
			
			//비동기 통신으로 토큰 보내기
			var xhr = new XMLHttpRequest();
			xhr.open('POST', 'http://localhost:9090${pageContext.request.contextPath}/google');
			xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
			xhr.onload = function() {
			  console.log('Signed in as: ' + xhr.responseText);
			};
			xhr.send('idtoken=' + id_token);
		}
		
		/* 작동 안되는데?  
		function onSignIn(googleUser) {
			var profile = googleUser.getBasicProfile();
			var id_token = googleUser.getAuthResponse().id_token;
			$("#googleBtn").click(function(){
			 	$.ajax({
			  		url: '승인된 리디렉션 URI',
			  		type: 'POST',
			  		data: 'idtoken=' + id_token, 
			  		dataType: 'JSON',
			  		beforeSend : function(xhr){
				  		xhr.setRequestHeader("Content-type","application/x-www-form-urlencoded");
			  	},
			  	success: function(json) {
					if (json.login_result == "success"){
						location.href = "로그인 완료 후 이동할 main 주소";
				  	}//end if
	          	}//success
		 	});//ajax
		});//click
	}
		*/
		
		const googleLoginBtn = document.getElementById("googleLoginBtn");
		googleLoginBtn.addEventListener("click", onClickGoogleLogin);
		
		/* 로그아웃 함수 아무곳이나 엮어주면 된다 */
		function signOut() {
			var auth2 = gapi.auth2.getAuthInstance();
			auth2.signOut().then(function () {
			  console.log('User signed out.');
			});
		}
	</script>
</body>
</html>

