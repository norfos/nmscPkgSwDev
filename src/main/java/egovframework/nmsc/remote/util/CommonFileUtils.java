package egovframework.nmsc.remote.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.FileUtils;
import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
 * 파일 업로드 유틸
 * 편의성을 위해 VO대신 맵으로 사용
 * return type List<Map<String, Object>> 
 * 리턴한 맵 리스트를 이용해 파일테이블에 insert해야함
 */
@Component("commonFileUtils")
public class CommonFileUtils {
	private static final Logger log = LoggerFactory.getLogger(CommonFileUtils.class);
	
	private static String certName = "nmsc.kma.go.kr.crt"; // 이미지 요청URL
	
	@Resource(name = "propertiesService")
	protected EgovPropertyService propertiesService;

	/**
	 * HTTPS 파일 다운로드
	 * @param url
	 * @param fileName
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public void httpsFileDown(String url, String fileName) throws IOException {
		File f = new File(fileName);
		FileUtils.copyURLToFile(new URL(url), f);
	}
	
	public static Map<String, Object> httpsDownloadFile(String fileUrl, String savePath, String saveName) throws IOException {
        int bufferSize = 4096;
        InputStream is = null;
        FileOutputStream os = null;
        String fileExists = "N";
        String remoteUrl = fileUrl+saveName;
        Map<String, Object> result = new HashMap<>();
        long contentLength = 0L;
        try {
            URL url = new URL(remoteUrl);
            HttpsURLConnection httpConn = (HttpsURLConnection) url.openConnection();
            
            int responseCode = httpConn.getResponseCode();

            // Check for HTTP response code 200 (success)
            if (responseCode == HttpsURLConnection.HTTP_OK) {
            	// Verify the content length of the file
                contentLength = httpConn.getContentLengthLong();
                if (contentLength > 5000) {
                	fileExists = "Y";
                }else {
                	result.put("fileExists", "N");
                	return result;
                }
                // Open input stream from the connection
                is = httpConn.getInputStream();

                // Open output stream to local file
                File dir = new File(savePath);
                if (!dir.exists()) {
                    dir.mkdirs(); // Create parent directories if they don't exist
                }
                String saveFilePath = savePath+saveName;
                os = new FileOutputStream(saveFilePath);
                
                is.mark(Integer.MAX_VALUE);
                long size = 0;
                // Read the file content in chunks
                byte[] buffer = new byte[bufferSize];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }

                // Close streams
                os.close();
                is.close();
                
            }
        } catch (Exception e) {
        	log.error("An error occurred while trying to download a file.");
            e.printStackTrace();
        } finally {
            // Close streams in a finally block
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
            	log.error("IOException : "+e.getMessage());
            }
        }
        result.put("fileExists",fileExists);
        result.put("fileSize",(contentLength/1000));
        return result;
    }
	
	/**
	 * 파일 삭제
	 * @param filePath
	 */
	public void fileRemove(String filePath) {
		try {
			File file = new File(filePath);
			if(file.exists()) {					// 파일에 접근 할 수 있으면
				if(file.delete()) {
					log.info("file Remove Success!!");
				}else {
					log.info("file Remove Fail!!");
				}
			}
		} catch (Exception e) {
			log.error(e.toString());
		}
		
	}
	
	private static X509Certificate loadCertificateFromFile(String filePath) throws Exception{
		InputStream is = null;
		try {
			is = CommonFileUtils.class.getClassLoader().getResourceAsStream(filePath);
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			Certificate certificate = cf.generateCertificate(is);
			if(certificate instanceof X509Certificate) {
				return (X509Certificate) certificate;
			}else {
				throw new IllegalArgumentException("The provided file does not contain an X.509 certificate.");
			}
		}finally {
			if(is != null) {
				is.close();
			}
		}
	}
	
