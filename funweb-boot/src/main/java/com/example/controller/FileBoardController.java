package com.example.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.domain.AttachfileVo;
import com.example.domain.BoardVo;
import com.example.domain.PageDto;
import com.example.service.AttachfileService;
import com.example.service.BoardService;

import lombok.extern.java.Log;
import net.coobird.thumbnailator.Thumbnailator;

@Log
@RequestMapping("/board/*")
@Controller
public class FileBoardController {

	@Autowired
	private BoardService boardService;
	
	@Autowired
	private AttachfileService attachService;
	
	
	@GetMapping("/fileList")
	public String fileList(
			@RequestParam(defaultValue = "1") int pageNum,
			@RequestParam(defaultValue = "") String category,
			@RequestParam(defaultValue = "") String search,
			Model model) {
		log.info("fileList() 호출됨");

		// 전체 글갯수
		int totalCount = boardService.getTotalCount(category, search);
		
		
		// ========================================
		// 한페이지에 해당하는 글목록 구하기 작업
		// ========================================
		
		// 한 페이지에 15개씩 가져오기
		int pageSize = 15;
		// 시작행 인덱스번호 구하기(수식)
		int startRow = (pageNum-1) * pageSize;
			
		// 원하는 페이지의 글을 가져오는 메소드
		List<BoardVo> list = null;
		if (totalCount > 0) {
			list = boardService.getBoards(startRow, pageSize, category, search);
		}
		
		
		
		// ========================================
		// 페이지블록 관련정보 구하기 작업
		// ========================================
		
		// 총 페이지 수 구하기
		// 글50개. 한화면에보여줄글 10개 -> 50/10 = 5페이지
		// 글55개. 한화면에보여줄글 10개 -> 55/10 = 5 + 1페이지(나머지 있으면) -> 6페이지
		//int pageCount = totalCount / pageSize + (totalCount % pageSize == 0 ? 0 : 1);
		int pageCount = totalCount / pageSize;
		if (totalCount % pageSize > 0) {
			pageCount += 1;
		}
		
		// 화면에 보여줄 페이지번호의 갯수 설정
		int pageBlock = 10;
		
		// 페이지블록의 시작페이지 구하기!
		// 1~10   11~20   21~30
		
		//  1~10 ->  1
		// 11~20 -> 11
		// 21~30 -> 21
		// 31~40
		
		// 페이지 블록의 시작페이지
		int startPage = ((pageNum / pageBlock) - (pageNum % pageBlock == 0 ? 1 : 0)) * pageBlock + 1;
		// 페이지 블록의 끝페이지
		int endPage = startPage + pageBlock - 1;
		if (endPage > pageCount) {
			endPage = pageCount;
		}
		
		
		// 페이지블록 관련 정보를 PageDTO에 저장(Map 컬렉션 사용 가능)
		PageDto pageDto = new PageDto();
		pageDto.setTotalCount(totalCount);
		pageDto.setPageCount(pageCount);
		pageDto.setPageBlock(pageBlock);
		pageDto.setStartPage(startPage);
		pageDto.setEndPage(endPage);
		pageDto.setCategory(category);
		pageDto.setSearch(search);
		
//		Map<String, Object> pageMap = new HashMap<>();
//		pageMap.put("totalCount", totalCount);
//		pageMap.put("pageCount", pageCount);
//		pageMap.put("pageBlock", pageBlock);
//		pageMap.put("startPage", startPage);
//		pageMap.put("endPage", endPage);
//		pageMap.put("category", category);
//		pageMap.put("search", search);
		
		
		// 뷰(jsp)에서 사용할 데이터를 model 객체에 저장해놓으면
		// 스프링 프론트컨트롤러가 request 영역객체로 옮겨줌
		model.addAttribute("boardList", list);
		model.addAttribute("pageDto", pageDto);
		//model.addAttribute("pageMap", pageMap);
		model.addAttribute("pageNum", pageNum);
		
		return "center/fileNotice";
	} // fileList()
	
	
	@GetMapping("/fileWrite")
	public String fileWrite(HttpSession session, @ModelAttribute("pageNum") String pageNum) {
		// 세션값 가져오기
//		String id = (String) session.getAttribute("id");
//		// 로그인 안했으면(세션값 없으면) /member/login 리다이렉트 이동
//		if (id == null) {
//			return "redirect:/member/login";
//		}
//		
		return "center/fileWriteForm";
	} // fileWrite() get
	
	
	@PostMapping("/fileWrite")
	public String fileWrite(HttpServletRequest request,
			@RequestParam("filename") List<MultipartFile> files,
			BoardVo boardVo) throws Exception {
		
		// ================================================== BoardVO 설정하기
		// 새글번호 생성해서 가져오기
		int num = boardService.getBoardNum();
		// vo에 새글번호 설정
		boardVo.setNum(num);
		
		// ip  regDate  setter메소드 호출로 값 저장
		boardVo.setIp(request.getRemoteAddr());
		boardVo.setRegDate(LocalDateTime.now());
		boardVo.setReadcount(0);
		
		// 주글의 답글관련 필드값(reRef,reLev,reSeq) 설정
		boardVo.setReRef(num); // 주글의 글그룹번호는 글번호와 동일 
		boardVo.setReLev(0); // 주글의 들여쓰기 레벨은 없음(0)
		boardVo.setReSeq(0); // 같은 그룹 내에서 최상단에 정렬(오름차순)되도록 순번은 0
		
		// ================================================== BoardVO 설정완료
		
		
		
		// ================================================== 파일업로드 수행하기
		// 애플리케이션 내장객체
		ServletContext application = request.getServletContext();
		String path = application.getRealPath("/upload");
		System.out.println("path: " + path); // C:/devtools/workspace-sts/funweb-boot/src/main/webapp/upload

//		String path = "C:/devtools/upload";
		
		String strDate = this.getFoleder(); // 날짜 "년/월/일" 형식의 문자열 리턴
		
		File dir = new File(path, strDate); // C:/devtools/workspace-sts/funweb-boot/src/main/webapp/upload/2020/06/16
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		
		// 첨부파일정보 담을 리스트 준비
		List<AttachfileVo> attachList = new ArrayList<>();
		
		for (MultipartFile multipartFile : files) {
			if (multipartFile.isEmpty()) {
				continue;
			}
			
			String filename = multipartFile.getOriginalFilename();
			// 익스플로러는 파일이름에 경로가 포함되있으므로
			// 순수 파일이름만 부분문자열로 가져오기
			int index = filename.lastIndexOf("\\") + 1;
			filename = filename.substring(index);
			
			// 파일명 중복 피하기 위해 파일이름 앞에 uuid 문자열 붙이기
			UUID uuid = UUID.randomUUID();
			String strUuid = uuid.toString();
			
			// 업로드(생성)할 파일이름
			String uploadFilename = strUuid + "_" + filename;
			
			// 생성할 파일정보 File 객체로 준비
			File saveFile = new File(dir, uploadFilename);
			
			multipartFile.transferTo(saveFile); // 임시업로드된 파일을 지정경로의 파일명으로 복사
			
			// ================================================== 파일업로드 수행완료
			

					
			
			// ================================================== 첨부파일 AttachfileVo 설정하기
					
			// 파일정보 담기위한 AttachfileVo 객체 생성
			AttachfileVo attachVo = new AttachfileVo();
			// 게시판 글번호 설정
			attachVo.setBno(boardVo.getNum());
			// 업로드 경로 설정. 경로에서 백슬래시문자는 슬래시로 모두 변환해서 설정.
			attachVo.setUploadpath(dir.getPath().replace("\\", "/"));
					
			attachVo.setUuid(strUuid);
			attachVo.setFilename(filename);
					
			// 파일명 확장자가 이미지면 "I", 아니면 "O"
			if (isImageType(saveFile)) {
				// 섬네일 이미지 생성하기
				File thumbnailFile = new File(dir, "s_" + uploadFilename);
				
				try (FileOutputStream fos = new FileOutputStream(thumbnailFile)) {
					Thumbnailator.createThumbnail(multipartFile.getInputStream(), fos, 100, 100);
				}
				
				attachVo.setImage("I");
			} else {
				attachVo.setImage("O");
			}
			
			// ================================================== 첨부파일 AttachfileVo 설정완료
					
			// AttachfileVo 첨부파일정보 한개 추가
			//attachService.insert(attachVo);
			attachList.add(attachVo);
		} // for
		
		
		// ============================ BoardVo, AttachfileVo DB 테이블에 insert 하기
		// board 테이블에 새글 insert하기
		//boardService.insert(boardVo);
		// attachfile 테이블에 첨부파일정보 리스트 insert하기
		//attachService.insert(attachList);
		
		
		// board테이블과 attachfiles 테이블 insert를 트랜잭션으로 처리
		boardService.insertBoardAndAttaches(boardVo, attachList);
		// ============================ BoardVo, AttachfileVo DB 테이블에 insert 완료
		
		
		// 글목록 /board/fileList 로 리다이렉트 이동
		return "redirect:/board/fileList";
	} // fileWrite() post
	
	
	private String getFoleder() {
		LocalDateTime dateTime = LocalDateTime.now(); // 오늘날짜 객체준비
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		String strDate = dateTime.format(formatter); // 2020/06/16
		return strDate;
	} // getFoleder()
	
	
	private boolean isImageType(File saveFile) throws IOException {
		boolean isImageType = false;
		
		String str = saveFile.getPath();
		System.out.println("이미지파일 경로: " + str);
		
		// 확장자명 구하기
		String ext = str.substring(str.lastIndexOf(".") + 1);
		if (ext.equalsIgnoreCase("png")
				|| ext.equalsIgnoreCase("gif")
				|| ext.equalsIgnoreCase("jpg")
				|| ext.equalsIgnoreCase("jpeg")) {
			isImageType = true;
		} else {
			isImageType = false;
		}
		
		// MIME 타입 정보 확인하기 (MIME 타입 정보가 없으면 null을 리턴함)
		String contentType = Files.probeContentType(saveFile.toPath());
		System.out.println("contentType: " + contentType); // "image/png"
		if (contentType != null) {
			isImageType = contentType.startsWith("image");
		} else {
			isImageType = false;
		}
		
		return isImageType;
	} // isImageType()
		
	
	
	
	@GetMapping("/fileContent")
	public String fileContent(int num, 
			@ModelAttribute("pageNum") String pageNum, 
			Model model,
			HttpSession session) {
		
		// 조회수 1증가
		boardService.updateReadcount(num);
		// 글 한개 가져오기
		BoardVo vo = boardService.getBoardAndAttachfilesByNum(num);
		// 첨부파일 리스트 가져오기
		List<AttachfileVo> attachList = vo.getAttachList();
		
		// 내용에서 엔터키 줄바꿈 \r\n -> <br> 바꾸기
		String content = "";
		if (vo.getContent() != null) {
			content = vo.getContent().replace("\r\n", "<br>");
			vo.setContent(content);
		}
		
		// jsp화면(뷰) 만들때 필요한 데이터를 Model 타입 객체에 저장
		model.addAttribute("boardVo", vo);
		model.addAttribute("attachList", attachList);
		
		return "center/fileContent";
	} // fileContent()
	
	
	@GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE) //무엇을 최종적으로 만들어낼지 annnotation에서 지정 //download service
	public ResponseEntity<Resource> download(String uuid) throws Exception {
		// uuid에 해당하는 레코드 한개 가져오기 
		AttachfileVo attachVo = attachService.getAttachfileByUuid(uuid);
		
		// 다운로드할 파일 객체 준비
		String filename = attachVo.getUuid() + "_" + attachVo.getFilename();
		File file = new File(attachVo.getUploadpath(), filename);

		
		Resource resource = new FileSystemResource(file);
		
		if(!resource.exists()) {
			log.info("There is no file to download.");
			return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);
		}
		
		String resFilename = resource.getFilename();
		// 다운로드할 파일이름에서 UUID 제거하기
		int beginIdx = resFilename.indexOf("_"+1); //uuid 빼고 ㄱ 다음 이름부터 
		String originalFilename = resFilename.substring(beginIdx);
		
		// 다운로드 파일명의 문자셋을 utf-8에서 iso-8859-1로 변환
		String downloadFilename = new String(originalFilename.getBytes("utf-8"), "iso-8859-1");
		System.out.println("iso-8859-1 filename = " + downloadFilename);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment; filename=" + downloadFilename);
		
		return  new ResponseEntity<Resource>(resource,headers,HttpStatus.OK);
	}//download() {
	
	
}










