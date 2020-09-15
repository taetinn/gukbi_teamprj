<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="javatime" uri="http://sargue.net/jsptags/time" %>
<!DOCTYPE html>
<html>
<head>
<%-- head 영역  include head.jsp --%>
<jsp:include page="/WEB-INF/views/include/head.jsp"/>

<link href="/css/bootstrap.css" rel="stylesheet">
<style>
div.panel-heading, li.media {
	cursor: pointer;
}
div.panel-body, div.panel-footer {
	display: none;
}
</style>
</head>
<body>
<div id="wrap">
	<%-- header 영역  include top.jsp --%>
	<jsp:include page="/WEB-INF/views/include/top.jsp" />

	<div class="clear"></div>
	<div id="sub_img_center"></div>
	
	<div class="clear"></div>
	<jsp:include page="/WEB-INF/views/include/board_submenu.jsp"/>
	
	<article>
		
	<h1>파일 게시판 Content</h1>
		
	<table id="notice">
		<tr>
			<th scope="col" width="200">글번호</th>
			<td width="500" style="text-align: left;">${ boardVo.num }</td>
		</tr>
		<tr>
			<th scope="col">조회수</th>
			<td style="text-align: left;">${ boardVo.readcount }</td>
		</tr>
		<tr>
			<th scope="col">작성자</th>
			<td style="text-align: left;">${ boardVo.name }</td>
		</tr>
		<tr>
			<th scope="col">작성일</th>
			<td style="text-align: left;"><javatime:format value="${ board.regDate }" pattern="yyyy.MM.dd"/></td>
		</tr>
		<tr>
			<th scope="col">글제목</th>
			<td style="text-align: left;">${ boardVo.subject }</td>
		</tr>
		<tr>
			<th scope="col">파일</th>
			<td style="text-align: left;">
				
				<c:if test="${ not empty attachList }"><%-- ${ not empty boardVo.attachList } --%>
					<c:forEach var="attach" items="${ attachList }">
						
						<c:choose>
							<c:when test="${ attach.image eq 'I' }">
								
								<c:set var="beginIndex" value="${ fn:indexOf(attach.uploadpath, 'upload') }" />
								<c:set var="length" value="${ fn:length(attach.uploadpath) }" />
								<c:set var="path" value="${ fn:substring(attach.uploadpath, beginIndex, length) }"  />
								
								<a href="/${ path }/${ attach.uuid }_${ attach.filename }">
									<img src="/${ path }/s_${ attach.uuid }_${ attach.filename }">
								</a><br>
							</c:when>
							<c:otherwise>
								<a href="/board/download?uuid=${ attach.uuid }">
									${ attach.filename }
								</a><br>
							</c:otherwise>
						</c:choose>
					
					</c:forEach>
				</c:if>
				
			</td>
		</tr>
		<tr>
			<th scope="col">글내용</th>
			<td style="text-align: left;">${ boardVo.content }</td>
		</tr>
	</table>

	<div id="table_search">
		<%-- 로그인 아이디와 글작성자 아이디(이름)가 같으면  --%>
		<c:if test="${ not empty id  and  id eq boardVo.name }">
			<button type="button" onclick="location.href='/board/fileModify?num=${ boardVo.num }&pageNum=${ pageNum }'">글수정</button>
			<button type="button" onclick="remove()">글삭제</button>
		</c:if>
		
		<button type="button" onclick="location.href='/board/fileReplyWrite?reRef=${ boardVo.reRef }&reLev=${ boardVo.reLev }&reSeq=${ boardVo.reSeq }&pageNum=${ pageNum }'">답글쓰기</button>
		<button type="button" onclick="location.href='/board/fileList?pageNum=${ pageNum }'">목록보기</button>
	</div>
	
	<div class="clear"></div>
	<div id="page_control">
	</div>
	
	<!-- 댓글 영역 -->
	<div class="panel panel-default">
		<div class="panel-heading">
			<span class="glyphicon glyphicon-comment"></span> 댓글
			<span class="reply-totalcount"></span>
			<button id="btnAddReply" class="btn btn-primary btn-xs pull-right">댓글쓰기</button>
		</div>
		<div class="panel-body">
        
     		<ul class="media-list">
				<li class="media">
					<a class="pull-left" href="#"> 
						<img alt="Generic placeholder image" src="/images/center/pic.jpg">
					</a>
					<div class="media-body">
						<div class="media-heading">
							<strong class="text-primary">user01</strong>
							<small class="pull-right text-muted">2020-07-20 16:20:45</small>
						</div>
						<p>Cras sit amet nibh libero, in gravida nulla. Nulla vel metus
							scelerisque ante sollicitudin commodo. Cras purus odio, vestibulum
							in vulputate at, tempus viverra turpis.
						</p>
					</div>
				</li>
			</ul>
      	</div><!-- /.panel-body -->
      	<div class="panel-footer">
      	</div>
	</div>
	
	
	
	
		
	</article>
    
	<div class="clear"></div>
	<%-- footer 영역  include bottom.jsp --%>
	<jsp:include page="/WEB-INF/views/include/bottom.jsp"/>
