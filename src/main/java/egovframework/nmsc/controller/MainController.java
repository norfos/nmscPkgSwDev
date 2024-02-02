package egovframework.nmsc.controller;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class MainController {
	
	/**
	 * 메인화면
	 * @Author : norfos
	 * @Date : 23.10.26
	 * @Method : main
	 * @return : String
	 * @throws Exception 
	 */
	@GetMapping(value = "/main/main.do" )
	public String main(Model model, HttpServletRequest req)  {
		
		String tempUrl = req.getRequestURL() + "";
        if (tempUrl.contains("localhost")) {
            // 인증에 실패하거나 권한이 없는 사용자일 경우
            model.addAttribute("adminYn","Y");
        }else {
        	model.addAttribute("adminYn","N");
        }
		
		return "main/main";
	}
	
	
	
}
