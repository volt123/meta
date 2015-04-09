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
			String infotypeNm = null;	// ����Ÿ�Ը�
			String infotypeLgclNm = null;	// ����Ÿ�� ����
			
			
			if(ah.isArray(colDomNm)){
				Object[] domGrpNmArray = ah.fetchArrayOfArray(in.get("DOM_GRP_NM"));				// ������ �׷��
				Object[] domGrpLgclNmArray = ah.fetchArrayOfArray(in.get("DOMAIN_GRP_LGCL_NM"));	// ������ �׷� ����
				Object[] colDomNmArray = ah.fetchArrayOfArray(colDomNm);							// ������ ��
				Object[] domLgclNmArray = ah.fetchArrayOfArray(in.get("DOMAIN_LGCL_NM"));			// ������ ����
				
				Object[] dbmsDatatypeArray = ah.fetchArrayOfArray(in.get("DBMS_DATATYPE"));			// DBMS ������Ÿ��
				Object[] abbrArray = ah.fetchArrayOfArray(in.get("ABBR"));							// DBMS ������Ÿ�� ����
				Object[] dfltScalArray = ah.fetchArrayOfArray(in.get("DFLT_SCAL"));					// �⺻ �ڸ���
				Object[] dfltPrcsArray = ah.fetchArrayOfArray(in.get("DFLT_PRCS"));					// �⺻ �Ҽ���
				Object[] lineArray = ah.fetchArrayOfArray(in.get("LINE"));							// ����
				
				result = new HashMap[colDomNmArray.length];
			
				for (int i = 0; i < colDomNmArray.length; i++) {
					System.out.println("INFOTYPEAutoGeneratorAction.start ==> " + i);
					result[i] = new HashMap();

					// LGD ����Ÿ�Ը� ������Ģ (����)
					// �����α׷쿵���� + "_" + �����θ� + "_" + ������Ÿ�� ���� + �⺻ �ڸ��� + "," + �⺻ �Ҽ���
					//
					// ==> ���� (����Ÿ�� ������ �Ҽ������� �κ��� '_'�� ����
					// LGD ����Ÿ�Ը� ������Ģ (���� - 2011.12.08) 
					// �����α׷쿵���� + "_" + �����θ� + "_" + ������Ÿ�� ���� + �⺻ �ڸ��� + "_" + �⺻ �Ҽ���
					//
					// ==> �߰� (����Ÿ�Գ��� ����)
					// LGD ����Ÿ�Գ��� ������Ģ (�߰� - 2011.12.17) 
					// �����α׷쿵���� + "_" + �����θ� + "_" + ������Ÿ�� ���� + �⺻ �ڸ��� + "_" + �⺻ �Ҽ���
					
					if(dfltScalArray[i] == null || "".equals(dfltScalArray[i]) || Integer.parseInt((String)dfltScalArray[i]) < 1 ) {
						infotypeNm = (String)domainGroupInfos.get((String)domGrpNmArray[i]) + "_" +
					                 (String)domainInfos.get((String)colDomNmArray[i]) + "_" + (String)abbrArray[i];	
//   ����Ÿ�Ը��� ���� ���·� �������� ����
//						infotypeNm = (String)domGrpLgclNmArray[i] + "_" +
//									 (String)domLgclNmArray[i] + "_" + (String)abbrArray[i];	

//						infotypeLgclNm = (String)domGrpLgclNmArray[i] + "-" +
//				                 		 (String)colDomNmArray[i] + "-" + (String)abbrArray[i];
//   �����θ��� �������� ���������� ����						
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
					
					result[i].put("INFOTYPE_NM", infotypeNm);			// ����Ÿ�Ը�
					result[i].put("INFOTYPE_LGCL_NM", infotypeLgclNm);	// ����Ÿ�� ����
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
