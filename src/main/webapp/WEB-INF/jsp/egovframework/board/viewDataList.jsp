<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge" charset=utf-8>
    <meta name="viewport" content="width=1500">
	<title>NMSC</title>
	<link href="https://webfontworld.github.io/kopus/KoPubWorldDotum.css" rel="stylesheet">
	<link rel="stylesheet" href="/resource/css/style.css" type="text/css">
	<link rel="icon" href="/resource/images/common/favicon.ico" type="image/x-icon" sizes="16x16">
	<link rel="stylesheet" href="/resource/css/jquery-ui.min.css" type="text/css">
	<script src="/resource/js/jquery-3.6.0.min.js"></script>
	<script src="/resource/js/common/common.js"></script> 
	<script src="/resource/js/jquery-ui.min.js"></script>
	

<script type="text/javascript">
$(document).ready(function() {
	// DatePicker 초기화
	$('.searchOptionDate').datepicker({
		dateFormat : 'yy-mm-dd',
		startDate: '-10y',		//달력에서 사용가능한 가장 빠른날짜. 이전날짜 선택 불가능
		showOtherMonths: true , //빈 공간에 현재월의 앞뒤월의 날짜를 표시
        showMonthAfterYear:true , // 월- 년 순서가아닌 년도 - 월 순서
	    autoclose: true,
	    todayHighlight: true,
	    showWeekDays : true,
	    changeYear: true,
	    changeMonth: true,
	    closeText: "닫기",
	    currentText: "오늘",
	    prevText: '이전 달',
	    nextText: '다음 달',
	    monthNames: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'],
	    monthNamesShort: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'],
	    dayNames: ['일', '월', '화', '수', '목', '금', '토'],
	    dayNamesShort: ['일', '월', '화', '수', '목', '금', '토'],
	    dayNamesMin: ['일', '월', '화', '수', '목', '금', '토'],
	    weekHeader: "주",
	});
	
	// 초기 날짜/시간 세팅	
	$('#sdate').datepicker('setDate', '-1M'); //(-1D:하루전, -1M:한달전, -1Y:일년전), (+1D:하루후, -1M:한달후, -1Y:일년후)
	$('#edate').datepicker('setDate', 'today');     
	$("#ehour").val("23").prop("selected", true);
	$("#emin").val("59").prop("selected", true);
	$("#esec").val("59").prop("selected", true);
	
	// 전체선택 체크시 비활성화 체크박스제외 활성화 처리
	$('input[name=selectall]').click(function(){
		var checked = $('input[name=selectall]').is(':checked');
		if(checked) {
			$('input:checkbox:not(:disabled)').prop('checked',true);
		} else {
			$('input:checkbox:not(:disabled)').prop('checked',false);
		}
	});
	
	// 전체선택후 체크박스 클릭시 전체선택 해제-활성화 처리
	$(document).on('click', 'input[name=seq]', function(){
    	if($('input[name=seq]:checkbox:checked').length == $("input[name=seq]").length) {
			$("input[name=selectall]").prop("checked",true);
		} else {
			$("input[name=selectall]").prop("checked",false);
		}
	});
	
	// 출력건수
	$('.SortingView').change(function() {
		pagePerRow = $('#pagePerRow').val();
		selectNoticeList(1);
	});
	
	$(document).on('click','img',function(){
		$('#imgDiv').hide();
		$('#main').show();
	});
	
	
	selectNoticeList(1);
	
});

	var pagePerRow = "10";	//페이징 갯수
	
	//notice list
	function selectNoticeList(page){
		var data = {};
		data.dataArea = $('#dataArea').val();
		data.sdate = $('#sdate').val()+' '+$('#shour').val()+':'+$('#smin').val()+':'+$('#ssec').val();
		data.edate = $('#edate').val()+' '+$('#ehour').val()+':'+$('#emin').val()+':'+$('#esec').val();
		data.currentPage = page;
		data.pagePerRow = pagePerRow;
		console.log(data);
		
		$.ajax({
			url : '/board/selectDataList.do',   
			type : 'post',
			data : JSON.stringify(data),
			contentType: 'application/json',
			dataType : 'json',
			success : function(result){
		        try{
			    	tbody = $("#NoticeList tbody");
			        tbody.children().remove();
					if(result.boardList.length > 0){
			        	$(result.boardList).each(function(index, item){
			        		var html = "";
							html += '<tr>';
							html += '<td> <input type="checkbox" value = "'+item["FILE_PATH"]+','+item["FILE_PTN"] + '" name="seq" /> </td>';
							html += '<td>'+item["MNT_TIME"]+'</td>';
							html += '<td>'+item["DATA_AREA"]+'</td>';
							html += '<td>'+item["DOWN_SIZE"]+'KB</td>';
							html += '<td><a href="/image/view?filePath='+item["FILE_PATH"]+'&fileName='+item["FILE_PTN"]+'" target="_blank">'+item["FILE_PTN"]+'</a></td>';
							html += '<td><button style="border:1px solid #2d2d2d; padding:1rem 1rem;" onclick="downloadFile(\''+item["FILE_PATH"]+'\',\''+item["FILE_PTN"]+'\'); return false;">download</button></td>';
							html += '</tr>';
							tbody.append(html);
							showPaging(Math.ceil(result.total / $('#pagePerRow').val()),10,parseInt(page),'selectNoticeList', $(".Pagination")); 
			        	});
						$('.totalCnt').text(result.total);
					} else {
							html = "";
							html += '<tr class="NoData">';
							html += '<th colspan="6">';
							html += 'No Data';
							html += '</th>';
							html += '</tr>';
							tbody.append(html);
							showPaging(Math.ceil(result.total / $('#pagePerRow').val()),10,parseInt(page),'selectNoticeList', $(".Pagination"));
							$('.totalCnt').text(result.total);
					}
				}catch(e){
			        console.log(e.message);
			    }finally{
			        console.log('finally');
			    }
			},
		    error:function(request, status, error){
				console.log("error:"+error);
		    }
		});  
	}
	
	//이미지뷰
	function imageView(filePath){
		$('#main').hide();
		var src = '/projectImages/'+filePath;
		$('#imgTag').attr('src',src);
		$('#imgDiv').show();
	}
	
	// 파일 다운로드
	function downloadFile(filePath, fileName){
		$('#tableForm #filePath').val(filePath);
		$('#tableForm #fileName').val(fileName);
		$('#tableForm').attr({
			action : '/board/downloadDataFile.do',
			target : '_self',
			method : 'post'
		}).submit(); 
	}
	
	
	// 멀티파일 다운로드
	function downloadMultiFile() {
		if($("input[name=seq]").is(":checked") == false) {
			alert("체크된 파일이 없습니다.");
			return false;
		} else {
			$('#tableForm').attr({
				action : '/board/downloadMultiFile.do',
				target : '_self',
				method : 'POST'
			}).submit();
		}
	}
	
	//이미지 전체화면
	function imgaeAll(){

		var fileCallPath = $('#imgTag').attr('src');
	    $("#imagePath").val(fileCallPath);
		var url = '/board/selectDataFullImage.do';
		window.open(url, "fullImagePopup", "fullscreen");
		$("#fullImage").attr("action", url);
		$("#fullImage").attr("target", "fullImagePopup");
		$("#fullImage").attr("method", "post");
		$("#fullImage").submit();
	}
	
	
	
