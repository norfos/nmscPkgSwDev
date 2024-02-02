package egovframework.nmsc.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import egovframework.nmsc.service.RemoteService;

@Controller
@Component("remoteController")
public class RemoteController {
	private static final Logger log = LoggerFactory.getLogger(RemoteController.class);

	@Resource(name = "propertiesService")
	protected EgovPropertyService propertiesService;

	@Resource(name = "remoteService")
	private RemoteService remoteService;
	
	
	public void reqRemoteItemList() throws Exception {
		try {
			remoteService.reqItemList();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	
	public void createCheckList() throws Exception {
		//아이템리스트 가져오기
		List<Map<String, Object>> itemList = remoteService.selItemList();
		
		//주기(분)별 체크리스트 만들기
		List<Map<String, Object>> checkList = remoteService.createCheckList(itemList, "HOUR");
		
		//메모리 sqllist 등록(1시간후 TODO리스트)
		int inserted = remoteService.insertCheckList(checkList);
		if( inserted >= 0 ) {
            log.info(String.format("TB_ITEM_DOWN_LIST 데이터 입력 성공: %d건", inserted));
        } else {
            log.info("데이터 입력 실패");
        }
	}
	
	public void remoteDownload() throws Exception {
		
		boolean existYn = remoteService.selectCheckCnt();
		if(!existYn) {
			//아이템리스트 가져오기
			List<Map<String, Object>> itemList = remoteService.selItemList();
			
			//주기(분)별 체크리스트 만들기
			List<Map<String, Object>> checkList = remoteService.createCheckList(itemList, "");
			
			//메모리 sqllist 등록(1시간후 TODO리스트)
			remoteService.insertCheckList(checkList);
		}
		
		//1. 체크리스트 조회(UTC기준 현재 년월일시분으로 검색)
		List<Map<String, Object>> itemList = remoteService.selectCheckList();
		
		if(!itemList.isEmpty()) {
			//2. 이미지 다운로드 요청
			List<Map<String, Object>> monitorChkList = remoteService.reqRemoteDownload(itemList);
			if(!monitorChkList.isEmpty()) {
				//3. 다운로드결과 체크리스트 업데이트
				int updateed = remoteService.updateDownDb(monitorChkList);
				if( updateed >= 0 ) {
					log.info(String.format("TB_ITEM_DOWN_LIST 데이터 업데이트 성공: %d건", updateed));
				} else {
					log.info("데이터 업데이트 실패");
				}
			}
		}
	}
	
	public void deleteOldDbList() throws Exception {
		//* 체크리스트 삭제(24시간경과건)
		remoteService.deleteOldDbList();
	}
	


}
