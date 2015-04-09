package com.lgdisplay.wf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import jspeed.base.query.DBAssistant;

import com.itplus.mm.common.util.StringUtils;
import com.itplus.mm.common.util.SysHandler;
import com.lgdisplay.util.URLReader;

public class MessengerSender implements
		com.itplus.mm.server.workflow.MailSender {

	final int DEBUG_MODE = 1;

	int runtime_mode;

	String server_ip;

	String server_port;

	Properties props = new Properties();

	public MessengerSender() {
		super();
		getAlarmInfo();
	}

	/**
	 * 메일 발송
	 * @param senderId 메일 발송자ID
	 * @param senderName 메일 발송자 성명
	 * @param email 메일 발송자 메일 주소
	 * @param toId_Name 메일 수신자 ID와 성명 들
	 * @param title 메일 제목
	 * @param contentUrl 메일 내용
	 * @throws Exception
	 */
	public void sendMail(DBAssistant dba, String senderId, String senderName, String email,
			String[][] toId_Name, String title, String contentUrl)
			throws Exception {
		System.out.println("........................................................");
		System.out.println("......................MESSENGER.........................");
		System.out.println("........................................................");
		sendMail(dba, senderName , email, toId_Name, title, contentUrl);

	}
	
    // 메일 발송 메소드
	public void sendMail(DBAssistant dba, String senderName, String senderemail, String[][] mailTo, String title,
			String contentUrl) throws Exception {
		
		URLReader urlreader = new URLReader();
		
		String s = contentUrl;
		MessengerContent smtp = new MessengerContent();
		contentUrl = smtp.getMailContent(urlreader.getParam(s),dba);
		String mailtitle = smtp.getMailTitle(urlreader.getParam(s),dba);
		ArrayList olist = smtp.toUser(senderemail, mailTo, dba);
		title = title.replaceAll("MAILTITLE", mailtitle);
		
		String METAMINER_LOGIN_URL = SysHandler.getInstance().getProperty("METAMINER_LOGIN_URL");
		
		System.out.println(".....title:::"+title+"::");
		System.out.println(".....contentUrl:::"+contentUrl+"::");

		Session msgSession = Session.getDefaultInstance(props, null);
		msgSession.setDebug(true);
		for (int i = 0; i < olist.size(); i++) {
			HashMap omap = (HashMap)olist.get(i);
			try {
				MimeMessage msg = new MimeMessage(msgSession);
				InternetAddress from = new InternetAddress(senderemail,senderName,"euc-kr");

				//String loginId = "";
				//String str = "<br><a href='" + METAMINER_LOGIN_URL + "?loginId=" + loginId + "' target='_blank'>메타마이너 시스템으로 이동</a>";
				String str = "<br><a href='" + METAMINER_LOGIN_URL + "' target='_blank'>EIMS 시스템으로 이동</a>";

				System.out.println(".....from user:::"+from.getAddress()+"::"+from.getPersonal());
				System.out.println(".....to user:::"+(String)omap.get("EMAIL")+"::"+(String)omap.get("USER_NM"));
				msg.setFrom(from);
				
				String[ ] recipients = null;
				if ( null == recipients ) recipients = new String[0];
				
				InternetAddress[] addressTo = new InternetAddress[1];
			    addressTo[0] = new InternetAddress((String)omap.get("EMAIL"),(String)omap.get("USER_NM"), "euc-kr");
			    msg.setRecipients(Message.RecipientType.TO, addressTo);
	
				msg.setSubject(title);
	
				msg.setContent(contentUrl + str, "text/html; charset=EUC-KR");
	
				Transport.send(msg);
			} catch (AddressException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
		
	}

	private void getAlarmInfo() {
		runtime_mode = 1;
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.host", "gwsmtp.lgdisplay.com");
	}

	public void sendAlarm(String userIp, String message) throws Exception {

	}
	
	public HashMap getParam(String sUrls) {
		HashMap oMap = new HashMap();
		String[] saS = StringUtils.split(sUrls,"?");
		String[] saS2 = StringUtils.split(saS[1],"&");
		for (int i=0;i<saS2.length;i++){
			String[] saS3 = StringUtils.split(saS2[i],"=");
			oMap.put(saS3[0], saS3[1]);
		}
		return oMap;
	}
}
