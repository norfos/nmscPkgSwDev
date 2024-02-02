package egovframework.nmsc.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FileDownload {
	private static final Logger log = LoggerFactory.getLogger(FileDownload.class);
	
	public void fileDownload(Map<String,Object> fileInfo , HttpServletResponse res) throws ClientAbortException  {
		String fileName = (String)fileInfo.get("sysFileName");
		String oriFileName = (String)fileInfo.get("oriFileName");
		String saveFile = (String)fileInfo.get("sysFilePath")+fileName;
		String contentType =  (String)fileInfo.get("eventn");
		
        File file = new File(saveFile);
        long fileLength = file.length();

        res.setHeader("Content-Disposition", "attachment; filename=\"" + oriFileName + "\";");
        res.setHeader("Content-Transfer-Encoding", "binary");
        res.setHeader("Content-Type", contentType);
        res.setHeader("Content-Length", "" + fileLength);
        res.setHeader("Pragma", "no-cache;");
        res.setHeader("Expires", "-1;");

        try(
            FileInputStream fis = new FileInputStream(saveFile);
            OutputStream os = res.getOutputStream();
        ) {
            int readCount = 0;
            byte[] buffer = new byte[1024];
            while((readCount = fis.read(buffer)) != -1){
                    os.write(buffer,0,readCount);
            }
        }catch(IOException e) {
        	throw new ClientAbortException(e);
        }catch(Exception ex){
           log.error(ex.toString());
        }
	}
}
