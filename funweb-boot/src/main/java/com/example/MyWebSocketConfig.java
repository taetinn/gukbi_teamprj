package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import com.example.websocket.MyTextWebSocketHandler;

@Configuration // component계열이라서 자동으로 객체 생성
@EnableWebSocket //웹소켓 기능 활성화
public class MyWebSocketConfig implements WebSocketConfigurer {

	@Autowired
	private MyTextWebSocketHandler webSocketHandler;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(webSocketHandler, "/chatting")
				.addInterceptors(new HttpSessionHandshakeInterceptor()) //httpsession에 있는attributes들을 websocketsession으로 복사해줌
				.setAllowedOrigins("*"); //
		/// chatting이라는 url주소가 오면 여기로 연결한다.
		// "/chatting"은 소켓연결을 위한 주소로 http연결이 아닌 ws연결이 되어야 한다. (ws : websocket)
	}

	@Bean
	public TaskScheduler taskScheduler() { //메소드가 리턴한 객체가 빈이됨. //쿼츠 스케줄러에러 방지용
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

		scheduler.setPoolSize(2);
		scheduler.setThreadNamePrefix("scheduled-task-");
		scheduler.setDaemon(true);

		return scheduler;
	}

}
