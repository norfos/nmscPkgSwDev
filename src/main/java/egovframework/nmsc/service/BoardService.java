package egovframework.nmsc.service;

import java.util.List;
import java.util.Map;

public interface BoardService {

	/**
	 * 데이터 리스트 조회
	 * @Author : norfos
	 * @Date : 23.10.26
	 * @Method : selectNoticeList
	 * @return : List
	 * @throws Exception 
	 */
	public List<Map<String, Object>> selectDateList(Map<String, Object> boardInfo) ;
	
	public List<Map<String, Object>> selectDateGroup();

	
	
	
}
