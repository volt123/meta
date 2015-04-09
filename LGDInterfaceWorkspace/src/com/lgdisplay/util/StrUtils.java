package com.lgdisplay.util;

/*
 * 작성된 날짜: 2006-10-20
 *
 * TODO 생성된 파일에 대한 템플리트를 변경하려면 다음으로 이동하십시오.
 * 창 - 환경 설정 - Java - 코드 스타일 - 코드 템플리트
 */

import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;


/**
 * @author db2admin
 *
 * TODO 생성된 유형 주석에 대한 템플리트를 변경하려면 다음으로 이동하십시오.
 * 창 - 환경 설정 - Java - 코드 스타일 - 코드 템플리트
 */
public class StrUtils {

	public static String EMPTY = "";
	/**
	 * The fornt and back of Text is cutted to Empty Text
	 * <pre>
	 * StrUtils.trim(" kor ") => "kor"
	 * StrUtils.trim("kor ") => "kor"
	 * StrUtils.trim(" kor") => "kor"
	 * StrUtils.trim("kor") => "kor"
	 * StrUtils.trim(" k o r ") => "k o r"
	 * StrUtils.trim(null) => ""
	 * </pre>
	 * @param s
	 * @return
	 */
	public static String trim(String s) {
		return nullToEmpty(s).trim();
	}
	/**
	 * The fornt and back of Object's Text is cutted to Empty Text
	 * <pre>
	 * StrUtils.trim(" kor ") => "kor"
	 * StrUtils.trim("kor ") => "kor"
	 * StrUtils.trim(" kor") => "kor"
	 * StrUtils.trim("kor") => "kor"
	 * StrUtils.trim(" k o r ") => "k o r"
	 * StrUtils.trim(null) => ""
	 * </pre>
	 * @param s
	 * @return
	 */
	public static String trim(Object s) {
		return nullToEmpty(s).toString().trim();
	}

