package com.lgdisplay.user;

import java.sql.ResultSet;

import jspeed.base.jdbc.BaseSQLException;
import jspeed.base.jdbc.QueryHelper;
import jspeed.base.util.StringHelper;

public class UserCollector {
	
	public int collectUser() throws BaseSQLException {
		QueryHelper qHelper = null;
		int retCnt = 0;
		
		try {
			qHelper = new QueryHelper();
			qHelper.begin();
			
			System.out.println("### UserCollector : START");
			
			// 새로 INSERT 된 데이터를 '처리중' 상태로  변경
			statusToProcessing(qHelper);
			
			// 부서 정보 수집
			retCnt += collectDepInfo(qHelper);
			
			// 부서 정보 수집 (WEB)
			collectDepInfoWeb(qHelper);
			
			// 직급 정보 수집
			retCnt += collectPositionInfo(qHelper);
			
			// 사용자 정보 수집
			retCnt += collectUserInfo(qHelper);
			
			// 권한 부여
			setUserAuthority(qHelper);
			
			// '삭제부서' 부서 중 사용자가 하나도 없는 부서를 삭제
			deleteDepInfo(qHelper);
			
			// '삭제부서' 부서 중 사용자가 하나도 없는 부서를 삭제(WEB)
			deleteDepInfoWeb(qHelper); 
			
			// 새로 INSERT 된 데이터를 '완료' 상태로  변경
			statusToSuccess(qHelper);
			
			qHelper.commit();
			
			System.out.println("### UserCollector : END");
		} catch (BaseSQLException e) {
			e.printStackTrace();
			if (qHelper != null) try { qHelper.rollback(); } catch (Exception ignore) {}
			System.out.println("### UserCollector : BaseSQLException : "+e.getMessage());
			
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			if (qHelper != null) try { qHelper.rollback(); } catch (Exception ignore) {}
			System.out.println("### UserCollector : Exception : "+e.getMessage());
			
			throw new BaseSQLException(e.getMessage());
		} finally {
			if (qHelper != null) try { qHelper.close(); } catch (Exception ignore) {}
		}
		
		return retCnt;
	}
	