var curPageRaw = "1";
	
	function getCurrentPage(){
		return curPageRaw;	
	}

	/************************************************************************
	함수명 : showPaging                          
	설   명 : 한칸씩 이동하는 페이징 함수
	인   자 : 
	사용법 : 데이터 뿌려주는 본 페이지 함수에 끼워서 사용
	작성일 : 2022.04.28
	작성자 : 김영지                   
	************************************************************************/	
	
	function showPaging(totalPage, showPageCnt, curPage, searchFunctionName, parentContainerSelect) {
		
		curPageRaw = curPage;
		
		var pagingWriting = "";
		var target = 1;		
		var pageBlock = Math.ceil(totalPage / showPageCnt);
		var curBlock = Math.ceil(curPage / showPageCnt);
		
		var targetPage ;
		
		if (curPage > 1) {
			pagingWriting += '<li><a class="Arrow active" aria-label="prev button" href="javascript:' + searchFunctionName + '(\'' + (curPage-1) + '\')"><svg aria-hidden="true" width="8" height="15"><path d="m2.411 7.499 5.295-5.671a1.122 1.122 0 0 0 0-1.514.957.957 0 0 0-1.416 0l-6 6.426a1.126 1.126 0 0 0-.029 1.478l6.024 6.47a.959.959 0 0 0 1.416 0 1.122 1.122 0 0 0 0-1.514Z"/></svg></a></li>'
		} else {
			pagingWriting += '<li><a class="Arrow disabled" aria-label="prev button"><svg aria-hidden="true" width="8" height="15"><path d="m2.411 7.499 5.295-5.671a1.122 1.122 0 0 0 0-1.514.957.957 0 0 0-1.416 0l-6 6.426a1.126 1.126 0 0 0-.029 1.478l6.024 6.47a.959.959 0 0 0 1.416 0 1.122 1.122 0 0 0 0-1.514Z"/></svg></a></li>'
		}
		
		if(totalPage == 0) {
			pagingWriting += '<li class="active"><a aria-label="button">1</a></li>';
		}
		
		for (i = 0; i < totalPage; i++) {
			var no = i + 1;
			if (((curBlock - 1) * showPageCnt < no) && (no <= curBlock * showPageCnt)) {
				if (curPage == no) {
					target = no;	// 현재페이지를 target에 대입 
					pagingWriting += '<li class="active"><a href="javascript:' + searchFunctionName + '(\'' + no + '\')">' + no + '</a></li>';
				} else {
					pagingWriting += '<li><a href="javascript:' + searchFunctionName + '(\'' + no + '\')">' + no + '</a></li>';
				}
			}
		}
		
		// 다음 타겟페이지는 현재 페이지에서 +1
		targetPage = target+1;
		
		// 현재페이지와 전체페이지수가 일치하지 않을때 이동가능 
		if (target != totalPage && totalPage > 1) {
			pagingWriting += '<li><a class="Arrow active" aria-label="next button" href="javascript:' + searchFunctionName + '(\'' + targetPage + '\')"><svg aria-hidden="true" width="8" height="15"><path d="M5.589 7.501.294 13.172a1.122 1.122 0 0 0 0 1.514.957.957 0 0 0 1.416 0l6-6.426a1.126 1.126 0 0 0 .029-1.478L1.715.312a.959.959 0 0 0-1.416 0 1.122 1.122 0 0 0 0 1.514Z"/></svg></a></li>'
		} else {
			pagingWriting += '<li><a class="Arrow disabled" aria-label="next button"><svg aria-hidden="true" width="8" height="15"><path d="M5.589 7.501.294 13.172a1.122 1.122 0 0 0 0 1.514.957.957 0 0 0 1.416 0l6-6.426a1.126 1.126 0 0 0 .029-1.478L1.715.312a.959.959 0 0 0-1.416 0 1.122 1.122 0 0 0 0 1.514Z"/></svg></a></li>'
		}
		
		if(parentContainerSelect != null && parentContainerSelect != undefined) {
			$(parentContainerSelect).find("#viewPagingUl").html("");
			$(parentContainerSelect).find("#viewPagingUl").html(pagingWriting);
		} else {
			$("#viewPagingUl").html("");
			$("#viewPagingUl").html(pagingWriting);
		}
	}

	
