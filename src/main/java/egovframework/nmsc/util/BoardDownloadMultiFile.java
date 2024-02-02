package egovframework.nmsc.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import egovframework.nmsc.service.impl.AdminServiceImpl;

public class BoardDownloadMultiFile {
	private static final Logger log = LoggerFactory.getLogger(BoardDownloadMultiFile.class);
	
	final static String folder = "/temp/";
	
	public BoardDownloadMultiFile() {
		
	}
	
	public BoardDownloadMultiFile(List<Map<String, Object>> fileInfoMaps,HttpServletRequest req, HttpServletResponse res) throws UnsupportedEncodingException {
		String sysFileName = "";
		String oriFileName = "";
		String sysFilePath = "";
		int index = fileInfoMaps.size();
		
		List<String> filesString = new ArrayList<>();
		
		if(index  == 1) {
		
		for(int i=0; i<fileInfoMaps.size();i++) {
			sysFileName = (String) fileInfoMaps.get(i).get("sysFileName");
			sysFilePath = (String) fileInfoMaps.get(i).get("sysFilePath");
			filesString.add(sysFilePath +"/"+ sysFileName);
		}
		
		oriFileName = (String) fileInfoMaps.get(0).get("oriFileName");
		oriFileName = URLEncoder.encode(oriFileName, "UTF-8");
		File file = new File(filesString.get(0));
		
		Long fileLength = file.length();
		
		res.setHeader("Content-Disposition", "attachment; filename=\"" + oriFileName + "\";");
		res.setHeader("Content-Transfer-Encoding", "binary");
		res.setHeader("Content-Length", "" + fileLength);
		res.setHeader("Pragma", "no-cache;");
		res.setHeader("Expires", "-1;");

         try( FileInputStream fis = new FileInputStream(filesString.get(0)); 
        	  OutputStream out = res.getOutputStream(); ) {
              int readCount = 0;
              byte[] buffer = new byte[1024];
              while((readCount = fis.read(buffer)) != -1) {
                out.write(buffer,0,readCount);
             }
         }catch(Exception ex){
             throw new RuntimeException("file Save Error");
         }
         
         /// zip파일 다운로드 시작  
		} else {
         List<File> filesArray = new ArrayList<>();
         for(int i = 0 ; i < fileInfoMaps.size() ; i++ ) {
        	
        	 File files = new File((String) fileInfoMaps.get(i).get("sysFileName"));
        	 filesArray.add(files);
         }
         int filelength  = filesArray.size();
         String ouputName = "Zip Files";
         ZipOutputStream zos = null;
         try {
        	    if (req.getHeader("User-Agent").indexOf("MSIE 5.5") > -1) {
        	        res.setHeader("Content-Disposition", "filename=" + ouputName + ".zip"+";");
        	    } else {
        	        res.setHeader("Content-Disposition", "attachment; filename=" + ouputName + ".zip"+";");
        	    }
        	    res.setHeader("Content-Transfer-Encoding", "binary");
        	    
        	                
        	    OutputStream os = res.getOutputStream();
        	    zos = new ZipOutputStream(os); // ZipOutputStream
        	    zos.setLevel(8); // 압축 레벨 - 최대 압축률은 9, 디폴트 8
        	    
        	     
        	    String[] filePaths = new String[filelength];
        	    String[] fileNames = new String[filelength];
        	    
        	    for(int i =0 ; i < filelength ; i ++) {
        	    	
        	    	filePaths[i] =  (String) fileInfoMaps.get(i).get("sysFilePath");
        	    	fileNames[i] =  (String) fileInfoMaps.get(i).get("sysFileName");
        	    }
        	    
        	    for(int i =0; i < filePaths.length; i ++){
        	        File sourceFile = new File(filePaths[i]+"\\",fileNames[i]);
        	                        
        	        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceFile))){
	        	        zos.putNextEntry(new ZipEntry(fileNames[i]));
	        	        
	        	        byte[] buffer = new byte[2048];
	        	        int cnt = 0;
	        	        while ((cnt = bis.read(buffer, 0, 2048)) != -1) {
	        	            zos.write(buffer, 0, cnt);
	        	        }
	        	        zos.closeEntry();
        	        }
        	 
        	    }
        	               
        	} catch(Exception e){
        		log.error(e.getMessage());
        	}finally {
    			try {
    				if(zos != null) {
    					zos.close();
    				}
				} catch (IOException e) {
					log.error(e.getMessage());
				}
        	}


		}

        
	}

	
}
