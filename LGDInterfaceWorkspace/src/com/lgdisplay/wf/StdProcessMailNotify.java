package com.lgdisplay.wf;

import java.util.ArrayList;

import jspeed.base.jdbc.CacheResultSet;
import jspeed.base.query.DBAssistant;
import jspeed.websvc.WSParam;

import com.itplus.mm.common.util.SysHandler;
import com.itplus.mm.server.workflow.MailSendRunner;
import com.itplus.mm.server.workflow.app.AgreeUserListGetApp;
import com.itplus.wf.def.act.WfactModel;

public class StdProcessMailNotify  implements com.itplus.wf.inst.app.IApplication{
 	public String execute(DBAssistant dba, WSParam params, WfactModel model) throws Exception {
 		 
		String title = SysHandler.getInstance().getProperty("MAIL_STD_TITLE");	// 선택된 협의자에게 보내는 메일 제목
		String title2 = SysHandler.getInstance().getProperty("MAIL_STD_APPROVER_TITLE");	// 선택되지 않은 협의자에게 보내는 메일 제목
		
		AgreeUserListGetApp agreeUsers = new AgreeUserListGetApp();
		jspeed.base.jdbc.CacheResultSet rs = agreeUsers.getFieldList(dba, params);
		String[] agreeUserIds = params.getParameter("AGREE_USER_IDS").split(",");	// 결재 선택된 협의자 목록
		
		String[] fieldList = new String[] {"USER_ID","USER_NM","EMAIL","USER_IP"};
		ArrayList value1 = new ArrayList();
		ArrayList value2 = new ArrayList();
		try
		{
		
			jspeed.base.jdbc.CacheResultSet rsNextApprover = new CacheResultSet(fieldList, value1);
			jspeed.base.jdbc.CacheResultSet rsAgreeApprover = new CacheResultSet(fieldList, value2);
			
			while (rs.next()){
				boolean flag = false;
				
				for (int i=0;i<agreeUserIds.length;i++){
					if (rs.getString("USER_ID").equals(agreeUserIds[i])){
						flag = true;
						break;
					}
				}

				Object[] obj = new Object[4];
				obj[0] = rs.getString("USER_ID");
				obj[1] = rs.getString("USER_NM");
				obj[2] = rs.getString("EMAIL");
				obj[3] = rs.getString("USER_IP");
				
				if (flag){	// 선택된 협의자 일 경우
					rsNextApprover.addRow(obj);
				}else{	// 선택되지 않은 협의자 일 경우
					rsAgreeApprover.addRow(obj);
				}
			}
			
			MailSendRunner mail = new MailSendRunner(dba, params);
			// 선택된 협의자에게 메일 발송
			if (rsNextApprover != null){
				mail.setToInfo(rsNextApprover);
//				mail.setSenderToMe();
				mail.send(title);
			}
			
			mail = new MailSendRunner(dba, params);
			// 선택되지 않은 협의자에게 메일 발송
			if (rsAgreeApprover != null){
				mail.setToInfo(rsAgreeApprover);
//				mail.setSenderToMe();
				mail.send(title2);
			}
			
		}
		 
		catch(Exception ignore)
		{
			System.out.println("[MAIL SEND ERROR]" + ignore.getMessage());
		}
		return "success";
		 
	}
}
