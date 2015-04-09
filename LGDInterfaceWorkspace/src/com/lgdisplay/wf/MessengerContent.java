package com.lgdisplay.wf;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import jspeed.base.jdbc.CacheResultSet;
import jspeed.base.query.DBAssistant;
import jspeed.websvc.WSParam;

import com.itplus.wf.def.act.WfactModel;
import com.lgdisplay.db.DBModelDAO;
import com.lgdisplay.util.JLog;
import com.lgdisplay.util.StrUtils;

public class MessengerContent {
//	static JLogger log = JLogger.getLogger(MessengerContent.class);
	public ArrayList toPreformerDeny(DBAssistant dba, WSParam params, WfactModel model) {
		ArrayList olist = null;
		DBModelDAO dao  = null;
		try {
			dao  = new DBModelDAO(dba);
			String query = "select WORK_ID,PERFORMER_ID as USER_ID,PERFORMER_USER_NAME from WF_WORK_PERFORMER where (work_id) in( "
				+"\n select max(a.work_id) from wf_worklist a, WF_WORK_PERFORMER b "
				+"\n where a.work_id=b.work_id "
				+"\n and a.inst_id=? "
				+"\n and a.work_id < ? "
				+"\n )";
			Object[] objs = new Object[2];
			objs[0] = params.getParameterObj("INST_ID");
			objs[1] = params.getParameterObj("WORK_ID");
			olist = dao.read(query,objs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if ( null != dao ) dao.close();
		}
		return olist;
	}
	
	public ArrayList toUser(String senderemail, String[][] mailTo, DBAssistant dba) {
		ArrayList olist = null;
		DBModelDAO dao  = null;
		try {
			dao  = new DBModelDAO(dba);
			String query = "SELECT * FROM C_USER WHERE 1=1 AND (";
			if ( null != senderemail ) query += " ";
			if ( null != mailTo ) 
				for (int i = 0; i < mailTo.length; i++) {
					JLog.debug("To User Name ===> " + mailTo[i][0]);
					JLog.debug("To User Name ===> " + mailTo[i][1]);
					query += " USER_ID = '"+ mailTo[i][0] +"' OR ";
				}
			query = query.substring(0, query.length()-3);
			query += ")";
			
			olist = dao.read(query);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if ( null != dao ) dao.close();
		}
		return olist;
	}
	public String getMailContent(HashMap map, DBAssistant dba) {
		System.out.println("........................................................");
		System.out.println("......................MAIL CONTENT........................."+map.toString());
		System.out.println("........................................................");
		StringBuffer osbContents = new StringBuffer();

		CacheResultSet rs = null;
		CacheResultSet rs2 = null;

		String inst_id = (String)map.get("INST_ID");

		String sql = null;

		String title = "";
		String dat_strc_nm = "";
		String user_nm = "";
//		String cell_phone = "";
		String user_position = "";
		String req_dt = "";
		String content = "";
		String REQ_REASON = "";
		String trg_tp_cd = "MM_ELEM_INFO";
		String trg_id = "";
		String req_dt2 = "";
		String APPR_DESC = "";

		try {
			com.itplus.mm.dao.CommonDAO dao  = new com.itplus.mm.dao.CommonDAO(dba);
			
			sql = "";
			sql += 	"SELECT \n";
			sql +=	"   A.REQ_USER_ID, \n";
			sql +=	"   A.REQ_TTL AS REQ_TTL, \n";
			sql +=	"   FN_GET_DAT_STRC_NAMESPACE(A.DAT_STRC_ID,'','>') AS DAT_STRC_NM, \n";
			sql +=	"   C.USER_NM AS USER_NM, \n";
			sql +=	"   C.CELL_PHONE AS CELL_PHONE, \n";
			sql +=	"   D.DISPLAY_NM AS USER_POSITION, \n";
			sql +=	"   a.APPR_STAT_TP_CD_1, \n";
			sql +=	"   B.REQ_DT AS REQ_DT, \n";
			sql +=	"   A.REQ_DESC AS REQ_DESC, \n";
			sql +=	"   A.REQ_REASON AS REQ_REASON, \n";
			sql +=	"   B.TRG_TP_CD AS TRG_TP_CD, \n";	
			sql +=	"   B.TRG_ID AS TRG_ID, \n";
			sql +=	"   E.APPR_DESC \n";
			sql +=	"FROM MM_APPR_REQ A, MM_APPR_REQ_DETAIL B, C_USER C, C_CD_NM D, MM_APPR_HST E \n";
			sql +=	"WHERE A.INST_ID=B.INST_ID(+) \n";
			sql +=	"  AND A.REQ_USER_ID=C.USER_ID \n";
			sql +=	"  AND C.POSITION_CODE=D.CD_ID(+) \n";
			sql +=	"  AND A.INST_ID=? AND D.LANG_CD='ko-KR' AND A.INST_ID=E.INST_ID(+) ";
			
			JLog.debug("[SQL]=\n"+sql);
			Object[] obj = new Object[1];
			obj[0] = inst_id;
			rs = dao.executeQuery("mm.mail.getContent", sql, obj);
			
			if (rs.next()){
				title = rs.getString("REQ_TTL");
				dat_strc_nm = rs.getString("DAT_STRC_NM");
				user_nm = rs.getString("USER_NM");
//				cell_phone = rs.getString("CELL_PHONE");
				user_position = rs.getString("USER_POSITION");
				req_dt = rs.getString("REQ_DT");
				content = rs.getString("REQ_DESC");
				trg_tp_cd = rs.getString("TRG_TP_CD");
				trg_id = rs.getString("TRG_ID");
				REQ_REASON = rs.getString("REQ_REASON");
				APPR_DESC = rs.getString("APPR_DESC");
				req_dt2 = jspeed.base.util.DateHelper.format(req_dt, "yyyyMMddHHmmss", "yyyy-MM-dd HH:mm:ss"); 
			}
			
			sql = "";
			
			if ("MM_ELEM_INFO".equals(trg_tp_cd)){
				sql += " SELECT ELEM_PHSC_NM, ELEM_LGCL_NM, REQ_TP_CD, REQ_TP_CD_SEQ FROM ( ";
				sql += " 	SELECT   ";
				sql += " 		X.ELEM_PHSC_NM AS ELEM_PHSC_NM, ";
				sql += " 		X.ELEM_LGCL_NM AS ELEM_LGCL_NM, ";
				sql += " 		X.REQ_TP_CD AS REQ_TP_CD, ";
				sql += " 		CASE WHEN X.REQ_TP_CD='WFGB_D' THEN 1  ";
				sql += " 	   		WHEN X.REQ_TP_CD='WFGB_U' THEN 2  ";
				sql += " 	   		WHEN X.REQ_TP_CD='WFGB_I' THEN 3 END AS REQ_TP_CD_SEQ  ";
				sql += " 	FROM  ";
				sql += " 		(SELECT  ";
				sql += " 			   I.REQ_TP_CD ";
				sql += " 			   ,I.ELEM_PHSC_NM ";
				sql += " 			   ,I.ELEM_LGCL_NM ";
				sql += " 		FROM MM_LNK_INFO L, MM_ELEM_INFO_REQ I ";
				sql += " 		WHERE L.LNK_INFO_ID=I.LNK_INFO_ID ";
				sql += " 			  AND I.ELEM_INFO_ID IN (SELECT ELEM_INFO_ID FROM MM_ELEM_REL_REQ WHERE PAR_ELEM_INFO_ID='"+trg_id+"' AND REL_TP_CD='RLTP_P') ";
				sql += " 			  AND I.REQ_DT='"+ req_dt+"' ";
				sql += " 	          AND I.ELEM_TP_ID='Entity' ";
				sql += " 		)X, ";
				sql += " 		( ";
				sql += " 			SELECT  ";
				sql += " 				   I.ELEM_PHSC_NM, ";
				sql += " 				   I.ELEM_LGCL_NM ";
				sql += " 			FROM MM_LNK_INFO L, MM_ELEM_INFO I ";
				sql += " 			WHERE L.LNK_INFO_ID=I.LNK_INFO_ID ";
				sql += " 				  AND I.ELEM_INFO_ID IN (SELECT ELEM_INFO_ID FROM MM_ELEM_REL WHERE PAR_ELEM_INFO_ID='"+trg_id+"' AND REL_TP_CD='RLTP_P') ";
				sql += " 				  AND L.DEL_YN='N' ";
				sql += " 				  AND L.AUTO_COLLECT_YN='N' ";
				sql += " 				  AND I.ELEM_TP_ID='Entity' ";
				sql += " 		)Y ";
				sql += " 	WHERE X.ELEM_PHSC_NM=Y.ELEM_PHSC_NM(+) ";
				sql += " 		  AND X.REQ_TP_CD <> 'WFGB_S' ";
				sql += " 	UNION  ";
				sql += " 	SELECT   ";
				sql += " 			Y.ELEM_PHSC_NM AS ELEM_PHSC_NM, ";
				sql += " 			Y.ELEM_LGCL_NM AS ELEM_LGCL_NM, ";
				sql += " 			CASE WHEN X.REQ_TP_CD IS NULL THEN 'WFGB_D' ELSE X.REQ_TP_CD END AS REQ_TP_CD, ";
				sql += " 			1 AS REQ_TP_CD_SEQ  ";
				sql += " 	FROM  ";
				sql += " 		(SELECT  ";
				sql += " 			   I.REQ_TP_CD ";
				sql += " 			   ,I.ELEM_PHSC_NM ";
				sql += " 			   ,I.ELEM_LGCL_NM ";
				sql += " 		FROM MM_LNK_INFO L, MM_ELEM_INFO_REQ I ";
				sql += " 		WHERE L.LNK_INFO_ID=I.LNK_INFO_ID ";
				sql += " 			  AND I.ELEM_INFO_ID IN (SELECT ELEM_INFO_ID FROM MM_ELEM_REL_REQ WHERE PAR_ELEM_INFO_ID='"+trg_id+"' AND REL_TP_CD='RLTP_P') ";
				sql += " 			  AND I.REQ_DT='"+ req_dt+"' ";
				sql += " 	          AND I.ELEM_TP_ID='Entity' ";
				sql += " 		)X, ";
				sql += " 		( ";
				sql += " 			SELECT  ";
				sql += " 				   I.ELEM_PHSC_NM, ";
				sql += " 				   I.ELEM_LGCL_NM ";
				sql += " 			FROM MM_LNK_INFO L, MM_ELEM_INFO I ";
				sql += " 			WHERE L.LNK_INFO_ID=I.LNK_INFO_ID ";
				sql += " 				  AND I.ELEM_INFO_ID IN (SELECT ELEM_INFO_ID FROM MM_ELEM_REL WHERE PAR_ELEM_INFO_ID='"+trg_id+"' AND REL_TP_CD='RLTP_P') ";
				sql += " 				  AND L.DEL_YN='N' ";
				sql += " 				  AND I.DEL_YN='N' ";
				sql += " 				  AND L.AUTO_COLLECT_YN='N' ";
				sql += " 				  AND I.ELEM_TP_ID='Entity' ";
				sql += " 				  AND (I.DEL_YN='N' OR I.REQ_DT='"+req_dt+"') ";
				sql += " 		)Y ";
				sql += " 	WHERE X.ELEM_PHSC_NM(+)=Y.ELEM_PHSC_NM ";
				sql += " 		  AND X.ELEM_PHSC_NM IS NULL ";
				sql += " ) ORDER BY REQ_TP_CD_SEQ, ELEM_LGCL_NM, REQ_TP_CD_SEQ ";	
				
			}
			
			else{
				sql += "";
				sql += " SELECT ";
			 	sql += " TRG_NAME, TRG_TP_CD, TRG_TP_CD_SEQ ";
				sql += " FROM ( ";
		 		sql += "	SELECT ";
		 		sql += "		C.TRG_NAME, C.TRG_TP_CD,";
			   	sql += "		CASE WHEN C.TRG_TP_CD='MM_UFW' THEN 1 ";
		 	   	sql += "		WHEN C.TRG_TP_CD='MM_CODE_DOM' THEN 2 ";
		 	   	sql += "		WHEN C.TRG_TP_CD='MM_CODE_DOM_VAL_REL' THEN 3 ";
				sql += "		WHEN C.TRG_TP_CD='MM_INFOTYPE' THEN 4 ";
				sql += "		WHEN C.TRG_TP_CD='MM_UTW' THEN 5 END AS TRG_TP_CD_SEQ  ";
				sql += "	 FROM MM_APPR_REQ A, MM_APPR_REQ_DETAIL B, V_MY_WORKS C ";
		 		sql += "	 WHERE A.INST_ID="+inst_id;
		 	   	sql += "		AND A.INST_ID=B.INST_ID ";
		 	   	sql += "		AND B.TRG_ID=C.TRG_ID AND B.REQ_DT=C.REQ_DT ";
				sql += ") ORDER BY TRG_TP_CD_SEQ, TRG_NAME ";
			}
			JLog.debug("[SQL]=\n"+sql);
			
			rs2 = dao.executeQuery("mm.mail.getContent", sql, new Object[] { });

//			osbContents
//			.append("업무      : ").append(dat_strc_nm).append("<br>")
//			.append(" 작성자 : ").append(user_nm).append(" "+user_position).append("<br>")
//			.append(" 작성일 : ").append(req_dt2).append("<br>")
//			.append(" 제목      : ").append(title).append("<br>")
//			.append(" 내용      : ").append(content).append("<br>")
//			.append(" 작업상세 : ");
			osbContents
			.append("\t <table border='0' cellpadding='4' cellspacing='1'>                                  \n")
			.append("\t <tr height='20'>                                                                    \n")
			.append("\t 	<td width='110' bgcolor='#E3EFFF'><b>업무</b></td>                                       \n")
			.append("\t 	<td width='400' bgcolor='#ffffff'>").append(dat_strc_nm).append("</td>\n")
			.append("\t </tr>                                                                               \n")
			.append("\t <tr height='20'>                                                                    \n")
			.append("\t 	<td bgcolor='#E3EFFF'><b>작성자</b></td>                                                 \n")
			.append("\t 	<td bgcolor='#ffffff'>").append(user_nm).append(" "+user_position).append("</td>  \n")
			.append("\t </tr>	                                                                              \n")
			.append("\t <tr height='20'>                                                                    \n")
			.append("\t 	<td bgcolor='#E3EFFF'><b>작성일</b></td>                                                 \n")
			.append("\t 	<td bgcolor='#ffffff'>").append(req_dt2).append("</td> \n")
			.append("\t </tr>	                                                                              \n")
			.append("\t <tr height='20'>                                                                    \n")
			.append("\t 	<td bgcolor='#E3EFFF'><b>제목</b></td>                                                   \n")
			.append("\t 	<td bgcolor='#ffffff'>").append(fnTranslateNoHtml(title)).append("</td>                          \n")
			.append("\t </tr>	                                                                              \n")
			.append("\t <tr height='20'>                                                                    \n")
			.append("\t 	<td bgcolor='#E3EFFF'><b>내용</b></td>                                                   \n")
			.append("\t 	<td bgcolor='#ffffff'>").append(fnTranslateNoHtml(content)).append("</td>                        \n")
			.append("\t </tr>	                                                                              \n")
			.append("\t <tr height='20'>                                                                    \n")
			.append("\t 	<td bgcolor='#E3EFFF'><b>근거</b></td>                                                   \n")
			.append("\t 	<td bgcolor='#ffffff'>").append(fnTranslateNoHtml(REQ_REASON)).append("</td>                        \n")
			.append("\t </tr>	                                                                              \n")
			.append("\t <tr height='20'>                                                                    \n")
			.append("\t 	<td bgcolor='#E3EFFF'><b>의견</b></td>                                                   \n")
			.append("\t 	<td bgcolor='#ffffff'>").append(fnTranslateNoHtml(StrUtils.nullToEmpty(APPR_DESC))).append("</td>                        \n")
			.append("\t </tr>	                                                                              \n")
			.append("\t <tr height='20'>                                                                    \n")
			.append("\t 	<td bgcolor='#E3EFFF'><b>작업상세</b></td>                                               \n")
			.append("\t 	<td bgcolor='#ffffff'>                                                            \n");
			
			if ("MM_ELEM_INFO".equals(trg_tp_cd)){	
				String chg_tp_cd = "";
				int i = 1;
				while (rs2.next()){
					if (!chg_tp_cd.equals(rs2.getString("REQ_TP_CD"))){
						chg_tp_cd = rs2.getString("REQ_TP_CD");
						if ("WFGB_D".equals(rs2.getString("REQ_TP_CD"))){
							osbContents.append("<br>\t"+i+". 테이블삭제<br>\t");
							osbContents.append("==============================================<br>");
							i++;
						}else if ("WFGB_U".equals(rs2.getString("REQ_TP_CD"))){
							osbContents.append("<br>\t"+i+". 테이블변경<br>\t");
							osbContents.append("==============================================<br>");
							i++;
						}else if ("WFGB_I".equals(rs2.getString("REQ_TP_CD"))){
							osbContents.append("<br>\t"+i+". 테이블추가<br>\t");
							osbContents.append("==============================================<br>");
							i++;
						}
					}
					osbContents.append(rs2.getString("ELEM_PHSC_NM") + " (" + rs2.getString("ELEM_LGCL_NM") + ")<br>\t"); 
				}
			}else{
				trg_tp_cd = "";
				int i = 1;
				while (rs2.next()){
					if (!trg_tp_cd.equals(rs2.getString("TRG_TP_CD"))){
						trg_tp_cd = rs2.getString("TRG_TP_CD");
						if ("MM_UFW".equals(rs2.getString("TRG_TP_CD"))){
							osbContents.append("<br>\t"+i+". 용어 신청<br>\t");
							osbContents.append("==============================================<br>");
							i++;
						}else if ("MM_CODE_DOM".equals(rs2.getString("TRG_TP_CD"))){
							osbContents.append("<br>\t"+i+". 코드 신청<br>\t");
							osbContents.append("==============================================<br>");
							i++;
						}else if ("MM_CODE_DOM_VAL_REL".equals(rs2.getString("TRG_TP_CD"))){
							osbContents.append("<br>\t"+i+". 연관코드 신청<br>\t");
							osbContents.append("==============================================<br>");
							i++;
						}else if ("MM_INFOTYPE".equals(rs2.getString("TRG_TP_CD"))){
							osbContents.append("<br>\t"+i+". 인포타입 신청<br>\t");
							osbContents.append("==============================================<br>");
							i++;
						}else if ("MM_UTW".equals(rs2.getString("TRG_TP_CD"))){
							osbContents.append("<br>\t"+i+". 단어 신청<br>\t");
							osbContents.append("==============================================<br>");
							i++;
						}
					}
					osbContents.append(rs2.getString("TRG_NAME") + "<br>\t"); 
				}
			}
			osbContents.append("</td>")
			.append("</tr>")	
			.append("</table>");
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
				}
			if (rs2 != null)
				try {
					rs2.close();
				} catch (SQLException e) {
				}
		}
		return osbContents.toString();
	}
	public String getMailTitle(HashMap map, DBAssistant dba) {

		CacheResultSet rs = null;

		String inst_id = (String)map.get("INST_ID");

		String sql = null;

		String title = "";

		try {
			com.itplus.mm.dao.CommonDAO dao  = new com.itplus.mm.dao.CommonDAO(dba);
			
			sql = "";
			sql += 	"SELECT ";
			sql +=	"   A.REQ_USER_ID, ";
			sql +=	"   A.REQ_TTL AS REQ_TTL, ";
			sql +=	"   FN_GET_DAT_STRC_NAMESPACE(A.DAT_STRC_ID,'','>') AS DAT_STRC_NM, ";
			sql +=	"   C.USER_NM AS USER_NM, ";
			sql +=	"   C.CELL_PHONE AS CELL_PHONE, ";
			sql +=	"   D.DISPLAY_NM AS USER_POSITION, ";
			sql +=	"   a.APPR_STAT_TP_CD_1, ";
			sql +=	"   B.REQ_DT AS REQ_DT, ";
			sql +=	"   A.REQ_DESC AS REQ_DESC, ";
			sql +=	"   A.REQ_REASON AS REQ_REASON, ";
			sql +=	"   B.TRG_TP_CD AS TRG_TP_CD, ";	
			sql +=	"   B.TRG_ID AS TRG_ID ";
			sql +=	"FROM MM_APPR_REQ A, MM_APPR_REQ_DETAIL B, C_USER C, C_CD_NM D ";
			sql +=	"WHERE A.INST_ID=B.INST_ID(+) ";
			sql +=	"  AND A.REQ_USER_ID=C.USER_ID ";
			sql +=	"  AND C.POSITION_CODE=D.CD_ID(+) ";
			sql +=	"  AND A.INST_ID="+inst_id;
			JLog.debug("[SQL]=\n"+sql);
			rs = dao.executeQuery("mm.mail.getContent", sql, new Object[] { });
			if (rs.next()){
				title = rs.getString("REQ_TTL");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
				}
		}
		return title;
	}

