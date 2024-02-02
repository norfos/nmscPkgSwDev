package egovframework.nmsc.controller;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import egovframework.nmsc.file.FileDownload;
import egovframework.nmsc.service.BoardService;
import egovframework.nmsc.util.BoardDownloadMultiFile;

@Controller
public class BoardController {
	private static final Logger log = LoggerFactory.getLogger(BoardController.class);
	
	@Resource(name="boardService")
	private BoardService boardService;
	
	
	private String rootPath = "/nmscFile";

	/**
	 * 데이터목록 조회
	 * @Author : norfos
	 * @Date : 23.10.26
	 * @Method : viewDataList
	 * @return : String
	 * @throws Exception 
	 */
	@GetMapping(value = "/board/viewDataList.do")
	public String viewNoticeBoard( Model model, HttpServletRequest req)  {
		List<Map<String, Object>> areaGroup = boardService.selectDateGroup();
		model.addAttribute("areaGroup",areaGroup);
		return "board/viewDataList";
	}
	
	/**
	 * 데이터목록
	 * @Author : norfos
	 * @Date : 23.10.26
	 * @Method : selectDataList
	 * @return : Map
	 * @throws Exception 
	 */
	@ResponseBody
	@PostMapping(value = "/board/selectDataList.do")
	public Map<String,Object> selectDataList(@RequestBody Map<String,Object> paramMap) throws Exception {
		List<Map<String, Object>> boardList = boardService.selectDateList(paramMap);
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("boardList",boardList);
		result.put("total",boardList.get(0).get("TOTAL"));
		result.put("paramMap", paramMap);
		return result;
	}
	

	/**
	 * 파일 다운로드
	 * @author norfos
	 * @date 23.10.26
	 * @param paramMap
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	@PostMapping(value = "/board/downloadDataFile.do")
	public void downloadDataFile(@RequestParam Map<String,Object> paramMap, HttpServletRequest req, HttpServletResponse res) {
		try {
			FileDownload fileDownload = new FileDownload();
			Map<String,Object> fileInfo = new HashMap<String,Object>();
			String sysFileName = (String) paramMap.get("fileName");
			String sysFilePath = rootPath+"/"+paramMap.get("filePath");
			String oriFileName = (String) paramMap.get("fileName");
			String eventn = "image/png";
			fileInfo.put("sysFileName",sysFileName);
			fileInfo.put("sysFilePath",sysFilePath);
			fileInfo.put("oriFileName",oriFileName);
			fileInfo.put("eventn",eventn);
			fileDownload.fileDownload(fileInfo,res);
		}catch(Exception e) {
			log.error(e.getMessage());
		}
		
	}
	
	/**
	 * 멀티파일다운로드
	 * @author norfos
	 * @date 23.10.26
	 * @param paramMap
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	@PostMapping(value = "/board/downloadMultiFile.do")
	public void downloadMultiFile(@RequestParam Map<String,Object> paramMap, HttpServletRequest req, HttpServletResponse res)  {
		try {
			List<Map<String,Object>> fileInfoMaps = new ArrayList<Map<String,Object>>();
			String[] seqList = req.getParameterValues("seq");
			for(String str : seqList) {
				String[] strList = str.split(",");
				Map<String,Object> fileInfo = new HashMap<String,Object>();
				String sysFileName = strList[1];
				String sysFilePath = rootPath+"/"+strList[0];
				String oriFileName = strList[1];
				fileInfo.put("sysFileName",sysFileName);
				fileInfo.put("sysFilePath",sysFilePath);
				fileInfo.put("oriFileName",oriFileName);
				fileInfoMaps.add(fileInfo);
			}
			new BoardDownloadMultiFile(fileInfoMaps,req,res);
		}catch(Exception e) {
			log.error(e.getMessage());
		}
	}
	
	@PostMapping(value = "/board/selectDataFullImage.do")
	public String selectDataFullImage(@RequestParam Map<String, Object> paramMap, Model model, HttpServletRequest req) {
		model.addAttribute("imagePath", paramMap.get("imagePath"));
		return "board/popup/fullImage";
	}
	
	@GetMapping(value="/image/view", produces= MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] getImage(@RequestParam("filePath") String filePath, // yyyymmdd_HHmmssZ
                                         @RequestParam("fileName") String fileName) // A
            throws IOException{
        FileInputStream fis = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        String fileDir = "/nmscFile/" + filePath + fileName; // 파일경로

        try{
            fis = new FileInputStream(fileDir);
        } catch(FileNotFoundException e){
            e.printStackTrace();
        }

        int readCount = 0;
        byte[] buffer = new byte[1024];
        byte[] fileArray = null;

        try{
            while((readCount = fis.read(buffer)) != -1){
                baos.write(buffer, 0, readCount);
            }
            fileArray = baos.toByteArray();
            fis.close();
            baos.close();
        } catch(IOException e){
            throw new RuntimeException("File Error");
        }
        return fileArray;
    }
	
}
