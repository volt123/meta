package com.lgdisplay.gen;

import java.util.HashMap;
import java.util.LinkedHashMap;

import com.itplus.common.actions.common.util.ArrayHelper;
import com.itplus.mm.actions.MMAction;
import com.lgdisplay.db.LgdDao;

public class LGDInfotypeAutoGenerator extends MMAction {

	public HashMap execute(HashMap in) throws Exception {
		HashMap outputHash = new LinkedHashMap();
		
		LgdDao lgdDao = new LgdDao();
		
		HashMap domainGroupInfos = new HashMap();
		domainGroupInfos = lgdDao.getDomainGroupInfo();

		HashMap domainInfos = new HashMap();
		domainInfos = lgdDao.getDomainInfo();
		
		try {
			HashMap[] result = null;
			
			ArrayHelper ah = new ArrayHelper();
			Object colDomNm = in.get("COL_DOM_NM");
			String infotypeNm = null;	// 인포타입명
			String infotypeLgclNm = null;	// 인포타입 논리명
			
			
			if(ah.isArray(colDomNm)){
				Object[] domGrpNmArray = ah.fetchArrayOfArray(in.get("DOM_GRP_NM"));				// 도메인 그룹명
				Object[] domGrpLgclNmArray = ah.fetchArrayOfArray(in.get("DOMAIN_GRP_LGCL_NM"));	// 도메인 그룹 논리명
				Object[] colDomNmArray = ah.fetchArrayOfArray(colDomNm);							// 도메인 명
				Object[] domLgclNmArray = ah.fetchArrayOfArray(in.get("DOMAIN_LGCL_NM"));			// 도메인 논리명
				
				Object[] dbmsDatatypeArray = ah.fetchArrayOfArray(in.get("DBMS_DATATYPE"));			// DBMS 데이터타입
				Object[] abbrArray = ah.fetchArrayOfArray(in.get("ABBR"));							// DBMS 데이터타입 약어명
				Object[] dfltScalArray = ah.fetchArrayOfArray(in.get("DFLT_SCAL"));					// 기본 자리수
				Object[] dfltPrcsArray = ah.fetchArrayOfArray(in.get("DFLT_PRCS"));					// 기본 소수점
				Object[] lineArray = ah.fetchArrayOfArray(in.get("LINE"));							// 순서
				
				result = new HashMap[colDomNmArray.length];
			
				for (int i = 0; i < colDomNmArray.length; i++) {
					System.out.println("INFOTYPEAutoGeneratorAction.start ==> " + i);
					result[i] = new HashMap();

					// LGD 인포타입명 생성규칙 (최초)
					// 도메인그룹영문명 + "_" + 도메인명 + "_" + 데이터타입 약어명 + 기본 자리수 + "," + 기본 소수점
					//
					// ==> 변경 (인포타입 생성시 소수점연결 부분을 '_'로 변경
					// LGD 인포타입명 생성규칙 (수정 - 2011.12.08) 
					// 도메인그룹영문명 + "_" + 도메인명 + "_" + 데이터타입 약어명 + 기본 자리수 + "_" + 기본 소수점
					//
					// ==> 추가 (인포타입논리명 생성)
					// LGD 인포타입논리명 생성규칙 (추가 - 2011.12.17) 
					// 도메인그룹영문명 + "_" + 도메인명 + "_" + 데이터타입 약어명 + 기본 자리수 + "_" + 기본 소수점
					
					if(dfltScalArray[i] == null || "".equals(dfltScalArray[i]) || Integer.parseInt((String)dfltScalArray[i]) < 1 ) {
						infotypeNm = (String)domainGroupInfos.get((String)domGrpNmArray[i]) + "_" +
					                 (String)domainInfos.get((String)colDomNmArray[i]) + "_" + (String)abbrArray[i];	
//   인포타입명은 영문 형태로 들고오도록 변경
//						infotypeNm = (String)domGrpLgclNmArray[i] + "_" +
//									 (String)domLgclNmArray[i] + "_" + (String)abbrArray[i];	

//						infotypeLgclNm = (String)domGrpLgclNmArray[i] + "-" +
//				                 		 (String)colDomNmArray[i] + "-" + (String)abbrArray[i];
//   도메인명을 논리명으로 가져오도록 변경						
						infotypeLgclNm = (String)domGrpNmArray[i] + "-" +
		                 		 		 (String)domLgclNmArray[i] + "-" + (String)abbrArray[i];
					
					} else if(dfltPrcsArray[i] == null || "".equals(dfltPrcsArray[i]) || Integer.parseInt((String)dfltScalArray[i]) < 1 ) {
						infotypeNm = (String)domainGroupInfos.get((String)domGrpNmArray[i]) + "_" +
								     (String)domainInfos.get((String)colDomNmArray[i]) + "_" + (String)abbrArray[i] + (String)dfltScalArray[i];

//						infotypeNm = (String)domGrpLgclNmArray[i] + "_" +
//								 	 (String)domLgclNmArray[i] + "_" + (String)abbrArray[i] + (String)dfltScalArray[i];	

//						infotypeLgclNm = (String)domGrpLgclNmArray[i] + "-" +
//			                 		 	 (String)colDomNmArray[i] + "-" + (String)abbrArray[i] + (String)dfltScalArray[i];
						
						infotypeLgclNm = (String)domGrpNmArray[i] + "-" +
	                 		 	 		 (String)domLgclNmArray[i] + "-" + (String)abbrArray[i] + (String)dfltScalArray[i];	

					} else {
						infotypeNm = (String)domainGroupInfos.get((String)domGrpNmArray[i]) + "_" +
								     (String)domainInfos.get((String)colDomNmArray[i]) + "_" + (String)abbrArray[i] + (String)dfltScalArray[i] + "_" + (String)dfltPrcsArray[i];
						
//						infotypeNm = (String)domGrpLgclNmArray[i] + "_" +
//									 (String)domLgclNmArray[i] + "_" + (String)abbrArray[i] + (String)dfltScalArray[i] + "_" + (String)dfltPrcsArray[i];	

//						infotypeLgclNm = (String)domGrpLgclNmArray[i] + "-" +
//		                 		 	 	 (String)colDomNmArray[i] + "-" + (String)abbrArray[i] + (String)dfltScalArray[i] + "_" + (String)dfltPrcsArray[i];
						
						infotypeLgclNm = (String)domGrpNmArray[i] + "-" +
                		 	 	 		 (String)domLgclNmArray[i] + "-" + (String)abbrArray[i] + (String)dfltScalArray[i] + "_" + (String)dfltPrcsArray[i];
					}
					
					result[i].put("INFOTYPE_NM", infotypeNm);			// 인포타입명
					result[i].put("INFOTYPE_LGCL_NM", infotypeLgclNm);	// 인포타입 논리명
					result[i].put("LINE", lineArray[i]);
				}
			}
			 
			this.genOutputHash(outputHash, result, "RETURN");
			
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
		
		return outputHash;
	}
}
