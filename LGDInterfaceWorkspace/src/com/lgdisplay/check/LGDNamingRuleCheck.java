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
				throw new Exception("���̺� �̸��� �����ϴ�.");
			
			TargetTableName = TargetTableName.trim();
			String t = TargetTableName;
			int targetTableNameLen = TargetTableName.trim().length();

			// Check 1. Table/View Code
			t = TargetTableName.substring(0, 1);
			if ( !(t.equals ("T"))) 
				throw new Exception("���̺� �̸��� 'T'�� �����ؾ� �մϴ�. �׷��� �Էµ� ���̺� �̸���('" + TargetTableName + "')�̰�, ���̺� �����ڸ��� �߸� ���� ���ڴ� '" + t + "'�Դϴ�.");

			// Check 2. Table Size
			String checkExceptionAreaCode = TargetTableName.substring(4, 7);
			if ( !(checkExceptionAreaCode.equals("ORR") || checkExceptionAreaCode.equals("ORC")) ) {
				if ( !(12 == targetTableNameLen) ) 
					throw new Exception("���̺��� ���̴� 12 digits�Դϴ�.. �׷��� ���� ���̺��� �̸���('" + TargetTableName + "')�̰�, ���̺��� ���̴� '" + TargetTableName.trim().length() + "'�Դϴ�.");
			}

			// Check 3. '_' digits
			t = TargetTableName.substring(1, 2);
			if ( !(t.equals ("_"))) 
				throw new Exception("���̺� �̸��� T �������� '_'�� �־�� �մϴ�. �׷��� �Էµ� ���̺� �̸���('" + TargetTableName + "')�̰�, _ �ڸ��� �߸� ���� ���ڴ� '" + t + "'�Դϴ�.");
			t = TargetTableName.substring(3, 4);
			if ( !(t.equals ("_"))) 
				throw new Exception("�׺�� �̸��� ȸ��� �������� '_'�� �־�� �մϴ�. �׷��� �Էµ� ���̺� �̸���('" + TargetTableName + "')�̰�, _ �ڸ��� �߸� ���� ���ڴ� '" + t + "'�Դϴ�.");
			t = TargetTableName.substring(targetTableNameLen - 2, targetTableNameLen - 1);
			if ( !(t.equals ("_"))) 
				throw new Exception("���̺� �̸��� ���̺� ���� �������� '_'�� �־�� �մϴ�. �׷��� �Էµ� ���̺� �̸���('" + TargetTableName + "')�̰�, _ �ڸ��� �߸� ���� ���ڴ� '" + TargetTableName.substring(targetTableNameLen - 1, targetTableNameLen) + "'�Դϴ�.");
			
			// Check 4. Table Attribute Type Code
			t = TargetTableName.substring(targetTableNameLen - 1, targetTableNameLen);
			if ( !omap.containsKey(t)) 
				throw new Exception("���̺� ������ (M,P,D,F,H,R,S,C,Q,B,T,O,N) �߿��� �����Ǿ�� �մϴ�. �׷��� �Էµ� ���̺� �̸���('" + TargetTableName + "')�̰�, '" + t + "' ���̺� ������ ������ �ʽ��ϴ�.");

			if (bLogPrint) {
				JLog.debug("#######################################################");
				JLog.debug(" ==> ���̺� �̸��� �� �����Ǿ����ϴ�!");
				JLog.debug("#######################################################");
			}
		} catch (Exception e) {
			//e.printStackTrace();

			if (bLogPrint) {
				JLog.debug("#######################################################");
				JLog.debug("==> ���̺� �̸� ������ �߸� �Ǿ����ϴ�!! " + e.getMessage());
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
	 *           'T' �� ������ �����ֱ⸦ ������ 8�ڸ� ��
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
				throw new Exception("PK �̸��� �����ϴ�.");
			
			TargetPKName = TargetPKName.trim();
			String p = TargetPKName;
			int targetPKNameLen = TargetPKName.trim().length();

			// Check 1. PK Code
			p = TargetPKName.substring(0, 2);
			if ( !(p.equals ("PK"))) 
				throw new Exception("PK �̸��� 'PK'�� �����ؾ� �մϴ�. �׷��� �Էµ� �̸���('" + TargetPKName + "')�̰�, PK �����ڸ��� �߸� ���� ���ڴ� '" + p + "'�Դϴ�.");

			// Check 2. PK Size
			if ( ! (11 == targetPKNameLen)  )
				throw new Exception("PK ��  ���̴�  11 digits �Դϴ�. �׷��� ���� PK���� �̸���('" + TargetPKName + "')�̰�, PK���� ���̴� '" + TargetPKName.trim().length() + "'�Դϴ�.");
			   
			   
			// Check 3. '_' digits
			p = TargetPKName.substring(2, 3);
			if ( !(p.equals ("_"))) 
				throw new Exception("PK ���� PK �������� '_'�� �־�� �մϴ�. �׷��� �Էµ� PK���� �̸���('" + TargetPKName + "')�̰�, _ �ڸ��� �߸� ���� ���ڴ� '" + p + "'�Դϴ�.");

			if (bLogPrint) {
				JLog.debug("#######################################################");
				JLog.debug(" ==>PK�� ������ �� �Ǿ����ϴ�!");
				JLog.debug("#######################################################");
			}
		}
		
		 catch (Exception e) {
			//e.printStackTrace();

			if (bLogPrint) {
				JLog.debug("#######################################################");
				JLog.debug("==> PK�� ������ �߸� �Ǿ����ϴ�!! " + e.getMessage());
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
				throw new Exception("�ε������� �����ϴ�.");
			
			TargetIndexName = TargetIndexName.trim();
			String I = TargetIndexName;
			int targetIndexNameLen = TargetIndexName.trim().length();

			// Check 1. Index Code
			I = TargetIndexName.substring(0, 1);
			if ( !(I.equals ("X"))) 
				throw new Exception("�ε����� �̸��� 'X'�� �����ؾ� �մϴ�. �׷��� �Էµ� �ε�������('" + TargetIndexName + "')�̰�, �ε����� �����ڸ��� �߸� ���� ���ڴ� '" + I + "'�Դϴ�.");

			// Check 2. Index Size
			// I= targetIndexNameLen() ;
			//	if ( !(14 >= targetIndexNameLen ) ) 
			//		throw new Exception("�ε�������  �ĺ��� ���̴� 13~14 digits�Դϴ�.. �׷��� ���� �ε�������('" + TargetIndexName + "')�̰�, �ε������� ���̴� '" + TargetIndexName.trim().length() + "'�Դϴ�."); 

			// Check 3. '_' digits
			I = TargetIndexName.substring(1, 2);
			if ( !(I.equals ("_"))) 
				throw new Exception("�ε������� X �������� '_'�� �־�� �մϴ�. �׷��� �Էµ� �ε�������('" + TargetIndexName + "')�̰�, _ �ڸ��� �߸� ���� ���ڴ� '" + I + "'�Դϴ�.");

			I = TargetIndexName.substring(10, 11);
			if ( !(I.equals ("_"))) 
				throw new Exception("�ε������� �ĺ��ڸ� �������� '_'�� �־�� �մϴ�. �׷��� �Էµ� �ε�������('" + TargetIndexName + "')�̰�, " + I + "'�Դϴ�.");
			
			// Check 4. Index Type Code
			I = TargetIndexName.substring(11, 12);
			if ( !omap.containsKey(I)) 
				throw new Exception("�ε��� ������ (PK,C,U,A,X) �߿��� �����Ǿ�� �մϴ�. �׷��� �Էµ� �ε�������('" + TargetIndexName + "')�̰�, '" + I + "' �ε��� ������ ������ �ʽ��ϴ�.");

			if (bLogPrint) {
				JLog.debug("#######################################################");
				JLog.debug(" ==> �ε����� �̸��� �� �����Ǿ����ϴ�!");
				JLog.debug("#######################################################");
			}
		} catch (Exception e) {
			//e.printStackTrace();

			if (bLogPrint) {
				JLog.debug("#######################################################");
				JLog.debug("==> �ε����� �̸� ������ �߸� �Ǿ����ϴ�!! " + e.getMessage());
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
