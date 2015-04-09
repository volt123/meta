package com.lgdisplay.check;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import jspeed.base.jdbc.BaseSQLException;
import jspeed.base.jdbc.CacheResultSet;
import jspeed.base.jdbc.QueryHelper;

public class LGDModelAlignmentObservanceCheck {
	
	public int ObservanceCheck() throws BaseSQLException {
		boolean debugMode = false;
		
		QueryHelper qHelper = null;
		int retCnt = 0;
		
		CacheResultSet cRs = null;
		
		ArrayList datStrcNamespace = new ArrayList();
		ArrayList datStrcNm = new ArrayList();
		ArrayList datStrcId = new ArrayList();
		ArrayList isExistDBSchema = new ArrayList();
		
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
			
			System.out.println("### ModelAlignmentObservanceCheck : START");

			// 0. 전체검사할 구조명 목록 가져오기
			//    - 조건 : 'LGD-' 로 시작하는 구조목록가져오기 (전체)
			//             해당 구조목록에 연결(SYS_ID)되어 있는 DB Schema 정보도 모두 가져오기
			cRs = getDatStrcInfo(qHelper);
			
			int size = 0;
			while (cRs.next()) {
				datStrcNamespace.add((String)cRs.getObject("DAT_STRC_NAMESPACE"));
				datStrcNm.add((String)cRs.getObject("DAT_STRC_NM"));
				datStrcId.add((String)cRs.getObject("DAT_STRC_ID"));
				size++;
			}

			// 	 0. 결과 저장할 테이블 모두 지우기
			//       - LGD_MODEL_ALIGN_CHECK_TYPE01
			//       - LGD_MODEL_ALIGN_CHECK_TYPE02
			//       - LGD_MODEL_ALIGN_CHECK_TYPE03
			if (debugMode) {
				System.out.println ("------  wowDebug  --- Starting : deleteAlignmentTables ---------------");
			}
			
			deleteAlignmentTables(qHelper);
			
			if (debugMode) {
				System.out.println ("------  wowDebug  --- Ended : deleteAlignmentTables ---------------");
			}

			
			// 1. 구조명에 해당하는 DB Schema 정보 가져오기 (각 건수 포함)
			//    - LGD_MODEL_ALIGN_CHECK_TYPE01 에 저장
			if (debugMode) {
				System.out.println ("------  wowDebug  --- Starting : insertDBSchemaInfoConnectedByDatStrc ---------------");
			}
			
			for (int i = 0; i < size; i++) {
				retCnt += insertDBSchemaInfoConnectedByDatStrc
						(qHelper, workTime, (String)datStrcNamespace.get(i), (String)datStrcId.get(i));
			}
			
			for (int i = 0; i < size; i++) {
				int retDBSchemaCnt = 0;
				retDBSchemaCnt = countDBSchemaInfoConnectedByDatStrc
						(qHelper, workTime, (String)datStrcId.get(i));
				if(0 == retDBSchemaCnt) isExistDBSchema.add("N");
				else isExistDBSchema.add("Y");
			}

			if (debugMode) {
				System.out.println ("------  wowDebug  --- Ended : insertDBSchemaInfoConnectedByDatStrc ---------------");
			}
			
			if (debugMode) {
				System.out.println ("------  wowDebug  --------------------------------------");
				for (int i = 0; i < size; i++) {
					System.out.println(i + " : " + datStrcNamespace.get(i) + " : " + datStrcNm.get(i) + " : " + datStrcId.get(i) + " : " + isExistDBSchema.get(i));
				}
				System.out.println ("------  wowDebug  --------------------------------------");
			}

			// 2. 테이블 비교 
			//    - 각 구조에 해당하는 모델과 연결된 DB Schema 정보를 이용하여 테이블 비교하기
			//    - DB Schema 가 없는 모델은 제외
			//    - LGD_MODEL_ALIGN_CHECK_TYPE02  에 저장
			if (debugMode) {
				System.out.println ("------  wowDebug  --- Starting : insertTableAlignmentResult ---------------");
			}
			
			for (int i = 0; i < size; i++) {
				if ("Y".equals((String)isExistDBSchema.get(i))) {
					retCnt += insertTableAlignmentResult
							(qHelper, workTime, (String)datStrcNamespace.get(i), (String)datStrcNm.get(i), (String)datStrcId.get(i));
				}
			}

			if (debugMode) {
				System.out.println ("------  wowDebug  --- Ended : insertTableAlignmentResult ---------------");
			}

			// 3. 컬럼 비교 
			//    - 각 구조에 해당하는 모델과 연결된 DB Schema 정보를 이용하여 컬럼 비교하기
			//    - DB Schema 가 없는 모델은 제외
			//    - LGD_MODEL_ALIGN_CHECK_TYPE03  에 저장
			if (debugMode) {
				System.out.println ("------  wowDebug  --- Starting : insertColumnAlignmentResult ---------------");
			}
			
			for (int i = 0; i < size; i++) {
				if ("Y".equals((String)isExistDBSchema.get(i))) {
					retCnt += insertColumnAlignmentResult
							(qHelper, workTime, (String)datStrcNamespace.get(i), (String)datStrcNm.get(i), (String)datStrcId.get(i));
				}
			}

			if (debugMode) {
				System.out.println ("------  wowDebug  --- Ended : insertColumnAlignmentResult ---------------");
			}

			qHelper.commit();
			
			System.out.println("### ModelAlignmentObservanceCheck : END");
			
		} catch (BaseSQLException e) {
			e.printStackTrace();
			if (qHelper != null) try { qHelper.rollback(); } catch (Exception ignore) {}
			System.out.println("### ModelAlignmentObservanceCheck : BaseSQLException : "+e.getMessage());
			
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			if (qHelper != null) try { qHelper.rollback(); } catch (Exception ignore) {}
			System.out.println("### ModelAlignmentObservanceCheck : Exception : "+e.getMessage());
			
			throw new BaseSQLException(e.getMessage());
		} finally {
			if (qHelper != null) try { qHelper.close(); } catch (Exception ignore) {}
		}
		
