package com.lgdisplay.db;

import java.util.ArrayList;
import java.util.HashMap;

import jspeed.base.jdbc.CacheResultSet;
import jspeed.base.jdbc.QueryHelper;
import jspeed.base.query.DBAssistant;
import jspeed.base.query.QueryService;
import jspeed.base.util.StringHelper;

import com.itplus.mm.common.util.DateUtil;
import com.itplus.mm.dao.CommonDAO;
import com.itplus.mm.server.elementType.ElementType;
import com.itplus.mm.server.elementType.ElementTypeColumn;
import com.itplus.mm.server.elementType.ElementTypeHandler;
 
public class StandardObservanceCheckDAO extends CommonDAO{

	public StandardObservanceCheckDAO() throws Exception
	{
		super();
	}
	public StandardObservanceCheckDAO(DBAssistant dba) throws Exception
	{
		super(dba);
	}
	public   QueryHelper getQueryHelper() throws Exception
	{
		return getDBAssistant().getQueryHelperBySqlId("mm.CheckDAO.observanceCheck");
	}
	/**
	 * 표준항목간 비교
	 * @param param TG_UFW_DIC_ID, SRC_UFW_DIC_ID
	 * @return
	 * @throws Exception
	 */
	public CacheResultSet ufwCheck(HashMap param) throws Exception
	{
		HashMap dynaMap  = new HashMap();
		if("Y".equals(param.get("BOTH_COMPARE_YN")))
		{
			dynaMap.put("JOIN_TYPE", "FULL");
		}
		else
		{
			dynaMap.put("JOIN_TYPE", "LEFT");
		}
		String sql = QueryService.getInstance().getSQL("mm.CheckDAO.cross.UFW", dynaMap);
		return this.executeQuery("mm.CheckDAO.cross.UFW", sql, param);
	
	}
	 
	/**
	 * 
	 * @param columnList
	 * @param param SRC_ELEM_INFO_ID,ELEM_TP_ID,TG_ELEM_INFO_ID
	 * @return
	 * @throws Exception
	 */
	public CacheResultSet elemCheck(java.util.ArrayList columnList, HashMap param) throws Exception
	{
		String elemType = (String)param.get("ELEM_TP_ID");
		StringBuffer sbs=new StringBuffer();
		StringBuffer sbt=new StringBuffer();
		StringBuffer sb2=new StringBuffer();
		StringBuffer sb3=new StringBuffer();
		StringBuffer sb4=new StringBuffer();
		for ( int i=0;i< columnList.size(); i++)
		{
			ElementTypeColumn column  = (ElementTypeColumn)columnList.get(i);
			if(column.hasAs4Alignment())
			{
				sbs.append("," +column.getSelectName4Alignment()  + " AS SRC_" + column.getColumnName());
				sbt.append("," +column.getSelectName4Alignment() + " AS TG_" + column.getColumnName());
			}	
			 
			else
			{
				sbs.append(", m" + i + ".STR_VAL SRC_" + column.getColumnName());
				sbt.append(", m" + i + ".STR_VAL TG_" + column.getColumnName());
				if(column.getDataType().equals("CLOB"))
				{
					sbs.append(", m" + i + ".STR_VAL SRC_" + column.getColumnName() + "_SEQ");
					sbt.append(", m" + i + ".STR_VAL TG_" + column.getColumnName() + "_SEQ");
					sb2.append(" left outer join MM_ELEM_VAR_COL_MAP m" + i + " on ");
					sb2.append("(m" + i + ".COL_PHSC_NM = '" + column.getColumnName() + "' and m" + i + ".ELEM_TP_ID='" + elemType + "' and m" + i + ".ELEM_INFO_ID=i.ELEM_INFO_ID )");
				
				}
				else
				{
					sb2.append(" left outer join MM_ELEM_VAR_COL_MAP m" + i + " on ");
					sb2.append("(m" + i + ".COL_PHSC_NM = '" + column.getColumnName() + "' and m" + i + ".ELEM_TP_ID='" + elemType + "' and m" + i + ".ELEM_INFO_ID=i.ELEM_INFO_ID)");
				}	
				
			}	
			if(column.isKey())
			{
				 
				if(sb4.length()>0) sb4.append(" and ");
				sb4.append("SRC_" + column.getColumnName() + " = TG_" + column.getColumnName());
			}
			else
			{
				if(sb3.length()>0) sb3.append(" or ");
				if(column.getDataType().equals("CLOB"))
				{
					sb3.append("(SRC_" + column.getColumnName() + " <> TG_" + column.getColumnName()  );
					sb3.append(" and SRC_" + column.getColumnName() + "_SEQ = TG_" + column.getColumnName() + "_SEQ)");
				}
				else
				{
					sb3.append("SRC_" + column.getColumnName() + " <> TG_" + column.getColumnName());
				}	
			}
			
		}
		if(sb3.length()>0)
			sb3.append(" or ");
		
		sb3.append(" TG_NAMESPACE is null ");
	 
		 
		String queryKey = "mm.CheckDAO.elemCheck." + elemType;
		String sql=QueryService.getInstance().getSQL(queryKey);
		if(sql == null)
		{
			queryKey = "mm.CheckDAO.elemCheck";
			sql=QueryService.getInstance().getSQL(queryKey );
		}
		HashMap t = new HashMap();
		t.put("src_fieldlist", sbs.toString());
		t.put("target_fieldlist", sbt.toString());
		t.put("outerjoin", sb2.toString());
		t.put("whereCond", sb3.toString());
		t.put("outerjoinCond", sb4.toString());
		sql = QueryService.getInstance().getSQL(queryKey, t);
		 
		return executeQuery(queryKey,sql, param );
		/*
		 * select * from (select  namespace src_namespace, i.elem_info_id src_elem_info_id {src_fieldlist}
from t_elem_info i  
{outerjoin} where del_yn='N' and i.elem_tp_id=? and i.elem_info_id in
(select r.elem_info_id from t_elem_rel r start with par_elem_info_id=?
connect by prior elem_info_id=par_elem_info_id )) src 
left outer join  
(select  namespace tg_namespace, i.elem_info_id  tg_elem_info_id {target_fieldlist}
from 
t_elem_info i   
{outerjoin} where del_yn='N' and i.elem_tp_id=? and i.elem_info_id in
(select r.elem_info_id from t_elem_rel r start with par_elem_info_id=?
connect by prior elem_info_id=par_elem_info_id )) target
on ( {outerjoinCond} )  where  ?='N' or ( {whereCond} )
		 */
	}
	 
