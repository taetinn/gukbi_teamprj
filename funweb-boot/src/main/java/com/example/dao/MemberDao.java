package com.example.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.example.domain.MemberVo;
import com.example.mapper.MemberMapper;

// Data Access Object(데이터베이스 접근 객체)
// member 테이블을 제어할 수 있는(sql문 실행하는) 클래스
public class MemberDao {
	
	private static MemberDao instance = new MemberDao();
	
	public static MemberDao getInstance() {
		return instance;
	}

	private MemberDao() {}
	
	
	public void insert(MemberVo vo) {
		// openSession()은 내부적으로 Connection을 가져온다.
		SqlSession sqlSession = DBManager.getSqlSessionFactory().openSession(true); // true는 AutoCommit
		// MemberMapper 인터페이스가 구현된 객체는
		// mybatis-config.xml 로딩시 이미 메모리에 등록되어있음.
		// 해당 타입으로 찾아서 리턴함
		MemberMapper mapper = sqlSession.getMapper(MemberMapper.class);
		
		int rowCount = mapper.insert(vo);
		
		if (rowCount > 0) {
			sqlSession.commit();
		} else {
			sqlSession.rollback();
		}
		// JDBC 자원닫기
		sqlSession.close();
	} // insert
	
	
	public int userCheck(String id, String passwd) {
		int check = -1; // -1: 아이디 없음, 0: 비밀번호 틀림, 1: 아이디 비밀번호 일치
		
		SqlSession sqlSession = null;
		try {
			sqlSession = DBManager.getSqlSessionFactory().openSession(true);
			MemberMapper mapper = sqlSession.getMapper(MemberMapper.class);
			
			String dbPasswd = mapper.getPasswdById(id);
			System.out.println("dbPasswd = " + dbPasswd);
			
			if (dbPasswd != null) {
				if (dbPasswd.equals(passwd)) {
					check = 1;
				} else {
					check = 0;
				}
			} else { // dbPasswd == null
				check = -1;
			}
		} finally {
			// JDBC 자원닫기
			sqlSession.close();
		}
		return check;
	} // userCheck
	
	
	public boolean isIdDuplicated(String id) {
		boolean isIdDup = false;
		
		try (SqlSession sqlSession = DBManager.getSqlSessionFactory().openSession(true)) {
			MemberMapper mapper = sqlSession.getMapper(MemberMapper.class);
			int count = mapper.countMemberById(id);
			if (count == 1) {
				isIdDup = true; // 아이디중복
			} else { // count == 0
				isIdDup = false; // 아이디중복아님
			}
		}
		//sqlSession은 자동으로 finally로 닫힘
		return isIdDup;
	}
	
	
	public List<MemberVo> getMembers() {
		List<MemberVo> list = null;
		
		try (SqlSession sqlSession = DBManager.getSqlSessionFactory().openSession(true)) {
			MemberMapper mapper = sqlSession.getMapper(MemberMapper.class);
			list = mapper.getMembers();
		}
		
		return list;
	} // getMembers()
	
	
	// id로 member 레코드(행) 한개 가져오기
	public MemberVo getMemberById(String id) throws SQLException{ 
		MemberVo vo = null;
		
		try (SqlSession sqlSession = DBManager.getSqlSessionFactory().openSession(true)) {
			MemberMapper mapper = sqlSession.getMapper(MemberMapper.class);
			vo = mapper.getMemberById(id);
		}
		
		if(vo == null) {
			throw new SQLException("id에 해당하는 MemberVo 존재 x");
			
		}
		
		return vo;
	} // getMemberById()
	
	
	public void update(MemberVo vo) {
		
		try (SqlSession sqlSession = DBManager.getSqlSessionFactory().openSession(true)) {
			MemberMapper mapper = sqlSession.getMapper(MemberMapper.class);
			mapper.update(vo);
		}
	} // update()
	
	
	public int deleteById(String id) {
		int rowCount = 0;
		
		try (SqlSession sqlSession = DBManager.getSqlSessionFactory().openSession(true)) {
			MemberMapper mapper = sqlSession.getMapper(MemberMapper.class);
			rowCount = mapper.deleteById(id);
		}
		
		return rowCount;
	} // deleteById()
	
	
	public void deleteAll() {
		
		try (SqlSession sqlSession = DBManager.getSqlSessionFactory().openSession(true)) {
			MemberMapper mapper = sqlSession.getMapper(MemberMapper.class);
			mapper.deleteAll();
		}
	} // deleteById()
	
	
	public int getCount() {
		int count = 0;
		
		try (SqlSession sqlSession = DBManager.getSqlSessionFactory().openSession(false)) {
			MemberMapper mapper = sqlSession.getMapper(MemberMapper.class);
			count = mapper.getCount();
		}
		
		return count;
	} // getMemberById()
	
	
	public static void main(String[] args) {
		
		MemberDao dao = MemberDao.getInstance();
		
		// getMembers()  테스트
		List<MemberVo> memberList = dao.getMembers();
		
		for (MemberVo vo : memberList) {
			System.out.println(vo.toString());
		}
		
		System.out.println("=========================");
		
		// getMemberById() 테스트
		//MemberVo memberVo = dao.getMemberById("qqq");
		//System.out.println(memberVo);
		
	} // main
	
	
}







