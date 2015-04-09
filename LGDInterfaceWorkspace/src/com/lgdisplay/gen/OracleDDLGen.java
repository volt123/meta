/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/
package com.lgdisplay.gen;

import com.itplus.mm.dao.elem.TeleminfoDAO;
import com.itplus.mm.server.structure.AbstractDDLGen;
import com.itplus.mm.server.structure.AbstractNamingRule;
import com.itplus.mm.server.structure.DDLObj;
import com.itplus.mm.server.structure.NamingCall;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import jspeed.base.jdbc.CacheResultSet;
import jspeed.base.log.Logger;
import jspeed.base.util.FileHelper;
import jspeed.base.util.StringHelper;

/** 
*************************************************************************************************************************** 
* PROJ : LGD 유지보수
* NAME : OracleDDLGen.java 
* DESC : ORACLE DBMS에 대한 DDL 스크립트를 생성하는 프로그램
* AUTHOR : 하정수
* VER : 1.0 
* Copyright 2015 GTOne Co.,LTD.  All rights reserved 
*************************************************************************************************************************** 
* No     DATE           Author                   Description 
*************************************************************************************************************************** 
* 1.0   2015. 04. 09.   하정수                   최초 Release
*************************************************************************************************************************** 
*/

public class OracleDDLGen extends AbstractDDLGen {
	String CREATE_SYNONYM_TEMPLET = null;
	HashMap FileNameMap;
	NamingCall call;

	public OracleDDLGen(TeleminfoDAO dao, String dbType) throws Exception {
		super(dao, dbType);
	}

	protected void getTemplate() throws Exception {
		super.getTemplate();
		this.CREATE_SYNONYM_TEMPLET = new String(
				FileHelper.getFileContent(this.templateDir
						+ "/createSynonym.txt"));
	}

	protected void getFile_Job_Name(HashMap param) {
		try {
			this.call = new NamingCall((String) param.get("DAT_STRC_ID"));
			HashMap temp = this.call.getBaseInfo();
			Iterator it = temp.keySet().iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				if (param.get(key) != null)
					continue;
				param.put(key, temp.get(key));
			}

			param.putAll(this.call.getNamingRuleObj().getDdlScript(param));
		} catch (Exception e) {
			this.log.print(128, "NAMING NOT FOUND==" + e.getMessage());
		}
	}
	/**
	 * DDL 생성의 시작점으로 개별 메서드를 호출하여 최종적으로 DDL 스크립트를 생성한다. 
	 */
	
	public CacheResultSet generateDDL(String tableId, String reqDt,
			String lnkInfoId, HashMap param) throws Exception {
		getTemplate();
		getFile_Job_Name(param);
		String kubun = (String) param.get("REQ_TP_CD");
		this.reqDt = reqDt;
		this.lnkInfoId = lnkInfoId;
		param.put("REQ_DT", reqDt);
		createTableSpaceDDL(tableId, param);
		addTableDDL(tableId, kubun, param);
		addFKDDL(tableId, kubun, param);
		addIndexDDL(tableId, param);

		addOtherDDL(kubun, param);
		return this.ddlObj.toCacheResultSet();
	}
	
	public void addOtherDDL(String kubun, HashMap param) throws Exception {
		String result = this.CREATE_SYNONYM_TEMPLET;

		if (result == null)
			return;
		param.put("TABLENAME", StringHelper.replaceStr(
				(String) param.get("TABLE_NAME"), "_", ""));
		this.ddlObj.addDDL("Synonym", (String) param.get("SYN_FILE_NAME"),
				replaceAllParam(result, param));
	}

	public String getCreateTableDDL(String tableId, HashMap param)
			throws Exception {
		String returnStr = this.CREATE_TABLE_TEMPLET;
		String pkId = null;
		String pkNamespace = null;

		CacheResultSet columnInfoRs = getColumnInfo(tableId,
				(String) param.get("TABLE_NAMESPACE"));
		CacheResultSet pkInfoRs = getPKInfo(tableId,
				(String) param.get("TABLE_NAMESPACE"));

		if (pkInfoRs.next()) {
			pkId = pkInfoRs.getString("ELEM_INFO_ID");
			pkNamespace = pkInfoRs.getString("NAMESPACE");
			param.put("PK_NAME", pkInfoRs.getString("ELEM_PHSC_NM"));
		}
		CacheResultSet pkColumnInfoRs = getPKColumn(pkId, pkNamespace);

		columnInfoRs.sort("ORDINAL_POSITION", 1);
		pkColumnInfoRs.sort("KEY_SEQ", 1);
		param.put("COLUMN_LIST", getCreateColumn(columnInfoRs));
		param.put("COMMENT_LIST", getCreateColumnComment(columnInfoRs, param));
		param.put("PK_COLUMN_LIST", getCreatePKColumn(pkColumnInfoRs));

		return replaceAllParam(returnStr, param);
	}

	protected String getCreateColumn(CacheResultSet rs) throws Exception {
		StringBuffer sb = new StringBuffer();
		rs.initRow();
		for (int i = 0; rs.next(); ++i) {
			if (!("WFGB_D".equals(rs.getString("REQ_TP_CD")))) {
				if (sb.length() > 0) {
					sb.append(",\n");
				}

				sb.append(" ")
						.append(rs.getString("ELEM_PHSC_NM"))
						.append("  ")
						.append(getType(rs.getString("TYPE_NAME"),
								rs.getString("COLUMN_SIZE"),
								rs.getString("DECIMAL_DIGITS"))).append("\n");
				if ((rs.getString("COLUMN_DEF") != null)
						&& (rs.getString("COLUMN_DEF").length() > 0)) {
					if ("3".equals(rs.getString("NULLABLE")))
						sb.append(rs.getString("COLUMN_DEF"));
					else
						sb.append(" DEFAULT ").append(
								rs.getString("COLUMN_DEF"));
				}
				if ("1".equals(rs.getString("NULLABLE"))) {
					sb.append(" NULL");
				} else {
					if (!("0".equals(rs.getString("NULLABLE"))))
						continue;
					sb.append(" NOT NULL");
				}
			}
		}

		return sb.toString();
	}

	public String getCreateIndexDDL(String tableId, String indexId,
			HashMap param) throws Exception {
		CacheResultSet columnInfoRs = getIndexColumnInfo(tableId, indexId);

		param.put("INDEX_COLUMN_LIST", getCreateIndexColumn(columnInfoRs));
		String result = null;

		result = this.INDEX_TEMPLET;

		return replaceAllParam(result, param);
	}

	protected String getCreateColumnComment(CacheResultSet rs, HashMap param)
			throws Exception {
		StringBuffer sb = new StringBuffer();
		rs.initRow();

		for (int i = 0; rs.next(); ++i) {
			if (!("WFGB_D".equals(rs.getString("REQ_TP_CD")))) {
				if (sb.length() > 0) {
					sb.append(";\n");
				}

				sb.append("COMMENT ON COLUMN ");
				if ((param.get("TABLE_SCHEMA") != null)
						&& (!("".equals(param.get("TABLE_SCHEMA")))))
					sb.append(param.get("TABLE_SCHEMA")).append(".");
				sb.append(param.get("TABLE_NAME")).append(".")
						.append(rs.getString("ELEM_PHSC_NM")).append(" IS '")
						.append(rs.getString("ELEM_LGCL_NM")).append("'");
			}
		}

		if (sb.length() > 0) {
			sb.append(";\n");
		}

		return sb.toString();
	}
}