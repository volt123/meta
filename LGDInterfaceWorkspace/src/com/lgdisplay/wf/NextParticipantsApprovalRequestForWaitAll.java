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
 * 통합결재연동 - 승인요청
 * @author ashyaris
 * @since 2011.09.27
 */
public class NextParticipantsApprovalRequestForWaitAll extends WFApprovalInterface {

	boolean debugMode = true;
	
	public String execute(DBAssistant dba, WSParam params, WfactModel model) throws Exception {
		WorkParam wParam = new WorkParam(params);
		WSParam wsParam = new WSParam(params.getRequest(), params.getResponse());
		
		try
		{
			QueryHelper qHelper = dba.getQueryHelper();
			HttpRequestWrapper req = new HttpRequestWrapper(params.getRequest());
			WfactModel nextActModel = (WfactModel)wsParam.getAttribute("__NEXT_ACT_MODEL__");

			String workId = StringHelper.evl(wParam.getWorkId(), "");
			String instId = StringHelper.evl(wParam.getInstId(), "");
			String userId = StringHelper.evl(wParam.getUserId(), "");
			String userNm = StringHelper.evl(wParam.getUserName(), "");
			String procId = StringHelper.evl(wParam.getProcId(), "");
			String sessLoginId = StringHelper.evl(wParam.getLoginId(), "");
			String nextActId = StringHelper.evl(nextActModel.getActid(), "");
			String nextActName = nextActModel.getActname();
			String reqDt = DateHelper.format(StringHelper.evl(params.getParameter("REQ_DT"), "").replaceAll("-", "").replaceAll(":", "").replaceAll(" ", ""), "yyyyMMddHHmmss", "yyyyMMdd");
			String acptBizCd = getAcptBizId(procId);
			String appFlag = StringHelper.evl(wParam.getAppReturnValue(), "");
			
			//HashMap actInfo = getPreviousActInfoForApproval(instId, workId);
			//String actId = StringHelper.evl(actInfo.get("ACT_ID"), "");
			//String actName = StringHelper.evl(actInfo.get("ACT_NAME"), "");
			String actId = StringHelper.evl(model.getActid(), "");
			String actName = StringHelper.evl(model.getActname(), "");
			
			if (debugMode) {
				System.out.println ("############# Debuging - Start ###############");
				System.out.println ("workId : " + workId);
				System.out.println ("instId : " + instId);
				System.out.println ("userId : " + userId);
				System.out.println ("userNm : " + userNm);
				System.out.println ("procId : " + procId);
				System.out.println ("appFlag : " + appFlag);
				System.out.println ("sessLoginId : " + sessLoginId);
				System.out.println ("nextActId : " + nextActId);
				System.out.println ("nextActName : " + nextActName);
				System.out.println ("reqDt : " + reqDt);
				System.out.println ("actId : " + actId);
				System.out.println ("actName : " + actName);
				System.out.println ("acptBizCd : " + acptBizCd);
				System.out.println ("################ Debuging - End ###############");
			}

			// Validation
			if (StringHelper.isNull(workId)) throw new Exception("WORK_ID is null");
			if (StringHelper.isNull(instId)) throw new Exception("INST_ID is null");
			if (StringHelper.isNull(userId)) throw new Exception("USER_ID is null");
			if (StringHelper.isNull(userNm)) throw new Exception("USER_NM is null");
			if (StringHelper.isNull(sessLoginId)) throw new Exception("LOGIN_ID is null");
			if (StringHelper.isNull(actId)) throw new Exception("ACT_ID is null");
			if (StringHelper.isNull(actName)) throw new Exception("ACT_NAME is null");
			if (StringHelper.isNull(nextActId)) throw new Exception("Next ACT_ID is null");
			if (StringHelper.isNull(nextActName)) throw new Exception("Next ACT_NAME is null");
			if (StringHelper.isNull(reqDt)) throw new Exception("REQ_DT is null");
			if (StringHelper.isNull(procId)) throw new Exception("PROC_ID is null");
			if (StringHelper.isNull(acptBizCd)) throw new Exception("ACPT_BIZ_CD is null");
			
			/*************************************
			 * 승인요청
			 *************************************/
			List nextParticipants = wParam.getNextParticipants();
			if (nextParticipants != null && nextParticipants.size() > 0) {
				for (int i=0; i<nextParticipants.size(); i++) {
					String nextParticipant = StringHelper.evl(nextParticipants.get(i), "");
					String nextParticipantNm = getUserNmByUserId(dba,nextParticipant);
					String nextParticipantLoginId = getLoginIdByUserId(dba, nextParticipant);
					
					// Validation
					if (StringHelper.isNull(nextParticipantNm)) throw new Exception("Next Participant Name is null");
					if (StringHelper.isNull(nextParticipantLoginId)) throw new Exception("Next Participant Login ID is null");
					
					// Query Parameter
					HashMap requestInHash = new HashMap();
					requestInHash.put("DATA_INTERFACE_TYPE_CODE", "I");							// I/U/D(입력/수정/삭제)
					requestInHash.put("ACPT_SYS_CD", ACPT_SYS_CD);								// 결재시스템코드
					requestInHash.put("ACPT_BIZ_CD", acptBizCd);								// 결재업무코드
					requestInHash.put("ACPT_BIZ_PROC_NO", instId);								// Legacy 결재업무관리번호
					requestInHash.put("ACPT_STEP_NO", getMaxStepNo(dba, acptBizCd, instId));	// 결재단계번호
					requestInHash.put("TITLE", params.getParameter("REQ_TTL"));					// 제목
					requestInHash.put("REQ_NM", userNm);										// 결재 요청자명
					requestInHash.put("REQ_EMPNO", sessLoginId);								// 결재 요청자 사번
					requestInHash.put("REQ_DT", reqDt);											// 최초 결재요청일
					requestInHash.put("LGCY_STEP_NM", nextActName);								// Legacy 결재단계명(예:주관부서 P/L승인)
					requestInHash.put("STEP_CD", "01");											// 통합결재단계 코드 (01 : 결재요청)
					requestInHash.put("CHRGR_NM", nextParticipantNm);							// 처리담당자명(승인자/결재처리자/참조자)
					requestInHash.put("CHRGR_EMPNO", nextParticipantLoginId);					// 처리담당자 사번
					requestInHash.put("ENTRUST_NM", " ");										// 권한위임자명
					requestInHash.put("ENTRUST_EMPNO", " ");									// 권한위임자 사번
					requestInHash.put("LINE_CHG_YN", "N");										// 결재선변경 여부
					requestInHash.put("REQ_DTL_URL", "http://"+req.getServerName()+":"+req.getServerPort()+"/metaminer/approval.jsp");		// 요청자 링크
					requestInHash.put("REQ_DTL_PARM", "loginId="+sessLoginId);															// 요청자 파라메터
					requestInHash.put("CHRGR_DTL_URL", "http://"+req.getServerName()+":"+req.getServerPort()+"/metaminer/approval.jsp");	// 처리담당자 링크
					requestInHash.put("CHRGR_DTL_PARM", "loginId="+nextParticipantLoginId);											// 처리담당자 파라메터

					System.out.println("### "+this.getClass().getName()+".execute() requestInHash="+requestInHash);
					
					// Insert
					insert(qHelper, requestInHash);
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
