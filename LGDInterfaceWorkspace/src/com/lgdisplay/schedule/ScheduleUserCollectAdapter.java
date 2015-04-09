package com.lgdisplay.schedule;

import jspeed.base.jdbc.BaseSQLException;
import jspeed.base.log.LogLevel;
import jspeed.base.log.LogService;
import jspeed.base.log.Logger;

import com.itplus.mm.server.schedule.ScheduleModel;
import com.lgdisplay.user.UserCollector;

public class ScheduleUserCollectAdapter implements com.itplus.mm.server.schedule.IMMJobNode {
	Logger log = LogService.getInstance().getLogServiceContext().getLogger("out");
	long recCnt;
	public ScheduleUserCollectAdapter() {
		recCnt = -1L;
	}
	public long getCheckCount() {
		return recCnt;
	}

	public void interrupt() {
		
	}

	public void run(ScheduleModel arg0) throws Exception {
		log.println(LogLevel.DEBUG,"@@@@@@@@@@@@@@@@@ ScheduleUserCollectAdapter start.. ScheduleModel ==>" + arg0 + "<==");
		try {
			
			try {
				recCnt = 0;
				UserCollector userCollector = new UserCollector();
				int ret = userCollector.collectUser();
				recCnt = ret;
			} catch (BaseSQLException e) {
				e.printStackTrace();
			}
			
			log.println("@@@@@@@@@@@@@@@@@ ScheduleUserCollectAdapter end..  record count ==>" + recCnt + "<==");
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
