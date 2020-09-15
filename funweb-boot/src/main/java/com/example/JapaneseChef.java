package com.example;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary //컴포넌트 우선순위 결정
@Component
public class JapaneseChef implements Chef {

	@Override
	public void doCook() {
		System.out.println("cooking japanese");
	}

}
