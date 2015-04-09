package com.lgdisplay.wf;

import jspeed.base.query.DBAssistant;
import jspeed.websvc.WSParam;

import com.itplus.mm.common.util.SysHandler;
import com.itplus.mm.server.workflow.MailSendRunner;
import com.itplus.wf.def.act.WfactModel;
import com.itplus.wf.work.WorkParam;

public class NextParticipantsMailNotify  implements com.itplus.wf.inst.app.IApplication{
 	public String execute(DBAssistant dba, WSParam params, WfactModel model) throws Exception {
 		 
		String title = SysHandler.getInstance().getProperty("MAIL_NEXT_PARTICIPANTS_TITLE");
		
		WorkParam wParam = new WorkParam(params);
		java.util.ArrayList nextParticipants = wParam.getNextParticipants();
		try
		{
		
			MailSendRunner mail = new MailSendRunner(dba, params);
			mail.setToInfo(nextParticipants.toArray());
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
