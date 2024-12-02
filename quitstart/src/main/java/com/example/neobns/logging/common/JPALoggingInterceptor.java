package com.example.neobns.logging.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;

public class JPALoggingInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(JPALoggingInterceptor.class);

	// 슬로우 쿼리 저장소
	private static final Queue<Map<String, Object>> SLOW_QUERIES = new ConcurrentLinkedQueue<>();

	// 슬로우 쿼리 저장소 최대 크기
	private static int SLOW_QUERIES_SIZE = 10;
	// 슬로우 쿼리 기준 시간
	public static long SLOW_QUERY_THRESHOLD_MS = 0;

	// 슬로우 쿼리 최근 10개 반환
	public static List<Map<String, Object>> getSlowQueries() {
		synchronized (SLOW_QUERIES) {
			return new ArrayList<>(SLOW_QUERIES);
		}
	}

	// 스레드별 시작시간이 담긴 맵
	private static final ThreadLocal<Map<Object, Long>> entityStartTimes = ThreadLocal.withInitial(HashMap::new);

	@PrePersist
	public void prePersist(Object entity) {
		long start = System.currentTimeMillis();
		entityStartTimes.get().put(entity, start);
		String requestId = MDC.get("requestId");
		String sql = MDC.get("lastSql");
		System.out.println(">>> prePersist : " + start + requestId);

	}

	@PostPersist
	public void postPersist(Object entity) {
		long stop = System.currentTimeMillis();
		long start = entityStartTimes.get().get(entity);
		
		System.out.println("post start : "+ start);

		System.out.println("현재 : " + MDC.getCopyOfContextMap());
		
//		if (true) {
//			System.out.println("entityStartTimes.get().toString() : " + entityStartTimes.get().toString());
//			return;
//		}//else {
//			start = entityStartTimes.get().remove(entity);
//		}
		
		
		System.out.println(">>> postPersist : " + start + " : " + stop);
		String requestId = MDC.get("requestId");
		String sql = MDC.get("lastSql");
		long elapsedTime = stop - start;
		// 로깅
		logger.info("HERE log info {}; {}; {}; {}", MDC.get("requestId"), "SQL", sql, elapsedTime);
	}

	@PreUpdate
	public void preUpdate(Object entity) {
		System.out.println(">>> preUpdate");
	}

	@PostUpdate
	public void postUpdate(Object entity) {
		System.out.println(">>> postUpdate");
	}

	@PreRemove
	public void preRemove(Object entity) {
		System.out.println(">>> preRemove");
	}

	@PostRemove
	public void postRemove(Object entity) {
		System.out.println(">>> postRemove");
	}

	@PostLoad
	public void postLoad(Object entity) {
		System.out.println(">>> postLoad");
	}

}
