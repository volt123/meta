package com.lgdisplay.check;

import java.rmi.Naming;
import java.util.HashMap;

import com.lgdisplay.util.JLog;

public class LGDNamingRuleCheck extends com.itplus.mm.server.structure.AbstractNamingRule {

	private boolean bLogPrint = true;
	
	public LGDNamingRuleCheck() throws Exception {
		super();
	}
	
	/**
	 * <pre>
	 * Validation Check for LGDisplay Database Naming Rule (Oracle)
	 * </pre>
	 * @param HashMap
	 *        param.get("DAT_VAL") - Database Name, 
	 *        param.get("DAT_SYS_ID") - Business System ID
	 * @return boolean
	 * @throws Exception 
	 */
	
	public boolean database(HashMap param) throws Exception {
		return true;
	}

	/**
	 * <pre>
	 * Validation Check for LGDisplay Tablespaces Naming Rule (Oracle)
	 * </pre>
	 * @param HashMap 
	 * 		  param.get("DAT_VAL") - Tablespaces Name, 
	 * 		  param.get("DAT_SYS_ID") - Business System ID
	 * @return boolean
	 * @throws Exception 
	 */
	
	public boolean tablespace(HashMap param) throws Exception {
		return true;
	}

	/**
	 * 
	 * <pre>
	 * Validation Check for LGDisplay Table Naming Rule (Oracle)
	 * </pre>
	 * @param HashMap
	 *        param.get("DAT_VAL") - Table Name, 
	 *        param.get("DAT_SYS_ID") - Business System ID
	 * @see
	 *        Naming Rule Formation -
	 *          Table/View Code(1) + '_' + Company Code(1) + '_' + Subject Area Code (2) + Data Group Code (1) + Sequence (3) + '_' + Table Type Code(1)
	 *        
	 *        1) Table/View Code - 1 digit
	 *           T - Table
	 *           V - View
	 *        2) Company Code - 1 digit
	 *           H - LGDisplay
	 *           O - Other
	 *        3) Subject Area Code - 2 digit
	 *           --> Not Check!
	 *        4) Data Group Code - 1 digit
	 *           --> Not Check!
	 *        5) Sequence - 3 digit
	 *           --> Not Check!
	 *        6) Table Attribute Type Code - 1 digit
	 *           M - Master
	 *           P - Particular
	 *           D - Detail
	 *           F - inFormation
	 *           H - History
	 *           R - Relationship
	 *           S - Summary
	 *           C - Code
	 *           Q - seQuence
	 *           B - Backup
	 *           T - Temporary
	 *           O - cOntents
	 *           N - sNapshot
	 *        7) Etc 
	 *           If 'Subject Area Code + Data Group Code' is in ('ORR', 'ORC'), Sequence is not 3 digit (it has the original Sequence.)
	 * @return boolean
	 * @throws Exception 
	 */
	
