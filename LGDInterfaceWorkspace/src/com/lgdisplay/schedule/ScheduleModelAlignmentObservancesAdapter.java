package com.lgdisplay.schedule;

import jspeed.base.jdbc.BaseSQLException;
import jspeed.base.log.LogLevel;
import jspeed.base.log.LogService;
import jspeed.base.log.Logger;

import com.itplus.mm.server.schedule.ScheduleModel;
import com.lgdisplay.check.LGDModelAlignmentObservanceCheck;

public class ScheduleModelAlignmentObservancesAdapter implements com.itplus.mm.server.schedule.IMMJobNode{
	Logger log = LogService.getInstance().getLogServiceContext().getLogger("out");
	long recCnt;

	public ScheduleModelAlignmentObservancesAdapter() {
		recCnt = -1L;
	}
	
	public long getCheckCount() {
		return recCnt;
	}
	
	public void interrupt() {
		
	}

	public void run(ScheduleModel model) throws Exception {
		
		log.println(LogLevel.DEBUG, "@@@@@@@@@@@@@@@@@ ScheduleModelAlignmentObservancesAdapter.java.run start.. ScheduleModel ==>" + model + "<==");

		try {
			
			try {
				recCnt = 0;
				LGDModelAlignmentObservanceCheck modelAlignmentObservanceCheck = new LGDModelAlignmentObservanceCheck();
				int ret = modelAlignmentObservanceCheck.ObservanceCheck();
				recCnt = ret;
			} catch (BaseSQLException e) {
				e.printStackTrace();
			}
			
			log.println("@@@@@@@@@@@@@@@@@ ScheduleModelAlignmentObservancesAdapter end..  record count ==>" + recCnt + "<==");
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}

}
