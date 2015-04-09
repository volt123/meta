package com.lgdisplay.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SYSModel_old {

	public static Map sys;
	public static Map sysdtls; 
	public static SYSDAO dao ;
	
	static {
		load();
	}
	public SYSModel_old() {
		if ( null == sys ) 
			load();
	}
	public static void load() {
		try {
			dao = new SYSDAO();
			sys = new HashMap();
			sysdtls = new HashMap();
			
			sys = dao.allsql();
			sysdtls = dao.allparam();
			System.out.println("#########################[SYS Loaded]:::"+sys.size());
			System.out.println("#########################[SYS DTL Loaded]:::"+sysdtls.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String getProperty(String fieldname) {
		
		if ( null == sys ) return "";
		if ( sys.containsKey(fieldname)) {
			return ((SYSVO)sys.get(fieldname)).getFieldValue();
		} else return "";
	}
	public static ArrayList getPropertyDtl(String fieldname) {
		
		if ( null == fieldname ) return new ArrayList();
		if ( sysdtls.containsKey(fieldname)) {
			return (ArrayList) sysdtls.get(fieldname);
		}
		return new ArrayList();
	}
	public static String getPropertyDtlValue(String fieldname, String fieldCd) {
		
		if ( null == fieldname ) return "";
		if ( sysdtls.containsKey(fieldname)) {
			ArrayList olist = (ArrayList) sysdtls.get(fieldname);
			for ( int i = 0; i < olist.size(); i++ ) { 
				SYSVO vo = (SYSVO)olist.get(i);
				if ( null != vo.getFieldCd() && vo.getFieldCd().equalsIgnoreCase(fieldCd)) 
					return vo.getFieldValue();
			}
		}
		return "";
	}
}