	public boolean table(HashMap param) throws Exception{

		if (bLogPrint) {
			JLog.debug("#######################################################");
			JLog.debug(":::::::       Check LGDisplay Table Naming Rule       :::::::");
			JLog.debug("param Info: " + param.toString());
		}
		
		String DAT_SYS_ID = (String)param.get("DAT_SYS_ID");
		String TargetTableName = (String)param.get("DAT_VAL");

		if (bLogPrint) {
			JLog.debug("Target TableName: " + TargetTableName);
			JLog.debug("Business System ID: " + DAT_SYS_ID);
		}
		
		HashMap omap = new HashMap();
		omap.put("M", "M(Master)");
		omap.put("P", "P(Particular)");
		omap.put("D", "D(Detail)");
		omap.put("F", "F(inFormation)");
		omap.put("H", "H(History)");
		omap.put("R", "R(Relation)");
		omap.put("S", "S(Summary)");
		omap.put("C", "C(Code)");
		omap.put("Q", "Q(seQuence)");
		omap.put("B", "B(Backup)");
		omap.put("T", "T(Temporary)");
		omap.put("O", "O(cOntents)");
		omap.put("N", "N(sNapshot)");

		try {
			if ( null == TargetTableName ) 
				throw new Exception("테이블 이름이 없습니다.");
			
			TargetTableName = TargetTableName.trim();
			String t = TargetTableName;
			int targetTableNameLen = TargetTableName.trim().length();

			// Check 1. Table/View Code
			t = TargetTableName.substring(0, 1);
			if ( !(t.equals ("T"))) 
				throw new Exception("테이블 이름은 'T'로 시작해야 합니다. 그러나 입력된 테이블 이름은('" + TargetTableName + "')이고, 테이블 구분자리에 잘못 적힌 문자는 '" + t + "'입니다.");

			// Check 2. Table Size
			String checkExceptionAreaCode = TargetTableName.substring(4, 7);
			if ( !(checkExceptionAreaCode.equals("ORR") || checkExceptionAreaCode.equals("ORC")) ) {
				if ( !(12 == targetTableNameLen) ) 
					throw new Exception("테이블의 길이는 12 digits입니다.. 그러나 현재 테이블의 이름은('" + TargetTableName + "')이고, 테이블의 길이는 '" + TargetTableName.trim().length() + "'입니다.");
			}

			// Check 3. '_' digits
			t = TargetTableName.substring(1, 2);
			if ( !(t.equals ("_"))) 
				throw new Exception("테이블 이름의 T 다음에는 '_'가 있어야 합니다. 그러나 입력된 테이블 이름은('" + TargetTableName + "')이고, _ 자리에 잘못 적힌 문자는 '" + t + "'입니다.");
			t = TargetTableName.substring(3, 4);
			if ( !(t.equals ("_"))) 
				throw new Exception("테비블 이름의 회사명 다음에는 '_'가 있어야 합니다. 그러나 입력된 테이블 이름은('" + TargetTableName + "')이고, _ 자리에 잘못 적힌 문자는 '" + t + "'입니다.");
			t = TargetTableName.substring(targetTableNameLen - 2, targetTableNameLen - 1);
			if ( !(t.equals ("_"))) 
				throw new Exception("테이블 이름의 테이블 유형 다음에는 '_'가 있어야 합니다. 그러나 입력된 테이블 이름은('" + TargetTableName + "')이고, _ 자리에 잘못 적힌 문자는 '" + TargetTableName.substring(targetTableNameLen - 1, targetTableNameLen) + "'입니다.");
			
			// Check 4. Table Attribute Type Code
			t = TargetTableName.substring(targetTableNameLen - 1, targetTableNameLen);
			if ( !omap.containsKey(t)) 
				throw new Exception("테이블 유형은 (M,P,D,F,H,R,S,C,Q,B,T,O,N) 중에서 구성되어야 합니다. 그러나 입력된 테이블 이름은('" + TargetTableName + "')이고, '" + t + "' 테이블 유형에 속하지 않습니다.");

			if (bLogPrint) {
				JLog.debug("#######################################################");
				JLog.debug(" ==> 테이블 이름이 잘 구성되었습니다!");
				JLog.debug("#######################################################");
			}
		} catch (Exception e) {
			//e.printStackTrace();

			if (bLogPrint) {
				JLog.debug("#######################################################");
				JLog.debug("==> 테이블 이름 구성이 잘못 되었습니다!! " + e.getMessage());
				JLog.debug("#######################################################");
			}
			throw e;
		}
		
		return true;
	}

	/**
	 * 
	 * <pre>
	 * Validation Check for LGDisplay PK(Primary Key) Naming Rule (Oracle)
	 * </pre>
	 * @param HashMap
	 * 		  param.get("DAT_VAL") - PK Name, 
	 * 		  param.get("TB_NM") - Table Name, 
	 *        param.get("DAT_SYS_ID") - Business System ID
	 *        
	 * @see
	 *        Naming Rule Formation -
	 *          PK Code(2) + '_' + Identifier Code (8)
	 *        
	 *        1) PK Code - 2 digit
	 *           PK - Primary key
	 *        2) Identifier Code - 8 digit
	 *           Identifier Code : Company Code(1) + '_' + Subject Area Code (2) + Data Group Code (1) + Sequence (3) 
	 *           'T' 및 데이터 생성주기를 제외한 8자리 값
	 *             
	 * @return boolean
	 * @throws Exception
	 */

