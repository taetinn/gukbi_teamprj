package com.example.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.example.domain.AttachfileVo;
import com.example.mapper.AttachfileMapper;

public class AttachfileDao {
	
	private static AttachfileDao instance = new AttachfileDao();
	
	public static AttachfileDao getInstance() {
		return instance;
	}

	private AttachfileDao() {
	}
	
	public AttachfileVo getAttachfileByUuid(String uuid) {
		AttachfileVo vo = null;
		
		try (SqlSession sqlSession = DBManager.getSqlSessionFactory().openSession(true)) {
			AttachfileMapper mapper = sqlSession.getMapper(AttachfileMapper.class);
			vo = mapper.getAttachfileByUuid(uuid);
		}
		
		return vo;
	}
	
	public List<AttachfileVo> getAttachfilesByBno(int bno) {
		List<AttachfileVo> list = null;
		
		try (SqlSession sqlSession = DBManager.getSqlSessionFactory().openSession(true)) {
			AttachfileMapper mapper = sqlSession.getMapper(AttachfileMapper.class);
			list = mapper.getAttachfilesByBno(bno);
		}
		
		return list;
	}
	
	// 첨부파일정보 한개 추가
	public void insert(AttachfileVo vo) {
		try (SqlSession sqlSession = DBManager.getSqlSessionFactory().openSession(true)) {
			AttachfileMapper mapper = sqlSession.getMapper(AttachfileMapper.class);
			mapper.insert(vo);
		}
	} // insert
	
	
	// 첨부파일정보 여러개 추가
	public void insert(List<AttachfileVo> list) {
		for (AttachfileVo vo : list) {
			this.insert(vo);
		}
	} // insert
	
	
	public void deleteAttachfilesByBno(int bno) {
		try (SqlSession sqlSession = DBManager.getSqlSessionFactory().openSession(true)) {
			AttachfileMapper mapper = sqlSession.getMapper(AttachfileMapper.class);
			mapper.deleteAttachfilesByBno(bno);
		}
	}
	
	
	public void deleteAttachfileByUuid(String uuid) {
		try (SqlSession sqlSession = DBManager.getSqlSessionFactory().openSession(true)) {
			AttachfileMapper mapper = sqlSession.getMapper(AttachfileMapper.class);
			mapper.deleteAttachfileByUuid(uuid);
		}
	}
	
	
	public void deleteAttachfilesByUuids(List<String> uuids) {
		try (SqlSession sqlSession = DBManager.getSqlSessionFactory().openSession(true)) {
			AttachfileMapper mapper = sqlSession.getMapper(AttachfileMapper.class);
			mapper.deleteAttachfilesByUuids(uuids);
		}
	}
	
}









