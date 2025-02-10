package spider.neo.solution.flowcontrol;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestChange {

    public static void main(String[] args) throws InterruptedException {
        BulkheadRegistry bulkheadRegistry = BulkheadRegistry.ofDefaults();
        Bulkhead bulkhead = bulkheadRegistry.bulkhead("testBulkhead",
                BulkheadConfig.custom()
                        .maxConcurrentCalls(20)
                        .maxWaitDuration(Duration.ofMillis(500))
                        .build());

        ExecutorService executor = Executors.newFixedThreadPool(20);

        // 20개의 요청 실행
        for (int i = 0; i < 20; i++) {
            executor.submit(() -> {
                if (bulkhead.tryAcquirePermission()) { // 요청 가능 여부 확인
                    try {
                        System.out.println("Executing task: " + Thread.currentThread().getName());
                        Thread.sleep(1000); // 1초 동안 실행
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        bulkhead.releasePermission(); // 실행 완료 후 자원 반환
                    }
                } else {
                    System.out.println("Task rejected: " + Thread.currentThread().getName());
                }
            });
        }

        // 1초 후 설정 변경 (20 -> 10)
        Thread.sleep(1000);
        System.out.println("Changing Bulkhead Config...");
        bulkhead.changeConfig(BulkheadConfig.custom()
                .maxConcurrentCalls(10)
                .maxWaitDuration(Duration.ofMillis(500))
                .build());

        executor.shutdown();
    }


}
