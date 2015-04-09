package com.lgdisplay.db;

import java.sql.ResultSet;
import java.util.*;

import jspeed.base.jdbc.*;
import jspeed.base.query.DBAssistant;
 
import com.itplus.mm.dao.CommonDAO;

public class LgdDao extends CommonDAO
{

	public LgdDao() throws Exception
	{
		super();
	}
	public LgdDao(DBAssistant dba ) throws Exception
	{
		super(dba);
	}
	
	/**
	 * @param param 
	 * @return
	 * @throws Exception
	 */
	public CacheResultSet findAllByStandardAgreeUser(HashMap param) throws  Exception
	{
		String userId = (String)param.get("SESS_USER_ID");
	
		String sql = " SELECT DISTINCT C.USER_ID \n";
		sql = sql +	 " 	, C.USER_NM \n";
		sql = sql +	 " 	, GET_DEP_NAME( C.DEP_ID ) AS DEPT_TITLE \n";
		sql = sql +	 " 	, GET_DEP_NAME( C.DEP_ID ) AS TEAM_TITLE \n";
		sql = sql +	 " 	, D.ROLE_NM \n";
		sql = sql +	 " 	, C.CELL_PHONE \n";
		sql = sql +	 " 	, 'N' AS ESSEN_YN \n";
		sql = sql +	 " 	, C.EMAIL \n";
		sql = sql +	 " 	, C.USER_IP \n";
		sql = sql +	 "  , 'Y' IS_DBA \n";
		sql = sql +	 " FROM C_USER_GROUP_ROLE A \n";
		sql = sql +	 " 	JOIN C_USER_GROUP_ROLE B ON (B.GROUP_ID = A.GROUP_ID AND B.ROLE_ID IN (109) ) \n"; // 원하는 협의자 ROLE_ID : 모델PI담당자 = 109 
		sql = sql +	 " 	JOIN C_USER C ON (C.USER_ID = B.USER_ID) \n";
		sql = sql +	 " 	JOIN C_ROLE D ON (D.ROLE_ID = B.ROLE_ID) \n";
		sql = sql +	 " WHERE A.USER_ID = " + userId + " \n";
		sql = sql +	 "   AND B.GROUP_ID NOT IN (-1, 102) \n";   // ALL 그룹(-1), 일반그룹(102) 제외하고 다른 그룹에서 묶인 '모델PI담당자' 찾기

		QueryHelper qHelper = new QueryHelper(); 
		CacheResultSet cRs = null;
		
		ResultSet rs = null;
		try {
			rs = qHelper.executeQuery(sql, new Object[] {});
			cRs = new CacheResultSet( rs );
			
			return cRs;
		} catch(Exception e) {
			throw e;
		} finally {
			if (rs != null) rs.close();
			if (qHelper != null) qHelper.close();
		}
	}
	
	/**
	 * @param param 
	 * @return
	 * @throws Exception
	 */
	public CacheResultSet findSelectedAgreeUsersByInstId(HashMap param) throws  Exception
	{
		String instId = (String)param.get("INST_ID");
		
		String sql = " SELECT AGREE_USER_IDS \n";
		sql = sql +	 " FROM MM_APPR_REQ \n";
		sql = sql +	 " WHERE INST_ID = " + instId + " \n";

		QueryHelper qHelper = new QueryHelper(); 
		CacheResultSet cRs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;

		try {
			rs1 = qHelper.executeQuery(sql, new Object[] {});
			if (rs1.next()) {
				String selectedAgreeUsers = rs1.getString("AGREE_USER_IDS");
				
				if (!("".equals(selectedAgreeUsers))) {

					sql =   	 " SELECT   USER_ID \n"; 
					sql = sql +	 "        , USER_NM \n"; 
					sql = sql +	 "        , GET_DEP_NAME(DEP_ID) AS DEPT_TITLE \n"; 
					sql = sql +	 "        , GET_DEP_NAME(DEP_ID) AS TEAM_TITLE \n"; 
					sql = sql +	 "        , (SELECT ROLE_NM FROM C_ROLE WHERE ROLE_ID = 109) AS ROLE_NM \n"; 
					sql = sql +	 "        , CELL_PHONE \n"; 
					sql = sql +	 "        , 'Y' AS ESSEN_YN \n"; 
					sql = sql +	 "        , EMAIL \n"; 
					sql = sql +	 "        , USER_IP \n"; 
					sql = sql +	 "        , 'N' IS_DBA \n"; 
					sql = sql +	 "   FROM C_USER  \n"; 
					sql = sql +	 "  WHERE USER_ID IN ( " + rs1.getString("AGREE_USER_IDS") + " '') \n";					
					
					rs2 = qHelper.executeQuery(sql, new Object[] {});
					cRs = new CacheResultSet( rs2 );
					
					return cRs;
				}
			} 
			
			return cRs;
			
		} catch(Exception e) {
			throw e;
		} finally {
			if (rs1 != null) rs1.close();
			if (rs2 != null) rs2.close();
			if (qHelper != null) qHelper.close();
		}
	}
	
