package com.lgdisplay.wf;

import jspeed.base.query.DBAssistant;
import jspeed.websvc.WSParam;

import com.itplus.mm.common.util.SysHandler;
import com.itplus.mm.server.workflow.MailSendRunner;
import com.itplus.wf.def.act.WfactModel;
import com.lgdisplay.util.JLog;

public class StdProcessMailFinish  implements com.itplus.wf.inst.app.IApplication{
 	public String execute(DBAssistant dba, WSParam params, WfactModel model) throws Exception {
 		 
		String title = SysHandler.getInstance().getProperty("MAIL_STD_FINISH_TITLE");
		
		try {
			JLog.debug("=======================================================");
			JLog.debug(":::::::StdProcessMailFinish 승인시 수신자:::::::::"+params.getParameterObj("REQ_USER_ID").toString());
			JLog.debug("=======================================================");

			MailSendRunner mail = new MailSendRunner(dba, params);
			mail.addToId(params.getParameterObj("REQ_USER_ID").toString());
			mail.setSenderToMe();
			mail.send(title);
		}
		 
		catch(Exception ignore)
		{
			ignore.printStackTrace();
			System.out.println("[MAIL SEND ERROR]" + ignore.getMessage());
		}
		return "success";
		 
	}
}
