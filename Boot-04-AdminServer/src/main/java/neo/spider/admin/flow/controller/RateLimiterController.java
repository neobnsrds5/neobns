package neo.spider.admin.flow.controller;

import neo.spider.admin.flow.dto.ratelimiter.RateLimiterDto;
import neo.spider.admin.flow.service.RateLimiterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/flow/rateLimiter")
public class RateLimiterController {

    private final RateLimiterService rateLimiterService;

    public RateLimiterController(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody RateLimiterDto dto) {
        try{
            boolean result = rateLimiterService.create(dto);
            if (!result) {
                return ResponseEntity.status(400).body("생성된 데이터가 없습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }

        return ResponseEntity.ok("success");
    }

    @GetMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam("id") long id, @RequestParam("application_name") String application_name) {
        try {
            boolean result = rateLimiterService.delete(id, application_name);
            if (!result){
                return ResponseEntity.status(400).body("삭제된 설정이 없습니다.");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
        return ResponseEntity.ok("삭제 성공");
    }

    @GetMapping("/find")
    public ResponseEntity<RateLimiterDto> find(@RequestParam("id") long id) {
        RateLimiterDto result = rateLimiterService.findById(id);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/update")
    public ResponseEntity<String> update(@RequestBody RateLimiterDto dto) {
        try {
            boolean result = rateLimiterService.update(dto);
            if (!result) {
                return ResponseEntity.status(400).body("수정된 설정이 없습니다.");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
        return ResponseEntity.ok("success");
    }
}
