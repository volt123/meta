package com.lgdisplay.code;

import java.sql.ResultSet;

import jspeed.base.jdbc.BaseSQLException;
import jspeed.base.jdbc.CacheResultSet;
import jspeed.base.jdbc.QueryHelper;

public class LGDCodeDeployer {
	
// 코드 배포 DATA_CD ! REQ_TP_CD ! MNGM_ID ! CODE_DOM_NM ! CODE_VAL_CNT
//	int columnCnt = codeRs.getColumnCount();
//	while (codeRs.next()) {
//		for (int i = 1; i <= columnCnt; i++)
//			if (i == 1)
//				deployContents = deployContents + codeRs.getString(i);
//			else
//				deployContents = deployContents + delimeter
//						+ codeRs.getString(i);
//
//		deployContents = deployContents + "\n";
//	}	

	public boolean deployCode(CacheResultSet codeRs) throws BaseSQLException {
		
		ResultSet rs = null;
		CacheResultSet crs = null;
		ResultSet rs2 = null; // codeValue
		CacheResultSet crs2 = null; // codeValue
		QueryHelper qHelperSource = null;
		QueryHelper qHelperTarget = null;
		boolean ret = false;
		try {
			//jdbc/jSpeedDataSource, ITPDataSource
			qHelperSource = new QueryHelper("ITPDataSource");
			qHelperTarget = new QueryHelper("HSCDataSource");
			
			while (codeRs.next()) {
	            String codeId       = codeRs.getString("MNGM_ID");

	            Object[] paramCodeId = new Object[]{codeId};

				String selectCodeSQL = "SELECT A.MNGM_ID    CD_ID    " //--코드ID  
							+ "    , A.CODE_DOM_NM CD_NM"
	//--    , D.PHSC_NM     물리명
	//--    , (SELECT DISPLAY_NM FROM MM_CD_NM WHERE MM_CD = A.CODE_TP_CD AND LANG_CD = 'ko-KR' AND DEL_YN = 'N') 코드구분
	//--    , C.UDP_VAL 주제영역
							+ "    , A.UPD_DT   REGI_DTTM " //--등록일시 A.UPD_DT
							+ "    , A.REQ_DT   UPDT_DTTM " //--수정일시 A.REQ_DT
							+ "    , B.UDP_VAL  RMRK " // --비고
							+ "    , 'System Manager' REGR_EMPL_NO "
							+ "    , 'System Manager' UPDR_EMPL_NO "  
							+ "FROM MM_CODE_DOM A "
							+ "    , (SELECT * FROM MM_UDP_VAL WHERE UDP_ID = 'CODE_ETC') B "
							+ "    , (SELECT * FROM MM_UDP_VAL WHERE UDP_ID = 'CODE_OWNER') C "
							//+ "    , MM_UFW D "
							+ " , MM_UFW_DIC E  "
							+ "WHERE A.UFW_DIC_ID = E.UFW_DIC_ID "
							+ "AND   A.CODE_DOM_ID = B.TRG_ID(+) "
							+ "AND   A.CODE_DOM_ID = C.TRG_ID(+) "
							//+ "AND   A.CODE_DOM_ID = D.CODE_DOM_ID "
							+ "AND   A.DEL_YN = 'N' "
							+ "AND   E.DEL_YN = 'N' "
							+ "AND   A.UFW_DIC_ID = '2c908079/184ee4/0118/4ee38daf/0001' "
							+ "AND   A.MNGM_ID = ? "
							+ "order by A.MNGM_ID, A.CODE_DOM_NM";
		
				String insertCodeSQL = "insert into T_H_ITS200_F (CD_ID, CD_NM, REGI_DTTM, UPDT_DTTM, RMRK, REGR_EMPL_NO, UPDR_EMPL_NO) values (?,?,to_date(?, 'yyyymmddHH24MISS'),to_date(?, 'yyyymmddHH24MISS'),?,?,?)";
				String deleteCodeSQL = "delete from T_H_ITS200_F where CD_ID = ?"; //to_date(A.REQ_DT, 'yyyymmddHH24MISS')
				rs = qHelperSource.executeQuery(selectCodeSQL, paramCodeId);
				crs = new CacheResultSet(rs);
				
				qHelperTarget.begin();
				
				qHelperTarget.executeUpdate(deleteCodeSQL, paramCodeId);
				
				crs.initRow();
				while (crs.next()) {
		            String cd_id        = crs.getString("CD_ID");
		            String cd_nm        = crs.getString("CD_NM");
		            String regi_dttm    = crs.getString("REGI_DTTM");
		            String updt_dttm    = crs.getString("UPDT_DTTM");
		            String rmrk         = crs.getString("RMRK");
		            String regr_empl_no = crs.getString("REGR_EMPL_NO");
		            String updr_empl_no = crs.getString("UPDR_EMPL_NO");
	
		            Object[] paramCode = new Object[]{cd_id, cd_nm, regi_dttm, updt_dttm, rmrk, regr_empl_no, updr_empl_no};
		            qHelperTarget.executeUpdate(insertCodeSQL, paramCode);
				}
		        rs.close();
		        
		        String selectCodeValueSQL = 
		        "SELECT B.MNGM_ID || A.CODE_VAL CDVL "
			    +"	, B.MNGM_ID     CD_ID "
			    +"	, A.CODE_VAL_NM CDVL_NM "
		        +"  , A.DISP_ODR    CD_SQNO "
				+"  , F.UDP_VAL  UPPR_CD_ID "
				+"  , G.UDP_VAL  UPPR_CDVL "
		        +"  , J.UDP_VAL     RMRK "
		        +"  , NVL(substr(H.UDP_VAL, 0,8) , '20110720')  USE_STRT_DT "
				+"  , NVL(substr(I.UDP_VAL, 0,8) , '99991231')  USE_END_DT "
				+"  , 'System Manager'        REGR_EMPL_NO "
				+"  , B.UPD_DT   REGI_DTTM "
				+"  , 'System Manager'        UPDR_EMPL_NO "
				+"  , B.REQ_DT   UPDT_DTTM "
		        +"FROM MM_CODE_DOM_VAL A "
		        +"  , MM_CODE_DOM B "
		        +"  , MM_UFW_DIC E "
		        +"  , (SELECT * FROM MM_UDP_VAL WHERE UDP_ID = 'HIGH_CODE_ID') F "
		        +"  , (SELECT * FROM MM_UDP_VAL WHERE UDP_ID = 'HIGH_CODE_VALUE') G "
		        +"  , (SELECT * FROM MM_UDP_VAL WHERE UDP_ID = 'USE_START_DATE') H "
		        +"  , (SELECT * FROM MM_UDP_VAL WHERE UDP_ID = 'USE_END_DATE') I "
		        +"  , (SELECT * FROM MM_UDP_VAL WHERE UDP_ID = 'CODE_ETC') J "
		        +"  WHERE A.CODE_DOM_ID = B.CODE_DOM_ID "
		        +"  AND   A.CODE_DOM_ID = F.TRG_ID(+) "
		        +"  AND   A.CODE_DOM_ID = G.TRG_ID(+) "
		        +"  AND   A.CODE_DOM_ID = H.TRG_ID(+) "
		        +"  AND   A.CODE_DOM_ID = I.TRG_ID(+) "
		        +"  AND   A.CODE_DOM_ID = J.TRG_ID(+) "
		        +"  AND   B.UFW_DIC_ID = E.UFW_DIC_ID "
		        +"  AND   B.DEL_YN = 'N' "
		        +"  AND   E.DEL_YN = 'N' "
		        +"  AND   B.UFW_DIC_ID = '2c908079/184ee4/0118/4ee38daf/0001' "	        	
		        +"  AND   B.MNGM_ID = ? "
		        +"  order by A.CODE_VAL_NM";
		        	
				String insertCodeValueSQL = "insert into T_H_ITS201_C (CDVL, CD_ID, CDVL_NM, CD_SQNO, UPPR_CD_ID, UPPR_CDVL, " 
					                      + "RMRK, USE_STRT_DT, USE_END_DT, REGR_EMPL_NO, REGI_DTTM, UPDR_EMPL_NO, UPDT_DTTM) " 
					                      + "values (?,?,?,?,?,?, ?,?,?,?, to_date(?, 'yyyymmddHH24MISS'), ?, to_date(?, 'yyyymmddHH24MISS'))";
				
				String deleteCodeValueSQL = "delete from T_H_ITS201_C where cd_id = ?"; //to_date(A.REQ_DT, 'yyyymmddHH24MISS')
	
				rs2 = qHelperSource.executeQuery(selectCodeValueSQL, paramCodeId);
				crs2 = new CacheResultSet(rs2);
				
				qHelperTarget.executeUpdate(deleteCodeValueSQL, paramCodeId);
				
				crs2.initRow();
				while (crs2.next()) {
		            String cdvl         = crs2.getString("CDVL");
		            String cd_id        = crs2.getString("CD_ID");
		            String cdvl_nm      = crs2.getString("CDVL_NM");
		            String cd_sqno      = crs2.getString("CD_SQNO");
		            String uppr_cd_id   = crs2.getString("UPPR_CD_ID");
		            String uppr_cdvl    = crs2.getString("UPPR_CDVL");
		            String rmrk         = crs2.getString("RMRK");
		            String use_strt_dt  = crs2.getString("USE_STRT_DT");
		            String use_end_dt   = crs2.getString("USE_END_DT");
		            String regr_empl_no = crs2.getString("REGR_EMPL_NO");
		            String regi_dttm    = crs2.getString("REGI_DTTM");
		            String updr_empl_no = crs2.getString("UPDR_EMPL_NO");
		            String updt_dttm    = crs2.getString("UPDT_DTTM");
	
		            Object[] paramCodeValue = new Object[]{cdvl, cd_id, cdvl_nm, cd_sqno, uppr_cd_id, uppr_cdvl, rmrk, use_strt_dt, use_end_dt, regr_empl_no, regi_dttm, updr_empl_no, updt_dttm };
		            qHelperTarget.executeUpdate(insertCodeValueSQL, paramCodeValue);
				}
		        rs2.close();
	        
		    /*****************************************************************/
		    /********************* End of Code Loop **************************/
		    /*****************************************************************/
			}
		        
	        qHelperTarget.commit();
	        ret=true;
		} catch (Exception e){
			e.printStackTrace();
			qHelperTarget.rollback();
			if(qHelperTarget!=null&&!qHelperTarget.isClosed()) qHelperTarget.close(); 
			if(qHelperSource!=null&&!qHelperSource.isClosed()) qHelperSource.close(); 
		}finally {
			if(qHelperTarget!=null&&!qHelperTarget.isClosed()) qHelperTarget.close(); 
		    if(qHelperSource!=null&&!qHelperSource.isClosed()) qHelperSource.close(); 
		}
		
		return ret;
	}
	
	
	public int deployCodeAll() throws BaseSQLException {
		
		ResultSet rs = null;
		CacheResultSet crs = null;
		ResultSet rs2 = null; // codeValue
		CacheResultSet crs2 = null; // codeValue
		QueryHelper qHelperSource = null;
		QueryHelper qHelperTarget = null;
		int ret = 0;
		try {
			//jdbc/jSpeedDataSource, ITPDataSource
			qHelperSource = new QueryHelper("ITPDataSource");
			
			qHelperTarget = new QueryHelper("HSCDataSource");
			
			String selectCodeSQL = "SELECT A.MNGM_ID    CD_ID    " //--코드ID  
						+ "    , A.CODE_DOM_NM CD_NM"
//--    , D.PHSC_NM     물리명
//--    , (SELECT DISPLAY_NM FROM MM_CD_NM WHERE MM_CD = A.CODE_TP_CD AND LANG_CD = 'ko-KR' AND DEL_YN = 'N') 코드구분
//--    , C.UDP_VAL 주제영역
						+ "    , A.UPD_DT   REGI_DTTM " //--등록일시 A.UPD_DT
						+ "    , A.REQ_DT   UPDT_DTTM " //--수정일시 A.REQ_DT
						+ "    , B.UDP_VAL  RMRK " // --비고
						+ "    , 'System Manager' REGR_EMPL_NO "
						+ "    , 'System Manager' UPDR_EMPL_NO "  
						+ "FROM MM_CODE_DOM A "
						+ "    , (SELECT * FROM MM_UDP_VAL WHERE UDP_ID = 'CODE_ETC') B "
						+ "    , (SELECT * FROM MM_UDP_VAL WHERE UDP_ID = 'CODE_OWNER') C "
						//+ "    , MM_UFW D "
						+ " , MM_UFW_DIC E  "
						+ "WHERE A.UFW_DIC_ID = E.UFW_DIC_ID "
						+ "AND   A.CODE_DOM_ID = B.TRG_ID(+) "
						+ "AND   A.CODE_DOM_ID = C.TRG_ID(+) "
						//+ "AND   A.CODE_DOM_ID = D.CODE_DOM_ID "
						+ "AND   A.DEL_YN = 'N' "
						+ "AND   E.DEL_YN = 'N' "
						+ "AND   A.UFW_DIC_ID = '2c908079/184ee4/0118/4ee38daf/0001' "
						+ "order by A.MNGM_ID, A.CODE_DOM_NM";
	
			String insertCodeSQL = "insert into T_H_ITS200_F (CD_ID, CD_NM, REGI_DTTM, UPDT_DTTM, RMRK, REGR_EMPL_NO, UPDR_EMPL_NO) values (?,?,to_date(?, 'yyyymmddHH24MISS'),to_date(?, 'yyyymmddHH24MISS'),?,?,?)";
			String deleteCodeSQL = "truncate table T_H_ITS200_F"; //to_date(A.REQ_DT, 'yyyymmddHH24MISS')
			rs = qHelperSource.executeQuery(selectCodeSQL);
			crs = new CacheResultSet(rs);
			
			qHelperTarget.begin();
			
			qHelperTarget.executeUpdate(deleteCodeSQL, new Object[]{});
			
			crs.initRow();
			while (crs.next()) {
	            String cd_id        = crs.getString("CD_ID");
	            String cd_nm        = crs.getString("CD_NM");
	            String regi_dttm    = crs.getString("REGI_DTTM");
	            String updt_dttm    = crs.getString("UPDT_DTTM");
	            String rmrk         = crs.getString("RMRK");
	            String regr_empl_no = crs.getString("REGR_EMPL_NO");
	            String updr_empl_no = crs.getString("UPDR_EMPL_NO");

	            Object[] param = new Object[]{cd_id, cd_nm, regi_dttm, updt_dttm, rmrk, regr_empl_no, updr_empl_no};
	            qHelperTarget.executeUpdate(insertCodeSQL, param);
	            ret = ret + 1;
			}
	        rs.close();
	        
	        String selectCodeValueSQL = 
	        "SELECT B.MNGM_ID || A.CODE_VAL CDVL "
		    +"	, B.MNGM_ID     CD_ID "
		    +"	, A.CODE_VAL_NM CDVL_NM "
	        +"  , A.DISP_ODR    CD_SQNO "
			+"  , F.UDP_VAL  UPPR_CD_ID "
			+"  , G.UDP_VAL  UPPR_CDVL "
	        +"  , J.UDP_VAL     RMRK "
	        +"  , NVL(substr(H.UDP_VAL, 0,8) , '20110720')  USE_STRT_DT "
			+"  , NVL(substr(I.UDP_VAL, 0,8) , '99991231')  USE_END_DT "
			+"  , 'System Manager'        REGR_EMPL_NO "
			+"  , B.UPD_DT   REGI_DTTM "
			+"  , 'System Manager'        UPDR_EMPL_NO "
			+"  , B.REQ_DT   UPDT_DTTM "
	        +"FROM MM_CODE_DOM_VAL A "
	        +"  , MM_CODE_DOM B "
	        +"  , MM_UFW_DIC E "
	        +"  , (SELECT * FROM MM_UDP_VAL WHERE UDP_ID = 'HIGH_CODE_ID') F "
	        +"  , (SELECT * FROM MM_UDP_VAL WHERE UDP_ID = 'HIGH_CODE_VALUE') G "
	        +"  , (SELECT * FROM MM_UDP_VAL WHERE UDP_ID = 'USE_START_DATE') H "
	        +"  , (SELECT * FROM MM_UDP_VAL WHERE UDP_ID = 'USE_END_DATE') I "
	        +"  , (SELECT * FROM MM_UDP_VAL WHERE UDP_ID = 'CODE_ETC') J "
	        +"  WHERE A.CODE_DOM_ID = B.CODE_DOM_ID "
	        +"  AND   A.CODE_DOM_ID = F.TRG_ID(+) "
	        +"  AND   A.CODE_DOM_ID = G.TRG_ID(+) "
	        +"  AND   A.CODE_DOM_ID = H.TRG_ID(+) "
	        +"  AND   A.CODE_DOM_ID = I.TRG_ID(+) "
	        +"  AND   A.CODE_DOM_ID = J.TRG_ID(+) "
	        +"  AND   B.UFW_DIC_ID = E.UFW_DIC_ID "
	        +"  AND   B.DEL_YN = 'N' "
	        +"  AND   E.DEL_YN = 'N' "
	        +"  AND   B.UFW_DIC_ID = '2c908079/184ee4/0118/4ee38daf/0001' "	        	
	        +"  order by A.CODE_VAL_NM";
	        
			String insertCodeValueSQL = "insert into T_H_ITS201_C (CDVL, CD_ID, CDVL_NM, CD_SQNO, UPPR_CD_ID, UPPR_CDVL, " 
				                      + "RMRK, USE_STRT_DT, USE_END_DT, REGR_EMPL_NO, REGI_DTTM, UPDR_EMPL_NO, UPDT_DTTM) " 
				                      + "values (?,?,?,?,?,?, ?,?,?,?, to_date(?, 'yyyymmddHH24MISS'), ?, to_date(?, 'yyyymmddHH24MISS'))";
			
			String deleteCodeValueSQL = "truncate table T_H_ITS201_C"; //to_date(A.REQ_DT, 'yyyymmddHH24MISS')

			rs2 = qHelperSource.executeQuery(selectCodeValueSQL);
			crs2 = new CacheResultSet(rs2);
			
			qHelperTarget.executeUpdate(deleteCodeValueSQL, new Object[]{});
			
			crs2.initRow();
			while (crs2.next()) {
	            String cdvl         = crs2.getString("CDVL");
	            String cd_id        = crs2.getString("CD_ID");
	            String cdvl_nm      = crs2.getString("CDVL_NM");
	            String cd_sqno      = crs2.getString("CD_SQNO");
	            String uppr_cd_id   = crs2.getString("UPPR_CD_ID");
	            String uppr_cdvl    = crs2.getString("UPPR_CDVL");
	            String rmrk         = crs2.getString("RMRK");
	            String use_strt_dt  = crs2.getString("USE_STRT_DT");
	            String use_end_dt   = crs2.getString("USE_END_DT");
	            String regr_empl_no = crs2.getString("REGR_EMPL_NO");
	            String regi_dttm    = crs2.getString("REGI_DTTM");
	            String updr_empl_no = crs2.getString("UPDR_EMPL_NO");
	            String updt_dttm    = crs2.getString("UPDT_DTTM");

	            Object[] param2 = new Object[]{cdvl, cd_id, cdvl_nm, cd_sqno, uppr_cd_id, uppr_cdvl, rmrk, use_strt_dt, use_end_dt, regr_empl_no, regi_dttm, updr_empl_no, updt_dttm };
	            qHelperTarget.executeUpdate(insertCodeValueSQL, param2);
	            ret = ret + 1;
			}
	        rs2.close();
	        
	        qHelperTarget.commit();

		} catch (Exception e){
			e.printStackTrace();
			qHelperTarget.rollback();
			if(qHelperTarget!=null&&!qHelperTarget.isClosed()) qHelperTarget.close(); 
			if(qHelperSource!=null&&!qHelperSource.isClosed()) qHelperSource.close(); 
		}finally {
			if(qHelperTarget!=null&&!qHelperTarget.isClosed()) qHelperTarget.close(); 
		    if(qHelperSource!=null&&!qHelperSource.isClosed()) qHelperSource.close(); 
		}
		
		return ret;
	}
	
//	public static void main(String[] args )
//	{
//
//		try {
//			HYCodeDeployer codeDeployer = new HYCodeDeployer();
//			codeDeployer.deployCodeAll();
//		} catch (BaseSQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
}
