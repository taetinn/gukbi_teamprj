package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.Criteria;
import com.example.domain.ReplyPageDto;
import com.example.domain.ReplyVo;
import com.example.service.ReplyService;

import lombok.extern.java.Log;

/*
 * 
 * REST컨트롤러의 HTTP method 매핑 방식 (CRUD)
 * POST - INSERT -C
 * Get - SELECT -R
 * PUT / PATCH- UPDATE - U
 * DELETE -DELETE-D
 * 
 * */

@RestController // 이 클래스의 모든 메소드의 리턴값이 JSON 또는 XML 응답으로 동작함 
@RequestMapping("/replies/*")
@Log
public class ReplyController {

	@Autowired
	private ReplyService replyService;
	
	@PostMapping(value = "/new", consumes = "application/json", produces = MediaType.TEXT_PLAIN_VALUE) 
	//consumes ==  client에서 보내는 방식 json 
	//produces --> 성공 시 success라는 문자열 출력을 하고싶어서 //응답으로 어떤 데이터를 넘기느냐
	public ResponseEntity<String> create(@RequestBody ReplyVo replyVo) {
		log.info("replyVo : " + replyVo);
		
		int count = replyService.register(replyVo);
		log.info("reply INSERT count : " + count);
		
		ResponseEntity<String> entity = null;
		if (count > 0) {
			entity = new ResponseEntity<String>("success", HttpStatus.OK);
		} else {
			entity = new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return entity;
	} //create()
	
	
	//요청주소 뒤에 .json이면 json응답, .xml이ㅏ면 xml응담을 줌
	@GetMapping(value="/pages/{bno}", produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<List<ReplyVo>> getList(@PathVariable("bno") int bno) { //@PathVariable : path에 값이 있으니 거기서 ""안의 값을 찾아랏
		
		List<ReplyVo> list = replyService.getList(bno);
		
		return new ResponseEntity<List<ReplyVo>>(list,HttpStatus.OK);
	} //getList()
	
	@GetMapping(value="/pages/{bno}/{pageNum}", produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<ReplyPageDto> getListWithPage(@PathVariable("bno") int bno,
			@PathVariable("pageNum") int pageNum){
		
		Criteria cri = new Criteria(pageNum,10);
		
		ReplyPageDto replyPageDto = replyService.getListWithPaging(bno, cri);
		
		return new ResponseEntity<ReplyPageDto>(replyPageDto, HttpStatus.OK);
		
	}
	
	@GetMapping(value="/{rno}", produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<ReplyVo> get(@PathVariable("rno") int rno) {
		ReplyVo replyVo =replyService.get(rno);
		return new ResponseEntity<ReplyVo>(replyVo,HttpStatus.OK);
	}
	
	@DeleteMapping(value="/{rno}", produces =  MediaType.TEXT_PLAIN_VALUE )
	public ResponseEntity<String> remove(@PathVariable("rno") int rno) {
		int cnt = replyService.remove(rno);
		
		ResponseEntity<String> entity = null;
		if (cnt > 0) {
			entity = new ResponseEntity<String>("success", HttpStatus.OK);
		} else {
			entity = new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return cnt > 0 
				? new ResponseEntity<String>("success", HttpStatus.OK) 
						: new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
	}//remove()
	
	@RequestMapping(method = { RequestMethod.PUT, RequestMethod.PATCH },
			value = "/{rno}",
			consumes = "application/json", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> modify(@RequestBody ReplyVo replyVo, 
			@PathVariable("rno") int rno) {
		
		replyVo.setRno(rno);
		replyService.modify(replyVo);
		
		return new ResponseEntity<String>("success", HttpStatus.OK);
		
	} // modify()
}