	private HashMap otherElemQuery( java.util.ArrayList src_columnList, java.util.ArrayList target_columnList, HashMap param) throws Exception
	{
		StringBuffer sb3 = new StringBuffer();
		StringBuffer sb4 = new StringBuffer();
		
		sb3.append("TG_NAMESPACE IS NULL");
		for( int i=0 ; i < src_columnList.size() ; i++)
		{
			ElementTypeColumn column  = (ElementTypeColumn)src_columnList.get(i);
			ElementTypeColumn tcolumn = (ElementTypeColumn)target_columnList.get(i);
			String targetColumnName =  tcolumn.getColumnName();
			if(targetColumnName == null || column.getColumnName()==null) continue;
			 
			if(column.isKey() || tcolumn.isKey())
			{
				 
				if(sb4.length()>0) sb4.append(" and ");
				 
				sb4.append("SRC_" +  column.getColumnName()  + " = TG_" + targetColumnName );
			}
			else
			{
				if(column.getDataType().equals("CLOB"))
				{
					sb3.append(" OR (SRC_" + column.getColumnName() + " <> TG_" + targetColumnName  );
					sb3.append(" and SRC_" + column.getColumnName() + "_SEQ = TG_" + targetColumnName + "_SEQ)");
				}
				else
				{
					sb3.append(" OR SRC_" + column.getColumnName() + " <> TG_" + targetColumnName);
				}	
			}
		}
		 
		/*
		 * select * from ( {srcquery} ) src 
left outer join ( {targetquery} ) target on ( {outerjoinCond} )  where ( {whereCond} )
		 */
		 
		HashMap result = new HashMap(); 
		result.put("SRC",getElemQuery((String)param.get("SRC_ELEM_TP_ID"), "SRC", src_columnList ));
		result.put("TARGET",getElemQuery( (String)param.get("TG_ELEM_TP_ID") , "TG", target_columnList ));
		result.put("OUTER_JOIN",sb4.toString());
		 
		result.put("WHERE",sb3.toString());
		return result;
	}
	 
	public CacheResultSet otherElemCheck(   java.util.ArrayList src_columnList, java.util.ArrayList target_columnList, HashMap param) throws Exception
	{
		
		/*
		 * select SRC.*,TARGET.* from ({SRC}) src left outer join 
  ( {TARGET} ) target on ({OUTER_JOIN}) where ?='N' or
 {WHERE}
UNION ALL

select SRC.*,TARGET.* from ( {TARGET} ) target  left outer join  ({SRC}) src 
  on ({OUTER_JOIN}) 
WHERE SRC_NAMESPACE IS NULL
		 */
		String sql = jspeed.base.query.QueryService.getInstance().getSQL("mm.CheckDAO.otherElemCheck.main", otherElemQuery( src_columnList, target_columnList, param));
		 
		return executeQuery("mm.CheckDAO.otherElemCheck.main",sql, param );
		
	}
	public long countOtherElemCheck(  java.util.ArrayList src_columnList, java.util.ArrayList target_columnList, HashMap param) throws Exception
	{
		/*
		 select A.CNT+B.CNT FROM (select count(*) AS CNT from ({SRC}) src left outer join 
  ( {TARGET} ) target on ({OUTER_JOIN}) where 
 ?='N' or ({WHERE}) ) A,
(select count(*) AS CNT from ( {TARGET} ) target  left outer join  ({SRC}) src 
  on ({OUTER_JOIN}) 
WHERE SRC_NAMESPACE IS NULL) B
		 */
		
		String sql = jspeed.base.query.QueryService.getInstance().getSQL("mm.CheckDAO.otherElemCheck.count", otherElemQuery( src_columnList, target_columnList, param));
		CacheResultSet rs =  executeQuery("mm.CheckDAO.otherElemCheck.count",sql, param );
		rs.next();
		return rs.getLong(1);
		
	}
	 
