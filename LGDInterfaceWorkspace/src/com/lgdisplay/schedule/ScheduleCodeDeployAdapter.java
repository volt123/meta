package com.lgdisplay.schedule;

import jspeed.base.jdbc.BaseSQLException;
import jspeed.base.log.LogLevel;
import jspeed.base.log.LogService;
import jspeed.base.log.Logger;

import com.itplus.mm.server.schedule.ScheduleModel;
import com.lgdisplay.code.LGDCodeDeployer;

public class ScheduleCodeDeployAdapter implements com.itplus.mm.server.schedule.IMMJobNode {
	Logger log = LogService.getInstance().getLogServiceContext().getLogger("out");
	long errCnt;
	public ScheduleCodeDeployAdapter() {
		errCnt = -1L;
	}
	public long getCheckCount() {
		return errCnt;
	}

	public void interrupt() {
		
	}

	public void run(ScheduleModel arg0) throws Exception {
		log.println(LogLevel.DEBUG,"@@@@@@@@@@@@@@@@@ ScheduleCodeDeployAdapter start.. ScheduleModel ==>" + arg0 + "<==");
		try {
			
			try {
				errCnt = 0;
				LGDCodeDeployer codeDeployer = new LGDCodeDeployer();
				int ret = codeDeployer.deployCodeAll();
				errCnt = ret;
			} catch (BaseSQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out
			.println("@@@@@@@@@@@@@@@@@ ScheduleCodeDeployAdapter end..  record count ==>"
					+ errCnt + "<==");
			
		} catch (Exception e) {
			throw e;
		}
	}
}
