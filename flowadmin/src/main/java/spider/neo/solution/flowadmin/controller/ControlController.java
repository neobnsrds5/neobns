package spider.neo.solution.flowadmin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import spider.neo.solution.flowadmin.dto.CreateApplicationDto;
import spider.neo.solution.flowadmin.dto.SearchApplicationResultDto;
import spider.neo.solution.flowadmin.dto.SearchDto;
import spider.neo.solution.flowadmin.dto.UpdateApplicationDto;
import spider.neo.solution.flowadmin.dto.bulkhead.BulkheadSearchDto;
import spider.neo.solution.flowadmin.dto.ratelimiter.RateLimiterSearchDto;
import spider.neo.solution.flowadmin.service.BulkheadService;
import spider.neo.solution.flowadmin.service.ControlService;
import spider.neo.solution.flowadmin.service.MessagePublisher;
import spider.neo.solution.flowadmin.service.RateLimiterService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Controller
public class ControlController {

    private final ControlService controlService;
    private final RateLimiterService rateLimiterService;
    private final BulkheadService bulkheadService;
    private final MessagePublisher messagePublisher;
    private final ObjectMapper objectMapper;

    public ControlController(ControlService controlService, MessagePublisher messagePublisher, ObjectMapper objectMapper,
                             BulkheadService bulkheadService, RateLimiterService rateLimiterService) {
        this.controlService = controlService;
        this.messagePublisher = messagePublisher;
        this.objectMapper = objectMapper;
        this.bulkheadService = bulkheadService;
        this.rateLimiterService = rateLimiterService;
    }

    @GetMapping("/")
    public String findPage(Model model,
                           @ModelAttribute SearchDto dto,
                           @RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "10") int size){

        try {
            if (dto.getId() != null){
                int id = Integer.parseInt(dto.getId());
            }
        } catch (Exception e) {
            int totalPage = 0;
            dto.setId(null);
            model.addAttribute("results", new ArrayList<SearchApplicationResultDto>());
            model.addAttribute("page", page);
            model.addAttribute("totalPage", totalPage);
            model.addAttribute("size", size);
            model.addAttribute("param", dto);
            model.addAttribute("range", new int[]{});
        }
        int total = controlService.count(dto);
        List<SearchApplicationResultDto> results = controlService.find(dto, page, size);
        int totalPage = (total / size) + (total % size == 0 ? 0 : 1);
        int dix = (page/10)*10;
        int start = dix+1;
        int end = dix+10;
        end = Math.min(end, totalPage);
        int[] range = IntStream.range(start, end+1).toArray();

        model.addAttribute("results", results);
        model.addAttribute("page", page);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("size", size);
        model.addAttribute("param", dto);
        model.addAttribute("range", range);

        return "main";
    }

    @PostMapping("/createApplication")
    public String create(@ModelAttribute CreateApplicationDto dto){
        long id = controlService.create(dto);
        return "redirect:/detail/"+id;
    }

    @GetMapping("/delete")
    public String delete(Model model,
                         @ModelAttribute SearchDto dto){
        int result = controlService.delete(Long.parseLong(dto.getId()));
        if (result == 0){
            System.out.println("delete failed");
        } else {
            System.out.println("delete success");
        }
        return "redirect:/";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") long id, Model model){
        SearchApplicationResultDto application = controlService.findById(id);
        List<BulkheadSearchDto> bulkheads = bulkheadService.findByApplication(application.getId());
        List<RateLimiterSearchDto> rateLimiters = rateLimiterService.findByApplication(application.getId());
        model.addAttribute("app", application);
        model.addAttribute("bulkheads", bulkheads);
        model.addAttribute("rateLimiters", rateLimiters);
        return "detail";
    }

    @PostMapping("/updateApplication")
    public String update(@ModelAttribute UpdateApplicationDto dto){
        int result = controlService.update(dto);
        if (result == 0){
            System.out.println("update failed");
        } else {
            System.out.println("update success");
        }
        return "redirect:/detail/" + dto.getId();
    }

}
