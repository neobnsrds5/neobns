package neo.spider.admin.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import neo.spider.admin.apimock.dto.ApiDTO;
import neo.spider.admin.apimock.service.ApiService;
import neo.spider.admin.dto.AdminApiDTO;
import neo.spider.admin.service.AdminService;

@Controller
@RequiredArgsConstructor
public class AdminController {

	private final AdminService adminService;
	
	@GetMapping("/admin")
	public String showAdmin(
			@RequestParam(defaultValue = "1") int page,   
            @RequestParam(defaultValue = "10") int size,  
            @RequestParam(required = false) String mockApiName,  
            Model model) {
//		List<ApiDTO> apiList = apiService.getAllApis();
//		model.addAttribute("apiList", apiList);
		
		 // 검색 및 페이징 처리된 API 목록 가져오기
        List<AdminApiDTO> apiList = adminService.findApisByPage(page, size, mockApiName);
        int totalApis = adminService.countApis(mockApiName);
        int totalPages = (int) Math.ceil((double) totalApis / size);

        // Model에 데이터 추가
        model.addAttribute("apiList", apiList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("searchKeyword", mockApiName); // 검색어 유지
		return "admin";
	}
	
}
