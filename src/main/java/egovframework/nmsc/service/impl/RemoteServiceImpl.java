package egovframework.nmsc.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import egovframework.nmsc.remote.util.CommonFileUtils;
import egovframework.nmsc.service.AdminService;
import egovframework.nmsc.service.RemoteService;
import egovframework.nmsc.sqliteDb.DDLService;
import egovframework.nmsc.sqliteDb.DDLService.ResultType;
import egovframework.nmsc.sqliteDb.SQLiteManager;

@Service("remoteService")
public class RemoteServiceImpl extends SQLiteManager implements RemoteService {
	private static final Logger log = LoggerFactory.getLogger(RemoteServiceImpl.class);

	@Resource(name = "propertiesService")
	protected EgovPropertyService propertiesService;
	
	@Resource(name = "adminService")
	protected AdminService adminService;

	private static String remoteImageDownUrl = "https://nmsc.kma.go.kr"; // 이미지 요청URL
//	private static String remoteItemListUrl = "http://ecoflagdev.iptime.org:51120/homepage/json/intrstApi.do"; // 
	private static String remoteItemListUrl = "https://nmsc.kma.go.kr/homepage/json/intrstApi.do"; // 
	private static String localFilePath = "/nmscFile"; // 로컬 파일 경로
//	private static String localFilePath = "C:/nmscFile"; // 로컬 파일 경로
	
	
	private DDLService DDL = new DDLService();
	//     * 100건마다 적재 시도
	private static final int OPT_BATCH_SIZE = 100;
	
	@Override
	public void createTbItemList() throws Exception{
		final String SQL = "CREATE TABLE TB_ITEM_LIST (	                 "+"\n"
				+ "    SATELLITE VARCHAR2(100),                          "+"\n"
				+ "    SENSOR VARCHAR2(100),                             "+"\n"
				+ "    DATA_LVL VARCHAR2(1024),                          "+"\n"
				+ "    DATA_TYPE VARCHAR2(1024),                         "+"\n"
				+ "    DATA_FORMAT VARCHAR2(100),                        "+"\n"
				+ "    DATA_AREA VARCHAR2(100),                          "+"\n"
				+ "    DATA_RES VARCHAR2(100),                           "+"\n"
				+ "    DATA_PROJ VARCHAR2(100),                          "+"\n"
				+ "    FILE_PATH VARCHAR2(1024) NOT NULL,         "+"\n"
				+ "    FILE_PTN VARCHAR2(256) NOT NULL,            "+"\n"
				+ "    PRODUCT_CYCLE INTEGER,						"+"\n"
				+ "    PRODUCT_STD_TIME INTEGER,					"+"\n"
				+ "    PRIMARY KEY (FILE_PATH,FILE_PTN)                  "+"\n"
				+ "    )";
				  
		Connection conn = createConnection();
		// 테이블 생성
		ResultType result = DDL.createTable("TB_ITEM_LIST", SQL, conn);

		// 테이블 생성 결과 출력
		switch( result ) {
        	case SUCCESS:
        		System.out.println("TB_ITEM_LIST 테이블 생성 완료.");
        		break;
        	case WARNING:
        		System.out.println("TB_ITEM_LIST 테이블이 이미 존재합니다.");
        		break;
        	case FAILURE:
        		System.out.println("TB_ITEM_LIST 테이블 생성 실패.");
        		break;
		}

		// DB 연결 종료
		DDL.closeConnection(conn);
	}
	
