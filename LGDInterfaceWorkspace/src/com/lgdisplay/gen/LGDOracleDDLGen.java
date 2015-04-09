package com.lgdisplay.gen;

import java.util.HashMap;
import java.util.Iterator;

import jspeed.base.jdbc.CacheResultSet;
import jspeed.base.util.FileHelper;
import jspeed.base.util.StringHelper;

import com.itplus.mm.common.util.SysHandler;
import com.itplus.mm.dao.elem.TeleminfoDAO;
import com.itplus.mm.server.structure.AbstractDDLGen;
import com.itplus.mm.server.structure.NamingCall;

public class LGDOracleDDLGen extends AbstractDDLGen {

	public LGDOracleDDLGen(TeleminfoDAO dao, String dbType) throws Exception {
		super(dao, dbType);
		CREATE_SYNONYM_TEMPLET = null;
	}

	protected void getTemplate() throws Exception {
		super.getTemplate();
		CREATE_SYNONYM_TEMPLET = new String(
				FileHelper.getFileContent(templateDir + "/createSynonym.txt"));
	}

	protected void getFile_Job_Name(HashMap param) {
		try {
			call = new NamingCall((String) param.get("DAT_STRC_ID"));
			HashMap temp = call.getBaseInfo();
			for (Iterator it = temp.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				if (param.get(key) == null)
					param.put(key, temp.get(key));
			}

			param.putAll(call.getNamingRuleObj().getDdlScript(param));
		} catch (Exception e) {
			log.print(128, "NAMING NOT FOUND==" + e.getMessage());
		}
	}

	public void addOtherDDL(String kubun, HashMap param) throws Exception {
		String result = CREATE_SYNONYM_TEMPLET;
		if (result != null) {
			param.put("TABLENAME", StringHelper.replaceStr(
					(String) param.get("TABLE_NAME"), "_", ""));
			ddlObj.addDDL("Synonym", (String) param.get("SYN_FILE_NAME"),
					replaceAllParam(result, param));
		}
	}

	public String getCreateTableDDL(String tableId, HashMap param)
			throws Exception {
		String returnStr = CREATE_TABLE_TEMPLET;
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
		
		//customized for Hyosung Capital
		// if startWith "T_" remove "T_"
		// if endWith "_ASCII", remove lastIndexOf "_ASCII"
	    String tableName = (String)param.get("TABLE_NAME");
		String entityId = tableName;
		if (entityId.startsWith("T_")) 
		{
			entityId = entityId.substring(2);
			int pos = entityId.lastIndexOf("_");
			if ( pos > 4) {
				entityId = entityId.substring(0,pos);
			}
		}
		param.put("HS_ENTITY_NAME", entityId);
		
		return replaceAllParam(returnStr, param);
	}

	protected String getCreateColumn(CacheResultSet rs) throws Exception {
		StringBuffer sb = new StringBuffer();
		rs.initRow();
		for (int i = 0; rs.next(); i++)
			if (!"WFGB_D".equals(rs.getString("REQ_TP_CD"))) {
				if (sb.length() > 0)
					sb.append(",\n");
				sb.append(" ")
						.append(rs.getString("ELEM_PHSC_NM"))
						.append("  ")
						.append(getType(rs.getString("TYPE_NAME"),
								rs.getString("COLUMN_SIZE"),
								rs.getString("DECIMAL_DIGITS"))).append("\n");
				if (rs.getString("COLUMN_DEF") != null
						&& rs.getString("COLUMN_DEF").length() > 0)
					if ("3".equals(rs.getString("NULLABLE")))
						sb.append(rs.getString("COLUMN_DEF"));
					else
						sb.append(" DEFAULT ").append(
								rs.getString("COLUMN_DEF"));
				if ("1".equals(rs.getString("NULLABLE")))
					sb.append(" NULL");
				else if ("0".equals(rs.getString("NULLABLE")))
					sb.append(" NOT NULL");
			}

		return sb.toString();
	}

