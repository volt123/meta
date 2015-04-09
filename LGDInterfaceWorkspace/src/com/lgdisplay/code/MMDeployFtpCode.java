package com.lgdisplay.code;


import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.HashMap;

import jspeed.base.jdbc.CacheResultSet;

import org.apache.commons.net.ftp.FTPClient;

import com.itplus.mm.common.util.ArrayHelper;
import com.itplus.mm.dao.ufw.TdeploymgtDAO;

public class MMDeployFtpCode implements IMMDeploy {

	public MMDeployFtpCode() {
	}

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
				System.out.println("MMDeployFtpCode.deployObjIdArray=====>"
						+ deployObjIdArray);
				System.out.println("MMDeployFtpCode.reqDtArray=====>"
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
			String hostname = "150.253.7.134";
			String username = "ftp";
			String password = "ftp1qazx";
			String directory = "D:/download/oracle10g";
			String filename = deployPath + "/" + deployFileName;
			try {
				FTPClient fc = new FTPClient();
				fc.setControlEncoding("euc-kr");
				fc.connect(hostname);
				System.out.println(fc.getReplyString());
				fc.login(username, password);
				System.out.println(fc.getReplyString());
				System.out.println(directory);
				try {
					InputStream fis = new FileInputStream(filename);
					fc.setFileType(2);
					System.out.println(fc.getReplyString());
					if (fc.storeFile(filename, fis))
						System.out.println("Success to putting ... (size : "
								+ filename.length() + ")");
					System.out.println(fc.getReplyString());
					fis.close();
					fc.rename("imp_dump.log", "imp_dump.log.move");
					System.out.println(fc.getReplyString());
				} catch (Exception e) {
					System.err.println("Fail to putting " + e);
					throw e;
				}
				fc.quit();
				System.out.println(fc.getReplyString());
			} catch (Exception e) {
				System.err.println(e);
				throw e;
			}
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
	Total time: 27 ms
	Jad reported messages/errors:
Couldn't resolve all exception handlers in method deploy
	Exit status: 0
	Caught exceptions:
*/