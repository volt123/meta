package com.lgdisplay.wf;

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

/**
 * ���հ��翬�� - ���ο�û
 * @author ashyaris
 * @since 2011.09.27
 */
public class NextParticipantsApprovalRequest extends WFApprovalInterface {

	boolean debugMode = true;
	
	public String execute(DBAssistant dba, WSParam params, WfactModel model) throws Exception {
		WorkParam wParam = new WorkParam(params);
		WSParam wsParam = new WSParam(params.getRequest(), params.getResponse());
		
		try
		{
			QueryHelper qHelper = dba.getQueryHelper();
			HttpRequestWrapper req = new HttpRequestWrapper(params.getRequest());
			WfactModel nextActModel = (WfactModel)wsParam.getAttribute("__NEXT_ACT_MODEL__");
			
			String instId = StringHelper.evl(wParam.getInstId(), "");
			String procId = StringHelper.evl(wParam.getProcId(), "");
			String reqUserNm = getUserNmByUserId(dba, params.getParameter("REQ_USER_ID"));
			String reqUserLoginId = getLoginIdByUserId(dba, params.getParameter("REQ_USER_ID"));
			String sessLoginId = StringHelper.evl(wParam.getLoginId(), "");
			String nextActId = StringHelper.evl(nextActModel.getActid(), "");
			String nextActName = nextActModel.getActname();
			String reqDt = DateHelper.format(StringHelper.evl(params.getParameter("REQ_DT"), "").replaceAll("-", "").replaceAll(":", "").replaceAll(" ", ""), "yyyyMMddHHmmss", "yyyyMMdd");
			List nextParticipants = wParam.getNextParticipants();
			String acptBizCd = getAcptBizId(procId);
			String appFlag = StringHelper.evl(wParam.getAppReturnValue(), "");
			
			if (debugMode) {
				System.out.println ("############# Debuging - Start ###############");
				System.out.println ("reqUserNm : " + reqUserNm);
				System.out.println ("reqUserLoginId : " + reqUserLoginId);
				System.out.println ("instId : " + instId);
				System.out.println ("procId : " + procId);
				System.out.println ("appFlag : " + appFlag);
				System.out.println ("sessLoginId : " + sessLoginId);
				System.out.println ("reqDt : " + reqDt);
				System.out.println ("nextActId : " + nextActId);
				System.out.println ("nextActName : " + nextActName);
				System.out.println ("acptBizCd : " + acptBizCd);
				System.out.println ("################ Debuging - End ###############");
			}

			// Validation
			if (StringHelper.isNull(instId)) throw new Exception("INST_ID is null");
			if (StringHelper.isNull(sessLoginId)) throw new Exception("LOGIN_ID is null");
			if (StringHelper.isNull(nextActId)) throw new Exception("Next ACT_ID is null");
			if (StringHelper.isNull(nextActName)) throw new Exception("Next ACT_NAME is null");
			if (StringHelper.isNull(reqDt)) throw new Exception("REQ_DT is null");
			if (StringHelper.isNull(procId)) throw new Exception("PROC_ID is null");
			if (StringHelper.isNull(acptBizCd)) throw new Exception("ACPT_BIZ_CD is null");
			
			if (nextParticipants != null && nextParticipants.size() > 0) {
				for (int i=0; i<nextParticipants.size(); i++) {
					String nextParticipant = StringHelper.evl(nextParticipants.get(i), "");
					String nextParticipantNm = getUserNmByUserId(dba,nextParticipant);
					String nextParticipantLoginId = getLoginIdByUserId(dba, nextParticipant);
					
					// Validation
					if (StringHelper.isNull(nextParticipantNm)) throw new Exception("Next Participant Name is null");
					if (StringHelper.isNull(nextParticipantLoginId)) throw new Exception("Next Participant Login ID is null");
					
					// Query Parameter
					HashMap inHash = new HashMap();
					inHash.put("DATA_INTERFACE_TYPE_CODE", "I");						// I/U/D(�Է�/����/����)
					inHash.put("ACPT_SYS_CD", ACPT_SYS_CD);								// ����ý����ڵ�
					inHash.put("ACPT_BIZ_CD", acptBizCd);								// ��������ڵ�
					inHash.put("ACPT_BIZ_PROC_NO", instId);								// Legacy �������������ȣ
//					inHash.put("ACPT_STEP_NO", StringHelper.lpad(nextActId, 4, '0'));	// ����ܰ��ȣ -- ��û�Ҷ��� ������ '0001' �ν�
					inHash.put("ACPT_STEP_NO", getMaxStepNo(dba, acptBizCd, instId));	// ����ܰ��ȣ -- ��û�Ҷ��� ������ '0001' �ν�
					inHash.put("TITLE", params.getParameter("REQ_TTL"));				// ����
					inHash.put("REQ_NM", reqUserNm);									// ���� ��û�ڸ�
					inHash.put("REQ_EMPNO", reqUserLoginId);							// ���� ��û�� ���
					inHash.put("REQ_DT", reqDt);										// ���� �����û��
					inHash.put("LGCY_STEP_NM", nextActName);							// Legacy ����ܰ��(��:�ְ��μ� P/L����)
					inHash.put("STEP_CD", "01");										// ���հ���ܰ� �ڵ� (01 : �����û)
					inHash.put("CHRGR_NM", nextParticipantNm);							// ó������ڸ�(������/����ó����/������)
					inHash.put("CHRGR_EMPNO", nextParticipantLoginId);					// ó������� ���
					inHash.put("ENTRUST_NM", " ");										// ���������ڸ�
					inHash.put("ENTRUST_EMPNO", " ");									// ���������� ���
					inHash.put("LINE_CHG_YN", "N");										// ���缱���� ����
//					inHash.put("REQ_DTL_URL", "http://156.147.19.45:3020/metaminer/approval.jsp");	// ��û�� ��ũ
					inHash.put("REQ_DTL_URL", "http://"+req.getServerName()+":"+req.getServerPort()+"/metaminer/approval.jsp");	// ��û�� ��ũ
					inHash.put("REQ_DTL_PARM", "loginId="+sessLoginId);														// ��û�� �Ķ����
//					inHash.put("CHRGR_DTL_URL", "http://156.147.19.45:3020/metaminer/approval.jsp");	// ó������� ��ũ
					inHash.put("CHRGR_DTL_URL", "http://"+req.getServerName()+":"+req.getServerPort()+"/metaminer/approval.jsp");	// ó������� ��ũ
					inHash.put("CHRGR_DTL_PARM", "loginId="+nextParticipantLoginId);											// ó������� �Ķ����
					
					System.out.println("### "+this.getClass().getName()+".execute() inHash="+inHash);
					
					// Insert
					insert(qHelper, inHash);
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