	public String getCreateIndexDDL(String tableId, String indexId,
			HashMap param) throws Exception {
		CacheResultSet columnInfoRs = getIndexColumnInfo(tableId, indexId);
		param.put("INDEX_COLUMN_LIST", getCreateIndexColumn(columnInfoRs));
		String result = null;
		result = INDEX_TEMPLET;
		return replaceAllParam(result, param);
	}

	protected String getCreateColumnComment(CacheResultSet rs, HashMap param)
			throws Exception {
		StringBuffer sb = new StringBuffer();
		rs.initRow();
		for (int i = 0; rs.next(); i++)
			if (!"WFGB_D".equals(rs.getString("REQ_TP_CD"))) {
				if (sb.length() > 0)
					sb.append(";\n");
				sb.append("COMMENT ON COLUMN ");
				if (param.get("TABLE_SCHEMA") != null
						&& !"".equals(param.get("TABLE_SCHEMA")))
					sb.append(param.get("TABLE_SCHEMA")).append(".");
				sb.append(param.get("TABLE_NAME")).append(".")
						.append(rs.getString("ELEM_PHSC_NM")).append(" IS '")
						.append(rs.getString("ELEM_LGCL_NM")).append("'");
			}

		if (sb.length() > 0)
			sb.append(";\n");
		return sb.toString();
	}

	String CREATE_SYNONYM_TEMPLET;
	HashMap FileNameMap;
	NamingCall call;
	
	protected void addIndexDDL(String tableId, HashMap param) throws Exception {
		for (CacheResultSet indexRs = getIndexInfo(tableId); indexRs.next();) {
			param.putAll(indexRs.getMap());
			
			if ("N".equals(indexRs.getString("UNIQ_YN"))) 
			{
				param.put("UNIQ_YN_NM", "");
				param.put("HS_INDEX_TYPE", "A");
			}
			else 
			{
				param.put("UNIQ_YN_NM", "UNIQUE");
				param.put("HS_INDEX_TYPE", "U");
			}

			
			System.out.println(dropCreateTable + ":" + tableId);
			if (dropCreateTable.contains(tableId)) {
				if ("WFGB_D".equals(indexRs.getString("REQ_TP_CD")))
					ddlObj.addDDL(
							"Index",
							null,
							getDropIndexDDL(tableId,
									indexRs.getString("INDEX_ID"), param));
				else
					createIndexDDL(tableId, indexRs, param, false);
			} else if (!"WFGB_S".equals(indexRs.getString("REQ_TP_CD")))
				if ("WFGB_D".equals(indexRs.getString("REQ_TP_CD")))
					ddlObj.addDDL(
							"Index",
							null,
							getDropIndexDDL(tableId,
									indexRs.getString("INDEX_ID"), param));
				else
					createIndexDDL(tableId, indexRs, param, false);
		}

	}
	
