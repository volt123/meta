package com.lgdisplay.gen;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import com.itplus.mm.actions.MMAction;
import com.itplus.mm.common.idgen.UUIDHexGenerator;
import com.itplus.mm.common.util.ArrayHelper;
import com.itplus.mm.common.util.CharBufferUtil;
import com.itplus.mm.common.util.MessageHandler;
import com.itplus.mm.common.util.SysHandler;
import com.itplus.mm.common.util.TreeHandler;
import com.itplus.mm.common.util.UDPHelper;


import com.itplus.mm.dao.elem.TeleminfoReqDAO;
import com.itplus.mm.dao.elem.TeleminfoDAO;

import com.itplus.mm.dao.elem.TelemvarcolmapReqDAO;
import com.itplus.mm.dao.elem.TelemvarcolmapDAO;

import com.itplus.mm.dao.elem.TelemrelReqDAO;
import com.itplus.mm.dao.elem.TelemrelDAO;

import com.itplus.mm.dao.elem.TelemvarcolvalReqDAO;
import com.itplus.mm.dao.elem.TelemvarcolvalDAO;

import com.itplus.mm.dao.elem.TelemfileReqDAO;
import com.itplus.mm.dao.elem.TelemfileDAO;

import com.itplus.mm.dao.elem.TelemvarcoltypeDAO;
import com.itplus.mm.dao.elem.TelemvarcolattrDAO;

import com.itplus.mm.server.datadic.AbstractValidationCheck;
import com.itplus.mm.server.udp.AbstractUDPValGen;
import com.itplus.mm.server.workflow.WorkFlowApplication;

import jspeed.base.jdbc.CacheResultSet;
import jspeed.base.property.PropertyService;
import jspeed.base.query.DBAssistant;

public class LGDBizReportObjectNameGen extends com.itplus.mm.server.workflow.AbstractWorkItem {

	public final static String REQ_INSERT = ""; //추가
	public final static String REQ_UPDATE = "UPDATE"; //수정
	public final static String REQ_DELETE = "DELETE"; //삭제
	
	TeleminfoReqDAO eleminfoReqDAO = null;
	TeleminfoDAO eleminfoDAO = null;
	TelemvarcolmapReqDAO elemvarcolmapReqDAO = null;
	TelemvarcolmapDAO elemvarcolmapDAO = null;
	
	TelemrelReqDAO elemrelReqDAO = null;
	TelemrelDAO elemrelDAO = null;
	TelemvarcolvalReqDAO elemvarcolvalReqDAO = null;
	TelemvarcolvalDAO elemvarcolvalDAO = null;
	TelemfileReqDAO elemfileReqDAO = null;
	TelemfileDAO elemfileDAO = null;
	
	TelemvarcoltypeDAO elemvarcoltypeDAO = null;
	TelemvarcolattrDAO elemvarcolattrDAO = null;
	
	UDPHelper udpHelper = new UDPHelper();
	
	public LGDBizReportObjectNameGen(DBAssistant dba, String tblName) throws Exception {
		super(dba, tblName);
		 
	}
	
	public HashMap[] addReq(String reqDate, HashMap info ) throws Exception {
		return addReq(reqDate, info, dba);
	}
	
	public HashMap listGetReq(HashMap info, String keyList) throws Exception {
		HashMap result = new HashMap();

		getDAO();
		
		try {
			HashMap p = new HashMap();

			p.put("MM_ELEM_INFO_REQ_KEY_LIST", keyList);


			// UDP 정보 가져오기 시작
			StringBuffer udpList = new StringBuffer();
			
			info.put("LANG_CD", (String)info.get("SESS_LANG_TYPE"));
			info.put("DEL_YN", "N");
			
			p.put("ELEM_TP_ID_LIST", (String)info.get("ELEM_TP_ID_LIST"));
			CacheResultSet rs = elemvarcoltypeDAO.findByelemtpidList(info, p);
			
			String sql = "";
			
			while(rs.next()) {
				if( "CLOB".equals(rs.getString("ATTR_TP_CD")) ) { // LOB 데이터 처리
					udpList.append("\n, FN_ELEMVARCOL_CLOB_REQ(A.ELEM_INFO_ID, '" + rs.getString("COL_PHSC_NM") + "', A.REQ_DT) AS " + rs.getString("COL_PHSC_NM") + " ");
				} else if( "file".equals(rs.getString("ATTR_TP_CD")) ) { // file 데이터 처리
					udpList.append("\n, FN_ELEMFILE_REQ(A.ELEM_INFO_ID, '" + rs.getString("COL_PHSC_NM") + "', A.REQ_DT) AS " + rs.getString("COL_PHSC_NM") + " ");
				
				} else if( "relcombo".equals(rs.getString("ATTR_TP_CD")) ) { // 연관콤보박스 데이터 처리 (대분류, 중분류, 소분류,,)
					
					info.put("ELEM_TP_ID", rs.getString("ELEM_TP_ID"));
					info.put("COL_PHSC_NM", rs.getString("COL_PHSC_NM"));
					info.put("ATTR_VAL", "SQL_DISPLAY");
					
					sql = udpHelper.udmQueryGenerator(info, elemvarcolattrDAO);
					
					sql = jspeed.base.util.StringHelper.replaceStr(sql, "?", " FN_ELEMVARCOL_REQ(A.ELEM_INFO_ID, '" + rs.getString("COL_PHSC_NM") + "', A.REQ_DT) ");
					
					udpList.append("\n, ( " + sql + " ) AS " + rs.getString("COL_PHSC_NM"));
					udpList.append("\n, FN_ELEMVARCOL_REQ(A.ELEM_INFO_ID, '" + rs.getString("COL_PHSC_NM") + "', A.REQ_DT) AS " + rs.getString("COL_PHSC_NM") + "_NM ");
				
				} else if( "include".equals(rs.getString("ATTR_TP_CD")) ) { // include 데이터 처리 (사용자검색, 부서검색)
					
					info.put("ELEM_TP_ID", rs.getString("ELEM_TP_ID"));
					info.put("COL_PHSC_NM", rs.getString("COL_PHSC_NM"));
					info.put("ATTR_VAL", "SQL");
					
					sql = udpHelper.udmQueryGenerator(info, elemvarcolattrDAO);
					
					sql = jspeed.base.util.StringHelper.replaceStr(sql, "?", " FN_ELEMVARCOL_REQ(A.ELEM_INFO_ID, '" + rs.getString("COL_PHSC_NM") + "', A.REQ_DT) ");
					
					udpList.append("\n, ( " + sql + " ) AS " + rs.getString("COL_PHSC_NM"));
					udpList.append("\n, FN_ELEMVARCOL_REQ(A.ELEM_INFO_ID, '" + rs.getString("COL_PHSC_NM") + "', A.REQ_DT) AS " + rs.getString("COL_PHSC_NM") + "_NM ");
					
				} else {
					udpList.append("\n, FN_ELEMVARCOL_REQ(A.ELEM_INFO_ID, '" + rs.getString("COL_PHSC_NM") + "', A.REQ_DT) AS " + rs.getString("COL_PHSC_NM") + " ");	
				}
			}
			
			p.put("UDP_LIST", udpList.toString());
			// UDP 정보 가져오기 끝
			
			result.put("RETURN", eleminfoReqDAO.findAllByReqList(info, p));
			
			return result;
		} catch(Exception e) {
			throw e;
		} finally {
		}
	}
	
	
	
