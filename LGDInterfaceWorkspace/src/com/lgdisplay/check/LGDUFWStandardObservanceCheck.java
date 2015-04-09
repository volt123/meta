package com.lgdisplay.check;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import jspeed.base.jdbc.BaseSQLException;
import jspeed.base.jdbc.CacheResultSet;
import jspeed.base.jdbc.QueryHelper;

public class LGDUFWStandardObservanceCheck {
	
	public int ObservanceCheck() throws BaseSQLException {
		boolean debugMode = false;
		
		QueryHelper qHelper = null;
		int retCnt = 0;
		
		CacheResultSet cRs = null;
		
		ArrayList datStrcNamespace = new ArrayList();
		ArrayList datStrcId = new ArrayList();
		ArrayList ufwDicNm = new ArrayList();
		ArrayList ufwDicId = new ArrayList();
		ArrayList elemTypeId = new ArrayList();
		ArrayList dbmsTypeCd = new ArrayList();
		ArrayList lnkInfoNm = new ArrayList();
		ArrayList lnkInfoId = new ArrayList();
		ArrayList filterStr = new ArrayList();
		
		// setting work time - key data 임
		String workTime = "";
		{
			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmss");
			workTime = formatter.format(date);
			
			if (debugMode) {
				System.out.println ("------  wowDebug  ----- workTime : " + workTime + "---------------");
			}
		}
		
		try {
			qHelper = new QueryHelper();
			qHelper.begin();
			
			System.out.println("### UFWStandardObservanceCheck : START");

			// 0. 전체검사할 연결정보 목록 가져오기
			//    - 조건 : 연결정보명이 '[관리모델]', '[모델마트]', [DB] 로 시작하는 목록 가져오기 (전체)
			cRs = getCollectInfo(qHelper);
			
			int size = 0;
			while (cRs.next()) {
				datStrcNamespace.add((String)cRs.getObject("DAT_STRC_NAMESPACE"));
				datStrcId.add((String)cRs.getObject("DAT_STRC_ID"));
				ufwDicNm.add((String)cRs.getObject("UFW_DIC_ID"));
				ufwDicId.add((String)cRs.getObject("UFW_DIC_NM"));
				dbmsTypeCd.add((String)cRs.getObject("DBMS_TP_CD"));
				elemTypeId.add((String)cRs.getObject("ELEM_TP_ID"));
				lnkInfoNm.add((String)cRs.getObject("LNK_INFO_NM"));
				lnkInfoId.add((String)cRs.getObject("LNK_INFO_ID"));
				filterStr.add((String)cRs.getObject("FILTER_STR"));
				size++;
			}

			// 	 0. 결과 저장할 테이블 모두 지우기
			//       - LGD_UFW_OBSERVANCE_CHECK_STAT
			//       - LGD_UFW_STANDARD_CHECK_RESULT
			if (debugMode) {
				System.out.println ("------  wowDebug  --- Starting : deleteAlignmentTables ---------------");
			}
			
			deleteAlignmentTables(qHelper);
			
			if (debugMode) {
				System.out.println ("------  wowDebug  --- Ended : deleteAlignmentTables ---------------");
			}

			
			// 1. 표준준수 통계
			//    - LGD_UFW_OBSERVANCE_CHECK_STAT 에 저장
			if (debugMode) {
				System.out.println ("------  wowDebug  --- Starting : insertUFWObservanceCheckResult ---------------");
			}
			
			for (int i = 0; i < size; i++) {
				retCnt += insertUFWObservanceCheckResult (
						    qHelper, workTime, (String)datStrcNamespace.get(i), (String)datStrcId.get(i), (String)ufwDicNm.get(i), (String)ufwDicId.get(i),
						    (String)dbmsTypeCd.get(i), (String)elemTypeId.get(i), (String)lnkInfoNm.get(i), (String)lnkInfoId.get(i), (String)filterStr.get(i)
						  );
			}
			
			if (debugMode) {
				System.out.println ("------  wowDebug  --- Ended : insertUFWObservanceCheckResult ---------------");
			}
			
			qHelper.commit();
			
			System.out.println("### UFWStandardObservanceCheck : END");
			
		} catch (BaseSQLException e) {
			e.printStackTrace();
			if (qHelper != null) try { qHelper.rollback(); } catch (Exception ignore) {}
			System.out.println("### UFWStandardObservanceCheck : BaseSQLException : "+e.getMessage());
			
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			if (qHelper != null) try { qHelper.rollback(); } catch (Exception ignore) {}
			System.out.println("### UFWStandardObservanceCheck : Exception : "+e.getMessage());
			
			throw new BaseSQLException(e.getMessage());
		} finally {
			if (qHelper != null) try { qHelper.close(); } catch (Exception ignore) {}
		}
		
		return retCnt;
	}
	