	/**
	 * @param utwId 
	 * @return Utw UI Name
	 * @throws Exception
	 */
	public String getUtwUINameByUtwId(String utwId) throws  Exception
	{
		String utwUIName = "";
		
		if (utwId == "")
			return utwUIName;
		
		String sql = " SELECT UDP_VAL FROM MM_UDP_VAL \n";
		sql = sql +	 "  WHERE UDP_ID = 'UTW_TECH_UI_NM' AND TRG_ID = '" + utwId + "'\n";

		QueryHelper qHelper = new QueryHelper(); 
		ResultSet rs = null;
		try {
			rs = qHelper.executeQuery(sql, new Object[] {});
			if (rs.next())
				utwUIName = rs.getString("UDP_VAL");
			
			return utwUIName;
			
		} catch(Exception e) {
			throw e;
		} finally {
			if (rs != null) rs.close();
			if (qHelper != null) qHelper.close();
		}
	}

	/**
	 * @param abbrId 
	 * @return abbr
	 * @throws Exception
	 */
	public String getAbbrByAbbrId(String abbrId) throws  Exception
	{
		String abbr = "";
		
		if (abbrId == "")
			return abbr;
		
		String sql = " SELECT ABBR FROM MM_ABBR \n";
		sql = sql +	 "  WHERE ABBR_ID = '" + abbrId + "'\n";

		QueryHelper qHelper = new QueryHelper(); 
		ResultSet rs = null;
		try {
			rs = qHelper.executeQuery(sql, new Object[] {});
			if (rs.next())
				abbr = rs.getString("ABBR");
			
			return abbr;
			
		} catch(Exception e) {
			throw e;
		} finally {
			if (rs != null) rs.close();
			if (qHelper != null) qHelper.close();
		}
	}
	
	/**
	 * 
	 * @param utwId 
	 * @return useAbbrYn
	 * @throws Exception
	 */
	public String getUseAbbrYnByUtwId(String utwId) throws  Exception
	{
		String useAbbrYn = "";
		
		if (utwId == "")
			return useAbbrYn;
		
		String sql = " SELECT UDP_VAL USE_ABBR FROM MM_UDP_VAL \n";
		sql = sql +  "  WHERE UDP_ID = 'UTW_TECH_USE_ABBR' \n";
		sql = sql +  "    AND TRG_ID = '" + utwId + "'\n";

		QueryHelper qHelper = new QueryHelper(); 
		ResultSet rs = null;
		try {
			rs = qHelper.executeQuery(sql, new Object[] {});
			if (rs.next()) useAbbrYn = rs.getString("USE_ABBR");
			else useAbbrYn = "N";
			
			return useAbbrYn;
			
		} catch(Exception e) {
			throw e;
		} finally {
			if (rs != null) rs.close();
			if (qHelper != null) qHelper.close();
		}
	}

