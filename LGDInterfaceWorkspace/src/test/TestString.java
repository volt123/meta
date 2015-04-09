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
		
		String utw_kor_nm = "ÇÑ±Û°ø¹éEng12";
		String utw_eng_nm = "Korean_Blank12";
		String utw_abbr = "KORBL12_k";
		String utw_def = "[Ç×¸ñ»ý¼º½Ã ¾à¾î»ç¿ë ¿ë¾î] ÀÎ¼âÈ¸·Î±âÆÇ. ±¸¸® ¹è¼±ÀÌ °¡´Ã°Ô ÀÎ¼âµÈ ÆÇÀ¸·Î, ¹ÝµµÃ¼¡¤ÄÁµ§¼­¡¤ ÀúÇ× µî °¢Á¾ ºÎÇ°À» ³¢¿ï¼ö ÀÖµµ·Ï µÅ ÀÖ¾î ºÎÇ° »óÈ£°£À» ¿¬°á½ÃÅ°´Â ±¸½ÇÀ» ÇÏ´Â ÀüÀÚ ºÎÇ°";
		String useAbbrYn = "Y";
		
		// Check 1
		Pattern p = Pattern.compile("\\W");
		Pattern p_kor = Pattern.compile("[^a-zA-Z0-9°¡-ÆR_]");
		
		if ("".equals(utw_kor_nm)) {
			errorCode = "11";
			errorMsg = "¿ë¾î¸íÀ» ÀÔ·ÂÇÏ¼Å¾ß ÇÕ´Ï´Ù.";
		} else if (utw_kor_nm.matches(".* .*")) {
			errorCode = "12";
			errorMsg = "¿ë¾î¸í¿¡ °ø¹éÀÌ Çã¿ëµÇÁö ¾Ê½À´Ï´Ù.";
		} else if (p_kor.matcher(utw_kor_nm).find()) {
			errorCode = "13";
			errorMsg = "¿ë¾î¸í¿¡´Â ÇÑ±Û°ú ¿µ¹®ÀÚ, ¼ýÀÚ, '_' ¸¸ »ç¿ëÇÏ½Ç ¼ö ÀÖ½À´Ï´Ù.";
		}

		if ("".equals(utw_eng_nm)) {
			errorCode = "21";
			errorMsg = "¿µ¹®¸íÀ» ÀÔ·ÂÇÏ¼Å¾ß ÇÕ´Ï´Ù.";
		} else if (utw_eng_nm.matches(".* .*")) {
			errorCode = "22";
			errorMsg = "¿µ¹®¸í¿¡ °ø¹éÀÌ Çã¿ëµÇÁö ¾Ê½À´Ï´Ù.";
		} else if (p.matcher(utw_eng_nm).find()) {
			errorCode = "23";
			errorMsg = "¿µ¹®¸í¿¡´Â ¿µ¹®ÀÚ¿Í ¼ýÀÚ, '_' ¸¸ »ç¿ëÇÏ½Ç ¼ö ÀÖ½À´Ï´Ù.";
		}

		if ("".equals(utw_abbr)) {
			errorCode = "31";
			errorMsg = "¾à¾î¸¦ ÀÔ·ÂÇÏ¼Å¾ß ÇÕ´Ï´Ù.";
		} else if (utw_abbr.matches(".* .*")) {
			errorCode = "32";
			errorMsg = "¾à¾î¿¡ °ø¹éÀÌ Çã¿ëµÇÁö ¾Ê½À´Ï´Ù.";
		} else if (p.matcher(utw_abbr).find()) {
			errorCode = "33";
			errorMsg = "¾à¾î¿¡´Â ¿µ¹®ÀÚ¿Í ¼ýÀÚ, '_' ¸¸ »ç¿ëÇÏ½Ç ¼ö ÀÖ½À´Ï´Ù.";
		}

		if ("".equals(errorCode)) {
			System.out.println ("¿¡·¯°¡ ¾ø½À´Ï´Ù.");
		} else {
			System.out.println (errorCode + "¿¡·¯ : " + errorMsg);
		}
	}
	
	public void testCheckString2() {

		String errorCode = "";
		String errorMsg = "";
		
		String utw_def = "1[Ç×¸ñ»ý¼º½Ã ¾à¾î»ç¿ë ¿ë¾î] ÀÎ¼âÈ¸·Î±âÆÇ. ±¸¸® ¹è¼±ÀÌ °¡´Ã°Ô ÀÎ¼âµÈ ÆÇÀ¸·Î, ¹ÝµµÃ¼¡¤ÄÁµ§¼­¡¤ ÀúÇ× µî °¢Á¾ ºÎÇ°À» ³¢¿ï¼ö ÀÖµµ·Ï µÅ ÀÖ¾î ºÎÇ° »óÈ£°£À» ¿¬°á½ÃÅ°´Â ±¸½ÇÀ» ÇÏ´Â ÀüÀÚ ºÎÇ°";
		String useAbbrYn = "Y";
		
		// Rule 2 Check
		String compareStr = "[Ç×¸ñ»ý¼º½Ã ¾à¾î»ç¿ë ¿ë¾î]";
		
		if ("Y".equals(useAbbrYn)) {
			if (utw_def.length() >= compareStr.length()) {
				if (!(compareStr.equals(utw_def.subSequence(0, compareStr.length())))) {
					errorCode = "40";
					errorMsg = "¾à¾î»ç¿ëÇÊ¼ö¿©ºÎ°¡ 'Y' ÀÎ °æ¿ì, ¿ë¾îÁ¤ÀÇ°¡ '" + compareStr + "'·Î ½ÃÀÛÇØ¾ßÇÕ´Ï´Ù.";
				}
			} else {
				errorCode = "40";
				errorMsg = "¾à¾î»ç¿ëÇÊ¼ö¿©ºÎ°¡ 'Y' ÀÎ °æ¿ì, ¿ë¾îÁ¤ÀÇ°¡ '" + compareStr + "'·Î ½ÃÀÛÇØ¾ßÇÕ´Ï´Ù.";
			}
		} else {
			if (utw_def.length() >= compareStr.length()) {
				if (compareStr.equals(utw_def.subSequence(0, compareStr.length()))) {
					errorCode = "41";
					errorMsg = "¾à¾î»ç¿ëÇÊ¼ö¿©ºÎ°¡ 'N' ÀÎ °æ¿ì, ¿ë¾îÁ¤ÀÇ°¡ '" + compareStr + "'·Î ½ÃÀÛÇÏ¸é ¾ÈµË´Ï´Ù.";
				}
			}
		}
		
		if ("".equals(errorCode)) {
			System.out.println ("¿¡·¯°¡ ¾ø½À´Ï´Ù.");
		} else {
			System.out.println (errorCode + "¿¡·¯ : " + errorMsg);
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
