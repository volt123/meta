package com.lgdisplay.code;

import java.util.HashMap;

import jspeed.base.dao.BaseModel;

import com.itplus.mm.common.util.DateUtil;
import com.itplus.mm.common.util.MessageHandler;
import com.itplus.mm.common.util.SysHandler;

public class DeployModel extends BaseModel {

	public DeployModel() {
		codeCnt = 0;
		codeValCnt = 0;
		comboCodeValCnt = 0;
		relCodeCnt = 0;
	}

	public void initialize(HashMap in) throws Exception {
		deployDt = DateUtil.getToday();
		deployStDt = DateUtil.getToday();
		deployObjId = in.get("DEPLOY_OBJ_ID");
		reqDt = in.get("REQ_DT");
		deployTpCd = (String) in.get("DEPLOY_TP_CD");
		deployTrgCd = (String) in.get("DEPLOY_TRG_CD");
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@ deployTpCd ==> "
				+ deployTpCd);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@ deployTrgCd ==> "
				+ deployTrgCd);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@ SESS_LANG_TYPE ==> "
				+ (String) in.get("SESS_LANG_TYPE"));
		deployRtnMsg = MessageHandler.getInstance().getMessage(deployTpCd,
				(String) in.get("SESS_LANG_TYPE"));
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@ deployRtnMsg ==> "
				+ deployRtnMsg);
		deploySuccYn = "Y";
		delimeter = "!";
		if ("MM_CODE_DOM_REQ".equals(deployTpCd)) {
			deployPath = SysHandler.getInstance().getProperty(
					"CODE_DEPLOY_FILEPATH");
			deployFileName = SysHandler.getInstance().getProperty(
					"CODE_DEPLOY_FILENAME");
		} else {
			deployPath = SysHandler.getInstance().getProperty(
					"CODEREL_DEPLOY_FILEPATH");
			deployFileName = SysHandler.getInstance().getProperty(
					"CODEREL_DEPLOY_FILENAME");
		}
	}

	public int getCodeCnt() {
		return codeCnt;
	}

	public void setCodeCnt(int codeCnt) {
		this.codeCnt = codeCnt;
	}

	public int getCodeValCnt() {
		return codeValCnt;
	}

	public void setCodeValCnt(int codeValCnt) {
		this.codeValCnt = codeValCnt;
	}

	public int getComboCodeValCnt() {
		return comboCodeValCnt;
	}

	public void setComboCodeValCnt(int comboCodeValCnt) {
		this.comboCodeValCnt = comboCodeValCnt;
	}

	public String getDelimeter() {
		return delimeter;
	}

	public void setDelimeter(String delimeter) {
		this.delimeter = delimeter;
	}

	public String getDeployContents() {
		return deployContents;
	}

	public void setDeployContents(String deployContents) {
		this.deployContents = deployContents;
	}

	public String getDeployDt() {
		return deployDt;
	}

	public void setDeployDt(String deployDt) {
		this.deployDt = deployDt;
	}

	public String getDeployEndDt() {
		return deployEndDt;
	}

	public void setDeployEndDt(String deployEndDt) {
		this.deployEndDt = deployEndDt;
	}

	public String getDeployFileName() {
		return deployFileName;
	}

	public void setDeployFileName(String deployFileName) {
		this.deployFileName = deployFileName;
	}

	public Object getDeployObjId() {
		return deployObjId;
	}

	public void setDeployObjId(Object deployObjId) {
		this.deployObjId = deployObjId;
	}

	public String getDeployPath() {
		return deployPath;
	}

	public void setDeployPath(String deployPath) {
		this.deployPath = deployPath;
	}

	public String getDeployRtnMsg() {
		return deployRtnMsg;
	}

	public void setDeployRtnMsg(String deployRtnMsg) {
		this.deployRtnMsg = deployRtnMsg;
	}

	public String getDeployStDt() {
		return deployStDt;
	}

	public void setDeployStDt(String deployStDt) {
		this.deployStDt = deployStDt;
	}

	public String getDeploySuccYn() {
		return deploySuccYn;
	}

	public void setDeploySuccYn(String deploySuccYn) {
		this.deploySuccYn = deploySuccYn;
	}

	public String getDeployTpCd() {
		return deployTpCd;
	}

	public void setDeployTpCd(String deployTpCd) {
		this.deployTpCd = deployTpCd;
	}

	public String getDeployTpIp() {
		return deployTpIp;
	}

	public void setDeployTpIp(String deployTpIp) {
		this.deployTpIp = deployTpIp;
	}

	public String getDeployTrgCd() {
		return deployTrgCd;
	}

	public void setDeployTrgCd(String deployTrgCd) {
		this.deployTrgCd = deployTrgCd;
	}

	public int getRelCodeCnt() {
		return relCodeCnt;
	}

	public void setRelCodeCnt(int relCodeCnt) {
		this.relCodeCnt = relCodeCnt;
	}

	public Object getReqDt() {
		return reqDt;
	}

	public void setReqDt(Object reqDt) {
		this.reqDt = reqDt;
	}

	private String deployDt;
	private String deployTpCd;
	private String deployTrgCd;
	private Object reqDt;
	private Object deployObjId;
	private String deploySuccYn;
	private String deployTpIp;
	private String deployStDt;
	private String deployEndDt;
	private String deployRtnMsg;
	private int codeCnt;
	private int codeValCnt;
	private int comboCodeValCnt;
	private int relCodeCnt;
	private String deployContents;
	private String delimeter;
	private String deployPath;
	private String deployFileName;
}


/*
	DECOMPILATION REPORT

	Decompiled from: C:\MMDev\workspaces\HYCustomizingWorkspace\jars\MetaMiner30.jar
	Total time: 25 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/