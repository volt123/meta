package com.lgdisplay.check;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.itplus.mm.actions.MMAction;
import com.itplus.mm.common.util.ArrayHelper;
import com.lgdisplay.db.LgdDao;
import com.lgdisplay.util.StringUtil;

/**
 * ��������� ��ȿ���˻� ���
 *  
 * - �˻����
 * 
 * 1. ��������
 *    1.1 ������ ���� �з����̾�� �� (9.1 - �ý��ۿ��� ����)
 *    1.2 ������ ����  �� �����ϴ���, �������� �����ϴ��� ������ ���� �ٲپ ���
 *    1.3 ������� �� UDP(���� �о� �鿩�� �ش� ���� ������ ���� ���
 * 2. �ٸ� ������ �������� �⺻���� ���(������ �������)
 *    2.1 �׸��� �������� ���̰� 30�ڰ� ������ ��� ���� �Ϻθ� ���� ��ȯ�϶� �˸�
 *    2.2 �׸��� �������� ���̰� �� ������� �ʰ� 30���̳� ���Կð�� �ʹ� ���� �� ����ߴٰ� �˸�.
 *    2.3 ���õ� ����� ���� �������� ���� ��� �� �����Ͽ�� ���������� ó��
 *    2.3 �׸��� �����ϴ� ��ü ����� �������ε� 30�ڰ� �Ѵ°�� ����DA�� ������ �޾� ó���϶�� �˸�  
 * 3. ���ڸ� ����� ���, ���ڸ� �߰��� ���� ���� ����
 *    3.1 �������� ó���� ������ �� ����
 *    3.2 ���� ����� ��� '_' ���� ����  ex) ATTR_12_CODE (x), ATTR12_CODE (0)
 * 9. ��Ÿ���̳ʿ��� �⺻������ ó���ϰ� �ִ� ��Ȳ
 *    9.1 ������ ���� �з����̾�� ��
 *    9.2 �� '_'�� ǥ�� (�������� ���ǵȴ�� ó���ǰ� ���� ==> ���� ���α׷������� �ش� ������ �ϵ��ڵ�ó�� (Ÿ����Ʈ �����ʿ�)
 *    9.3 ��� �� ����ؾ��� (ȭ�鿡�� ó���ǰ� ����)
 *    9.4 �ϼ��� �׸��� ������ ���� �ߺ�üũ (���� ���˽� ó���ϰ� ����) ==> ���� ���α׷����� ó���ϵ��� �����ʿ� (��������)
 *    
 * 99. �����߰� (2011.12.08)
 *    99.1 1.2 �������� 
 *         ������ �� 'Ư�������'�� ��� 1.2 ��Ģ�� ���ܰ� �߻�
 *         ��, �������� 'Ư�������'�� ���� ������ �� ����ϴ� ���� �ƴ϶� ���õ� ���(������ Ȥ�� ���)�� �����ؾ���. 
 *
 * 99. �������� (2012.01.30)
 *    99.1 1.2 �������� 
 *         ������ �� 'Ư�������'�� ��� 1.2 ��Ģ�� ���ܰ� �߻�
 *         ��, �������� 'Ư�������'�� ���� �Ϲݿ��� ���� �������� ����ϰ�, 
 *         30�ڰ� �Ѵ� ���� ���õ� ���(������ Ȥ�� ���)�� �����ؾ���. 
 *     
 * 100. ���ǻ���
 *      ��Ÿ�ý��ۿ����� �������� ������ ���, �̸� ������ó�� �� �� �� �ִ� ������ �ִµ�, 
 *      �� ���α׷��� �ش� ������ �����ϸ� �� ���ư�. (���� �����Ͽ� ���α׷��� �����ؾ���)       
 * 
 * @param UTW_ID		String[]	�ܾ�ID
 * @param ABBR_ID		String[]	�������ID
 * @param UTW_NM_ID		String[]	
 * @param FULL_UTW_NM	String[]	��û����
 * @param UTW_NM		String[]	�ܾ��
 * @param ABBR			String[]	��������
 * @param EN_NM			String[]	������
 * @param LINE			String[]	����
 * @param TP_CD			String[]	����(ABBR(���),EN_NM(������),UTW_NM(�ܾ��), PHSC_NM)
 * @param PHSC_NM		String[]	������ü��

 * @return LINE					String[]	����
 * @return VALD_CHECK_SUCC_YN	String[]	��ȿ���˻� ���� ����(Y,N)
 * @return ERROR_MSG			String[]	���� �޽���
 * @return PHSC_NM				String[]	���յ� ������(default: "", ���� ������ ��� ������ ������ ������ ����)

 * @throws Exception
 * 
 */
public class LGDUFWGeneratorValidCheck extends MMAction {
	
