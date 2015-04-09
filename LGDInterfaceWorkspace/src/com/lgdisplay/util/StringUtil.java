package com.lgdisplay.util;

import java.io.IOException;
import java.io.Reader;

public class StringUtil {
	
	public static String EMPTY = "";
	
	/**
	 * 문자열 byte 단위로 자르기
	 * @param str 문자열
	 * @param endIndex 인덱스
	 * @return
	 */
	public static String getByteString(String str, int endIndex) {
		
		if ( str == null || "".equals(str) )
			return "";
		
		int total = 0;
		StringBuffer sb = new StringBuffer(endIndex);
		char[] c = str.toCharArray();
		
		for ( int k=0; k<c.length; k++ ) {
			total += String.valueOf(c[k]).getBytes().length;
			if ( total > endIndex ) {
				break;
			}
			sb.append(c[k]);
		}
		return sb.toString();
	}

	 /**
	  * If The Input value is null, Empty
	  *
	  * <pre>
	  * StrUtils.nullToEmpty(null)               = ""
	  * StrUtils.nullToEmpty("kore")            = "kore"
	  * StrUtils.nullToEmpty(" ")                  = " "
	  * StrUtils.nullToEmpty("")                   = ""
	  * </pre>
	  *
	  * @param s
	  * @return
	  */
	 public static String nullToEmpty(String s) {
	 	if ( null ==s ) return EMPTY;
	 	else return s;
	 }

	/**
	 * <p></p>
	 * <pre>
	 * StrUtils.lpadNum("123", 4)     = "0123"
	 * StrUtils.lpadNum(" 123", 5)    = "00123"
	 * StrUtils.lpadNum(" 123 ", 6)   = "000123"
	 * StrUtils.lpadNum("", 5)          = "00000"
	 * StrUtils.lpadNum(null, 5)       = "00000"
	 * StrUtils.lpadNum("123", 0)       = "123"
	 * StrUtils.lpadNum(" 123", 0)     = " 123"
	 * StrUtils.lpadNum(" 123", 2)     = " 123"
	 * StrUtils.lpadNum("123".3)       = "123"
	 * StrUtils.lpadNum(null, 0)        = null
	 * StrUtils.lpadNum(null, -1)      = null
	 * StrUtils.lpadNum("", -1)          = ""
	 * StrUtils.lpadNum(" 123", -1)     = " 123"
	 * StrUtils.lpadNum("", 0)            = ""
	 * </pre>
	 * @param s
	 * @param sz
	 * @return
	 */
	public static String lpadNum(String s, int sz ) {
		if ( 0 >= sz ) return s;
		if ( null == s ) return full(s, '0', sz);
		if ( sz < s.getBytes().length ) return s;

		byte[] bt = new byte[sz];
		byte[] bs = s.trim().getBytes();

		for ( int b = 0; b < sz; b++ ) {
			if ( b < bs.length ) 	bt[b] = bs[b];
			else bt[b] = '0';
		}
		return new String(bt);
	}

	/**
	 * <p></p>
	 *
	 * <pre>
	 * StrUtils.full("kor", '1', 5)         = "kor11"
	 * StrUtils.full("eng", ' ', 6)         = "eng   "
	 * StrUtils.full("", '0', 4)             = "0000"
	 * StrUtils.full("한글", '1', 5)         = "한글1"
	 * StrUtils.full("한글", '1', 4)         = "한글"
	 * StrUtils.full("", ' ', 4)              = "    "
	 * StrUtils.full(null, '0', 4)          = "    "
	 * </pre>
	 * @param org
	 * @param rplc
	 * @param sz
	 * @return
	 */
	public static String full(String org, char rplc, int sz ) {
		org = (null == org) ? "":org;
		for ( int i = 0; i < sz; i++ ) org += rplc;
		return org;
	}
	/**
	 * <p></p>
	 *
	 * <pre>
	 * StrUtils.lpadNum("123", 4)     = "1230"
	 * StrUtils.lpadNum(" 123", 5)    = "12300"
	 * StrUtils.lpadNum(" 123 ", 6)   = "123000"
	 * StrUtils.lpadNum("", 5)          = "00000"
	 * StrUtils.lpadNum(null, 5)       = "00000"
	 * StrUtils.lpadNum("123", 0)       = "123"
	 * StrUtils.lpadNum(" 123", 0)     = " 123"
	 * StrUtils.lpadNum(" 123", 2)     = " 123"
	 * StrUtils.lpadNum("123".3)       = "123"
	 * StrUtils.lpadNum(null, 0)        = null
	 * StrUtils.lpadNum(null, -1)      = null
	 * StrUtils.lpadNum("", -1)          = ""
	 * StrUtils.lpadNum(" 123", -1)     = " 123"
	 * StrUtils.lpadNum("", 0)            = ""
	 * </pre>
	 *
	 * @param s
	 * @param sz
	 * @return
	 */
	public static String rpadNum(String s, int sz ) {
		if ( 0 >= sz ) return s;
		if ( null == s ) return full(s, '0', sz);
		if ( sz < s.getBytes().length ) return s;

		byte[] bt = new byte[sz];
		byte[] bs = s.getBytes();

		byte bb = (byte) (sz - bs.length);

		if ( 0 > bb ) return s;

		for ( int c = 0; c < bb; c++ )  bt[c] = '0';
		for ( int c = 0; c < bs.length; c++ ) bt[c] = bs[c];

		return new String(bt);
	}
	
