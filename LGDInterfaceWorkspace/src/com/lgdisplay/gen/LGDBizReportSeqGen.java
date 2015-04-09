package com.lgdisplay.gen;

import java.util.HashMap;

import com.itplus.mm.server.udp.AbstractUDPValGen;
import com.lgdisplay.db.LgdDao;

public class LGDBizReportSeqGen extends AbstractUDPValGen {

	/**
	 * <pre>
	 *  Biz Report Sequence Generator  
	 * </pre>
	 * @param in
	 * @return
	 * @throws Exception
	 */	
	public String udpValGenerator(HashMap in) throws Exception {
		
		// REQ_TP_CD	String	생성(WFGB_I), 수정(WFGB_U), 삭제(WFGB_D)
		String insertStat = (String)in.get("REQ_TP_CD");
		String returnValue = (String)in.get("STR_VAL");
		
		if ("WFGB_I".equals(insertStat)) {
			if (null == returnValue || "".equals(returnValue)) {
				LgdDao lgdDao = new LgdDao();
				returnValue = lgdDao.getSeq("BizReport");
			}
		} 
		
		return returnValue;
	}
}
