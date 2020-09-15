package com.example.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.example.domain.MailDto;

@Component
public class MyMailSender{
	
	@Autowired
	private JavaMailSender mailSender; 
	
	private static final String FROM_ADDRESS = "taetinn@naver.com";
	
	public void sendTextMail(MailDto mailDto) {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom(FROM_ADDRESS);
		msg.setTo(mailDto.getAddress());
		msg.setSubject(mailDto.getTitle());
		msg.setText(mailDto.getMessage());
		
		mailSender.send(msg);
	}
	
	
	//html 형식의 내용과 첨부파일 첨부가능
		public void sendHtmlMail(MailDto mailDto) {
			try {
				
				MailHandler mailHandler = new MailHandler(mailSender);
				
				// 보내는 사람
				mailHandler.setFrom(FROM_ADDRESS);
				
				// 받는 사람
				mailHandler.setTo(mailDto.getAddress());
				
				// 메일 제목
				mailHandler.setSubject(mailDto.getTitle());
				
				// 메일 내용 (html 문서형식)
				String htmlContent = "<p>" + mailDto.getMessage() +"</p>";
				htmlContent += "<img src='cid:img1'>";//키값으로 img 소스 경로 대체
				mailHandler.setText(htmlContent,true);
				
				//이미지 삽입
				mailHandler.setInline("img1", "static/images/main_img.jpg");//key val:img1
				
				//첨부파일
				mailHandler.setAttach("jsp.txt", "static/26341c34-512b-48f1-905b-19b5e9b2fe13_jsp1.txt");
				
				//메일전송
				mailHandler.send();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

}
