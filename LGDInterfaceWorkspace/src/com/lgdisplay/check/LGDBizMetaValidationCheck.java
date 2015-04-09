package com.lgdisplay.check;

import com.itplus.mm.server.datadic.AbstractValidationCheck;
import com.lgdisplay.db.LgdDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class LGDBizMetaValidationCheck extends AbstractValidationCheck {

	/**
	 * 
	 * <pre>
	 * ºñÁî¸ÞÅ¸¿¡ ´ëÇÑ »ç¿ëÀÚ Á¤ÀÇ À¯È¿¼º Ã¼Å©¸¦ ÇÑ´Ù. - ½Å±Ô ½Ã
	 * </pre>
	 * @param info
	 * 	 
	 * [´Ü¾î]
	 * REQ_TP_CD		String		»ý¼º(WFGB_I), ¼öÁ¤(WFGB_U), »èÁ¦(WFGB_D)
	 * REQ_EDIT_TYPE	String		»ý¼º(), ¼öÁ¤(UPDATE), »èÁ¦(DELETE)
	 * UTW_DIC_ID		String		´Ü¾î»çÀüID
	 * UTW_ID			String		´Ü¾î ID
	 * DAT_STRC_ID		String		µ¥ÀÌÅÍ ±¸Á¶ ID
	 * UTW_NM			String		ÇÑ±Û ¸í
	 * ABBR				String		¾à¾î
	 * EN_NM			String		¿µ¹® ¸í
	 * UTW_TP_CD		String		´Ü¾î À¯Çü ÄÚµå
	 * UTW_DEF			String		Á¤ÀÇ
	 * 
	 * @return
	 * @throws Exception
	 */	

	/*
     * LGD ¿ë¾î Ã¼Å© Rule
     * 
	 *    Rule 1. ¿ë¾î¸í, ¿µ¹®¸í, ¾à¾î ÇÊ¼öÃ¼Å©, °ø¹éÃ¼Å©, Æ¯¼ö¹®ÀÚ»ç¿ë ¸øÇÏ°Ô Ã¼Å©
	 *    Rule 2. UDPÀÇ ¾à¾îÇÊ¼ö Ç×¸ñÀ» ÀÐ¾î¿Í¼­, 
	 *        'Y' ÀÎ °æ¿ì, ¿ë¾î¼³¸í¿¡ '[Ç×¸ñ»ý¼º½Ã ¾à¾î»ç¿ë ÇÊ¼ö]' ³»¿ëÀÌ ÀÖ´ÂÁö È®ÀÎ
	 *        'N' ÀÎ °æ¿ì, ¿ë¾î¼³¸í¿¡ '[Ç×¸ñ»ý¼º½Ã ¾à¾î»ç¿ë ÇÊ¼ö]' ³»¿ëÀÌ ¾ø´ÂÁö È®ÀÎ
	 */
	
     public HashMap checkInsertValid(HashMap info) throws Exception {	
		HashMap result = new HashMap();
		
		String checkSuccYN = "Y";
		String errorCode = "", errorMsg = "";
		
		String utwId = (String)info.get("UTW_ID");
		String utwKorNm = (String)info.get("UTW_NM");
		String utwEngNm = (String)info.get("EN_NM");
		String utwAbbr = (String)info.get("ABBR");
		String utwDef = (String)info.get("UTW_DEF");
		ArrayList udp_vals = (ArrayList)info.get("UDP_VAL"); 
		String useAbbrYn = (String)udp_vals.get(4);

		// Rule 1 Check
		Pattern p = Pattern.compile("\\W");
		Pattern p_kor = Pattern.compile("[^a-zA-Z0-9°¡-ÆR_]");
		
		if ("".equals(utwKorNm)) {
			errorCode = "11";
			errorMsg = "¿ë¾î¸íÀ» ÀÔ·ÂÇÏ¼Å¾ß ÇÕ´Ï´Ù.";
		} else if (utwKorNm.matches(".* .*")) {
			errorCode = "12";
			errorMsg = "¿ë¾î¸í¿¡ °ø¹éÀÌ Çã¿ëµÇÁö ¾Ê½À´Ï´Ù.";
		} else if (p_kor.matcher(utwKorNm).find()) {
			errorCode = "13";
			errorMsg = "¿ë¾î¸í¿¡´Â ÇÑ±Û°ú ¿µ¹®ÀÚ, ¼ýÀÚ, '_' ¸¸ »ç¿ëÇÏ½Ç ¼ö ÀÖ½À´Ï´Ù.";
		}

		if ("".equals(utwEngNm)) {
			errorCode = "21";
			errorMsg = "¿µ¹®¸íÀ» ÀÔ·ÂÇÏ¼Å¾ß ÇÕ´Ï´Ù.";
		} else if (utwEngNm.matches(".* .*")) {
			errorCode = "22";
			errorMsg = "¿µ¹®¸í¿¡ °ø¹éÀÌ Çã¿ëµÇÁö ¾Ê½À´Ï´Ù.";
		} else if (p.matcher(utwEngNm).find()) {
			errorCode = "23";
			errorMsg = "¿µ¹®¸í¿¡´Â ¿µ¹®ÀÚ¿Í ¼ýÀÚ, '_' ¸¸ »ç¿ëÇÏ½Ç ¼ö ÀÖ½À´Ï´Ù.";
		}

		if ("".equals(utwAbbr)) {
			errorCode = "31";
			errorMsg = "¾à¾î¸¦ ÀÔ·ÂÇÏ¼Å¾ß ÇÕ´Ï´Ù.";
		} else if (utwAbbr.matches(".* .*")) {
			errorCode = "32";
			errorMsg = "¾à¾î¿¡ °ø¹éÀÌ Çã¿ëµÇÁö ¾Ê½À´Ï´Ù.";
		} else if (p.matcher(utwAbbr).find()) {
			errorCode = "33";
			errorMsg = "¾à¾î¿¡´Â ¿µ¹®ÀÚ¿Í ¼ýÀÚ, '_' ¸¸ »ç¿ëÇÏ½Ç ¼ö ÀÖ½À´Ï´Ù.";
		}
		
		// Rule 2 Check
		if ("".equals(errorCode)) {
			
			String compareStr = "[Ç×¸ñ»ý¼º½Ã ¾à¾î»ç¿ë ¿ë¾î]";
			
			if ("Y".equals(useAbbrYn)) {
				if (utwDef.length() >= compareStr.length()) {
					if (!(compareStr.equals(utwDef.subSequence(0, compareStr.length())))) {
						errorCode = "40";
						errorMsg = "¾à¾î»ç¿ëÇÊ¼ö¿©ºÎ°¡ 'Y' ÀÎ °æ¿ì, ¿ë¾îÁ¤ÀÇ°¡ '" + compareStr + "'·Î ½ÃÀÛÇØ¾ßÇÕ´Ï´Ù.";
					}
				} else {
					errorCode = "40";
					errorMsg = "¾à¾î»ç¿ëÇÊ¼ö¿©ºÎ°¡ 'Y' ÀÎ °æ¿ì, ¿ë¾îÁ¤ÀÇ°¡ '" + compareStr + "'·Î ½ÃÀÛÇØ¾ßÇÕ´Ï´Ù.";
				}
			} else {
				if (utwDef.length() >= compareStr.length()) {
					if (compareStr.equals(utwDef.subSequence(0, compareStr.length()))) {
						errorCode = "41";
						errorMsg = "¾à¾î»ç¿ëÇÊ¼ö¿©ºÎ°¡ 'N' ÀÎ °æ¿ì, ¿ë¾îÁ¤ÀÇ°¡ '" + compareStr + "'·Î ½ÃÀÛÇÏ¸é ¾ÈµË´Ï´Ù.";
					}
				}
			}
		}
		
		if ("".equals(errorCode)) {
			checkSuccYN = "Y";
			// System.out.println ("¿¡·¯°¡ ¾ø½À´Ï´Ù.");
		} else {
			checkSuccYN = "N";
			//System.out.println (errorCode + "¿¡·¯");
		}

		result.put("VALID_ID", utwId);
		result.put("ERROR_ID", errorCode);
		result.put("ERROR_MSG", errorMsg);
		result.put("VALD_CHECK_SUCC_YN", checkSuccYN);
		
		return result;
	}

	/**
	 * 
	 * <pre>
	 * Ç¥ÁØ¿¡ ´ëÇÑ »ç¿ëÀÚ Á¤ÀÇ À¯È¿¼º Ã¼Å©¸¦ ÇÑ´Ù. - ¼öÁ¤ ½Ã
	 * </pre>
	 * @param info
	 * 	 
	 * [´Ü¾î]
	 * REQ_TP_CD		String		»ý¼º(WFGB_I), ¼öÁ¤(WFGB_U), »èÁ¦(WFGB_D)
	 * REQ_EDIT_TYPE	String		»ý¼º(), ¼öÁ¤(UPDATE), »èÁ¦(DELETE)
	 * UTW_DIC_ID		String		´Ü¾î»çÀüID
	 * UTW_ID			String		´Ü¾î ID
	 * DAT_STRC_ID		String		µ¥ÀÌÅÍ ±¸Á¶ ID
	 * UTW_NM			String		ÇÑ±Û ¸í
	 * ABBR				String		¾à¾î
	 * EN_NM			String		¿µ¹® ¸í
	 * UTW_TP_CD		String		´Ü¾î À¯Çü ÄÚµå
	 * UTW_DEF			String		Á¤ÀÇ
	 * 
	 * @return
	 * @throws Exception
	 */	
	public HashMap checkUpdateValid(HashMap info) throws Exception {		
		HashMap result = new HashMap();
		
		String checkSuccYN = "Y";
		String errorCode = "", errorMsg = "";
		
		String utwId = (String)info.get("UTW_ID");
		String utwKorNm = (String)info.get("UTW_NM");
		String utwEngNm = (String)info.get("EN_NM");
		String utwAbbr = (String)info.get("ABBR");
		String utwDef = (String)info.get("UTW_DEF");
		ArrayList udp_vals = (ArrayList)info.get("UDP_VAL"); 
		String useAbbrYn = (String)udp_vals.get(4);
		
		// Rule 1 Check
		Pattern p = Pattern.compile("\\W");
		Pattern p_kor = Pattern.compile("[^a-zA-Z0-9°¡-ÆR_]");
		
		if ("".equals(utwKorNm)) {
			errorCode = "11";
			errorMsg = "¿ë¾î¸íÀ» ÀÔ·ÂÇÏ¼Å¾ß ÇÕ´Ï´Ù.";
		} else if (utwKorNm.matches(".* .*")) {
			errorCode = "12";
			errorMsg = "¿ë¾î¸í¿¡ °ø¹éÀÌ Çã¿ëµÇÁö ¾Ê½À´Ï´Ù.";
		} else if (p_kor.matcher(utwKorNm).find()) {
			errorCode = "13";
			errorMsg = "¿ë¾î¸í¿¡´Â ÇÑ±Û°ú ¿µ¹®ÀÚ, ¼ýÀÚ, '_' ¸¸ »ç¿ëÇÏ½Ç ¼ö ÀÖ½À´Ï´Ù.";
		}

		if ("".equals(utwEngNm)) {
			errorCode = "21";
			errorMsg = "¿µ¹®¸íÀ» ÀÔ·ÂÇÏ¼Å¾ß ÇÕ´Ï´Ù.";
		} else if (utwEngNm.matches(".* .*")) {
			errorCode = "22";
			errorMsg = "¿µ¹®¸í¿¡ °ø¹éÀÌ Çã¿ëµÇÁö ¾Ê½À´Ï´Ù.";
		} else if (p.matcher(utwEngNm).find()) {
			errorCode = "23";
			errorMsg = "¿µ¹®¸í¿¡´Â ¿µ¹®ÀÚ¿Í ¼ýÀÚ, '_' ¸¸ »ç¿ëÇÏ½Ç ¼ö ÀÖ½À´Ï´Ù.";
		}

		if ("".equals(utwAbbr)) {
			errorCode = "31";
			errorMsg = "¾à¾î¸¦ ÀÔ·ÂÇÏ¼Å¾ß ÇÕ´Ï´Ù.";
		} else if (utwAbbr.matches(".* .*")) {
			errorCode = "32";
			errorMsg = "¾à¾î¿¡ °ø¹éÀÌ Çã¿ëµÇÁö ¾Ê½À´Ï´Ù.";
		} else if (p.matcher(utwAbbr).find()) {
			errorCode = "33";
			errorMsg = "¾à¾î¿¡´Â ¿µ¹®ÀÚ¿Í ¼ýÀÚ, '_' ¸¸ »ç¿ëÇÏ½Ç ¼ö ÀÖ½À´Ï´Ù.";
		}
		
		// Rule 2 Check
		if ("".equals(errorCode)) {
			
			String compareStr = "[Ç×¸ñ»ý¼º½Ã ¾à¾î»ç¿ë ¿ë¾î]";
			
			if ("Y".equals(useAbbrYn)) {
				if (utwDef.length() >= compareStr.length()) {
					if (!(compareStr.equals(utwDef.subSequence(0, compareStr.length())))) {
						errorCode = "40";
						errorMsg = "¾à¾î»ç¿ëÇÊ¼ö¿©ºÎ°¡ 'Y' ÀÎ °æ¿ì, ¿ë¾îÁ¤ÀÇ°¡ '" + compareStr + "'·Î ½ÃÀÛÇØ¾ßÇÕ´Ï´Ù.";
					}
				} else {
					errorCode = "40";
					errorMsg = "¾à¾î»ç¿ëÇÊ¼ö¿©ºÎ°¡ 'Y' ÀÎ °æ¿ì, ¿ë¾îÁ¤ÀÇ°¡ '" + compareStr + "'·Î ½ÃÀÛÇØ¾ßÇÕ´Ï´Ù.";
				}
			} else {
				if (utwDef.length() >= compareStr.length()) {
					if (compareStr.equals(utwDef.subSequence(0, compareStr.length()))) {
						errorCode = "41";
						errorMsg = "¾à¾î»ç¿ëÇÊ¼ö¿©ºÎ°¡ 'N' ÀÎ °æ¿ì, ¿ë¾îÁ¤ÀÇ°¡ '" + compareStr + "'·Î ½ÃÀÛÇÏ¸é ¾ÈµË´Ï´Ù.";
					}
				}
			}
		}
		
		if ("".equals(errorCode)) {
			checkSuccYN = "Y";
			// System.out.println ("¿¡·¯°¡ ¾ø½À´Ï´Ù.");
		} else {
			checkSuccYN = "N";
			//System.out.println (errorCode + "¿¡·¯");
		}

		result.put("VALID_ID", utwId);
		result.put("ERROR_ID", errorCode);
		result.put("ERROR_MSG", errorMsg);
		result.put("VALD_CHECK_SUCC_YN", checkSuccYN);
		
		return result;
	}

	/**
	 * 
	 * <pre>
	 * Ç¥ÁØ¿¡ ´ëÇÑ »ç¿ëÀÚ Á¤ÀÇ À¯È¿¼º Ã¼Å©¸¦ ÇÑ´Ù. - »èÁ¦ ½Ã
	 * </pre>
	 * @param info
	 * 	 
	 * [´Ü¾î]
	 * REQ_TP_CD		String		»ý¼º(WFGB_I), ¼öÁ¤(WFGB_U), »èÁ¦(WFGB_D)
	 * REQ_EDIT_TYPE	String		»ý¼º(), ¼öÁ¤(UPDATE), »èÁ¦(DELETE)
	 * UTW_DIC_ID		String		´Ü¾î»çÀüID
	 * UTW_ID			String		´Ü¾î ID
	 * DAT_STRC_ID		String		µ¥ÀÌÅÍ ±¸Á¶ ID
	 * UTW_NM			String		ÇÑ±Û ¸í
	 * ABBR				String		¾à¾î
	 * EN_NM			String		¿µ¹® ¸í
	 * UTW_TP_CD		String		´Ü¾î À¯Çü ÄÚµå
	 * UTW_DEF			String		Á¤ÀÇ
	 * 
	 * @return
	 * @throws Exception
	 */			
	public HashMap checkDeleteValid(HashMap info) throws Exception {		
		HashMap result = new HashMap();

		result.put("VALID_ID", null);
		result.put("ERROR_ID", null);
		result.put("ERROR_MSG", null);
		result.put("VALD_CHECK_SUCC_YN", "Y");
		
		return result;
	}

}
