package com.lgdisplay.check;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.itplus.mm.actions.MMAction;
import com.itplus.mm.common.util.ArrayHelper;
import com.lgdisplay.db.LgdDao;
import com.lgdisplay.util.StringUtil;

/**
 * 용어물리명생성 유효성검사 기능
 *  
 * - 검사로직
 * 
 * 1. 강제조항
 *    1.1 마지막 용어는 분류어이어야 함 (9.1 - 시스템에서 점검)
 *    1.2 마지막 용어는  약어를 선택하던지, 영문명을 선택하던지 강제로 약어로 바꾸어서 사용
 *    1.3 약어전용 용어를 UDP(에서 읽어 들여서 해당 용어는 무조건 약어로 사용
 * 2. 다른 용어들은 영문명을 기본으로 사용(마지막 용어제외)
 *    2.1 항목의 물리명의 길이가 30자가 넘으면 경고를 내고 일부를 약어로 전환하라 알림
 *    2.2 항목의 물리명의 길이가 약어를 사용하지 않고도 30자이내 들어왔올경우 너무 많은 약어를 사용했다고 알림.
 *    2.3 선택된 용어의 약어와 영문명이 같을 경우 약어를 선택하였어도 영문명으로 처리
 *    2.3 항목을 구성하는 전체 약어의 조합으로도 30자가 넘는경우 통합DA의 도움을 받아 처리하라고 알림  
 * 3. 숫자를 사용할 경우, 숫자만 중간에 따로 두지 않음
 *    3.1 물리명의 처음이 숫자일 수 없음
 *    3.2 숫자 용어의 경우 '_' 없이 연결  ex) ATTR_12_CODE (x), ATTR12_CODE (0)
 * 9. 메타마이너에서 기본적으로 처리하고 있는 상황
 *    9.1 마지막 용어는 분류어이어야 함
 *    9.2 용어간 '_'로 표시 (사전에서 정의된대로 처리되고 있음 ==> 현재 프로그램에서는 해당 조건을 하드코딩처리 (타사이트 수정필요)
 *    9.3 모든 용어를 사용해야함 (화면에서 처리되고 있음)
 *    9.4 완성된 항목의 물리명에 대한 중복체크 (최종 점검시 처리하고 있음) ==> 현재 프로그램에서 처리하도록 수정필요 (개선사항)
 *    
 * 99. 로직추가 (2011.12.08)
 *    99.1 1.2 로직변경 
 *         마지막 용어가 '특수관용어'인 경우 1.2 규칙의 예외가 발생
 *         즉, 마지막용어가 '특수관용어'인 경우는 강제로 약어를 사용하는 것이 아니라 선택된 용어(영문명 혹은 약어)를 조합해야함. 
 *
 * 99. 로직변경 (2012.01.30)
 *    99.1 1.2 로직변경 
 *         마지막 용어가 '특수관용어'인 경우 1.2 규칙의 예외가 발생
 *         즉, 마지막용어가 '특수관용어'인 경우는 일반용어와 같이 영문명을 사용하고, 
 *         30자가 넘는 경우는 선택된 용어(영문명 혹은 약어)로 조합해야함. 
 *     
 * 100. 주의사항
 *      메타시스템에서는 마지막이 숫자인 경우, 이를 도메인처리 안 할 수 있는 세팅이 있는데, 
 *      현 프로그램은 해당 세팅을 설정하면 안 돌아감. (새로 검증하여 프로그램을 개발해야함)       
 * 
 * @param UTW_ID		String[]	단어ID
 * @param ABBR_ID		String[]	영문약어ID
 * @param UTW_NM_ID		String[]	
 * @param FULL_UTW_NM	String[]	요청용어명
 * @param UTW_NM		String[]	단어명
 * @param ABBR			String[]	영문약어명
 * @param EN_NM			String[]	영문명
 * @param LINE			String[]	순서
 * @param TP_CD			String[]	유형(ABBR(약어),EN_NM(영문명),UTW_NM(단어명), PHSC_NM)
 * @param PHSC_NM		String[]	물리전체명

 * @return LINE					String[]	순서
 * @return VALD_CHECK_SUCC_YN	String[]	유효성검사 성공 여부(Y,N)
 * @return ERROR_MSG			String[]	에러 메시지
 * @return PHSC_NM				String[]	조합된 물리명(default: "", 값이 존재할 경우 서버가 전달한 값으로 조합)

 * @throws Exception
 * 
 */
