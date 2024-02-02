var isTabActive = true;
//자동로그아웃 경로 표출유무
var isDoLogoutWarningAlert = false;
//이전 interval에 측정된 로그아웃 유예시간
var prevTimeDiff = 0;
//로그아웃 유예시간을 체크하는 interval 객체
var logoutCheck = null;
//로그아웃 예정 시간
var pageLoadDate = new Date();

var commonDeferred = $.Deferred();

$(document).ready(function() {
	$(window).focus(function() {
		isTabActive = true;
	});

	$(window).blur(function() {
		//isTabActive = false;
	});
	
    $(document).click(function() {
    	//클릭하는 행위를 할 시 로그아웃 유예시간 증가
    	pageLoadDate = new Date();
    	isDoLogoutWarningAlert = false;
    });
    
    /*if(isLogin){
    	//interval 시간
    	var delay = 20; 	//자동로그아웃 유예시간(분)
    	var autoLogoutTime = 30;
    	
    	logoutCheck = setInterval(function(){
    		if(isTabActive == false){
    			pageLoadDate.setMilliseconds((new Date()).setMilliseconds(prevTimeDiff * -1) - pageLoadDate);
    			return false;
    		}
        	if(new Date() - pageLoadDate > 1000 * 60 * autoLogoutTime) {
        		//30분간 이용을 하지 않으면 로그아웃
        		alert("You have been automatically logged out due to long periods of inactivity.");
        		clearInterval(logoutCheck);
        		location.href = context+'/user/logout.do';
        	} else if(isDoLogoutWarningAlert == false && (1000 * 60 * autoLogoutTime) - (new Date() - pageLoadDate) < (1000 * 60 * 3) + (delay * 1000)){
        		//일분전 기준에 interval delay시간을 합한값보다(로그아웃까지 최소 3분남았을 시) 클시 유저알림 발생
        		
        		if(confirm("There are 3 minutes left until automatic logout due to long unused use. Press the OK button to extend it.")) {
        			isDoLogoutWarningAlert = false;
        		} else {
        			isDoLogoutWarningAlert = true;
        		}
        	}
        	
        	prevTimeDiff = new Date() - pageLoadDate;
        }, 1000 * delay);
    }*/
    commonDeferred.resolve();
    //우클릭 방지
	document.oncontextmenu = function (e) {
		return false;
	}
});


/************************************************************************
함수명 :                          
설   명 : 사용자 정의 함수 헤더메뉴 드롭다운 이벤트
인   자 : 
사용법 : 'dropdown' 클래스 명시시 사용가능
작성일 : 2022.03.02
작성자 : 이다솔                   
************************************************************************/
(function($) {
    $.fn.dropdown = function() {
        return this.each(function() {
	        var $gnb = $(this);
	        var $menu = $gnb.find(".menu");
	        var $depth1 = $gnb.find(".depth1");
	        var $depth2 = $gnb.find(".depth2");
	        
	        $gnb.mouseenter(function() {
	            gnbOn();
	        }).mouseleave(function() {
	            gnbOff();
	        });
	        
	        $gnb.find("a").focusin(function() {
	            gnbOn();
	        }).focusout(function() {
	            gnbOff();
	        });
	            
	        function gnbOn() {
	            $gnb.find($depth2).stop().animate({height: "200"});
	        }
	        
	        function gnbOff() {
	            $gnb.find($depth2).stop().animate({height: "0"});
	        }

        });
    }
})(jQuery);

$(function() {
	$(".dropdown").dropdown();
});     


/************************************************************************
함수명 : jsp에 직접 div그려서 showLoading , hideLoading 구현                             
설   명 : 페이지 로딩이벤트 메서드
인   자 : 
사용법 : 페이지 로드시 로딩이벤트 동작 (viewIpHeader IN include)
작성일 : 2022.07.11
작성자 : 김영지                   
************************************************************************/

// 페이지 처음시작시 온로드로 호출 로딩 완료후 제거
window.onload = function() { 
	$('.blobsWrap').css('display','none') ;
	$('body').removeClass('blobs') ;  
}; 

function hideLoading() {
	$('.blobsWrap').css('display','none') ;
	$('body').removeClass('blobs') ;  
	$('html, body').removeClass('ScrollDisabled');
}

function showLoading() {
	$('.blobsWrap').css('display','') ;			// css 'display:none' 제거를 하려면 공백선언
	$('body').addClass('blobs') ;  
	$('html, body').addClass('ScrollDisabled');
	
}

/************************************************************************
함수명 : hidePopup                             
설   명 : 팝업 숨기기
인   자 : 팝업 해당 아이디
사용법 : 온클릭 함수로 팝업 해당아이디 매개변수로 넣어줌
작성일 : 2022.03.24 -> 2022.07.20
작성자 : 김영지                   
************************************************************************/
function hidePopup(elementId) {
	$('html, body').removeClass('ScrollDisabled');
	$('#'+elementId).parents('#popFrame'+elementId).remove();	// 팝업 뼈대 div 제거
}
	