	/**
	public boolean primarykey(HashMap param) throws Exception {
		
		if (bLogPrint) {
			JLog.debug("#######################################################");
			JLog.debug(":::::::       Check LGDisplay PK Naming Rule       :::::::");
			JLog.debug("param Info: " + param.toString());
		}
		
		String DAT_SYS_ID = (String)param.get("DAT_SYS_ID");
		String TargetPKName = (String)param.get("DAT_VAL");
		
		if (bLogPrint) {
			JLog.debug("Target PKName: " + TargetPKName);
			JLog.debug("Business System ID: " + DAT_SYS_ID);
		}

		try {
			if ( null == TargetPKName ) 
				throw new Exception("PK 이름이 없습니다.");
			
			TargetPKName = TargetPKName.trim();
			String p = TargetPKName;
			int targetPKNameLen = TargetPKName.trim().length();

			// Check 1. PK Code
			p = TargetPKName.substring(0, 2);
			if ( !(p.equals ("PK"))) 
				throw new Exception("PK 이름은 'PK'로 시작해야 합니다. 그러나 입력된 이름은('" + TargetPKName + "')이고, PK 구분자리에 잘못 적힌 문자는 '" + p + "'입니다.");

			// Check 2. PK Size
			if ( ! (11 == targetPKNameLen)  )
				throw new Exception("PK 총  길이는  11 digits 입니다. 그러나 현재 PK명의 이름은('" + TargetPKName + "')이고, PK명의 길이는 '" + TargetPKName.trim().length() + "'입니다.");
			   
			   
			// Check 3. '_' digits
			p = TargetPKName.substring(2, 3);
			if ( !(p.equals ("_"))) 
				throw new Exception("PK 명의 PK 다음에는 '_'가 있어야 합니다. 그러나 입력된 PK명의 이름은('" + TargetPKName + "')이고, _ 자리에 잘못 적힌 문자는 '" + p + "'입니다.");

			if (bLogPrint) {
				JLog.debug("#######################################################");
				JLog.debug(" ==>PK명 구성이 잘 되었습니다!");
				JLog.debug("#######################################################");
			}
		}
		
		 catch (Exception e) {
			//e.printStackTrace();

			if (bLogPrint) {
				JLog.debug("#######################################################");
				JLog.debug("==> PK명 구성이 잘못 되었습니다!! " + e.getMessage());
				JLog.debug("#######################################################");
			}
			throw e;
		}
		
		return true;
	}
	*/
	
	/**
	 * 
	 * <pre>
	 * Validation Check for LGDisplay FK(Foreign Key) Naming Rule (Oracle)
	 * </pre>
	 * @param HashMap
	 * 		  param.get("DAT_VAL") - FK Name, 
	 * 		  param.get("FK_TB_NM") - FK Table Name, 
	 * 		  param.get("PK_TB_NM") - PK Table Name, 
	 *        param.get("DAT_SYS_ID") - Business System ID
	 * @return boolean
	 * @throws Exception
	 */
	
	/**
	public boolean foreignkey(HashMap param) throws Exception {
		return true;
	}
	
	*/
	
	/**
	 * 
	 * <pre>
	 * Validation Check for LGDisplay Function Naming Rule (Oracle)
	 * </pre>
	 * @param HashMap
	 * 		  param.get("DAT_VAL") - Function Name, 
	 *        param.get("DAT_SYS_ID") - Business System ID
	 * <pre>
	 * @return boolean
	 * @throws Exception
	 */
	public boolean function(HashMap param) throws Exception {
		return true;
	}
	
	/**
	 * 
	 * <pre>
	 * Validation Check for LGDisplay Stored Procedure Naming Rule (Oracle)
	 * </pre>
	 * @param HashMap
	 * 		  param.get(DAT_VAL) - SP Name, 
	 *        param.get("DAT_SYS_ID") - Business System ID
	 * @return boolean
	 * @throws Exception
	 */
	public boolean storedprocedure(HashMap param) throws Exception {
		return true;
	}
	
	/**
	 * 
	 * <pre>
	 * Validation Check for LGDisplay Index Naming Rule (Oracle)
	 * </pre>
	 * @param HashMap
	 * 		  param.get(DAT_VAL) - Index Name, 
	 *        param.get("DAT_SYS_ID") - Business System ID
	 * @see
	 *        Index Naming Rule Formation - Index Code(1) + '_' + Identifier Code (8) + '_' + Index Type Code (1 or 2) + Turn Code(2)
	 *        Identifier Code : Company Code(1) + '_' + Subject Area Code (2) + Data Group Code (1) + Sequence (3) 
	 *        
	 *        1) Index Code - 1 digit
	 *           X - Index
	 *        2) Company Code - 1 digit
	 *           H - LGDisplay
	 *           O - Other
	 *        3) Subject Area Code - 2 digit
	 *           --> Not Check!
	 *        4) Data Group Code - 1 digit
	 *           --> Not Check!
	 *        5) Sequence - 3 digit
	 *           --> Not Check!
	 *        6) Index Type Code - 1 or 2 digit
	 *           PK - Primary key
	 *           P - Partition
	 *           C - Cluster
	 *           U - Unique
	 *           A - non-unique
	 *           X - cluster + unique
	 *        7) Turn Code - 2 digit 
	 *           01~99 --> Not Check!
	 * @return 
	 * @throws Exception
	 */
	
