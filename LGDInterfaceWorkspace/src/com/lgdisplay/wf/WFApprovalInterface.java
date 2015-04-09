package com.lgdisplay.wf;


import java.sql.ResultSet;
import java.util.HashMap;

import jspeed.base.jdbc.QueryHelper;
import jspeed.base.query.DBAssistant;
import jspeed.base.util.StringHelper;
import jspeed.websvc.WSParam;

import com.itplus.wf.def.act.WfactModel;

public abstract class WFApprovalInterface implements com.itplus.wf.inst.app.IApplication {
	
	public final static String ACPT_SYS_CD = "43";		// ����ý����ڵ�
	
	abstract public String execute(DBAssistant dba, WSParam params, WfactModel model) throws Exception;
	
	
	/**
	 * PROC ID�� ��������ڵ� Return
	 * 
	 * @param procId
	 * @return String ��������ڵ�
	 * @throws Exception
	 */
	public String getAcptBizId(String procId) throws Exception {
		
		String AcptBizId = "";
		
		String ACPT_BIZ_11 = "10001";		// ��������ڵ�(ǥ�ص�����(�ý���) �������μ���)
		String ACPT_BIZ_12 = "10002";		// ��������ڵ�(�� �������μ���)
		String ACPT_BIZ_23 = "20003";		// ��������ڵ�(��������(Biz) �������μ���)

//		String ACPT_BIZ_21 = "20001";		// ��������ڵ�(������(Biz) �������μ���)
//		String ACPT_BIZ_22 = "20002";		// ��������ڵ�(KPI(Biz) �������μ���)
//		String ACPT_BIZ_24 = "20004";		// ��������ڵ�(��ǥ����(Biz) �������μ���)
//		String ACPT_BIZ_25 = "20005";		// ��������ڵ�(��������(Biz) �������μ���)

		
		String STD_TERM_DATA = "19";             //����Ͽ�û
		String STD_ITEM_DATA = "37";             //�׸��Ͽ�û
		String STD_DOMAIN_DATA = "38";           //�����ε�Ͽ�û
		String REG_MODEL = "28";                 //�𵨵�Ͽ�û
		String BIZ_REPORT = "36";                //��������(Biz)��Ͽ�û
//		String FLW_DATA = "31";                  //�������帧���ǿ�û		
		
		
		try {
			
			if (procId.equals(STD_TERM_DATA))        AcptBizId = ACPT_BIZ_11;  //ǥ�ص����� - �����
			else if	(procId.equals(STD_ITEM_DATA))   AcptBizId = ACPT_BIZ_11;  //ǥ�ص����� - �׸���
			else if	(procId.equals(STD_DOMAIN_DATA)) AcptBizId = ACPT_BIZ_11;  //ǥ�ص����� - �����ε��
			else if	(procId.equals(REG_MODEL))       AcptBizId = ACPT_BIZ_12;  //�𵨵��
			else if	(procId.equals(BIZ_REPORT))      AcptBizId = ACPT_BIZ_23;  //��������(Biz)���

			return AcptBizId;
			
		} catch (Exception e) {
			throw e;			
		}
		
	}
	/**
	 * USER_ID�� LOGIN_ID(���)�� ��ȸ�Ѵ�
	 * @param dba
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public String getLoginIdByUserId(DBAssistant dba, String userId) throws Exception {
		QueryHelper qHelper = null;
		ResultSet rs = null;
		
		try {
			qHelper = dba.getQueryHelper();
			
			String sql = "SELECT LOGIN_ID FROM C_USER WHERE USER_ID = ?";
			
			rs = qHelper.executeQuery(sql, new Object[] { StringHelper.evl(userId, "") });
			if (rs.next()) {
				return rs.getString("LOGIN_ID");
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("[APPROVAL INTERFACE ERROR] "+e.getStackTrace()[0].getClassName()+"."+e.getStackTrace()[0].getMethodName()+"()" + e.getMessage());
			throw e;
		} finally {
			if (rs != null) try { rs.close(); } catch (Exception ignore) {}
		}
	}
	
	/**
	 * STEP_NO�� ����
	 * @param dba
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public String getMaxStepNo(DBAssistant dba, String acptBizCd, String instId) throws Exception {
		QueryHelper qHelper = null;
		ResultSet rs = null;
		
		try {
			qHelper = dba.getQueryHelper();
			
			String sql = "select (nvl(max(acpt_step_no), '0000') + 1) stepNo from eai_ep_info where acpt_biz_cd = ? and acpt_biz_proc_no = ?";
			
			rs = qHelper.executeQuery(sql, new Object[] { StringHelper.evl(acptBizCd, "") ,  StringHelper.evl(instId, "") });
			if (rs.next()) {
				return StringHelper.lpad(rs.getString("stepNo"), 4, '0');
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("[APPROVAL INTERFACE ERROR] "+e.getStackTrace()[0].getClassName()+"."+e.getStackTrace()[0].getMethodName()+"()" + e.getMessage());
			throw e;
		} finally {
			if (rs != null) try { rs.close(); } catch (Exception ignore) {}
		}
	}

	/**
	 * USER_ID�� ����� �̸��� ��ȸ�Ѵ�
	 * @param dba
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public String getUserNmByUserId(DBAssistant dba, String userId) throws Exception {
		QueryHelper qHelper = null;
		ResultSet rs = null;
		
		try {
			qHelper = dba.getQueryHelper();
			
			String sql = "SELECT USER_NM FROM C_USER WHERE USER_ID = ?";
			
			rs = qHelper.executeQuery(sql, new Object[] { StringHelper.evl(userId, "") });
			if (rs.next()) {
				return rs.getString("USER_NM");
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("[APPROVAL INTERFACE ERROR] "+e.getStackTrace()[0].getClassName()+"."+e.getStackTrace()[0].getMethodName()+"()" + e.getMessage());
			throw e;
		} finally {
			if (rs != null) try { rs.close(); } catch (Exception ignore) {}
		}
	}
	
	public int insert(QueryHelper qHelper, HashMap inHash) throws Exception {
		int returnValue = 0;
		StringBuffer sbSql = null;
		
		try {
			/* Prepared Parameter
			(EAI_SEQ_ID)
			DATA_INTERFACE_TYPE_CODE
			(DATA_INTERFACE_DATE)
			(EAI_TRANSFER_FLAG)
			(EAI_TRANSFER_DATE)
			ACPT_SYS_CD
			ACPT_BIZ_CD
			ACPT_BIZ_PROC_NO
			ACPT_STEP_NO
			TITLE
			REQ_NM
			REQ_EMPNO
			REQ_DT
			LGCY_STEP_NM
			STEP_CD
			CHRGR_NM
			CHRGR_EMPNO
			ENTRUST_NM
			ENTRUST_EMPNO
			LINE_CHG_YN
			REQ_DTL_URL
			REQ_DTL_PARM
			CHRGR_DTL_URL
			CHRGR_DTL_PARM
			(REG_TM)
			(WORK_YN)
			*/
			sbSql = new StringBuffer();
			sbSql.append("INSERT INTO EAI_EP_INFO ( \n");
			sbSql.append("         EAI_SEQ_ID, DATA_INTERFACE_TYPE_CODE, DATA_INTERFACE_DATE, EAI_TRANSFER_FLAG, EAI_TRANSFER_DATE, \n");
			sbSql.append("         ACPT_SYS_CD, ACPT_BIZ_CD, ACPT_BIZ_PROC_NO, ACPT_STEP_NO, TITLE, \n");
			sbSql.append("         REQ_NM, REQ_EMPNO, REQ_DT, LGCY_STEP_NM, STEP_CD, CHRGR_NM, CHRGR_EMPNO, \n");
			sbSql.append("         ENTRUST_NM, ENTRUST_EMPNO, LINE_CHG_YN, REQ_DTL_URL, REQ_DTL_PARM, \n");
			sbSql.append("         CHRGR_DTL_URL, CHRGR_DTL_PARM, REG_TM, WORK_YN ) \n");
			sbSql.append(" VALUES ( \n");
			sbSql.append("         (SELECT NVL(MAX(EAI_SEQ_ID), 0)+1 FROM EAI_EP_INFO),?,SYSDATE,'N',SYSDATE, \n");
			sbSql.append("          ?, ?, ?, ?, ?,  \n");
			sbSql.append("          ?, ?, ?, ?, ?, ?, ?,  \n");
			sbSql.append("          ?, ?, ?, ?, ?, \n");
			sbSql.append("          ?,?,(SELECT TO_CHAR(SYSDATE, 'yyyyMMddHH24miss') FROM DUAL),'N')  \n");
			
