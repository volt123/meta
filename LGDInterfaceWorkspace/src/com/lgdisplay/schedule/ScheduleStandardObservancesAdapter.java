package com.lgdisplay.schedule;

import java.util.HashMap;
import java.util.HashSet;

import jspeed.base.jdbc.CacheResultSet;
import jspeed.base.log.LogLevel;
import jspeed.base.log.LogService;
import jspeed.base.log.Logger;

import com.itplus.mm.common.util.DateUtil;
import com.itplus.mm.common.util.SysHandler;
import com.itplus.mm.dao.structure.TdatstrcelemmapDAO;
import com.itplus.mm.server.schedule.ScheduleModel;
import com.lgdisplay.db.StandardObservanceCheckDAO;

public class ScheduleStandardObservancesAdapter implements com.itplus.mm.server.schedule.IMMJobNode{
	Logger log = LogService.getInstance().getLogServiceContext().getLogger("out");
	long errCnt;

	public ScheduleStandardObservancesAdapter() {
		errCnt = -1L;
	}
	
	public long getCheckCount() {
		return errCnt;
	}
	
	public void interrupt() {
		
	}

	public void run(ScheduleModel model) throws Exception {
		
		log.println(LogLevel.DEBUG, "@@@@@@@@@@@@@@@@@ ScheduleStandardObservancesAdapter.run start.. ScheduleModel ==>" + model + "<==");
		StandardObservanceCheckDAO dao = new StandardObservanceCheckDAO();
		TdatstrcelemmapDAO datstrcmapDAO = new TdatstrcelemmapDAO(dao.getDBAssistant());
		
		try
		{
			dao.begin();
			
			// 관리모델, 수집모델 중 어느 모델을 기준으로 준수도 체크 Job을 수행할지를 결정함
			HashMap param = new HashMap();
			String modelType = SysHandler.getInstance().getProperty("OBS_CHECK_ALL_MODEL");
			
			if ("M".equals(modelType)){
				param.put("AUTO_COLLECT_YN", "N");
				param.put("MODEL_TYPE","P");
				param.put("DAT_STRC_ID","");
			}else{
				param.put("AUTO_COLLECT_YN", "Y");
				param.put("MODEL_TYPE","P");
				param.put("DAT_STRC_ID","");
			}
			CacheResultSet modelRs = datstrcmapDAO.findModelInfoAndDatStrcByModelType(param);	//모델 목록(모델정보 + 매핑된 데이터구조 정보)
			
			HashMap in = new HashMap();
			in.put("START_DT", DateUtil.getToday());
			in.put("MODEL_TYPE","P");
			in.put("ONLY_DIFF", "Y");
			in.put("T_MD_ATTR_TYPE","P");//모델속성타입(L or P or PL)
			in.put("T_AUT_CLT_YN","Y");//자동수집여부( Y or N )
			in.put("AUTO_COLLECT_YN", param.get("AUTO_COLLECT_YN"));
			
			errCnt = 0;
			HashSet unique = new HashSet();
			while (modelRs.next()){
				if(!unique.contains(modelRs.getString("DAT_STRC_ID")))
				{
					in.put("DAT_STRC_ID", modelRs.getString("DAT_STRC_ID"));
					errCnt += dao.insertObservanceCheckCountByDatStrc(in);
					unique.add(modelRs.getString("DAT_STRC_ID"));
				}
			}
			
			dao.commit(); 
		}
		catch(Exception e)
		{
			dao.rollback();
			e.printStackTrace();
			throw e;
		}
		finally
		{
			dao.close();
		}
		
	}

}