	/**
	public boolean index(HashMap param) throws Exception {


		if (bLogPrint) {
			JLog.debug("#######################################################");
			JLog.debug(":::::::       Check LGDisplay Index Naming Rule       :::::::");
			JLog.debug("param Info: " + param.toString());
		}
		
		String DAT_SYS_ID = (String)param.get("DAT_SYS_ID");
		String TargetIndexName = (String)param.get("DAT_VAL");

		if (bLogPrint) {
			JLog.debug("Target IndexName: " + TargetIndexName);
			JLog.debug("Business System ID: " + DAT_SYS_ID);
		}
		
		HashMap omap = new HashMap();
		omap.put("PK", "PK(Primary key)");
		omap.put("P", "P(Partition)");
		omap.put("C", "C(Cluster)");
		omap.put("U", "U(Unique)");
		omap.put("A", "A(non-unique)");
		omap.put("X", "X(cluster + unique)");

		try {
			if ( null == TargetIndexName ) 
				throw new Exception("인덱스명이 없습니다.");
			
			TargetIndexName = TargetIndexName.trim();
			String I = TargetIndexName;
			int targetIndexNameLen = TargetIndexName.trim().length();

			// Check 1. Index Code
			I = TargetIndexName.substring(0, 1);
			if ( !(I.equals ("X"))) 
				throw new Exception("인덱스명 이름은 'X'로 시작해야 합니다. 그러나 입력된 인덱스명은('" + TargetIndexName + "')이고, 인덱스명 구분자리에 잘못 적힌 문자는 '" + I + "'입니다.");

			// Check 2. Index Size
			// I= targetIndexNameLen() ;
			//	if ( !(14 >= targetIndexNameLen ) ) 
			//		throw new Exception("인덱스명의  식별자 길이는 13~14 digits입니다.. 그러나 현재 인덱스명은('" + TargetIndexName + "')이고, 인덱스명의 길이는 '" + TargetIndexName.trim().length() + "'입니다."); 

			// Check 3. '_' digits
			I = TargetIndexName.substring(1, 2);
			if ( !(I.equals ("_"))) 
				throw new Exception("인덱스명의 X 다음에는 '_'가 있어야 합니다. 그러나 입력된 인덱스명은('" + TargetIndexName + "')이고, _ 자리에 잘못 적힌 문자는 '" + I + "'입니다.");

			I = TargetIndexName.substring(10, 11);
			if ( !(I.equals ("_"))) 
				throw new Exception("인덱스명의 식별자명 다음에는 '_'가 있어야 합니다. 그러나 입력된 인덱스명은('" + TargetIndexName + "')이고, " + I + "'입니다.");
			
			// Check 4. Index Type Code
			I = TargetIndexName.substring(11, 12);
			if ( !omap.containsKey(I)) 
				throw new Exception("인덱스 유형은 (PK,C,U,A,X) 중에서 구성되어야 합니다. 그러나 입력된 인덱스명은('" + TargetIndexName + "')이고, '" + I + "' 인덱스 유형에 속하지 않습니다.");

			if (bLogPrint) {
				JLog.debug("#######################################################");
				JLog.debug(" ==> 인덱스명 이름이 잘 구성되었습니다!");
				JLog.debug("#######################################################");
			}
		} catch (Exception e) {
			//e.printStackTrace();

			if (bLogPrint) {
				JLog.debug("#######################################################");
				JLog.debug("==> 인덱스명 이름 구성이 잘못 되었습니다!! " + e.getMessage());
				JLog.debug("#######################################################");
			}
			throw e;
		}
		
		return true;
	}
	
	*/	

	/**
	 * 
	 * <pre>
	 * Validation Check for LGDisplay View Naming Rule (Oracle)
	 * </pre>
	 * @param HashMap
	 * 		  param.get(DAT_VAL) - View Name, 
	 *        param.get("DAT_SYS_ID") - Business System ID
	 * @return
	 * @throws Exception
	 */
	public boolean view(HashMap param) throws Exception {
		return true;
	}
	
	/**
	 * 
	 * <pre>
	 * Validation Check for LGDisplay Trigger Naming Rule (Oracle)
	 * </pre>
	 * @param HashMap
	 * 		  param.get(DAT_VAL) - Trigger Name, 
	 *        param.get("DAT_SYS_ID") - Business System ID
	 * @return
	 * @throws Exception
	 */
	public boolean trigger(HashMap param) throws Exception {
		return true;
	}

}
