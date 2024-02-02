package egovframework.nmsc.init;

import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import egovframework.nmsc.service.RemoteService;
import egovframework.nmsc.service.impl.RemoteServiceImpl;

@WebListener
public class NmscServletContextListener {
	private static final Logger log = LoggerFactory.getLogger(NmscServletContextListener.class);
//	@Resource(name = "remoteService")
//	private RemoteService remoteService;

	public void init() {
        // 웹 애플리케이션 시작 시 실행될 코드 작성
        log.info("웹 애플리케이션 시작 - 초기화 메소드 실행!");
        try {
        	RemoteService remoteService = new RemoteServiceImpl();
        	remoteService.createTbItemList();
        	remoteService.createTbItemDownList();
        	remoteService.createTbItemThreadCnt();
        	remoteService.insertTbItemThreadCnt();
			remoteService.reqItemList();
			remoteService.createTbItemAreaGroup();
			remoteService.insertTbItemAreaGroup();
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
    }
	

    
}
