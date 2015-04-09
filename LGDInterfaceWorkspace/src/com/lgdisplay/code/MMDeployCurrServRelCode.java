package com.lgdisplay.code;

import java.io.FileWriter;
import java.util.HashMap;

import jspeed.base.jdbc.CacheResultSet;

import com.itplus.mm.common.util.ArrayHelper;
import com.itplus.mm.dao.ufw.TdeploymgtDAO;

public class MMDeployCurrServRelCode implements IMMDeploy {

	public MMDeployCurrServRelCode() {
	}

	public void deploy(DeployModel deployModel) throws Exception {
		TdeploymgtDAO deploymgtDAO = null;
		try {
			Object deployObjId = deployModel.getDeployObjId();
			Object reqDt = deployModel.getReqDt();
			deploymgtDAO = new TdeploymgtDAO();
			StringBuffer keyList = new StringBuffer();
			ArrayHelper ah = new ArrayHelper();
			if (ah.isArray(deployObjId)) {
				Object deployObjIdArray[] = ah.fetchArrayOfArray(deployObjId);
				Object reqDtArray[] = ah.fetchArrayOfArray(reqDt);
				System.out.println("MMDeployCurrServRelCode.deployObjId=====>"
						+ deployObjIdArray);
				System.out.println("MMDeployCurrServRelCode.reqDtArray=====>"
						+ reqDtArray);
				for (int i = 0; i < deployObjIdArray.length; i++)
					if (keyList.length() < 1)
						keyList.append(" AND ((CODE_VAL_REL_ID = '"
								+ (String) deployObjIdArray[i]
								+ "' AND REQ_DT = '" + (String) reqDtArray[i]
								+ "') ");
					else
						keyList.append(" OR (CODE_VAL_REL_ID = '"
								+ (String) deployObjIdArray[i]
								+ "' AND REQ_DT = '" + (String) reqDtArray[i]
								+ "') ");

				if (keyList.length() > 1)
					keyList.append(" ) ");
			}
			if (keyList.length() < 1)
				keyList.append(" AND 1 <> 1 ");
			HashMap in = new HashMap();
			HashMap p = new HashMap();
			p.put("CODE_VAL_REL_ID_LIST", keyList.toString());
			CacheResultSet relCodeRs = deploymgtDAO.findByCodedomvalrelDeploy(
					in, p);
			deployModel.setRelCodeCnt(relCodeRs.getRowCount());
			String deployContents = "";
			String delimeter = deployModel.getDelimeter();
			int columnCnt = relCodeRs.getColumnCount();
			while (relCodeRs.next()) {
				for (int i = 1; i <= columnCnt; i++)
					if (i == 1)
						deployContents = deployContents
								+ relCodeRs.getString(i);
					else
						deployContents = deployContents + delimeter
								+ relCodeRs.getString(i);

				deployContents = deployContents + "\n";
			}
			String deployPath = deployModel.getDeployPath();
			String deployFileName = deployModel.getDeployDt() + "_"
					+ deployModel.getDeployFileName();
			System.out.println("deployPath + deployFileName==>" + deployPath
					+ "/" + deployFileName);
			FileWriter fileWriter = new FileWriter(deployPath + "/"
					+ deployFileName);
			fileWriter.write(deployContents);
			fileWriter.close();
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
	Total time: 37 ms
	Jad reported messages/errors:
Couldn't resolve all exception handlers in method deploy
	Exit status: 0
	Caught exceptions:
*/