	public void addFKDDL(String tableId, String reqTpCd, HashMap param)
	throws Exception {
		String fkResult = "";
		String fkId = null;
		String fkNamespace = null;
		CacheResultSet fkInfoRs = getFKInfo(tableId,
				(String) param.get("TABLE_NAMESPACE"));
		if (fkInfoRs.next()
				&& "Y".equals(SysHandler.getInstance().getProperty(
						"FK_PARSING_YN"))) {
			fkInfoRs.initRow();
			while (fkInfoRs.next()) {
				reqTpCd = fkInfoRs.getString("REQ_TP_CD");
				fkId = fkInfoRs.getString("ELEM_INFO_ID");
				fkNamespace = fkInfoRs.getString("NAMESPACE");
				param.put("FKENTITY_NAME", fkInfoRs.getString("FKENTITY_NAME"));
				param.put("FK_NAME", fkInfoRs.getString("FK_NAME"));
				param.put("PKENTITY_NAME", fkInfoRs.getString("PKENTITY_NAME"));
				CacheResultSet fkColumnInfoRs = getFKColumn(fkId, fkNamespace);
				param.put("FK_COLUMN_LIST",
						getCreateFKColumn(fkColumnInfoRs, "FKATTRIBUTE_NAME"));
				param.put("PK_COLUMN_LIST",
						getCreateFKColumn(fkColumnInfoRs, "PKATTRIBUTE_NAME"));
				
				//customized for Hyosung Capital
				// if startWith "T_" remove "T_"
				// if endWith "_ASCII", remove lastIndexOf "_ASCII"
			    String pkEntityName = (String)param.get("PKENTITY_NAME");
			    String fkEntityName = (String)param.get("FKENTITY_NAME");

				if (pkEntityName.startsWith("T_")) 
				{
					pkEntityName = pkEntityName.substring(2);
					int pos = pkEntityName.lastIndexOf("_");
					if ( pos > 4) {
						pkEntityName = pkEntityName.substring(0,pos);
					}
				}
				param.put("HS_PKENTITY_NAME", pkEntityName);
				
				if (fkEntityName.startsWith("T_")) 
				{
					fkEntityName = fkEntityName.substring(2);
					int pos = fkEntityName.lastIndexOf("_");
					if ( pos > 4) {
						fkEntityName = fkEntityName.substring(0,pos);
					}
				}
				param.put("HS_FKENTITY_NAME", fkEntityName);
				
				if (reqTpCd.equals("WFGB_I")) {
					fkResult = fkResult
							+ replaceAllParam(ALTER_FK_TEMPLET, param) + "\n";
					System.out.println("addFKDDL.WFGB_I==>" + fkResult);
				} else if (reqTpCd.equals("WFGB_U")) {
					fkResult = fkResult
							+ replaceAllParam(DROP_FK_TEMPLET, param) + "\n";
					fkResult = fkResult
							+ replaceAllParam(ALTER_FK_TEMPLET, param) + "\n";
					System.out.println("addFKDDL.WFGB_U==>" + fkResult);
				} else if (reqTpCd.equals("WFGB_D")) {
					fkResult = fkResult
							+ replaceAllParam(DROP_FK_TEMPLET, param) + "\n";
					System.out.println("addFKDDL.WFGB_D==>" + fkResult);
				}
			}
			ddlObj.addDDL("Foreign Key",
					"FK_" + (String) param.get("TB_FILE_NAME"), fkResult);
		}
	}
	
	
	protected String getAlterFKDDLByUD(String tableId, HashMap param)
	throws Exception {
		CacheResultSet fkInfoRs = getFKInfo(tableId,
				(String) param.get("TABLE_NAMESPACE"));
		String reqTpCd = "";
		String fkId = "";
		String fkNamespace = "";
		String fkResult = null;
		while (fkInfoRs.next()) {
			reqTpCd = fkInfoRs.getString("REQ_TP_CD");
			fkId = fkInfoRs.getString("ELEM_INFO_ID");
			fkNamespace = fkInfoRs.getString("NAMESPACE");
			param.put("FKENTITY_NAME", fkInfoRs.getString("FKENTITY_NAME"));
			param.put("FK_NAME", fkInfoRs.getString("FK_NAME"));
			param.put("PKENTITY_NAME", fkInfoRs.getString("PKENTITY_NAME"));
			CacheResultSet fkColumnInfoRs = getFKColumn(fkId, fkNamespace);
			param.put("FK_COLUMN_LIST",
					getCreateFKColumn(fkColumnInfoRs, "FKATTRIBUTE_NAME"));
			param.put("PK_COLUMN_LIST",
					getCreateFKColumn(fkColumnInfoRs, "PKATTRIBUTE_NAME"));
			
			
			//customized for Hyosung Capital
			// if startWith "T_" remove "T_"
			// if endWith "_ASCII", remove lastIndexOf "_ASCII"
		    String pkEntityName = (String)param.get("PKENTITY_NAME");
		    String fkEntityName = (String)param.get("FKENTITY_NAME");
		
			if (pkEntityName.startsWith("T_")) 
			{
				pkEntityName = pkEntityName.substring(2);
				int pos = pkEntityName.lastIndexOf("_");
				if ( pos > 4) {
					pkEntityName = pkEntityName.substring(0,pos);
				}
			}
			param.put("HS_PKENTITY_NAME", pkEntityName);
			
			if (fkEntityName.startsWith("T_")) 
			{
				fkEntityName = fkEntityName.substring(2);
				int pos = fkEntityName.lastIndexOf("_");
				if ( pos > 4) {
					fkEntityName = fkEntityName.substring(0,pos);
				}
			}
			param.put("HS_FKENTITY_NAME", fkEntityName);
			
			
			if (reqTpCd.equals("WFGB_U") || reqTpCd.equals("WFGB_D"))
				fkResult = fkResult + replaceAllParam(DROP_FK_TEMPLET, param)
						+ "\n";
		}
		return fkResult;
	}
	
	
	protected String getAlterFKDDLByUI(String tableId, HashMap param)
	throws Exception {
		CacheResultSet fkInfoRs = getFKInfo(tableId,
				(String) param.get("TABLE_NAMESPACE"));
		String reqTpCd = "";
		String fkId = "";
		String fkNamespace = "";
		String fkResult = null;
		while (fkInfoRs.next()) {
			reqTpCd = fkInfoRs.getString("REQ_TP_CD");
			fkId = fkInfoRs.getString("ELEM_INFO_ID");
			fkNamespace = fkInfoRs.getString("NAMESPACE");
			param.put("FKENTITY_NAME", fkInfoRs.getString("FKENTITY_NAME"));
			param.put("FK_NAME", fkInfoRs.getString("FK_NAME"));
			param.put("PKENTITY_NAME", fkInfoRs.getString("PKENTITY_NAME"));
			CacheResultSet fkColumnInfoRs = getFKColumn(fkId, fkNamespace);
			param.put("FK_COLUMN_LIST",
					getCreateFKColumn(fkColumnInfoRs, "FKATTRIBUTE_NAME"));
			param.put("PK_COLUMN_LIST",
					getCreateFKColumn(fkColumnInfoRs, "PKATTRIBUTE_NAME"));
			
			//customized for Hyosung Capital
			// if startWith "T_" remove "T_"
			// if endWith "_ASCII", remove lastIndexOf "_ASCII"
		    String pkEntityName = (String)param.get("PKENTITY_NAME");
		    String fkEntityName = (String)param.get("FKENTITY_NAME");
		
			if (pkEntityName.startsWith("T_")) 
			{
				pkEntityName = pkEntityName.substring(2);
				int pos = pkEntityName.lastIndexOf("_");
				if ( pos > 4) {
					pkEntityName = pkEntityName.substring(0,pos);
				}
			}
			param.put("HS_PKENTITY_NAME", pkEntityName);
			
			if (fkEntityName.startsWith("T_")) 
			{
				fkEntityName = fkEntityName.substring(2);
				int pos = fkEntityName.lastIndexOf("_");
				if ( pos > 4) {
					fkEntityName = fkEntityName.substring(0,pos);
				}
			}
			param.put("HS_FKENTITY_NAME", fkEntityName);
			
			if (reqTpCd.equals("WFGB_U") || reqTpCd.equals("WFGB_I"))
				fkResult = fkResult + replaceAllParam(ALTER_FK_TEMPLET, param)
						+ "\n";
		}
		return fkResult;
	}
	
}


/*
	DECOMPILATION REPORT

	Decompiled from: C:\MMDev\workspaces\HYCustomizingWorkspace\MetaMiner30.jar
	Total time: 187 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/