			returnValue = qHelper.executeUpdate(sbSql.toString(), new Object[] {
							StringHelper.evl(inHash.get("DATA_INTERFACE_TYPE_CODE"), ""),
							StringHelper.evl(inHash.get("ACPT_SYS_CD"), ""),
							StringHelper.evl(inHash.get("ACPT_BIZ_CD"), ""),
							StringHelper.evl(inHash.get("ACPT_BIZ_PROC_NO"), ""),
							StringHelper.evl(inHash.get("ACPT_STEP_NO"), ""),
							StringHelper.evl(inHash.get("TITLE"), ""),
							StringHelper.evl(inHash.get("REQ_NM"), ""),
							StringHelper.evl(inHash.get("REQ_EMPNO"), ""),
							StringHelper.evl(inHash.get("REQ_DT"), ""),
							StringHelper.evl(inHash.get("LGCY_STEP_NM"), ""),
							StringHelper.evl(inHash.get("STEP_CD"), ""),
							StringHelper.evl(inHash.get("CHRGR_NM"), ""),
							StringHelper.evl(inHash.get("CHRGR_EMPNO"), ""),
							StringHelper.evl(inHash.get("ENTRUST_NM"), " "),   //���������� ������ ���� 1ĭ
							StringHelper.evl(inHash.get("ENTRUST_EMPNO"), " "),  //���������� ������ ���� 1ĭ
							StringHelper.evl(inHash.get("LINE_CHG_YN"), ""),
							StringHelper.evl(inHash.get("REQ_DTL_URL"), ""),
							StringHelper.evl(inHash.get("REQ_DTL_PARM"), ""),
							StringHelper.evl(inHash.get("CHRGR_DTL_URL"), ""),
							StringHelper.evl(inHash.get("CHRGR_DTL_PARM"), "")
						});
			
