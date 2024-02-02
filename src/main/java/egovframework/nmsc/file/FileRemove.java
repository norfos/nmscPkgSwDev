package egovframework.nmsc.file;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FileRemove {
	private static final Logger log = LoggerFactory.getLogger(FileRemove.class);

	public void fileRemove(List<Map<String, Object>> fileInfos) {
		try {
			
			for(int i = 0; i< fileInfos.size(); i++) {
				String sysFileNm = (String)fileInfos.get(i).get("sysFileName");  
				String sysFilePath = (String)fileInfos.get(i).get("sysFilePath");
				File file = new File(sysFilePath+sysFileNm);
				
				if(file.exists()) {					// 파일에 접근 할 수 있으면
					if(!file.delete()) {
						log.debug("file Remove Fail");
					}else {
						log.debug("file Remove Success");
					}
				}
				
			}
		} catch (Exception e) {
			log.error(e.toString());
		}
		
	}
	
	public void boardFileRemove(List<Map<String, Object>> fileInfos) {
		try {
			for(int i = 0; i< fileInfos.size(); i++) {
				String sysFileNm = (String)fileInfos.get(i).get("sysFileName");  
				String sysFilePath = (String)fileInfos.get(i).get("sysFilePath");
				
				String[] fileNmSplit = sysFileNm.split("\\.");
				String thumbFileNm = fileNmSplit[0]+"_0.png";
				
				// 업로드 파일
				File file = new File(sysFilePath+"\\"+sysFileNm);
				// 썸네일 파일
				File thumbFile = new File(sysFilePath+"\\"+thumbFileNm);
				
				if(file.exists()) {					// 파일에 접근 할 수 있으면
					if(!file.delete()) {
						log.info("file Remove Fail");
					}else {
						log.info("file Remove Success");
					}
				}
				if(thumbFile.exists()) {					// 파일에 접근 할 수 있으면
					if(!thumbFile.delete()) {
						log.info("file Remove Fail");
					}else {
						log.info("file Remove Success");
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
	}
}
