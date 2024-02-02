package egovframework.nmsc.service;

import java.util.List;
import java.util.Map;

public interface AdminService {
	
	public int updateItemDelYn(List<Map<String, Object>> dataMapList) throws Exception;
	
	public int selectThreadCnt();
	
	public List<Map<String, Object>> selectItemAreaGroup();
	
	public int updateThreadCnt(String cnt) throws Exception;
	
	public int updateAreaGroup(List<Map<String, Object>> dataMapList) throws Exception;
	
	 
}
