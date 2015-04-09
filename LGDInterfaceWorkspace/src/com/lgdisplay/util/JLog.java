package com.lgdisplay.util;

import com.lgdisplay.util.DateUtil;

public class JLog {

	public static void debug(String s) {
		System.out.println(DateUtil.getSysdate("yyyy-MM-dd HH:mm:ss") + "	" + s);
	}

}
