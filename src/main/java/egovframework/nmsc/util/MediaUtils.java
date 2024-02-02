package egovframework.nmsc.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;

//업로드한 파일 중 원하는 파일만 거르는 클래스
public class MediaUtils {

	private static Map<String, MediaType> mediaMap;
	
	static {
		mediaMap = new HashMap<String, MediaType>();
		mediaMap.put("JPG", MediaType.IMAGE_JPEG);
		mediaMap.put("JPEG", MediaType.IMAGE_JPEG);
		mediaMap.put("GIF", MediaType.IMAGE_GIF);
		mediaMap.put("PNG", MediaType.IMAGE_PNG);
		mediaMap.put("PDF", MediaType.APPLICATION_PDF);
		mediaMap.put("MP4", MediaType.MULTIPART_FORM_DATA);
		mediaMap.put("AVI", MediaType.MULTIPART_FORM_DATA);
		mediaMap.put("WMB", MediaType.MULTIPART_FORM_DATA);
		mediaMap.put("MPG", MediaType.MULTIPART_FORM_DATA);
		mediaMap.put("MKV", MediaType.MULTIPART_FORM_DATA);
		mediaMap.put("MOV", MediaType.MULTIPART_FORM_DATA);
	}//
	
	public static MediaType getMediaType(String type) {
		return mediaMap.get(type.toUpperCase());
	}//
	
}