	private String getElemQuery(String elemType, String srcTarget, java.util.ArrayList columnList ) throws Exception
	{
	 
		
		StringBuffer sbs=new StringBuffer();
		 
		StringBuffer sb2=new StringBuffer();
		 
		for ( int i=0;i< columnList.size(); i++)
		{
			ElementTypeColumn column  = (ElementTypeColumn)columnList.get(i);
			if(column.hasAs4Alignment())
			{
				sbs.append("," +column.getSelectName4Alignment()  + " AS " + srcTarget + "_" + column.getColumnName());
			 
				 
			    continue;
			}
			sbs.append(", m" + i + ".STR_VAL "  + srcTarget + "_" + column.getColumnName());
			 
			if(column.getDataType().equals("CLOB"))
			{
				sbs.append(", m" + i + ".STR_VAL "  + srcTarget + "_" + column.getColumnName() + "_SEQ");
				sb2.append(" left outer join MM_ELEM_VAR_COL_VAL m" + i + " on ");
				sb2.append("(m" + i + ".COL_PHSC_NM = '" + column.getColumnName() + "' and m" + i + ".ELEM_TP_ID='" + elemType + "' and m" + i + ".ELEM_INFO_ID=i.ELEM_INFO_ID )");
			
			}
			else
			{
				sb2.append(" left outer join MM_ELEM_VAR_COL_MAP m" + i + " on ");
				sb2.append("(m" + i + ".COL_PHSC_NM = '" + column.getColumnName() + "' and m" + i + ".ELEM_TP_ID='" + elemType + "' and m" + i + ".ELEM_INFO_ID=i.ELEM_INFO_ID)");
			}	
		 
			
		}
		String sql=QueryService.getInstance().getSQL("mm.CheckDAO.otherElemCheck." + elemType);
		if(sql == null)
		{
			sql=QueryService.getInstance().getSQL("mm.CheckDAO.otherElemCheck.defaultType");
		}
		sql=StringHelper.replaceStr(sql, "{srctarget}", srcTarget);
		sql=StringHelper.replaceStr(sql, "{fieldlist}", sbs.toString());
		sql=StringHelper.replaceStr(sql, "{outerjoin}", sb2.toString());
		 
		return sql;
		/*
		 *  select  namespace   {fieldlist}
from t_elem_info i  
{outerjoin} where del_yn='N' and i.elem_tp_id=? and i.elem_info_id in
(select r.elem_info_id from t_elem_rel r, t_elem_rel t where
   r.hier_seq>t.hier_seq
and t.elem_info_id=?
and r.hier_seq<(select  nvl(min(hier_seq),99999999999999999999) from t_elem_rel
n where n.hier_seq>t.hier_seq and
n.hier_lvl<=t.hier_lvl)))  
		 */
	}
	/**
	 * 단위항목간 비교
	 * @param param  SRC_UTW_DIC_ID, TG_UTW_DIC_ID, SESS_LANG_TYPE
	 * @return
	 * @throws Exception
	 */
	public CacheResultSet utwCheck(HashMap param) throws Exception
	{
		HashMap dynaMap  = new HashMap();
		if("Y".equals(param.get("BOTH_COMPARE_YN")))
		{
			dynaMap.put("JOIN_TYPE", "FULL");
		}
		else
		{
			dynaMap.put("JOIN_TYPE", "LEFT");
		}
		String sql = QueryService.getInstance().getSQL("mm.CheckDAO.cross.UTW", dynaMap);
		return this.executeQuery("mm.CheckDAO.cross.UTW", sql, param);
	}
	/**
	 * 충실도
	 * @param param ELEM_TP_ID, ELEM_INFO_ID
	 * @return
	 * @throws Exception
	 */
	public CacheResultSet fidelityCheck( java.util.ArrayList columnList, HashMap param)  throws  Exception
	{
		/*
		 * SELECT I.* {fieldlist} FROM MM_ELEM_INFO I  {outerjoin}
WHERE I.ELEM_TP_ID=? AND I.ELEM_INFO_ID IN                                                                                 
(SELECT R.ELEM_INFO_ID FROM MM_ELEM_REL R START WITH PAR_ELEM_INFO_ID=?
CONNECT BY PRIOR ELEM_INFO_ID=PAR_ELEM_INFO_ID) AND ( {whereCond} )
		 */
		String elemType = (String)param.get("ELEM_TP_ID");
		StringBuffer sb1=new StringBuffer();
		StringBuffer sb2=new StringBuffer();
		StringBuffer sb3=new StringBuffer();
		for ( int i=0;i< columnList.size(); i++)
		{
			ElementTypeColumn column  = (ElementTypeColumn)columnList.get(i);
			sb1.append(", case when m" + i + ".STR_VAL is null then 'x' else 'o' end as " + column.getColumnName());
			if(column.getDataType().equals("CLOB"))
			{
				sb2.append(" left outer join MM_ELEM_VAR_COL_VAL m" + i + " on ");
				sb2.append("(m" + i + ".COL_PHSC_NM = '" + column.getColumnName() + "' and m" + i + ".ELEM_TP_ID='" + elemType + "' and m" + i + ".ELEM_INFO_ID=i.ELEM_INFO_ID and m" + i + ".SEQ=1)");
			
			}
			else
			{
				sb2.append(" left outer join MM_ELEM_VAR_COL_MAP m" + i + " on ");
				sb2.append("(m" + i + ".COL_PHSC_NM = '" + column.getColumnName() + "' and m" + i + ".ELEM_TP_ID='" + elemType + "' and m" + i + ".ELEM_INFO_ID=i.ELEM_INFO_ID)");
			}	
//			if(i>0) sb3.append(" or ");
//			sb3.append("m" + i + ".STR_VAL is null");
			
		}
		String sql=QueryService.getInstance().getSQL("mm.CheckDAO.fidelityCheck");
		sql=StringHelper.replaceStr(sql, "{fieldlist}", sb1.toString());
		sql=StringHelper.replaceStr(sql, "{outerjoin}", sb2.toString());
		sql=StringHelper.replaceStr(sql, "{whereCond}", sb3.toString());
		return executeQuery("mm.CheckDAO.fidelityCheck",sql, param );
	}
	/**
	 * 충실도
	 * @param param ELEM_TP_ID, ELEM_INFO_ID
	 * @return
	 * @throws Exception
	 */
	public CacheResultSet fidelityCheckFromLog( java.util.ArrayList columnList, HashMap param)  throws  Exception
	{
		/*
		 SELECT i.* , {fieldlist} FROM 
 MM_FIDELITY_CHECK_DETAIL D,
 V_ELEM_INFO_HIST_ALL i {outerjoin}
WHERE D.DAT_STRC_ID=? AND i.ELEM_TP_ID=?  
AND D.START_DT=? AND
D.ELEM_INFO_ID = i.ELEM_INFO_ID AND
D.START_DT BETWEEN i.START_DT AND NVL(i.END_DT, ? )
		 */
		String elemType = (String)param.get("ELEM_TP_ID");
		StringBuffer sb1=new StringBuffer();
		StringBuffer sb2=new StringBuffer();
		 
		for ( int i=0;i< columnList.size(); i++)
		{
			ElementTypeColumn column  = (ElementTypeColumn)columnList.get(i);
			sb1.append(", case when m" + i + ".STR_VAL is null then 'x' else 'o' end as " + column.getColumnName());
			if(column.getDataType().equals("CLOB"))
			{
				sb2.append(" left outer join V_ELEM_VAR_COL_VAL_HIST_ALL m" + i + " on ");
				sb2.append("(m" + i + ".COL_PHSC_NM = '" + column.getColumnName() + "' and m" + i + ".ELEM_TP_ID='" + elemType + "' and m" + i + ".ELEM_INFO_ID=i.ELEM_INFO_ID and SEQ=1 AND i.START_DT=m" + i + ".START_DT)");
			
			}
			else
			{
				sb2.append(" left outer join V_ELEM_VAR_COL_MAP_HIST_ALL m" + i + " on ");
				sb2.append("(m" + i + ".COL_PHSC_NM = '" + column.getColumnName() + "' and m" + i + ".ELEM_TP_ID='" + elemType + "' and m" + i + ".ELEM_INFO_ID=i.ELEM_INFO_ID AND i.START_DT=m" + i + ".START_DT)");
			}	
			 
			
		}
		String sql=QueryService.getInstance().getSQL("mm.CheckDAO.fidelityCheck.fromLog");
		sql=StringHelper.replaceStr(sql, "{fieldlist}", sb1.toString());
		sql=StringHelper.replaceStr(sql, "{outerjoin}", sb2.toString());
		param.put("TODAY", DateUtil.getToday());
		return executeQuery("mm.CheckDAO.fidelityCheck.fromLog",sql, param );
	}
	
	/**
	 * 
	 * @param columnList notnull check 대상이 되는 가변 컬럼
	 * @param param	ELEM_TP_ID, ELEM_INFO_ID
	 * @return
	 * @throws Exception
	 */
	public long countFidelityCheck( java.util.ArrayList columnList, HashMap param)  throws  Exception
	{
		/*
		 * SELECT COUNT(*) FROM MM_ELEM_INFO I  {outerjoin}
WHERE I.ELEM_TP_ID=? AND I.ELEM_INFO_ID IN                                                                                 
(SELECT R.ELEM_INFO_ID FROM MM_ELEM_REL R START WITH PAR_ELEM_INFO_ID=? CONNECT BY PRIOR ELEM_INFO_ID=PAR_ELEM_INFO_ID) AND ( {whereCond} )
		 */
		String elemType = (String)param.get("ELEM_TP_ID");
		  
		StringBuffer sb2=new StringBuffer();
		StringBuffer sb3=new StringBuffer();
		for ( int i=0;i< columnList.size(); i++)
		{
			ElementTypeColumn column  = (ElementTypeColumn)columnList.get(i);
			if(column.getDataType().equals("CLOB"))
			{
				sb2.append(" left outer join MM_ELEM_VAR_COL_VAL m" + i + " on ");
				sb2.append("(m" + i + ".COL_PHSC_NM = '" + column.getColumnName() + "' and m" + i + ".ELEM_TP_ID='" + elemType + "' and m" + i + ".elem_info_id=i.elem_info_id and  m" + i + ".seq=1)");
			
			}
			else
			{
				sb2.append(" left outer join MM_ELEM_VAR_COL_MAP m" + i + " on ");
				sb2.append("(m" + i + ".COL_PHSC_NM = '" + column.getColumnName() + "' and m" + i + ".ELEM_TP_ID='" + elemType + "' and m" + i + ".elem_info_id=i.elem_info_id)");
			}	
			if(i>0) sb3.append(" or ");
			sb3.append("m" + i + ".str_val is null");
			
		}
		String sql=QueryService.getInstance().getSQL("mm.CheckDAO.fidelityCheck.count");
		sql=StringHelper.replaceStr(sql, "{outerjoin}", sb2.toString());
		sql=StringHelper.replaceStr(sql, "{whereCond}", sb3.toString());
	 
		 
		CacheResultSet rs = executeQuery("mm.CheckDAO.fidelityCheck.count",sql, param , 0, -1);
		rs.next();
		return rs.getLong(1);
		
	}
	public CacheResultSet countFidelityCheckFromLogByDatStrId(   HashMap param)  throws  Exception
	{
		/* select ELEM_TP_ID, ALL_COUNT, ERR_COUNT FROM MM_FIDELITY_CHECK WHERE 	DAT_STRC_ID=? AND START_DT=? */
		return executeQuery("mm.CheckDAO.countFidelityCheckFromLogByDatStrId", param );
	
	}
	public String getLastStartDateFidelityCheckByDatStrId (   HashMap param)  throws  Exception
	{
		/* select max(START_DT) FROM MM_FIDELITY_CHECK WHERE 	DAT_STRC_ID=?   */
		CacheResultSet rs =  executeQuery("mm.CheckDAO.getLastStartDateFidelityCheckByDatStrId", param );
		rs.next();
		return rs.getString(1);
	
	}
	public String getLastStartDateObservanceCheckByDatStrId (   HashMap param)  throws  Exception
	{
		/* select max(START_DT) FROM MM_OBSERVANCE_CHECK WHERE 	DAT_STRC_ID=?   */
		CacheResultSet rs =  executeQuery("mm.CheckDAO.getLastStartDateObservanceCheckByDatStrId", param );
		rs.next();
		return rs.getString(1);
	
	}
	private boolean isFirstPage(HashMap param)
	{
		if(param.get("PAGE_NUM") == null)
		{
			return true;
			
		}
		try
		{
			if(Integer.parseInt((String)param.get("PAGE_NUM"))>1)
			{
				return false;
			}
		}
		catch(Exception ignore)
		{
			
		}
		return true;
	}
	/**
	 * 준수도 history
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public CacheResultSet observanceCheckHistory(HashMap param) throws  Exception
	{
		return executeQuery("mm.CheckDAO.observanceCheck.history", param );
	 
	}
	/**
	 * 최근 전체 준수도율
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public CacheResultSet observanceCheckLastAllList(HashMap param) throws  Exception
	{
		return executeQuery("mm.CheckDAO.observanceCheck.lastAllList", param );
	 
	}
	/**
	 * 최근 전체 충실도율
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public CacheResultSet fidelityCheckLastAllList(HashMap param) throws  Exception
	{
		return executeQuery("mm.CheckDAO.fidelityCheck.lastAllList", param );
	 
	}
	/**
	 * 준수도
	 * @param param
	 * @return
	 * @throws Exception
	 */
	
