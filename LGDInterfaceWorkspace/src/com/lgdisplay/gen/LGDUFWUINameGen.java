package com.lgdisplay.gen;

import java.util.ArrayList;
import java.util.HashMap;

import com.itplus.mm.common.util.ArrayHelper;
import com.itplus.mm.server.udp.AbstractUDPValGen;
import com.lgdisplay.db.LgdDao;

public class LGDUFWUINameGen extends AbstractUDPValGen {

	/**
	 * <pre>
	 * 항목의 UDP 중 UI명 생성 시 항목에 구성된 용어의 UI명을 이용하여 자동 생성  
	 * </pre>
	 * @param in
	 * 
	 * [용어]
	 * REQ_TP_CD	String	생성(WFGB_I), 수정(WFGB_U), 삭제(WFGB_D)
	 * REQ_EDIT_TYPE	String	생성(), 수정(UPDATE), 삭제(DELETE)
	 * UFW_DIC_ID	String	용어사전ID
	 * UFW_ID		String	용어 ID
	 * INFOTYPE_NM	String	인포타입 명
	 * CODE_DOM_NM	String	코드 도메인 명
	 * DAT_STRC_ID	String	데이터 구조 ID
	 * UFW_NM		String	표준항목 명
	 * PHSC_NM		String	컬럼물리 명
	 * UFW_DEF		String	정의
	 * UFW_DESC		String	설명
	 * UTW_ID		String[]	단어ID
	 * 
	 * @return
	 * @throws Exception
	 */		
	public String udpValGenerator(HashMap in) throws Exception {
		String ufwUIName = "";

		ArrayHelper ah = new ArrayHelper();
		Object utwIds = in.get("UTW_ID");
		LgdDao lgdDao = new LgdDao();
		
		if(ah.isArray(utwIds)) {
			ArrayList utwIdArray = new ArrayList();
			
			for (int i = 0; i < ((ArrayList)utwIds).size(); i++) {
				String utwUIName = lgdDao.getUtwUINameByUtwId((String)((ArrayList)utwIds).get(i));
				if (!("".equals(utwUIName))) {
					if (i == 0) ufwUIName =                   utwUIName;
					else 	    ufwUIName = ufwUIName + " " + utwUIName;
				}
			}
		} else {
			ufwUIName = lgdDao.getUtwUINameByUtwId((String)utwIds);
		}
		
		return ufwUIName;
	}

}