</script>
</head>
<body>
	<div class="HeaderWrap">
	    
	    <div class="MenuHeader">
	        <div class="Menu" style="justify-content: center;">
	            <a href="/main/main.do">
	                <img src="/resource/Images/nmscHeader.png" style="height:170px; width:132rem;" alt="NMSC Logo"/>
	            </a>
	            
	        </div>
	    </div>
	</div>
<form id="fullImage">
	<input type="hidden" name="imagePath" id="imagePath" value=""/>
</form>
<div class="Wrap">
	
	<div class="ContWrap">
		<div class="ContHwrap">
			<h1>
				Real-Time Data
			</h1>
			<ul class="Navigation">
				<li>
                    <a aria-label="home" href="/main/main.do">
                        <svg width="14" height="16" aria-hidden="true">
                        	<g>
                        		<path d="M1 5.9 7 1l6 4.9v7.7a1.368 1.368 0 0 1-1.333 1.4H2.333A1.368 1.368 0 0 1 1 13.6Z" fill="none" stroke="#919191" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"/>
                        	</g>
                        </svg>
                    </a>
				</li>
				<li>
					<a>Real-Time Data</a>
				</li>
			</ul>
		</div>
	</div>
	
	<div id="main" class="ContWrap">
	
		<div class="ContBwrap">
			<div class="SearchBoxWrap" style="height:21.4rem;">
				<table>
				<caption></caption>
				<tr>
					<th>Select Area</th>
					<th>Search Time (UTC)</th>
					<th></th>
				</tr>
				<tr>
					<td>
						<select name="dataArea" id="dataArea">
							<option value="">ALL</option>
							<c:forEach var="data" items="${areaGroup}" varStatus="status">
								<option value="${data.DATA_AREA}" <%-- <c:if test="${data.DATA_AREA == paramMap.dataArea  }" selected="selected"</c:if> --%>>${data.DATA_AREA}</option>
							</c:forEach>
						</select>
					</td>
					<td>
						<input type="text" class="searchOptionDate" name="sdate" id="sdate" style="width:140px; display:inline-block;"/>
						<select id="shour" style="width:50px;">
							<c:forEach var="hour" begin="0" end="23">
								<option value="<c:if test="${hour<10}">0</c:if>${hour}"><c:if test="${hour<10}">0</c:if>${hour}h</option>
							</c:forEach>
						</select>
						:
						<select id="smin" style="width:50px;">
							<c:forEach var="min" begin="0" end="59">
								<option value="<c:if test="${min<10}">0</c:if>${min}"><c:if test="${min<10}">0</c:if>${min}m</option>
							</c:forEach>
						</select>
						:
						<select id="ssec" style="width:50px;">
							<c:forEach var="sec" begin="0" end="59">
								<option value="<c:if test="${sec<10}">0</c:if>${sec}"><c:if test="${sec<10}">0</c:if>${sec}s</option>
							</c:forEach>
						</select>
						<br/><br/>
						<input type="text" class="searchOptionDate" name="edate" id="edate" class="dateOption" style="width:140px; display:inline-block;"/>
						<select id="ehour" style="width:50px;">
							<c:forEach var="hour" begin="0" end="23">
								<option value="<c:if test="${hour<10}">0</c:if>${hour}"><c:if test="${hour<10}">0</c:if>${hour}h</option>
							</c:forEach>
						</select>
						:
						<select id="emin" style="width:50px;">
							<c:forEach var="min" begin="0" end="59">
								<option value="<c:if test="${min<10}">0</c:if>${min}"><c:if test="${min<10}">0</c:if>${min}m</option>
							</c:forEach>
						</select>
						:
						<select id="esec" style="width:50px;">
							<c:forEach var="sec" begin="0" end="59">
								<option value="<c:if test="${sec<10}">0</c:if>${sec}"><c:if test="${sec<10}">0</c:if>${sec}s</option>
							</c:forEach>
						</select>
					</td>
					<td>
					<button type="submit" aria-label="Search button" onclick="selectNoticeList(1)">
						<svg width="20" height="20"><g fill="#0071eb"><path d="M9.031 18.061a9.031 9.031 0 1 1 9.031-9.031 9.041 9.041 0 0 1-9.031 9.031ZM9.031 2a7.031 7.031 0 1 0 7.031 7.031A7.038 7.038 0 0 0 9.031 2Z"/><path d="M19 20.001a1 1 0 0 1-.707-.293l-3.669-3.669a1 1 0 0 1 1.414-1.414l3.669 3.669A1 1 0 0 1 19 20.001Z"/></g></svg>                                     
					</button>
					</td>
				</tr>
				</table>
			</div>
			<div class="Content">
				<div class="SortingWrap">
					<p class="Sorting">Total<span class="totalCnt">0</span></p>
					<select class="SortingView" name="pagePerRow" id="pagePerRow">
						<option value="10" selected>10</option>
						<option value="50">50</option>
						<option value="100">100</option>
					</select>
				</div>
				<form id="tableForm" name="tableForm">
					<input type="hidden" id="filePath" name="filePath" value=""/>		
					<input type="hidden" id="fileName" name="fileName" value=""/>
					<table id ="NoticeList">
						<thead>
							<tr>
								<th><input type="checkbox" name="selectall" value="AllSelect" /></th>
								<th>Obs Time(UTC)</th>
								<th>Area</th>
								<th>Size(KB)</th>
								<th>File Name</th>
								<th>Download each</th>
							</tr>
						</thead>
						<tbody>
						
						</tbody>
					</table>
				</form>
				<div class="Pagination">
                	<div>
						<ul id="viewPagingUl">
							<li><a class="Arrow disabled" aria-label="prev button"><svg aria-hidden="true" width="8" height="15"><path d="m2.411 7.499 5.295-5.671a1.122 1.122 0 0 0 0-1.514.957.957 0 0 0-1.416 0l-6 6.426a1.126 1.126 0 0 0-.029 1.478l6.024 6.47a.959.959 0 0 0 1.416 0 1.122 1.122 0 0 0 0-1.514Z"/></svg></a></li>
							<li class="active"><a href="#">1</a></li>
							<li><a class="Arrow disabled" aria-label="next button"><svg aria-hidden="true" width="8" height="15"><path d="M5.589 7.501.294 13.172a1.122 1.122 0 0 0 0 1.514.957.957 0 0 0 1.416 0l6-6.426a1.126 1.126 0 0 0 .029-1.478L1.715.312a.959.959 0 0 0-1.416 0 1.122 1.122 0 0 0 0 1.514Z"/></svg></a></li>
						</ul>
					</div>
				</div>
				<br/>
				<div class="UserBtnWrap">
					<button type="button" class="BlackBtn" onclick="downloadMultiFile()">
						Down load
						<svg width="18" height="18"><g transform="translate(-1422.5 -684.591)"><circle cx="9" cy="9" r="9" transform="translate(1422.5 684.591)"/><g fill="none" stroke="#fff" stroke-linecap="round" stroke-width="2"><path d="m1427.525 689.615 7.92 7.92M1435.445 689.615l-7.92 7.92"/></g></g></svg>
					</button>
				</div>
			</div>
		</div>
	</div>
	<!-- 
	<div id="imgDiv" class="ContWrap">
		
		<button type="button" class="BlackBtn" onclick="imgaeAll()" style="border:1px solid #2d2d2d; height:4rem; padding:1rem 1.6rem; float:right;">
			<h1>전체화면보기</h1>
		</button>
		<img id="imgTag" src=""/>
	</div>
	 -->
</div>
</body>
</html>
