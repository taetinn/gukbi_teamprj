package com.example.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.example.domain.BoardVo;
import com.example.mapper.BoardMapper;

public class BoardDao {
	
	private static BoardDao instance = new BoardDao();
	
	public static BoardDao getInstance() {
		return instance;
	}

	private BoardDao() {
	}
	
	
	// 게시판 새글번호 생성해서 가져오기
	public int getBoardNum() {
		int boardNum = 0;
		
		try (SqlSession sqlSession = DBManager.getSqlSessionFactory().openSession(true)) {
			BoardMapper mapper = sqlSession.getMapper(BoardMapper.class);
			boardNum = mapper.getBoardNum();
		}
		
		return boardNum;
	} // getBoardNum()
	
	
	// 주글 insert 메소드
	public void insert(BoardVo vo) {
		try (SqlSession sqlSession = DBManager.getSqlSessionFactory().openSession(true)) {
			BoardMapper mapper = sqlSession.getMapper(BoardMapper.class);
			// 새글번호 구하기
			int num = mapper.getBoardNum();
			// 새글번호를 vo에 설정
			vo.setNum(num);
			// 주글관련 re 필드값 설정
			vo.setReRef(num);
			vo.setReLev(0);
			vo.setReSeq(0);
			// 조회수 0
			vo.setReadcount(0); 
			
			mapper.insertBoard(vo);
		}
	} // insert()
	
	
	// (검색어가 적용되는) board 테이블의 전체 행 갯수 가져오기
	public int getTotalCount(String category, String search) {
		int count = 0;
		
		try (SqlSession sqlSession = DBManager.getSqlSessionFactory().openSession(true)) {
			BoardMapper mapper = sqlSession.getMapper(BoardMapper.class);
			count = mapper.getTotalCount(category, search);
		}
		
		return count;
	} // getTotalCount()
	
	
	// (검색어가 적용되는) 게시판 글목록 가져오기
	public List<BoardVo> getBoards(int startRow, int pageSize, String category, String search) {
		List<BoardVo> list = null;
		
		try (SqlSession sqlSession = DBManager.getSqlSessionFactory().openSession(true)) {
			BoardMapper mapper = sqlSession.getMapper(BoardMapper.class);
			list = mapper.getBoards(startRow, pageSize, category, search);
		}
		
		return list;
	} // getBoards()
	
	
	public BoardVo getBoardByNum(int num) {
		BoardVo vo = null;
		
		try (SqlSession sqlSession = DBManager.getSqlSessionFactory().openSession(true)) {
			BoardMapper mapper = sqlSession.getMapper(BoardMapper.class);
			vo = mapper.getBoardByNum(num);
		}
		
		return vo;
	} // getBoardByNum()
	
	
	// board테이블과 attachfile 테이블 조인해서 결과 리턴
	public BoardVo getBoardAndAttachfilesByNum(int num) {
		BoardVo boardVo = null;
		
		try (SqlSession sqlSession = DBManager.getSqlSessionFactory().openSession(true)) {
			BoardMapper mapper = sqlSession.getMapper(BoardMapper.class);
			boardVo = mapper.getBoardAndAttachfilesByNum(num);
		}
		
		return boardVo;
	} // getBoardAndAttachfilesByNum
	
	
	
	
	// 특정글 조회수 1증가시키는 메소드
	public void updateReadcount(int num) {
		try (SqlSession sqlSession = DBManager.getSqlSessionFactory().openSession(true)) {
			BoardMapper mapper = sqlSession.getMapper(BoardMapper.class);
			mapper.updateReadcount(num);
		}
	} // updateReadcount()
	
	
	public void update(BoardVo vo) {
		try (SqlSession sqlSession = DBManager.getSqlSessionFactory().openSession(true)) {
			BoardMapper mapper = sqlSession.getMapper(BoardMapper.class);
			mapper.update(vo);
		}
	} // update()
	
	
	public void deleteByNum(int num) {
		try (SqlSession sqlSession = DBManager.getSqlSessionFactory().openSession(true)) {
			BoardMapper mapper = sqlSession.getMapper(BoardMapper.class);
			mapper.deleteByNum(num);
		}
	} // deleteByNum()
	
	
	// 답글쓰기
	public void replyInsert(BoardVo vo) {
		
		SqlSession sqlSession = DBManager.getSqlSessionFactory().openSession(false); // false는  수동커밋
		
		try {
			BoardMapper mapper = sqlSession.getMapper(BoardMapper.class);
			
			// update sql문을 수행하는 메소드의 리턴값은
			// 변경된 행의 갯수의 의미가 아니다!
			mapper.updateReSeqByReRef(vo.getReRef(), vo.getReSeq());
			
			// 새글번호 구하기
			int num = mapper.getBoardNum();
			
			// 답글 insert할 정보로 vo를 설정
			vo.setNum(num);
			vo.setReLev(vo.getReLev()+1);
			vo.setReSeq(vo.getReSeq()+1);
			vo.setReadcount(0); // 조회수 0
			
			mapper.insertBoard(vo);
			
			sqlSession.commit(); // 커밋
		} catch (Exception e) {
			e.printStackTrace();
			sqlSession.rollback(); // 롤백
		} finally {
			sqlSession.close();
		}
	} // replyInsert()
	
	
	// 샘플 주글들 여러개를 insert하기
	public void insertDummyRows(int count) {
		// count가 100이면 BoardVo 주글정보 객체를 100개 준비하기
		for (int i=1; i<=count; i++) {
			BoardVo vo = new BoardVo();
			
			int num = getBoardNum(); // 새글번호 가져오기
			
			vo.setNum(num); // 기본키 항목
			vo.setName("홍길동"+num);
			vo.setPasswd("1234");
			vo.setSubject("글제목" + num);
			vo.setContent("글내용\n입니다. " + num);
			vo.setReadcount(0);
			vo.setRegDate(LocalDateTime.now());
			vo.setReRef(num);
			vo.setReLev(0);
			vo.setReSeq(0);
			
			this.insert(vo); // dao객체의 insert() 활용해서 행 추가
		} // for
	} // insertDummyRow
	
	
	public void deleteAll() {
		try (SqlSession sqlSession = DBManager.getSqlSessionFactory().openSession(true)) {
			BoardMapper mapper = sqlSession.getMapper(BoardMapper.class);
			mapper.deleteAll();
		}
	}
	
	
	public int getCount() {
		int count = 0;
		
		try (SqlSession sqlSession = DBManager.getSqlSessionFactory().openSession(true)) {
			BoardMapper mapper = sqlSession.getMapper(BoardMapper.class);
			count = mapper.getCount();
		}
		
		return count;
	}
	
	
	
	public static void main(String[] args) {
		// 새글번호 생성 테스트
		BoardDao dao = BoardDao.getInstance();
		//System.out.println("새글번호: " + dao.getBoardNum());
		
		// 더미 레코드 100개 추가
		//dao.insertDummyRows(500);
		//System.out.println("새글번호: " + dao.getBoardNum());
		
		//BoardVo vo = dao.getBoardByNum(1);
		//System.out.println(vo);
		
		//System.out.println(dao.getBoardNum());
		
		//List<BoardVo> list = dao.getBoards(0, 10);
		//System.out.println(list);
	}
	
	

	
	
}