	public HashMap[] addReq(String reqDate, HashMap info, DBAssistant dba) throws Exception {
		System.out.println("@@@@@@@@@@@@@@@@@ NewUserDefMetaWorkItem.addReq  info ==> " + info);
		// TODO Auto-generated method stub
		
		String reqObjd = null;
		HashMap[] result = null;
		try {
			getDAO();
			
			ArrayHelper ah = new ArrayHelper();
			Object reqTpCd = info.get("REQ_TP_CD");
			Object mmAction = info.get("MMAction");	// 파일첨부 처리
			
			if(ah.isArray(reqTpCd)){
				HashMap param = new HashMap();
				param.put("MMAction", mmAction);	// 파일첨부 처리
				
				Object[] reqTpCdArray = ah.fetchArrayOfArray(reqTpCd);
				Object[] lineArray = info.get("LINE") != null ? ah.fetchArrayOfArray(info.get("LINE")) : null;
				Object[] reqEditTypeArray = info.get("REQ_EDIT_TYPE") != null ? ah.fetchArrayOfArray(info.get("REQ_EDIT_TYPE")) : null;
				Object[] reqDtArray = info.get("REQ_DT") != null ? ah.fetchArrayOfArray(info.get("REQ_DT")) : null;
				
				Object[] datStrcIdArray = info.get("DAT_STRC_ID") != null ? ah.fetchArrayOfArray(info.get("DAT_STRC_ID")) : null;
				Object[] elemInfoIdArray = info.get("ELEM_INFO_ID") != null ? ah.fetchArrayOfArray(info.get("ELEM_INFO_ID")) : null;
				Object[] elemTpIdArray = info.get("ELEM_TP_ID") != null ? ah.fetchArrayOfArray(info.get("ELEM_TP_ID")) : null;				
				Object[] elemPhscNmArray = info.get("ELEM_PHSC_NM") != null ? ah.fetchArrayOfArray(info.get("ELEM_PHSC_NM")) : null;
				Object[] symbolCdArray = info.get("SYMBOL_CD") != null ? ah.fetchArrayOfArray(info.get("SYMBOL_CD")) : null;
				Object[] relTpCdArray = info.get("REL_TP_CD") != null ? ah.fetchArrayOfArray(info.get("REL_TP_CD")) : null;
				Object[] parElemInfoIdArray = info.get("PAR_ELEM_INFO_ID") != null ? ah.fetchArrayOfArray(info.get("PAR_ELEM_INFO_ID")) : null;
				Object[] parNamespaceArray = info.get("PAR_NAMESPACE") != null ? ah.fetchArrayOfArray(info.get("PAR_NAMESPACE")) : null;
								
				Object[] errorIdArray = info.get("ERROR_ID") != null ? ah.fetchArrayOfArray(info.get("ERROR_ID")) : null;

				// 사용자정의메타 UDP
				ArrayList colPhscNmArray = info.get("COL_PHSC_NM") != null ? (ArrayList)info.get("COL_PHSC_NM") : null;
				ArrayList strValArray = info.get("STR_VAL") != null ? (ArrayList)info.get("STR_VAL") : null;
				ArrayList colTpArray = info.get("COL_TP") != null ? (ArrayList)info.get("COL_TP") : null;
				ArrayList fileTpCdArray = info.get("FILE_TP_CD") != null ? (ArrayList)info.get("FILE_TP_CD") : null;
				
				result = new HashMap[reqTpCdArray.length];
				
				String reqEditType = null;
				for(int i=0; i < reqTpCdArray.length; i++){
					
					reqEditType = reqEditTypeArray == null || reqEditTypeArray[i] == null || "".equals(reqEditTypeArray[i]) ? null : (String)reqEditTypeArray[i];

					param.put("REQ_TP_CD", reqTpCdArray[i]);
					param.put("REQ_DT", reqDtArray == null || reqDtArray[i] == null || "".equals(reqDtArray[i]) ? null : reqDtArray[i]);
					
					param.put("DAT_STRC_ID", datStrcIdArray == null || datStrcIdArray[i] == null || "".equals(datStrcIdArray[i]) ? null : datStrcIdArray[i]);
					param.put("ELEM_INFO_ID", elemInfoIdArray == null || elemInfoIdArray[i] == null || "".equals(elemInfoIdArray[i]) ? null : elemInfoIdArray[i]);
					param.put("ELEM_TP_ID", elemTpIdArray == null || elemTpIdArray[i] == null || "".equals(elemTpIdArray[i]) ? null : elemTpIdArray[i]);					
					param.put("ELEM_PHSC_NM", elemPhscNmArray[i]);
					param.put("SYMBOL_CD", symbolCdArray == null || symbolCdArray[i] == null || "".equals(symbolCdArray[i]) ? null : symbolCdArray[i]);
					param.put("REL_TP_CD", relTpCdArray == null || relTpCdArray[i] == null || "".equals(relTpCdArray[i]) ? null : relTpCdArray[i]);
					param.put("PAR_ELEM_INFO_ID", parElemInfoIdArray == null || parElemInfoIdArray[i] == null || "".equals(parElemInfoIdArray[i]) ? null : parElemInfoIdArray[i]);
					param.put("PAR_NAMESPACE", parNamespaceArray == null || parNamespaceArray[i] == null || "".equals(parNamespaceArray[i]) ? null : parNamespaceArray[i]);
					
					param.put("DEL_YN", "N");
					param.put("LANG_CD", info.get("SESS_LANG_TYPE"));					
					param.put("SESS_LANG_TYPE", info.get("SESS_LANG_TYPE"));
					param.put("SESS_USER_ID", info.get("SESS_USER_ID").toString());
					param.put("SESS_DEPT_ID", info.get("SESS_DEPT_ID"));

					// 사용자정의객체 UDP
					param.put("COL_PHSC_NM", colPhscNmArray == null || colPhscNmArray.get(i) == null || "".equals(colPhscNmArray.get(i)) ? null : colPhscNmArray.get(i));
					param.put("STR_VAL", strValArray == null || strValArray.get(i) == null || "".equals(strValArray.get(i)) ? null : strValArray.get(i));
					param.put("COL_TP", colTpArray == null || colTpArray.get(i) == null || "".equals(colTpArray.get(i)) ? null : colTpArray.get(i));
					param.put("FILE_TP_CD", fileTpCdArray == null || fileTpCdArray.get(i) == null || "".equals(fileTpCdArray.get(i)) ? null : fileTpCdArray.get(i));
					
					try {
						result[i] = new HashMap();
						HashMap chkValid = null;
						
						System.out.println("워크플로우 작업저장 시 NewUserDefMetaWorkItem.addReq.reqEditType ==> [" + reqEditType + "]");
						System.out.println("워크플로우 작업저장 시 NewUserDefMetaWorkItem.addReq.reqTpCdArray[i] ==> [" + reqTpCdArray[i] + "]");
						

						// 사용자 정의 유효성 체크 시작
						String className = null;
						
						className = com.itplus.mm.common.util.SysHandler.getInstance().getProperty("UDFMETA.VALIDATION.CLASS");
						if(className == null) 
						{
							className = "com.itplus.mm.server.datadic.impl.DefaultValidationCheck";
						}
						System.out.println("@@@@@@@@@@@@@@@@@ NewCODEWorkItem.addReq  className ==>" + className + "<==");
						
						if( WorkFlowApplication.INSERT.equals(reqTpCdArray[i]) ){
							chkValid = ((AbstractValidationCheck)Class.forName(className).newInstance()).checkInsertValid(param);
						} else if( WorkFlowApplication.UPDATE.equals(reqTpCdArray[i]) ){
							chkValid = ((AbstractValidationCheck)Class.forName(className).newInstance()).checkUpdateValid(param);							
						} else if( WorkFlowApplication.DELETE.equals(reqTpCdArray[i]) ){
							chkValid = ((AbstractValidationCheck)Class.forName(className).newInstance()).checkDeleteValid(param);
						}
						// 사용자 정의 유효성 체크 끝
						
						if("Y".equals(chkValid.get("VALD_CHECK_SUCC_YN"))) {
							if( reqEditType == null || REQ_INSERT.equals(reqEditType) ){
								if( WorkFlowApplication.INSERT.equals(reqTpCdArray[i]) ){
									chkValid = checkInsertValid(param);
								} else if( WorkFlowApplication.UPDATE.equals(reqTpCdArray[i]) ){
									chkValid = checkUpdateValid(param);							
								} else if( WorkFlowApplication.DELETE.equals(reqTpCdArray[i]) ){
									chkValid = checkDeleteValid(param);							
								}
							} else if( REQ_UPDATE.equals(reqEditType) ){

								chkValid = checkReqUpdateValid(param);	// 워크플로우 작업저장 시 (checkReqUpdateValid) 체크
								
							} else if( REQ_DELETE.equals(reqEditType) ){
								chkValid = new HashMap();

								chkValid.put("VALID_ID", null);
								chkValid.put("ERROR_ID", null);
								chkValid.put("ERROR_MSG", null);
								chkValid.put("VALD_CHECK_SUCC_YN", "Y");
							}	
						}
												
						param.put("VALID_ID", chkValid.get("VALID_ID"));
						param.put("ERROR_ID", chkValid.get("ERROR_ID"));
						param.put("ERROR_MSG", chkValid.get("ERROR_MSG"));
						param.put("VALD_CHECK_SUCC_YN", chkValid.get("VALD_CHECK_SUCC_YN"));						

						
						// 유효성 체크시 오류인 항목은  REQ 테이블에 저장하지 않는다.
						if( "N".equals(chkValid.get("VALD_CHECK_SUCC_YN")) ) {
							reqObjd = (String)param.get("ELEM_INFO_ID");
						} else {
							if( reqEditType == null || REQ_INSERT.equals(reqEditType) ){
								reqObjd = insertReq(reqDate, param);
							} else if( REQ_UPDATE.equals(reqEditType) ){
								reqObjd = updateReq(reqDate, param);						
							} else if( REQ_DELETE.equals(reqEditType) ){
								reqObjd = deleteReq(reqDate, param);						
							}
						}
						
						if(reqEditType == null) { // 작업저장 시 
							result[i].put("REQ_DT", reqDate);
						} else { // 워크플로우 작업저장 시
							result[i].put("REQ_DT", param.get("REQ_DT"));
						}						
						
						result[i].put("REQ_TP_CD", reqTpCdArray[i]);
						result[i].put("VALD_CHECK_SUCC_YN", chkValid.get("VALD_CHECK_SUCC_YN"));
						
						result[i].put("ID", reqObjd);
						result[i].put("VALID_ID", chkValid.get("VALID_ID"));
						
						// REQ에 저장하지 않은 경우는 ERROR_ID를 EXCEPTION으로 Client에 넘겨준다.
						if( "N".equals(chkValid.get("VALD_CHECK_SUCC_YN")) ) {
							result[i].put("ERROR_ID", "EXCEPTION");
						} else {
							result[i].put("ERROR_ID", chkValid.get("ERROR_ID"));
						}
						
						result[i].put("ERROR_MSG", chkValid.get("ERROR_MSG"));
						result[i].put("LINE", lineArray == null || lineArray[i] == null || "".equals(lineArray[i]) ? null : lineArray[i]);
						
					} catch(Exception e) {
						eleminfoReqDAO.rollback();
						
						System.out.println("NewUserDefMetaWorkItem.addReq.ERROR ==> " + e.getMessage());
						System.out.println("NewUserDefMetaWorkItem.addReq.LINE ==> " + (i+1));
						
						e.printStackTrace();
						
						if(reqEditType == null) { // 작업저장 시 
							result[i].put("REQ_DT", reqDate);
						} else { // 워크플로우 작업저장 시
							result[i].put("REQ_DT", param.get("REQ_DT"));
						}
						
						result[i].put("REQ_TP_CD", reqTpCdArray[i]);
						result[i].put("VALD_CHECK_SUCC_YN", "N");
						
						result[i].put("ID", null);
						result[i].put("VALID_ID", null);
						result[i].put("ERROR_ID", "EXCEPTION");
						result[i].put("ERROR_MSG", MessageHandler.getInstance().getMessage("SMSG_ST011", (String)info.get("SESS_LANG_TYPE")));
						result[i].put("LINE", lineArray == null || lineArray[i] == null || "".equals(lineArray[i]) ? null : lineArray[i]);
						
					}
				}
				
			}
		} catch(Exception e) {
			throw e;
		} finally {
		}
		
		return result;
	}

		
	public HashMap checkInsertValid(HashMap info) throws Exception {		
		HashMap result = new HashMap();

		result.put("VALID_ID", null);
		result.put("ERROR_ID", null);
		result.put("ERROR_MSG", null);
		result.put("VALD_CHECK_SUCC_YN", "Y");
		
		return result;
	}

