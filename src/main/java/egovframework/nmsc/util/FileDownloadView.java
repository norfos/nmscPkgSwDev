package egovframework.nmsc.util;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.view.AbstractView;

import egovframework.nmsc.controller.RemoteController;


public class FileDownloadView  extends AbstractView {
	private static final Logger log = LoggerFactory.getLogger(FileDownloadView.class);
	private Map<String, Object> fileInfoMap;
	
	public FileDownloadView() {
		
	}
	
	public FileDownloadView(Map<String, Object> fileInfoMap) {
		this.fileInfoMap = fileInfoMap;
	}
	
	public FileDownloadView(String fileId) {
		this.fileInfoMap = new HashMap<String, Object>();
		this.fileInfoMap.put( "FILE_ID" , fileId );
	}
	
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest req, HttpServletResponse res ) throws Exception {
		res.setContentType("application/octet-stream; utf-8");
		res.setContentType("application/octet-stream");
		res.setHeader("Content-disposition", "attachment; filename=" + getDownFileNames(req, fileInfoMap.get("ORI_FILE_NAME").toString()));
		res.setHeader("Content-Transfer-Encoding", "binary");
		 
		InputStream fis = null;
		OutputStream out = res.getOutputStream();
        File file = null;
        
        try {
        	String path = (String)fileInfoMap.get("SYS_FILE_PATH");
        	String name = (String)fileInfoMap.get("SYS_FILE_NAME");
        	
        	file = new File( path + File.separator + name);
        	fis = Files.newInputStream(file.toPath());
        	
            res.setContentLength( (int)file.length()  );
    		FileCopyUtils.copy(fis, out);
             
        } catch(Exception e){
        	log.error(e.getMessage());
        } finally{
            if(fis != null){
                try{
                    fis.close();
                }catch(Exception e){}
            }
        }
         
        out.flush();
	}
	
	public String getDownFileNames(HttpServletRequest request, String realName) {
		String browser = "Firefox";
		String header = request.getHeader("User-Agent");
		String resultName = new String();

		if (header.indexOf("MSIE") > -1) {
			browser = "MSIE";
		} else if (header.indexOf("Chrome") > -1) {
			browser = "Chrome";
		} else if (header.indexOf("Opera") > -1) {
			browser = "Opera";
		} else if (header.indexOf("Safari") > -1) {
			browser = "Safari";
		}

		if (realName == null || realName.equals("")) {
			realName = "UnKnownFileName";
		}
		
		try {
			// Explorer
			if ( browser.indexOf("Internet Explorer") != -1 ) {
				resultName = new String(realName.getBytes("euc-kr"), "8859_1").replaceAll(" ", "%20");
			}
			// Opera
			else if ( browser.indexOf("Opera") != -1 ) {
				resultName = new String(realName.getBytes("utf-8"), "8859_1");
			}
			// Chrome
			else if ( browser.indexOf("Chrome") != -1 ) {
				resultName = new String(realName.getBytes("euc-kr"), "8859_1");
			}
			// Safari
			else if ( browser.indexOf("Safari") != -1 ) {
				resultName = new String(realName.getBytes("utf-8"), "8859_1");
			}
			// FireFox
			else if ( browser.indexOf("Firefox") != -1 ) {
				resultName = new String(realName.trim().replaceAll(" ", "_").getBytes("euc-kr"), "8859_1");
			}
			// Other
			else
				resultName = new String(realName.getBytes("euc-kr"), "8859_1");
		} catch (Exception ex) {
			resultName = realName;
		}
		return resultName;
	}

}