/************************************************************************
함수명 : showPopup                          
설   명 : 팝업 띄우기
인   자 : 팝업 해당 아이디
사용법 : url , 팝업해당아이디 , param 값을 넘겨줌
작성일 : 2022.03.24 -> 2022.07.20
작성자 : 김영지                   

************************************************************************/	
function showPopup(url , elementId , params) {
	 $('html, body').addClass('ScrollDisabled');	// 부모페이지 스크롤 방지
   	   
	var popupFrameId = "popFrame"+elementId;	// 팝업 호출 뼈대 div 생성

      if(params != null && params != undefined){
    	  $('.FootWrap').append($('<div id="'+popupFrameId+'">').load(url, params));
      }else{
          $('.FootWrap').append($('<div id="'+popupFrameId+'">').load(url));
      }
}

/************************************************************************
함수명 :                           
설   명 : svg 가입유형 아이콘
인   자 : 
사용법 : 가입유형에 따라 svg아이콘 호출
작성일 : 2022.03.28
작성자 : 김영지                   
************************************************************************/	
var googleIcon = '<svg class="square_google" width="36" height="36"><g fill="#fff" stroke="#dbdbdb"><rect width="36" height="36" rx="4" stroke="none"/><rect x=".5" y=".5" width="35" height="35" rx="3.5" fill="none"/></g><path d="M26.64 18.2a10.347 10.347 0 0 0-.164-1.841H18v3.481h4.844a4.14 4.14 0 0 1-1.8 2.716v2.258h2.909A8.777 8.777 0 0 0 26.64 18.2Z" fill="#4285f4" fill-rule="evenodd"/><path d="M18 27a8.592 8.592 0 0 0 5.956-2.181l-2.909-2.258a5.43 5.43 0 0 1-8.083-2.851H9.957v2.332A9 9 0 0 0 18 27Z" fill="#34a853" fill-rule="evenodd"/><path d="M12.964 19.71a5.321 5.321 0 0 1 0-3.42v-2.332H9.957a9.011 9.011 0 0 0 0 8.084l3.007-2.332Z" fill="#fbbc05" fill-rule="evenodd"/><path d="M18 12.58a4.862 4.862 0 0 1 3.441 1.346l2.581-2.581A8.649 8.649 0 0 0 18 9a9 9 0 0 0-8.043 4.958l3.007 2.332A5.364 5.364 0 0 1 18 12.58Z" fill="#ea4335" fill-rule="evenodd"/><path d="M9 9h18v18H9Z" fill="none"/></svg>'
var naverIcon = '<svg class="square_naver" width="36" height="36"><g fill="#03c75a" stroke="#03ac4e"><rect width="36" height="36" rx="4" stroke="none"/><rect x=".5" y=".5" width="35" height="35" rx="3.5" fill="none"/></g><path d="M20.85 18.491 14.919 10h-4.918v15.866h5.151v-8.492l5.932 8.491h4.917V10H20.85Z" fill="#fff"/></svg>' 
var kakaoIcon = '<svg class="square_kakao" width="36" height="36"><g fill="#fee500" stroke="#dbc600"><rect width="36" height="36" rx="4" stroke="none"/><rect x=".5" y=".5" width="35" height="35" rx="3.5" fill="none"/></g><path d="M19.001 9c-4.971 0-9 3.335-9 7.449a7.191 7.191 0 0 0 3.932 6.155l-1 3.908a.373.373 0 0 0 .564.42l4.377-3.1a10.9 10.9 0 0 0 1.127.061c4.97 0 9-3.335 9-7.449s-4.03-7.449-9-7.449" fill="rgba(0,0,0,0.9)"/></svg>'
var signIcon = '<svg class="square_sign" width="36" height="36"><g fill="#3281df" stroke="#2b6fc0"><rect width="36" height="36" rx="4" stroke="none"/><rect x=".5" y=".5" width="35" height="35" rx="3.5" fill="none"/></g><g fill="#fff"><path d="M11.259 26.585a1 1 0 0 1-1-1v-1.686a4.376 4.376 0 0 1 4.373-4.37h6.743a4.375 4.375 0 0 1 4.37 4.37v1.686a1 1 0 0 1-1 1ZM13.628 13.787a4.376 4.376 0 0 1 4.373-4.37 4.375 4.375 0 0 1 4.37 4.37 4.375 4.375 0 0 1-4.37 4.37 4.376 4.376 0 0 1-4.373-4.37Z"/></g></svg>'

/************************************************************************
함수명 : getParameterByName                          
설   명 : location href param 값 받아오기
인   자 : key-value
사용법 : url뒤에 get방식으로 호출
작성일 : 2022.07.26
작성자 : 김영지                   

************************************************************************/	
function getParameterByName(paramNm) {
	paramNm = paramNm.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
	var regex = new RegExp("[\\?&]" + paramNm + "=([^&#]*)"),
	results = regex.exec(location.search);
	
	return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g," ") );
}

/**********************************************************************
함수명 :                           
설   명 : 현재시간
인   자 : 
사용법 : 현재시간 표출
작성일 : 2022.05.11
작성자 : 이다솔                   
************************************************************************/	