		return retCnt;
	}
	
	/**
	 *	 0. 전체검사할 구조명 목록 가져오기
	 *	    - 조건 : 'LGD-' 로 시작하는 구조목록가져오기 (전체)
	 * 
	 * @param qHelper
	 * @throws Exception
	 */
	private CacheResultSet getDatStrcInfo(QueryHelper qHelper) throws Exception {

		CacheResultSet cRs = null;
		ResultSet rs = null;

		try {
			String sql =       " SELECT FN_GET_DAT_STRC_NAMESPACE(DAT_STRC_ID,'','>') AS DAT_STRC_NAMESPACE, \n"; 
			       sql = sql + "       DAT_STRC_NM, DAT_STRC_ID \n";
			       sql = sql + "  FROM MM_DAT_STRC \n"; 
			       sql = sql + " WHERE DAT_STRC_NM LIKE 'LGD-%' \n"; 
			       sql = sql + " ORDER BY DISP_ODR  \n"; 
			
			rs = qHelper.executeQuery(sql, new Object[] {});
			cRs = new CacheResultSet( rs );

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return cRs;
	}
	
	/**
	 *	 0. 결과 저장할 테이블 모두 지우기
	 *      - LGD_MODEL_ALIGN_CHECK_TYPE01
	 *      - LGD_MODEL_ALIGN_CHECK_TYPE02
	 *      - LGD_MODEL_ALIGN_CHECK_TYPE03
	 * 
	 * @param qHelper
	 * @throws Exception
	 */
	private void deleteAlignmentTables(QueryHelper qHelper) throws Exception {
		String sql = null;
		
		try {
			sql = "DELETE FROM LGD_MODEL_ALIGN_CHECK_TYPE01 \n";
			qHelper.executeUpdate(sql.toString(), new Object[] {});

			sql = "DELETE FROM LGD_MODEL_ALIGN_CHECK_TYPE02 \n";
			qHelper.executeUpdate(sql.toString(), new Object[] {});
		
			sql = "DELETE FROM LGD_MODEL_ALIGN_CHECK_TYPE03 \n";
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
	private int insertDBSchemaInfoConnectedByDatStrc(QueryHelper qHelper, String workTime, String datStrcNamespace, String datStrcId) throws Exception {

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

	/**
	 *	 1. 구조명에 해당하는 DB Schema 정보 가져오기 (각 건수 포함)
	 *	    - LGD_MODEL_ALIGN_CHECK_TYPE01 에 저장
	 * 
	 * @param qHelper, workTime, datStrcId
	 * @throws Exception
	 */
	private int countDBSchemaInfoConnectedByDatStrc(QueryHelper qHelper, String workTime, String datStrcId) throws Exception {

		ResultSet rs = null;
		int retDBSchemaCnt = 0;

		try {
			StringBuffer sbSql = new StringBuffer();
			
			sbSql.append(" SELECT COUNT(DB_SID_NM) AS CNT FROM LGD_MODEL_ALIGN_CHECK_TYPE01 \n");
			sbSql.append("  WHERE REG_DT = '" + workTime + "' \n");
			sbSql.append("    AND DAT_STRC_ID = '" + datStrcId + "' \n");
			
			rs = qHelper.executeQuery(sbSql.toString(), new Object[] {});
			if (rs.next())
				retDBSchemaCnt = rs.getInt("CNT");

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return retDBSchemaCnt;
	}

			/**
	 *	 2. 테이블 비교 
	 *	    - 각 구조에 해당하는 모델과 연결된 DB Schema 정보를 이용하여 테이블 비교하기
	 *	    - DB Schema 가 없는 모델은 제외
	 *	    - LGD_MODEL_ALIGN_CHECK_TYPE02  에 저장
	 * 
	 * @param qHelper, workTime, datStrcId
	 * @throws Exception
	 */
	private int insertTableAlignmentResult(QueryHelper qHelper, String workTime, String datStrcNamespace, String datStrcNm, String datStrcId) throws Exception {

		int retCnt = 0;

		try {
			// 비교 기준 : 모델
			StringBuffer sbSql = new StringBuffer();

			sbSql.append("INSERT INTO LGD_MODEL_ALIGN_CHECK_TYPE02 \n");  
			sbSql.append("        (REG_DT, DAT_STRC_ID, DAT_STRC_NAMESPACE, DAT_STRC_NM,  DAT_STRC_ENTITY_NM, DAT_STRC_TABLE_NM, \n");  
			sbSql.append("         DB_NAMESPACE, DB_SID_NM, DB_SCHEMA_NM, DB_TABLE_NM, DB_TABLE_DEF, ALIGN_STD_TYPE) \n");  
			sbSql.append("        SELECT '" + workTime + "' AS REG_DT, \n");  
			sbSql.append("               '" + datStrcId + "' AS DAT_STRC_ID, \n");  
			sbSql.append("               '" + datStrcNamespace + "' AS DAT_STRC_NAMESPACE,  \n");  
			sbSql.append("               '" + datStrcNm + "' AS DAT_STRC_NM, \n");  
			sbSql.append("               SRC_ENTITY_NM AS DAT_STRC_ENTITY_NM, \n");  
			sbSql.append("               SRC_TABLE_NM  AS DAT_STRC_TABLE_NM, \n");  
			sbSql.append("               TRG_NAMESPACE AS DB_NAMESPACE,  \n");  
			sbSql.append("               TRG_SID       AS DB_SID_NM,  \n");  
			sbSql.append("               TRG_SCHEMA    AS DB_SCHEMA_NM,  \n");  
			sbSql.append("               TRG_TABLE_NM  AS DB_TABLE_NM,  \n");  
			sbSql.append("               TRG_TABLE_DEF AS DB_TABLE_DEF,  \n");  
			sbSql.append("               'MODEL'       AS ALIGN_STD_TYPE \n");  
			sbSql.append("         FROM ( \n");  
			sbSql.append("               SELECT * FROM \n");  
			sbSql.append("                ( \n");  
			sbSql.append("                 SELECT  I.NAMESPACE    AS SRC_NAMESPACE \n");  
			sbSql.append("                        ,I.ELEM_PHSC_NM AS SRC_TABLE_NM \n");  
			sbSql.append("                        ,I.ELEM_LGCL_NM AS SRC_ENTITY_NM \n");  
			sbSql.append("                   FROM MM_ELEM_INFO I   , MM_ELEM_INFO J \n");  
			sbSql.append("                  WHERE J.ELEM_INFO_ID IN ( \n");  
			sbSql.append("                             SELECT ELEM_INFO_ID FROM  \n");  
			sbSql.append("                              ( \n");  
			sbSql.append("                              SELECT B.ELEM_INFO_ID, FN_ELEMVARCOL(B.ELEM_INFO_ID, 'MODEL_TYPE') as MODEL_TYPE \n");  
			sbSql.append("                                FROM MM_DAT_STRC_ELEM_MAP A,MM_ELEM_INFO B, MM_LNK_INFO C \n");  
			sbSql.append("                                WHERE A.DAT_STRC_ID = '" + datStrcId + "' \n");  
			sbSql.append("                                 AND A.ELEM_INFO_ID = B.ELEM_INFO_ID \n");  
			sbSql.append("                                 AND B.LNK_INFO_ID = C.LNK_INFO_ID \n");  
			sbSql.append("                                 AND B.ELEM_TP_ID = 'Model' \n");  
			sbSql.append("                                 AND B.DEL_YN = 'N' \n");  
			sbSql.append("                                 AND C.DEL_YN = 'N' \n");  
			sbSql.append("                                 AND C.AUTO_COLLECT_YN = 'N' \n");  
			sbSql.append("                                ) AA    \n");  
			sbSql.append("                                WHERE MODEL_TYPE='P' \n");  
			sbSql.append("                          ) \n");  
			sbSql.append("                    AND I.NAMESPACE LIKE J.NAMESPACE || '.%' \n");  
			sbSql.append("                    AND I.LNK_INFO_ID=J.LNK_INFO_ID \n");  
			sbSql.append("                    AND I.ELEM_TP_ID='Entity' \n");  
			sbSql.append("                    AND I.DEL_YN='N' \n");  
			sbSql.append("                ) A \n");  
			sbSql.append("                left outer join \n");  
			sbSql.append("                ( \n");  
			sbSql.append("                   SELECT D.NAMESPACE    AS TRG_NAMESPACE, \n");  
			sbSql.append("                          C.LNK_INFO_NM  AS TRG_SID,  \n");  
			sbSql.append("                          C.FILTER_STR   AS TRG_SCHEMA,  \n");  
			sbSql.append("                          D.ELEM_PHSC_NM AS TRG_TABLE_NM,  \n");  
			sbSql.append("                          D.ELEM_LGCL_NM AS TRG_TABLE_DEF \n");  
			sbSql.append("                    FROM ( \n");  
			sbSql.append("                            SELECT LNK_INFO_ID, LNK_INFO_NM, FILTER_STR FROM ( \n");  
			sbSql.append("                            SELECT A.DAT_STRC_ID, A.DAT_STRC_NM, B.SYS_ID, E.LNK_INFO_ID, E.LNK_INFO_NM, E.FILTER_STR, E.URI \n");  
			sbSql.append("                              FROM MM_DAT_STRC A, MM_DAT_STRC_SYS B,  \n");  
			sbSql.append("                                   ( \n");  
			sbSql.append("                                        SELECT C.LNK_INFO_ID, C.LNK_INFO_NM, C.URI, (REPLACE(D.FILTER_STR, '.*', '')) FILTER_STR, D.FILTER_SEQ, \n");  
			sbSql.append("                                               (C.LNK_INFO_NM || '.' || (REPLACE(D.FILTER_STR, '.*', ''))) SYS_ID \n");  
			sbSql.append("                                          FROM MM_LNK_INFO C, MM_LNK_INFO_FILTER D \n");  
			sbSql.append("                                        WHERE C.DEL_YN='N' \n");  
			sbSql.append("                                          AND C.LNK_INFO_ID = D.LNK_INFO_ID \n");  
			sbSql.append("                                        ORDER BY C.LNK_INFO_NM, D.ELEM_TP_ID, D.FILTER_SEQ \n");  
			sbSql.append("                                   ) E \n");  
			sbSql.append("                             WHERE A.DAT_STRC_ID = '" + datStrcId + "' \n");  
			sbSql.append("                               AND A.DAT_STRC_ID = B.DAT_STRC_ID \n");  
			sbSql.append("                               AND B.LNK_PRDT_CD = 'PRDR_schema' \n");  
			sbSql.append("                               AND B.SYS_ID = E.SYS_ID \n");  
			sbSql.append("                           )  \n");  
			sbSql.append("                    ) C, MM_ELEM_INFO D   \n");  
			sbSql.append("                    WHERE D.LNK_INFO_ID = C.LNK_INFO_ID \n");  
			sbSql.append("                      AND D.DEL_YN = 'N' \n");  
			sbSql.append("                      AND D.ELEM_TP_ID = 'Table' \n");  
			sbSql.append("                      AND D.NAMESPACE LIKE C.FILTER_STR || '.%' \n");  
			sbSql.append("             ) B \n");  
			sbSql.append("            on ( UPPER(SRC_TABLE_NM)=UPPER(TRG_TABLE_NM) ) \n");  
			sbSql.append("WHERE 1=1   \n");  
			sbSql.append("AND (SRC_NAMESPACE IS NULL OR TRG_NAMESPACE IS NULL  OR  FN_IS_SAME(SRC_TABLE_NM , TRG_TABLE_NM)=0 )                \n");  
			sbSql.append(")   \n");  

			retCnt += qHelper.executeUpdate(sbSql.toString(), new Object[] {});

			// 비교 기준 : DB Schema
			sbSql = new StringBuffer();
			
			sbSql.append("INSERT INTO LGD_MODEL_ALIGN_CHECK_TYPE02 \n");  
			sbSql.append("        (REG_DT, DAT_STRC_ID, DAT_STRC_NAMESPACE, DAT_STRC_NM,  DAT_STRC_ENTITY_NM, DAT_STRC_TABLE_NM, \n");  
			sbSql.append("         DB_NAMESPACE, DB_SID_NM, DB_SCHEMA_NM, DB_TABLE_NM, DB_TABLE_DEF, ALIGN_STD_TYPE) \n");  
			sbSql.append("        SELECT '" + workTime + "' AS REG_DT, \n");  
			sbSql.append("               '" + datStrcId + "' AS DAT_STRC_ID, \n");  
			sbSql.append("               '" + datStrcNamespace + "' AS DAT_STRC_NAMESPACE,  \n");  
			sbSql.append("               '" + datStrcNm + "' AS DAT_STRC_NM, \n");  
			sbSql.append("               TRG_ENTITY_NM AS DAT_STRC_ENTITY_NM, \n");  
			sbSql.append("               TRG_TABLE_NM  AS DAT_STRC_TABLE_NM, \n");  
			sbSql.append("               SRC_NAMESPACE AS DB_NAMESPACE,  \n");  
			sbSql.append("               SRC_SID       AS DB_SID_NM,  \n");  
			sbSql.append("               SRC_SCHEMA    AS DB_SCHEMA_NM,  \n");  
			sbSql.append("               SRC_TABLE_NM  AS DB_TABLE_NM,  \n");  
			sbSql.append("               SRC_TABLE_DEF AS DB_TABLE_DEF,  \n");  
			sbSql.append("               'SCHEMA'       AS ALIGN_STD_TYPE \n");  
			sbSql.append("         FROM ( \n");  
			sbSql.append("            select * from \n");  
			sbSql.append("                ( \n");  
			sbSql.append("                   SELECT D.NAMESPACE    AS SRC_NAMESPACE, \n");  
			sbSql.append("                          C.LNK_INFO_NM  AS SRC_SID,  \n");  
			sbSql.append("                          C.FILTER_STR   AS SRC_SCHEMA,  \n");  
			sbSql.append("                          D.ELEM_PHSC_NM AS SRC_TABLE_NM,  \n");  
			sbSql.append("                          D.ELEM_LGCL_NM AS SRC_TABLE_DEF \n");  
			sbSql.append("                    FROM ( \n");  
			sbSql.append("                            SELECT LNK_INFO_ID, LNK_INFO_NM, FILTER_STR FROM ( \n");  
			sbSql.append("                            SELECT A.DAT_STRC_ID, A.DAT_STRC_NM, B.SYS_ID, E.LNK_INFO_ID, E.LNK_INFO_NM, E.FILTER_STR, E.URI \n");  
			sbSql.append("                              FROM MM_DAT_STRC A, MM_DAT_STRC_SYS B,  \n");  
			sbSql.append("                                   ( \n");  
			sbSql.append("                                        SELECT C.LNK_INFO_ID, C.LNK_INFO_NM, C.URI, (REPLACE(D.FILTER_STR, '.*', '')) FILTER_STR, D.FILTER_SEQ, \n");  
			sbSql.append("                                               (C.LNK_INFO_NM || '.' || (REPLACE(D.FILTER_STR, '.*', ''))) SYS_ID \n");  
			sbSql.append("                                          FROM MM_LNK_INFO C, MM_LNK_INFO_FILTER D \n");  
			sbSql.append("                                        WHERE C.DEL_YN='N' \n");  
			sbSql.append("                                          AND C.LNK_INFO_ID = D.LNK_INFO_ID \n");  
			sbSql.append("                                        ORDER BY C.LNK_INFO_NM, D.ELEM_TP_ID, D.FILTER_SEQ \n");  
			sbSql.append("                                   ) E \n");  
			sbSql.append("                             WHERE A.DAT_STRC_ID = '" + datStrcId + "' \n");  
			sbSql.append("                               AND A.DAT_STRC_ID = B.DAT_STRC_ID \n");  
			sbSql.append("                               AND B.LNK_PRDT_CD = 'PRDR_schema' \n");  
			sbSql.append("                               AND B.SYS_ID = E.SYS_ID \n");  
			sbSql.append("                           )  \n");  
			sbSql.append("                    ) C, MM_ELEM_INFO D   \n");  
			sbSql.append("                    WHERE D.LNK_INFO_ID = C.LNK_INFO_ID \n");  
			sbSql.append("                      AND D.DEL_YN = 'N' \n");  
			sbSql.append("                      AND D.ELEM_TP_ID = 'Table' \n");  
			sbSql.append("                      AND D.NAMESPACE LIKE C.FILTER_STR || '.%' \n");  
			sbSql.append("                ) A \n");  
			sbSql.append("                left outer join \n");  
			sbSql.append("                ( \n");  
			sbSql.append("                 SELECT  I.NAMESPACE    AS TRG_NAMESPACE \n");  
			sbSql.append("                        ,I.ELEM_PHSC_NM AS TRG_TABLE_NM \n");  
			sbSql.append("                        ,I.ELEM_LGCL_NM AS TRG_ENTITY_NM \n");  
			sbSql.append("                   FROM MM_ELEM_INFO I   , MM_ELEM_INFO J \n");  
			sbSql.append("                  WHERE J.ELEM_INFO_ID IN ( \n");  
			sbSql.append("                             SELECT ELEM_INFO_ID FROM  \n");  
			sbSql.append("                              ( \n");  
			sbSql.append("                              SELECT B.ELEM_INFO_ID, FN_ELEMVARCOL(B.ELEM_INFO_ID, 'MODEL_TYPE') as MODEL_TYPE, B.NAMESPACE, B.LNK_INFO_ID \n");  
			sbSql.append("                                FROM MM_DAT_STRC_ELEM_MAP A,MM_ELEM_INFO B, MM_LNK_INFO C \n");  
			sbSql.append("                                WHERE A.DAT_STRC_ID = '" + datStrcId + "' \n");  
			sbSql.append("                                 AND A.ELEM_INFO_ID = B.ELEM_INFO_ID \n");  
			sbSql.append("                                 AND B.LNK_INFO_ID = C.LNK_INFO_ID \n");  
			sbSql.append("                                 AND B.ELEM_TP_ID = 'Model' \n");  
			sbSql.append("                                 AND B.DEL_YN = 'N' \n");  
			sbSql.append("                                 AND C.DEL_YN = 'N' \n");  
			sbSql.append("                                 AND C.AUTO_COLLECT_YN = 'N' \n");  
			sbSql.append("                                ) AA    \n");  
			sbSql.append("                                WHERE MODEL_TYPE='P' \n");  
			sbSql.append("                          ) \n");  
			sbSql.append("                    AND I.NAMESPACE LIKE J.NAMESPACE || '.%' \n");  
			sbSql.append("                    AND I.LNK_INFO_ID=J.LNK_INFO_ID \n");  
			sbSql.append("                    AND I.ELEM_TP_ID='Entity' \n");  
			sbSql.append("                    AND I.DEL_YN='N' \n");  
			sbSql.append("             ) B \n");  
			sbSql.append("            on ( UPPER(SRC_TABLE_NM) = UPPER(TRG_TABLE_NM) ) \n");  
			sbSql.append("            WHERE 1=1   \n");  
			sbSql.append("            AND (SRC_NAMESPACE IS NULL OR TRG_NAMESPACE IS NULL  OR  FN_IS_SAME(SRC_TABLE_NM , TRG_TABLE_NM)=0 ) \n");  
			sbSql.append("            ORDER BY SRC_SID, SRC_SCHEMA, SRC_TABLE_NM  \n");  
			sbSql.append("       ) \n");  			
			
			retCnt += qHelper.executeUpdate(sbSql.toString(), new Object[] {});

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return retCnt;
	}
	
	/**
	 *	 3. 테이블 비교 
	 *	    - 각 구조에 해당하는 모델과 연결된 DB Schema 정보를 이용하여 컬럼 비교하기
	 *	    - DB Schema 가 없는 모델은 제외
	 *	    - LGD_MODEL_ALIGN_CHECK_TYPE03  에 저장
	 * 
	 * @param qHelper, workTime, datStrcId
	 * @throws Exception
	 */
	private int insertColumnAlignmentResult(QueryHelper qHelper, String workTime, String datStrcNamespace, String datStrcNm, String datStrcId) throws Exception {

		int retCnt = 0;

		try {
			// 비교 기준 : 모델
			StringBuffer sbSql = new StringBuffer();

			sbSql.append("INSERT INTO LGD_MODEL_ALIGN_CHECK_TYPE03 \n");  
			sbSql.append("        (REG_DT, DAT_STRC_ID, DAT_STRC_NAMESPACE, DAT_STRC_NM,  DAT_STRC_ENTITY_NM, DAT_STRC_TABLE_NM, \n");  
			sbSql.append("         DAT_STRC_ATTRIBUTE_NM, DAT_STRC_COLUMN_NM, DAT_STRC_ORDINAL_POSITION, DAT_STRC_DATATYPE_NM, DAT_STRC_COLUMN_SIZE, DAT_STRC_COLUMN_PRECISION, \n");  
			sbSql.append("         DB_NAMESPACE, DB_SID_NM, DB_SCHEMA_NM, DB_TABLE_NM,  \n");  
			sbSql.append("         DB_COLUMN_NM, DB_COLUMN_DEF, DB_ORDINAL_POSITION, DB_DATATYPE_NM, DB_COLUMN_SIZE,  \n");  
			sbSql.append("         DB_COLUMN_PRECISION, ALIGN_STD_TYPE) \n");  
			sbSql.append("        SELECT '" + workTime + "' AS REG_DT, \n");  
			sbSql.append("               '" + datStrcId + "' AS DAT_STRC_ID, \n");  
			sbSql.append("               '" + datStrcNamespace + "' AS DAT_STRC_NAMESPACE,  \n");  
			sbSql.append("               '" + datStrcNm + "' AS DAT_STRC_NM, \n");  
			sbSql.append("               SRC_ENTITY_NM         AS DAT_STRC_ENTITY_NM, \n");  
			sbSql.append("               SRC_TABLE_NM          AS DAT_STRC_TABLE_NM, \n");  
			sbSql.append("               SRC_ATTRIBUTE_NM      AS DAT_STRC_ATTRIBUTE_NM, \n");  
			sbSql.append("               SRC_COLUMN_NM         AS DAT_STRC_COLUMN_NM, \n");  
			sbSql.append("               SRC_ORDINAL_POSITION  AS DAT_STRC_ORDINAL_POSITION, \n");  
			sbSql.append("               SRC_DATATYPE_NM       AS DAT_STRC_DATATYPE_NM, \n");  
			sbSql.append("               SRC_COLUMN_SIZE       AS DAT_STRC_COLUMN_SIZE, \n");  
			sbSql.append("               SRC_PRECISION         AS DAT_STRC_COLUMN_PRECISION, \n");  
			sbSql.append("               TRG_NAMESPACE         AS DB_NAMESPACE,  \n");  
			sbSql.append("               TRG_SID               AS DB_SID_NM,  \n");  
			sbSql.append("               TRG_SCHEMA            AS DB_SCHEMA_NM,  \n");  
			sbSql.append("               TRG_TABLE_NM          AS DB_TABLE_NM,  \n");  
			sbSql.append("               TRG_COLUMN_NM         AS DB_COLUMN_NM, \n");  
			sbSql.append("               TRG_COLUMN_DEF        AS DB_COLUMN_DEF, \n");  
			sbSql.append("               TRG_ORDINAL_POSITION  AS DB_ORDINAL_POSITION, \n");  
			sbSql.append("               TRG_DATATYPE_NM       AS DB_DATATYPE_NM, \n");  
			sbSql.append("               TRG_COLUMN_SIZE       AS DB_COLUMN_SIZE, \n");  
			sbSql.append("               TRG_PRECISION         AS DB_COLUMN_PRECISION, \n");  
			sbSql.append("               'MODEL'               AS ALIGN_STD_TYPE \n");  
			sbSql.append("         FROM ( \n");  
			sbSql.append("               SELECT * FROM \n");  
			sbSql.append("                    ( \n");  
			sbSql.append("                      SELECT I.NAMESPACE    as SRC_NAMESPACE,  \n");  
			sbSql.append("                             C.ELEM_LGCL_NM as SRC_ENTITY_NM,  \n");  
			sbSql.append("                             C.ELEM_PHSC_NM as SRC_TABLE_NM, \n");  
			sbSql.append("                             I.ELEM_LGCL_NM AS SRC_ATTRIBUTE_NM ,  \n");  
			sbSql.append("                             I.ELEM_PHSC_NM as SRC_COLUMN_NM,  \n");  
			sbSql.append("                             FN_TO_NUMBER(FN_ELEMVARCOL(I.ELEM_INFO_ID,'ORDINAL_POSITION')) as SRC_ORDINAL_POSITION,  \n");  
			sbSql.append("                             FN_ELEMVARCOL(I.ELEM_INFO_ID,'TYPE_NAME') as SRC_DATATYPE_NM,  \n");  
			sbSql.append("                             FN_TO_NUMBER(FN_ELEMVARCOL(I.ELEM_INFO_ID,'COLUMN_SIZE')) as SRC_COLUMN_SIZE,  \n");  
			sbSql.append("                             FN_TO_NUMBER(FN_ELEMVARCOL(I.ELEM_INFO_ID,'DECIMAL_DIGITS')) as SRC_PRECISION \n");  
			sbSql.append("                     FROM MM_ELEM_INFO I, \n");  
			sbSql.append("                            ( \n");  
			sbSql.append("                                 SELECT  I.LNK_INFO_ID  \n");  
			sbSql.append("                                       , I.ELEM_PHSC_NM \n");  
			sbSql.append("                                       , I.ELEM_LGCL_NM \n");  
			sbSql.append("                                       , I.NAMESPACE    AS SRC_NAMESPACE \n");  
			sbSql.append("                                   FROM MM_ELEM_INFO I  , MM_ELEM_INFO J \n");  
			sbSql.append("                                  WHERE J.ELEM_INFO_ID IN ( \n");  
			sbSql.append("                                             SELECT ELEM_INFO_ID FROM  \n");  
			sbSql.append("                                              ( \n");  
			sbSql.append("                                              SELECT B.ELEM_INFO_ID, FN_ELEMVARCOL(B.ELEM_INFO_ID, 'MODEL_TYPE') as MODEL_TYPE, B.NAMESPACE, B.LNK_INFO_ID \n");  
			sbSql.append("                                                FROM MM_DAT_STRC_ELEM_MAP A,MM_ELEM_INFO B, MM_LNK_INFO C \n");  
			sbSql.append("                                                WHERE A.DAT_STRC_ID = '" + datStrcId + "' \n");  
			sbSql.append("                                                 AND A.ELEM_INFO_ID = B.ELEM_INFO_ID \n");  
			sbSql.append("                                                 AND B.LNK_INFO_ID = C.LNK_INFO_ID \n");  
			sbSql.append("                                                 AND B.ELEM_TP_ID = 'Model' \n");  
			sbSql.append("                                                 AND B.DEL_YN = 'N' \n");  
			sbSql.append("                                                 AND C.DEL_YN = 'N' \n");  
			sbSql.append("                                                 AND C.AUTO_COLLECT_YN = 'N' \n");  
			sbSql.append("                                                ) AA    \n");  
			sbSql.append("                                                WHERE MODEL_TYPE='P' \n");  
			sbSql.append("                                          ) \n");  
			sbSql.append("                                   AND I.NAMESPACE LIKE J.NAMESPACE || '.%' \n");  
			sbSql.append("                                   AND I.LNK_INFO_ID=J.LNK_INFO_ID \n");  
			sbSql.append("                                   AND I.ELEM_TP_ID='Entity' \n");  
			sbSql.append("                                   AND I.DEL_YN='N' \n");  
			sbSql.append("                            ) C \n");  
			sbSql.append("                            WHERE I.DEL_YN='N' AND I.ELEM_TP_ID='Attribute' AND I.NAMESPACE LIKE C.SRC_NAMESPACE || '.%' \n");  
			sbSql.append("                            AND  I.LNK_INFO_ID=C.LNK_INFO_ID \n");  
			sbSql.append("                    ) A1 \n");  
			sbSql.append("                    LEFT outer join  \n");  
			sbSql.append("                    ( \n");  
			sbSql.append("                          SELECT D.NAMESPACE    AS TRG_NAMESPACE,    \n");  
			sbSql.append("                                 C.LNK_INFO_NM  AS TRG_SID,  \n");  
			sbSql.append("                                 C.FILTER_STR   AS TRG_SCHEMA,  \n");  
			sbSql.append("                                 FN_ELEMVARCOL(D.ELEM_INFO_ID,'TABLE_NAME') as TRG_TABLE_NM,  \n");  
			sbSql.append("                                 D.ELEM_PHSC_NM AS TRG_COLUMN_NM,  \n");  
			sbSql.append("                                 D.ELEM_LGCL_NM AS TRG_COLUMN_DEF,  \n");  
			sbSql.append("                                 FN_TO_NUMBER(FN_ELEMVARCOL(D.ELEM_INFO_ID,'ORDINAL_POSITION')) as TRG_ORDINAL_POSITION,  \n");  
			sbSql.append("                                 FN_ELEMVARCOL(D.ELEM_INFO_ID,'TYPE_NAME') as TRG_DATATYPE_NM,  \n");  
			sbSql.append("                                 FN_TO_NUMBER(FN_ELEMVARCOL(D.ELEM_INFO_ID,'COLUMN_SIZE')) as TRG_COLUMN_SIZE,  \n");  
			sbSql.append("                                 FN_TO_NUMBER(FN_ELEMVARCOL(D.ELEM_INFO_ID,'DECIMAL_DIGITS')) as TRG_PRECISION \n");  
			sbSql.append("                            FROM ( \n");  
			sbSql.append("                                    SELECT LNK_INFO_ID, LNK_INFO_NM, FILTER_STR FROM ( \n");  
			sbSql.append("                                    SELECT A.DAT_STRC_ID, A.DAT_STRC_NM, B.SYS_ID, E.LNK_INFO_ID, E.LNK_INFO_NM, E.FILTER_STR, E.URI \n");  
			sbSql.append("                                      FROM MM_DAT_STRC A, MM_DAT_STRC_SYS B,  \n");  
			sbSql.append("                                           ( \n");  
			sbSql.append("                                                SELECT C.LNK_INFO_ID, C.LNK_INFO_NM, C.URI, (REPLACE(D.FILTER_STR, '.*', '')) FILTER_STR, D.FILTER_SEQ, \n");  
			sbSql.append("                                                       (C.LNK_INFO_NM || '.' || (REPLACE(D.FILTER_STR, '.*', ''))) SYS_ID \n");  
			sbSql.append("                                                  FROM MM_LNK_INFO C, MM_LNK_INFO_FILTER D \n");  
			sbSql.append("                                                WHERE C.DEL_YN='N' \n");  
			sbSql.append("                                                  AND C.LNK_INFO_ID = D.LNK_INFO_ID \n");  
			sbSql.append("                                                ORDER BY C.LNK_INFO_NM, D.ELEM_TP_ID, D.FILTER_SEQ \n");  
			sbSql.append("                                           ) E \n");  
			sbSql.append("                                     WHERE A.DAT_STRC_ID = '" + datStrcId + "' \n");  
			sbSql.append("                                       AND A.DAT_STRC_ID = B.DAT_STRC_ID \n");  
			sbSql.append("                                       AND B.LNK_PRDT_CD = 'PRDR_schema' \n");  
			sbSql.append("                                       AND B.SYS_ID = E.SYS_ID \n");  
			sbSql.append("                                   )  \n");  
			sbSql.append("                            ) C, MM_ELEM_INFO D  \n");  
			sbSql.append("                            WHERE D.LNK_INFO_ID = C.LNK_INFO_ID \n");  
			sbSql.append("                              AND D.DEL_YN = 'N' \n");  
			sbSql.append("                              AND D.ELEM_TP_ID = 'Column' \n");  
			sbSql.append("                              AND D.NAMESPACE LIKE C.FILTER_STR || '.%' \n");  
			sbSql.append("                    ) B1 \n");  
			sbSql.append("                    on (SRC_TABLE_NM=TRG_TABLE_NM AND SRC_COLUMN_NM=TRG_COLUMN_NM) \n");  
			sbSql.append("                    WHERE 1=1   \n");  
			sbSql.append("                      AND (SRC_NAMESPACE IS NULL OR TRG_NAMESPACE IS NULL      OR FN_IS_SAME(SRC_TABLE_NM , TRG_TABLE_NM)=0                 OR   \n");  
			sbSql.append("                           FN_IS_SAME(SRC_COLUMN_NM , TRG_COLUMN_NM)=0         OR FN_IS_SAME(SRC_ORDINAL_POSITION , TRG_ORDINAL_POSITION)=0 OR  \n");  
			sbSql.append("                           FN_IS_SAME(SRC_DATATYPE_NM , TRG_DATATYPE_NM)=0     OR FN_IS_SAME(SRC_COLUMN_SIZE , TRG_COLUMN_SIZE)=0           OR   \n");  
			sbSql.append("                           FN_IS_SAME(SRC_PRECISION , TRG_PRECISION)=0 ) \n");  
			sbSql.append("                    ORDER BY SRC_TABLE_NM, SRC_ORDINAL_POSITION  \n");  
			sbSql.append("            ) \n");  

			retCnt += qHelper.executeUpdate(sbSql.toString(), new Object[] {});

			// 비교 기준 : DB Schema
			sbSql = new StringBuffer();
			
			sbSql.append("INSERT INTO LGD_MODEL_ALIGN_CHECK_TYPE03 \n");  
			sbSql.append("        (REG_DT, DAT_STRC_ID, DAT_STRC_NAMESPACE, DAT_STRC_NM,  DAT_STRC_ENTITY_NM, DAT_STRC_TABLE_NM, \n");  
			sbSql.append("         DAT_STRC_ATTRIBUTE_NM, DAT_STRC_COLUMN_NM, DAT_STRC_ORDINAL_POSITION, DAT_STRC_DATATYPE_NM, DAT_STRC_COLUMN_SIZE, DAT_STRC_COLUMN_PRECISION, \n");  
			sbSql.append("         DB_NAMESPACE, DB_SID_NM, DB_SCHEMA_NM, DB_TABLE_NM,  \n");  
			sbSql.append("         DB_COLUMN_NM, DB_COLUMN_DEF, DB_ORDINAL_POSITION, DB_DATATYPE_NM, DB_COLUMN_SIZE,  \n");  
			sbSql.append("         DB_COLUMN_PRECISION, ALIGN_STD_TYPE) \n");  
			sbSql.append("        SELECT '" + workTime + "' AS REG_DT, \n");  
			sbSql.append("               '" + datStrcId + "' AS DAT_STRC_ID, \n");  
			sbSql.append("               '" + datStrcNamespace + "' AS DAT_STRC_NAMESPACE,  \n");  
			sbSql.append("               '" + datStrcNm + "' AS DAT_STRC_NM, \n");  
			sbSql.append("               TRG_ENTITY_NM         AS DAT_STRC_ENTITY_NM, \n");  
			sbSql.append("               TRG_TABLE_NM          AS DAT_STRC_TABLE_NM, \n");  
			sbSql.append("               TRG_ATTRIBUTE_NM      AS DAT_STRC_ATTRIBUTE_NM, \n");  
			sbSql.append("               TRG_COLUMN_NM         AS DAT_STRC_COLUMN_NM, \n");  
			sbSql.append("               TRG_ORDINAL_POSITION  AS DAT_STRC_ORDINAL_POSITION, \n");  
			sbSql.append("               TRG_DATATYPE_NM       AS DAT_STRC_DATATYPE_NM, \n");  
			sbSql.append("               TRG_COLUMN_SIZE       AS DAT_STRC_COLUMN_SIZE, \n");  
			sbSql.append("               TRG_PRECISION         AS DAT_STRC_COLUMN_PRECISION, \n");  
			sbSql.append("               SRC_NAMESPACE         AS DB_NAMESPACE,  \n");  
			sbSql.append("               SRC_SID               AS DB_SID_NM,  \n");  
			sbSql.append("               SRC_SCHEMA            AS DB_SCHEMA_NM,  \n");  
			sbSql.append("               SRC_TABLE_NM          AS DB_TABLE_NM,  \n");  
			sbSql.append("               SRC_COLUMN_NM         AS DB_COLUMN_NM, \n");  
			sbSql.append("               SRC_COLUMN_DEF        AS DB_COLUMN_DEF, \n");  
			sbSql.append("               SRC_ORDINAL_POSITION  AS DB_ORDINAL_POSITION, \n");  
			sbSql.append("               SRC_DATATYPE_NM       AS DB_DATATYPE_NM, \n");  
			sbSql.append("               SRC_COLUMN_SIZE       AS DB_COLUMN_SIZE, \n");  
			sbSql.append("               SRC_PRECISION         AS DB_COLUMN_PRECISION, \n");  
			sbSql.append("               'SCHEMA'              AS ALIGN_STD_TYPE \n");  
			sbSql.append("         FROM ( \n");  
			sbSql.append("            SELECT * FROM \n");  
			sbSql.append("            ( \n");  
			sbSql.append("                  SELECT D.NAMESPACE    AS SRC_NAMESPACE,    \n");  
			sbSql.append("                         C.LNK_INFO_NM  AS SRC_SID,  \n");  
			sbSql.append("                         C.FILTER_STR   AS SRC_SCHEMA,  \n");  
			sbSql.append("                         FN_ELEMVARCOL(D.ELEM_INFO_ID,'TABLE_NAME') as SRC_TABLE_NM,  \n");  
			sbSql.append("                         D.ELEM_PHSC_NM AS SRC_COLUMN_NM,  \n");  
			sbSql.append("                         D.ELEM_LGCL_NM AS SRC_COLUMN_DEF,  \n");  
			sbSql.append("                         FN_TO_NUMBER(FN_ELEMVARCOL(D.ELEM_INFO_ID,'ORDINAL_POSITION')) as SRC_ORDINAL_POSITION,  \n");  
			sbSql.append("                         FN_ELEMVARCOL(D.ELEM_INFO_ID,'TYPE_NAME') as SRC_DATATYPE_NM,  \n");  
			sbSql.append("                         FN_TO_NUMBER(FN_ELEMVARCOL(D.ELEM_INFO_ID,'COLUMN_SIZE')) as SRC_COLUMN_SIZE,  \n");  
			sbSql.append("                         FN_TO_NUMBER(FN_ELEMVARCOL(D.ELEM_INFO_ID,'DECIMAL_DIGITS')) as SRC_PRECISION \n");  
			sbSql.append("                    FROM ( \n");  
			sbSql.append("                            SELECT LNK_INFO_ID, LNK_INFO_NM, FILTER_STR FROM ( \n");  
			sbSql.append("                            SELECT A.DAT_STRC_ID, A.DAT_STRC_NM, B.SYS_ID, E.LNK_INFO_ID, E.LNK_INFO_NM, E.FILTER_STR, E.URI \n");  
			sbSql.append("                              FROM MM_DAT_STRC A, MM_DAT_STRC_SYS B,  \n");  
			sbSql.append("                                   ( \n");  
			sbSql.append("                                        SELECT C.LNK_INFO_ID, C.LNK_INFO_NM, C.URI, (REPLACE(D.FILTER_STR, '.*', '')) FILTER_STR, D.FILTER_SEQ, \n");  
			sbSql.append("                                               (C.LNK_INFO_NM || '.' || (REPLACE(D.FILTER_STR, '.*', ''))) SYS_ID \n");  
			sbSql.append("                                          FROM MM_LNK_INFO C, MM_LNK_INFO_FILTER D \n");  
			sbSql.append("                                        WHERE C.DEL_YN='N' \n");  
			sbSql.append("                                          AND C.LNK_INFO_ID = D.LNK_INFO_ID \n");  
			sbSql.append("                                        ORDER BY C.LNK_INFO_NM, D.ELEM_TP_ID, D.FILTER_SEQ \n");  
			sbSql.append("                                   ) E \n");  
			sbSql.append("                             WHERE A.DAT_STRC_ID = '" + datStrcId + "' \n");  
			sbSql.append("                               AND A.DAT_STRC_ID = B.DAT_STRC_ID \n");  
			sbSql.append("                               AND B.LNK_PRDT_CD = 'PRDR_schema' \n");  
			sbSql.append("                               AND B.SYS_ID = E.SYS_ID \n");  
			sbSql.append("                           )  \n");  
			sbSql.append("                    ) C, MM_ELEM_INFO D  \n");  
			sbSql.append("                    WHERE D.LNK_INFO_ID = C.LNK_INFO_ID \n");  
			sbSql.append("                      AND D.DEL_YN = 'N' \n");  
			sbSql.append("                      AND D.ELEM_TP_ID = 'Column' \n");  
			sbSql.append("                      AND D.NAMESPACE LIKE C.FILTER_STR || '.%' \n");  
			sbSql.append("            ) A1 \n");  
			sbSql.append("            LEFT outer join  \n");  
			sbSql.append("            ( \n");  
			sbSql.append("              SELECT I.NAMESPACE    as TRG_NAMESPACE,  \n");  
			sbSql.append("                     C.ELEM_LGCL_NM as TRG_ENTITY_NM,  \n");  
			sbSql.append("                     C.ELEM_PHSC_NM as TRG_TABLE_NM, \n");  
			sbSql.append("                     I.ELEM_LGCL_NM AS TRG_ATTRIBUTE_NM ,  \n");  
			sbSql.append("                     I.ELEM_PHSC_NM as TRG_COLUMN_NM,  \n");  
			sbSql.append("                     FN_TO_NUMBER(FN_ELEMVARCOL(I.ELEM_INFO_ID,'ORDINAL_POSITION')) as TRG_ORDINAL_POSITION,  \n");  
			sbSql.append("                     FN_ELEMVARCOL(I.ELEM_INFO_ID,'TYPE_NAME') as TRG_DATATYPE_NM,  \n");  
			sbSql.append("                     FN_TO_NUMBER(FN_ELEMVARCOL(I.ELEM_INFO_ID,'COLUMN_SIZE')) as TRG_COLUMN_SIZE,  \n");  
			sbSql.append("                     FN_TO_NUMBER(FN_ELEMVARCOL(I.ELEM_INFO_ID,'DECIMAL_DIGITS')) as TRG_PRECISION \n");  
			sbSql.append("             FROM MM_ELEM_INFO I, \n");  
			sbSql.append("                    ( \n");  
			sbSql.append("                         SELECT  I.LNK_INFO_ID  \n");  
			sbSql.append("                               , I.ELEM_PHSC_NM \n");  
			sbSql.append("                               , I.ELEM_LGCL_NM \n");  
			sbSql.append("                               , I.NAMESPACE    AS SRC_NAMESPACE \n");  
			sbSql.append("                           FROM MM_ELEM_INFO I  , MM_ELEM_INFO J \n");  
			sbSql.append("                          WHERE J.ELEM_INFO_ID IN ( \n");  
			sbSql.append("                                     SELECT ELEM_INFO_ID FROM  \n");  
			sbSql.append("                                      ( \n");  
			sbSql.append("                                      SELECT B.ELEM_INFO_ID, FN_ELEMVARCOL(B.ELEM_INFO_ID, 'MODEL_TYPE') as MODEL_TYPE, B.NAMESPACE, B.LNK_INFO_ID \n");  
			sbSql.append("                                        FROM MM_DAT_STRC_ELEM_MAP A,MM_ELEM_INFO B, MM_LNK_INFO C \n");  
			sbSql.append("                                        WHERE A.DAT_STRC_ID = '" + datStrcId + "' \n");  
			sbSql.append("                                         AND A.ELEM_INFO_ID = B.ELEM_INFO_ID \n");  
			sbSql.append("                                         AND B.LNK_INFO_ID = C.LNK_INFO_ID \n");  
			sbSql.append("                                         AND B.ELEM_TP_ID = 'Model' \n");  
			sbSql.append("                                         AND B.DEL_YN = 'N' \n");  
			sbSql.append("                                         AND C.DEL_YN = 'N' \n");  
			sbSql.append("                                         AND C.AUTO_COLLECT_YN = 'N' \n");  
			sbSql.append("                                        ) AA    \n");  
			sbSql.append("                                        WHERE MODEL_TYPE='P' \n");  
			sbSql.append("                                  ) \n");  
			sbSql.append("                           AND I.NAMESPACE LIKE J.NAMESPACE || '.%' \n");  
			sbSql.append("                           AND I.LNK_INFO_ID=J.LNK_INFO_ID \n");  
			sbSql.append("                           AND I.ELEM_TP_ID='Entity' \n");  
			sbSql.append("                           AND I.DEL_YN='N' \n");  
			sbSql.append("                    ) C \n");  
			sbSql.append("                    WHERE I.DEL_YN='N' AND I.ELEM_TP_ID='Attribute' AND I.NAMESPACE LIKE C.SRC_NAMESPACE || '.%' \n");  
			sbSql.append("                    AND  I.LNK_INFO_ID=C.LNK_INFO_ID \n");  
			sbSql.append("            ) B1 \n");  
			sbSql.append("            on (TRG_TABLE_NM=SRC_TABLE_NM AND SRC_COLUMN_NM=TRG_COLUMN_NM) \n");  
			sbSql.append("            WHERE 1=1   \n");  
			sbSql.append("              AND (SRC_NAMESPACE IS NULL OR TRG_NAMESPACE IS NULL      OR FN_IS_SAME(TRG_TABLE_NM , SRC_TABLE_NM)=0                 OR   \n");  
			sbSql.append("                   FN_IS_SAME(TRG_COLUMN_NM , SRC_COLUMN_NM)=0         OR FN_IS_SAME(SRC_ORDINAL_POSITION , TRG_ORDINAL_POSITION)=0 OR  \n");  
			sbSql.append("                   FN_IS_SAME(SRC_DATATYPE_NM , TRG_DATATYPE_NM)=0     OR FN_IS_SAME(SRC_COLUMN_SIZE , TRG_COLUMN_SIZE)=0           OR   \n");  
			sbSql.append("                   FN_IS_SAME(SRC_PRECISION , TRG_PRECISION)=0 ) \n");  
			sbSql.append("            ORDER BY SRC_SID, SRC_SCHEMA, SRC_TABLE_NM, SRC_ORDINAL_POSITION     \n");  
			sbSql.append(") \n");  			
			
			retCnt += qHelper.executeUpdate(sbSql.toString(), new Object[] {});

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return retCnt;
	}
	
}