</div>



<!-- Modal -->
<div class="modal fade" id="myModal" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel">댓글 모달창</h4>
      </div>
      <div class="modal-body">
	      <div class="form-group">
				<label for="reply">댓글 내용</label> 
				<input type="text" class="form-control" placeholder="댓글 내용" name="reply" id="reply">
		  </div>
	      <div class="form-group">
				<label for="replyer">댓글 작성자</label> 
				<input type="text" class="form-control" placeholder="댓글 작성자" name="replyer" id="replyer" readonly>
		  </div>
		  <div class="form-group">
				<label for="regDate">댓글 작성일자</label> 
				<input type="text" class="form-control" placeholder="댓글 작성일자" name="regDate" id="regDate">
		  </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-warning" id="btnModalModify">수정</button>
        <button type="button" class="btn btn-danger" id="btnModalRemove">삭제</button>
        <button type="button" class="btn btn-primary" id="btnModalRegister">등록</button>
        <button type="button" class="btn btn-default" data-dismiss="modal" id="btnModalClose">닫기</button>
      </div>
    </div> <!-- 모달 콘텐츠 -->
  </div> <!-- 모달 다이얼로그 -->
</div> <!-- 모달 전체 윈도우 -->


<script src="/script/jquery-3.5.1.js"></script>
<script src="/script/bootstrap.js"></script>
<script>
// 함수 정의용

	function remove() {
		var result = confirm('${ boardVo.num }번 글을 정말 삭제하시겠습니까?');
		if (result) {
			location.href = '/board/fileDelete?num=${ boardVo.num }&pageNum=${ pageNum }';
		}
	} // remove()

	
	function displayTime(timeValue) {
		console.log(typeof timeValue);
		console.log(timeValue);

		var today = new Date();
		var date = new Date(timeValue); // new Date('2020-07-20T14:56:45');
		
		var gap = today.getTime() - date.getTime(); // 밀리초 차이값
		var oneDay = 1000 * 60 * 60 * 24;
		//var index = timeValue.indexOf('T');
		var strArr = timeValue.split('T');
		var str = '';
		
		if (gap < oneDay) { // '14:56:45'
			//str = timeValue.substring(index + 1, timeValue.length-1);
			str = strArr[1];
		} else { // '2020-07-20'
			//str = timeValue.substring(0, index);
			str = strArr[0];
		}

		console.log(str);
		return str;
	} // displayTime()
	
	
	
	
	function showReplyPage(replyCnt) { // replyCnt는 총 댓글갯수
		if (replyCnt == 0) {
			return;
		}

		// 한 페이지에 보여지는 댓글 갯수
		var pageSize = 10;
		// 총 페이지 갯수
		var pageCount = Math.ceil(replyCnt / pageSize);
		// 한개의 페이지블록 당 요소갯수
		var pageBlock = 5;

		
		// ((1 - 1) / 5) * 5 + 1 -> 1
		// ((2 - 1) / 5) * 5 + 1 -> 1
		// ((3 - 1) / 5) * 5 + 1 -> 1
		// ((4 - 1) / 5) * 5 + 1 -> 1
		// ((5 - 1) / 5) * 5 + 1 -> 1

		// ((6 - 1) / 5) * 5 + 1 -> 6
		// ((7 - 1) / 5) * 5 + 1 -> 6
		// ((8 - 1) / 5) * 5 + 1 -> 6
		// ((9 - 1) / 5) * 5 + 1 -> 6
		// ((10- 1) / 5) * 5 + 1 -> 6
		
		// 페이지 블록의 시작페이지
		//var startPage = (Math.floor(pageNum / pageBlock) - (pageNum % pageBlock == 0 ? 1 : 0)) * pageBlock + 1;
		var startPage = (Math.floor((pageNum - 1) / pageBlock)) * pageBlock + 1;
		// 페이지 블록의 끝페이지
		var endPage = startPage + pageBlock - 1;
		if (endPage > pageCount) {
			endPage = pageCount;
		}

		var prev = startPage > pageBlock;
		var next = endPage < pageCount;

		// 페이지블록 화면요소 만들기
		var str = '';
		str += '<div class="clearfix">';
		str += '<ul class="pagination pull-right">';

		if (prev) {
			str += '	<li><a href="' + (startPage - pageBlock) + '">«이전</a></li>';
		} else {
			str += '	<li class="disabled"><a href="#">«이전</a></li>';
		}

		for (let i=startPage; i<=endPage; i++) {

			var active = (pageNum == i) ? 'active' : '';
			
			str += '	<li class="' + active + '"><a href="' + i + '">' + i + '</a></li>';
		}

		if (next) {
			str += '	<li><a href="' + (startPage + pageBlock) + '">다음»</a></li>';
		} else {
			str += '	<li  class="disabled"><a href="#">다음»</a></li>';
		}
		
		str += '</ul></div>';

		$('div.panel-footer').html(str);
		
	} // showReplyPage()
	
	
	
	
	var $spanReplyTotalCount = $('span.reply-totalcount');

	// 댓글목록 데이터 가공하여 화면에 추가하기
	function processList(replyCnt, list) {

		$spanReplyTotalCount.html(replyCnt); // 전체 댓글갯수 화면에 배치

		var str = '';

		if (list == null || list.length == 0) {
			//$('ul.media-list').html('');
			$('ul.media-list').empty(); // remove()와 구분 주의!
			return;
		}
		
		for (let reply of list) { // length-1 ~ 0 역순
			str += '<li class="media" data-rno="' + reply.rno + '">';
			str += '	<a class="pull-left" href="#"> ';
			str += '		<img src="/images/center/pic.jpg">';
			str += '	</a>';
			str += '	<div class="media-body">';
			str += '		<div class="media-heading">';
			str += '			<strong class="text-primary">' + reply.replyer + '</strong>';
			str += '			<small class="pull-right text-muted">' + displayTime(reply.regDate) + '</small>';
			str += '		</div>';
			str += '		<p>' + reply.reply + '</p>';
			str += '	</div>';
			str += '</li>';
		} // for

		$('ul.media-list').html(str); // 댓글리스트 화면출력

		showReplyPage(replyCnt);  // 페이지블록 출력하기
		
	} // processList()
	
	
	
	
	var loginId = '${ id }'; // 로그인한 세션 아이디를 변수에 저장
    var bnoValue = ${ boardVo.num }; // 현재 게시판 글번호

	
	// 새로 댓글목록 보여주기
	function showList(pageNum) {

		$.ajax({
			url: '/replies/pages/' + bnoValue + '/' + pageNum,
			method: 'GET',
			success: function (result) {
				console.log(typeof result);
				console.log(result.replyCnt);
				console.log(result.list);

				processList(result.replyCnt, result.list);
			},
			error: function () {
				alert('댓글 리스트 가져오기 오류 발생...');
			}
		});
	} // showList()
	
	// 화면이 뜨자마자 댓글목록 가져오기 실행!
	var pageNum = 1;
	showList(pageNum); // 1페이지는 최신페이지
	