	/**
	 * change String to int, otherwise 0
	 * <pre>
	 * StrUtils.toInt("123")                 = 123
	 * StrUtils.toInt("10")                    = 10
	 * StrUtils.toInt("")          			= 0
	 * StrUtils.toInt(null)          			= 0
	 * StrUtils.toInt("  123")      			= 123
	 * StrUtils.toInt("123   ")     			= 123
	 * StrUtils.toInt("0  0")    				= throws ValidationException
	 * StrUtils.toInt("k123")   			= throws ValidationException
	 * </pre>
	 *
	 * @param s
	 * @return
	 * @throws ValidationException
	 */
	public static int toInt(String s) throws Exception {
		return toInteger(s).intValue();
	}
	
	/**
	 * <pre>change String to Integer, otherwise new Integer(0).
	 * String type of Null or Empty is new Integer(0), String type of Number is Integer, Otherwise, case SpecialChar is throw ValidationException
	 * </pre>
	 * <pre>
	 * StrUtils.toInteger("1234")        = Integer(1234)
	 * StrUtils.toInteger("")          = Integer(0)
	 * StrUtils.toInteger(null)          = Integer(0)
	 * StrUtils.toInteger("  456")      = Integer(456)
	 * StrUtils.toInteger("234   ")     = Integer(234)
	 * StrUtils.toInteger("0  0")    = throws ValidationException
	 * StrUtils.toInteger("k123")   = throws ValidationException
	 * </pre>
	 * @param s
	 * @return
	 * @throws ValidationException
	 */
	public static Integer toInteger(String s) throws Exception {
		if ( null == s ) return new Integer(0);
		if ( 0 >= s.trim().length() ) return new Integer(0);

		try {
			return new Integer(s.trim());
		} catch (NumberFormatException e) {
			throw new Exception(e);
		}
	}
	
	public static String readClobData(Reader reader) throws IOException {
        StringBuffer data = new StringBuffer();
        char[] buf = new char[1024];
        int cnt = 0;
        if (null != reader) {
            while ( (cnt = reader.read(buf)) != -1) {
                data.append(buf, 0, cnt);
            }
        }
        return data.toString();
    }
	
	 /**
	  * <p>Checks if a String is empty ("") or null.</p>
	  *
	  * <pre>
	  * StrUtils.isEmpty(null)      = true
	  * StrUtils.isEmpty("")        = true
	  * StrUtils.isEmpty(" ")       = false
	  * StrUtils.isEmpty("bob")     = false
	  * StrUtils.isEmpty("  bob  ") = false
	  * </pre>
	  *
	  * <p>NOTE: This method changed in Lang version 2.0.
	  * It no longer trims the String.
	  * That functionality is available in isBlank().</p>
	  *
	  * @param str  the String to check, may be null
	  * @return <code>true</code> if the String is empty or null
	  */
	 public static boolean isEmpty(String str) {
		 return str == null || str.length() == 0;
	 }
	 
	 public static boolean isEmpty(Object o) {
		 return o == null || o.toString().trim().length() == 0;
	 }
	 
	 /**
	  * <p>Checks if a String is empty ("") or null.</p>
	  *
	  * <pre>
	  * StrUtils.isEmpty(null)      = true
	  * StrUtils.isEmpty("")        = true
	  * StrUtils.isEmpty(" ")       = true
	  * StrUtils.isEmpty("bob")     = false
	  * StrUtils.isEmpty("  bob  ") = false
	  * </pre>
	  *
	  * <p>NOTE: This method changed in Lang version 2.0.
	  * It no longer trims the String.
	  * That functionality is available in isBlank().</p>
	  *
	  * @param str  the String to check, may be null
	  * @return <code>true</code> if the String is empty or null
	  */
	 public static boolean isEmpty2(String str) {
		 return str == null || str.length() == 0;
	 }
	 
	 public static String substring(String str, int beginIndex, int endIndex) {
		 if ( isEmpty2 (str) ) return "";
		 int len = str.trim().length();
		 if ( len >= beginIndex && len <= endIndex ) {
			 return str.substring(beginIndex, endIndex);
		 } else return "";
	 }

	 /**
	  * <p>Checks if a String is Integer.</p>
	  *
	  * <pre>
	  * StrUtils.isInteger(null)   = false
	  * StrUtils.isInteger("")     = false
	  * StrUtils.isEmpty("12")     = true
	  * StrUtils.isEmpty("week12") = false
	  * </pre>
	  */
	 public static boolean isInteger(String str) {
		 try {
			 Integer.parseInt(str);
			 return true;
		 } 
		 catch (Exception e) {
			 return false;
		 }
	 }

	 /**
	  * If The Input value is null, replacement's value for return
	  *
	  * <pre>
	  * StrUtils.defaultString(null,"1")               = "1"
	  * StrUtils.defaultString("kore","")            = "kore"
	  * StrUtils.defaultString(" ","")                  = " "
	  * StrUtils.defaultString("","")                   = ""
	  * </pre>
	  *
	  * @param s
	  * @return
	  */
	 public static String defaultString(String s,String rplc) {
	 	if ( null ==s ) return rplc;
	 	else if ( 0 == s.trim().length() ) return rplc;
	 	else return s;
	 }
}
