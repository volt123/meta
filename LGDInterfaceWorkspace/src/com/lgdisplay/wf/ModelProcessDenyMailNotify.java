package com.lgdisplay.wf;

import java.util.ArrayList;
import java.util.HashMap;

import jspeed.base.query.DBAssistant;
import jspeed.websvc.WSParam;

import com.itplus.mm.common.util.SysHandler;
import com.itplus.mm.server.workflow.MailSendRunner;
import com.itplus.wf.def.act.WfactModel;

public class ModelProcessDenyMailNotify  implements com.itplus.wf.inst.app.IApplication{
 	public String execute(DBAssistant dba, WSParam params, WfactModel model) throws Exception {
 		 
		String title = SysHandler.getInstance().getProperty("MAIL_MODEL_DENY_TITLE");
		System.out.println("#################################################");
		System.out.println("HashMap::::::::"+params.getHashMap().toString());
		System.out.println("#################################################");
//		MmapprrequestDAO dao = new MmapprrequestDAO(dba);
//		jspeed.base.jdbc.CacheResultSet rs = dao.findFinishNotifyUser(params.toOneParameterMap());
		try
		{
//			MailSendRunner mail = new MailSendRunner(dba, params);
//			mail.addToId(params.getParameterObj("REQ_USER_ID").toString());
//			mail.setToInfo(rs);
			MessengerContent maildeny = new MessengerContent();
			ArrayList olist = maildeny.toPreformerDeny(dba, params, model);
			String previousPerformer = "";
			if ( null != olist && 0 != olist.size() ) previousPerformer = (String)((HashMap)olist.get(0)).get("USER_ID");
			MailSendRunner mail = new MailSendRunner(dba, params);
			mail.addToId(previousPerformer);
			mail.setSenderToMe();
			Object[] o = new Object[0];
			mail.setToInfo(o);
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