	public HashMap checkUpdateValid(HashMap info) throws Exception {		
		System.out.println("checkUpdateValid===> start..");
		
		CacheResultSet rs = eleminfoReqDAO.findByUpdateValidCheck(info);
		HashMap result = new HashMap();

		System.out.println("checkUpdateValid.row cnt===" + rs.getRowCount());
		
		if(rs.next()){
			String elemInfoId = rs.getString("ELEM_INFO_ID");
			String mmCd = rs.getString("MM_CD");
			String mmCdMsg = rs.getString("MM_CD_MSG");
			
			result.put("VALID_ID", elemInfoId);
			result.put("ERROR_ID", mmCd);
			result.put("ERROR_MSG", mmCdMsg);
			result.put("VALD_CHECK_SUCC_YN", "N");
		} else {
			result.put("VALID_ID", null);
			result.put("ERROR_ID", null);
			result.put("ERROR_MSG", null);
			result.put("VALD_CHECK_SUCC_YN", "Y");
		}
		
		return result;
	}
	
	public HashMap checkReqUpdateValid(HashMap info) throws Exception {		
		HashMap result = new HashMap();

		result.put("VALID_ID", null);
		result.put("ERROR_ID", null);
		result.put("ERROR_MSG", null);
		result.put("VALD_CHECK_SUCC_YN", "Y");
		
		return result;
	}
	