	/**
	 *	 0. 전체검사할 연결정보 목록 가져오기
	 *	    - 조건 : 연결정보명이 '[관리모델]', '[모델마트]', [DB] 로 시작하는 목록 가져오기 (전체)
	 * 
	 * @param qHelper
	 * @throws Exception
	 */
	private CacheResultSet getCollectInfo(QueryHelper qHelper) throws Exception {

		CacheResultSet cRs = null;
		ResultSet rs = null;

		try {
			StringBuffer sbSql = new StringBuffer();

			sbSql.append("SELECT * FROM  \n");  
			sbSql.append("( \n");  
			sbSql.append("    SELECT   \n");  
			sbSql.append("           Y.DAT_STRC_ID, \n");  
			sbSql.append("           FN_GET_DAT_STRC_NAMESPACE(Y.DAT_STRC_ID,'','>') AS DAT_STRC_NAMESPACE,  \n");  
			sbSql.append("           Z.UFW_DIC_ID, (SELECT UFW_DIC_NM FROM MM_UFW_DIC WHERE UFW_DIC_ID = Z.UFW_DIC_ID) AS UFW_DIC_NM, \n");  
			sbSql.append("           Z.DBMS_TP_CD, 'ERWin' AS ELEM_TP_ID, \n");  
			sbSql.append("           X.LNK_INFO_ID, X.LNK_INFO_NM, X.FILTER_STR \n");  
			sbSql.append("      FROM  \n");  
			sbSql.append("           ( \n");  
			sbSql.append("            SELECT A.LNK_INFO_ID, A.LNK_INFO_NM, A.ROOT_ELEM_TP_ID, A.PRDT_CD,  REPLACE(B.FILTER_STR, '.*', '') AS FILTER_STR \n");  
			sbSql.append("              FROM MM_LNK_INFO A \n");  
			sbSql.append("              LEFT OUTER JOIN MM_LNK_INFO_FILTER B \n");  
			sbSql.append("                ON A.LNK_INFO_ID = B.LNK_INFO_ID \n");  
			sbSql.append("             WHERE A.DEL_YN = 'N' \n");  
			sbSql.append("               AND (A.LNK_INFO_NM LIKE '[관리모델]%') \n");  
			sbSql.append("             ORDER BY LNK_INFO_NM, FILTER_STR \n");  
			sbSql.append("            ) X, MM_ELEM_INFO Y, MM_DAT_STRC Z \n");  
			sbSql.append("      WHERE X.LNK_INFO_ID = Y.LNK_INFO_ID \n");  
			sbSql.append("        AND Y.ELEM_TP_ID = 'Model' \n");  
			sbSql.append("        AND Y.ELEM_PHSC_NM LIKE '[물리]%' \n");  
			sbSql.append("        AND Y.DAT_STRC_ID IS NOT NULL \n");  
			sbSql.append("        AND Y.DAT_STRC_ID = Z.DAT_STRC_ID \n");  
			sbSql.append("      ORDER BY Z.DISP_ODR  \n");  
			sbSql.append(") AA \n");  
			sbSql.append("UNION ALL \n");  
			sbSql.append("SELECT * FROM  \n");  
			sbSql.append("( \n");  
			sbSql.append("    SELECT   \n");  
			sbSql.append("           Y.DAT_STRC_ID, \n");  
			sbSql.append("           FN_GET_DAT_STRC_NAMESPACE(Y.DAT_STRC_ID,'','>') AS DAT_STRC_NAMESPACE,  \n");  
			sbSql.append("           Z.UFW_DIC_ID, (SELECT UFW_DIC_NM FROM MM_UFW_DIC WHERE UFW_DIC_ID = Z.UFW_DIC_ID) AS UFW_DIC_NM, \n");  
			sbSql.append("           Z.DBMS_TP_CD, 'ModelMart' AS ELEM_TP_ID, \n");  
			sbSql.append("           X.LNK_INFO_ID, X.LNK_INFO_NM, X.FILTER_STR  \n");  
			sbSql.append("      FROM  \n");  
			sbSql.append("           ( \n");  
			sbSql.append("            SELECT A.LNK_INFO_ID, A.LNK_INFO_NM, A.ROOT_ELEM_TP_ID, A.PRDT_CD,  REPLACE(B.FILTER_STR, '.*', '') AS FILTER_STR \n");  
			sbSql.append("              FROM MM_LNK_INFO A \n");  
			sbSql.append("              LEFT OUTER JOIN MM_LNK_INFO_FILTER B \n");  
			sbSql.append("                ON A.LNK_INFO_ID = B.LNK_INFO_ID \n");  
			sbSql.append("             WHERE A.DEL_YN = 'N' \n");  
			sbSql.append("               AND (A.LNK_INFO_NM LIKE '[모델마트]%') \n");  
			sbSql.append("             ORDER BY LNK_INFO_NM, FILTER_STR \n");  
			sbSql.append("            ) X, MM_ELEM_INFO Y, MM_DAT_STRC Z \n");  
			sbSql.append("      WHERE X.LNK_INFO_ID = Y.LNK_INFO_ID \n");  
			sbSql.append("        AND Y.ELEM_TP_ID = 'Model' \n");  
			sbSql.append("        AND Y.ELEM_PHSC_NM LIKE '[물리]%' \n");  
			sbSql.append("        AND Y.DAT_STRC_ID IS NOT NULL \n");  
			sbSql.append("        AND Y.DAT_STRC_ID = Z.DAT_STRC_ID \n");  
			sbSql.append("      ORDER BY Z.DISP_ODR \n");  
			sbSql.append(") BB \n");  
			sbSql.append("UNION ALL \n");  
			sbSql.append("SELECT * FROM \n");  
			sbSql.append("( \n");  
			sbSql.append("    SELECT   \n");  
			sbSql.append("           Y.DAT_STRC_ID, \n");  
			sbSql.append("           FN_GET_DAT_STRC_NAMESPACE(Y.DAT_STRC_ID,'','>') AS DAT_STRC_NAMESPACE,  \n");  
			sbSql.append("           Z.UFW_DIC_ID, (SELECT UFW_DIC_NM FROM MM_UFW_DIC WHERE UFW_DIC_ID = Z.UFW_DIC_ID) AS UFW_DIC_NM, \n");  
			sbSql.append("           Z.DBMS_TP_CD, 'DB Schema' AS ELEM_TP_ID, \n");  
			sbSql.append("           X.LNK_INFO_ID, X.LNK_INFO_NM, X.FILTER_STR  \n");  
			sbSql.append("      FROM  \n");  
			sbSql.append("           ( \n");  
			sbSql.append("            SELECT A.LNK_INFO_ID, A.LNK_INFO_NM, A.ROOT_ELEM_TP_ID, A.PRDT_CD,  REPLACE(B.FILTER_STR, '.*', '') AS FILTER_STR \n");  
			sbSql.append("              FROM MM_LNK_INFO A \n");  
			sbSql.append("              LEFT OUTER JOIN MM_LNK_INFO_FILTER B \n");  
			sbSql.append("                ON A.LNK_INFO_ID = B.LNK_INFO_ID \n");  
			sbSql.append("             WHERE A.DEL_YN = 'N' \n");  
			sbSql.append("               AND (A.LNK_INFO_NM LIKE '[DB]%') \n");  
			sbSql.append("             ORDER BY LNK_INFO_NM, FILTER_STR \n");  
			sbSql.append("            ) X, MM_ELEM_INFO Y, MM_DAT_STRC_ELEM_MAP YY, MM_DAT_STRC Z \n");  
			sbSql.append("      WHERE X.LNK_INFO_ID = Y.LNK_INFO_ID \n");  
			sbSql.append("        AND Y.ELEM_TP_ID = 'Schema' \n");  
			sbSql.append("        AND Y.DAT_STRC_ID IS NOT NULL \n");  
			sbSql.append("        AND Y.DAT_STRC_ID = Z.DAT_STRC_ID   \n");  
			sbSql.append("        AND Y.DAT_STRC_ID = YY.DAT_STRC_ID \n");  
			sbSql.append("        AND X.FILTER_STR = YY.REL_MODEL_NM \n");  
			sbSql.append("      ORDER BY Z.DISP_ODR  \n");  
			sbSql.append(") CC  \n");
			
			rs = qHelper.executeQuery(sbSql.toString(), new Object[] {});
			cRs = new CacheResultSet( rs );

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return cRs;
	}
	
	/**
	 *	 0. 결과 저장할 테이블 모두 지우기
	 *      - LGD_UFW_OBSERVANCE_CHECK_STAT
	 *      - LGD_UFW_STANDARD_CHECK_RESULT
	 * 
	 * @param qHelper
	 * @throws Exception
	 */
	private void deleteAlignmentTables(QueryHelper qHelper) throws Exception {
		String sql = null;
		
		try {
			sql = "DELETE FROM LGD_UFW_OBSERVANCE_CHECK_STAT \n";
			qHelper.executeUpdate(sql.toString(), new Object[] {});

			sql = "DELETE FROM LGD_UFW_STANDARD_CHECK_RESULT \n";
			qHelper.executeUpdate(sql.toString(), new Object[] {});

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}	

	/**
	 *	 1. 구조명에 해당하는 DB Schema 정보 가져오기 (각 건수 포함)
	 *	    - LGD_MODEL_ALIGN_CHECK_TYPE01 에 저장
	 * 
	 * @param qHelper, workTime, datStrcId
	 * @throws Exception
	 */
	private int insertUFWObservanceCheckResult( QueryHelper qHelper, String workTime, String datStrcNamespace, String datStrcId,
			                                    String ufwDicNm, String ufwDicId, String dbmsTypeCd, String elemTypeId, 
			                                    String lnkInfoNm, String lnkInfoId, String filterStr
			                                   ) throws Exception {

		int retDBSchemaCnt = 0;

		try {
			StringBuffer sbSql = new StringBuffer();
			
			sbSql.append("INSERT INTO LGD_MODEL_ALIGN_CHECK_TYPE01 \n");  
			sbSql.append("        (REG_DT, DAT_STRC_ID, DAT_STRC_NAMESPACE, DAT_STRC_NM, DAT_TABLE_CNT, DAT_COLUMN_CNT, DB_SID_NM, DB_SCHEMA_NM, DB_TABLE_CNT, DB_COLUMN_CNT) \n");  
			sbSql.append("        SELECT '" + workTime + "' AS REG_DT, \n");  
			sbSql.append("               '" + datStrcId + "' AS DAT_STRC_ID, \n");  
			sbSql.append("               '" + datStrcNamespace + "' AS DAT_STRC_NAMESPACE,  \n");  
			sbSql.append("               X.SRC_STRC_NM AS DAT_STRC_NM,  \n");  
			sbSql.append("               X.SRC_TABLE_CNT AS DAT_STRC_TABLE_CNT, \n");  
			sbSql.append("               X.SRC_COLUMN_CNT AS DAT_STRC_COLUMN_CNT, \n");  
			sbSql.append("               Y.TRG_SID AS DB_SID_NM,  \n");  
			sbSql.append("               Y.TRG_SCHEMA AS DB_SCHEMA_NM, \n");  
			sbSql.append("               Y.TRG_TABLE_CNT AS DB_TABLE_CNT, \n");  
			sbSql.append("               Y.TRG_COLUMN_CNT AS DB_COLUMN_CNT \n");  
			sbSql.append("          FROM (  \n");  
			sbSql.append("                SELECT DAT_STRC_NM AS SRC_STRC_NM,  \n");  
			sbSql.append("                      NAMESPACE AS SRC_NAMESPACE,  \n");  
			sbSql.append("                      DAT_STRC_ID AS SRC_ID, \n");  
			sbSql.append("                      ( \n");  
			sbSql.append("                        SELECT COUNT(*)  \n");  
			sbSql.append("                          FROM MM_ELEM_INFO I , MM_ELEM_INFO J \n");  
			sbSql.append("                         WHERE I.DEL_YN='N'  \n");  
			sbSql.append("                           AND J.ELEM_INFO_ID=AA.ELEM_INFO_ID  \n");  
			sbSql.append("                           AND I.ELEM_TP_ID='Entity'  \n");  
			sbSql.append("                           AND I.LNK_INFO_ID=j.LNK_INFO_ID  \n");  
			sbSql.append("                           AND I.NAMESPACE like J.NAMESPACE || '.%'            \n");  
			sbSql.append("                      ) AS SRC_TABLE_CNT, \n");  
			sbSql.append("                      ( \n");  
			sbSql.append("                        SELECT COUNT(*)  \n");  
			sbSql.append("                          FROM MM_ELEM_INFO I , MM_ELEM_INFO J \n");  
			sbSql.append("                         WHERE I.DEL_YN='N'  \n");  
			sbSql.append("                           AND J.ELEM_INFO_ID=AA.ELEM_INFO_ID  \n");  
			sbSql.append("                           AND I.ELEM_TP_ID='Attribute'  \n");  
			sbSql.append("                           AND I.LNK_INFO_ID=j.LNK_INFO_ID  \n");  
			sbSql.append("                           AND I.NAMESPACE like J.NAMESPACE || '.%'          \n");  
			sbSql.append("                      ) AS SRC_COLUMN_CNT \n");  
			sbSql.append("                  FROM \n");  
			sbSql.append("                  ( \n");  
			sbSql.append("                      SELECT B.ELEM_INFO_ID, FN_ELEMVARCOL(B.ELEM_INFO_ID, 'MODEL_TYPE') as MODEL_TYPE, B.NAMESPACE, B.DAT_STRC_NM, A.DAT_STRC_ID \n");  
			sbSql.append("                        FROM MM_DAT_STRC_ELEM_MAP A, MM_ELEM_INFO B, MM_LNK_INFO C \n");  
			sbSql.append("                       WHERE A.DAT_STRC_ID = '" + datStrcId + "' \n");  
			sbSql.append("                         AND A.ELEM_INFO_ID = B.ELEM_INFO_ID \n");  
			sbSql.append("                         AND B.LNK_INFO_ID = C.LNK_INFO_ID \n");  
			sbSql.append("                         AND B.ELEM_TP_ID = 'Model' \n");  
			sbSql.append("                         AND B.DEL_YN = 'N' \n");  
			sbSql.append("                         AND C.DEL_YN = 'N' \n");  
			sbSql.append("                         AND C.AUTO_COLLECT_YN = 'N' \n");  
			sbSql.append("                  ) AA    \n");  
			sbSql.append("                  WHERE MODEL_TYPE='P' \n");  
			sbSql.append("               ) X \n");  
			sbSql.append("               LEFT OUTER JOIN \n");  
			sbSql.append("               ( \n");  
			sbSql.append("                 SELECT  \n");  
			sbSql.append("                         LNK_INFO_NM AS TRG_SID,  \n");  
			sbSql.append("                         FILTER_STR  AS TRG_SCHEMA, \n");  
			sbSql.append("                         DAT_STRC_ID AS TRG_ID, \n");  
			sbSql.append("                         ( \n");  
			sbSql.append("                             SELECT COUNT(*)  \n");  
			sbSql.append("                               FROM MM_ELEM_INFO \n");  
			sbSql.append("                              WHERE LNK_INFO_ID = YY.LNK_INFO_ID \n");  
			sbSql.append("                                AND DEL_YN = 'N' \n");  
			sbSql.append("                                AND ELEM_TP_ID = 'Table' \n");  
			sbSql.append("                                AND NAMESPACE LIKE YY.FILTER_STR || '.%'                        \n");  
			sbSql.append("                         ) AS TRG_TABLE_CNT, \n");  
			sbSql.append("                         ( \n");  
			sbSql.append("                             SELECT COUNT(*)  \n");  
			sbSql.append("                               FROM MM_ELEM_INFO \n");  
			sbSql.append("                              WHERE LNK_INFO_ID = YY.LNK_INFO_ID \n");  
			sbSql.append("                                AND DEL_YN = 'N' \n");  
			sbSql.append("                                AND ELEM_TP_ID = 'Column' \n");  
			sbSql.append("                                AND NAMESPACE LIKE YY.FILTER_STR || '.%' \n");  
			sbSql.append("                         ) AS TRG_COLUMN_CNT \n");  
			sbSql.append("                   FROM ( \n");  
			sbSql.append("                         SELECT A.DAT_STRC_ID, A.DAT_STRC_NM, B.SYS_ID, E.LNK_INFO_ID, E.LNK_INFO_NM, E.FILTER_STR, E.URI \n");  
			sbSql.append("                           FROM MM_DAT_STRC A, MM_DAT_STRC_SYS B,  \n");  
			sbSql.append("                               ( \n");  
			sbSql.append("                                    SELECT C.LNK_INFO_ID, C.LNK_INFO_NM, C.URI, (REPLACE(D.FILTER_STR, '.*', '')) FILTER_STR, D.FILTER_SEQ, \n");  
			sbSql.append("                                           (C.LNK_INFO_NM || '.' || (REPLACE(D.FILTER_STR, '.*', ''))) SYS_ID \n");  
			sbSql.append("                                      FROM MM_LNK_INFO C, MM_LNK_INFO_FILTER D \n");  
			sbSql.append("                                    WHERE C.DEL_YN='N' \n");  
			sbSql.append("                                      AND C.LNK_INFO_ID = D.LNK_INFO_ID \n");  
			sbSql.append("                                    ORDER BY C.LNK_INFO_NM, D.ELEM_TP_ID, D.FILTER_SEQ \n");  
			sbSql.append("                               ) E \n");  
			sbSql.append("                         WHERE A.DAT_STRC_ID = '" + datStrcId + "' \n");  
			sbSql.append("                           AND A.DAT_STRC_ID = B.DAT_STRC_ID \n");  
			sbSql.append("                           AND B.LNK_PRDT_CD = 'PRDR_schema' \n");  
			sbSql.append("                           AND B.SYS_ID = E.SYS_ID \n");  
			sbSql.append("                        ) YY \n");  
			sbSql.append("               ) Y \n");  
			sbSql.append("               ON (X.SRC_ID = Y.TRG_ID)     \n");  
			sbSql.append("               ORDER BY X.SRC_STRC_NM, Y.TRG_SID, Y.TRG_SCHEMA    \n");  
			
			retDBSchemaCnt = qHelper.executeUpdate(sbSql.toString(), new Object[] {});

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return retDBSchemaCnt;
	}	
}
