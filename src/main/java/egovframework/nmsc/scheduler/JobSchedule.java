package egovframework.nmsc.scheduler;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import egovframework.nmsc.controller.RemoteController;

@Component("jobSchedule")
public class JobSchedule {
	private static final Logger log = LoggerFactory.getLogger(JobSchedule.class);
	
	@Resource(name = "remoteController")
	private RemoteController remoteController;


	// remote 아이템 리스트 호출 ( 하루1번 호출 )
	@Scheduled(cron = "0 0 8 * * ?")	//매일 8시마다 실행
//	@Scheduled(cron = "0 * * * * *")
	public void reqRemoteItemList() {
		log.info("remote 아이템 리스트 호출");
		try {
			remoteController.reqRemoteItemList();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	
	//체크리스트 생성(매시 50분 실행)
	@Scheduled(cron = "0 50 * * * ?")	//매시 50분마다 호출
	public void createCheckList () {
		log.info("체크리스트 생성");
		try {
			remoteController.createCheckList();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	
	//리모트 다운로드 실행(1분마다 실행)
	@Scheduled(cron = "0 * * * * *")
	public void remoteDownload () {
		log.info("리모트 다운로드 실행(1분마다 실행)");
		try {
			remoteController.remoteDownload();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	
	//24시간경과 체크리스트 삭제
	//하루4번 03:30 09:30 15:30 21:30 실행
	@Scheduled(cron = "0 30 3,9,15,21 * * ?")	
	public void deleteOldDbList () {
		log.info("24시간경과 체크리스트 삭제");
		try {
			remoteController.deleteOldDbList();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	

	
	
}
