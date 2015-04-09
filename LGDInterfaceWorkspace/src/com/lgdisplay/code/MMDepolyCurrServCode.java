package com.lgdisplay.code;

import java.util.HashMap;

import jspeed.base.jdbc.BaseSQLException;
import jspeed.base.jdbc.CacheResultSet;

import com.itplus.mm.common.util.ArrayHelper;
import com.itplus.mm.dao.ufw.TdeploymgtDAO;
import com.itplus.mm.server.deploy.DeployModel;
import com.itplus.mm.server.deploy.IMMDeploy;

public class MMDepolyCurrServCode implements IMMDeploy {

	
	public void deploy(DeployModel deployModel) throws Exception {
		TdeploymgtDAO deploymgtDAO = null;
		try {
			Object deployObjId = deployModel.getDeployObjId();
			Object reqDt = deployModel.getReqDt();
			deploymgtDAO = new TdeploymgtDAO();
			StringBuffer keyList = new StringBuffer();
			StringBuffer keyList1 = new StringBuffer();
			StringBuffer keyList2 = new StringBuffer();
			ArrayHelper ah = new ArrayHelper();
			if (ah.isArray(deployObjId)) {
				Object deployObjIdArray[] = ah.fetchArrayOfArray(deployObjId);
				Object reqDtArray[] = ah.fetchArrayOfArray(reqDt);
				System.out
						.println("MMDeployCurrServCode.deployObjIdArray=====>"
								+ deployObjIdArray);
				System.out.println("MMDeployCurrServCode.reqDtArray=====>"
						+ reqDtArray);
				for (int i = 0; i < deployObjIdArray.length; i++)
					if (keyList.length() < 1) {
						keyList.append(" AND ((A.CODE_DOM_ID = '"
								+ (String) deployObjIdArray[i]
								+ "' AND A.REQ_DT = '" + (String) reqDtArray[i]
								+ "') ");
						keyList1.append(" WHERE (CODE_DOM_ID = '"
								+ (String) deployObjIdArray[i]
								+ "' AND REQ_DT < '" + (String) reqDtArray[i]
								+ "') ");
						keyList2.append(" AND ((A.CODE_DOM_ID = '"
								+ (String) deployObjIdArray[i]
								+ "' AND A.REQ_DT = '" + (String) reqDtArray[i]
								+ "') ");
					} else {
						keyList.append(" OR (A.CODE_DOM_ID = '"
								+ (String) deployObjIdArray[i]
								+ "' AND A.REQ_DT = '" + (String) reqDtArray[i]
								+ "') ");
						keyList1.append(" OR (CODE_DOM_ID = '"
								+ (String) deployObjIdArray[i]
								+ "' AND REQ_DT < '" + (String) reqDtArray[i]
								+ "') ");
						keyList2.append(" OR (A.CODE_DOM_ID = '"
								+ (String) deployObjIdArray[i]
								+ "' AND A.REQ_DT = '" + (String) reqDtArray[i]
								+ "') ");
					}

				if (keyList.length() > 1)
					keyList.append(" ) ");
				if (keyList2.length() > 1)
					keyList2.append(" ) ");
			}
			if (keyList.length() < 1)
				keyList.append(" AND 1 <> 1 ");
			if (keyList1.length() < 1)
				keyList1.append(" WHERE 1 <> 1 ");
			if (keyList2.length() < 1)
				keyList2.append(" AND 1 <> 1 ");
			HashMap in = new HashMap();
			HashMap p = new HashMap();
			in.put("CODE_DOM_ID_LIST", keyList.toString());
			p.put("CODE_DOM_ID_LIST", keyList.toString());
			p.put("CODE_DOM_ID_LIST_1", keyList1.toString());
			p.put("CODE_DOM_ID_LIST_2", keyList2.toString());
			
			CacheResultSet codeRs = deploymgtDAO.findByCodeDeploy(in, p);
			CacheResultSet codeValRs = deploymgtDAO.findByCodevalDeploy(in, p);
			CacheResultSet comboCodeValRs = deploymgtDAO
					.findByCombocodevalDeploy(in, p);
			deployModel.setCodeCnt(codeRs.getRowCount());
			deployModel.setCodeValCnt(codeValRs.getRowCount());
			deployModel.setComboCodeValCnt(comboCodeValRs.getRowCount());
			String deployContents = "";
			String delimeter = deployModel.getDelimeter();

			// 코드 배포 DATA_CD ! REQ_TP_CD ! MNGM_ID ! CODE_DOM_NM ! CODE_VAL_CNT
			// 코드 배포 모듈을 호출함
			try {
				LGDCodeDeployer codeDeployer = new LGDCodeDeployer();
				codeDeployer.deployCode(codeRs);
			} catch (BaseSQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// 코드 배포 DATA_CD ! REQ_TP_CD ! MNGM_ID ! CODE_DOM_NM ! CODE_VAL_CNT
//			int columnCnt = codeRs.getColumnCount();
//			while (codeRs.next()) {
//				for (int i = 1; i <= columnCnt; i++)
//					if (i == 1)
//						deployContents = deployContents + codeRs.getString(i);
//					else
//						deployContents = deployContents + delimeter
//								+ codeRs.getString(i);
//
//				deployContents = deployContents + "\n";
//			}
			

		} catch (Exception e) {
			throw e;
		} finally {
			deploymgtDAO.close();
		}
		return;
	}

	

	
	
}


/*
	DECOMPILATION REPORT

	Decompiled from: C:\MMDev\workspaces\HYCustomizingWorkspace\jars\MetaMiner30.jar
	Total time: 28 ms
	Jad reported messages/errors:
Couldn't resolve all exception handlers in method deploy
	Exit status: 0
	Caught exceptions:
*/