	/**
	 * @param abbrIds 
	 * @return abbrs
	 * @throws Exception
	 */
	public ArrayList<String> getAbbrByAbbrIds(Object[] abbrIds) throws  Exception
	{
		ArrayList<String> abbrs = new ArrayList<String>();
		int arrayLen = abbrIds.length;
		
		if (arrayLen < 1) {
			return abbrs;
		} else {
			for (int i = 0; i < arrayLen; i++)
				abbrs.add("");
		}

		StringBuffer sbSql = new StringBuffer("");
		sbSql.append(" SELECT ABBR_ID, ABBR FROM MM_ABBR \n");
		sbSql.append("  WHERE ABBR_ID IN ('NOON'");
		
		for (int i = 0; i < arrayLen; i++) {
			sbSql.append(", '" + (String)abbrIds[i] + "'");
		}
		
		sbSql.append(")\n");
		
		QueryHelper qHelper = new QueryHelper(); 
		ResultSet rs = null;
		try {
			rs = qHelper.executeQuery(sbSql.toString(), new Object[] {});
			while (rs.next()) {
				String dbAbbrId = rs.getString("ABBR_ID");
				String dbAbbr = rs.getString("ABBR");
				
				for (int i = 0; i < arrayLen; i++) {
					if (((String)abbrIds[i]).equals(dbAbbrId)) {
						abbrs.set(i, dbAbbr);
					}
				}
			}
			
			return abbrs;
			
		} catch(Exception e) {
			throw e;
		} finally {
			if (rs != null) rs.close();
			if (qHelper != null) qHelper.close();
		}
	}

	/**
	 * @param utwIds 
	 * @return useAbbrYns
	 * @throws Exception
	 */
	public ArrayList<String> getUseAbbrYnByUtwIds(Object[]utwIds) throws  Exception
	{
		ArrayList<String> useAbbrYns = new ArrayList<String>();
		int arrayLen = utwIds.length;
		
		if (arrayLen < 1) {
			return useAbbrYns;
		} else {
			for (int i = 0; i < arrayLen; i++)
				useAbbrYns.add("");
		}
		
		StringBuffer sbSql = new StringBuffer("");
		sbSql.append(" SELECT TRG_ID AS UTW_ID, UDP_VAL AS USE_ABBR_YN FROM MM_UDP_VAL \n");
		sbSql.append("  WHERE UDP_ID = 'UTW_TECH_USE_ABBR' \n");
		sbSql.append("    AND TRG_ID IN ('NONE'");
		
		for (int i = 0; i < arrayLen; i++) {
			sbSql.append(", '").append((String)utwIds[i]).append("'");
		}
		
		sbSql.append(")\n");
		
		QueryHelper qHelper = new QueryHelper(); 
		ResultSet rs = null;
		try {
			rs = qHelper.executeQuery(sbSql.toString(), new Object[] {});
			while (rs.next()) {
				String dbUtwId = rs.getString("UTW_ID");
				String dbUseAbbrYn = (rs.getString("USE_ABBR_YN").equals("Y") ? "Y" : "N");
				
				for (int i = 0; i < arrayLen; i++) {
					if (((String)utwIds[i]).equals(dbUtwId)) {
						useAbbrYns.set(i, dbUseAbbrYn);
					}
				}
			}
			
			return useAbbrYns;
			
		} catch(Exception e) {
			throw e;
		} finally {
			if (rs != null) rs.close();
			if (qHelper != null) qHelper.close();
		}
	}

	/**
	 * @param utwIds 
	 * @return useAbbrYns
	 * @throws Exception
	 */
	public ArrayList<String> getCommonUseTermYnByUtwIds(Object[]utwIds) throws  Exception
	{
		ArrayList<String> commonUseTermYns = new ArrayList<String>();
		int arrayLen = utwIds.length;
		
		if (arrayLen < 1) {
			return commonUseTermYns;
		} else {
			for (int i = 0; i < arrayLen; i++)
				commonUseTermYns.add("");
		}
		
		StringBuffer sbSql = new StringBuffer("");
		sbSql.append(" SELECT TRG_ID AS UTW_ID, UDP_VAL AS TERM_CLASSFY FROM MM_UDP_VAL \n");
		sbSql.append("  WHERE UDP_ID = 'UTW_TECH_CLASSFY' \n");
		sbSql.append("    AND TRG_ID IN ('NONE'");
		
		for (int i = 0; i < arrayLen; i++) {
			sbSql.append(", '").append((String)utwIds[i]).append("'");
		}
		
		sbSql.append(")\n");
		
		QueryHelper qHelper = new QueryHelper(); 
		ResultSet rs = null;
		try {
			rs = qHelper.executeQuery(sbSql.toString(), new Object[] {});
			while (rs.next()) {
				String dbUtwId = rs.getString("UTW_ID");
				String dbCommonUseTermYn = (rs.getString("TERM_CLASSFY").equals("특수관용어") ? "Y" : "N");
				
				for (int i = 0; i < arrayLen; i++) {
					if (((String)utwIds[i]).equals(dbUtwId)) {
						commonUseTermYns.set(i, dbCommonUseTermYn);
					}
				}
			}
			
			return commonUseTermYns;
			
		} catch(Exception e) {
			throw e;
		} finally {
			if (rs != null) rs.close();
			if (qHelper != null) qHelper.close();
		}
	}

