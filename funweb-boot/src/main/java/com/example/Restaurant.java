package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*
 * Spring Web MVC 모듈에서
 * @Component를 상속받는 애노테이셔 ㄴ종류
 * - @Controller 컨트롤러 역할을 하는 클래스에 사용
 * - @Service 	트랜잭션 처리를 수행하는 클ㄹ래스에 사용
 * - @Repository DAO클래스에 사용
 */

@Component //스프링에서 관리하는 객체 범위안에 포함됨
public class Restaurant {
	
	@Autowired // setter 노 필요 . restaurant 이 chef를 이용한다라는 의존관계 형성을 위해 :: 자동으로 객체 연결
	private Chef chef;
	
	public void makeDish() {
		chef.doCook();
	}

}