	public HashMap checkDeleteValid(HashMap info) throws Exception {		
		System.out.println("checkDeleteValid===> start..");
		
		CacheResultSet rs = eleminfoReqDAO.findByDeleteValidCheck(info);
		HashMap result = new HashMap();

		System.out.println("checkDeleteValid.row cnt===" + rs.getRowCount());
		
		if(rs.next()){
			String elemInfoId = rs.getString("ELEM_INFO_ID");
			String mmCd = rs.getString("MM_CD");
			String mmCdMsg = rs.getString("MM_CD_MSG");
			
			result.put("VALID_ID", elemInfoId);
			result.put("ERROR_ID", mmCd);
			result.put("ERROR_MSG", mmCdMsg);
			result.put("VALD_CHECK_SUCC_YN", "N");
		} else {
			result.put("VALID_ID", null);
			result.put("ERROR_ID", null);
			result.put("ERROR_MSG", null);
			result.put("VALD_CHECK_SUCC_YN", "Y");
		}
		
		return result;
	}

	public String insertReq(String reqDate, HashMap info) throws Exception{		
				
		String elemInfoId = null;
		
		try {
			// [LG디스플레이] 사용자정의메타 객체명 사이트 커스트마이징 할 수 있게 클래스 제공
			
			AbstractUDPValGen abstractUDPValGen = null; 
			String className = null;
			
			className = com.itplus.mm.common.util.SysHandler.getInstance().getProperty("UDM.ABBRAUTOGEN.CLASS");
			
			if(className != null && !"".equals(className) && !"com.itplus.mm.server.udp.impl.DefaultUDPValGen".equals(className) && !WorkFlowApplication.DELETE.equals(info.get("REQ_TP_CD"))) {
			
				try {
					abstractUDPValGen = (AbstractUDPValGen)Class.forName(className).newInstance();
					
					info.put("ELEM_PHSC_NM", abstractUDPValGen.udpValGenerator(info));
				} catch(Exception e) {
					e.printStackTrace();
					System.out.println("AbstractUDPValGen." + className + " Exception..");
				}
			
			}
			
			
			MMAction action = (MMAction)info.get("MMAction");	// 파일첨부 처리
			
			UUIDHexGenerator gen = new UUIDHexGenerator();
			
			if( WorkFlowApplication.INSERT.equals(info.get("REQ_TP_CD")) ){
				elemInfoId = gen.generate().toString();
			} else if( WorkFlowApplication.UPDATE.equals(info.get("REQ_TP_CD")) ){
				elemInfoId = (String)info.get("ELEM_INFO_ID");
			} else if( WorkFlowApplication.DELETE.equals(info.get("REQ_TP_CD")) ){
				elemInfoId = (String)info.get("ELEM_INFO_ID");
			}

			String parNamespace = info.get("PAR_NAMESPACE") == null || "".equals(info.get("PAR_NAMESPACE")) ? "" : (String)info.get("PAR_NAMESPACE");
			
			HashMap param = new HashMap();
	
			param.put("SESS_LANG_TYPE", (String)info.get("SESS_LANG_TYPE"));
			param.put("PAR_ELEM_INFO_ID", info.get("PAR_ELEM_INFO_ID") == null || "".equals(info.get("PAR_ELEM_INFO_ID")) ? "0" : (String)info.get("PAR_ELEM_INFO_ID"));
			
			param.put("ELEM_INFO_ID", elemInfoId);
			param.put("REQ_DT", info.get("REQ_DT") == null || "".equals(info.get("REQ_DT")) ? reqDate : (String)info.get("REQ_DT"));
			param.put("ELEM_PHSC_NM", (String)info.get("ELEM_PHSC_NM"));
			param.put("ELEM_LGCL_NM", (String)info.get("ELEM_PHSC_NM"));
			
			if("".equals(parNamespace)) {
				param.put("NAMESPACE", (String)info.get("ELEM_PHSC_NM") + "." + elemInfoId);	
			} else {
				param.put("NAMESPACE", parNamespace + "." + (String)info.get("ELEM_PHSC_NM") + "." + elemInfoId);
			}
			
			param.put("ELEM_TP_ID", (String)info.get("ELEM_TP_ID"));
			
			param.put("LNK_INFO_ID", "UserDefMeta");
			param.put("CHG_TP_CD", "");
			param.put("UPD_DT", com.itplus.mm.common.util.DateUtil.getToday());
			param.put("UPD_ID", (String)info.get("SESS_USER_ID"));
			param.put("SESS_USER_ID", (String)info.get("SESS_USER_ID"));
			param.put("REQ_TP_CD", (String)info.get("REQ_TP_CD"));
			param.put("APPR_STAT_TP_CD", WorkFlowApplication.BEFORE);
			param.put("VALD_CHECK_SUCC_YN", (String)info.get("VALD_CHECK_SUCC_YN"));
			param.put("VALD_CHECK_ID", (String)info.get("ERROR_CD"));
			param.put("INST_ID", "");
			
			param.put("SYMBOL_CD", (String)info.get("SYMBOL_CD"));
			param.put("DAT_STRC_ID", (String)info.get("DAT_STRC_ID"));
						
			eleminfoReqDAO.insert(param);
			
			param.put("REL_TP_CD", (String)info.get("REL_TP_CD"));
			elemrelReqDAO.insert(param);

			// 사용자정의메타 UDP 시작
			ArrayList colPhscNmArray = (ArrayList)info.get("COL_PHSC_NM");
			ArrayList strValArray = (ArrayList)info.get("STR_VAL");
			ArrayList colTpArray = (ArrayList)info.get("COL_TP");
			
			ArrayList fileTpCdArray = (ArrayList)info.get("FILE_TP_CD");
			
			int index = 0;
			CharBufferUtil cbutil = new CharBufferUtil();
			
			for(int i=0; colPhscNmArray != null && i < colPhscNmArray.size(); i++){
				System.out.println("NewUserDefMetaWorkItem.insertReq colPhscNmArray ==>" + colPhscNmArray.get(i) + "<==");
				System.out.println("NewUserDefMetaWorkItem.insertReq strValArray ==>" + strValArray.get(i) + "<==");
				
				param.put("COL_PHSC_NM", colPhscNmArray == null || colPhscNmArray.get(i) == null || "".equals(colPhscNmArray.get(i)) ? null : colPhscNmArray.get(i));
				param.put("STR_VAL", strValArray == null || strValArray.get(i) == null || "".equals(strValArray.get(i)) ? null : strValArray.get(i));
				param.put("COL_TP", colTpArray == null || colTpArray.get(i) == null || "".equals(colTpArray.get(i)) ? null : colTpArray.get(i));
				
				param.put("FILE_TP_CD", fileTpCdArray == null || fileTpCdArray.get(i) == null || "".equals(fileTpCdArray.get(i)) ? null : fileTpCdArray.get(i));
				
				if( "CLOB".equals(param.get("COL_TP")) ) { // LOB 데이터 처리
					
					CharBuffer cb = cbutil.getCharBuffer( (String)param.get("STR_VAL"));
				
					HashMap param1 = new HashMap();

					param1.put("ELEM_INFO_ID", (String)param.get("ELEM_INFO_ID"));
					param1.put("ELEM_TP_ID", (String)param.get("ELEM_TP_ID"));
					param1.put("COL_PHSC_NM", (String)param.get("COL_PHSC_NM"));
					param1.put("UPD_DT", (String)param.get("REQ_DT"));
					
					eleminfoReqDAO.insertLagrgeColumnAddBatch(param1, cb, false);
					
				} else if( "file".equals(param.get("COL_TP")) ) { // 파일업로드 처리
					String dir = jspeed.base.property.PropertyService.getInstance().getProperty("mm.config", "userdefobj.uploadDir");
					
					System.out.println("dir====>" + dir);
					dir = com.itplus.mm.server.MMAppInit.WEB_INF_REAL_PATH + (dir.startsWith("/")? "":"/") + dir;
					System.out.println("dir====>" + dir);
					
					String fileName = null;
					if( WorkFlowApplication.INSERT.equals(param.get("FILE_TP_CD")) || WorkFlowApplication.UPDATE.equals(param.get("FILE_TP_CD")) ) {
						fileName = action.saveAttach(index++, dir, (String)param.get("STR_VAL"), false);	
					} else if( WorkFlowApplication.DELETE.equals(param.get("FILE_TP_CD")) ) {
						fileName = "";
					}
					
					param.put("FILE_POS", PropertyService.getInstance().getProperty("mm.config", "userdefobj.uploadDir"));
					param.put("FILE_NM", param.get("STR_VAL"));
					param.put("FILE_PHSC_NM", fileName); 
					
					elemfileReqDAO.insert(param);	

					
				} else if( "class".equals(param.get("COL_TP")) ) { // 사용자정의 클래스 처리, 예) 채번개체
					
					param.put("STR_VAL", udpHelper.udmValGenerator(param, elemvarcolattrDAO));						
					
					elemvarcolmapReqDAO.insert(param);
				} else {
					elemvarcolmapReqDAO.insert(param);
				}
			}
			// 사용자정의메타 UDP 끝
		} catch(Exception e) {
			throw e;
		}
		
		return elemInfoId;
	}
	