	public CacheResultSet observanceCheck(HashMap param) throws  Exception
	{
/*
 * 기존 SQL문
 * ---------------------
 * SELECT   I.ELEM_INFO_ID AS SRC_ID,  I.NAMESPACE SRC_NAMESPACE,    I.ELEM_PHSC_NM SRC_PHSC_NM, I.ELEM_LGCL_NM 
SRC_LGCL_NM,
M.STR_VAL AS SRC_DATATYPE_TP_CD, N.STR_VAL AS SRC_COLUMN_SIZE, L.STR_VAL AS SRC_DECIMAL_DIGITS,
U.PHSC_NM AS TG_PHSC_NM, U.UFW_NM AS TG_LGCL_NM, 
U.DATATYPE_TP_CD AS TG_DATATYPE_TP_CD, U.PRCS AS TG_COLUMN_SIZE, 
U.UFW_ID AS TG_ID, U.SCAL TG_DECIMAL_DIGITS 
FROM MM_ELEM_INFO I
LEFT OUTER JOIN (SELECT ELEM_INFO_ID, STR_VAL FROM MM_ELEM_VAR_COL_MAP WHERE   COL_PHSC_NM='COLUMN_SIZE') N
ON I.ELEM_INFO_ID=N.ELEM_INFO_ID
LEFT OUTER JOIN (SELECT ELEM_INFO_ID, STR_VAL FROM MM_ELEM_VAR_COL_MAP WHERE   COL_PHSC_NM='DECIMAL_DIGITS') L
ON I.ELEM_INFO_ID=L.ELEM_INFO_ID

 
LEFT OUTER JOIN MM_UFW U
ON   U.UFW_DIC_ID=? AND {outerJoin}  ,
 (SELECT ELEM_INFO_ID, STR_VAL FROM MM_ELEM_VAR_COL_MAP WHERE   COL_PHSC_NM='DATATYPE_TP_CD') M
where  I.ELEM_INFO_ID IN
(SELECT R.ELEM_INFO_ID FROM MM_ELEM_REL R START WITH PAR_ELEM_INFO_ID=? CONNECT BY PRIOR ELEM_INFO_ID=PAR_ELEM_INFO_ID) 
AND  I.DEL_YN='N'
AND M.ELEM_INFO_ID=I.ELEM_INFO_ID  AND N.ELEM_INFO_ID=I.ELEM_INFO_ID
AND I.ELEM_PHSC_NM IS NOT NULL AND I.ELEM_TP_ID  != 'ProcedureColumn'  {where}
 */	
/*		
		String baseType = (String)param.get("DIFF_TYPE");
		String outerJoin = "I.ELEM_PHSC_NM=u.phsc_nm";
		if("L".equals(baseType))
		{
			outerJoin = "I.ELEM_LGCL_NM=u.ufw_nm";
		}
		
		StringBuffer where = new StringBuffer();
		if("Y".equals(param.get("ONLY_DEFF")))
		{
			
			
			where.append(" AND (");
			if( "P".equals( baseType) || "L".equals( baseType))
			{
				where.append( " UFW_NM is null " );
				
			}
			else 
			{
				where.append(" ( UFW_NM is null or UFW_NM<>I.ELEM_LGCL_NM ) ");
			}
				// data type 틀린 것
			if("Y".equals(param.get("INCL_DATATYPE_DEFF")))
			{
				where.append( " or M.STR_VAL !=  DATATYPE_TP_CD ");
			}
				//			사이즈가 틀린 것
			if("Y".equals(param.get("INCL_DATASIZE_DEFF")))
			{
				where.append( " or (  N.STR_VAL != PRCS or SCAL != L.STR_VAL) ");
			}
			where.append( ")" );
		}	
		
		HashMap dysql = new HashMap();
		dysql.put("outerJoin", outerJoin);
		dysql.put("where", where.toString());
		String sql=QueryService.getInstance().getSQL("mm.CheckDAO.observanceCheck", dysql);
		return executeQuery("mm.CheckDAO.observanceCheck", sql, param );
*/		
		String tElemTpId = (String)param.get("T_ELEM_TP_ID");//모형요소정보ID( Schema or Model ...)
		String tMdAttrType = (String)param.get("T_MD_ATTR_TYPE");//모델속성타입(L or P or PL)
		String tAutCltYn = (String)param.get("T_AUT_CLT_YN");//자동수집여부( Y or N )
		/*
		 * if ( 모형요소정보ID == "Schema" ) {
		 *   "물리명";
		 * } else {
		 *   if ( 자동수집여부 == "Y" ) {
		 *     if ( 모델속성타입 == "논리" ) {
		 *       "논리명";
		 *     } else if ( 모델속성타입  == "물리" ) {
		 *       "물리명";
		 *     } else if ( 모델속성타입 == "물리논리" ) {
		 *       "논리명";
		 *     }
		 *   } else {
		 *     "논리명";
		 *   }
		 * } 
		 */
		String whereleftquery = "";
		String whererightquery = "";
		if ( "Schema".equalsIgnoreCase(tElemTpId) ) {
			whereleftquery += "T_COL_PHSC_NM";
			whererightquery += "V_COL_PHSC_NM(+)";
		} else {
			if ( "Y".equalsIgnoreCase(tAutCltYn) ) {
				if ( "L".equalsIgnoreCase(tMdAttrType) ) {
					whereleftquery += "T_COL_LGCL_NM";
					whererightquery += "V_COL_LGCL_NM(+)";
				} else if ( "P".equalsIgnoreCase(tMdAttrType) ) {
					whereleftquery += "T_COL_PHSC_NM";
					whererightquery += "V_COL_PHSC_NM(+)";
				} else {
					whereleftquery += "T_COL_LGCL_NM";
					whererightquery += "V_COL_LGCL_NM(+)";
				}
			} else {
				whereleftquery += "T_COL_LGCL_NM";
				whererightquery += "V_COL_LGCL_NM(+)";
			}
		}
		HashMap dysql = new HashMap();
		dysql.put("leftJoin", whereleftquery);
		dysql.put("rightJoin", whererightquery);
		String sql=QueryService.getInstance().getSQL("mm.CheckDAO.observanceCheck", dysql);
		if("Y".equals(param.get("ONLY_DIFF")))
		{
			sql = "SELECT * FROM (" + sql + ") Z WHERE FLAG=0";
		}
		return executeQuery("mm.CheckDAO.observanceCheck", sql, param );
	}
	public CacheResultSet observanceCheckByModel(HashMap param) throws  Exception
	{
 
		/*String tElemTpId = (String)param.get("T_ELEM_TP_ID");//모형요소정보ID( Schema or Model ...)
		String tMdAttrType = (String)param.get("T_MD_ATTR_TYPE");//모델속성타입(L or P or PL)
		String tAutCltYn = (String)param.get("T_AUT_CLT_YN");//자동수집여부( Y or N )
		 
		String whereleftquery = "";
		String whererightquery = "";
		if(param.get("DIFF_TYPE") != null)
		{
			if("P".equals(param.get("DIFF_TYPE") ))
			{
				whereleftquery += "T_COL_PHSC_NM";
				whererightquery += "V_COL_PHSC_NM(+)";
			}
			else
			{
				whereleftquery += "T_COL_LGCL_NM";
				whererightquery += "V_COL_LGCL_NM(+)";
			}
		}
		else
		{
			if ( "Schema".equalsIgnoreCase(tElemTpId) ) {
				whereleftquery += "T_COL_PHSC_NM";
				whererightquery += "V_COL_PHSC_NM(+)";
			} else {
				if ( "Y".equalsIgnoreCase(tAutCltYn) ) {
					if ( "L".equalsIgnoreCase(tMdAttrType) ) {
						whereleftquery += "T_COL_LGCL_NM";
						whererightquery += "V_COL_LGCL_NM(+)";
					} else if ( "P".equalsIgnoreCase(tMdAttrType) ) {
						whereleftquery += "T_COL_PHSC_NM";
						whererightquery += "V_COL_PHSC_NM(+)";
					} else {
						whereleftquery += "T_COL_LGCL_NM";
						whererightquery += "V_COL_LGCL_NM(+)";
					}
				} else {
					whereleftquery += "T_COL_LGCL_NM";
					whererightquery += "V_COL_LGCL_NM(+)";
				}
			}
		}
		HashMap dysql = new HashMap();
		dysql.put("leftJoin", whereleftquery);
		dysql.put("rightJoin", whererightquery);
		String sql=QueryService.getInstance().getSQL("mm.CheckDAO.observanceCheckByModel", dysql);
		*/
		String sql=QueryService.getInstance().getSQL("mm.CheckDAO.observanceCheckByModel");
		if("Y".equals(param.get("ONLY_DIFF")))
		{
			sql = "SELECT * FROM (" + sql + ") Z WHERE FLAG=0";
		}
		return executeQuery("mm.CheckDAO.observanceCheckByModel", sql, param );
	}
	/**
	 * 준수도
	 * @param param
	 * @return
	 * @throws Exception
	 */
	