	@Override
	public void createTbItemDownList() throws Exception{
		final String SQL = "CREATE TABLE TB_ITEM_DOWN_LIST (    "+"\n"   
				  + "    MNT_SEQ     	INTEGER PRIMARY KEY AUTOINCREMENT,          	"+"\n"
				  + "    MNT_CYCLE      INTEGER,          	"+"\n"
				  + "    MNT_YMD     	VARCHAR2(8) ,          	"+"\n"
				  + "    MNT_HOUR     	VARCHAR2(2) ,           "+"\n"
				  + "    MNT_MIN     	VARCHAR2(2) ,          	"+"\n"
				  + "    MNT_IMIN     	VARCHAR2(2) ,          	"+"\n"
				  + "    RCT_YN	    VARCHAR2(1) ,               "+"\n"
				  + "    RCT_CD        VARCHAR2(1) ,            "+"\n"
				  + "    SATELLITE     	VARCHAR2(100) ,         "+"\n"
				  + "    SENSOR     	VARCHAR2(100) ,         "+"\n"
				  + "    DATA_LVL     	VARCHAR2(1024) ,        "+"\n"
				  + "    DATA_TYPE     	VARCHAR2(1024) ,        "+"\n"
				  + "    DATA_FORMAT    VARCHAR2(100) ,         "+"\n"
				  + "    DATA_AREA     	VARCHAR2(100) ,         "+"\n"
				  + "    DATA_RES     	VARCHAR2(100) ,         "+"\n"
				  + "    DATA_PROJ     	VARCHAR2(100) ,         "+"\n"
				  + "    FILE_PATH     	VARCHAR2(1024) ,        "+"\n"
				  + "    FILE_PTN		VARCHAR2(256),		    "+"\n"
				  + "    DOWN_YN		VARCHAR2(1),            "+"\n"
				  + "    DOWN_DATE		DATETIME,                "+"\n"
				  + "    DOWN_SIZE		INTEGER,                "+"\n"
				  + "    DEL_YN		VARCHAR2(1)                "+"\n"
				+ "    )";
		
				  
		Connection conn = createConnection();
		// 테이블 생성
		ResultType result = DDL.createTable("TB_ITEM_DOWN_LIST", SQL, conn);

		// 테이블 생성 결과 출력
		switch( result ) {
          	case SUCCESS:
          		System.out.println("TB_ITEM_DOWN_LIST 테이블 생성 완료.");
          		break;
          	case WARNING:
          		System.out.println("TB_ITEM_DOWN_LIST 테이블이 이미 존재합니다.");
          		break;
          	case FAILURE:
          		System.out.println("TB_ITEM_DOWN_LIST 테이블 생성 실패.");
          		break;
		}

		// DB 연결 종료
		DDL.closeConnection(conn);
	}
	
	@Override
	public void createTbItemThreadCnt() throws Exception{
		final String SQL = "CREATE TABLE TB_ITEM_THREAD_CNT (    "+"\n"   
				  + "    THREAD_GUBUN VARCHAR2(2) PRIMARY KEY,          	"+"\n"
				  + "    THREAD_CNT INTEGER          	"+"\n"
				+ "    )";
		
				  
		Connection conn = createConnection();
		// 테이블 생성
		ResultType result = DDL.createTable("TB_ITEM_THREAD_CNT", SQL, conn);

		// 테이블 생성 결과 출력
		switch( result ) {
          	case SUCCESS:
          		System.out.println("TB_ITEM_THREAD_CNT 테이블 생성 완료.");
          		break;
          	case WARNING:
          		System.out.println("TB_ITEM_THREAD_CNT 테이블이 이미 존재합니다.");
          		break;
          	case FAILURE:
          		System.out.println("TB_ITEM_THREAD_CNT 테이블 생성 실패.");
          		break;
		}

		// DB 연결 종료
		DDL.closeConnection(conn);
	}
	
	@Override
	public void insertTbItemThreadCnt() throws Exception{
		
		//아이템 리스트 merge 처리 (VALUSE 형태의 UPSERT)
//		PK 기준으로 해당 데이터가 있으면 insert하지 않고 replace(update)
		final String SQL = "INSERT OR REPLACE INTO TB_ITEM_THREAD_CNT (THREAD_GUBUN, THREAD_CNT) VALUES ('TR', 5)";

		// 변수설정
	    //   - Database 변수
	    Connection conn = createConnection();
	    PreparedStatement pstmt = null;
	
	    try {
	         // PreparedStatement 생성
	         pstmt = conn.prepareStatement(SQL);
	         
	         // 쿼리 실행    
	    	 pstmt.executeUpdate();
				
	    	 // 트랜잭션 COMMIT
	    	 conn.commit();
	
	     } catch (SQLException e) {
	         // 오류출력
	         System.out.println(e.getMessage());
	         
	         // 트랜잭션 ROLLBACK
	         if( conn != null ) {
	             conn.rollback();
	         }
	         
	     } finally {
	    	 closeStatement(pstmt);
	    	 closeConnection(conn);
	     }
	}
	
