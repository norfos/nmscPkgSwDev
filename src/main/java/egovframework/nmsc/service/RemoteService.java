package egovframework.nmsc.service;

import java.util.List;
import java.util.Map;

public interface RemoteService {

	
	public void createTbItemList() throws Exception;

	public void createTbItemDownList() throws Exception;
	
	public void createTbItemThreadCnt() throws Exception;
	
	public void insertTbItemThreadCnt() throws Exception;
	
	public void createTbItemAreaGroup() throws Exception;
	
	public void insertTbItemAreaGroup() throws Exception;
	
	public int reqItemList() throws Exception;
	
	public List<Map<String, Object>> selItemList() throws Exception;
	
	public List<Map<String, Object>> createCheckList(List<Map<String, Object>> itemList, String timeAdd) throws Exception;
	
	public int insertCheckList(List<Map<String, Object>> dataMapList) throws Exception;
	
	public boolean selectCheckCnt() throws Exception;
	
	public List<Map<String, Object>> selectCheckList() throws Exception;
	
	public List<Map<String, Object>> reqRemoteDownload(List<Map<String, Object>> list) throws Exception;
	
	public int updateDownDb(List<Map<String, Object>> dataMapList) throws Exception;
	
	public int deleteOldDbList() throws Exception;
	
}