	public CacheResultSet observanceCheckFromLog(HashMap param) throws  Exception
	{
	 
		param.put("TODAY", DateUtil.getToday());
		return executeQuery("mm.CheckDAO.observanceCheckFromLog",   param );
		 
	} 
	/**
	 * 
	 * @param param DAT_STRC_ID
	 * @throws Exception
	 */
	public int insertObservanceCheckCountByDatStrc(HashMap param) throws  Exception
	{
		CacheResultSet rs = executeQuery("mm.TdatstrcelemmapDAO.findAllDatStrcElemInfoByDatStrc", param );
		int errCnt=0;
//		param.put("START_DT", DateUtil.getToday());
//		param.put("MODEL_TYPE","P");
//		param.put("ONLY_DIFF", "Y");
//		param.put("T_MD_ATTR_TYPE","P");//모델속성타입(L or P or PL)
//		param.put("T_AUT_CLT_YN","Y");//자동수집여부( Y or N )
		StringBuffer sb = new StringBuffer();
		String oldDatSrcId = null;
		int strIdx = 0;
		for(int i=0; rs.next() ; i++, strIdx++)
		{
			if( !rs.getString("DAT_STRC_ID").equals(oldDatSrcId))
			{
				if(oldDatSrcId != null )
				{
					this.executeBatchAll();
					param.put("ELEM_INFO_IDS", sb.toString());
					param.put("ERR_COUNT", "" + errCnt); 
					insertObservanceCheckCount(param);
					sb.setLength(0);
					strIdx=0;
					errCnt=0;
				}
				oldDatSrcId = rs.getString("DAT_STRC_ID");
			}
			if(strIdx>0) sb.append(",");
			sb.append("'").append(rs.getString("ELEM_INFO_ID")).append("'");
			param.putAll(rs.getMap());
			
			/*
			 param.put("ELEM_TP_ID", "Entity");
			 TeleminfoDAO elemInfoDao= new TeleminfoDAO(this.getDBAssistant());
			
			java.sql.ResultSet entityRs = elemInfoDao.findIdByNameSpaceLike( param);
			try
			{
				param.put("T_ELEM_TP_ID",rs.getString("ELEM_TP_ID"));//모형요소정보ID( Schema or Model ...)
				
				while(entityRs.next())
				{
					param.put("ELEM_INFO_ID", entityRs.getString("ELEM_INFO_ID"));
					CacheResultSet rs1 = this.observanceCheck(param);
					errCnt += insertObservanceCheck(param, rs1);
					if(i>0) sb.append(",");
					
				}
			}
			finally
			{
				entityRs.close();
			}*/
			CacheResultSet rs1 = this.observanceCheckByModel(param);
			errCnt += insertObservanceCheck(param, rs1);
			
			
		}
		 
		if(oldDatSrcId != null)
		{
			this.executeBatchAll();
			param.put("ELEM_INFO_IDS", sb.toString());
			param.put("ERR_COUNT", "" + errCnt); 
			insertObservanceCheckCount(param);
		}
		return errCnt;
	}
	
