package test;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class TestMailSender {
 
	String id="leewow@lgdpartner.com";
    String pwd="dlehddn09";
    String to="leewow@lgdpartner.com";
    String from="leewow@lgdpartner.com"; //보내는 주소는 서버 이름과 같아야함.

    public void sendMail() {
    	
		Properties properties = System.getProperties();
		properties.put("mail.transport.protocol", "smtp");
		properties.put("mail.smtp.host", "gwsmtp.lgdisplay.com");

		Session msgSession = Session.getDefaultInstance(properties, null);
		msgSession.setDebug(true);

		try {
			Message testMsg = new MimeMessage(msgSession);

			testMsg.setFrom(new InternetAddress(from, "MetaMiner"));
			InternetAddress[] address = {new InternetAddress(to, "Meta 담당자")};
			testMsg.setRecipients(Message.RecipientType.TO, address);
			testMsg.setSubject("Send Mail Test by leewow - Send Mail Test  한글");
			testMsg.setSentDate(new Date());
			String msgStr = new String("Send Mail Test  한글, Русское испытание");
			
			testMsg.setText(msgStr);
			
			Transport.send(testMsg);

		} catch(Exception e) {
			e.printStackTrace();
		}
    }
    
    public static void main(String[] args){
    	
    	TestMailSender testMailSender = new TestMailSender ();
    	testMailSender.sendMail();
    }
}
