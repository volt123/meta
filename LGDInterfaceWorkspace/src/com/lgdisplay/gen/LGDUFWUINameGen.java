package com.lgdisplay.gen;

import java.util.ArrayList;
import java.util.HashMap;

import com.itplus.mm.common.util.ArrayHelper;
import com.itplus.mm.server.udp.AbstractUDPValGen;
import com.lgdisplay.db.LgdDao;

public class LGDUFWUINameGen extends AbstractUDPValGen {

	/**
	 * <pre>
	 * �׸��� UDP �� UI�� ���� �� �׸� ������ ����� UI���� �̿��Ͽ� �ڵ� ����  
	 * </pre>
	 * @param in
	 * 
	 * [���]
	 * REQ_TP_CD	String	����(WFGB_I), ����(WFGB_U), ����(WFGB_D)
	 * REQ_EDIT_TYPE	String	����(), ����(UPDATE), ����(DELETE)
	 * UFW_DIC_ID	String	������ID
	 * UFW_ID		String	��� ID
	 * INFOTYPE_NM	String	����Ÿ�� ��
	 * CODE_DOM_NM	String	�ڵ� ������ ��
	 * DAT_STRC_ID	String	������ ���� ID
	 * UFW_NM		String	ǥ���׸� ��
	 * PHSC_NM		String	�÷����� ��
	 * UFW_DEF		String	����
	 * UFW_DESC		String	����
	 * UTW_ID		String[]	�ܾ�ID
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