	private int insertObservanceCheck(HashMap param, CacheResultSet rs1 ) throws Exception
	{
		while(rs1.next())
		{
			/* insert into MM_OBSERVANCE_CHECK_DETAIL (DAT_STRC_ID,
	START_DT,
	SRC_ID,
	TG_ID ) values(?, ?, ?, ? ) */
			param.putAll(rs1.getMap());
			//System.out.println(rs1.getMap());
			this.addBatch("mm.CheckDAO.insertObservanceCheck", param, 10000);
		}
		return rs1.getRowCount();
	}
	/**
	 * 
	 * @param param UFW_DIC_ID, ELEM_INFO_ID
	 * @return
	 * @throws Exception
	 */
	private int insertObservanceCheckCount(HashMap param ) throws  Exception
	{
		/*
		 * select count(*) from MM_ELEM_INFO i,  MM_ELEM_INFO j where 
i.LNK_INFO_ID=j.LNK_INFO_ID AND i.ELEM_TP_ID in ('Column','Attribute') AND
i.namespace like j.namespace || '%'
and j.elem_info_id in ({ELEM_INFO_IDS})
		 */
		
		String sql = jspeed.base.query.QueryService.getInstance().getSQL("mm.CheckDAO.observanceCheckCount.all",param);
		CacheResultSet rs = this.executeQuery("mm.CheckDAO.observanceCheckCount.all",sql, param);
		rs.next();
		param.put("ALL_COUNT", rs.getObject(1));
	 
		param.put("END_DT", DateUtil.getToday());
		
		/*
		 *  INSERT INTO MM_OBSERVANCE_CHECK (DAT_STRC_ID,START_DT, END_DT,ALL_COUNT,ERR_COUNT,SCHD_ID) values
		 *  	(?,?,?, ?, ?, ?)
		 */
		return executeUpdate("mm.CheckDAO.observanceCheckCount.insert", param);
	}
	
	private void addCount(String elemType, int count, HashMap errMap)
	{
		String temp = (String) errMap.get(elemType);
		if(temp == null)
		{
			errMap.put(elemType, "" + count);
		}
		else
		{
			errMap.put(elemType, "" + (Integer.parseInt(temp) + count));
		}
	}
	/**
	 * 
	 * @param param DAT_STRC_ID
	 * @throws Exception
	 */
	public int insertFidelityCheckByDatStrc(HashMap param) throws  Exception
	{
		CacheResultSet rs = executeQuery("mm.TdatstrcelemmapDAO.findAllDatStrcElemInfoByDatStrc", param );
		HashMap typeErrCnt=new HashMap();
		param.put("START_DT", DateUtil.getToday());
		param.put("ONLY_DIFF", "Y");
		StringBuffer sb = new StringBuffer();
		ArrayList fTypes = ElementTypeHandler.getInstance().getFidelityElementType();
		for(int i=0; rs.next() ; i++)
		{
			String elemInfoId = rs.getString("ELEM_INFO_ID");
			param.putAll(rs.getMap());
			for( int j=0 ; j < fTypes.size(); j++)
			{
				ElementType type = (ElementType)fTypes.get(j);
				if(type.getRootElem().equals(rs.getString("ELEM_TP_ID")))
				{
					param.put("ELEM_TP_ID", type.getElementType());
					param.put("ELEM_INFO_ID", elemInfoId);
					CacheResultSet rs1 = fidelityCheck(type.getNotNullCheckColumnList(),param);
					
					addCount(type.getElementType(), insertFidelityCheck(param, rs1), typeErrCnt);
				}	
			}
			if(i>0) sb.append(",");
			sb.append("'").append(rs.getString("ELEM_INFO_ID")).append("'");
		}
		this.executeBatchAll();
		param.put("ELEM_INFO_IDS", sb.toString());
		insertFidelityCheckCount(param, typeErrCnt);
		java.util.Iterator it = typeErrCnt.values().iterator();
		int totalErr=0;
		while(it.hasNext())
		{
			totalErr += Integer.parseInt(it.next().toString());
		}
		return totalErr;
		
	}
	
	private int insertFidelityCheck(HashMap param, CacheResultSet rs1 ) throws Exception
	{
		while(rs1.next())
		{
			/* insert into MM_FIDELITY_CHECK_DETAIL (DAT_STRC_ID,START_DT,ELEM_INFO_ID) values(?, ?, ? ) */
			param.putAll(rs1.getMap());
			 
			this.addBatch("mm.CheckDAO.insertFidelityCheck", param, 10000);
		}
		return rs1.getRowCount();
	}
	/**
	 * 
	 * @param param UFW_DIC_ID, ELEM_INFO_ID
	 * @return
	 * @throws Exception
	 */
	private void insertFidelityCheckCount(HashMap param, HashMap typeErrCnt ) throws  Exception
	{
		/*
		 * select count(*) from MM_ELEM_INFO i,  MM_ELEM_INFO j where 
i.LNK_INFO_ID=j.LNK_INFO_ID AND i.ELEM_TP_ID ? AND
i.NAMESPACE  like j.NAMESPACE  || '%'
and j.ELEM_INFO_ID in ({ELEM_INFO_IDS})
		 */
		java.util.Iterator it = typeErrCnt.keySet().iterator();
		param.put("END_DT", DateUtil.getToday());
		while(it.hasNext())
		{
			String type = (String)it.next();
			param.put("ELEM_TP_ID", type);
			String sql = jspeed.base.query.QueryService.getInstance().getSQL("mm.CheckDAO.fidelityCheckCount.all",param);
			CacheResultSet rs = this.executeQuery("mm.CheckDAO.fidelityCheckCount.all", sql, param);
			rs.next();
			param.put("ALL_COUNT", rs.getObject(1));
			 
			param.put("ERR_COUNT", typeErrCnt.get(type));
			if(rs.getInt(1)==0 || typeErrCnt.get(type).equals("0"))
			{
				continue;
			}
			
			/*
			 *  INSERT INTO MM_FIDELITY_CHECK  (DAT_STRC_ID,START_DT, END_DT,ALL_COUNT,ERR_COUNT,SCHD_ID,ELEM_TP_ID) values
			 *  	(?,?,?, ?, ?, ?,?)
			 */
			executeUpdate("mm.CheckDAO.fidelityCheckCount.insert", param);
		}	
	}
	public CacheResultSet dataMapTrgElemGet(HashMap param) throws Exception
	{
	
		/*
		     SELECT DATAMAP_ID
		           ,TRG_ELEM_INFO_ID AS ELEM_INFO_ID
		           ,TRG_ELEM_PHSC_NM AS ELEM_PHSC_NM
		           ,TRG_ELEM_LGCL_NM AS ELEM_LGCL_NM
		           ,REL_TP_CD
		           ,REL_DESC
		           ,TRG_ELEM_TP_ID AS ELEM_TP_ID
		       FROM V_DATAMAP
		      WHERE SRC_ELEM_INFO_ID=? 
		 */
		
		return executeQuery("mm.CheckDAO.dataMapTrgElemGet",param);
	}
	
	public CacheResultSet dataMapSrcElemGet(HashMap param) throws Exception
	{
		/*
		     SELECT DATAMAP_ID
		           ,SRC_ELEM_INFO_ID AS ELEM_INFO_ID
		           ,SRC_ELEM_PHSC_NM AS ELEM_PHSC_NM
		           ,SRC_ELEM_LGCL_NM AS ELEM_LGCL_NM
		           ,REL_TP_CD 
		           ,REL_DESC
		           ,SRC_ELEM_TP_ID AS ELEM_TP_ID
		       FROM V_DATAMAP
		      WHERE TRG_ELEM_INFO_ID=? 
		 */
		
		return executeQuery("mm.CheckDAO.dataMapSrcElemGet",param);
	}
	