	/**
	 * 
	 * @param workId 
	 * @return StringArray
	 * @throws Exception
	 */
	public ArrayList getParticipants(String workId) throws  Exception
	{
		ArrayList participants = new ArrayList();
		
		if (workId == "")
			return participants;
		
		String sql = " SELECT PARTICIPANT_ID FROM WF_WORK_PARTICIPANT \n";
		sql = sql +	 "  WHERE WORK_ID = '" + workId + "'\n";

		QueryHelper qHelper = new QueryHelper(); 
		ResultSet rs = null;
		try {
			rs = qHelper.executeQuery(sql, new Object[] {});
			while(rs.next())
				participants.add(rs.getString("PARTICIPANT_ID"));
			
			return participants;
			
		} catch(Exception e) {
			throw e;
		} finally {
			if (rs != null) rs.close();
			if (qHelper != null) qHelper.close();
		}
	}

	/**
	 * 
	 * @param  
	 * @return HashMap
	 * @throws Exception
	 */
	public HashMap getDomainGroupInfo() throws  Exception
	{
		HashMap domainGroupInfos = new HashMap();
		
		String sql = " SELECT DOM_GRP_NM, DOM_DEF FROM MM_DOM_GRP \n";

		QueryHelper qHelper = new QueryHelper(); 
		ResultSet rs = null;
		try {
			rs = qHelper.executeQuery(sql, new Object[] {});
			while(rs.next())
				domainGroupInfos.put((String)rs.getString("DOM_GRP_NM"), (String)rs.getString("DOM_DEF"));

			return domainGroupInfos;
		} catch(Exception e) {
			throw e;
		} finally {
			if (rs != null) rs.close();
			if (qHelper != null) qHelper.close();
		}
	}

	/**
	 * 
	 * @param  
	 * @return HashMap
	 * @throws Exception
	 */
	public HashMap getDomainInfo() throws  Exception
	{
		HashMap domainInfos = new HashMap();
		
		String sql = " SELECT COL_DOM_NM, COL_DOM_DEF FROM MM_COL_DOM \n";

		QueryHelper qHelper = new QueryHelper(); 
		ResultSet rs = null;
		try {
			rs = qHelper.executeQuery(sql, new Object[] {});
			while(rs.next())
				domainInfos.put((String)rs.getString("COL_DOM_NM"), (String)rs.getString("COL_DOM_DEF"));

			return domainInfos;
		} catch(Exception e) {
			throw e;
		} finally {
			if (rs != null) rs.close();
			if (qHelper != null) qHelper.close();
		}
	}

	/**
	 * @param  
	 * @return String
	 * @throws Exception
	 */
	public String getSeq(String seqType) throws  Exception
	{
		String Seq = "";
		String sql = "";
		
		if ("BizReport".equals(seqType)) {
			sql = " SELECT 'SRPT' || LPAD (SEQ_BIZ_REPORT_ID.NEXTVAL, 5, 0) AS SEQ FROM DUAL \n";
		} else if ("BizIndex".equals(seqType)) {
			sql = " SELECT 'IDX' || LPAD (SEQ_BIZ_INDEX_ID.NEXTVAL, 8, 0) AS SEQ FROM DUAL \n";
		} else if ("BizView".equals(seqType)) {
			sql = " SELECT 'VIEWP' || LPAD (SEQ_BIZ_VIEW_ID.NEXTVAL, 5, 0) AS SEQ FROM DUAL \n";
		} else {
			return "Seq Type Error!!";
		}
		
		QueryHelper qHelper = new QueryHelper(); 
		ResultSet rs = null;
		try {
			rs = qHelper.executeQuery(sql, new Object[] {});
			if (rs.next())
				Seq = rs.getString("SEQ");
			
			return Seq;
			
		} catch(Exception e) {
			throw e;
		} finally {
			if (rs != null) rs.close();
			if (qHelper != null) qHelper.close();
		}
	}
	
}
