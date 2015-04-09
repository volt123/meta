package com.lgdisplay.wf;

import java.util.HashMap;

import jspeed.base.jdbc.CacheResultSet;
import jspeed.base.jdbc.QueryHelper;
import jspeed.base.query.DBAssistant;
import jspeed.websvc.WSParam;

import com.itplus.wf.def.act.WfactModel;
import com.itplus.wf.def.table.IFieldListValue;
import com.lgdisplay.db.LgdDao;

//협의자  정보
public class StandardAgreeUserListGetApp implements IFieldListValue ,com.itplus.wf.inst.app.IApplication
{
	/**
	 * WORKS_BOX_ID,   
	 * ALL_KUBUN : ALTP_A(전체)/ ALTP_S(성공)/ ALTP_F(실패),  
	 * PROC_ID, 
	 * TRG_IDS(array, TRG_TP_CD, TRG_ID  ) , 
	 * DAT_STRC_ID
	 * INST_ID
	 */
	 	 
	boolean debugMode = false;

	public CacheResultSet getFieldList(QueryHelper qHelper, WSParam param) throws Exception {
		DBAssistant dba = new DBAssistant(this);
		dba.setQueryHelper(qHelper); 
		return getFieldList(dba, param);
 		
 
	}
	public CacheResultSet getFieldList(DBAssistant dba,WSParam param) throws Exception
	{
//		MmworksboxdetailDAO dao = null;
//		MmapprrequestdetailDAO dao2 = null;
		LgdDao lgdDao = null;
		
//		CacheResultSet rs = null;		
		CacheResultSet reqAgreeUserRs = null;
		 
//		if(param.getParameter("INST_ID") == null || "".equals(param.getParameter("INST_ID"))) //not processing
//		{
//			dao = new MmworksboxdetailDAO(dba);
//			rs = ((MmworksboxdetailDAO)dao).findByBoxId(param.getHashMap());
//		}
//		else // in proccessing
//		{
//			dao2 = new MmapprrequestdetailDAO(dba);
//			rs = ((MmapprrequestdetailDAO)dao2).findByApprId(param.getHashMap());
//		}
			
		lgdDao =  new LgdDao(dba);
		
		HashMap p = new HashMap();

		p.put("SESS_USER_ID", param.getParameter("SESS_USER_ID"));

		if (debugMode) {
			System.out.println ("############# Debuging - Start ###############");
			System.out.println ("SESS_USER_ID : " + (String)param.getParameter("SESS_USER_ID"));
			System.out.println ("################ Debuging - End ###############");
		}

		reqAgreeUserRs = lgdDao.findAllByStandardAgreeUser(p);
		
		if (debugMode) {
			System.out.println ("############# Debuging - Start ###############");
			System.out.println ("reqAgreeUserRs = lgdDao.findAllByStandardAgreeUser(p);" );
			System.out.println ("################ Debuging - End ###############");
		}

		return reqAgreeUserRs;
	}
	
	public String execute(DBAssistant dba, WSParam params, WfactModel model) throws Exception {
		
		params.addOutputHash("AGREE_USER_INFO", getFieldList(dba, params));
		return "success";
	}
}



