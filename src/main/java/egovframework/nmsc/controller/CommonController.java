package egovframework.nmsc.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CommonController {
	
	/**
	 * 접근불가페이지
	 * @Author : 김영지
	 * @Date : 22.03.21
	 * @Method : accessDenied
	 * @return : String
	 * @throws Exception 
	 */
	@GetMapping(value = "/common/accessDenied.do")
	public String accessDenied(Model model, HttpServletRequest req) {
		return "common/403";
	}
	
	/**
	 * 공통 로딩페이지
	 * @Author : 김영지
	 * @Date : 22.03.23
	 * @Method : viewIpLoading
	 * @return : String
	 * @throws Exception 
	 */
	@GetMapping(value = "/common/viewIpLoading.do")
	public String viewIpLoading(Model model, HttpServletRequest req)  {
		return "common/viewIpLoading";
	}
	
	/**
	 * 공통헤더
	 * @Author : 김영지
	 * @Date : 22.03.10
	 * @Method : viewIpHeader
	 * @return
	 * @throws Exception 
	 */
	@GetMapping(value = "/common/viewIpHeader.do")
	public String viewIpHeader(Model model, HttpServletRequest req) {
		return "common/viewIpHeader";
	}
	
	/**
	 * 공통푸터
	 * @Author : 김영지
	 * @Date : 22.03.10
	 * @Method : viewIpFooter
	 * @return
	 * @throws Exception 
	 */
	@GetMapping(value = "/common/viewIpFooter.do")
	public String viewIpFooter(Model model, HttpServletRequest req)  {
		return "common/viewIpFooter";
	}
	
	/**
	 * 공통 확인창 팝업
	 * @Author : 김영지
	 * @Date : 22.03.17
	 * @Method : viewPpConfirmation
	 * @return : String
	 * @throws Exception 
	 */
	@GetMapping(value = "/common/popup/viewPpConfirmation.do")
	public String viewPpConfirmation(@RequestParam Map<String, Object> paramMap, Model model, HttpServletRequest req)  {
		model.addAttribute("paramMap", paramMap);
		return "common/popup/viewPpConfirmation";
	}
	
	/**
	 * 공통 페이징
	 * @Author : 김영지
	 * @Date : 22.04.26
	 * @Method : viewIpPaging
	 * @return : String
	 * @throws Exception 
	 */
	@GetMapping(value = "/common/viewIpPaging.do")
	public String viewIpPaging(Model model)  {
		return "common/viewIpPaging";
	}
	
	
	
	
}