	public String updateReq(String reqDate, HashMap info) throws Exception{		
		String elemInfoId = null;
		
		try {
			// [LG디스플레이] 사용자정의메타 객체명 사이트 커스트마이징 할 수 있게 클래스 제공
			
			AbstractUDPValGen abstractUDPValGen = null; 
			String className = null;
			
			className = com.itplus.mm.common.util.SysHandler.getInstance().getProperty("UDM.ABBRAUTOGEN.CLASS");
			
			if(className != null && !"".equals(className) && !"com.itplus.mm.server.udp.impl.DefaultUDPValGen".equals(className) && !WorkFlowApplication.DELETE.equals(info.get("REQ_TP_CD"))) {
			
				try {
					abstractUDPValGen = (AbstractUDPValGen)Class.forName(className).newInstance();
					
					info.put("ELEM_PHSC_NM", abstractUDPValGen.udpValGenerator(info));
				} catch(Exception e) {
					System.out.println("AbstractUDPValGen." + className + " Exception..");
				}
			
			}
			
			MMAction action = (MMAction)info.get("MMAction");	// 파일첨부 처리
			
			elemInfoId = (String)info.get("ELEM_INFO_ID");

			String parNamespace = info.get("PAR_NAMESPACE") == null || "".equals(info.get("PAR_NAMESPACE")) ? "" : (String)info.get("PAR_NAMESPACE");
			
			HashMap param = new HashMap();
	
			param.put("ELEM_INFO_ID", elemInfoId);
			param.put("REQ_DT", (String)info.get("REQ_DT"));
			param.put("ELEM_PHSC_NM", (String)info.get("ELEM_PHSC_NM"));
			param.put("ELEM_LGCL_NM", (String)info.get("ELEM_PHSC_NM"));
			
			if("".equals(parNamespace)) {
				param.put("NAMESPACE", (String)info.get("ELEM_PHSC_NM") + "." + elemInfoId);	
			} else {
				param.put("NAMESPACE", parNamespace + "." + (String)info.get("ELEM_PHSC_NM") + "." + elemInfoId);
			}
			
			param.put("ELEM_TP_ID", (String)info.get("ELEM_TP_ID"));
			
			param.put("LNK_INFO_ID", "UserDefMeta");
			param.put("CHG_TP_CD", "");
			param.put("UPD_DT", com.itplus.mm.common.util.DateUtil.getToday());
			param.put("UPD_ID", (String)info.get("SESS_USER_ID"));
			param.put("SESS_USER_ID", (String)info.get("SESS_USER_ID"));
			param.put("REQ_TP_CD", (String)info.get("REQ_TP_CD"));
			param.put("APPR_STAT_TP_CD", WorkFlowApplication.BEFORE);
			param.put("VALD_CHECK_SUCC_YN", (String)info.get("VALD_CHECK_SUCC_YN"));
			param.put("VALD_CHECK_ID", (String)info.get("ERROR_CD"));
			param.put("INST_ID", "");
			
			param.put("SYMBOL_CD", (String)info.get("SYMBOL_CD"));
			param.put("DAT_STRC_ID", (String)info.get("DAT_STRC_ID"));
						
			eleminfoReqDAO.updateForuserdifinedMeta(param);
			

			// 사용자정의메타 UDP 시작
			ArrayList colPhscNmArray = (ArrayList)info.get("COL_PHSC_NM");
			ArrayList strValArray = (ArrayList)info.get("STR_VAL");
			ArrayList colTpArray = (ArrayList)info.get("COL_TP");
			
			ArrayList fileTpCdArray = (ArrayList)info.get("FILE_TP_CD");
			
			int index = 0;
			CharBufferUtil cbutil = new CharBufferUtil();
			
			for(int i=0; colPhscNmArray != null && i < colPhscNmArray.size(); i++){
				System.out.println("NewUserDefMetaWorkItem.insertReq colPhscNmArray ==>" + colPhscNmArray.get(i) + "<==");
				System.out.println("NewUserDefMetaWorkItem.insertReq strValArray ==>" + strValArray.get(i) + "<==");
				
				param.put("COL_PHSC_NM", colPhscNmArray == null || colPhscNmArray.get(i) == null || "".equals(colPhscNmArray.get(i)) ? null : colPhscNmArray.get(i));
				param.put("STR_VAL", strValArray == null || strValArray.get(i) == null || "".equals(strValArray.get(i)) ? null : strValArray.get(i));
				param.put("COL_TP", colTpArray == null || colTpArray.get(i) == null || "".equals(colTpArray.get(i)) ? null : colTpArray.get(i));
				
				param.put("FILE_TP_CD", fileTpCdArray == null || fileTpCdArray.get(i) == null || "".equals(fileTpCdArray.get(i)) ? null : fileTpCdArray.get(i));
				
				if( "CLOB".equals(param.get("COL_TP")) ) { // LOB 데이터 처리
					
					CharBuffer cb = cbutil.getCharBuffer( (String)param.get("STR_VAL"));
				
					HashMap param1 = new HashMap();

					param1.put("ELEM_INFO_ID", (String)param.get("ELEM_INFO_ID"));
					param1.put("ELEM_TP_ID", (String)param.get("ELEM_TP_ID"));
					param1.put("COL_PHSC_NM", (String)param.get("COL_PHSC_NM"));
					param1.put("UPD_DT", (String)param.get("REQ_DT"));
					
					elemvarcolvalReqDAO.delete(param);
					eleminfoReqDAO.insertLagrgeColumnAddBatch(param1, cb, false);
					
				} else if( "file".equals(param.get("COL_TP")) ) { // 파일업로드 처리
					String dir = jspeed.base.property.PropertyService.getInstance().getProperty("mm.config", "userdefobj.uploadDir");
					
					System.out.println("dir====>" + dir);
					dir = com.itplus.mm.server.MMAppInit.WEB_INF_REAL_PATH + (dir.startsWith("/")? "":"/") + dir;
					System.out.println("dir====>" + dir);
					
					String fileName = null;
					if( WorkFlowApplication.INSERT.equals(param.get("FILE_TP_CD")) || WorkFlowApplication.UPDATE.equals(param.get("FILE_TP_CD")) ) {
						fileName = action.saveAttach(index++, dir, (String)param.get("STR_VAL"), false);	
					} else if( WorkFlowApplication.DELETE.equals(param.get("FILE_TP_CD")) ) {
						fileName = "";
					}
					
					param.put("FILE_POS", PropertyService.getInstance().getProperty("mm.config", "userdefobj.uploadDir"));
					param.put("FILE_NM", param.get("STR_VAL"));
					param.put("FILE_PHSC_NM", fileName); 
					
					if( WorkFlowApplication.INSERT.equals(param.get("FILE_TP_CD")) ) {
						elemfileReqDAO.insert(param);	
					} else if( WorkFlowApplication.UPDATE.equals(param.get("FILE_TP_CD")) ) {
						elemfileReqDAO.update(param);
					} else if( WorkFlowApplication.DELETE.equals(param.get("FILE_TP_CD")) ) {
						elemfileReqDAO.update(param);
					}

					
				} else if( "class".equals(param.get("COL_TP")) ) { // 사용자정의 클래스 처리, 예) 채번개체

					elemvarcolmapReqDAO.delete(param);
					elemvarcolmapReqDAO.insert(param);
					
				} else {
					elemvarcolmapReqDAO.delete(param);
					elemvarcolmapReqDAO.insert(param);
				}
			}
			// 사용자정의메타 UDP 끝
			
		} catch(Exception e) {
			throw e;
		}
		
		return elemInfoId;
	}
	