public class LGDUFWGeneratorValidCheck extends MMAction {
	
	public HashMap execute(HashMap in) throws Exception {

		// 향후(?) 사전을 찾아서 해당 사전에서 정의한  크기로 바꾸는 작업을 해야함
		int gAbbrLen = 30;
		
		HashMap outputHash = new LinkedHashMap();
		
		try {
			HashMap[] result = null;
			LgdDao lgdDao = new LgdDao();
				
			ArrayHelper ah = new ArrayHelper();
			Object utwId = in.get("UTW_ID");
			String size = jspeed.base.util.StringHelper.nvl((String)in.get("SIZE"), "0")  ;
			
			String checkSuccYN = "Y";
			String errorCode = "", errorMsg = "";
			String reGenPhscNm = "";
			String reGenPhscNm2 = "";               // 마지막용어가 분류어이면서 특수관용어인 경우를 처리하기 위한 변수
			int tmpIndex, totalIndex = 0;
		
			if(ah.isArray(utwId)) {
				Object[] utwIdArray = ah.fetchArrayOfArray(utwId);					
				Object[] abbrIdArray = ah.fetchArrayOfArray(in.get("ABBR_ID"));		
				Object[] utwNmIdArray = ah.fetchArrayOfArray(in.get("UTW_NM_ID"));
				Object[] fulUtwNmArray = ah.fetchArrayOfArray(in.get("FULL_UTW_NM"));				
				Object[] utwNmArray = ah.fetchArrayOfArray(in.get("UTW_NM"));		
				Object[] abbrArray = ah.fetchArrayOfArray(in.get("ABBR"));
				Object[] enNmArray = ah.fetchArrayOfArray(in.get("EN_NM"));
				Object[] lineArray = ah.fetchArrayOfArray(in.get("LINE"));				
				Object[] tpCdArray = ah.fetchArrayOfArray(in.get("TP_CD"));				
				Object[] phscNmArray = ah.fetchArrayOfArray(in.get("PHSC_NM"));				
				
				result = new HashMap[Integer.parseInt(size)];
				
				// 용어에 대한 기본 정보 수집
				// 1. 용어에 대한 실제 약어 목록 가져오기
				// 2. 용어에 대한 약어필수여부 목록 가져오기
				// 3. 용어에 대한 특수관용어여부 목록 가져오기
				
				ArrayList<String> useAbbrYnArray = new ArrayList<String>();
				ArrayList<String> dbAbbrArray =  new ArrayList<String>();
				ArrayList<String> commonUseTermYnArray =  new ArrayList<String>();

				useAbbrYnArray       = lgdDao.getUseAbbrYnByUtwIds(utwIdArray);
				dbAbbrArray          = lgdDao.getAbbrByAbbrIds(abbrIdArray);
				commonUseTermYnArray = lgdDao.getCommonUseTermYnByUtwIds(utwIdArray);
				
				for (int index = 0; index < Integer.parseInt(size); index++) {
					tmpIndex = 0;
					errorCode = "";
					errorMsg = "";
					reGenPhscNm = "";
					reGenPhscNm2 = "";


					// 0. 물리명 재구성
					//    0.1  마지막 용어(분류어)는 무조건 약어를 사용
					//    0.2  숫자는 앞의 용어에 붙음 "_" 사용하지 않음
					//    0.3 약어사용필수여부(UTW_TECH_USE_ABBR) == 'Y' 확인하여 필수인 경우 무조건 약어 사용
					//   ==> 로직변경 (2011.12.08)
					//    0.1 마지막 용어(분류어)는 무조건 약어를 사용하는 것을 기본으로 하나
					//       만약, 마지막 용어(분류어)가 '특수관용어'인 경우는 선택된 용어로 조합해야함
					//   ==> 로직변경 (2012.01.30)
					//    0.1 마지막 용어(분류어)는 무조건 약어를 사용하는 것을 기본으로 하나
					//       만약, 마지막 용어(분류어)가 '특수관용어'인 경우는 일반용어와 같이 영문명을 사용하고,
					//       30자가 넘는 경우에서만 선택된 용어(영문명 혹은 약어)로 조합해야함
					
					for(int i = 0; i < utwIdArray.length; i++){
						if ( index == Integer.parseInt((String)lineArray[i]) )  ++tmpIndex;
						
						if("Y".equals(useAbbrYnArray.get(i))) {
							abbrArray[i] = dbAbbrArray.get(i);
							enNmArray[i] = dbAbbrArray.get(i);
						}
					}

					int ufwLastIndex = totalIndex + tmpIndex - 1;
					for (int x = totalIndex; x < ufwLastIndex; x++) {
						if ("EN_NM".equals((String)tpCdArray[x])) {
							String tempEnNm = (String)enNmArray[x];
							if (StringUtil.isInteger(tempEnNm)) {
								reGenPhscNm += tempEnNm.toUpperCase();
							} else {
								if (x == totalIndex) reGenPhscNm = tempEnNm.toUpperCase();
								else reGenPhscNm += "_" + tempEnNm.toUpperCase();
							}
						} else { 
							String tempAbbr = (String)abbrArray[x];
							if (StringUtil.isInteger(tempAbbr)) {
								reGenPhscNm += tempAbbr.toUpperCase();
							} else {
								if (x == totalIndex) reGenPhscNm = tempAbbr.toUpperCase();
								else reGenPhscNm += "_" + tempAbbr.toUpperCase();
							}
						}
					}

					// 마지막용어 (도메인) 처리
					//   ==> 로직변경 (2011.12.08)
					//    0.1 마지막 용어(분류어)는 무조건 약어를 사용하는 것을 기본으로 하나
					//       만약, 마지막 용어(분류어)가 '특수관용어'인 경우는 선택된 용어로 조합해야함
					//   ==> 로직변경 (2012.01.30)
					//    0.1 마지막 용어(분류어)는 무조건 약어를 사용하는 것을 기본으로 하나
					//       만약, 마지막 용어(분류어)가 '특수관용어'인 경우는 일반용어와 같이 영문명을 사용하고,
					//       30자가 넘는 경우에서만 선택된 용어(영문명 혹은 약어)로 조합해야함
					String lastTerm;
					String lastTerm2;
					
					if ("N".equals(commonUseTermYnArray.get(ufwLastIndex))) {
						lastTerm = dbAbbrArray.get(ufwLastIndex);
						if (1 == tmpIndex) reGenPhscNm += lastTerm;
						else reGenPhscNm += "_" + lastTerm;
					} else {
						reGenPhscNm2 = reGenPhscNm;
						if ("EN_NM".equals((String)tpCdArray[ufwLastIndex])) {
							lastTerm = ((String)enNmArray[ufwLastIndex]).toUpperCase();
							if (1 == tmpIndex) reGenPhscNm += lastTerm;
							else reGenPhscNm += "_" + lastTerm;
						} else {
							lastTerm = dbAbbrArray.get(ufwLastIndex);
							if (1 == tmpIndex) reGenPhscNm += lastTerm;
							else reGenPhscNm += "_" + lastTerm;
							
							lastTerm2 = ((String)enNmArray[ufwLastIndex]).toUpperCase();
							if (1 == tmpIndex) reGenPhscNm2 += lastTerm2;
							else reGenPhscNm2 += "_" + lastTerm2;
						}
					}

					//2. 항목 물리명의 처음 문자가 숫자가 되면 안됨
					if (Character.isDigit(reGenPhscNm.charAt(0))) {
						errorCode = "4";
                    }
				
					//3. 항목의 물리명의 길이가 30(gAbbrLength)자가 넘으면 임의의 용어를  약어로 변경하라고 경고
					if ((reGenPhscNm.length() > gAbbrLen) && "".equals(errorCode)) {
						errorCode = "3";
					} 

					//   ==> 로직변경 (2012.01.30)
					//    0.1 마지막 용어(분류어)는 무조건 약어를 사용하는 것을 기본으로 하나
					//       만약, 마지막 용어(분류어)가 '특수관용어'인 경우는 일반용어와 같이 영문명을 사용하고,
					//       30자가 넘는 경우에서만 선택된 용어(영문명 혹은 약어)로 조합해야함
					//
					//3.5  항목의 물리명의 길이가 30(gAbbrLength)자가 넘지 않으면서, 
					//    마지막용어(분류어-특수관용어)를 약어로 사용한 경우는 영문명을 사용하라고 경고
					if ("".equals(errorCode)) {
						if ("Y".equals(commonUseTermYnArray.get(ufwLastIndex))) {
							if ("ABBR".equals((String)tpCdArray[ufwLastIndex])) {
								if (reGenPhscNm2.length() <= gAbbrLen) {
									errorCode = "3.5";
								}
							}
						}
					}

					//4. 다른 용어들은 영문명을 기본으로 사용(마지막 용어제외)
					//   - 영문명과 약어가 같은 경우 에러로 비교하지 않음
					//   - 마지막용어는 자동으로 약어를 사용하는 것을 기본으로 하나
					//     만약, 마지막 용어(분류어)가 '특수관용어'인 경우는 선택된 용어로 조합해야함
					String tmpAbbr = "";
					if ("".equals(errorCode)) {
						for (int j = totalIndex; j < ufwLastIndex; j++) {
							tmpAbbr = "";
							String tmpAbbr1 = "";
							if (!"EN_NM".equals(tpCdArray[j])) {
								if (!((String)enNmArray[j]).toUpperCase().equals(((String)abbrArray[j]).toUpperCase())) {
									for (int k = totalIndex; k < ufwLastIndex; k++) {
										if (j == k) 
											tmpAbbr1 = (String)enNmArray[j];
										else 
											tmpAbbr1 = (String)abbrArray[k];	
										
										if (StringUtil.isInteger(tmpAbbr1)) 
											tmpAbbr += tmpAbbr1;
										else 
											tmpAbbr += "_" + tmpAbbr1;
									}
									
									tmpAbbr += "_" + lastTerm;
									
									if (tmpAbbr.length() - 1  <= gAbbrLen) {
										errorCode = "2";
										checkSuccYN = "N";
										errorMsg = "'" + (String)fulUtwNmArray[ufwLastIndex] + "'";
										break;
									}
								}
							}	
						}					
					}		
					
					// 에러 발생시 에러문장 설정하는 부분
					//  - 2011.10.21 에러 1 조건 없어짐.

//					String errorMsg1   = "의 마지막은  약어를 사용하셔야 합니다.\n";        // 해당에러가 나올수 없음. 강제 처리함.
					String errorMsg2   = "의 물리명을 만드실때  약어를 많이 사용하셨습니다. \n일부 용어의 물리명은 영문명으로 선택하셔야 합니다.\n";
//					String errorMsg2   = "는(은) 영문명으로 하셔야 합니다.\n";            // 로직변경으로 삭제
//					String errorMsg3   = "는(은) 약어로 하셔야 합니다.\n";                // 로직변경으로 삭제
					String errorMsg3   = "의 물리명 길이가  " + gAbbrLen + "을 초과하고 있습니다. \n일부 용어의 물리명을 약어로 선택하셔야 합니다.\n";
					String errorMsg3_5 = "의 물리명 길이가  " + gAbbrLen + "를 넘지 않고 있습니다. \n이러한 경우에 마지막용어(분류어 - 특수관용어)는 영문명을 사용하셔야 합니다.\n";
					String errorMsg4   = "의 물리명을 숫자로 시작하시면 안됩니다.\n";
					String errorMsg5   = "전체 약어의 조합이 " + gAbbrLen + "을 초과합니다.\nDA에게 문의하여 주십시요.\n";

					if ("4".equals(errorCode)) {
						checkSuccYN = "N";
						errorMsg =  "'" + fulUtwNmArray[index] + "'" + errorMsg4;
					} else if ("3.5".equals(errorCode)) {
						checkSuccYN = "N";
						errorMsg = "'" + fulUtwNmArray[index] + "'" + errorMsg3_5;
					} else if ("3".equals(errorCode)) {
						for (int j = totalIndex; j < totalIndex + tmpIndex; j++) {
							if (!"ABBR".equals(tpCdArray[j])) {   
								checkSuccYN = "N";
								errorMsg = "'" + (String)fulUtwNmArray[totalIndex + tmpIndex - 1] + "'" + errorMsg3;
								break;
							}
						}

						//5. 전체의 약어의 조합이 30자가 넘는경우 수동으로 수정하라는 경고
						if ("".equals(errorMsg)) {
							checkSuccYN = "N";
							errorMsg = "'" + (String)fulUtwNmArray[totalIndex + tmpIndex - 1] + "' 의 " + errorMsg5;	
						}
					} else if ("2".equals(errorCode)) {
						checkSuccYN = "N";
						errorMsg += errorMsg2;

					} else {
						checkSuccYN = "Y";
						errorMsg = "";
					}
					
					result[index] = new HashMap();
					
					result[index].put("LINE", lineArray[totalIndex + tmpIndex - 1]);
					result[index].put("VALD_CHECK_SUCC_YN", checkSuccYN);
					result[index].put("ERROR_MSG", errorMsg);					
					result[index].put("PHSC_NM", reGenPhscNm);	
					
					totalIndex = totalIndex + tmpIndex;
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
