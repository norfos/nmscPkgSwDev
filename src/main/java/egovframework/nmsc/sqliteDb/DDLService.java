package egovframework.nmsc.sqliteDb;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DDLService extends SQLiteManager {
	private static final Logger log = LoggerFactory.getLogger(DDLService.class);
    // 생성자
    public DDLService() {
    }
    public DDLService(String url) {
        super(url);
    }
 
    // SQL 실행 함수
    public ResultType executeSQL(final String SQL, Connection conn) throws SQLException {
        // 변수설정
        //   - Database 변수
        Statement stmt = null;
 
        //   - 결과 변수
        ResultType result = ResultType.FAILURE;
 
        try {
            // Database 연결
 
            // Statement 객체  생성
            stmt = conn.createStatement();
 
            // SQL 실행
            stmt.execute(SQL);
 
            // 트랜잭션 COMMIT
            conn.commit();
 
            // 성공
            result = ResultType.SUCCESS;
 
        } catch (SQLException e) {
            // 오류출력
            System.out.println(e.getMessage());
 
            // 트랜잭션 ROLLBACK
            if( conn != null ) {
                conn.rollback();
            }
 
            // 오류
            result = ResultType.FAILURE;
 
        } finally {
            // Statement 종료
            if( stmt != null ) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                	log.error(e.getMessage());
                }
            }
        }
 
        // 결과 반환
        return result;
    }
 
    // 테이블 조회 함수
    public boolean checkTable(String tableName, Connection conn) throws SQLException {
        // 변수설정
        //   - Database 변수
        DatabaseMetaData meta = conn.getMetaData();
 
        // 테이블 목록 조회
        ResultSet tables = meta.getTables(null, null, tableName, null);
 
        // 테이블 생성 확인
        return ( tables.next() ? tables.getRow() != 0 : false );
    }
 
    // 테이블 생성 함수
    public ResultType createTable(String tableName, final String SQL, Connection conn) throws SQLException {
        // 테이블 확인
        if( checkTable(tableName, conn) ) {
            return ResultType.WARNING;
        }
 
        // SQL 실행 및 반환
        return executeSQL(SQL, conn);
    }
 
    // 테이블 삭제 함수
    public ResultType dropTable(String tableName, Connection conn) throws SQLException {
        // 테이블 확인
        if( !checkTable(tableName, conn) ) {
            return ResultType.WARNING;
        }
 
        // SQL 실행 및 반환
        return executeSQL("DROP TABLE IF EXISTS "+tableName, conn);
    }
 
    // 결과 코드 정의
    public enum ResultType {
        SUCCESS(1),     // 성공
        WARNING(0),     // 경고
        FAILURE(-1);    // 실패
 
        // 코드 변수
        private int code = 0;
 
        // 코드값 설정
        private ResultType(int code){
            this.code = code;
        }
 
        // 코드값 가져오기
        public int getCode() {
            return this.code;
        }
    }
}