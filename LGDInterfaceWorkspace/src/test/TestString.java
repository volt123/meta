package test;

import java.util.regex.Pattern;

import org.junit.Test;

import com.lgdisplay.db.LgdDao;

public class TestString {

	@Test
	public void test() {
		//testCheckString1();
		testCheckString2();
		//testIsDigit();
	}
	
	public void testCheckString1() {

		String errorCode = "";
		String errorMsg = "";
		
		String utw_kor_nm = "�ѱ۰���Eng12";
		String utw_eng_nm = "Korean_Blank12";
		String utw_abbr = "KORBL12_k";
		String utw_def = "[�׸������ ����� ���] �μ�ȸ�α���. ���� �輱�� ���ð� �μ�� ������, �ݵ�ü���������� ���� �� ���� ��ǰ�� ����� �ֵ��� �� �־� ��ǰ ��ȣ���� �����Ű�� ������ �ϴ� ���� ��ǰ";
		String useAbbrYn = "Y";
		
		// Check 1
		Pattern p = Pattern.compile("\\W");
		Pattern p_kor = Pattern.compile("[^a-zA-Z0-9��-�R_]");
		
		if ("".equals(utw_kor_nm)) {
			errorCode = "11";
			errorMsg = "������ �Է��ϼž� �մϴ�.";
		} else if (utw_kor_nm.matches(".* .*")) {
			errorCode = "12";
			errorMsg = "���� ������ ������ �ʽ��ϴ�.";
		} else if (p_kor.matcher(utw_kor_nm).find()) {
			errorCode = "13";
			errorMsg = "������ �ѱ۰� ������, ����, '_' �� ����Ͻ� �� �ֽ��ϴ�.";
		}

		if ("".equals(utw_eng_nm)) {
			errorCode = "21";
			errorMsg = "�������� �Է��ϼž� �մϴ�.";
		} else if (utw_eng_nm.matches(".* .*")) {
			errorCode = "22";
			errorMsg = "������ ������ ������ �ʽ��ϴ�.";
		} else if (p.matcher(utw_eng_nm).find()) {
			errorCode = "23";
			errorMsg = "�������� �����ڿ� ����, '_' �� ����Ͻ� �� �ֽ��ϴ�.";
		}

		if ("".equals(utw_abbr)) {
			errorCode = "31";
			errorMsg = "�� �Է��ϼž� �մϴ�.";
		} else if (utw_abbr.matches(".* .*")) {
			errorCode = "32";
			errorMsg = "�� ������ ������ �ʽ��ϴ�.";
		} else if (p.matcher(utw_abbr).find()) {
			errorCode = "33";
			errorMsg = "���� �����ڿ� ����, '_' �� ����Ͻ� �� �ֽ��ϴ�.";
		}

		if ("".equals(errorCode)) {
			System.out.println ("������ �����ϴ�.");
		} else {
			System.out.println (errorCode + "���� : " + errorMsg);
		}
	}
	
	public void testCheckString2() {

		String errorCode = "";
		String errorMsg = "";
		
		String utw_def = "1[�׸������ ����� ���] �μ�ȸ�α���. ���� �輱�� ���ð� �μ�� ������, �ݵ�ü���������� ���� �� ���� ��ǰ�� ����� �ֵ��� �� �־� ��ǰ ��ȣ���� �����Ű�� ������ �ϴ� ���� ��ǰ";
		String useAbbrYn = "Y";
		
		// Rule 2 Check
		String compareStr = "[�׸������ ����� ���]";
		
		if ("Y".equals(useAbbrYn)) {
			if (utw_def.length() >= compareStr.length()) {
				if (!(compareStr.equals(utw_def.subSequence(0, compareStr.length())))) {
					errorCode = "40";
					errorMsg = "������ʼ����ΰ� 'Y' �� ���, ������ǰ� '" + compareStr + "'�� �����ؾ��մϴ�.";
				}
			} else {
				errorCode = "40";
				errorMsg = "������ʼ����ΰ� 'Y' �� ���, ������ǰ� '" + compareStr + "'�� �����ؾ��մϴ�.";
			}
		} else {
			if (utw_def.length() >= compareStr.length()) {
				if (compareStr.equals(utw_def.subSequence(0, compareStr.length()))) {
					errorCode = "41";
					errorMsg = "������ʼ����ΰ� 'N' �� ���, ������ǰ� '" + compareStr + "'�� �����ϸ� �ȵ˴ϴ�.";
				}
			}
		}
		
		if ("".equals(errorCode)) {
			System.out.println ("������ �����ϴ�.");
		} else {
			System.out.println (errorCode + "���� : " + errorMsg);
		}
	}

	public void testIsDigit() {
		String termFullPhscNm = "1stTerm";
		
		if (Character.isDigit(termFullPhscNm.charAt(0))) {
			System.out.println("** " + termFullPhscNm + "' first letter is number.");
		} else {
			System.out.println("** " + termFullPhscNm + "' first letter is not number.");
		}
	}
	
}
