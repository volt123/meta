package com.lgdisplay.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	public static String getSysdate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(new Date());
	}
	public static String getSysdate(String formatter) {
		SimpleDateFormat sdf = new SimpleDateFormat(formatter);
		return sdf.format(new Date());
	}
	public static String getYMD() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(new Date());
	}
	public static String getYMDH() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
		return sdf.format(new Date());
	}
	public static String getYMDHM() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		return sdf.format(new Date());
	}
	public static String getHMS() {
		SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
		return sdf.format(new Date());
	}
}
