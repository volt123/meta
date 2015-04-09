package com.lgdisplay.db;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import jspeed.base.jdbc.CacheResultSet;
import jspeed.base.jdbc.QueryHelper;
import jspeed.base.query.DBAssistant;

import com.itplus.mm.dao.CommonDAO;

public class DBModelDAO extends CommonDAO {
	public DBModelDAO() throws Exception {
	}

	public DBModelDAO(DBAssistant _dba) throws Exception {
		super(_dba);
	}
	
	public CacheResultSet select(HashMap<String, String> param) throws Exception {
		CacheResultSet crs;
		String query = param.get("SQL_ID").toString();
		String USER_IP = param.get("USER_IP").toString();
		System.out.println(USER_IP);
//		Object[] objs = (Object[])param.get("PARAM_ID");
		Object[] objs = { param.get("LOGIN_ID").toString() };
		
		QueryHelper qHelper = new QueryHelper();
		ResultSet rs = null;
		crs = null;
		try {
			
			rs = qHelper.executeQuery(query, objs );
			crs = new CacheResultSet(rs);
		} catch (Exception e) {
			throw e;
		} finally {
			if (qHelper != null)
				qHelper.close();
			if (rs != null)
				rs.close();
		}
		return crs;
	}
	public ArrayList read(String query) throws Exception {
		DBModel model = new DBModel();
		ArrayList oresults = new ArrayList();
		
		QueryHelper qHelper = new QueryHelper();
		ResultSet rs = null;
		try {
			Object[] objs = new Object[0];
			rs = qHelper.executeQuery(query, objs );
			oresults = model.getModel(rs);
		} catch (Exception e) {
			throw e;
		} finally {
			if (qHelper != null)
				qHelper.close();
			if (rs != null)
				rs.close();
		}
		return oresults;
	}
	public ArrayList read(String query, Object[] objs) throws Exception {
		DBModel model = new DBModel();
		ArrayList oresults = new ArrayList();
		
		QueryHelper qHelper = new QueryHelper();
		ResultSet rs = null;
		try {
			rs = qHelper.executeQuery(query, objs );
			oresults = model.getModel(rs);
		} catch (Exception e) {
			throw e;
		} finally {
			if (qHelper != null)
				qHelper.close();
			if (rs != null)
				rs.close();
		}
		return oresults;
	}
	public int executeUpdate(HashMap param) throws Exception {
		String query = param.get("SQL_ID").toString();
		
//		System.out.println("\n");
//		System.out.println("\n");
//		System.out.println(param.get("PARAM_ID").getClass());
//		System.out.println("\n");
//		System.out.println("\n");
		
		String objs = (String)param.get("PARAM_ID");
		String login_id = (String)param.get("LOGIN_ID");
		
		QueryHelper qHelper = new QueryHelper();
		ResultSet rs = null;
		int count = 0;
		try {
			count += qHelper.executeUpdate(query, new Object[]{ objs, login_id });
		} catch (Exception e) {
			throw e;
		} finally {
			if (qHelper != null)
				qHelper.close();
			if (rs != null)
				rs.close();
		}
		return count;
	}
}