	private static SSLContext createSSLContext(X509Certificate trustedCertificate) throws Exception {
		TrustManager[] trustManagers = { new CustomTrustManager(trustedCertificate) };
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null,trustManagers,null);
		return sslContext;
	}
	
	static class CustomTrustManager implements X509TrustManager{
		private final X509Certificate trustedCertificate;
		
		public CustomTrustManager(X509Certificate trustedCertificate) {
			this.trustedCertificate = trustedCertificate;
		}
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) {
			
		}
		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) {
			for(X509Certificate cert : chain) {
				try {
					cert.checkValidity();
					cert.verify(trustedCertificate.getPublicKey());
				}catch(Exception e) {
					e.printStackTrace();
					throw new SecurityException("Certificate validation failed.");
				}
			}
		}
		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}
	}
	
	public List<Map<String, Object>> httpsJsonToListMap(String urlStr) {
		List<Map<String, Object>> dataMapList = new ArrayList<>();
		HttpsURLConnection connection = null;
		
		try {
//			X509Certificate downloadedCert = loadCertificateFromFile(certName);
//			System.out.println("Subject : "+downloadedCert.getSubjectDN());
//			System.out.println("Issuer : "+downloadedCert.getIssuerDN());
//			SSLContext sslContext = createSSLContext(downloadedCert);
			
            // HTTPS URL 설정
            URL url = new URL(urlStr);

            // HttpsURLConnection 열기
            connection = (HttpsURLConnection) url.openConnection();
            // 기본적으로는 SSL 인증서 검증을 수행하며, 비즈니스에서는 실제 인증서를 사용해야 합니다.
            //connection.setSSLSocketFactory(sslContext.getSocketFactory());
            
            connection.setDoInput(true);
            connection.setUseCaches(false);
            
            // 요청 메소드 설정
            connection.setRequestMethod("GET");
            connection.setRequestProperty("HeaderKey", "HeaderValue");

            // 응답 코드 확인
            int responseCode = connection.getResponseCode();
            System.out.println("응답코드 : "+responseCode);
            System.out.println("응답메세지 : "+connection.getResponseMessage());
            
            connection.connect();
            connection.setInstanceFollowRedirects(true);
            
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                // 응답 데이터 읽기
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                	response.append(line);
                }
                reader.close();
                
                // JSON 데이터 처리
                String jsonResponse = response.toString();
                //2. Parser
                JSONParser jsonParser = new JSONParser();
                
                //3. To Object
                Object obj = jsonParser.parse(jsonResponse);
                
                //4. To JsonObject
                JSONObject jsonObj = (JSONObject) obj;
                
                // 여기에서 JSON 데이터를 파싱하거나 다른 작업 수행
                JSONArray jsonBody = (JSONArray) jsonObj.get("body");
                
                for(int i=0;i<jsonBody.size();i++) {
                	Map<String,Object> map = getJsonObjectToMap((JSONObject) jsonBody.get(i));
                	dataMapList.add(map);
                }
            } else {
                log.info("HTTP 요청 실패. 응답 코드: " + responseCode);
            }

            // 연결 닫기
            connection.disconnect();
		} catch (ParseException e) {
			log.error("#httpsJson ParseException : "+e.getMessage());
        } catch (IOException e) {
        	log.error("#httpsJson IOException : "+e.getMessage());
        } catch (Exception e) {
        	log.error("#httpsJson Exception : "+e.getMessage());
        } finally {
        	if(connection != null) {
        		connection.disconnect();
        	}
        }
		return dataMapList;
	}
	
	public static Map<String,Object> getJsonObjectToMap(JSONObject jsonObject){
		Map<String,Object> map = null;
		try {
			map = new ObjectMapper().readValue(jsonObject.toJSONString(), Map.class);
		}catch(JsonParseException e) {
			log.error("#getJsonObjectToMap JsonParseException : "+e.getMessage());
		}catch(JsonMappingException e) {
			log.error("#getJsonObjectToMap JsonMappingException : "+e.getMessage());
		}catch(IOException e) {
			log.error("#getJsonObjectToMap IOException : "+e.getMessage());
		}
		return map;
	}
	
	

	
}
