package egovframework.nmsc.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import egovframework.nmsc.service.BoardService;
import egovframework.nmsc.sqliteDb.SQLiteManager;

@Service("boardService")
public class BoardServiceImpl extends SQLiteManager implements BoardService {
	private static final Logger log = LoggerFactory.getLogger(BoardServiceImpl.class);
//	private static String localFile = "C:/nmscPkgData/list/nmscPkgList.csv"; // 임시 리모트파일 경로
//	private static String localFile = "/file/nmscPkgData/list/nmscPkgList.csv"; // 임시 리모트파일 경로
	
	@Override
	public List<Map<String, Object>> selectDateList(Map<String, Object> paramMap) {
	//   - SQL
		//currentPage
		//{dataArea: '', sdate: '2023-12-31 00:00:00', edate: '2024-01-31 23:59:59', currentPage: 1, pagePerRow: '10'}
				
        final String SQL = "   SELECT       								"+"\n"
        					+ "   	COUNT(*) OVER() AS TOTAL,              "+"\n"
        					+ "   	MNT_SEQ,              "+"\n"
					    	+ "   	SATELLITE,              "+"\n"
					    	+ "   	SENSOR,                 "+"\n"
					    	+ "   	DATA_LVL,               "+"\n"
					    	+ "   	DATA_TYPE,              "+"\n"
					    	+ "   	DATA_FORMAT,            "+"\n"
					    	+ "   	DATA_AREA,              "+"\n"
					    	+ "   	DATA_RES,               "+"\n"
					    	+ "   	DATA_PROJ,              "+"\n"
					    	+ "   	REPLACE(FILE_PATH,'/vol01/DATA/','') AS FILE_PATH,              "+"\n"
					    	+ "   	FILE_PTN,                "+"\n"
					    	+ "   	DOWN_YN,                "+"\n"
					    	+ "   	DOWN_SIZE,                "+"\n"
					    	+ "   	SUBSTR(MNT_YMD,1,4)||'-'||SUBSTR(MNT_YMD,5,2)||'-'||SUBSTR(MNT_YMD,7,2)||' '||MNT_HOUR||':'||MNT_MIN AS MNT_TIME                "+"\n"
				    	+ "   	FROM TB_ITEM_DOWN_LIST           "+"\n"
				    	+ "   	WHERE DOWN_YN = 'Y'           "+"\n"
				    	+ "   	AND DEL_YN = 'N'           "+"\n"
				    	+ "   	AND SUBSTR(MNT_YMD,1,4)||'-'||SUBSTR(MNT_YMD,5,2)||'-'||SUBSTR(MNT_YMD,7,2)||' '||MNT_HOUR||':'||MNT_MIN BETWEEN ? AND ?	 "+"\n"
				    	+ "   	ORDER BY MNT_YMD||MNT_HOUR||MNT_IMIN DESC           "+"\n"
				    	+ "		LIMIT ? OFFSET (?-1) * ?           "+"\n"
				    	;
        
        //   - 조회 결과 변수
        final Set<String> columnNames = new HashSet<String>();        
        final List<Map<String, Object>> selected = new ArrayList<Map<String, Object>>();
 
        // 변수설정
        //   - Database 변수
        Connection conn = createConnection();
        PreparedStatement pstmt = null;
        ResultSetMetaData meta = null;
        ResultSet rs = null;
        try {
            // PreparedStatement 객체 생성
            pstmt = conn.prepareStatement(SQL);
            
            // 조회 데이터 조건 매핑
            pstmt.setObject(1, paramMap.get("sdate"));
            pstmt.setObject(2, paramMap.get("edate"));
            pstmt.setObject(3, paramMap.get("pagePerRow"));
            pstmt.setObject(4, paramMap.get("currentPage"));
            pstmt.setObject(5, paramMap.get("pagePerRow"));
            
            // 데이터 조회
            rs = pstmt.executeQuery();
            
            // 조회된 데이터의 컬럼명 저장
            meta = pstmt.getMetaData();
            for(int i=1; i<=meta.getColumnCount(); i++) {
                columnNames.add(meta.getColumnName(i));
            }
            
            // ResultSet -> List<Map> 객체
            Map<String, Object> resultMap = null;
            
            while(rs.next()) {
                resultMap = new HashMap<String, Object>();
                
                for(String column : columnNames) {
                    resultMap.put(column, rs.getObject(column));
                }
                
                if( resultMap != null ) {
                    selected.add(resultMap);
                }
            }
            
        } catch (SQLException e) {
            // 오류처리
        	log.error(e.getMessage());
            
        } finally  {
        	closeResultSet(rs);
        	closeStatement(pstmt);
        	closeConnection(conn);
        }
 
        // 결과 반환
        //   - 조회된 데이터 리스트
        return selected;
		
	}
	
	@Override
	public List<Map<String, Object>> selectDateGroup() {
	//   - SQL
		final String SQL = "SELECT DATA_AREA FROM TB_ITEM_LIST GROUP BY DATA_AREA";
        
        //   - 조회 결과 변수
        final Set<String> columnNames = new HashSet<String>();        
        final List<Map<String, Object>> selected = new ArrayList<Map<String, Object>>();
 
        // 변수설정
        //   - Database 변수
        Connection conn = createConnection();
        PreparedStatement pstmt = null;
        ResultSetMetaData meta = null;
        ResultSet rs = null;
        try {
            // PreparedStatement 객체 생성
            pstmt = conn.prepareStatement(SQL);
            
            
            // 데이터 조회
            rs = pstmt.executeQuery();
            
            // 조회된 데이터의 컬럼명 저장
            meta = pstmt.getMetaData();
            for(int i=1; i<=meta.getColumnCount(); i++) {
                columnNames.add(meta.getColumnName(i));
            }
            
            // ResultSet -> List<Map> 객체
            Map<String, Object> resultMap = null;
            
            while(rs.next()) {
                resultMap = new HashMap<String, Object>();
                
                for(String column : columnNames) {
                    resultMap.put(column, rs.getObject(column));
                }
                
                if( resultMap != null ) {
                    selected.add(resultMap);
                }
            }
            
        } catch (SQLException e) {
            // 오류처리
            System.out.println(e.getMessage());
            
        } finally  {
        	closeResultSet(rs);
        	closeStatement(pstmt);
        	closeConnection(conn);
        }
 
        // 결과 반환
        //   - 조회된 데이터 리스트
        return selected;
		
	}

	
}