	@Override
	public int reqItemList() throws Exception{
		//리모트 아이템 리스트 요청(JSON)
		CommonFileUtils commonFileUtils = new CommonFileUtils();
		List<Map<String, Object>> dataMapList = commonFileUtils.httpsJsonToListMap(remoteItemListUrl);
		//아이템 리스트 merge 처리 (VALUSE 형태의 UPSERT)
//		PK 기준으로 해당 데이터가 있으면 insert하지 않고 replace(update)
		final String SQL = "   INSERT INTO TB_ITEM_LIST (SATELLITE, SENSOR, DATA_LVL, DATA_TYPE, DATA_FORMAT, DATA_AREA, DATA_RES, DATA_PROJ, FILE_PATH, FILE_PTN, PRODUCT_CYCLE, PRODUCT_STD_TIME) 	"+"\n"
				+ "   	VALUES(?,	?,	?,	?,	?,	?,	?,	?,	?,	?, ?, ?)	"+"\n"
				+ "   	ON CONFLICT(FILE_PATH,FILE_PTN) "+"\n"
				+ "   	DO UPDATE SET SATELLITE=excluded.SATELLITE, SENSOR=excluded.SENSOR, DATA_LVL=excluded.DATA_LVL, DATA_TYPE=excluded.DATA_TYPE, DATA_FORMAT=excluded.DATA_FORMAT, DATA_AREA=excluded.DATA_AREA, DATA_RES=excluded.DATA_RES, DATA_PROJ=excluded.DATA_PROJ, PRODUCT_CYCLE=excluded.PRODUCT_CYCLE, PRODUCT_STD_TIME=excluded.PRODUCT_STD_TIME	";
		// 변수설정
	    //   - Database 변수
	    Connection conn = createConnection();
	    PreparedStatement pstmt = null;
	
	    //   - 입력 결과 변수
	    int inserted = 0;
	
	    try {
	         // PreparedStatement 생성
	         pstmt = conn.prepareStatement(SQL);
	         
	         for(int i = 0; i < dataMapList.size(); i++ ) {
	        	// 입력 데이터 객체
	        	Map<String, Object> dataMap = dataMapList.get(i);
	
		        // 입력 데이터 매핑
		        pstmt.setObject(1, dataMap.get("satellite"));
		        pstmt.setObject(2, dataMap.get("sensor"));
		        pstmt.setObject(3, dataMap.get("dataLvl"));
		        pstmt.setObject(4, dataMap.get("dataType"));
		        pstmt.setObject(5, dataMap.get("dataFormat"));
		        pstmt.setObject(6, dataMap.get("dataArea"));
		        pstmt.setObject(7, dataMap.get("dataRes"));
		        pstmt.setObject(8, dataMap.get("dataProj"));
		        pstmt.setObject(9, dataMap.get("filePath"));
		        pstmt.setObject(10, dataMap.get("filePtn"));
		        pstmt.setObject(11, checkNull(String.valueOf(dataMap.get("productCycle")),"10"));
		        pstmt.setObject(12, checkNull(String.valueOf(dataMap.get("productStdTime")),"3"));
		        
		         
		        // Batch에 추가
				pstmt.addBatch();
				
				// Batch 실행
				if( i % OPT_BATCH_SIZE == 0 ) {
				    inserted += pstmt.executeBatch().length;
				}
	         }
	
	         // 입력 건수 조회
	         inserted += pstmt.executeBatch().length;

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
	         inserted = -1;
	
	     } finally {
	    	 closeStatement(pstmt);
	        	closeConnection(conn);
	     }
	
	     // 결과 반환
	     //   - 입력된 데이터 건수
	     return inserted;
	}
	
	public static String checkNull(String str, String replaceStr) {
		return (str == null || str.equals("") || str.equals("null")) ? replaceStr : str;
	}
	