	public String deleteReq(String reqDate, HashMap info) throws Exception{		
		String reqDt = null;
		String elemInfoId = null;
				
		try {
			
			reqDt = (String)info.get("REQ_DT");
			elemInfoId = (String)info.get("ELEM_INFO_ID");

			HashMap param = new HashMap();
			
			param.put("REQ_DT", reqDt);
			param.put("ELEM_INFO_ID", elemInfoId);
			
			eleminfoReqDAO.delete(param);
						
		} catch(Exception e) {
			throw e;
		}
		
		return elemInfoId;
	}
	
	public void deploy(String id, String reqDt, String reqType) throws Exception {
		
		getDAO();
		
		HashMap param = new HashMap();
		param.put("ELEM_INFO_ID", id);
		param.put("REQ_DT", reqDt);
		
		System.out.println("NewUserDefMetaWorkItem.deploy.id ==> " + id);
		System.out.println("NewUserDefMetaWorkItem.deploy.id ==> " + reqDt);
		System.out.println("NewUserDefMetaWorkItem.deploy.id ==> " + reqType);
		
		
		if(reqType.equals(WorkFlowApplication.INSERT)) {
			eleminfoDAO.insertFromReq(param);
			elemvarcolmapDAO.insertFromReqForUserdef(param);
			elemrelDAO.insertFromReqForUserdef(param);
			elemvarcolvalDAO.insertFromReqForUserdef(param);
			elemfileDAO.insertFromReq(param);
		} else if(reqType.equals(WorkFlowApplication.UPDATE)) {
			eleminfoDAO.delete(param);
			elemvarcolmapDAO.deleteByeleminfoid(param);
			elemrelDAO.deleteByeleminfoid(param);
			elemvarcolvalDAO.deleteByeleminfoid(param);
			elemfileDAO.deleteByeleminfoid(param);
			
			eleminfoDAO.insertFromReq(param);
			elemvarcolmapDAO.insertFromReqForUserdef(param);
			elemrelDAO.insertFromReqForUserdef(param);
			elemvarcolvalDAO.insertFromReqForUserdef(param);
			elemfileDAO.insertFromReq(param);
		} else if(reqType.equals(WorkFlowApplication.DELETE)) {
			eleminfoDAO.updateDelYn (param);
		}
	
		eleminfoReqDAO.commit();
		TreeHandler.clearTreeMap();
		TreeHandler.getInstance().reload();
	}
	
