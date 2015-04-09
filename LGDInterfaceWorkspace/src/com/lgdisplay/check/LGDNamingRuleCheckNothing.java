package com.lgdisplay.check;

import java.rmi.Naming;
import java.util.HashMap;

import com.lgdisplay.util.JLog;

public class LGDNamingRuleCheckNothing extends com.itplus.mm.server.structure.AbstractNamingRule {

	private boolean bLogPrint = true;
	
	public LGDNamingRuleCheckNothing() throws Exception {
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
	 * LGDisplay Legacy Area is not a compliance area of Data Standards.
	 * So Any Tables are not checked. (Oracle)
	 * </pre>
	 * @param HashMap
	 *        param.get("DAT_VAL") - Table Name, 
	 *        param.get("DAT_SYS_ID") - Business System ID
	 * @see
	 *        Naming Rule Formation - Nothing
	 * @return boolean
	 * @throws Exception 
	 */
	public boolean table(HashMap param) throws Exception{

		if (bLogPrint) {
			JLog.debug("#######################################################");
			JLog.debug(":::::::       Check LGDisplay Legacy Table Naming Rule       :::::::");
			JLog.debug("param Info: " + param.toString());
		}
		
		String DAT_SYS_ID = (String)param.get("DAT_SYS_ID");
		String TargetTableName = (String)param.get("DAT_VAL");

		if (bLogPrint) {
			JLog.debug("Target TableName: " + TargetTableName);
			JLog.debug("Business System ID: " + DAT_SYS_ID);
		}
		
		try {
			if ( null == TargetTableName ) 
				throw new Exception("Not exist Table Name.");
			
			if (bLogPrint) {
				JLog.debug("#######################################################");
				JLog.debug(" ==> Validation Passed!");
				JLog.debug("#######################################################");
			}
		} catch (Exception e) {

			if (bLogPrint) {
				JLog.debug("#######################################################");
				JLog.debug("==> Validation Failed!! " + e.getMessage());
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
	 * @return boolean
	 * @throws Exception
	 */
	public boolean primarykey(HashMap param) throws Exception {
		return true;
	}
	
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
	public boolean foreignkey(HashMap param) throws Exception {
		return true;
	}
	
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
	 * @return 
	 * @throws Exception
	 */
	public boolean index(HashMap param) throws Exception {
		return true;
	}
	
	
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
