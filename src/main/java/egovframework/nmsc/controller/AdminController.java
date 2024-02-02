package egovframework.nmsc.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import egovframework.nmsc.file.FileRemove;
import egovframework.nmsc.service.AdminService;
import egovframework.nmsc.service.BoardService;

@Controller
public class AdminController {
	private static final Logger log = LoggerFactory.getLogger(AdminController.class);
	
	@Resource(name="adminService")
	private AdminService adminService;
	
	@Resource(name="boardService")
	private BoardService boardService;
	
	private String rootPath = "/nmscFile";
	
	@RequestMapping(value = "/admin/viewDataMngList.do")
	public String viewNoticeBoard(@RequestParam Map<String,Object> paramMap, Model model, HttpServletRequest req) throws Exception {
		System.out.println("viewDataMngList paramMap : "+paramMap);
		String saveGb = String.valueOf(paramMap.get("saveGb"));
		if("thread".equals(saveGb)) {
			String threadCnt = String.valueOf(paramMap.get("threadCnt"));
			int updated = adminService.updateThreadCnt(threadCnt);
			if(updated>0) {
				model.addAttribute("saveResult","처리완료");
			}else {
				model.addAttribute("saveResult","처리실패");
			}
		}
		
		if("areaGroup".equals(saveGb)) {
			String areaGroup = String.valueOf(paramMap.get("areaGroup"));
			String areaGroupYN = String.valueOf(paramMap.get("areaGroupYN"));
			String areaGroupArr[] = areaGroup.split(",");
			String areaGroupYNArr[] = areaGroupYN.split(",");
			List<Map<String,Object>> areaGroupList = new ArrayList<Map<String,Object>>();
			for(int i=0;i<areaGroupArr.length;i++) {
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("group",areaGroupArr[i]);
				map.put("chk",areaGroupYNArr[i]);
				areaGroupList.add(map);
			}
			int updated = adminService.updateAreaGroup(areaGroupList);
			if(updated>0) {
				model.addAttribute("saveResult","처리완료");
			}else {
				model.addAttribute("saveResult","처리실패");
			}		
		}
		
		List<Map<String, Object>> areaGroup = boardService.selectDateGroup();
		int threadCnt = adminService.selectThreadCnt();
		List<Map<String, Object>> itemAreaGroup = adminService.selectItemAreaGroup();
		model.addAttribute("areaGroup",areaGroup);
		model.addAttribute("threadCnt",threadCnt);
		model.addAttribute("itemAreaGroup",itemAreaGroup);
		return "admin/viewDataMngList";
	}
	
	@ResponseBody
	@PostMapping(value = "/admin/selectDataMngList.do")
	public Map<String,Object> selectDataMngList(@RequestBody Map<String,Object> paramMap) throws Exception {
		List<Map<String, Object>> boardList = boardService.selectDateList(paramMap);
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("boardList",boardList);
		result.put("total",boardList.get(0).get("TOTAL"));
		result.put("paramMap", paramMap);
		return result;
	}
	@ResponseBody
	@PostMapping(value = "/admin/fileRemove.do")
	public void eachFileRemove(@RequestParam("mntSeq[]") List<String> mntSeq, @RequestParam("filePath[]") List<String> filePath, @RequestParam("fileName[]") List<String> fileName, HttpServletRequest req, HttpServletResponse res) throws Exception {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		for(int i=0;i<filePath.size();i++) {
			Map<String,Object> fileInfo = new HashMap<String,Object>();
			fileInfo.put("mntSeq",mntSeq.get(i));
			fileInfo.put("sysFilePath",rootPath+"/"+filePath.get(i));
			fileInfo.put("sysFileName",fileName.get(i));
			list.add(fileInfo);
		}
		FileRemove fr = new FileRemove();
		fr.fileRemove(list);
		adminService.updateItemDelYn(list);
	}
	
	
	
}
