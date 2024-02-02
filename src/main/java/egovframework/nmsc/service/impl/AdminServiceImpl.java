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

import egovframework.nmsc.service.AdminService;
import egovframework.nmsc.sqliteDb.SQLiteManager;

@Service("adminService")
public class AdminServiceImpl extends SQLiteManager implements AdminService {
	private static final Logger log = LoggerFactory.getLogger(AdminServiceImpl.class);
//  * 100건마다 적재 시도
	private static final int OPT_BATCH_SIZE = 100;
	
	@Override
	public int updateItemDelYn(List<Map<String, Object>> dataMapList) throws Exception{
		
		final String sql = "UPDATE TB_ITEM_DOWN_LIST				"+"\n"
				+ " SET DEL_YN = 'Y'	"+"\n"
				+ " WHERE MNT_SEQ = ?								"+"\n"
				;

	     // 변수설정
	     //   - Database 변수
	     Connection conn = createConnection();
	     PreparedStatement pstmt = null;
	
	     //   - 입력 결과 변수
	     int updateed = 0;
	
	     try {
	         // PreparedStatement 생성
	         pstmt = conn.prepareStatement(sql);
	         
	         for(int i = 0; i < dataMapList.size(); i++ ) {
	        	// 입력 데이터 객체
	        	Map<String, Object> dataMap = dataMapList.get(i);
		        // 입력 데이터 매핑
		        pstmt.setObject(1, dataMap.get("mntSeq"));
		         
		        // Batch에 추가
				pstmt.addBatch();
				
				// Batch 실행
				if( i % OPT_BATCH_SIZE == 0 ) {
					updateed += pstmt.executeBatch().length;
				}
	         }
	
	         // 입력 건수 조회
	         updateed += pstmt.executeBatch().length;
 
	         // 트랜잭션 COMMIT
	         conn.commit();
	
	     } catch (SQLException e) {
	         // 오류출력
	         System.out.println(e.getMessage());
	         
	         // 트랜잭션 ROLLBACK
	         if( conn != null ) {
	             conn.rollback();
	         }
	         
	         // 오류
	         updateed = -1;
	
	     } finally {
	    	 closeStatement(pstmt);
	        	closeConnection(conn);
	     }
	
	     // 결과 반환
	     //   - 입력된 데이터 건수
	     return updateed;
	}
	
	@Override
	public int selectThreadCnt(){
	//   - SQL
		final String SQL = "SELECT THREAD_CNT FROM TB_ITEM_THREAD_CNT WHERE THREAD_GUBUN = 'TR'";
        
        // 변수설정
        //   - Database 변수
        Connection conn = createConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        // ResultSet -> List<Map> 객체
        int result = 0;
        
        try {
            // PreparedStatement 객체 생성
            pstmt = conn.prepareStatement(SQL);
            
            // 데이터 조회
            rs = pstmt.executeQuery();
            if(rs.next()) {
            	result = Integer.parseInt(String.valueOf(rs.getObject("THREAD_CNT")));
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
        return result;
		
	}
	
	@Override
	public List<Map<String, Object>> selectItemAreaGroup() {
	//   - SQL
		final String SQL = "SELECT DATA_AREA, DOWN_YN FROM TB_ITEM_AREA_GROUP";
        
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
	
	
	@Override
	public int updateThreadCnt(String cnt) throws Exception{
		
		final String sql = "UPDATE TB_ITEM_THREAD_CNT				"+"\n"
				+ " SET THREAD_CNT = ?	"+"\n"
				+ " WHERE THREAD_GUBUN = 'TR'								"+"\n"
				;

	     // 변수설정
	     //   - Database 변수ㄷ
	     Connection conn = createConnection();
	     PreparedStatement pstmt = null;
	
	     //   - 입력 결과 변수
	     int updateed = 0;
	
	     try {
	         // PreparedStatement 생성
	         pstmt = conn.prepareStatement(sql);
	         
	         // 입력 데이터 매핑
	         pstmt.setObject(1, cnt);
		        
	         // 입력 건수 조회
	         updateed = pstmt.executeUpdate();
 
	         // 트랜잭션 COMMIT
	         conn.commit();
	
	     } catch (SQLException e) {
	         // 오류출력
	         System.out.println(e.getMessage());
	         
	         // 트랜잭션 ROLLBACK
	         if( conn != null ) {
	             conn.rollback();
	         }
	         
	         // 오류
	         updateed = -1;
	
	     } finally {
	    	 closeStatement(pstmt);
	        	closeConnection(conn);
	     }
	
	     // 결과 반환
	     //   - 입력된 데이터 건수
	     return updateed;
	}
	
	@Override
	public int updateAreaGroup(List<Map<String, Object>> dataMapList) throws Exception{
		
		final String sql = "UPDATE TB_ITEM_AREA_GROUP		"+"\n"
				+ " SET DOWN_YN = ?				"+"\n"
				+ " WHERE DATA_AREA = ?				"+"\n"
				;

	     // 변수설정
	     //   - Database 변수
	     Connection conn = createConnection();
	     PreparedStatement pstmt = null;
	
	     //   - 입력 결과 변수
	     int updateed = 0;
	
	     try {
	         // PreparedStatement 생성
	         pstmt = conn.prepareStatement(sql);
	         
	         for(int i = 0; i < dataMapList.size(); i++ ) {
	        	// 입력 데이터 객체
	        	Map<String, Object> dataMap = dataMapList.get(i);
		        // 입력 데이터 매핑
		        pstmt.setObject(1, dataMap.get("chk"));
		        pstmt.setObject(2, dataMap.get("group"));
		         
		        // Batch에 추가
				pstmt.addBatch();
				
				// Batch 실행
				if( i % OPT_BATCH_SIZE == 0 ) {
					updateed += pstmt.executeBatch().length;
				}
	         }
	
	         // 입력 건수 조회
	         updateed += pstmt.executeBatch().length;
 
	         // 트랜잭션 COMMIT
	         conn.commit();
	
	     } catch (SQLException e) {
	         // 오류출력
	         System.out.println(e.getMessage());
	         
	         // 트랜잭션 ROLLBACK
	         if( conn != null ) {
	             conn.rollback();
	         }
	         
	         // 오류
	         updateed = -1;
	
	     } finally {
	    	 closeStatement(pstmt);
	        	closeConnection(conn);
	     }
	
	     // 결과 반환
	     //   - 입력된 데이터 건수
	     return updateed;
	}
	
}
