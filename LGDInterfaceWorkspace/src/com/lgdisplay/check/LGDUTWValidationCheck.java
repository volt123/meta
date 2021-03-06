package com.lgdisplay.check;

import com.itplus.mm.server.datadic.AbstractValidationCheck;
import com.lgdisplay.db.LgdDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class LGDUTWValidationCheck extends AbstractValidationCheck {

	/**
	 * 
	 * <pre>
	 * 표준에 대한 사용자 정의 유효성 체크를 한다. - 신규 시
	 * </pre>
	 * @param info
	 * 	 
	 * [단어]
	 * REQ_TP_CD		String		생성(WFGB_I), 수정(WFGB_U), 삭제(WFGB_D)
	 * REQ_EDIT_TYPE	String		생성(), 수정(UPDATE), 삭제(DELETE)
	 * UTW_DIC_ID		String		단어사전ID
	 * UTW_ID			String		단어 ID
	 * DAT_STRC_ID		String		데이터 구조 ID
	 * UTW_NM			String		한글 명
	 * ABBR				String		약어
	 * EN_NM			String		영문 명
	 * UTW_TP_CD		String		단어 유형 코드
	 * UTW_DEF			String		정의
	 * 
	 * @return
	 * @throws Exception
	 */	

	/*
     * LGD 용어 체크 Rule
     * 
	 *    Rule 1. 용어명, 영문명, 약어 필수체크, 공백체크, 특수문자사용 못하게 체크
	 *    Rule 2. UDP의 약어필수 항목을 읽어와서, 
	 *        'Y' 인 경우, 용어설명에 '[항목생성시 약어사용 필수]' 내용이 있는지 확인
	 *        'N' 인 경우, 용어설명에 '[항목생성시 약어사용 필수]' 내용이 없는지 확인
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
		Pattern p_kor = Pattern.compile("[^a-zA-Z0-9가-힣_]");
		
		if ("".equals(utwKorNm)) {
			errorCode = "11";
			errorMsg = "용어명을 입력하셔야 합니다.";
		} else if (utwKorNm.matches(".* .*")) {
			errorCode = "12";
			errorMsg = "용어명에 공백이 허용되지 않습니다.";
		} else if (p_kor.matcher(utwKorNm).find()) {
			errorCode = "13";
			errorMsg = "용어명에는 한글과 영문자, 숫자, '_' 만 사용하실 수 있습니다.";
		}

		if ("".equals(utwEngNm)) {
			errorCode = "21";
			errorMsg = "영문명을 입력하셔야 합니다.";
		} else if (utwEngNm.matches(".* .*")) {
			errorCode = "22";
			errorMsg = "영문명에 공백이 허용되지 않습니다.";
		} else if (p.matcher(utwEngNm).find()) {
			errorCode = "23";
			errorMsg = "영문명에는 영문자와 숫자, '_' 만 사용하실 수 있습니다.";
		}

		if ("".equals(utwAbbr)) {
			errorCode = "31";
			errorMsg = "약어를 입력하셔야 합니다.";
		} else if (utwAbbr.matches(".* .*")) {
			errorCode = "32";
			errorMsg = "약어에 공백이 허용되지 않습니다.";
		} else if (p.matcher(utwAbbr).find()) {
			errorCode = "33";
			errorMsg = "약어에는 영문자와 숫자, '_' 만 사용하실 수 있습니다.";
		}
		
		// Rule 2 Check
		if ("".equals(errorCode)) {
			
			String compareStr = "[항목생성시 약어사용 용어]";
			
			if ("Y".equals(useAbbrYn)) {
				if (utwDef.length() >= compareStr.length()) {
					if (!(compareStr.equals(utwDef.subSequence(0, compareStr.length())))) {
						errorCode = "40";
						errorMsg = "약어사용필수여부가 'Y' 인 경우, 용어정의가 '" + compareStr + "'로 시작해야합니다.";
					}
				} else {
					errorCode = "40";
					errorMsg = "약어사용필수여부가 'Y' 인 경우, 용어정의가 '" + compareStr + "'로 시작해야합니다.";
				}
			} else {
				if (utwDef.length() >= compareStr.length()) {
					if (compareStr.equals(utwDef.subSequence(0, compareStr.length()))) {
						errorCode = "41";
						errorMsg = "약어사용필수여부가 'N' 인 경우, 용어정의가 '" + compareStr + "'로 시작하면 안됩니다.";
					}
				}
			}
		}
		
		if ("".equals(errorCode)) {
			checkSuccYN = "Y";
			// System.out.println ("에러가 없습니다.");
		} else {
			checkSuccYN = "N";
			//System.out.println (errorCode + "에러");
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
	 * 표준에 대한 사용자 정의 유효성 체크를 한다. - 수정 시
	 * </pre>
	 * @param info
	 * 	 
	 * [단어]
	 * REQ_TP_CD		String		생성(WFGB_I), 수정(WFGB_U), 삭제(WFGB_D)
	 * REQ_EDIT_TYPE	String		생성(), 수정(UPDATE), 삭제(DELETE)
	 * UTW_DIC_ID		String		단어사전ID
	 * UTW_ID			String		단어 ID
	 * DAT_STRC_ID		String		데이터 구조 ID
	 * UTW_NM			String		한글 명
	 * ABBR				String		약어
	 * EN_NM			String		영문 명
	 * UTW_TP_CD		String		단어 유형 코드
	 * UTW_DEF			String		정의
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
		Pattern p_kor = Pattern.compile("[^a-zA-Z0-9가-힣_]");
		
		if ("".equals(utwKorNm)) {
			errorCode = "11";
			errorMsg = "용어명을 입력하셔야 합니다.";
		} else if (utwKorNm.matches(".* .*")) {
			errorCode = "12";
			errorMsg = "용어명에 공백이 허용되지 않습니다.";
		} else if (p_kor.matcher(utwKorNm).find()) {
			errorCode = "13";
			errorMsg = "용어명에는 한글과 영문자, 숫자, '_' 만 사용하실 수 있습니다.";
		}

		if ("".equals(utwEngNm)) {
			errorCode = "21";
			errorMsg = "영문명을 입력하셔야 합니다.";
		} else if (utwEngNm.matches(".* .*")) {
			errorCode = "22";
			errorMsg = "영문명에 공백이 허용되지 않습니다.";
		} else if (p.matcher(utwEngNm).find()) {
			errorCode = "23";
			errorMsg = "영문명에는 영문자와 숫자, '_' 만 사용하실 수 있습니다.";
		}

		if ("".equals(utwAbbr)) {
			errorCode = "31";
			errorMsg = "약어를 입력하셔야 합니다.";
		} else if (utwAbbr.matches(".* .*")) {
			errorCode = "32";
			errorMsg = "약어에 공백이 허용되지 않습니다.";
		} else if (p.matcher(utwAbbr).find()) {
			errorCode = "33";
			errorMsg = "약어에는 영문자와 숫자, '_' 만 사용하실 수 있습니다.";
		}
		
		// Rule 2 Check
		if ("".equals(errorCode)) {
			
			String compareStr = "[항목생성시 약어사용 용어]";
			
			if ("Y".equals(useAbbrYn)) {
				if (utwDef.length() >= compareStr.length()) {
					if (!(compareStr.equals(utwDef.subSequence(0, compareStr.length())))) {
						errorCode = "40";
						errorMsg = "약어사용필수여부가 'Y' 인 경우, 용어정의가 '" + compareStr + "'로 시작해야합니다.";
					}
				} else {
					errorCode = "40";
					errorMsg = "약어사용필수여부가 'Y' 인 경우, 용어정의가 '" + compareStr + "'로 시작해야합니다.";
				}
			} else {
				if (utwDef.length() >= compareStr.length()) {
					if (compareStr.equals(utwDef.subSequence(0, compareStr.length()))) {
						errorCode = "41";
						errorMsg = "약어사용필수여부가 'N' 인 경우, 용어정의가 '" + compareStr + "'로 시작하면 안됩니다.";
					}
				}
			}
		}
		
		if ("".equals(errorCode)) {
			checkSuccYN = "Y";
			// System.out.println ("에러가 없습니다.");
		} else {
			checkSuccYN = "N";
			//System.out.println (errorCode + "에러");
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
	 * 표준에 대한 사용자 정의 유효성 체크를 한다. - 삭제 시
	 * </pre>
	 * @param info
	 * 	 
	 * [단어]
	 * REQ_TP_CD		String		생성(WFGB_I), 수정(WFGB_U), 삭제(WFGB_D)
	 * REQ_EDIT_TYPE	String		생성(), 수정(UPDATE), 삭제(DELETE)
	 * UTW_DIC_ID		String		단어사전ID
	 * UTW_ID			String		단어 ID
	 * DAT_STRC_ID		String		데이터 구조 ID
	 * UTW_NM			String		한글 명
	 * ABBR				String		약어
	 * EN_NM			String		영문 명
	 * UTW_TP_CD		String		단어 유형 코드
	 * UTW_DEF			String		정의
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
