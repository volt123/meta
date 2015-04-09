package com.lgdisplay.wf;

import jspeed.base.query.DBAssistant;
import jspeed.websvc.WSParam;

import com.itplus.mm.common.util.SysHandler;
import com.itplus.mm.dao.workflow.MmapprrequestDAO;
import com.itplus.mm.server.workflow.MailSendRunner;
import com.itplus.wf.def.act.WfactModel;

public class ModelProcessMailFinish  implements com.itplus.wf.inst.app.IApplication{
 	public String execute(DBAssistant dba, WSParam params, WfactModel model) throws Exception {
 		 
		String title = SysHandler.getInstance().getProperty("MAIL_MODEL_FINISH_TITLE");
		
		MmapprrequestDAO dao = new MmapprrequestDAO(dba);
		jspeed.base.jdbc.CacheResultSet rs = dao.findFinishNotifyUser(params.toOneParameterMap());
		try
		{
		
			MailSendRunner mail = new MailSendRunner(dba, params);
			
			mail.setToInfo(rs);
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