	/**
	 * 데이터맵에서 논리연결의 정보를 가져오는 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public CacheResultSet findElemLgclRelByPrimaryKey(HashMap param) throws Exception
	{
		/*
		 	SELECT *
		 	  FROM T_ELEM_LGCL_REL
		 	 WHERE ELEM_LGCL_REL_ID=?
		 */
		return executeQuery("mm.CheckDAO.findElemLgclRelByPrimaryKey",param);
	}
	
	/**
	 * 
	 * @param param DAT_STRC_ID
	 * @throws Exception
	 */
	
	public int insertMetamapCheckByLgclRelGrp(HashMap param) throws  Exception
	{
				
		this.begin();
		// 전체대상 카운트 리턴
		CacheResultSet rs1 = executeQuery("mm.MMmetamapcheckDAO.allCountMetamapCheckByLgclRelGrp", param );//REL_GRP_ID
		Integer allCount = null;
		if(rs1.next()){
			int tmpAllCount = rs1.getInt("ALL_COUNT");
			allCount = new Integer(tmpAllCount);
			System.out.println("================================================= 전체 대상 카운트 : "+ allCount);
		}
		
		// 오류입력하고  카운트 리턴 
		int tmpErrCount = this.executeUpdate("mm.MMmetamapcheckdetailDAO.insertMetamapCheckDetailByLgclRelGrp", param );//REL_GRP_ID, START_DT
		Integer errCount = new Integer(tmpErrCount);
		System.out.println("================================================= 오류 카운트 : "+ errCount);
		
		param.put("ALL_COUNT",allCount);
		param.put("ERR_COUNT",errCount);
		executeQuery("mm.MMmetamapcheckDAO.insertMetamapCheckByLgclRelGrp", param );//REL_GRP_ID,START_DT,ALL_COUNT,ERR_COUNT
		
		this.commit();
		return tmpErrCount;	// 오류카운트 리턴	
	}
	
	public CacheResultSet metamapCheckLastAllList(HashMap param) throws  Exception
	{
		return executeQuery("mm.MMmetamapcheckDAO.metamapCheckLastAllList", param );//SCHD_ID, REL_GRP_ID
	}
	
	public CacheResultSet findAgreeUserListByUFW(CacheResultSet rs) throws  Exception
	{
/* 
SELECT U.USER_ID
	, U.USER_NM
	, GET_GROUP_NAME( U.USER_ID ) AS GROUP_NM
	, GET_ROLE_NAME( U.USER_ID ) AS ROLE_NM
	, U.CELL_PHONE
	, B.DAT_STRC_NM
	, (CASE WHEN B.APPR_ESSEN_YN='Y' THEN 'Y' ELSE 'N' END)  AS ESSEN_YN
FROM MM_LNK_INFO A, MM_DAT_STRC B, MM_DAT_STRC_ELEM_MAP C, MM_ELEM_VAR_COL_MAP D, C_USER U,
	(SELECT A.ELEM_INFO_ID AS MODEL_ELEM_INFO_ID
		   ,B.ELEM_INFO_ID AS ENTITY_ELEM_INFO_ID
		   ,C.ELEM_INFO_ID AS ATTR_ELEM_INFO_ID
		   ,A.LNK_INFO_ID
		   ,FN_ELEMVARCOL(B.ELEM_INFO_ID,'MAINOWNER_ID') AS OWNER_ID
	FROM 
		(SELECT 
		   ELEM_INFO_ID, LNK_INFO_ID
		FROM MM_ELEM_INFO
		WHERE ELEM_TP_ID = 'Model' AND DEL_YN='N') A,
		  
		(SELECT 
		   R.PAR_ELEM_INFO_ID, E.ELEM_INFO_ID, LNK_INFO_ID
		FROM MM_ELEM_INFO E, MM_ELEM_REL R
		WHERE E.ELEM_INFO_ID=R.ELEM_INFO_ID
		  AND (R.REL_TP_CD='RLTP_L' OR R.REL_TP_CD='RLTP_R')
		  AND E.ELEM_TP_ID = 'Entity'
		  AND DEL_YN='N') B,
		  
		(SELECT 
		   R.PAR_ELEM_INFO_ID, E.ELEM_INFO_ID, LNK_INFO_ID
		FROM MM_ELEM_INFO E, MM_ELEM_REL R
		WHERE E.ELEM_INFO_ID=R.ELEM_INFO_ID
		  AND (R.REL_TP_CD='RLTP_L' OR R.REL_TP_CD='RLTP_R')
		  AND E.ELEM_TP_ID = 'Attribute'
		  {KEY_LIST}
		  AND DEL_YN='N') C
	
	WHERE A.ELEM_INFO_ID=B.PAR_ELEM_INFO_ID
		  AND B.ELEM_INFO_ID=C.PAR_ELEM_INFO_ID) E
		  
WHERE A.LNK_INFO_ID=E.LNK_INFO_ID
	  AND E.MODEL_ELEM_INFO_ID=C.ELEM_INFO_ID
	  AND B.DAT_STRC_ID=C.DAT_STRC_ID
	  AND E.MODEL_ELEM_INFO_ID=D.ELEM_INFO_ID
	  AND A.AUTO_COLLECT_YN='N'
	  AND D.COL_PHSC_NM='MODEL_TYPE'
	  AND D.STR_VAL='P'
	  AND E.OWNER_ID=U.USER_ID
	  AND U.DEL_YN='N'
	  AND U.ACTIVE_YN='Y'
	  AND B.UFW_DIC_ID = ?
GROUP BY USER_ID, USER_NM, CELL_PHONE, DAT_STRC_NM, APPR_ESSEN_YN
*/
		StringBuffer keyList  = new StringBuffer();
		HashMap p = new HashMap ();
		HashMap param = new HashMap();
		try {
			while(rs.next()){
				String ufwNm = rs.getString("UFW_NM");
				String phscNm = rs.getString("PHSC_NM");
				String ufwDicId = rs.getString("UFW_DIC_ID");
				
				if( keyList.length() < 1 ) {
					param.put("UFW_DIC_ID", ufwDicId);
					keyList.append(" AND ((E.ELEM_PHSC_NM = '" + phscNm + "' AND E.ELEM_LGCL_NM = '" + ufwNm + "') ");
				} else {
					keyList.append(" OR (E.ELEM_PHSC_NM = '" + phscNm + "' AND E.ELEM_LGCL_NM = '" + ufwNm + "') ");
				}
			} 
			
			if( keyList.length() >= 1 ) {
				keyList.append(") ");
			}
			
			p.put("KEY_LIST", keyList.toString());
			
			String query = jspeed.base.query.QueryService.getInstance().getSQL("mm.CheckDAO.findAgreeUserListByUFW", p);
			return this.executeQuery("mm.CheckDAO.findAgreeUserListByUFW", query,  param );
			
		} catch(Exception e) {
			throw e;
		} finally {

		}
		
	}
	/**
	 * 
	 * <pre>
	 * 2007.08.17 남상우 추가 준수도 검사 선행단계
	 * </pre>
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public CacheResultSet findModelTypeByElemInfoId(HashMap param) throws  Exception {
		return executeQuery("mm.CheckDAO.findModelTypeByElemInfoId", param );
	}
	/**
	 * 
	 * <pre>
	 * 모델작성요청맵핑 준수도검사 > 준수도 검사 선행단계
	 * 2007.08.22 남상우 추가
	 * </pre>
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public CacheResultSet findModelTypeModelReqByElemInfoId(HashMap param) throws  Exception {
		return executeQuery("mm.CheckDAO.findModelTypeModelReqByElemInfoId", param );
	}
	/**
	 * 
	 * <pre>
	 * 모델작성요청맵핑 준수도검사 > 준수도 검사
	 * 2007.08.22 남상우 추가
	 * </pre>
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public CacheResultSet observanceCheckModelReq(HashMap param) throws  Exception {
		String tElemTpId = (String)param.get("T_ELEM_TP_ID");//모형요소정보ID( Schema or Model ...)
		String tMdAttrType = (String)param.get("T_MD_ATTR_TYPE");//모델속성타입(L or P or PL)
		String tAutCltYn = (String)param.get("T_AUT_CLT_YN");//자동수집여부( Y or N )
		/*
		 * if ( 모형요소정보ID == "Schema" ) {
		 *   "물리명";
		 * } else {
		 *   if ( 자동수집여부 == "Y" ) {
		 *     if ( 모델속성타입 == "논리" ) {
		 *       "논리명";
		 *     } else if ( 모델속성타입  == "물리" ) {
		 *       "물리명";
		 *     } else if ( 모델속성타입 == "물리논리" ) {
		 *       "논리명";
		 *     }
		 *   } else {
		 *     "논리명";
		 *   }
		 * } 
		 */
		String whereleftquery = "";
		String whererightquery = "";
		if ( "Schema".equalsIgnoreCase(tElemTpId) ) {
			whereleftquery += "T_COL_PHSC_NM";
			whererightquery += "V_COL_PHSC_NM(+)";
		} else {
			if ( "Y".equalsIgnoreCase(tAutCltYn) ) {
				if ( "L".equalsIgnoreCase(tMdAttrType) ) {
					whereleftquery += "T_COL_LGCL_NM";
					whererightquery += "V_COL_LGCL_NM(+)";
				} else if ( "P".equalsIgnoreCase(tMdAttrType) ) {
					whereleftquery += "T_COL_PHSC_NM";
					whererightquery += "V_COL_PHSC_NM(+)";
				} else {
					whereleftquery += "T_COL_LGCL_NM";
					whererightquery += "V_COL_LGCL_NM(+)";
				}
			} else {
				whereleftquery += "T_COL_LGCL_NM";
				whererightquery += "V_COL_LGCL_NM(+)";
			}
		}
		HashMap dysql = new HashMap();
		dysql.put("leftJoin", whereleftquery);
		dysql.put("rightJoin", whererightquery);
		
		
		String sql=null;
		if(com.itplus.mm.server.collector.CollectorUtil.isFromCollectModel())
		{
			com.itplus.mm.dao.elem.TeleminfoReqDAO.setDynaSql(dysql, param);
			sql=QueryService.getInstance().getSQL("mm.CheckDAO.observanceCheckModelReqByEntityIds", dysql);
			return executeQuery("mm.CheckDAO.observanceCheckModelReqByEntityIds", sql, param );
		}
		else
		{
			sql=QueryService.getInstance().getSQL("mm.CheckDAO.observanceCheckModelReq", dysql);
			return executeQuery("mm.CheckDAO.observanceCheckModelReq", sql, param );
		}
		