	@Override
	public void createTbItemAreaGroup() throws Exception{
		final String SQL = "CREATE TABLE TB_ITEM_AREA_GROUP (    "+"\n"   
				  + "    DATA_AREA VARCHAR2 PRIMARY KEY,          	"+"\n"
				  + "    DOWN_YN VARCHAR2(1)          	"+"\n"
				+ "    )";
		
		Connection conn = createConnection();		  
		// 테이블 생성
		ResultType result = DDL.createTable("TB_ITEM_AREA_GROUP", SQL, conn);

		// 테이블 생성 결과 출력
		switch( result ) {
          	case SUCCESS:
          		System.out.println("TB_ITEM_AREA_GROUP 테이블 생성 완료.");
          		break;
          	case WARNING:
          		System.out.println("TB_ITEM_AREA_GROUP 테이블이 이미 존재합니다.");
          		break;
          	case FAILURE:
          		System.out.println("TB_ITEM_AREA_GROUP 테이블 생성 실패.");
          		break;
		}

		// DB 연결 종료
		DDL.closeConnection(conn);
	}
	
	@Override
	public void insertTbItemAreaGroup() throws Exception{
		
		//아이템 리스트 merge 처리 (VALUSE 형태의 UPSERT)
//		PK 기준으로 해당 데이터가 있으면 insert하지 않고 replace(update)
		final String SQL = "INSERT OR REPLACE INTO TB_ITEM_AREA_GROUP SELECT DATA_AREA, 'Y' FROM TB_ITEM_LIST GROUP BY DATA_AREA";

		// 변수설정
	    //   - Database 변수
	    Connection conn = createConnection();
	    PreparedStatement pstmt = null;
	
	    try {
	         // PreparedStatement 생성
	         pstmt = conn.prepareStatement(SQL);
	         
	         // 쿼리 실행    
	    	 pstmt.executeUpdate();
				
	    	 // 트랜잭션 COMMIT
	    	 conn.commit();
	
	     } catch (SQLException e) {
	         // 오류출력
	         System.out.println(e.getMessage());
	         
	         // 트랜잭션 ROLLBACK
	         if( conn != null ) {
	             conn.rollback();
	         }
	         
	     } finally {
	    	 closeStatement(pstmt);
	        	closeConnection(conn);
	     }
	}
	