	/**
	 * 새로 INSERT 된 데이터를 '처리중' 상태로  변경
	 * 
	 * @param qHelper
	 * @throws Exception
	 */
	private void statusToProcessing(QueryHelper qHelper) throws Exception {
		try {
			String sql_EAI_HR_ORG_INFO = "UPDATE EAI_HR_ORG_INFO\n"			// 부서
										+"SET APPLICATION_TRANSFER_FLAG = 'P'\n"
										+"WHERE APPLICATION_TRANSFER_FLAG = 'N'";
			String sql_EAI_HR_INFO = "UPDATE EAI_HR_INFO\n"					// 사용자
									+"SET APPLICATION_TRANSFER_FLAG = 'P'\n"
									+"WHERE APPLICATION_TRANSFER_FLAG = 'N'";
			String sql_EAI_XHR_INFO = "UPDATE EAI_XHR_INFO\n"					// 사용자(협력사)
									+ "SET APPLICATION_TRANSFER_FLAG = 'P'\n"
									+ "WHERE APPLICATION_TRANSFER_FLAG = 'N'";
			
			qHelper.executeUpdate(sql_EAI_HR_ORG_INFO, new Object[] {});
			qHelper.executeUpdate(sql_EAI_HR_INFO, new Object[] {});
			qHelper.executeUpdate(sql_EAI_XHR_INFO, new Object[] {});
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * 새로 INSERT 된 데이터를 '완료' 상태로  변경
	 * 
	 * @param qHelper
	 * @throws Exception
	 */
	private void statusToSuccess(QueryHelper qHelper) throws Exception {
		try {
			String sql_EAI_HR_ORG_INFO = "UPDATE EAI_HR_ORG_INFO\n"			// 부서
										+"SET APPLICATION_TRANSFER_FLAG = 'Y', APPLICATION_TRANSFER_DATE = SYSDATE\n"
										+"WHERE APPLICATION_TRANSFER_FLAG = 'P'";
			String sql_EAI_HR_INFO = "UPDATE EAI_HR_INFO\n"					// 사용자
									+"SET APPLICATION_TRANSFER_FLAG = 'Y', APPLICATION_TRANSFER_DATE = SYSDATE\n"
									+"WHERE APPLICATION_TRANSFER_FLAG = 'P'";
			String sql_EAI_XHR_INFO = "UPDATE EAI_XHR_INFO\n"					// 사용자(협력사)
									+ "SET APPLICATION_TRANSFER_FLAG = 'Y'\n"
									+ "WHERE APPLICATION_TRANSFER_FLAG = 'P'";
			
			qHelper.executeUpdate(sql_EAI_HR_ORG_INFO, new Object[] {});
			qHelper.executeUpdate(sql_EAI_HR_INFO, new Object[] {});
			qHelper.executeUpdate(sql_EAI_XHR_INFO, new Object[] {});
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * 부서 정보 수집
	 * 
	 * @param qHelper
	 * @throws Exception
	 */
	private int collectDepInfo(QueryHelper qHelper) throws Exception {
		ResultSet rs = null;
		StringBuffer sbSql = null;
		int retCnt = 0;
		
		try {
			// 부서 정보 이행 (INSERT/UPDATE)
			// 부서 코드 전체를 아스키 코드로 변환하여 DEP_ID로 사용한다
			sbSql = new StringBuffer();
			sbSql.append("MERGE INTO C_DEP_INFO A\n");
			sbSql.append("USING (\n");
			sbSql.append("        SELECT\n");
			sbSql.append("            A.ORG_CODE AS ORG_DEP_ID\n");
			sbSql.append("            , FN_VARCHAR2_TO_ASCII(CAST(A.ORG_CODE AS VARCHAR2(500))) AS DEP_ID\n");
			sbSql.append("            , FN_VARCHAR2_TO_ASCII(CAST(A.PARENT_ORG_CODE AS VARCHAR2(500))) AS PAR_DEP_ID\n");
			sbSql.append("            , A.ORG_NAME AS DEP_TITLE\n");
			sbSql.append("            , '부서 연계정보' AS DEP_DESC\n");
			sbSql.append("            , NULL AS DEP_TP_CD\n");
			sbSql.append("            , 0 AS DISPLAY_DEPTH\n");
			sbSql.append("            , NULL AS DISPLAY_ORDER\n");
			sbSql.append("            , 4 AS CRE_ID\n");
			sbSql.append("            , TO_CHAR(SYSDATE, 'yyyyMMddHH24miss') AS CRE_DT\n");
			sbSql.append("            , NULL AS UPD_ID\n");
			sbSql.append("            , NULL AS UPD_DT\n");
			sbSql.append("        FROM (\n");
			sbSql.append("                SELECT A.*\n");
			sbSql.append("                FROM EAI_HR_ORG_INFO A,\n");
			sbSql.append("                     (SELECT ORG_CODE, MAX(EAI_SEQ_ID) AS EAI_SEQ_ID\n");
			sbSql.append("                      FROM EAI_HR_ORG_INFO\n");
			sbSql.append("                      WHERE APPLICATION_TRANSFER_FLAG = 'P'\n");
			sbSql.append("                      GROUP BY ORG_CODE) B\n");
			sbSql.append("                WHERE A.APPLICATION_TRANSFER_FLAG = 'P'\n");
			sbSql.append("                  AND A.ORG_CODE = B.ORG_CODE\n");
			sbSql.append("                  AND A.EAI_SEQ_ID = B.EAI_SEQ_ID\n");
			sbSql.append("             ) A\n");
			sbSql.append("        WHERE A.DATA_INTERFACE_TYPE_CODE IN ('I', 'U')\n");
			sbSql.append("          AND (A.END_DATE IS NULL OR (A.END_DATE IS NOT NULL AND TO_DATE(A.END_DATE, 'yyyy-MM-dd') >= TRUNC(SYSDATE)))\n");
			sbSql.append("      ) B\n");
			sbSql.append("   ON (A.DEP_ID = B.DEP_ID)\n");
			sbSql.append(" WHEN NOT MATCHED THEN\n");
			sbSql.append("   INSERT (A.DEP_ID,A.PAR_DEP_ID,A.DEP_TITLE,A.DEP_DESC,A.DEP_TP_CD,A.DISPLAY_DEPTH,A.DISPLAY_ORDER,A.CRE_ID,A.CRE_DT,A.UPD_ID,A.UPD_DT)\n");
			sbSql.append("   VALUES (B.DEP_ID,B.PAR_DEP_ID,B.DEP_TITLE,B.DEP_DESC,B.DEP_TP_CD,B.DISPLAY_DEPTH,B.DISPLAY_ORDER,B.CRE_ID,B.CRE_DT,B.UPD_ID,B.UPD_DT)\n");
			sbSql.append(" WHEN MATCHED THEN\n");
			sbSql.append("   UPDATE SET\n");
			sbSql.append("     A.PAR_DEP_ID = B.PAR_DEP_ID,\n");
			sbSql.append("     A.DEP_TITLE = B.DEP_TITLE,\n");
			sbSql.append("     A.DEP_DESC = B.DEP_DESC,\n");
			sbSql.append("     A.DEP_TP_CD = B.DEP_TP_CD,\n");
			sbSql.append("     A.DISPLAY_DEPTH = B.DISPLAY_DEPTH,\n");
			sbSql.append("     A.DISPLAY_ORDER = B.DISPLAY_ORDER,\n");
			sbSql.append("     A.CRE_ID = B.CRE_ID,\n");
			sbSql.append("     A.CRE_DT = B.CRE_DT,\n");
			sbSql.append("     A.UPD_ID = B.UPD_ID,\n");
			sbSql.append("     A.UPD_DT = B.UPD_DT\n");
			
			retCnt += qHelper.executeUpdate(sbSql.toString(), new Object[] {});
			
			// 협력사의 부서 정보 이행 (INSERT/UPDATE)
			// 부서 코드 전체를 아스키 코드로 변환하여 DEP_ID로 사용한다
			// 협력사 DEP_ID=1000001
			sbSql = new StringBuffer();
			sbSql.append("MERGE INTO C_DEP_INFO A\n");
			sbSql.append("USING (\n");
			sbSql.append("         SELECT DEP_ID,\n");
			sbSql.append("                1000001 AS PAR_DEP_ID,\n");
			sbSql.append("                DEP_TITLE,\n");
			sbSql.append("                '부서 연계정보' AS DEP_DESC,\n");
			sbSql.append("                1 AS DISPLAY_DEPTH,\n");
			sbSql.append("                4 AS CRE_ID,\n");
			sbSql.append("                TO_CHAR(SYSDATE, 'yyyyMMddHH24miss') AS CRE_DT\n");
			sbSql.append("         FROM (\n");
			sbSql.append("                 SELECT DISTINCT\n");
			sbSql.append("                        FN_VARCHAR2_TO_ASCII(CAST(A.DEPT_CD AS VARCHAR2(500))) AS DEP_ID,\n");
			sbSql.append("                        A.DEPT_CD_NM AS DEP_TITLE\n");
			sbSql.append("                 FROM EAI_XHR_INFO A,\n");
			sbSql.append("                      (SELECT DEPT_CD, MAX(EAI_SEQ_ID) AS EAI_SEQ_ID\n");
			sbSql.append("                       FROM EAI_XHR_INFO\n");
			sbSql.append("                       WHERE APPLICATION_TRANSFER_FLAG = 'P'\n");
			sbSql.append("                       GROUP BY DEPT_CD) B\n");
			sbSql.append("                 WHERE A.DEPT_CD = B.DEPT_CD\n");
			sbSql.append("                   AND A.EAI_SEQ_ID = B.EAI_SEQ_ID\n");
			sbSql.append("                   AND APPLICATION_TRANSFER_FLAG = 'P'\n");
			sbSql.append("              )\n");
			sbSql.append("      ) B\n");
			sbSql.append("   ON (A.DEP_ID = B.DEP_ID)\n");
			sbSql.append(" WHEN NOT MATCHED THEN\n");
			sbSql.append("   INSERT (A.DEP_ID, A.PAR_DEP_ID, A.DEP_TITLE, A.DEP_DESC, A.DISPLAY_DEPTH, A.CRE_ID, A.CRE_DT)\n");
			sbSql.append("   VALUES (B.DEP_ID, B.PAR_DEP_ID, B.DEP_TITLE, B.DEP_DESC, B.DISPLAY_DEPTH, B.CRE_ID, B.CRE_DT)\n");
			sbSql.append(" WHEN MATCHED THEN\n");
			sbSql.append("   UPDATE SET\n");
			sbSql.append("     A.DEP_TITLE = B.DEP_TITLE\n");
			
			retCnt += qHelper.executeUpdate(sbSql.toString(), new Object[] {});
			
			// 부서 정보 이행 (DELETE) '삭제부서'로 이동
			sbSql = new StringBuffer();
			sbSql.append("UPDATE C_DEP_INFO\n");
			sbSql.append("SET PAR_DEP_ID = 1000000\n");
			sbSql.append("WHERE DEP_ID IN (\n");
			sbSql.append("        SELECT ORG_CODE\n");
			sbSql.append("        FROM EAI_HR_ORG_INFO A\n");
			sbSql.append("        WHERE ( A.DATA_INTERFACE_TYPE_CODE = 'D' OR (A.END_DATE IS NOT NULL AND A.END_DATE < TO_CHAR(SYSDATE, 'yyyyMMddHH24miss')) )\n");
			sbSql.append("          AND A.APPLICATION_TRANSFER_FLAG = 'P'\n");
			sbSql.append("      )\n");
			
			retCnt += qHelper.executeUpdate(sbSql.toString(), new Object[] {});
			
			System.out.println("### UserCollector.collectDepInfo() retCnt : "+retCnt);
			
			return retCnt;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (rs != null) try { rs.close(); } catch (Exception ignore) {}
		}
	}
	
	/**
	 * 부서 정보 수집 (LCD WEB에서 사용하는 DEP정보, 추가컬럼때문에 별도로 관리)
	 * 
	 * @param qHelper
	 * @throws Exception
	 */
	private int collectDepInfoWeb(QueryHelper qHelper) throws Exception {
		ResultSet rs = null;
		StringBuffer sbSql = null;
		int retCnt = 0;
		
		try {
			// 부서 정보 이행 (INSERT/UPDATE)
			// 부서 코드 전체를 아스키 코드로 변환하여 DEP_ID로 사용한다
			sbSql = new StringBuffer();
			sbSql.append("MERGE INTO C_DEP_INFO_WEB A\n");
			sbSql.append("USING (\n");
			sbSql.append("        SELECT\n");
			sbSql.append("            A.ORG_CODE AS ORG_DEP_ID\n");
			sbSql.append("            , FN_VARCHAR2_TO_ASCII(CAST(A.ORG_CODE AS VARCHAR2(500))) AS DEP_ID\n");
			sbSql.append("            , FN_VARCHAR2_TO_ASCII(CAST(A.PARENT_ORG_CODE AS VARCHAR2(500))) AS PAR_DEP_ID\n");
			sbSql.append("            , A.ORG_NAME AS DEP_TITLE\n");
			sbSql.append("            , '부서 연계정보' AS DEP_DESC\n");
			sbSql.append("            , NULL AS DEP_TP_CD\n");
			sbSql.append("            , 0 AS DISPLAY_DEPTH\n");
			sbSql.append("            , NULL AS DISPLAY_ORDER\n");
			sbSql.append("            , 4 AS CRE_ID\n");
			sbSql.append("            , TO_CHAR(SYSDATE, 'yyyyMMddHH24miss') AS CRE_DT\n");
			sbSql.append("            , NULL AS UPD_ID\n");
			sbSql.append("            , NULL AS UPD_DT\n");
			sbSql.append("            , A.ORG_LEVEL_CODE AS ORG_LEVEL_CODE\n");
			sbSql.append("            , A.ORG_LEVEL_NAME AS ORG_LEVEL_NAME\n");
			sbSql.append("            , A.ORG_GROUP_CODE AS ORG_GROUP_CODE\n");
			sbSql.append("            , A.ORG_GROUP_NAME AS ORG_GROUP_NAME\n");			
			sbSql.append("        FROM (\n");
			sbSql.append("                SELECT A.*\n");
			sbSql.append("                FROM EAI_HR_ORG_INFO A,\n");
			sbSql.append("                     (SELECT ORG_CODE, MAX(EAI_SEQ_ID) AS EAI_SEQ_ID\n");
			sbSql.append("                      FROM EAI_HR_ORG_INFO\n");
			sbSql.append("                      WHERE APPLICATION_TRANSFER_FLAG = 'P'\n");
			sbSql.append("                      GROUP BY ORG_CODE) B\n");
			sbSql.append("                WHERE A.APPLICATION_TRANSFER_FLAG = 'P'\n");
			sbSql.append("                  AND A.ORG_CODE = B.ORG_CODE\n");
			sbSql.append("                  AND A.EAI_SEQ_ID = B.EAI_SEQ_ID\n");
			sbSql.append("             ) A\n");
			sbSql.append("        WHERE A.DATA_INTERFACE_TYPE_CODE IN ('I', 'U')\n");
			sbSql.append("          AND (A.END_DATE IS NULL OR (A.END_DATE IS NOT NULL AND TO_DATE(A.END_DATE, 'yyyy-MM-dd') >= TRUNC(SYSDATE)))\n");
			sbSql.append("      ) B\n");
			sbSql.append("   ON (A.DEP_ID = B.DEP_ID)\n");
			sbSql.append(" WHEN NOT MATCHED THEN\n");
			sbSql.append("   INSERT (A.DEP_ID,A.PAR_DEP_ID,A.DEP_TITLE,A.DEP_DESC,A.DEP_TP_CD,A.DISPLAY_DEPTH,A.DISPLAY_ORDER,A.CRE_ID,A.CRE_DT,A.UPD_ID,A.UPD_DT,A.ORG_LEVEL_CODE,A.ORG_LEVEL_NAME,A.ORG_GROUP_CODE,A.ORG_GROUP_NAME)\n");
			sbSql.append("   VALUES (B.DEP_ID,B.PAR_DEP_ID,B.DEP_TITLE,B.DEP_DESC,B.DEP_TP_CD,B.DISPLAY_DEPTH,B.DISPLAY_ORDER,B.CRE_ID,B.CRE_DT,B.UPD_ID,B.UPD_DT,B.ORG_LEVEL_CODE,B.ORG_LEVEL_NAME,B.ORG_GROUP_CODE,B.ORG_GROUP_NAME)\n");
			sbSql.append(" WHEN MATCHED THEN\n");
			sbSql.append("   UPDATE SET\n");
			sbSql.append("     A.PAR_DEP_ID = B.PAR_DEP_ID,\n");
			sbSql.append("     A.DEP_TITLE = B.DEP_TITLE,\n");
			sbSql.append("     A.DEP_DESC = B.DEP_DESC,\n");
			sbSql.append("     A.DEP_TP_CD = B.DEP_TP_CD,\n");
			sbSql.append("     A.DISPLAY_DEPTH = B.DISPLAY_DEPTH,\n");
			sbSql.append("     A.DISPLAY_ORDER = B.DISPLAY_ORDER,\n");
			sbSql.append("     A.CRE_ID = B.CRE_ID,\n");
			sbSql.append("     A.CRE_DT = B.CRE_DT,\n");
			sbSql.append("     A.UPD_ID = B.UPD_ID,\n");
			sbSql.append("     A.UPD_DT = B.UPD_DT,\n");
			sbSql.append("     A.ORG_LEVEL_CODE = B.ORG_LEVEL_CODE,\n");
			sbSql.append("     A.ORG_LEVEL_NAME = B.ORG_LEVEL_NAME,\n");
			sbSql.append("     A.ORG_GROUP_CODE = B.ORG_GROUP_CODE,\n");
			sbSql.append("     A.ORG_GROUP_NAME = B.ORG_GROUP_NAME\n");

			retCnt += qHelper.executeUpdate(sbSql.toString(), new Object[] {});
			
			// 협력사의 부서 정보 이행 (INSERT/UPDATE)
			// 부서 코드 전체를 아스키 코드로 변환하여 DEP_ID로 사용한다
			// 협력사 DEP_ID=1000001
			sbSql = new StringBuffer();
			sbSql.append("MERGE INTO C_DEP_INFO_WEB A\n");
			sbSql.append("USING (\n");
			sbSql.append("         SELECT DEP_ID,\n");
			sbSql.append("                1000001 AS PAR_DEP_ID,\n");
			sbSql.append("                DEP_TITLE,\n");
			sbSql.append("                '부서 연계정보' AS DEP_DESC,\n");
			sbSql.append("                1 AS DISPLAY_DEPTH,\n");
			sbSql.append("                4 AS CRE_ID,\n");
			sbSql.append("                TO_CHAR(SYSDATE, 'yyyyMMddHH24miss') AS CRE_DT\n");
			sbSql.append("         FROM (\n");
			sbSql.append("                 SELECT DISTINCT\n");
			sbSql.append("                        FN_VARCHAR2_TO_ASCII(CAST(A.DEPT_CD AS VARCHAR2(500))) AS DEP_ID,\n");
			sbSql.append("                        A.DEPT_CD_NM AS DEP_TITLE\n");
			sbSql.append("                 FROM EAI_XHR_INFO A,\n");
			sbSql.append("                      (SELECT DEPT_CD, MAX(EAI_SEQ_ID) AS EAI_SEQ_ID\n");
			sbSql.append("                       FROM EAI_XHR_INFO\n");
			sbSql.append("                       WHERE APPLICATION_TRANSFER_FLAG = 'P'\n");
			sbSql.append("                       GROUP BY DEPT_CD) B\n");
			sbSql.append("                 WHERE A.DEPT_CD = B.DEPT_CD\n");
			sbSql.append("                   AND A.EAI_SEQ_ID = B.EAI_SEQ_ID\n");
			sbSql.append("                   AND APPLICATION_TRANSFER_FLAG = 'P'\n");
			sbSql.append("              )\n");
			sbSql.append("      ) B\n");
			sbSql.append("   ON (A.DEP_ID = B.DEP_ID)\n");
			sbSql.append(" WHEN NOT MATCHED THEN\n");
			sbSql.append("   INSERT (A.DEP_ID, A.PAR_DEP_ID, A.DEP_TITLE, A.DEP_DESC, A.DISPLAY_DEPTH, A.CRE_ID, A.CRE_DT)\n");
			sbSql.append("   VALUES (B.DEP_ID, B.PAR_DEP_ID, B.DEP_TITLE, B.DEP_DESC, B.DISPLAY_DEPTH, B.CRE_ID, B.CRE_DT)\n");
			sbSql.append(" WHEN MATCHED THEN\n");
			sbSql.append("   UPDATE SET\n");
			sbSql.append("     A.DEP_TITLE = B.DEP_TITLE\n");
			
			retCnt += qHelper.executeUpdate(sbSql.toString(), new Object[] {});
			
			// 부서 정보 이행 (DELETE) '삭제부서'로 이동
			sbSql = new StringBuffer();
			sbSql.append("UPDATE C_DEP_INFO_WEB\n");
			sbSql.append("SET PAR_DEP_ID = 1000000\n");
			sbSql.append("WHERE DEP_ID IN (\n");
			sbSql.append("        SELECT ORG_CODE\n");
			sbSql.append("        FROM EAI_HR_ORG_INFO A\n");
			sbSql.append("        WHERE ( A.DATA_INTERFACE_TYPE_CODE = 'D' OR (A.END_DATE IS NOT NULL AND A.END_DATE < TO_CHAR(SYSDATE, 'yyyyMMddHH24miss')) )\n");
			sbSql.append("          AND A.APPLICATION_TRANSFER_FLAG = 'P'\n");
			sbSql.append("      )\n");
			
			retCnt += qHelper.executeUpdate(sbSql.toString(), new Object[] {});
			
			System.out.println("### UserCollector.collectDepInfoWeb() retCnt : "+retCnt);
			
			return retCnt;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (rs != null) try { rs.close(); } catch (Exception ignore) {}
		}
	}	
	
	/**
	 * 직급 정보 수집
	 * 
	 * @param qHelper
	 * @throws Exception
	 */
	private int collectPositionInfo(QueryHelper qHelper) throws Exception {
		ResultSet rs = null;
		StringBuffer sbSql = null;
		int retCnt = 0;
		
		try {
			// MAX 직급 CD_ID (그룹 코드 = 2100)
			String maxCdId = "2100";
			sbSql = new StringBuffer("SELECT NVL(MAX(CD_ID), 2100) AS MAX_CD_ID FROM C_CD WHERE CD_ID LIKE '2%' AND CD_ID NOT LIKE '20%'");
			
			rs = qHelper.executeQuery(sbSql.toString());
			if (rs.next()) {
				maxCdId = StringHelper.evl(rs.getString("MAX_CD_ID"), "2100");
			}
			
			// 직급코드 INSERT
			sbSql = new StringBuffer();
			sbSql.append("INSERT INTO C_CD (CD_ID, CD_GRP_ID, DISPLAY_ORDER, DEL_YN)\n");
			sbSql.append("SELECT NVL(?, 2100) + A.RNUM AS CD_ID,\n");
			sbSql.append("       2100 AS CD_GRP_ID,\n");
			sbSql.append("       (SELECT NVL(MAX(DISPLAY_ORDER), 0) FROM C_CD WHERE CD_GRP_ID = 2100)+A.RNUM AS DISPLAY_ORDER,\n");
			sbSql.append("       'N' AS DEL_YN\n");
			sbSql.append("FROM (\n");
			sbSql.append("        SELECT ROWNUM AS RNUM\n");
			sbSql.append("        FROM (\n");
			sbSql.append("                SELECT DISTINCT POSITION_CD_NM AS DISPLAY_NM\n");
			sbSql.append("                FROM (\n");
			sbSql.append("                        SELECT JOB_TITLE_CD AS POSITION_CD, JOB_TITLE_NM AS POSITION_CD_NM, APPLICATION_TRANSFER_FLAG FROM EAI_HR_INFO\n");
			sbSql.append("                        UNION\n");
			sbSql.append("                        SELECT POSITION_CD, POSITION_CD_NM, APPLICATION_TRANSFER_FLAG FROM EAI_XHR_INFO\n");
			sbSql.append("                     )\n");
			sbSql.append("                WHERE POSITION_CD IS NOT NULL\n");
			sbSql.append("                  AND POSITION_CD_NM IS NOT NULL\n");
			sbSql.append("                  AND POSITION_CD_NM NOT IN (SELECT DISPLAY_NM FROM C_CD_NM WHERE CD_ID LIKE '2%' AND CD_ID NOT LIKE '20%')\n");
			sbSql.append("                  AND APPLICATION_TRANSFER_FLAG = 'P'\n");
			sbSql.append("                ORDER BY POSITION_CD_NM\n");
			sbSql.append("             )\n");
			sbSql.append("     ) A\n");
			
			retCnt += qHelper.executeUpdate(sbSql.toString(), new Object[] { maxCdId });
			
			// 직급코드명 INSERT
			sbSql = new StringBuffer();
			sbSql.append("MERGE INTO C_CD_NM A\n");
			sbSql.append("USING (\n");
			sbSql.append("        SELECT NVL(?, 2100) + A.RNUM AS CD_ID, B.LANG_CD, A.DISPLAY_NM\n");
			sbSql.append("        FROM (\n");
			sbSql.append("                SELECT DISPLAY_NM, ROWNUM AS RNUM\n");
			sbSql.append("                FROM (\n");
			sbSql.append("                        SELECT DISTINCT POSITION_CD_NM AS DISPLAY_NM\n");
			sbSql.append("                        FROM (\n");
			sbSql.append("                                SELECT JOB_TITLE_CD AS POSITION_CD, JOB_TITLE_NM AS POSITION_CD_NM, APPLICATION_TRANSFER_FLAG FROM EAI_HR_INFO\n");
			sbSql.append("                                UNION\n");
			sbSql.append("                                SELECT POSITION_CD, POSITION_CD_NM, APPLICATION_TRANSFER_FLAG FROM EAI_XHR_INFO\n");
			sbSql.append("                             )\n");
			sbSql.append("                        WHERE POSITION_CD IS NOT NULL\n");
			sbSql.append("                          AND POSITION_CD_NM IS NOT NULL\n");
			sbSql.append("                          AND POSITION_CD_NM NOT IN (SELECT DISPLAY_NM FROM C_CD_NM WHERE CD_ID LIKE '2%' AND CD_ID NOT LIKE '20%')\n");
			sbSql.append("                          AND APPLICATION_TRANSFER_FLAG = 'P'\n");
			sbSql.append("                        ORDER BY POSITION_CD_NM\n");
			sbSql.append("                     )\n");
			sbSql.append("             ) A,\n");
			sbSql.append("             (SELECT 'ko-KR' AS LANG_CD FROM DUAL UNION SELECT 'en-US' AS LANG_CD FROM DUAL) B\n");
			sbSql.append("        ORDER BY CD_ID, LANG_CD\n");
			sbSql.append("      ) B\n");
			sbSql.append("   ON (A.DISPLAY_NM = B.DISPLAY_NM AND\n");
			sbSql.append("       A.LANG_CD = B.LANG_CD)\n");
			sbSql.append(" WHEN NOT MATCHED THEN\n");
			sbSql.append("   INSERT (A.CD_ID, A.LANG_CD, A.DISPLAY_NM)\n");
			sbSql.append("   VALUES (B.CD_ID, B.LANG_CD, B.DISPLAY_NM)\n");
			
			qHelper.executeUpdate(sbSql.toString(), new Object[] { maxCdId });
			
			System.out.println("### UserCollector.collectPositionInfo() retCnt : "+retCnt);
			
			return retCnt;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (rs != null) try { rs.close(); } catch (Exception ignore) {}
		}
	}
	
	/**
	 * 사용자 정보 수집
	 * 
	 * @param qHelper
	 * @throws Exception
	 */
	private int collectUserInfo(QueryHelper qHelper) throws Exception {
		StringBuffer sbSql = null;
		int retCnt = 0;
		
		try {
			// 사용자 정보 이행 (EAI_HR_INFO) (INSERT/UPDATE)
			sbSql = new StringBuffer();
			sbSql.append("MERGE INTO C_USER A\n");
			sbSql.append("USING (\n");
			sbSql.append("        SELECT\n");
			sbSql.append("            (SELECT MAX(USER_ID) FROM C_USER) + EAI_SEQ_ID AS USER_ID\n");
			sbSql.append("            , NVL((\n");
			sbSql.append("                SELECT DEP_ID\n");
			sbSql.append("                FROM C_DEP_INFO A\n");
			sbSql.append("                WHERE\n");
			sbSql.append("                    FN_VARCHAR2_TO_ASCII(CAST (REPLACE(DEPT_CD,'X',9) AS VARCHAR2(500))) = A.DEP_ID(+)\n");
			sbSql.append("                ), 0) AS DEP_ID\n");
			sbSql.append("            , (\n");
			sbSql.append("                SELECT CD_ID\n");
			sbSql.append("                FROM C_CD_NM\n");
			sbSql.append("                WHERE\n");
			sbSql.append("                    CD_ID(+) LIKE '2%' AND LANG_CD(+) = 'ko-KR'\n");
			sbSql.append("                    AND POSITION_CD_NM = DISPLAY_NM(+)\n");
			sbSql.append("                ) AS POSITION_CODE\n");
			sbSql.append("            , EMP_KOR_NM AS USER_NM\n");
			sbSql.append("            , EMP_NO AS LOGIN_ID\n");
			sbSql.append("            , 'INF' AS SOC_NO\n");
			sbSql.append("            , EMAIL_ADDR AS EMAIL\n");
			sbSql.append("            , MOBILE_NO AS CELL_PHONE\n");
			sbSql.append("            , NVL(ACTIVE_YN, 'Y') AS ACTIVE_YN\n");
			sbSql.append("            , NVL(PWD_FAIL_COUNT, 0) AS PWD_FAIL_COUNT\n");
			sbSql.append("            , NVL(CRE_ID, 0) AS CRE_ID\n");
			sbSql.append("            , NVL(CRE_DT, TO_CHAR(EAI_INTERFACE_DATE, 'YYYYMMDDHH24MISS')) AS CRE_DT\n");
			sbSql.append("            , NVL(UPD_ID, 0) AS UPD_ID\n");
			sbSql.append("            , NVL(UPD_DT, TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS')) AS UPD_DT\n");
			sbSql.append("            , NVL(DEL_YN, 'N') AS DEL_YN\n");
			sbSql.append("            , NVL(OFF_DUTY_YN, 'Y') AS OFF_DUTY_YN\n");
			sbSql.append("        FROM (\n");
			sbSql.append("                SELECT\n");
			sbSql.append("                    ROW_NUMBER() OVER (PARTITION BY EMP_NO ORDER BY EAI_INTERFACE_DATE DESC) AS RNUM\n");
			sbSql.append("                    , EAI_SEQ_ID, DEPT_CD, EMP_KOR_NM, EMP_NO, EMAIL_ADDR, MOBILE_NO, POSITION_CD_NM, EAI_INTERFACE_DATE\n");
			sbSql.append("                    , ACTIVE_YN, PWD_FAIL_COUNT, CRE_ID, CRE_DT, UPD_ID, UPD_DT, DEL_YN, OFF_DUTY_YN\n");
			sbSql.append("                FROM (\n");
			sbSql.append("                        SELECT A.EAI_SEQ_ID, A.DEPT_CD, A.EMP_KOR_NM, A.EMP_NO, A.EMAIL_ADDR, A.MOBILE_NO, A.JOB_TITLE_NM AS POSITION_CD_NM, A.EAI_INTERFACE_DATE,\n");
			sbSql.append("                               B.ACTIVE_YN, B.PWD_FAIL_COUNT, B.CRE_ID, B.CRE_DT, B.UPD_ID, B.UPD_DT, B.DEL_YN, B.OFF_DUTY_YN\n");
			sbSql.append("                        FROM EAI_HR_INFO A, C_USER B,\n");
			sbSql.append("                             (SELECT EMP_NO, MAX(EAI_SEQ_ID) AS EAI_SEQ_ID\n");
			sbSql.append("                              FROM EAI_HR_INFO\n");
			sbSql.append("                              WHERE APPLICATION_TRANSFER_FLAG = 'P'\n");
			sbSql.append("                              GROUP BY EMP_NO) C\n");
			sbSql.append("                        WHERE A.EMP_NO = B.LOGIN_ID(+)\n");
			sbSql.append("                          AND A.DATA_INTERFACE_TYPE_CODE IN ('I', 'U')\n");
			sbSql.append("                          AND A.APPLICATION_TRANSFER_FLAG = 'P'\n");
			sbSql.append("                          AND A.EMP_NO = C.EMP_NO\n");
			sbSql.append("                          AND A.EAI_SEQ_ID = C.EAI_SEQ_ID\n");
			sbSql.append("                          AND A.RETIRE_FLAG <> 'T'\n");
			sbSql.append("                        ORDER BY EMP_NO\n");
			sbSql.append("                     )\n");
			sbSql.append("            )\n");
			sbSql.append("        WHERE RNUM = 1\n");
			sbSql.append("        ORDER BY EMP_NO\n");
			sbSql.append("      ) B\n");
			sbSql.append("   ON (A.LOGIN_ID = B.LOGIN_ID)\n");
			sbSql.append(" WHEN NOT MATCHED THEN\n");
			sbSql.append("   INSERT (A.USER_ID,A.LOGIN_ID,A.DEP_ID,A.POSITION_CODE,A.USER_NM,A.SOC_NO,A.EMAIL,A.CELL_PHONE,A.ACTIVE_YN,A.PWD_FAIL_COUNT,A.CRE_ID,A.CRE_DT,A.UPD_ID,A.UPD_DT,A.DEL_YN,A.OFF_DUTY_YN)\n");
			sbSql.append("   VALUES (B.USER_ID,B.LOGIN_ID,B.DEP_ID,B.POSITION_CODE,B.USER_NM,B.SOC_NO,B.EMAIL,B.CELL_PHONE,B.ACTIVE_YN,B.PWD_FAIL_COUNT,B.CRE_ID,B.CRE_DT,B.UPD_ID,B.UPD_DT,B.DEL_YN,B.OFF_DUTY_YN)\n");
			sbSql.append(" WHEN MATCHED THEN\n");
			sbSql.append("   UPDATE SET\n");
			sbSql.append("     A.DEP_ID = B.DEP_ID,\n");
			sbSql.append("     A.POSITION_CODE = B.POSITION_CODE,\n");
			sbSql.append("     A.USER_NM = B.USER_NM,\n");
			sbSql.append("     A.SOC_NO = B.SOC_NO,\n");
			sbSql.append("     A.EMAIL = B.EMAIL,\n");
			sbSql.append("     A.CELL_PHONE = B.CELL_PHONE,\n");
			sbSql.append("     A.ACTIVE_YN = B.ACTIVE_YN,\n");
			sbSql.append("     A.PWD_FAIL_COUNT = B.PWD_FAIL_COUNT,\n");
			sbSql.append("     A.CRE_ID = B.CRE_ID,\n");
			sbSql.append("     A.CRE_DT = B.CRE_DT,\n");
			sbSql.append("     A.UPD_ID = B.UPD_ID,\n");
			sbSql.append("     A.UPD_DT = B.UPD_DT,\n");
			sbSql.append("     A.DEL_YN = B.DEL_YN,\n");
			sbSql.append("     A.OFF_DUTY_YN = B.OFF_DUTY_YN\n");
			
			retCnt += qHelper.executeUpdate(sbSql.toString(), new Object[] {});
			
			// 사용자 정보(협력사) 이행(EAI_XHR_INFO) (INSERT/UPDATE)
			sbSql = new StringBuffer();
			sbSql.append("MERGE INTO C_USER A\n");
			sbSql.append("USING (\n");
			sbSql.append("        SELECT\n");
			sbSql.append("            (SELECT MAX(USER_ID) FROM C_USER) + EAI_SEQ_ID AS USER_ID\n");
			sbSql.append("            , NVL((\n");
			sbSql.append("                SELECT DEP_ID\n");
			sbSql.append("                FROM C_DEP_INFO A\n");
			sbSql.append("                WHERE\n");
			sbSql.append("                    FN_VARCHAR2_TO_ASCII(CAST (DEPT_CD AS VARCHAR2(500))) = A.DEP_ID(+)\n");
			sbSql.append("                ), 0) AS DEP_ID\n");
			sbSql.append("            , (\n");
			sbSql.append("                SELECT CD_ID\n");
			sbSql.append("                FROM C_CD_NM\n");
			sbSql.append("                WHERE\n");
			sbSql.append("                    CD_ID(+) LIKE '2%' AND LANG_CD(+) = 'ko-KR'\n");
			sbSql.append("                    AND POSITION_CD_NM = DISPLAY_NM(+)\n");
			sbSql.append("                ) AS POSITION_CODE\n");
			sbSql.append("            , EMP_KOR_NM AS USER_NM\n");
			sbSql.append("            , EMP_NO AS LOGIN_ID\n");
			sbSql.append("            , 'INF' AS SOC_NO\n");
			sbSql.append("            , EMAIL_ADDR AS EMAIL\n");
			sbSql.append("            , MOBILE_NO AS CELL_PHONE\n");
			sbSql.append("            , NVL(ACTIVE_YN, 'Y') AS ACTIVE_YN\n");
			sbSql.append("            , NVL(PWD_FAIL_COUNT, 0) AS PWD_FAIL_COUNT\n");
			sbSql.append("            , NVL(CRE_ID, 0) AS CRE_ID\n");
			sbSql.append("            , NVL(CRE_DT, TO_CHAR(EAI_INTERFACE_DATE, 'YYYYMMDDHH24MISS')) AS CRE_DT\n");
			sbSql.append("            , NVL(UPD_ID, 0) AS UPD_ID\n");
			sbSql.append("            , NVL(UPD_DT, TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS')) AS UPD_DT\n");
			sbSql.append("            , NVL(DEL_YN, 'N') AS DEL_YN\n");
			sbSql.append("            , NVL(OFF_DUTY_YN, 'Y') AS OFF_DUTY_YN\n");
			sbSql.append("        FROM (\n");
			sbSql.append("                SELECT\n");
			sbSql.append("                    ROW_NUMBER() OVER (PARTITION BY EMP_NO ORDER BY EAI_INTERFACE_DATE DESC) AS RNUM\n");
			sbSql.append("                    , EAI_SEQ_ID, DEPT_CD, EMP_KOR_NM, EMP_NO, EMAIL_ADDR, MOBILE_NO, POSITION_CD_NM, EAI_INTERFACE_DATE\n");
			sbSql.append("                    , ACTIVE_YN, PWD_FAIL_COUNT, CRE_ID, CRE_DT, UPD_ID, UPD_DT, DEL_YN, OFF_DUTY_YN\n");
			sbSql.append("                FROM (\n");
			sbSql.append("                        SELECT A.EAI_SEQ_ID, A.DEPT_CD, A.EMP_KOR_NM, A.EMP_NO, A.EMAIL_ADDR, A.MOBILE_NO, POSITION_CD_NM, A.EAI_INTERFACE_DATE,\n");
			sbSql.append("                               B.ACTIVE_YN, B.PWD_FAIL_COUNT, B.CRE_ID, B.CRE_DT, B.UPD_ID, B.UPD_DT, B.DEL_YN, B.OFF_DUTY_YN\n");
			sbSql.append("                        FROM EAI_XHR_INFO A, C_USER B,\n");
			sbSql.append("                             (SELECT EMP_NO, MAX(EAI_SEQ_ID) AS EAI_SEQ_ID\n");
			sbSql.append("                              FROM EAI_XHR_INFO\n");
			sbSql.append("                              WHERE APPLICATION_TRANSFER_FLAG = 'P'\n");
			sbSql.append("                              GROUP BY EMP_NO) C\n");
			sbSql.append("                        WHERE A.EMP_NO = B.LOGIN_ID(+)\n");
			sbSql.append("                          AND A.DATA_INTERFACE_TYPE_CODE IN ('I', 'U')\n");
			sbSql.append("                          AND A.APPLICATION_TRANSFER_FLAG = 'P'\n");
			sbSql.append("                          AND A.EMP_NO = C.EMP_NO\n");
			sbSql.append("                          AND A.EAI_SEQ_ID = C.EAI_SEQ_ID\n");
			sbSql.append("                          AND A.WORK_GB = 'F'\n");
			sbSql.append("                        ORDER BY EMP_NO\n");
			sbSql.append("                     )\n");
			sbSql.append("            )\n");
			sbSql.append("        WHERE RNUM = 1\n");
			sbSql.append("        ORDER BY EMP_NO\n");
			sbSql.append("      ) B\n");
			sbSql.append("   ON (A.LOGIN_ID = B.LOGIN_ID)\n");
			sbSql.append(" WHEN NOT MATCHED THEN\n");
			sbSql.append("   INSERT (A.USER_ID,A.LOGIN_ID,A.DEP_ID,A.POSITION_CODE,A.USER_NM,A.SOC_NO,A.EMAIL,A.CELL_PHONE,A.ACTIVE_YN,A.PWD_FAIL_COUNT,A.CRE_ID,A.CRE_DT,A.UPD_ID,A.UPD_DT,A.DEL_YN,A.OFF_DUTY_YN)\n");
			sbSql.append("   VALUES (B.USER_ID,B.LOGIN_ID,B.DEP_ID,B.POSITION_CODE,B.USER_NM,B.SOC_NO,B.EMAIL,B.CELL_PHONE,B.ACTIVE_YN,B.PWD_FAIL_COUNT,B.CRE_ID,B.CRE_DT,B.UPD_ID,B.UPD_DT,B.DEL_YN,B.OFF_DUTY_YN)\n");
			sbSql.append(" WHEN MATCHED THEN\n");
			sbSql.append("   UPDATE SET\n");
			sbSql.append("     A.DEP_ID = B.DEP_ID,\n");
			sbSql.append("     A.POSITION_CODE = B.POSITION_CODE,\n");
			sbSql.append("     A.USER_NM = B.USER_NM,\n");
			sbSql.append("     A.SOC_NO = B.SOC_NO,\n");
			sbSql.append("     A.EMAIL = B.EMAIL,\n");
			sbSql.append("     A.CELL_PHONE = B.CELL_PHONE,\n");
			sbSql.append("     A.ACTIVE_YN = B.ACTIVE_YN,\n");
			sbSql.append("     A.PWD_FAIL_COUNT = B.PWD_FAIL_COUNT,\n");
			sbSql.append("     A.CRE_ID = B.CRE_ID,\n");
			sbSql.append("     A.CRE_DT = B.CRE_DT,\n");
			sbSql.append("     A.UPD_ID = B.UPD_ID,\n");
			sbSql.append("     A.UPD_DT = B.UPD_DT,\n");
			sbSql.append("     A.DEL_YN = B.DEL_YN,\n");
			sbSql.append("     A.OFF_DUTY_YN = B.OFF_DUTY_YN\n");
			
			retCnt += qHelper.executeUpdate(sbSql.toString(), new Object[] {});
			
			// 사용자 정보 이행(EAI_HR_INFO) (DEL_YN = 'Y') 삭제 FLAG 처리
			sbSql = new StringBuffer();
			sbSql.append("MERGE INTO C_USER A\n");
			sbSql.append("USING (\n");
			sbSql.append("        SELECT\n");
			sbSql.append("            EMP_NO AS LOGIN_ID\n");
			sbSql.append("            , 'N' AS ACTIVE_YN\n");
			sbSql.append("            , 'Y' AS DEL_YN\n");
			sbSql.append("            , NVL(DEL_DT, TO_CHAR(SYSDATE, 'yyyyMMddHH24miss')) AS DEL_DT\n");
			sbSql.append("        FROM (\n");
			sbSql.append("                SELECT\n");
			sbSql.append("                    ROW_NUMBER() OVER (PARTITION BY EMP_NO ORDER BY EAI_INTERFACE_DATE DESC) AS RNUM\n");
			sbSql.append("                    , EAI_SEQ_ID, DEPT_CD, EMP_KOR_NM, EMP_NO, EMAIL_ADDR, MOBILE_NO, POSITION_CD_NM, EAI_INTERFACE_DATE\n");
			sbSql.append("                    , ACTIVE_YN, PWD_FAIL_COUNT, CRE_ID, CRE_DT, UPD_ID, UPD_DT, DEL_YN, DEL_DT, OFF_DUTY_YN\n");
			sbSql.append("                FROM (\n");
			sbSql.append("                        SELECT A.EAI_SEQ_ID, A.DEPT_CD, A.EMP_KOR_NM, A.EMP_NO, A.EMAIL_ADDR, A.MOBILE_NO, A.JOB_TITLE_NM AS POSITION_CD_NM, A.EAI_INTERFACE_DATE,\n");
			sbSql.append("                               B.ACTIVE_YN, B.PWD_FAIL_COUNT, B.CRE_ID, B.CRE_DT, B.UPD_ID, B.UPD_DT, B.DEL_YN, B.DEL_DT, B.OFF_DUTY_YN\n");
			sbSql.append("                        FROM EAI_HR_INFO A, C_USER B,\n");
			sbSql.append("                             (SELECT EMP_NO, MAX(EAI_SEQ_ID) AS EAI_SEQ_ID\n");
			sbSql.append("                              FROM EAI_HR_INFO\n");
			sbSql.append("                              WHERE APPLICATION_TRANSFER_FLAG = 'P'\n");
			sbSql.append("                              GROUP BY EMP_NO) C\n");
			sbSql.append("                        WHERE A.EMP_NO = B.LOGIN_ID(+)\n");
			sbSql.append("                          AND (A.DATA_INTERFACE_TYPE_CODE = 'D' OR A.RETIRE_FLAG = 'T')\n");
			sbSql.append("                          AND A.APPLICATION_TRANSFER_FLAG = 'P'\n");
			sbSql.append("                          AND A.EMP_NO = C.EMP_NO\n");
			sbSql.append("                          AND A.EAI_SEQ_ID = C.EAI_SEQ_ID\n");
			sbSql.append("                        ORDER BY EMP_NO\n");
			sbSql.append("                     )\n");
			sbSql.append("            )\n");
			sbSql.append("        WHERE RNUM = 1\n");
			sbSql.append("        ORDER BY EMP_NO\n");
			sbSql.append("      ) B\n");
			sbSql.append("   ON (A.LOGIN_ID = B.LOGIN_ID)\n");
			sbSql.append(" WHEN MATCHED THEN\n");
			sbSql.append("   UPDATE SET\n");
			sbSql.append("     A.ACTIVE_YN = B.ACTIVE_YN,\n");
			sbSql.append("     A.DEL_YN = B.DEL_YN,\n");
			sbSql.append("     A.DEL_DT = B.DEL_DT\n");
			
			retCnt += qHelper.executeUpdate(sbSql.toString(), new Object[] {});
			
			// 사용자 정보(협력사) 이행(EAI_XHR_INFO) (DEL_YN = 'Y') 삭제 FLAG 처리
			sbSql = new StringBuffer();
			sbSql.append("MERGE INTO C_USER A\n");
			sbSql.append("USING (\n");
			sbSql.append("        SELECT\n");
			sbSql.append("            EMP_NO AS LOGIN_ID\n");
			sbSql.append("            , 'N' AS ACTIVE_YN\n");
			sbSql.append("            , 'Y' AS DEL_YN\n");
			sbSql.append("            , NVL(DEL_DT, TO_CHAR(SYSDATE, 'yyyyMMddHH24miss')) AS DEL_DT\n");
			sbSql.append("        FROM (\n");
			sbSql.append("                SELECT\n");
			sbSql.append("                    ROW_NUMBER() OVER (PARTITION BY EMP_NO ORDER BY EAI_INTERFACE_DATE DESC) AS RNUM\n");
			sbSql.append("                    , EAI_SEQ_ID, DEPT_CD, EMP_KOR_NM, EMP_NO, EMAIL_ADDR, MOBILE_NO, POSITION_CD_NM, EAI_INTERFACE_DATE\n");
			sbSql.append("                    , ACTIVE_YN, PWD_FAIL_COUNT, CRE_ID, CRE_DT, UPD_ID, UPD_DT, DEL_YN, DEL_DT, OFF_DUTY_YN\n");
			sbSql.append("                FROM (\n");
			sbSql.append("                        SELECT A.EAI_SEQ_ID, A.DEPT_CD, A.EMP_KOR_NM, A.EMP_NO, A.EMAIL_ADDR, A.MOBILE_NO, A.POSITION_CD_NM, A.EAI_INTERFACE_DATE,\n");
			sbSql.append("                               B.ACTIVE_YN, B.PWD_FAIL_COUNT, B.CRE_ID, B.CRE_DT, B.UPD_ID, B.UPD_DT, B.DEL_YN, B.DEL_DT, B.OFF_DUTY_YN\n");
			sbSql.append("                        FROM EAI_XHR_INFO A, C_USER B,\n");
			sbSql.append("                             (SELECT EMP_NO, MAX(EAI_SEQ_ID) AS EAI_SEQ_ID\n");
			sbSql.append("                              FROM EAI_XHR_INFO\n");
			sbSql.append("                              WHERE APPLICATION_TRANSFER_FLAG = 'P'\n");
			sbSql.append("                              GROUP BY EMP_NO) C\n");
			sbSql.append("                        WHERE A.EMP_NO = B.LOGIN_ID(+)\n");
			sbSql.append("                          AND (A.DATA_INTERFACE_TYPE_CODE = 'D' OR A.WORK_GB = 'T')\n");
			sbSql.append("                          AND A.APPLICATION_TRANSFER_FLAG = 'P'\n");
			sbSql.append("                          AND A.EMP_NO = C.EMP_NO\n");
			sbSql.append("                          AND A.EAI_SEQ_ID = C.EAI_SEQ_ID\n");
			sbSql.append("                        ORDER BY EMP_NO\n");
			sbSql.append("                     )\n");
			sbSql.append("            )\n");
			sbSql.append("        WHERE RNUM = 1\n");
			sbSql.append("        ORDER BY EMP_NO\n");
			sbSql.append("      ) B\n");
			sbSql.append("   ON (A.LOGIN_ID = B.LOGIN_ID)\n");
			sbSql.append(" WHEN MATCHED THEN\n");
			sbSql.append("   UPDATE SET\n");
			sbSql.append("     A.ACTIVE_YN = B.ACTIVE_YN,\n");
			sbSql.append("     A.DEL_YN = B.DEL_YN,\n");
			sbSql.append("     A.DEL_DT = B.DEL_DT\n");
			
			retCnt += qHelper.executeUpdate(sbSql.toString(), new Object[] {});
			
			// SSO를 위해 PASSWORD 지정 
			String SSO_PWD = "M2RiYzg5OTk2ZWJlNDlmMmE0MGExODY2ODJlZmZmNGViMmIwOGZhNQ==";

			// 신규 사용자 PASSWORD 강제입력 
			sbSql = new StringBuffer();
			sbSql.append("UPDATE C_USER A\n");
			sbSql.append("   SET USER_PWD = '" + SSO_PWD + "'\n");
			sbSql.append(" WHERE USER_PWD IS NULL\n");

			retCnt += qHelper.executeUpdate(sbSql.toString(), new Object[] {});

			System.out.println("### UserCollector.collectUserInfo() retCnt : "+retCnt);
			
			return retCnt;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * 권한 부여
	 * 
	 * @param qHelper
	 * @throws Exception
	 */
	private void setUserAuthority(QueryHelper qHelper) throws Exception {
		StringBuffer sbSql = null;
		
		try {
			// 추가된 사용자의 권한을 초기셋팅 (INSERT)
			// EAI_HR_INFO의 SUPERVISOR인 사용자는 관리자 권한(GROUP_ID=101, ROLE_ID=102)을 부여
			// EAI_HR_INFO의 SUPERVISOR가 아닌 사용자는 일반 사용자 권한(GROUP_ID=102, ROLE_ID=103)을 부여
			sbSql = new StringBuffer();
			sbSql.append("MERGE INTO C_USER_GROUP_ROLE A\n");
			sbSql.append("USING (\n");
			sbSql.append("        SELECT\n");
			sbSql.append("            USER_ID\n");
			sbSql.append("            , CASE WHEN B.SUPERVISOR IS NOT NULL THEN 101 ELSE 102 END AS GROUP_ID\n");
			sbSql.append("            , CASE WHEN B.SUPERVISOR IS NOT NULL THEN 102 ELSE 103 END AS ROLE_ID\n");
			sbSql.append("            , 4 AS CRE_ID, TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS') AS CRE_DT\n");
			sbSql.append("        FROM C_USER A,\n");
			sbSql.append("             (SELECT DISTINCT SUPERVISOR FROM EAI_HR_INFO WHERE SUPERVISOR IS NOT NULL) B\n");
			sbSql.append("        WHERE A.LOGIN_ID = B.SUPERVISOR(+)\n");
			sbSql.append("          AND A.SOC_NO = 'INF'\n");
			sbSql.append("          AND A.LOGIN_ID IN (\n");
			sbSql.append("                SELECT EMP_NO FROM EAI_HR_INFO WHERE APPLICATION_TRANSFER_FLAG = 'P'\n");
			sbSql.append("                UNION\n");
			sbSql.append("                SELECT EMP_NO FROM EAI_XHR_INFO WHERE APPLICATION_TRANSFER_FLAG = 'P'\n");
			sbSql.append("              )\n");
			sbSql.append("      ) B\n");
			sbSql.append("   ON (A.USER_ID = B.USER_ID AND A.GROUP_ID = B.GROUP_ID AND A.ROLE_ID = B.ROLE_ID)\n");
			sbSql.append(" WHEN NOT MATCHED THEN\n");
			sbSql.append("   INSERT (A.USER_ID, A.GROUP_ID, A.ROLE_ID, A.CRE_ID, A.CRE_DT)\n");
			sbSql.append("   VALUES (B.USER_ID, B.GROUP_ID, B.ROLE_ID, B.CRE_ID, B.CRE_DT)\n");
			
			qHelper.executeUpdate(sbSql.toString(), new Object[] {});
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * '삭제부서' 부서 중 사용자가 하나도 없는 부서를 삭제
	 * 
	 * @param qHelper
	 * @throws Exception
	 */
	private void deleteDepInfo(QueryHelper qHelper) throws Exception {
		StringBuffer sbSql = null;
		
		try {
			// '삭제부서'(DEP_ID=1000000)에 속하면서 사용자가 한 명도 없는 부서를 삭제 (DELETE)
			sbSql = new StringBuffer();
			sbSql.append("DELETE FROM C_DEP_INFO\n");
			sbSql.append("WHERE PAR_DEP_ID = 1000000\n");
			sbSql.append("  AND DEP_ID NOT IN (SELECT DEP_ID FROM C_USER)\n");
			
			qHelper.executeUpdate(sbSql.toString(), new Object[] {});
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * '삭제부서' 부서 중 사용자가 하나도 없는 부서를 삭제 (LCD WEB에서 사용하는 DEP정보, 추가컬럼때문에 별도로 관리)
	 * 
	 * @param qHelper
	 * @throws Exception
	 */
	private void deleteDepInfoWeb(QueryHelper qHelper) throws Exception {
		StringBuffer sbSql = null;
		
		try {
			// '삭제부서'(DEP_ID=1000000)에 속하면서 사용자가 한 명도 없는 부서를 삭제 (DELETE)
			sbSql = new StringBuffer();
			sbSql.append("DELETE FROM C_DEP_INFO_WEB\n");
			sbSql.append("WHERE PAR_DEP_ID = 1000000\n");
			sbSql.append("  AND DEP_ID NOT IN (SELECT DEP_ID FROM C_USER)\n");
			
			qHelper.executeUpdate(sbSql.toString(), new Object[] {});
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}	
	
}
