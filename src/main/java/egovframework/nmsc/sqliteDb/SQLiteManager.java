package egovframework.nmsc.sqliteDb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLiteManager {
	private static final Logger log = LoggerFactory.getLogger(SQLiteManager.class);
	 // 상수 설정
    //   - Database 변수
//	private static String  dbFile = "/vol01/home/dms02test01/MONITOR/nmscMonitor.db";	//위성센터 개발서버
//	private static String  dbFile = "C:/Users/wizai/Desktop/작업폴더1121/nmscMonitor.db";	//위성센터 로컬
//	private static String  dbFile = "/home/norfos/nmsc/nmscMonitor.db";					//wizai 개발서버
//	private static String  dbFile = "C:/nmscPkgSw/nmscPkgSw.db";						//wizai 로컬
	private static String  dbFile = "/nmscPkgSw/nmscPkgSw.db";						//wizai 로컬
    private static final String SQLITE_JDBC_DRIVER = "org.sqlite.JDBC";
    private static final String SQLITE_FILE_DB_URL = "jdbc:sqlite:"+dbFile;
    private static final String SQLITE_MEMORY_DB_URL = "jdbc:sqlite::memory";
 
    //  - Database 옵션 변수
    private static final boolean OPT_AUTO_COMMIT = false;
    private static final int OPT_VALID_TIMEOUT = 500;
 
    // 변수 설정
    //   - Database 접속 정보 변수
    private static Connection conn = null;
    private String driver = null;
    private String url = null;
 
    // 생성자
    public SQLiteManager(){
        this(SQLITE_FILE_DB_URL);
    }
    public SQLiteManager(String url) {
        // JDBC Driver 설정
        this.driver = SQLITE_JDBC_DRIVER;
        this.url = url;
    }
    
    static{
        initializeDataSource();
    }
    
    private static void initializeDataSource() {
    	// JDBC Driver Class 로드
        try {
			Class.forName(SQLITE_JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
        	log.error(e.getMessage());
        }
    }
 
    // DB 연결 함수
    public Connection createConnection() {
        try {
			 conn = DriverManager.getConnection(SQLITE_FILE_DB_URL);
			 conn.setAutoCommit(OPT_AUTO_COMMIT);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return conn;
    }
 
    // DB 연결 종료 함수
    public void closeConnection() {
        try {
            if( this.conn != null ) {
                this.conn.close();
            }
        } catch (SQLException e) {
        	log.error(e.getMessage());
        } finally {
            this.conn = null;
 
            // 로그 출력
            System.out.println("CLOSED");
        }
    }
 
    // DB 재연결 함수
    public Connection ensureConnection() {
        try {
            if( this.conn == null || this.conn.isValid(OPT_VALID_TIMEOUT) ) {
                closeConnection();      // 연결 종료
                createConnection();     // 연결
            }
        } catch (SQLException e) {
        	log.error(e.getMessage());
        }
 
        return this.conn;
    }
 
    // DB 연결 객체 가져오기
    public Connection getConnection() {
        return this.conn;
    }
    
    public void closeConnection(Connection conn) {
    	if( conn != null ) {
	    	try {
	            conn.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        } 
    	}
    }
    
    public static void closeResultSet(ResultSet rs) {
    	if(rs != null) {
    		try {
    			rs.close();
    		}catch(SQLException e) {
    			e.printStackTrace();
    		}
    	}
    }
    public static void closeStatement(PreparedStatement ps) {
    	if(ps != null) {
    		try {
    			ps.close();
    		}catch(SQLException e) {
    			e.printStackTrace();
    		}
    	}
    }
}
