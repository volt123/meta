package com.lgdisplay.code;

import java.util.HashMap;

import com.itplus.mm.common.util.ArrayHelper;
import com.itplus.mm.common.util.StringUtils;
import com.itplus.mm.dao.ufw.TdeploymgtDAO;
import com.itplus.mm.dao.ufw.TdeploymgtlogDAO;

// Referenced classes of package com.itplus.mm.server.deploy:
//            DeployModel

public class CodeDeployLogMgr {

	public CodeDeployLogMgr() throws Exception {
	}

	public void writeLog(DeployModel deployModel) throws Exception {
		System.out
				.println("@@@@@@@@@@@@@@@@@@@@@@@@@ CodeDeployLogMgr.writeLog() start.. @@@@@@@@@@ ");
		TdeploymgtDAO deploymgtDAO = new TdeploymgtDAO();
		TdeploymgtlogDAO deploymgtlogDAO = new TdeploymgtlogDAO();
		String logMessage = deployModel.getDeployRtnMsg();
		if ("MM_CODE_DOM_REQ".equals(deployModel.getDeployTpCd())) {
			logMessage = StringUtils
					.replace(
							logMessage,
							"%1",
							(new StringBuffer(String.valueOf(deployModel
									.getCodeCnt()))).toString());
			logMessage = StringUtils.replace(
					logMessage,
					"%2",
					(new StringBuffer(String.valueOf(deployModel
							.getCodeValCnt()))).toString());
			logMessage = StringUtils.replace(
					logMessage,
					"%3",
					(new StringBuffer(String.valueOf(deployModel
							.getComboCodeValCnt()))).toString());
		} else {
			logMessage = StringUtils.replace(
					logMessage,
					"%1",
					(new StringBuffer(String.valueOf(deployModel
							.getRelCodeCnt()))).toString());
		}
		deployModel.setDeployRtnMsg(logMessage);
		try {
			deploymgtlogDAO.begin();
			ArrayHelper ah = new ArrayHelper();
			Object deployObjId = deployModel.getDeployObjId();
			Object reqDt = deployModel.getReqDt();
			if (ah.isArray(deployObjId)) {
				Object deployObjIdArray[] = ah.fetchArrayOfArray(deployObjId);
				Object reqDtArray[] = ah.fetchArrayOfArray(reqDt);
				HashMap param = new HashMap();
				param.put("DEPLOY_DT", deployModel.getDeployDt());
				param.put("DEPLOY_TP_CD", deployModel.getDeployTpCd());
				param.put("DEPLOY_TRG_CD", deployModel.getDeployTrgCd());
				param.put("DEPLOY_ST_DT", deployModel.getDeployStDt());
				param.put("DEPLOY_END_DT", deployModel.getDeployEndDt());
				param.put("DEPLOY_RTN_MSG", logMessage);
				System.out
						.println("@@@@@@@@@@@@@@@@@@@@@@@@@ CodeDeployLogMgr.writeLog() start.. @@@@@@@@@@ "
								+ deployModel.getDeployDt());
				for (int i = 0; i < deployObjIdArray.length; i++) {
					System.out
							.println("@@@@@@@@@@@@@@@@@@@@@@@@@ CodeDeployLogMgr.writeLog() DEPLOY_OBJ_ID.. @@@@@@@@@@ "
									+ (String) deployObjIdArray[i]);
					param.put("REQ_DT", (String) reqDtArray[i]);
					param.put("DEPLOY_OBJ_ID", (String) deployObjIdArray[i]);
					param.put("DEPLOY_TP_IP", deployModel.getDeployTpIp());
					param.put("DEPLOY_SUCC_YN", deployModel.getDeploySuccYn());
					deploymgtDAO.insert(param);
					deploymgtlogDAO.insert(param);
				}

			}
		} catch (Exception e) {
			deploymgtlogDAO.rollback();
			throw e;
		} finally {
			deploymgtlogDAO.commit();
			deploymgtlogDAO.close();
		}
		return;
	}
}


/*
	DECOMPILATION REPORT

	Decompiled from: C:\MMDev\workspaces\HYCustomizingWorkspace\jars\MetaMiner30.jar
	Total time: 179 ms
	Jad reported messages/errors:
Couldn't resolve all exception handlers in method writeLog
	Exit status: 0
	Caught exceptions:
*/