	/* 문자열의 지환 */
	public String replace(String str, String pattern, String replace) {
		int s = 0;
		int e = 0;
		StringBuffer result = new StringBuffer();

		while ((e = str.indexOf(pattern, s)) >= 0) { 
			result.append(str.substring(s, e)); 
			result.append(replace); 
			s = e + pattern.length(); 
		} 
		result.append(str.substring(s)); 
		return result.toString(); 
	}

	public String fnTranslateNewLine(String str) {
		String rStr = str;
		if (rStr != null && !rStr.equals("")) {
			rStr = replace(rStr, "\r\n", "<br>");
			rStr = replace(rStr, "\n\r", "<br>");
		}
		return rStr;
	}
	
	public String fnTranslateNoHtml(String str) {
		String rStr = str;
		if (rStr != null && !rStr.equals("")) {
			rStr = replace(rStr, "&", "&amp;");
			rStr = replace(rStr, "<", "&lt;");
			rStr = replace(rStr, ">", "&gt;");
			rStr = replace(rStr, " ", "&nbsp;");
			rStr = replace(rStr, "\r\n", "<br>");
			rStr = replace(rStr, "\n\r", "<br>");
			rStr = replace(rStr, "\r\n", "<br>");
			rStr = replace(rStr, "\n", "<br>");
		}
		return rStr;
	}
	
	public String printContent(String content, String mode) {
		String rStr = content;
		if (mode.equals("1")) {
			rStr = fnTranslateNewLine(rStr);
		}else{
			rStr = fnTranslateNoHtml(rStr);
		}
		return rStr;
	}

}
