<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<script type="text/javascript">
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
<!-- pageNav -->
<div>
	<ul id="viewPagingUl">
		<li><a class="Arrow disabled" aria-label="prev button"><svg aria-hidden="true" width="8" height="15"><path d="m2.411 7.499 5.295-5.671a1.122 1.122 0 0 0 0-1.514.957.957 0 0 0-1.416 0l-6 6.426a1.126 1.126 0 0 0-.029 1.478l6.024 6.47a.959.959 0 0 0 1.416 0 1.122 1.122 0 0 0 0-1.514Z"/></svg></a></li>
		<li class="active"><a href="#">1</a></li>
		<li><a class="Arrow disabled" aria-label="next button"><svg aria-hidden="true" width="8" height="15"><path d="M5.589 7.501.294 13.172a1.122 1.122 0 0 0 0 1.514.957.957 0 0 0 1.416 0l6-6.426a1.126 1.126 0 0 0 .029-1.478L1.715.312a.959.959 0 0 0-1.416 0 1.122 1.122 0 0 0 0 1.514Z"/></svg></a></li>
	</ul>
</div>