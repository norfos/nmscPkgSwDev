<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%> 
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%> 
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge" charset=utf-8>
    <meta name="viewport" content="width=1500">
	<title>NMSC</title>
	<link href="https://webfontworld.github.io/kopus/KoPubWorldDotum.css" rel="stylesheet">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resource/css/style.css" type="text/css">
	<link rel="icon" href="${pageContext.request.contextPath}/resource/images/common/favicon.ico" type="image/x-icon" sizes="16x16">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resource/css/jquery-ui.min.css" type="text/css">
	<script src="${pageContext.request.contextPath}/resource/js/jquery-3.6.0.min.js"></script>
	<script src="${pageContext.request.contextPath}/resource/js/common/common.js"></script> 
	<script src="${pageContext.request.contextPath}/resource/js/jquery-ui.min.js"></script>
</head>
<body>
	<div class="HeaderWrap">
	    
	    <div class="MenuHeader">
	        <div class="Menu" style="justify-content: center;">
	            <a href="/main/main.do">
	                <!-- <img src="/images/nmscHeader.png" style="height:170px; width:132rem;" alt="NMSC Logo"/> -->
	                <img src="${pageContext.request.contextPath}/resource/Images/nmscHeader.png" style="height:170px; width:132rem;" alt="NMSC Logo"/>
	            </a>
	            
	        </div>
	    </div>
	</div>
<script>
$(document).ready(function() {
	var adminYn = '${adminYn}';
	console.log('adminYn : '+adminYn);
	if(adminYn == 'Y'){
		console.log('ok!!!');
		$('#dmDiv').css('display','block');
		//$("#dmDiv").show();
	}
});
</script>

<div class="MainWrap">
    

    <div class="Main">
        <div class="Box">
        	<a href="${pageContext.request.contextPath}/board/viewDataList.do">
            <p>
                &#183; Real-Time Data
            </p>
            <div class="Spectrum"></div>
            </a>
        </div>
        <div id="dmDiv" class="Box" style="display:none;">
            <a href="${pageContext.request.contextPath}/admin/viewDataMngList.do">
            <p>
                &#183; Data Management
            </p>
            <div class="Flux"></div>
			</a>
        </div>
        
    </div>
</div>

</body>
</html>