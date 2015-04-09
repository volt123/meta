package com.lgdisplay.code;

import java.util.HashMap;

import com.itplus.mm.common.util.DateUtil;
import com.itplus.mm.common.util.SysHandler;

// Referenced classes of package com.itplus.mm.server.deploy:
//            DeployModel, CodeDeployLogMgr, IMMDeploy

public class CodeDeployMgr {

	public CodeDeployMgr() throws Exception {
		deployModel = new DeployModel();
		deployLogMgr = new CodeDeployLogMgr();
	}

	public void codeDeploy(HashMap in) throws Exception {
		deployModel.initialize(in);
		String codeDeployLogMethod = SysHandler.getInstance().getProperty(
				"CODE_DEPLOY_LOG_METHOD");
		String className = null;
		className = SysHandler.getInstance().getProperty(
				deployModel.getDeployTrgCd() + "."
						+ deployModel.getDeployTpCd() + ".CLASS");
		if (className == null)
			className = "com.itplus.mm.server.deploy.impl.MMDeployCurrServCode";
		System.out
				.println("@@@@@@@@@@@@@@@@@@@@@@@@@ CodeDeployMgr.java codeDeploy() className ==> "
						+ className);
		iMMDeploy = (IMMDeploy) Class.forName(className).newInstance();
		try {
			System.out
					.println("@@@@@@@@@@@@@@@@@@@@@@@@@ CodeDeployMgr.java codeDeploy() start.. @@@@@@@@@@ ");
			iMMDeploy.deploy(deployModel);
			System.out
					.println("@@@@@@@@@@@@@@@@@@@@@@@@@ CodeDeployMgr.java codeDeploy() end.. @@@@@@@@@@ ");
		} catch (Exception e) {
			if (!"site".equals(codeDeployLogMethod)) {
				deployModel.setDeploySuccYn("N");
				deployModel.setDeployRtnMsg(e.getMessage());
			}
			throw e;
		} finally {
			if (!"site".equals(codeDeployLogMethod)) {
				deployModel.setDeployEndDt(DateUtil.getToday());
				deployLogMgr.writeLog(deployModel);
			}
		}
		return;
	}

	IMMDeploy iMMDeploy;
	DeployModel deployModel;
	CodeDeployLogMgr deployLogMgr;
}


/*
	DECOMPILATION REPORT

	Decompiled from: C:\MMDev\workspaces\HYCustomizingWorkspace\jars\MetaMiner30.jar
	Total time: 24 ms
	Jad reported messages/errors:
Couldn't resolve all exception handlers in method codeDeploy
	Exit status: 0
	Caught exceptions:
*/