package com.lgdisplay.code;

import java.io.FileWriter;
import java.util.HashMap;

import jspeed.base.jdbc.CacheResultSet;

import com.itplus.mm.common.util.ArrayHelper;
import com.itplus.mm.dao.ufw.TdeploymgtDAO;

public class MMDeployCurrServ implements IMMDeploy {

	public MMDeployCurrServ() {
	}

	public void deploy(DeployModel deployModel) throws Exception {
		com.itplus.mm.server.deploy.CodeDeployLogMgr deployLogMgr = null;
		try {
			Object codeDomId = deployModel.getDeployObjId();
			Object reqDt = deployModel.getReqDt();
			TdeploymgtDAO deploymgtDAO = new TdeploymgtDAO();
			StringBuffer keyList = new StringBuffer();
			StringBuffer keyList1 = new StringBuffer();
			StringBuffer keyList2 = new StringBuffer();
			ArrayHelper ah = new ArrayHelper();
			if (ah.isArray(codeDomId)) {
				Object codeDomIdArray[] = ah.fetchArrayOfArray(codeDomId);
				Object reqDtArray[] = ah.fetchArrayOfArray(reqDt);
				System.out.println("MMDeployCurrServ.codeDomIdArray=====>"
						+ codeDomIdArray);
				System.out.println("MMDeployCurrServ.reqDtArray=====>"
						+ reqDtArray);
				for (int i = 0; i < codeDomIdArray.length; i++)
					if (keyList.length() < 1) {
						keyList.append(" AND ((A.CODE_DOM_ID = '"
								+ (String) codeDomIdArray[i]
								+ "' AND A.REQ_DT = '" + (String) reqDtArray[i]
								+ "') ");
						keyList1.append(" WHERE (CODE_DOM_ID = '"
								+ (String) codeDomIdArray[i]
								+ "' AND REQ_DT < '" + (String) reqDtArray[i]
								+ "') ");
						keyList2.append(" AND ((A.CODE_DOM_ID = '"
								+ (String) codeDomIdArray[i]
								+ "' AND A.REQ_DT = '" + (String) reqDtArray[i]
								+ "') ");
					} else {
						keyList.append(" OR (A.CODE_DOM_ID = '"
								+ (String) codeDomIdArray[i]
								+ "' AND A.REQ_DT = '" + (String) reqDtArray[i]
								+ "') ");
						keyList1.append(" OR (CODE_DOM_ID = '"
								+ (String) codeDomIdArray[i]
								+ "' AND REQ_DT < '" + (String) reqDtArray[i]
								+ "') ");
						keyList2.append(" OR (A.CODE_DOM_ID = '"
								+ (String) codeDomIdArray[i]
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
			int columnCnt = codeRs.getColumnCount();
			while (codeRs.next()) {
				for (int i = 1; i <= columnCnt; i++)
					if (i == 1)
						deployContents = deployContents + codeRs.getString(i);
					else
						deployContents = deployContents + delimeter
								+ codeRs.getString(i);

				deployContents = deployContents + "\n";
			}
			columnCnt = codeValRs.getColumnCount();
			while (codeValRs.next()) {
				for (int i = 1; i <= columnCnt; i++)
					if (i == 1)
						deployContents = deployContents
								+ codeValRs.getString(i);
					else
						deployContents = deployContents + delimeter
								+ codeValRs.getString(i);

				deployContents = deployContents + "\n";
			}
			columnCnt = comboCodeValRs.getColumnCount();
			while (comboCodeValRs.next()) {
				for (int i = 1; i <= columnCnt; i++)
					if (i == 1)
						deployContents = deployContents
								+ comboCodeValRs.getString(i);
					else
						deployContents = deployContents + delimeter
								+ comboCodeValRs.getString(i);

				deployContents = deployContents + "\n";
			}
			String deployPath = deployModel.getDeployPath();
			String deployFileName = deployModel.getDeployFileName();
			System.out.println("deployPath + deployFileName==>" + deployPath
					+ "/" + deployFileName);
			FileWriter fileWriter = new FileWriter(deployPath + "/"
					+ deployFileName);
			fileWriter.write(deployContents);
			fileWriter.close();
			if (ah.isArray(codeDomId)) {
				Object codeDomIdArray[] = ah.fetchArrayOfArray(codeDomId);
				Object reqDtArray[] = ah.fetchArrayOfArray(reqDt);
				HashMap param = new HashMap();
				param.put("DEPLOY_DT", deployModel.getDeployDt());
				param.put("DEPLOY_TP_CD", deployModel.getDeployTpCd());
				param.put("DEPLOY_TRG_CD", deployModel.getDeployTrgCd());
				for (int i = 0; i < codeDomIdArray.length; i++) {
					param.put("REQ_DT", (String) reqDtArray[i]);
					param.put("DEPLOY_OBJ_ID", (String) codeDomIdArray[i]);
					param.put("DEPLOY_SUCC_YN", "Y");
					deploymgtDAO.insert(param);
				}

			}
		} catch (Exception e) {
			throw e;
		}
	}
}


/*
	DECOMPILATION REPORT

	Decompiled from: C:\MMDev\workspaces\HYCustomizingWorkspace\jars\MetaMiner30.jar
	Total time: 114 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/