	public HashMap execute(HashMap in) throws Exception {

		// ����(?) ������ ã�Ƽ� �ش� �������� ������  ũ��� �ٲٴ� �۾��� �ؾ���
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
			String reGenPhscNm2 = "";               // �������� �з����̸鼭 Ư��������� ��츦 ó���ϱ� ���� ����
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
				
				// �� ���� �⺻ ���� ����
				// 1. �� ���� ���� ��� ��� ��������
				// 2. �� ���� ����ʼ����� ��� ��������
				// 3. �� ���� Ư�������� ��� ��������
				
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


					// 0. ������ �籸��
					//    0.1  ������ ���(�з���)�� ������ �� ���
					//    0.2  ���ڴ� ���� �� ���� "_" ������� ����
					//    0.3 ������ʼ�����(UTW_TECH_USE_ABBR) == 'Y' Ȯ���Ͽ� �ʼ��� ��� ������ ��� ���
					//   ==> �������� (2011.12.08)
					//    0.1 ������ ���(�з���)�� ������ �� ����ϴ� ���� �⺻���� �ϳ�
					//       ����, ������ ���(�з���)�� 'Ư�������'�� ���� ���õ� ���� �����ؾ���
					//   ==> �������� (2012.01.30)
					//    0.1 ������ ���(�з���)�� ������ �� ����ϴ� ���� �⺻���� �ϳ�
					//       ����, ������ ���(�з���)�� 'Ư�������'�� ���� �Ϲݿ��� ���� �������� ����ϰ�,
					//       30�ڰ� �Ѵ� ��쿡���� ���õ� ���(������ Ȥ�� ���)�� �����ؾ���
					
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

					// ��������� (������) ó��
					//   ==> �������� (2011.12.08)
					//    0.1 ������ ���(�з���)�� ������ �� ����ϴ� ���� �⺻���� �ϳ�
					//       ����, ������ ���(�з���)�� 'Ư�������'�� ���� ���õ� ���� �����ؾ���
					//   ==> �������� (2012.01.30)
					//    0.1 ������ ���(�з���)�� ������ �� ����ϴ� ���� �⺻���� �ϳ�
					//       ����, ������ ���(�з���)�� 'Ư�������'�� ���� �Ϲݿ��� ���� �������� ����ϰ�,
					//       30�ڰ� �Ѵ� ��쿡���� ���õ� ���(������ Ȥ�� ���)�� �����ؾ���
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

					//2. �׸� �������� ó�� ���ڰ� ���ڰ� �Ǹ� �ȵ�
					if (Character.isDigit(reGenPhscNm.charAt(0))) {
						errorCode = "4";
                    }
				
					//3. �׸��� �������� ���̰� 30(gAbbrLength)�ڰ� ������ ������ ��  ���� �����϶�� ���
					if ((reGenPhscNm.length() > gAbbrLen) && "".equals(errorCode)) {
						errorCode = "3";
					} 

					//   ==> �������� (2012.01.30)
					//    0.1 ������ ���(�з���)�� ������ �� ����ϴ� ���� �⺻���� �ϳ�
					//       ����, ������ ���(�з���)�� 'Ư�������'�� ���� �Ϲݿ��� ���� �������� ����ϰ�,
					//       30�ڰ� �Ѵ� ��쿡���� ���õ� ���(������ Ȥ�� ���)�� �����ؾ���
					//
					//3.5  �׸��� �������� ���̰� 30(gAbbrLength)�ڰ� ���� �����鼭, 
					//    ���������(�з���-Ư�������)�� ���� ����� ���� �������� ����϶�� ���
					if ("".equals(errorCode)) {
						if ("Y".equals(commonUseTermYnArray.get(ufwLastIndex))) {
							if ("ABBR".equals((String)tpCdArray[ufwLastIndex])) {
								if (reGenPhscNm2.length() <= gAbbrLen) {
									errorCode = "3.5";
								}
							}
						}
					}

					//4. �ٸ� ������ �������� �⺻���� ���(������ �������)
					//   - ������� �� ���� ��� ������ ������ ����
					//   - ���������� �ڵ����� �� ����ϴ� ���� �⺻���� �ϳ�
					//     ����, ������ ���(�з���)�� 'Ư�������'�� ���� ���õ� ���� �����ؾ���
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
					
					// ���� �߻��� �������� �����ϴ� �κ�
					//  - 2011.10.21 ���� 1 ���� ������.

//					String errorMsg1   = "�� ��������  �� ����ϼž� �մϴ�.\n";        // �ش翡���� ���ü� ����. ���� ó����.
					String errorMsg2   = "�� �������� ����Ƕ�  �� ���� ����ϼ̽��ϴ�. \n�Ϻ� ����� �������� ���������� �����ϼž� �մϴ�.\n";
//					String errorMsg2   = "��(��) ���������� �ϼž� �մϴ�.\n";            // ������������ ����
//					String errorMsg3   = "��(��) ���� �ϼž� �մϴ�.\n";                // ������������ ����
					String errorMsg3   = "�� ������ ���̰�  " + gAbbrLen + "�� �ʰ��ϰ� �ֽ��ϴ�. \n�Ϻ� ����� �������� ���� �����ϼž� �մϴ�.\n";
					String errorMsg3_5 = "�� ������ ���̰�  " + gAbbrLen + "�� ���� �ʰ� �ֽ��ϴ�. \n�̷��� ��쿡 ���������(�з��� - Ư�������)�� �������� ����ϼž� �մϴ�.\n";
					String errorMsg4   = "�� �������� ���ڷ� �����Ͻø� �ȵ˴ϴ�.\n";
					String errorMsg5   = "��ü ����� ������ " + gAbbrLen + "�� �ʰ��մϴ�.\nDA���� �����Ͽ� �ֽʽÿ�.\n";

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

						//5. ��ü�� ����� ������ 30�ڰ� �Ѵ°�� �������� �����϶�� ���
						if ("".equals(errorMsg)) {
							checkSuccYN = "N";
							errorMsg = "'" + (String)fulUtwNmArray[totalIndex + tmpIndex - 1] + "' �� " + errorMsg5;	
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
