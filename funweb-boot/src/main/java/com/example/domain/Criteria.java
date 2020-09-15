package com.example.domain;

import lombok.Data;

@Data
public class Criteria {
	
	private int pageNum; //댓글 페이지 번호
	private int amount; //페이지다 ㅇ댓글 개수
	private int startRow; //시장 행번호

	public Criteria() {
		this(1,10); //인자 값을 많이 받는 생성자 쪽으로 값을 토스
	}
	
	public Criteria(int pageNum,int amount) {
		this.pageNum = pageNum;
		this.amount = amount;
		//MySQL기준 : 시장행번호 0
		this.startRow = (this.pageNum-1)*this.amount;
		
		//Oracle :  시작행번호 1
//		this.startRow = (this.pageNum-1)*this.amount+1;
	}
}