	public void deployAdm(HashMap info) throws Exception {

		String id = (String)info.get("ID");
		String reqDt = (String)info.get("REQ_DT");
		String reqType = (String)info.get("REQ_TP_CD");
		
		getDAO();
		
		HashMap param = new HashMap();
		param.put("ELEM_INFO_ID", id);
		param.put("TRG_ID", id);
		param.put("REQ_DT", reqDt);
		
		System.out.println("NewUserDefMetaWorkItem.deploy.id ==> " + id);
		System.out.println("NewUserDefMetaWorkItem.deploy.id ==> " + reqDt);
		System.out.println("NewUserDefMetaWorkItem.deploy.id ==> " + reqType);
		
		eleminfoReqDAO.updateApprstattpcd(param);
		
		if(reqType.equals(WorkFlowApplication.INSERT)) {
			eleminfoDAO.insertFromReq(param);
			elemvarcolmapDAO.insertFromReqForUserdef(param);
			elemrelDAO.insertFromReqForUserdef(param);
			elemvarcolvalDAO.insertFromReqForUserdef(param);
			elemfileDAO.insertFromReq(param);
		} else if(reqType.equals(WorkFlowApplication.UPDATE)) {
			eleminfoDAO.delete(param);
			elemvarcolmapDAO.deleteByeleminfoid(param);
			elemrelDAO.deleteByeleminfoid(param);
			elemvarcolvalDAO.deleteByeleminfoid(param);
			elemfileDAO.deleteByeleminfoid(param);
			
			eleminfoDAO.insertFromReq(param);
			elemvarcolmapDAO.insertFromReqForUserdef(param);
			elemrelDAO.insertFromReqForUserdef(param);
			elemvarcolvalDAO.insertFromReqForUserdef(param);
			elemfileDAO.insertFromReq(param);
		} else if(reqType.equals(WorkFlowApplication.DELETE)) {
			eleminfoDAO.updateDelYn (param);
		}
	
		eleminfoReqDAO.commit();
		TreeHandler.clearTreeMap();
		TreeHandler.getInstance().reload();
	}
	
	private void getDAO() throws Exception {
		if(eleminfoReqDAO == null) {	
			eleminfoReqDAO = new TeleminfoReqDAO(dba);
			eleminfoDAO = new TeleminfoDAO(dba);
			elemvarcolmapReqDAO = new TelemvarcolmapReqDAO(dba);
			elemvarcolmapDAO = new TelemvarcolmapDAO(dba);
			
			elemrelReqDAO = new TelemrelReqDAO(dba);
			elemrelDAO = new TelemrelDAO(dba);
			elemvarcolvalReqDAO = new TelemvarcolvalReqDAO(dba);
			elemvarcolvalDAO = new TelemvarcolvalDAO(dba);
			elemfileReqDAO = new TelemfileReqDAO(dba);
			elemfileDAO = new TelemfileDAO(dba);
			
			elemvarcoltypeDAO = new TelemvarcoltypeDAO(dba);
			elemvarcolattrDAO = new TelemvarcolattrDAO(dba);
		}
	}
	
		

}
