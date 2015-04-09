package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import jspeed.base.jdbc.BaseSQLException;

public class Test {
	
	public Connection getConnection() throws Exception
	{
		Connection conn = null;

		String url = "jdbc:oracle:thin:@10.110.132.36:1521:XE";
		String usr = "metaadm";
		String pwd = "metaadm";

		try
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(url, usr, pwd);
		}
		catch (Exception e)
		{
			System.out.println("connection failed : " + e.getMessage());
			throw e;
		}

		return conn;
	}
	
	public static void main(String[] args) throws Exception {
		new Test().collectUser();
	}
	
	public int collectUser() throws BaseSQLException {
		
		try {
			collectDepInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
	}

	private int collectDepInfo() throws Exception {
		Connection conn = null;
		Statement stmt = null;
		StringBuffer sbSql = null;
		int retCnt = 0;
		
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			
			sbSql = new StringBuffer();
			sbSql.append("MERGE INTO METAADM.C_DEP_INFO A\n");
			sbSql.append("USING (\n");
			sbSql.append("        WITH TMP AS (\n");
			sbSql.append("            SELECT A.*, LEVEL AS LVL\n");
			sbSql.append("            FROM EAI_HR_ORG_INFO A\n"); 
			sbSql.append("            CONNECT BY PRIOR ORG_CODE = PARENT_ORG_CODE\n");
			sbSql.append("                AND ORG_CODE <> 41064 AND PARENT_ORG_CODE <> 41012\n");
			sbSql.append("            START WITH PARENT_ORG_CODE IS NULL\n");
			sbSql.append("        )\n");
			sbSql.append("        /*\n");
			sbSql.append("            DEP_ID  PAR_DEP_ID  DEP_TITLE   DEP_DESC    DEP_TP_CD   DISPLAY_DEPTH   DISPLAY_ORDER   CRE_ID  CRE_DT  UPD_ID  UPD_DT\n");
			sbSql.append("        */\n");
			sbSql.append("        SELECT\n");
			sbSql.append("            A.ORG_CODE AS DEP_ID\n");
			sbSql.append("            , DECODE(B.ORG_CODE, NULL, 999999, B.PARENT_ORG_CODE) AS PAR_DEP_ID\n");
			sbSql.append("            , A.ORG_NAME AS DEP_TITLE\n");
			sbSql.append("            , '부서 연계정보' AS DEP_DESC\n");
			sbSql.append("            , NULL AS DEP_TP_CD\n");
			sbSql.append("            , DECODE(B.ORG_CODE, NULL, 1, B.LVL-1) AS DISPLAY_DEPTH\n");
			sbSql.append("            , NULL AS DISPLAY_ORDER\n");
			sbSql.append("            , 4 AS CRE_ID\n");
			sbSql.append("            , TO_CHAR(SYSDATE, 'yyyyMMddHH24miss') AS CRE_DT\n");
			sbSql.append("            , NULL AS UPD_ID\n");
			sbSql.append("            , NULL AS UPD_DT\n");
			sbSql.append("        FROM (\n");
			sbSql.append("                SELECT *\n");
			sbSql.append("                FROM EAI_HR_ORG_INFO\n");
			sbSql.append("                WHERE (ORG_CODE <> 41064 OR PARENT_ORG_CODE <> 41012)\n");
			sbSql.append("                  AND APPLICATION_TRANSFER_FLAG = 'P'\n");
			sbSql.append("             ) A,\n");
			sbSql.append("             TMP B\n");
			sbSql.append("        WHERE A.ORG_CODE = B.ORG_CODE\n");
			sbSql.append("          AND B.APPLICATION_TRANSFER_FLAG = 'P'\n");
			sbSql.append("          AND A.DATA_INTERFACE_TYPE_CODE IN ('I', 'U')\n");
			sbSql.append("          AND (A.END_DATE IS NULL OR (A.END_DATE IS NOT NULL AND A.END_DATE >= TO_CHAR(SYSDATE, 'yyyyMMddHH24miss')))\n");
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
			
			stmt = conn.createStatement();
			retCnt = stmt.executeUpdate(sbSql.toString());
			System.out.println("### collectDepInfo() retCnt1 : "+retCnt);
			
			conn.rollback();
		} catch (Exception e) {
			e.printStackTrace();
			if (conn != null) try { conn.rollback(); } catch (Exception ignroe) {}
			throw e;
		} finally {
			if (conn != null) try { conn.close(); } catch (Exception ignroe) {}
		}
		
		return retCnt;
	}
}
