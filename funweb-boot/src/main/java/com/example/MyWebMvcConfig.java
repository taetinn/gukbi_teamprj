package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.interceptor.MemberLoginCheckInterceptor;
import com.example.interceptor.MemberStayLoggedInInterceptor;


@Configuration
public class MyWebMvcConfig implements WebMvcConfigurer{
	
	@Autowired
	private  MemberLoginCheckInterceptor memberLoginCheckInterceptor;
	@Autowired
	private MemberStayLoggedInInterceptor memberStayLoggedInInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		//방법1.
//		registry.addInterceptor(new MemberLoginCheckInterceptor());
		
		//방법2. 스프링방식 -- autowired이요해서 선언 후 , 
		InterceptorRegistration reg = registry.addInterceptor(memberLoginCheckInterceptor);
		reg.addPathPatterns("/board/file*")
		.excludePathPatterns("/board/fileList");
		
		registry.addInterceptor(memberStayLoggedInInterceptor).addPathPatterns("/*");
		//.excludePathPatterns("/css/**","/fonts/**","/images/**","/script/**");
		
	}

}