			return returnValue;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("[APPROVAL INTERFACE ERROR] "+e.getStackTrace()[0].getClassName()+"."+e.getStackTrace()[0].getMethodName()+"()" + e.getMessage());
			throw e;
		}
	}
	
	/**
	 * ���� ��û��/��û�� �����ȭ�� �Ķ���� ������ ��ȸ�Ѵ�<BR>
	 * ���հ��翬�� �� �����û-������ο� ���� row�� �׻� ������ Insert �Ǿ�� ��<BR>
	 * [��û������, ����������, ��û����, ����, ��û�� �����ȭ�� URL �� �Ķ����] �׸���
	 * �����û-��������� ��Ÿ���� KEY���̸� �����û�� ���ο� ���� row�� �� �׸��� ���ƾ� ���հ���ý��ۿ��� ������ �ν���
	 * @param instId
	 * @param workId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public HashMap getDataForApproval(String instId, String workId, String userId) throws Exception {
		QueryHelper qHelper = null;
		ResultSet rs = null;
		HashMap returnMap = new HashMap();
		
		try {
			qHelper = new QueryHelper();
			
			// ���� ��û��
			StringBuffer sbSql = new StringBuffer();
			sbSql.append("SELECT REQ_USER_ID, REQ_EMPNO, REQ_NM FROM (\n");
			sbSql.append("SELECT B.PERFORMER_ID AS REQ_USER_ID, B.PERFORMER_LOGIN_ID AS REQ_EMPNO, B.PERFORMER_USER_NAME AS REQ_NM\n");
			sbSql.append("FROM WF_WORKLIST A, WF_WORK_PERFORMER B\n");
			sbSql.append("WHERE A.WORK_ID = B.WORK_ID(+)\n");
			sbSql.append("  AND A.APP_INVK_TP_CD = 'CLASS'\n");
			sbSql.append("  AND B.WORK_STATUS_CD = 'FINISHED'\n");
			sbSql.append("  AND A.INST_ID = ?\n");
			sbSql.append("  AND B.WORK_ID < ?\n");
			sbSql.append("ORDER BY A.WORK_ID DESC, B.REG_DT DESC\n");
			sbSql.append(") WHERE ROWNUM = 1\n");
			
			rs = qHelper.executeQuery(sbSql.toString(), new Object[] { instId, workId });
			if (rs.next()) {
				returnMap.put("REQ_NM", rs.getString("REQ_NM"));
				returnMap.put("REQ_EMPNO", rs.getString("REQ_EMPNO"));
				
				// ��û�� �����ȭ�� �Ķ���� ����
				returnMap.put("REQ_DTL_PARM", "loginId="+StringHelper.evl(returnMap.get("REQ_EMPNO"), ""));
			}
			
			return returnMap;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("[APPROVAL INTERFACE ERROR] "+e.getStackTrace()[0].getClassName()+"."+e.getStackTrace()[0].getMethodName()+"()" + e.getMessage());
			throw e;
		} finally {
			if (rs != null) try { rs.close(); } catch (Exception ignore) {}
			if (qHelper != null) try { qHelper.close(); } catch (Exception ignore) {}
		}
	}
	
	/**
	 * �ٷ� ���� ������ �ܰ��� ACT_ID, ACT_NAME�� ��ȸ
	 * @param instId
	 * @param workId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public HashMap getPreviousActInfoForApproval(String instId, String workId) throws Exception {
		QueryHelper qHelper = null;
		ResultSet rs = null;
		HashMap returnMap = new HashMap();
		
		try {
			qHelper = new QueryHelper();
			
			// ACT_ID, ACT_NAME
			StringBuffer sbSql = new StringBuffer();
			sbSql.append("SELECT C.ACT_ID, C.ACT_NAME\n");
			sbSql.append("FROM WF_WORKLIST A, WF_WORK_PERFORMER B, wf_act c\n");
			sbSql.append("WHERE A.WORK_ID = B.WORK_ID(+)\n");
			sbSql.append("  and a.act_id = c.act_id\n");
			sbSql.append("  AND A.APP_INVK_TP_CD = 'CLASS'\n");
			sbSql.append("  AND B.WORK_STATUS_CD = 'FINISHED'\n");
			sbSql.append("  AND A.INST_ID = ?\n");
			sbSql.append("  AND B.WORK_ID < ?\n");
			sbSql.append("  AND ROWNUM = 1\n");
			sbSql.append("ORDER BY A.WORK_ID DESC\n");
			
			rs = qHelper.executeQuery(sbSql.toString(), new Object[] { instId, workId });
			if (rs.next()) {
				returnMap.put("ACT_ID", rs.getString("ACT_ID"));
				returnMap.put("ACT_NAME", rs.getString("ACT_NAME"));
			}
			
			return returnMap;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("[APPROVAL INTERFACE ERROR] "+e.getStackTrace()[0].getClassName()+"."+e.getStackTrace()[0].getMethodName()+"()" + e.getMessage());
			throw e;
		} finally {
			if (rs != null) try { rs.close(); } catch (Exception ignore) {}
			if (qHelper != null) try { qHelper.close(); } catch (Exception ignore) {}
		}
	}
	
}
