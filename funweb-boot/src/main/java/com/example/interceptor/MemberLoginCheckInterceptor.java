package com.example.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


@Component
public class MemberLoginCheckInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception { //controller 실행 전 컨트롤러 실행여부를 t/f값으로 판단

		HttpSession session = request.getSession();

		// 세션값 가져오기
		String id = (String) session.getAttribute("id");
		// 로그인 안했으면(세션값 없으면) /member/login 리다이렉트 이동
		if (id == null) {
			response.sendRedirect("/member/login");
			return false; //false 리턴 시 컨트롤러 메소드 실행x
		}

		return true; //컨트롤러 메소드 실행o
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}

}
