package com.lgdisplay.wf;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;

import jspeed.base.http.HttpRequestWrapper;
import jspeed.base.jdbc.QueryHelper;
import jspeed.base.query.DBAssistant;
import jspeed.base.util.DateHelper;
import jspeed.base.util.StringHelper;
import jspeed.websvc.WSParam;

import com.itplus.wf.def.act.WfactModel;
import com.itplus.wf.work.WorkParam;
import com.lgdisplay.db.LgdDao;

/**
 * ���հ��翬�� - ������οϷ�
 * @author ashyaris
 * @since 2011.09.28
 */
public class NextParticipantsApprovalConfirm extends WFApprovalInterface {

	public String execute(DBAssistant dba, WSParam params, WfactModel model) throws Exception {
		WorkParam wParam = new WorkParam(params);
		
		try
		{
			QueryHelper qHelper = dba.getQueryHelper();
			HttpRequestWrapper req = new HttpRequestWrapper(params.getRequest());
			
			String workId = StringHelper.evl(wParam.getWorkId(), "");
			String instId = StringHelper.evl(wParam.getInstId(), "");
			String userId = StringHelper.evl(wParam.getUserId(), "");
			String userNm = StringHelper.evl(wParam.getUserName(), "");
			String procId = StringHelper.evl(wParam.getProcId(), "");
			String sessLoginId = StringHelper.evl(wParam.getLoginId(), "");
			String reqDt = DateHelper.format(StringHelper.evl(params.getParameter("REQ_DT"), "").replaceAll("-", "").replaceAll(":", "").replaceAll(" ", ""), "yyyyMMddHHmmss", "yyyyMMdd");
			String acptBizCd = getAcptBizId(procId);
			String appFlag = StringHelper.evl(params.getParameter("APP_FLAG"), "");
			
			
			//HashMap actInfo = getPreviousActInfoForApproval(instId, workId);
			//String actId = StringHelper.evl(actInfo.get("ACT_ID"), "");
			//String actName = StringHelper.evl(actInfo.get("ACT_NAME"), "");
			String actId = StringHelper.evl(model.getActid(), "");
			String actName = StringHelper.evl(model.getActname(), "");
			
			// Validation
			if (StringHelper.isNull(instId)) throw new Exception("INST_ID is null");
			if (StringHelper.isNull(workId)) throw new Exception("WORK_ID is null");
			if (StringHelper.isNull(procId)) throw new Exception("PROC_ID is null");
			if (StringHelper.isNull(userId)) throw new Exception("USER_ID is null");
			if (StringHelper.isNull(userNm)) throw new Exception("USER_NM is null");
			if (StringHelper.isNull(sessLoginId)) throw new Exception("LOGIN_ID is null");
			if (StringHelper.isNull(actId)) throw new Exception("ACT_ID is null");
			if (StringHelper.isNull(actName)) throw new Exception("ACT_NAME is null");
			if (StringHelper.isNull(reqDt)) throw new Exception("REQ_DT is null");
			if (StringHelper.isNull(acptBizCd)) throw new Exception("ACPT_BIZ_CD is null");
			if (StringHelper.isNull(appFlag)) throw new Exception("APP_FLAG is null");
			
			String epStepCd = "";
			if ("WFST_A".equals(appFlag)) epStepCd = "02";
			else if ("WFST_D".equals(appFlag)) epStepCd = "03";
			else epStepCd = "02";

			if ("02".equals(epStepCd)) {
				
				/*************************************
				 * ����Ȯ��
				 *************************************/
				// Query Parameter
				HashMap approvalInHash = new HashMap();
				approvalInHash.put("DATA_INTERFACE_TYPE_CODE", "U");					// I/U/D(�Է�/����/����)
				approvalInHash.put("ACPT_SYS_CD", ACPT_SYS_CD);							// ����ý����ڵ�
				approvalInHash.put("ACPT_BIZ_CD", acptBizCd);							// ��������ڵ�
				approvalInHash.put("ACPT_BIZ_PROC_NO", instId);							// Legacy �������������ȣ
				approvalInHash.put("ACPT_STEP_NO", getMaxStepNo(dba, acptBizCd, instId));	// ����ܰ��ȣ
				approvalInHash.put("TITLE", params.getParameter("REQ_TTL"));			// ����
				approvalInHash.put("REQ_DT", reqDt);									// ���� �����û��
				approvalInHash.put("LGCY_STEP_NM", actName);							// Legacy ����ܰ��(��:�ְ��μ� P/L����)
				approvalInHash.put("STEP_CD", epStepCd);								// ���հ���ܰ� �ڵ� (02 : �������, 03 : �ݷ�)
				approvalInHash.put("CHRGR_NM", userNm);									// ó������ڸ�(������/����ó����/������)
				approvalInHash.put("CHRGR_EMPNO", sessLoginId);							// ó������� ���
				approvalInHash.put("ENTRUST_NM", " ");									// ���������ڸ�
				approvalInHash.put("ENTRUST_EMPNO", " ");								// ���������� ���
				approvalInHash.put("LINE_CHG_YN", "N");									// ���缱���� ����
				approvalInHash.put("REQ_DTL_URL", "http://"+req.getServerName()+":"+req.getServerPort()+"/metaminer/approval.jsp");	// ��û�� ��ũ
				approvalInHash.put("CHRGR_DTL_URL", "http://"+req.getServerName()+":"+req.getServerPort()+"/metaminer/approval.jsp");	// ó������� ��ũ
				approvalInHash.put("CHRGR_DTL_PARM", "loginId="+sessLoginId);											// ó������� �Ķ����
				
				// ���� ��û��/��û��/�����ȭ�� �Ķ���� ����
				approvalInHash.putAll(getDataForApproval(instId, workId, userId));
				
				System.out.println("### "+this.getClass().getName()+".execute() approvalInHash="+approvalInHash);
				
				// Insert
				insert(qHelper, approvalInHash);
			
			} else if ("03".equals(epStepCd)) {
			  
				/*************************************
				 * �ݷ�Ȯ�� - �ݷ��Ǵ� ���, ������ ������ �ǵ� ����ó���ؾ���
				 *************************************/
				LgdDao lgdDao = new LgdDao();
				
				List participants = lgdDao.getParticipants(workId);
				if (participants != null && participants.size() > 0) {
					for (int i=0; i<participants.size(); i++) {
						String participantId = StringHelper.evl(participants.get(i), "");
						String participantNm = getUserNmByUserId(dba, participantId);
						String participantLoginId = getLoginIdByUserId(dba, participantId);
						
						// Validation
						if (StringHelper.isNull(participantNm)) throw new Exception("Participant Name is null");
						if (StringHelper.isNull(participantLoginId)) throw new Exception("Participant Login ID is null");

						// Query Parameter
						HashMap approvalInHash = new HashMap();
						approvalInHash.put("DATA_INTERFACE_TYPE_CODE", "U");					// I/U/D(�Է�/����/����)
						approvalInHash.put("ACPT_SYS_CD", ACPT_SYS_CD);							// ����ý����ڵ�
						approvalInHash.put("ACPT_BIZ_CD", acptBizCd);							// ��������ڵ�
						approvalInHash.put("ACPT_BIZ_PROC_NO", instId);							// Legacy �������������ȣ
						approvalInHash.put("ACPT_STEP_NO", getMaxStepNo(dba, acptBizCd, instId));	// ����ܰ��ȣ
						approvalInHash.put("TITLE", params.getParameter("REQ_TTL"));			// ����
						approvalInHash.put("REQ_DT", reqDt);									// ���� �����û��
						approvalInHash.put("LGCY_STEP_NM", actName);							// Legacy ����ܰ��(��:�ְ��μ� P/L����)
						approvalInHash.put("STEP_CD", "03");									// ���հ���ܰ� �ڵ� (02 : �������)
						approvalInHash.put("CHRGR_NM", participantNm);							// ó������ڸ�(������/����ó����/������)
						approvalInHash.put("CHRGR_EMPNO", participantLoginId);					// ó������� ���
						approvalInHash.put("ENTRUST_NM", " ");									// ���������ڸ�
						approvalInHash.put("ENTRUST_EMPNO", " ");								// ���������� ���
						approvalInHash.put("LINE_CHG_YN", "N");									// ���缱���� ����
						approvalInHash.put("REQ_DTL_URL", "http://"+req.getServerName()+":"+req.getServerPort()+"/metaminer/approval.jsp");	// ��û�� ��ũ
						approvalInHash.put("CHRGR_DTL_URL", "http://"+req.getServerName()+":"+req.getServerPort()+"/metaminer/approval.jsp");	// ó������� ��ũ
						approvalInHash.put("CHRGR_DTL_PARM", "loginId="+participantLoginId);	// ó������� �Ķ����
					
						// ���� ��û��/��û��/�����ȭ�� �Ķ���� ����
						approvalInHash.putAll(getDataForApproval(instId, workId, userId));
						
						System.out.println("### "+this.getClass().getName()+".execute() approvalInHash="+approvalInHash);
						
						// Insert
						insert(qHelper, approvalInHash);
					}
				}
		    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("[APPROVAL INTERFACE ERROR] "+e.getStackTrace()[0].getClassName()+"."+e.getStackTrace()[0].getMethodName()+"()" + e.getMessage());
			throw e;
		}
		
		return "success";
	}
	
}
