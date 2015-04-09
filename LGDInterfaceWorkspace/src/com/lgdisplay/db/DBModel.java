package com.lgdisplay.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class DBModel {

	public ArrayList getModel(ResultSet rs) {
		ResultSetMetaData rsmd = null;
		ArrayList crs = new ArrayList();
		try {
			if ( null != rs ) rsmd = rs.getMetaData();
			
			if ( rs.next() ) {
				crs = new ArrayList();
				HashMap map = new HashMap();
				for(int i = 1; i < rsmd.getColumnCount() + 1; i++)
			        map.put(rsmd.getColumnName(i).toUpperCase(), rs.getString(i));
				crs.add(map);
			} else return crs;
			
			while ( rs.next() ) {
				HashMap map = new HashMap();
				for(int i = 1; i < rsmd.getColumnCount() + 1; i++)
			        map.put(rsmd.getColumnName(i).toUpperCase(), rs.getString(i));
				crs.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return crs;
	}
	public HashMap getModelMapByKey(ResultSet rs, String findkeyfields) {
		ResultSetMetaData rsmd = null;
		HashMap datamap = new HashMap();
		try {
			if ( null != rs ) rsmd = rs.getMetaData();
			
			String keyname = "";
			if ( rs.next() ) {
				datamap = new HashMap();
				HashMap map = new HashMap();
				keyname = "";
				for(int i = 1; i < rsmd.getColumnCount() + 1; i++) {
			        map.put(rsmd.getColumnName(i), rs.getString(i));
			        if ( findkeyfields.trim().equalsIgnoreCase(rsmd.getColumnName(i))) keyname = rs.getString(i);
				}
				if ( 0 != keyname.trim().length() ) datamap.put(keyname, map);
			} else return datamap;
			
			while ( rs.next() ) {
				HashMap map = new HashMap();
		        keyname = "";
				for(int i = 1; i < rsmd.getColumnCount() + 1; i++) {
			        map.put(rsmd.getColumnName(i), rs.getString(i));
			        if ( findkeyfields.trim().equalsIgnoreCase(rsmd.getColumnName(i))) keyname = rs.getString(i);
				}
				if ( 0 != keyname.trim().length() ) datamap.put(keyname, map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return datamap;
	}
}
