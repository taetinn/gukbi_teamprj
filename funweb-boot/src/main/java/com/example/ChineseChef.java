package com.example;

import org.springframework.stereotype.Component;

@Component //스프링에서 관리하는 객체 범위안에 포함됨
public class ChineseChef implements Chef {

	@Override
	public void doCook() {
		System.out.println("cooking chinese");

	}

}