</script>
<script>
// 이벤트 연결용

	// 모달창 화면요소를 미리 변수로 저장
	var modal = $('.modal');
	var modalTitle = $('#myModalLabel');
	var modalInputReply = modal.find('input[name="reply"]');
	var modalInputReplyer = modal.find('input[name="replyer"]');
	var modalInputRegDate = modal.find('input[name="regDate"]');

	var btnModalRegister = $('#btnModalRegister');
	var btnModalRemove = $('#btnModalRemove');
	var btnModalModify = $('#btnModalModify');

	// 댓글등록 버튼 클릭했을때
	$('#btnAddReply').on('click', function (event) {
		// 이벤트 전파 막기
		event.stopPropagation();

		modalTitle.html('댓글 등록'); // 모달창 제목 설정

		modal.find('input').val('');
		modalInputReplyer.val(loginId); // 댓글은 로그인 전용이므로 댓글 작성자를 로그인 아이디로 설정
		modalInputRegDate.closest('div').hide(); // 댓글 작성일자 영역 숨기기
		modal.find('button[id != "btnModalClose"]').hide(); // 닫기버튼을 제외한 모든버튼 숨기기	

		btnModalRegister.show(); // 등록버튼 보이기
		
		modal.modal('show'); // 'show'모달창 띄우기, 'hide'모달창 숨기기

		//return false; // 기본기능방지, 이벤트전파막기 모두 수행
	});


	// 모달창의 댓글등록 버튼을 클릭했을때
	btnModalRegister.on('click', function () {
		
		var reply = {
			bno: bnoValue,
			reply: modalInputReply.val(),
			replyer: modalInputReplyer.val()
		};
		console.log('reply : ' + reply);

		var str = JSON.stringify(reply);
		console.log('str : ' + str);

		$.ajax({
			method: 'POST',
			url: '/replies/new',
			data: str,
			contentType: 'application/json; charset=utf-8',
			success: function (result) {
				alert(result);

				modal.find('input').val('');
				modal.modal('hide');

				pageNum = 1;
				showList(pageNum);
			},
			error: function () {
				alert('댓글등록 에러발생...');
			}
		});
	});

	// 댓글 아이템을 클릭했을때
	$('ul.media-list').on('click', 'li', function () {
		// 선택한 댓글 아이디와 로그인 아이디가 다르면 리턴(종료)
		var replyId = $(this).find('strong').text();
		if (replyId != loginId) {
			return;
		}
		
		var rno = $(this).data('rno');
		console.log(rno);

		$.ajax({
			method: 'GET',
			url: '/replies/' + rno,
			success: function (result) {
				console.log(result);

				modalInputReply.val(result.reply);
				modalInputReplyer.val(result.replyer);
				modalInputRegDate.val(displayTime(result.regDate)).prop('readonly', true);
				modal.data('rno', result.rno);

				modal.find('button[id != "btnModalClose"]').hide();
				btnModalRemove.show();
				btnModalModify.show();

				modal.modal('show');
			},
			error: function () {
				alert('댓글 한개 가져오기 에러발생...')
			}
		});
	});


	// 모달창의 댓글수정 버튼 클릭했을때
	btnModalModify.on('click', function () {

		var rno = modal.data('rno');
		
		var reply = {
			reply: modalInputReply.val()
		};

		var str = JSON.stringify(reply);

		$.ajax({
			method: 'PUT',
			url: '/replies/' + rno,
			data: str,
			contentType: 'application/json; charset=utf-8',
			success: function (result) {
				alert(result);
				modal.modal('hide');
				showList(pageNum);
			},
			error: function () {
				alert('댓글 수정 오류 발생...')
			}
		});

	});


	// 모달창의 댓글삭제 버튼 클릭했을때
	btnModalRemove.on('click', function () {

		var rno = modal.data('rno');
		
		var isRemove = confirm(rno + '번 댓글을 정말로 삭제하시겠습니까?');
		if (!isRemove) {
			return;
		}

		$.ajax({
			method: 'DELETE',
			url: '/replies/' + rno,
			success: function (result) {
				alert(result);
				modal.modal('hide');
				showList(pageNum);
			},
			error: function () {
				alert('댓글 삭제 오류 발생...');
			}
		});

	});
	

	

	$('div.panel-heading').on('click', function () {
		$(this).next().fadeToggle(); // div.panel-body
		$(this).next().next().toggle(); // div.panel-footer

		var $divPanel = $(this).closest('div.panel');

		if ($divPanel.hasClass('panel-default')) {
			$divPanel.removeClass('panel-default');
			$divPanel.addClass('panel-info');
		} else { // panel-info
			$divPanel.removeClass('panel-info');
			$divPanel.addClass('panel-default');
		}
	});

	// 동적 이벤트 연결(이벤트 위임 방식 사용)
	$('div.panel-footer').on('click', 'li a', function (event) {
		// a태그의 기본기능 하이퍼링크 막기
		event.preventDefault();

		var targetPageNum = $(this).attr('href'); // attr() VS prop()
		console.log('targetPageNum : ' + targetPageNum);

		if (targetPageNum == '#') {
			return;
		}
		
		pageNum = targetPageNum;

		showList(pageNum);
	});
	
</script>
</body>
</html>   