	@Override
	public List<Map<String, Object>> selItemList() throws Exception {
		// 상수설정
        //   - SQL
        final String SQL = "   SELECT       				"+"\n"
					    	+ "   	SATELLITE,              "+"\n"
					    	+ "   	SENSOR,                 "+"\n"
					    	+ "   	DATA_LVL,               "+"\n"
					    	+ "   	DATA_TYPE,              "+"\n"
					    	+ "   	DATA_FORMAT,            "+"\n"
					    	+ "   	DATA_AREA,              "+"\n"
					    	+ "   	DATA_RES,               "+"\n"
					    	+ "   	DATA_PROJ,              "+"\n"
					    	+ "   	FILE_PATH,              "+"\n"
					    	+ "   	FILE_PTN,                "+"\n"
					    	+ "   	PRODUCT_CYCLE,                "+"\n"
					    	+ "   	PRODUCT_STD_TIME                "+"\n"
				    	+ "   	FROM TB_ITEM_LIST           "+"\n"
				    	+ "   	WHERE DATA_AREA IN (SELECT DATA_AREA FROM TB_ITEM_AREA_GROUP WHERE DOWN_YN = 'Y')          "+"\n"
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
	public List<Map<String, Object>> createCheckList(List<Map<String, Object>> itemList, String timeAdd) throws Exception{
		
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		try {
			//date setting
			Date nowDate = new Date(); 
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			// Java 시간 더하기
		    Calendar cal = Calendar.getInstance();
		    cal.setTime(nowDate);
		    // 1시간 후
		    if("HOUR".equals(timeAdd)) {
		    	cal.add(Calendar.HOUR, 1);
		    }
		    String now = sdf.format(cal.getTime());
			String yyyy = now.substring(0,4);
			String MM = now.substring(4,6);
			String dd = now.substring(6,8);
			String HH = now.substring(8,10);
			String mm = now.substring(10,12);
			
            for(Map<String, Object> item:itemList){
                //메모리 등록
            	int d = Integer.parseInt(String.valueOf(item.get("PRODUCT_STD_TIME"))); //3
            	int c = Integer.parseInt(String.valueOf(item.get("PRODUCT_CYCLE"))); //2
            	int min = c;
            	int imin = 0;
            	for(int i=d;i<60;i+=c) {	
            		Map<String, Object> map = new HashMap<String, Object>();
            		String FILE_PATH = String.valueOf(item.get("FILE_PATH")).replaceAll("%Y",yyyy).replaceAll("%m",MM).replaceAll("%d",dd).replaceAll("%H",HH).replaceAll("%type",(String) item.get("DATA_TYPE"));
            		String FILE_PTN = String.valueOf(item.get("FILE_PTN")).replaceAll("%Y",yyyy).replaceAll("%m",MM).replaceAll("%d",dd).replaceAll("%H",HH).replaceAll("%M",String.format("%02d", imin));
            		//c 2 / d 3 = s 5 , 7, 9, 11,
            		//SEQ,CYCLE,EXEC_MIN, SATELLITE, SENSOR, DATA_LVL, DATA_TYPE, DATA_FORMAT, DATA_AREA, DATA_RES, DATA_PROJ, FILE_PATH, FILE_PTN
            		map.put("MNT_CYCLE", c);
            		map.put("MNT_YMD", yyyy+MM+dd);
            		map.put("MNT_HOUR", HH);
            		map.put("MNT_MIN", String.format("%02d", i));
            		map.put("MNT_IMIN", String.format("%02d", imin));
            		map.put("SATELLITE", item.get("SATELLITE"));
            		map.put("SENSOR", item.get("SENSOR"));
            		map.put("DATA_LVL", item.get("DATA_LVL"));
            		map.put("DATA_TYPE", item.get("DATA_TYPE"));
            		map.put("DATA_FORMAT", item.get("DATA_FORMAT"));
            		map.put("DATA_AREA", item.get("DATA_AREA"));
            		map.put("DATA_RES", item.get("DATA_RES"));
            		map.put("DATA_PROJ", item.get("DATA_PROJ"));
            		map.put("FILE_PATH", FILE_PATH);
            		map.put("FILE_PTN", FILE_PTN);
            		resultList.add(map);
            		min+=c;
            		imin+=c;
            	}
            }
			
		}catch(Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return resultList;
	}
	
	
	@Override
	public int insertCheckList(List<Map<String, Object>> dataMapList) throws Exception{
		final String sql = "INSERT INTO TB_ITEM_DOWN_LIST ("+"\n"
        		+ "  MNT_CYCLE,      	"+"\n"
        		+ "  MNT_YMD,     	"+"\n"
        		+ "  MNT_HOUR,     	"+"\n"
        		+ "  MNT_MIN,     	"+"\n"
        		+ "  MNT_IMIN,     	"+"\n"
        		+ "  RCT_YN,     	"+"\n"
        		+ "  RCT_CD,     	"+"\n"
        		+ "  SATELLITE,     	"+"\n"
        		+ "  SENSOR,     	"+"\n"
        		+ "  DATA_LVL,     	"+"\n"
  				+ "  DATA_TYPE,     	"+"\n"
  				+ "  DATA_FORMAT,    "+"\n"
  				+ "  DATA_AREA,     	"+"\n"
  				+ "  DATA_RES,     	"+"\n"
  				+ "  DATA_PROJ,     	"+"\n"
                + "  FILE_PATH,     	"+"\n"
                + "  FILE_PTN,		"+"\n"
                + "  DOWN_YN,		"+"\n"
                + "  DOWN_DATE,		"+"\n"
                + "  DEL_YN		"+"\n"
                + ") VALUES (                           "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                                "+"\n"
		        + "    ?,                                "+"\n"
		        + "    DATETIME('NOW'),                   "+"\n"
		        + "    'N'                   "+"\n"
		        + ")";

	     // 변수설정
	     //   - Database 변수
	     Connection conn = createConnection();
	     PreparedStatement pstmt = null;
	
	     //   - 입력 결과 변수
	     int inserted = 0;
	
	     try {
	         // PreparedStatement 생성
	         pstmt = conn.prepareStatement(sql);
	         
	         for(int i = 0; i < dataMapList.size(); i++ ) {
	        	// 입력 데이터 객체
	        	Map<String, Object> dataMap = dataMapList.get(i);
	
		        // 입력 데이터 매핑
		        pstmt.setObject(1, dataMap.get("MNT_CYCLE"));
		        pstmt.setObject(2, dataMap.get("MNT_YMD"));
		        pstmt.setObject(3, dataMap.get("MNT_HOUR"));
		        pstmt.setObject(4, dataMap.get("MNT_MIN"));
		        pstmt.setObject(5, dataMap.get("MNT_IMIN"));
		        pstmt.setObject(6, "N");
		        pstmt.setObject(7, "0");
		        pstmt.setObject(8, dataMap.get("SATELLITE"));
		        pstmt.setObject(9, dataMap.get("SENSOR"));
		        pstmt.setObject(10, dataMap.get("DATA_LVL"));
		        pstmt.setObject(11, dataMap.get("DATA_TYPE"));
		        pstmt.setObject(12, dataMap.get("DATA_FORMAT"));
		        pstmt.setObject(13, dataMap.get("DATA_AREA"));
		        pstmt.setObject(14, dataMap.get("DATA_RES"));
		        pstmt.setObject(15, dataMap.get("DATA_PROJ"));
		        pstmt.setObject(16, dataMap.get("FILE_PATH"));
		        pstmt.setObject(17, dataMap.get("FILE_PTN"));
		        pstmt.setObject(18, "N");
		         
		        // Batch에 추가
				pstmt.addBatch();
				
				// Batch 실행
				if( i % OPT_BATCH_SIZE == 0 ) {
				    inserted += pstmt.executeBatch().length;
				}
	         }
	
	         // 입력 건수 조회
	         inserted += pstmt.executeBatch().length;
 
	         // 트랜잭션 COMMIT
	         conn.commit();
	
	     } catch (SQLException e) {
	         // 오류출력
	         System.out.println(e.getMessage());
	         e.printStackTrace();
	         // 트랜잭션 ROLLBACK
	         if( conn != null ) {
	             conn.rollback();
	         }
	         
	         // 오류
	         inserted = -1;
	
	     } finally {
	    	 closeStatement(pstmt);
	        	closeConnection(conn);
	     }
	
	     // 결과 반환
	     //   - 입력된 데이터 건수
	     return inserted;
	}
	
	@Override
	public boolean selectCheckCnt() throws Exception{
		// 상수설정
        //   - SQL
        final String SQL = "   SELECT COUNT(*) AS CNT FROM TB_ITEM_DOWN_LIST WHERE MNT_YMD = ? AND MNT_HOUR = ?   ";
						 
        // 변수설정
        //   - Database 변수
        Connection conn = createConnection();
        PreparedStatement pstmt = null;
        boolean isYn = false;
        ResultSet rs = null;
        try {
			//date setting
			Date nowDate = new Date(); 
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			String now = sdf.format(nowDate);
			String yyyy = now.substring(0,4);
			String MM = now.substring(4,6);
			String dd = now.substring(6,8);
			String HH = now.substring(8,10);
			
            // PreparedStatement 객체 생성
            pstmt = conn.prepareStatement(SQL);
            
            // 조회 데이터 조건 매핑
            pstmt.setObject(1, yyyy+MM+dd);
            pstmt.setObject(2, HH);
            
            // 데이터 조회
            rs = pstmt.executeQuery();
            if(rs.next()) {
	            int result = rs.getInt(1);
	            if(result>0) {
	            	isYn = true;
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
        return isYn;
	}
	
	
	@Override
	public List<Map<String, Object>> selectCheckList() throws Exception{
		// 상수설정
        //   - SQL
        final String SQL = "   SELECT                       "+"\n"
						 + "   	MNT_SEQ,                    "+"\n"
						 + "   	MNT_CYCLE,                  "+"\n"
						 + "   	MNT_YMD,                    "+"\n"
						 + "   	MNT_HOUR,                   "+"\n"
						 + "   	MNT_MIN,                    "+"\n"
						 + "   	RCT_YN,                     "+"\n"
						 + "   	RCT_CD,                     "+"\n"
						 + "   	SATELLITE,                  "+"\n"
						 + "   	SENSOR,                     "+"\n"
						 + "   	DATA_LVL,                   "+"\n"
						 + "   	DATA_TYPE,                  "+"\n"
						 + "   	DATA_FORMAT,                "+"\n"
						 + "   	DATA_AREA,                  "+"\n"
						 + "   	DATA_RES,                   "+"\n"
						 + "   	DATA_PROJ,                  "+"\n"
						 + "   	FILE_PATH,                  "+"\n"
						 + "   	FILE_PTN,                    "+"\n"
						 + "   	DOWN_YN                    "+"\n"
						 + "   FROM TB_ITEM_DOWN_LIST              "+"\n"
						 + "   WHERE 1=1                    "+"\n"
						 + "   AND SUBSTR(MNT_YMD,1,4)||'-'||SUBSTR(MNT_YMD,5,2)||'-'||SUBSTR(MNT_YMD,7,2)||' '||MNT_HOUR||':'||MNT_IMIN||':00' >= datetime('now', '-1 hour')			    "+"\n"
						 + "   AND RCT_YN = 'N'           	"+"\n"
						 + "   AND RCT_CD = '0'           	"+"\n"
						 + "   AND DOWN_YN = 'N'           	"+"\n"
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
	public List<Map<String, Object>> reqRemoteDownload(List<Map<String, Object>> listMap) {
        
		List<Map<String, Object>> mtList = new ArrayList<Map<String, Object>>();
		//thread cnt get
		int threadCnt = adminService.selectThreadCnt();
		
        // 쓰레드 풀을 생성한다. (고정 스레드 풀 이용)
        ExecutorService servicePool = Executors.newFixedThreadPool(threadCnt);
        // Callable 객체를 생성한 후 쓰레드 풀에 등록한다. 등록된 쓰레드에 대해 Future 객체를 리턴 받는다.
        
        try {
	        for(Map<String, Object> map : listMap) {
	            /*
	               1) Runnable : 메서드가 void형인 run 메서드만 있다.
	               2) Callable : 제네릭으로 정의한 리턴 타입을 가지는 call 메서드가 제공된다.
	               (비동기로 데이터를 처리한 이후에 그 결과를 리턴할 필요가 있다면 Callable을 이용해야한다.)
	             */
	        	String filePath = (String) map.get("FILE_PATH");
	        	String fileName = (String) map.get("FILE_PTN");
	        	filePath = filePath.replace("/vol01/DATA", "");
	            Future<Map<String, Object>> future = servicePool.submit(calSquare(filePath, fileName));
	            if("Y".equals(future.get().get("fileExists"))){
					map.put("DOWN_YN","Y");
					map.put("DOWN_SIZE",future.get().get("fileSize"));
		            mtList.add(map);
	            }
	        }
        } catch (InterruptedException e) {
			log.error(e.getMessage());
			Thread.currentThread().interrupt();
		} catch (ExecutionException e) {
			log.error(e.getMessage());
		}
	    servicePool.shutdown();

        return mtList;
    }
	
	public Callable<Map<String, Object>> calSquare(String path, String name) {
	    Callable<Map<String, Object>> callable = new Callable<Map<String, Object>>() {
	        @Override
	        public Map<String, Object> call() throws Exception {
	        	//Thread.sleep(1000);
	            CommonFileUtils commonFileUtils = new CommonFileUtils();
	            return commonFileUtils.httpsDownloadFile(remoteImageDownUrl+path, localFilePath+path, name);
	        }
	    };
		return callable;
	}
	
	
	@Override
	public int updateDownDb(List<Map<String, Object>> dataMapList) throws Exception{
		final String sql = "UPDATE TB_ITEM_DOWN_LIST				"+"\n"
				+ " SET DOWN_YN = ?, DOWN_DATE = DATETIME('NOW'), DOWN_SIZE = ?	"+"\n"
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
		        pstmt.setObject(1, dataMap.get("DOWN_YN"));
		        pstmt.setObject(2, dataMap.get("DOWN_SIZE"));
		        pstmt.setObject(3, dataMap.get("MNT_SEQ"));
		         
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
	public int deleteOldDbList() throws Exception {
		final String sql = "DELETE FROM TB_ITEM_DOWN_LIST		"+"\n"
				+ " WHERE SUBSTR(MNT_YMD,1,4)||'-'||SUBSTR(MNT_YMD,5,2)||'-'||SUBSTR(MNT_YMD,7,2)||' '||MNT_HOUR||':'||MNT_IMIN||':00' < datetime('now', '-24 hour')			    "+"\n"
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
	         
		    // 쿼리 실행    
			pstmt.executeUpdate();
			
			// 업데이트 건수 조회
			updateed = pstmt.getUpdateCount();
				
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
