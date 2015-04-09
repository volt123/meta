package com.lgdisplay.db;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jspeed.base.jdbc.QueryHelper;
import jspeed.base.query.DBAssistant;

import com.itplus.mm.dao.CommonDAO;
import com.lgdisplay.util.StrUtils;

public class SYSDAO extends CommonDAO {
	
	public SYSDAO() throws Exception {
	}

	public SYSDAO(DBAssistant _dba) throws Exception {
		super(_dba);
	}
	public Map allsql() throws Exception {
		String query =  
			 "\n SELECT FIELD_NAME,FIELD_VALUE,FIELD_INS_TP_CD,FIELD_INS,FIELD_ADMIN_EDIT_YN,FIELD_DESC FROM MM_SYS_LGD ";
		QueryHelper qHelper = new QueryHelper();
		ResultSet rs = null;
		Map map = new HashMap();
		try {
			rs = qHelper.executeQuery(query, new Object[] {	});
			while ( rs.next() ) {
				SYSVO vo = new SYSVO();
				vo.setFieldName(StrUtils.nullToEmpty(rs.getString("FIELD_NAME")));
				vo.setFieldValue(StrUtils.nullToEmpty(rs.getString("FIELD_VALUE")));
				vo.setFieldInsTpCd(StrUtils.nullToEmpty(rs.getString("FIELD_INS_TP_CD")));
				vo.setFieldIns(StrUtils.nullToEmpty(rs.getString("FIELD_INS")));
				vo.setFieldAdminEditYn(StrUtils.nullToEmpty(rs.getString("FIELD_ADMIN_EDIT_YN")));
				vo.setFieldDesc(StrUtils.nullToEmpty(rs.getString("FIELD_DESC")));
				map.put(vo.getFieldName(), vo);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try { if (qHelper != null) qHelper.close();}catch(Exception e){}
			try { if (rs != null) rs.close();}catch(Exception e){}
		}
		return map;
	}
	public Map allparam() throws Exception {
		String query =  
			"\n SELECT "
			+"\n		A.* "
			+"\n		,decode(FIELD_NAME,PRE_SQL,'FALSE','TRUE') AS EQ_SQL "
			+"\n	FROM ( "
			+"\n		SELECT FIELD_NAME, FIELD_VALUE, FIELD_CD, FIELD_DESC, "
			+"\n		 		lag(FIELD_NAME, 1, 'N/A') over ( order by FIELD_NAME) as PRE_SQL, "
			+"\n				lead(FIELD_NAME, 1, 'N/A') over ( order by FIELD_NAME) as NEXT_SQL "
			+"\n	FROM SITE_SYS_DTL "
			+"\n		ORDER BY FIELD_NAME, FIELD_VALUE "
			+"\n	) A "
			+"\n    ORDER BY FIELD_NAME, FIELD_CD, FIELD_VALUE ";
		QueryHelper qHelper = new QueryHelper();
		ResultSet rs = null;
		Map map = new HashMap();
		ArrayList params = new ArrayList();
		try {
//			qHelper.begin();
			rs = qHelper.executeQuery(query, new Object[] {	});
			while ( rs.next() ) {
				SYSVO vo = new SYSVO();
				vo.setFieldName(StrUtils.nullToEmpty(rs.getString("FIELD_NAME")));
				vo.setFieldCd(StrUtils.nullToEmpty(rs.getString("FIELD_CD")));
				vo.setFieldValue(StrUtils.nullToEmpty(rs.getString("FIELD_VALUE")));
				vo.setFieldDesc(StrUtils.nullToEmpty(rs.getString("FIELD_DESC")));
				
				if ( StrUtils.nullToEmpty(rs.getString("PRE_SQL")).equalsIgnoreCase("N/A") ) {
					params.add(vo);
				} else if ( StrUtils.nullToEmpty(rs.getString("EQ_SQL")).equalsIgnoreCase("TRUE") ) {
					map.put(StrUtils.nullToEmpty(rs.getString("PRE_SQL")), params);
					params = new ArrayList();
					params.add(vo);
				} else if ( StrUtils.nullToEmpty(rs.getString("EQ_SQL")).equalsIgnoreCase("FALSE") ) {
					params.add(vo);
				}
				if ( StrUtils.nullToEmpty(rs.getString("NEXT_SQL")).equalsIgnoreCase("N/A") ) {
					map.put( StrUtils.nullToEmpty(rs.getString("FIELD_NAME")), params);
				}
			}
//			qHelper.commit();
		} catch (Exception e) {
//			qHelper.rollback();
			throw e;
		} finally {
			try { if (qHelper != null) qHelper.close();}catch(Exception e){}
			try { if (rs != null) rs.close();}catch(Exception e){}
		}
		return map;
	}
}