	/**
	 * The first char be changing to capitalize, otherwise return to Empty
	 * <pre>
	 * StrUtils.capitalize("kor")           = "Kor"
	 * StrUtils.capitalize(" kor")          = " kor"
	 * StrUtils.capitalize("")                = ""
	 * StrUtils.capitalize(null)            = ""
	 * StrUtils.capitalize("Kor")           = "Kor"
	 * StrUtils.capitalize("1kor")          = "1kor"
	 * </pre>
	 * @param s
	 * @return
	 */
	public static String capitalize(String s) {
		if ( null == s ) return EMPTY;
		if ( 0 >= s.trim().length() ) return EMPTY;
		String c = s.substring(0,1);
		return c.toUpperCase() + ((1==s.trim().length())?EMPTY:s.substring(1,s.length()));
	}
	/**
	 * <p></p>
	 * <pre>
	 * StrUtils.getClassName(com.yami.data.DataBean.class)                  = "DataBean"
	 * StrUtils.getClassName(null)                                                              = null
	 * </pre>
	 * @param clazz
	 * @return
	 */
	public static String getClassName(Class<?> clazz) {
		if ( null == clazz ) return null;

		String pkgname = clazz.getName();

		int lastidx = pkgname.lastIndexOf(".");
		return pkgname.substring(lastidx+1, pkgname.length());
	}
	/**
	 * change String to byte, otherwise EMPTY
	 * <pre>
	 * StrUtils.tobyte("123")                 = 123
	 * StrUtils.tobyte("10")                    = 10
	 * StrUtils.tobyte("")          			= EMPTY
	 * StrUtils.tobyte(null)          = EMPTY
	 * StrUtils.tobyte("  123")      			= 123
	 * StrUtils.tobyte("123   ")     			= 123
	 * StrUtils.tobyte("0  0")    				= throws ValidationException
	 * StrUtils.tobyte("k123")   			= throws ValidationException
	 * </pre>
	 *
	 * @param s
	 * @return
	 * @throws ValidationException
	 */
	public static byte tobyte(String s) throws ValidationException {
		return toByte(s).byteValue();
	}
	/**
	 * <pre>change String to Byte, otherwise new Byte('\0').
	 * String type of Null or Empty is new Byte(0), String type of Number is Byte, Otherwise, case SpecialChar is throw ValidationException
	 * </pre>
	 * <pre>
	 * StrUtils.toByte("1234")        = Byte(1234)
	 * StrUtils.toByte("")          = Byte((byte)' ')
	 * StrUtils.toByte(null)          = Byte((byte)' ')
	 * StrUtils.toByte("  456")      = Byte(456)
	 * StrUtils.toByte("234   ")     = Byte(234)
	 * StrUtils.toByte("0  0")    = throws ValidationException
	 * StrUtils.toByte("k123")   = throws ValidationException
	 * </pre>
	 * @param s
	 * @return
	 * @throws ValidationException
	 */
	public static Byte toByte(String s) throws ValidationException {
		if ( null == s ) return new Byte((byte)'\0');
		if ( 0 >= s.trim().length() ) return new Byte((byte)'\0');

		try {
			return new Byte(s.trim());
		} catch (NumberFormatException e) {
			throw new ValidationException(e);
		}
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
	public static int toInt(String s) throws ValidationException {
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
	public static Integer toInteger(String s) throws ValidationException {
		if ( null == s ) return new Integer(0);
		if ( 0 >= s.trim().length() ) return new Integer(0);

		try {
			return new Integer(s.trim());
		} catch (NumberFormatException e) {
			throw new ValidationException(e);
		}
	}
	/**
	 * change String to long, otherwise 0
	 * <pre>
	 * StrUtils.tolong("123")                 = 123
	 * StrUtils.tolong("10")                    = 10
	 * StrUtils.tolong("")          			= 0
	 * StrUtils.tolong(null)          			= 0
	 * StrUtils.tolong("  123")      			= 123
	 * StrUtils.tolong("123   ")     			= 123
	 * StrUtils.tolong("0  0")    				= throws ValidationException
	 * StrUtils.tolong("k123")   			= throws ValidationException
	 * </pre>
	 *
	 * @param s
	 * @return
	 * @throws ValidationException
	 */
	public static long tolong(String s) throws ValidationException {
		return toLong(s).longValue();
	}
	/**
	 * <pre>change String to Long, otherwise new Long(0).
	 * String type of Null or Empty is new Long(0), String type of Number is Long, Otherwise, case SpecialChar is throw ValidationException
	 * </pre>
	 * <pre>
	 * StrUtils.toLong("1234")        = Long(1234)
	 * StrUtils.toLong("")          = Long(0)
	 * StrUtils.toLong(null)          = Long(0)
	 * StrUtils.toLong("  456")      = Long(456)
	 * StrUtils.toLong("234   ")     = Long(234)
	 * StrUtils.toLong("0  0")    = throws ValidationException
	 * StrUtils.toLong("k123")   = throws ValidationException
	 * </pre>
	 * @param s
	 * @return
	 * @throws ValidationException
	 */
	public static Long toLong(String s) throws ValidationException {
		if ( null == s ) return new Long(0);
		if ( 0 >= s.trim().length() ) return new Long(0);

		try {
			return new Long(s.trim());
		} catch (NumberFormatException e) {
			throw new ValidationException(e);
		}
	}
	/**
	 * change String to double, otherwise 0
	 * <pre>
	 * StrUtils.todouble("123")                 = 123
	 * StrUtils.todouble("10")                    = 10
	 * StrUtils.todouble("")          			= 0
	 * StrUtils.todouble(null)          			= 0
	 * StrUtils.todouble("  123")      			= 123
	 * StrUtils.todouble("123   ")     			= 123
	 * StrUtils.todouble("0  0")    				= throws ValidationException
	 * StrUtils.todouble("k123")   			= throws ValidationException
	 * </pre>
	 *
	 * @param s
	 * @return
	 * @throws ValidationException
	 */
	public static double todouble(String s) throws ValidationException {
		return toDouble(s).doubleValue();
	}
	/**
	 * <pre>change String to Double, otherwise new Double(0).
	 * String type of Null or Empty is new Double(0), String type of Number is Double, Otherwise, case SpecialChar is throw ValidationException
	 * </pre>
	 * <pre>
	 * StrUtils.toDouble("1234")        = Double(1234)
	 * StrUtils.toDouble("")          = Double(0)
	 * StrUtils.toDouble(null)          = Double(0)
	 * StrUtils.toDouble("  456")      = Double(456)
	 * StrUtils.toDouble("234   ")     = Double(234)
	 * StrUtils.toDouble("0  0")    = throws ValidationException
	 * StrUtils.toDouble("k123")   = throws ValidationException
	 * </pre>
	 * @param s
	 * @return
	 * @throws ValidationException
	 */
	public static Double toDouble(String s) throws ValidationException {
		if ( null == s ) return new Double(0);
		if ( 0 >= s.trim().length() ) return new Double(0);

		try {
			return new Double(s.trim());
		} catch (NumberFormatException e) {
			throw new ValidationException(e);
		}
	}
	/**
	 * change String to float, otherwise 0
	 * <pre>
	 * StrUtils.tofloat("123")                 = 123
	 * StrUtils.tofloat("10")                    = 10
	 * StrUtils.tofloat("")          			= 0
	 * StrUtils.tofloat(null)          			= 0
	 * StrUtils.tofloat("  123")      			= 123
	 * StrUtils.tofloat("123   ")     			= 123
	 * StrUtils.tofloat("0  0")    				= throws ValidationException
	 * StrUtils.tofloat("k123")   			= throws ValidationException
	 * </pre>
	 *
	 * @param s
	 * @return
	 * @throws ValidationException
	 */
	public static float tofloat(String s) throws ValidationException {
		return toFloat(s).floatValue();
	}
	/**
	 * <pre>change String to Float, otherwise new Float(0).
	 * String type of Null or Empty is new Float(0), String type of Number is Float, Otherwise, case SpecialChar is throw ValidationException
	 * </pre>
	 * <pre>
	 * StrUtils.toFloat("1234")        = Float(1234)
	 * StrUtils.toFloat("")          = Float(0)
	 * StrUtils.toFloat(null)          = Float(0)
	 * StrUtils.toFloat("  456")      = Float(456)
	 * StrUtils.toFloat("234   ")     = Float(234)
	 * StrUtils.toFloat("0  0")    = throws ValidationException
	 * StrUtils.toFloat("k123")   = throws ValidationException
	 * </pre>
	 * @param s
	 * @return
	 * @throws ValidationException
	 */
	public static Float toFloat(String s) throws ValidationException {
		if ( null == s ) return new Float(0);
		if ( 0 >= s.trim().length() ) return new Float(0);

		try {
			return new Float(s.trim());
		} catch (NumberFormatException e) {
			throw new ValidationException(e);
		}
	}

	/**
	 * <p></p>
	 *
	 * <pre>
	 * StrUtils.lpad("kor", 4)         = " kor"
	 * StrUtils.lpad("kor", 3)         = "kor"
	 * StrUtils.lpad("한글", 5)         = " 한글"
	 * StrUtils.lpad("한글", 4)         = "한글"
	 * StrUtils.lpad(null, 3)          = "   "
	 * StrUtils.lpad("", 3)              = "   "
	 * </pre>
	 * @param s
	 * @param sz
	 * @return
	 */
	public static String lpad(String s, int sz ) {
		if ( null == s ) return full(s, '0', sz);
		byte[] bt = new byte[sz];
		byte[] bs = s.getBytes();

		for ( int b = 0; b < sz; b++ ) {
			if ( b < bs.length ) 	bt[b] = bs[b];
			else bt[b] = ' ';
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
	 * StrUtils.full("kor", '1', 5)         = "kor11"
	 * StrUtils.full("eng", ' ', 6)         = "eng   "
	 * StrUtils.full("", '0', 4)             = "0000"
	 * StrUtils.full("한글", '0', 4)             = "한글"
	 * StrUtils.full("한글", 'k', 6)             = "한글kk"
	 * StrUtils.full("", ' ', 4)              = "    "
	 * StrUtils.full(null, '0', 4)          = "    "
	 * </pre>
	 * @param org
	 * @param rplc
	 * @param sz
	 * @return
	 */
	public static byte[] full(byte[] org, byte rplc, int sz ) {
		org = (null == org) ? new byte[sz]:org;
		byte[] bt = new byte[sz];
		for ( int i = 0; i < sz; i++ ) bt[i] = rplc;
		for ( int c = 0; c < org.length; c++ ) bt[c] = org[c];
		return bt;
	}
	/**
	 * <p></p>
	 *
	 * <pre>
	 * StrUtils.lpad("kor", 5)            = "  kor"
	 * StrUtils.lpad("kor",3)             = "kor"
	 * StrUtils.lpad("", 5)                 = "     "
	 * StrUtils.lpad("한글",5)            = " 한글"
	 * StrUtils.lpad("한글",4)            = "한글"
	 * StrUtils.lpad("한글",2)            = "한글"
	 * StrUtils.lpad(null, 4)              = "    "
	 * StrUtils.lpad(null, 0)              = null;
	 * StrUtils.lpad("kor", 0)             = "kor"
	 * StrUtils.lpad("kor", 2)             = "kor"
	 * </pre>
	 *
	 * @param b
	 * @param sz
	 * @return
	 */
	public static byte[] lpad(byte[] b, int sz ) {
		if ( 0 >= sz ) return b;

		if ( null == b ) return full(b,(byte)' ',sz);
		if ( sz < b.length ) return b;

		byte[] bt = new byte[sz];
		byte[] bs = b;

		for ( int c = 0; c < sz; c++ ) {
			if ( c < bs.length ) 	bt[c] = bs[c];
			else bt[c] = ' ';
		}
		return bt;
	}
	/**
	 * <p></p>
	 * <pre>
	 * StrUtils.rpad("kor".getBytes(), 5)            = "kor  "
	 * StrUtils.rpad("kor".getBytes(),3)             = "kor"
	 * StrUtils.rpad("".getBytes(), 5)                 = "     "
	 * StrUtils.rpad("한글".getBytes(),5)            = "한글 "
	 * StrUtils.rpad("한글".getBytes(),4)            = "한글"
	 * StrUtils.rpad("한글".getBytes(),2)            = "한글"
	 * StrUtils.rpad(null, 4)                               = "    "
	 * StrUtils.rpad(null, 0)                               = null
	 * StrUtils.rpad("kor".getBytes(), 0)             = "kor"
	 * StrUtils.rpad("kor".getBytes(), 2)             = "kor"
	 * </pre>
	 * @param b
	 * @param sz
	 * @return
	 */
	public static byte[] rpad(byte[] b, int sz) {
		if ( 0 >= sz ) return b;
		if ( null == b ) return full(b,(byte)' ',sz);
		if ( sz < b.length ) return b;

		byte[] bt = new byte[sz];
		byte bb = (byte) (sz - b.length);

		if ( 0 > bb ) return b;

		for ( int c = 0; c < sz; c++ )  bt[c] = ' ';
		for ( int c = 0; c < b.length; c++ ) bt[c] = b[c];
		return bt;
	}
	/**
	 * <p></p>
	 *
	 * <pre>
	 * StrUtils.rpad("kor", 5)            = "kor  "
	 * StrUtils.rpad("kor",3)             = "kor"
	 * StrUtils.rpad("", 5)                 = "     "
	 * StrUtils.rpad("한글",5)            = "한글 "
	 * StrUtils.rpad("한글",4)            = "한글"
	 * StrUtils.rpad("한글",2)            = "한글"
	 * StrUtils.rpad(null, 4)              = "    "
	 * StrUtils.rpad(null, 0)              = null
	 * StrUtils.rpad("kor", 0)             = "kor"
	 * StrUtils.rpad("kor", 2)             = "kor"
	 * </pre>
	 *
	 * @param s
	 * @param sz
	 * @return
	 */
	public static String rpad(String s, int sz ) {
		if ( 0 >= sz ) return s;
		if ( null == s ) return full(s,' ',sz);
		return new String(rpad(s.getBytes(), sz) ) ;
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

		if ( sz > bs.length ) {
			int idx = 0;
			for ( int b = 0; b < (sz-bs.length); b++ ) bt[idx++] = '0';
			for ( int b = 0; b < bs.length; b++ ) bt[idx++] = bs[b];
		} else  bt = bs;
		return new String(bt);
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
		 return str == null || str.trim().length() == 0;
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
	  * If The Input value is null, Empty
	  * <pre>
	  * Object o = new Object();
	  * StrUtils.nullToEmpty(null)               = ""
	  * StrUtils.nullToEmpty(o)            = o
	  * StrUtils.nullToEmpty(" ")                  = " "
	  * StrUtils.nullToEmpty("")                   = ""
	  * </pre>
	  * @param o
	  * @return
	  */
	 public static Object nullToEmpty(Object o) {
		 	if ( null ==o ) return EMPTY;
		 	else return o;
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
	 	else return s;
	 }
	 /**
	  * <p></p>
	  * 
	  * <pre>
	  * </pre>
	  * @param b
	  * @return
	  */
	 public static String toHex(byte[] b) {
		 StringBuffer retString = new StringBuffer();
		 for (int i = 0; i < b.length; ++i) {
			 retString.append(
						Integer.toHexString(0x0100 + (b[i] & 0x00FF)).substring(1));
		 }
		 return retString.toString();
	 }

	 public static String encodeEucKr(String s) {
		 String s1 = "";
		 try {
			s1 = URLEncoder.encode(s, "EUC-KR");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s1;
	 }
	 public static String decodeEucKr(String s) {
		 String s1 = "";
		 try {
			s1 = URLDecoder.decode(s, "EUC-KR");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s1;
	 }

	 public static String encode(String s, String enc) {
		 String s1 = "";
		 try {
			s1 = URLEncoder.encode(s, enc);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s1;
	 }
	 public static String decode(String s, String enc) {
		 String s1 = "";
		 try {
			s1 = URLDecoder.decode(s, enc);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s1;
	 }
	 public static String toEncode(String s, String enc, String dec) {
		 String s1 = "";
		 try {
			s1 = new String(s.getBytes(enc),dec);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s1;
	 }
	 public static String toKorEncode(String s) {
		return toEncode(s,"8859_1","EUC_KR"); 
	 }
	 public static String toKorDecode(String s) {
			return toEncode(s,"EUC_KR","8859_1"); 
	 }
	 public static String replaceEnter(String str) {
			
		char[] chars = str.toCharArray();
		char a;
		
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<chars.length; i++){
			a = chars[i];
			if(a == '\n') {
				sb.append("<br>");
			} else {
				sb.append(a);
			}
		}
		
		return sb.toString();
	}
	 public static String clearEnter(String str) {
			
			char[] chars = str.toCharArray();
			char a;
			
			StringBuffer sb = new StringBuffer();
			for(int i=0; i<chars.length; i++){
				a = chars[i];
				if(a == '\n') {
					sb.append(" ");
				} else {
					sb.append(a);
				}
			}
			
			return sb.toString();
		}
    public static String filenameAppend(String filename, String regx, String rename) {
		int lastidx = filename.lastIndexOf(regx);
		return filename.substring(0, lastidx)+rename+filename.substring(lastidx, filename.length());
		
	}
    public static String cutByDot(String s, int maxlen) {
		if ( null == s ) return "";
		if ( s.length() <= maxlen ) return s;
		return s.substring(0,maxlen)+"......";
		
	}
    public static String makeDateType14Char(String date) {
        if(date == null || date.length() <= 0)
            return "";
        if(date.length() == 14)
            return date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8) + " " + date.substring(8, 10) + ":" + date.substring(10, 12) + ":" + date.substring(12);
        else
            return date;
    }
    public static String substring(String str, int beginIndex, int endIndex) {
		 if ( isEmpty2 (str) ) return "";
		 int len = str.trim().length();
		 if ( len >= beginIndex && len <= endIndex ) {
			 return str.substring(beginIndex, endIndex);
		 } else return "";
	 }
    public static String substring(String str, int beginIndex) {
		 if ( isEmpty2 (str) ) return "";
		 int len = str.trim().length();
		 if ( len >= beginIndex ) {
			 return str.substring(beginIndex);
		 } else return "";
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
	public static void copy(ArrayList<String> src, ArrayList<String> trg) {
		if ( null == trg ) return;
		if ( null == src ) src = new ArrayList<String>();
		for ( int i = 0; i < trg.size(); i++ ) src.add(trg.get(i));
	}
	public static String replace(String str, String pattern, String replace) {
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

	public static String fnTranslateNewLine(String str) {
		String rStr = str;
		if (rStr != null && !rStr.equals("")) {
			rStr = replace(rStr, "\r\n", "<br>");
			rStr = replace(rStr, "\n\r", "<br>");
		}
		return rStr;
	}
	
	public static String fnTranslateNoHtml(String str) {
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

