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
	 * �����Ÿ�� ���� ����� ���� ��ȿ�� üũ�� �Ѵ�. - �ű� ��
	 * </pre>
	 * @param info
	 * 	 
	 * [�ܾ�]
	 * REQ_TP_CD		String		����(WFGB_I), ����(WFGB_U), ����(WFGB_D)
	 * REQ_EDIT_TYPE	String		����(), ����(UPDATE), ����(DELETE)
	 * UTW_DIC_ID		String		�ܾ����ID
	 * UTW_ID			String		�ܾ� ID
	 * DAT_STRC_ID		String		������ ���� ID
	 * UTW_NM			String		�ѱ� ��
	 * ABBR				String		���
	 * EN_NM			String		���� ��
	 * UTW_TP_CD		String		�ܾ� ���� �ڵ�
	 * UTW_DEF			String		����
	 * 
	 * @return
	 * @throws Exception
	 */	

	/*
     * LGD ��� üũ Rule
     * 
	 *    Rule 1. ����, ������, ��� �ʼ�üũ, ����üũ, Ư�����ڻ�� ���ϰ� üũ
	 *    Rule 2. UDP�� ����ʼ� �׸��� �о�ͼ�, 
	 *        'Y' �� ���, ���� '[�׸������ ����� �ʼ�]' ������ �ִ��� Ȯ��
	 *        'N' �� ���, ���� '[�׸������ ����� �ʼ�]' ������ ������ Ȯ��
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
		Pattern p_kor = Pattern.compile("[^a-zA-Z0-9��-�R_]");
		
		if ("".equals(utwKorNm)) {
			errorCode = "11";
			errorMsg = "������ �Է��ϼž� �մϴ�.";
		} else if (utwKorNm.matches(".* .*")) {
			errorCode = "12";
			errorMsg = "���� ������ ������ �ʽ��ϴ�.";
		} else if (p_kor.matcher(utwKorNm).find()) {
			errorCode = "13";
			errorMsg = "������ �ѱ۰� ������, ����, '_' �� ����Ͻ� �� �ֽ��ϴ�.";
		}

		if ("".equals(utwEngNm)) {
			errorCode = "21";
			errorMsg = "�������� �Է��ϼž� �մϴ�.";
		} else if (utwEngNm.matches(".* .*")) {
			errorCode = "22";
			errorMsg = "������ ������ ������ �ʽ��ϴ�.";
		} else if (p.matcher(utwEngNm).find()) {
			errorCode = "23";
			errorMsg = "�������� �����ڿ� ����, '_' �� ����Ͻ� �� �ֽ��ϴ�.";
		}

		if ("".equals(utwAbbr)) {
			errorCode = "31";
			errorMsg = "�� �Է��ϼž� �մϴ�.";
		} else if (utwAbbr.matches(".* .*")) {
			errorCode = "32";
			errorMsg = "�� ������ ������ �ʽ��ϴ�.";
		} else if (p.matcher(utwAbbr).find()) {
			errorCode = "33";
			errorMsg = "���� �����ڿ� ����, '_' �� ����Ͻ� �� �ֽ��ϴ�.";
		}
		
		// Rule 2 Check
		if ("".equals(errorCode)) {
			
			String compareStr = "[�׸������ ����� ���]";
			
			if ("Y".equals(useAbbrYn)) {
				if (utwDef.length() >= compareStr.length()) {
					if (!(compareStr.equals(utwDef.subSequence(0, compareStr.length())))) {
						errorCode = "40";
						errorMsg = "������ʼ����ΰ� 'Y' �� ���, ������ǰ� '" + compareStr + "'�� �����ؾ��մϴ�.";
					}
				} else {
					errorCode = "40";
					errorMsg = "������ʼ����ΰ� 'Y' �� ���, ������ǰ� '" + compareStr + "'�� �����ؾ��մϴ�.";
				}
			} else {
				if (utwDef.length() >= compareStr.length()) {
					if (compareStr.equals(utwDef.subSequence(0, compareStr.length()))) {
						errorCode = "41";
						errorMsg = "������ʼ����ΰ� 'N' �� ���, ������ǰ� '" + compareStr + "'�� �����ϸ� �ȵ˴ϴ�.";
					}
				}
			}
		}
		
		if ("".equals(errorCode)) {
			checkSuccYN = "Y";
			// System.out.println ("������ �����ϴ�.");
		} else {
			checkSuccYN = "N";
			//System.out.println (errorCode + "����");
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
	 * ǥ�ؿ� ���� ����� ���� ��ȿ�� üũ�� �Ѵ�. - ���� ��
	 * </pre>
	 * @param info
	 * 	 
	 * [�ܾ�]
	 * REQ_TP_CD		String		����(WFGB_I), ����(WFGB_U), ����(WFGB_D)
	 * REQ_EDIT_TYPE	String		����(), ����(UPDATE), ����(DELETE)
	 * UTW_DIC_ID		String		�ܾ����ID
	 * UTW_ID			String		�ܾ� ID
	 * DAT_STRC_ID		String		������ ���� ID
	 * UTW_NM			String		�ѱ� ��
	 * ABBR				String		���
	 * EN_NM			String		���� ��
	 * UTW_TP_CD		String		�ܾ� ���� �ڵ�
	 * UTW_DEF			String		����
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
		Pattern p_kor = Pattern.compile("[^a-zA-Z0-9��-�R_]");
		
		if ("".equals(utwKorNm)) {
			errorCode = "11";
			errorMsg = "������ �Է��ϼž� �մϴ�.";
		} else if (utwKorNm.matches(".* .*")) {
			errorCode = "12";
			errorMsg = "���� ������ ������ �ʽ��ϴ�.";
		} else if (p_kor.matcher(utwKorNm).find()) {
			errorCode = "13";
			errorMsg = "������ �ѱ۰� ������, ����, '_' �� ����Ͻ� �� �ֽ��ϴ�.";
		}

		if ("".equals(utwEngNm)) {
			errorCode = "21";
			errorMsg = "�������� �Է��ϼž� �մϴ�.";
		} else if (utwEngNm.matches(".* .*")) {
			errorCode = "22";
			errorMsg = "������ ������ ������ �ʽ��ϴ�.";
		} else if (p.matcher(utwEngNm).find()) {
			errorCode = "23";
			errorMsg = "�������� �����ڿ� ����, '_' �� ����Ͻ� �� �ֽ��ϴ�.";
		}

		if ("".equals(utwAbbr)) {
			errorCode = "31";
			errorMsg = "�� �Է��ϼž� �մϴ�.";
		} else if (utwAbbr.matches(".* .*")) {
			errorCode = "32";
			errorMsg = "�� ������ ������ �ʽ��ϴ�.";
		} else if (p.matcher(utwAbbr).find()) {
			errorCode = "33";
			errorMsg = "���� �����ڿ� ����, '_' �� ����Ͻ� �� �ֽ��ϴ�.";
		}
		
		// Rule 2 Check
		if ("".equals(errorCode)) {
			
			String compareStr = "[�׸������ ����� ���]";
			
			if ("Y".equals(useAbbrYn)) {
				if (utwDef.length() >= compareStr.length()) {
					if (!(compareStr.equals(utwDef.subSequence(0, compareStr.length())))) {
						errorCode = "40";
						errorMsg = "������ʼ����ΰ� 'Y' �� ���, ������ǰ� '" + compareStr + "'�� �����ؾ��մϴ�.";
					}
				} else {
					errorCode = "40";
					errorMsg = "������ʼ����ΰ� 'Y' �� ���, ������ǰ� '" + compareStr + "'�� �����ؾ��մϴ�.";
				}
			} else {
				if (utwDef.length() >= compareStr.length()) {
					if (compareStr.equals(utwDef.subSequence(0, compareStr.length()))) {
						errorCode = "41";
						errorMsg = "������ʼ����ΰ� 'N' �� ���, ������ǰ� '" + compareStr + "'�� �����ϸ� �ȵ˴ϴ�.";
					}
				}
			}
		}
		
		if ("".equals(errorCode)) {
			checkSuccYN = "Y";
			// System.out.println ("������ �����ϴ�.");
		} else {
			checkSuccYN = "N";
			//System.out.println (errorCode + "����");
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
	 * ǥ�ؿ� ���� ����� ���� ��ȿ�� üũ�� �Ѵ�. - ���� ��
	 * </pre>
	 * @param info
	 * 	 
	 * [�ܾ�]
	 * REQ_TP_CD		String		����(WFGB_I), ����(WFGB_U), ����(WFGB_D)
	 * REQ_EDIT_TYPE	String		����(), ����(UPDATE), ����(DELETE)
	 * UTW_DIC_ID		String		�ܾ����ID
	 * UTW_ID			String		�ܾ� ID
	 * DAT_STRC_ID		String		������ ���� ID
	 * UTW_NM			String		�ѱ� ��
	 * ABBR				String		���
	 * EN_NM			String		���� ��
	 * UTW_TP_CD		String		�ܾ� ���� �ڵ�
	 * UTW_DEF			String		����
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
