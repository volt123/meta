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
		
		String utw_kor_nm = "한글공백Eng12";
		String utw_eng_nm = "Korean_Blank12";
		String utw_abbr = "KORBL12_k";
		String utw_def = "[항목생성시 약어사용 용어] 인쇄회로기판. 구리 배선이 가늘게 인쇄된 판으로, 반도체·컨덴서· 저항 등 각종 부품을 끼울수 있도록 돼 있어 부품 상호간을 연결시키는 구실을 하는 전자 부품";
		String useAbbrYn = "Y";
		
		// Check 1
		Pattern p = Pattern.compile("\\W");
		Pattern p_kor = Pattern.compile("[^a-zA-Z0-9가-힣_]");
		
		if ("".equals(utw_kor_nm)) {
			errorCode = "11";
			errorMsg = "용어명을 입력하셔야 합니다.";
		} else if (utw_kor_nm.matches(".* .*")) {
			errorCode = "12";
			errorMsg = "용어명에 공백이 허용되지 않습니다.";
		} else if (p_kor.matcher(utw_kor_nm).find()) {
			errorCode = "13";
			errorMsg = "용어명에는 한글과 영문자, 숫자, '_' 만 사용하실 수 있습니다.";
		}

		if ("".equals(utw_eng_nm)) {
			errorCode = "21";
			errorMsg = "영문명을 입력하셔야 합니다.";
		} else if (utw_eng_nm.matches(".* .*")) {
			errorCode = "22";
			errorMsg = "영문명에 공백이 허용되지 않습니다.";
		} else if (p.matcher(utw_eng_nm).find()) {
			errorCode = "23";
			errorMsg = "영문명에는 영문자와 숫자, '_' 만 사용하실 수 있습니다.";
		}

		if ("".equals(utw_abbr)) {
			errorCode = "31";
			errorMsg = "약어를 입력하셔야 합니다.";
		} else if (utw_abbr.matches(".* .*")) {
			errorCode = "32";
			errorMsg = "약어에 공백이 허용되지 않습니다.";
		} else if (p.matcher(utw_abbr).find()) {
			errorCode = "33";
			errorMsg = "약어에는 영문자와 숫자, '_' 만 사용하실 수 있습니다.";
		}

		if ("".equals(errorCode)) {
			System.out.println ("에러가 없습니다.");
		} else {
			System.out.println (errorCode + "에러 : " + errorMsg);
		}
	}
	
	public void testCheckString2() {

		String errorCode = "";
		String errorMsg = "";
		
		String utw_def = "1[항목생성시 약어사용 용어] 인쇄회로기판. 구리 배선이 가늘게 인쇄된 판으로, 반도체·컨덴서· 저항 등 각종 부품을 끼울수 있도록 돼 있어 부품 상호간을 연결시키는 구실을 하는 전자 부품";
		String useAbbrYn = "Y";
		
		// Rule 2 Check
		String compareStr = "[항목생성시 약어사용 용어]";
		
		if ("Y".equals(useAbbrYn)) {
			if (utw_def.length() >= compareStr.length()) {
				if (!(compareStr.equals(utw_def.subSequence(0, compareStr.length())))) {
					errorCode = "40";
					errorMsg = "약어사용필수여부가 'Y' 인 경우, 용어정의가 '" + compareStr + "'로 시작해야합니다.";
				}
			} else {
				errorCode = "40";
				errorMsg = "약어사용필수여부가 'Y' 인 경우, 용어정의가 '" + compareStr + "'로 시작해야합니다.";
			}
		} else {
			if (utw_def.length() >= compareStr.length()) {
				if (compareStr.equals(utw_def.subSequence(0, compareStr.length()))) {
					errorCode = "41";
					errorMsg = "약어사용필수여부가 'N' 인 경우, 용어정의가 '" + compareStr + "'로 시작하면 안됩니다.";
				}
			}
		}
		
		if ("".equals(errorCode)) {
			System.out.println ("에러가 없습니다.");
		} else {
			System.out.println (errorCode + "에러 : " + errorMsg);
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