//		return executeQuery("mm.CheckDAO.observanceCheckModelReq", param );
	}

	/**
	 * 
	 * <pre>
	 * 테이블 유사도 검색 목록
	 * 2007.08.22 남상우 추가
	 * </pre>
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public CacheResultSet findAllByNotInTblLgclNm(HashMap param) throws Exception {
 		return executeQuery("mm.CheckDAO.findAllByNotInTblLgclNm", param);
 	}
	public CacheResultSet findElemInfoReqListByElemInfoIdAndReqDt(HashMap param) throws Exception {
 		return executeQuery("mm.CheckDAO.findElemInfoReqListByElemInfoIdAndReqDt", param);
 	}
	/**
	 * 
	 * <pre>
	 * ELEM_TP_ID가 SubjectArea일 때 준수도 검사 대상 엔티티를 읽어 온다. 
	 * </pre>
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public CacheResultSet findObservanceSubjectAreaByParElemInfoId(HashMap param) throws Exception {
 		return executeQuery("mm.CheckDAO.findObservanceSubjectAreaByParElemInfoId", param);
 	}
	public int insertObservanceSchemaCheckCountByDatStrc(HashMap param, HashMap datMap) throws  Exception
	{
		int errCnt = 0;

		CacheResultSet rs1 = this.observanceCheckByModel(param);
		errCnt += insertObservanceSchemaCheck(param, rs1);

		this.executeBatchAll();
		Integer[] temp = (Integer[])datMap.get(param.get("DAT_STRC_ID"));
		 
		if(temp == null)
		{
			 temp = new Integer[] { new Integer(0),new Integer(0) };
			
		}
		else
		{
			 
		}
		param.put("ELEM_INFO_IDS", "'" + param.get("ELEM_INFO_ID") + "'");
		temp[1] = new Integer(temp[1].intValue() + errCnt);
		String sql = jspeed.base.query.QueryService.getInstance().getSQL(
				"mm.CheckDAO.observanceCheckCount.all", param);
		CacheResultSet rs = this.executeQuery(
				"mm.CheckDAO.observanceCheckCount.all", sql, param);
		rs.next();
		temp[0] = new Integer(temp[0].intValue() + rs.getInt(1));
		datMap.put(param.get("DAT_STRC_ID"), temp);
		 
		 

		return errCnt;
	}
	private int insertObservanceSchemaCheck(HashMap param, CacheResultSet rs1)
	throws Exception {
	while (rs1.next()) {
		/*
		 * insert into MM_OBSERVANCE_CHECK_DETAIL (DAT_STRC_ID, START_DT,
		 * SRC_ID, TG_ID ) values(?, ?, ?, ? )
		 */
		param.putAll(rs1.getMap());
		// System.out.println(rs1.getMap());
		this.addBatch("mm.CheckDAO.insertObservanceSchemaCheck", param,
				10000);
	}
	return rs1.getRowCount();
	}
	
	public int insertObservanceSchemaCheckCount(HashMap param)
		throws Exception {
	/*
	 * select count() from MM_ELEM_INFO i, MM_ELEM_INFO j where
	 * i.LNK_INFO_ID=j.LNK_INFO_ID AND i.ELEM_TP_ID in
	 * ('Column','Attribute') AND i.namespace like j.namespace || '%' and
	 * j.elem_info_id in ({ELEM_INFO_IDS})
	 */
	
	
	
	param.put("END_DT", DateUtil.getToday());
	
	/*
	 * INSERT INTO MM_OBSERVANCE_CHECK (DAT_STRC_ID,START_DT,
	 * END_DT,ALL_COUNT,ERR_COUNT,SCHD_ID) values (?,?,?, ?, ?, ?)
	 */
	return executeUpdate("mm.CheckDAO.observanceSchemaCheckCount.insert",
			param);
	}
}
