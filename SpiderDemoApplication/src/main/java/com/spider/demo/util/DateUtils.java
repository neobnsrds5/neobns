package com.spider.demo.util;

/**
 * 날짜 처리를 위한 유틸리티 클래스.
 */
/**
 * 날짜 처리를 위한 유틸리티 클래스.
 */
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtils {

    // yyyyMMdd 형식의 DateTimeFormatter
    static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 현재 날짜를 yyyyMMdd 형식의 문자열로 변환합니다.
     *
     * @return yyyyMMdd 형식의 현재 날짜 문자열
     */
    public static String getCurrentDateAsString() {
        return LocalDate.now().format(DATE_FORMATTER);
    }

    /**
     * LocalDate를 yyyyMMdd 형식의 문자열로 변환합니다.
     *
     * @param date 변환할 LocalDate
     * @return yyyyMMdd 형식의 문자열
     * @throws IllegalArgumentException date가 null일 경우
     */
    public static String toDateString(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        return date.format(DATE_FORMATTER);
    }

    /**
     * yyyyMMdd 형식의 문자열을 LocalDate로 변환합니다.
     *
     * @param dateString yyyyMMdd 형식의 문자열
     * @return 변환된 LocalDate 객체
     * @throws IllegalArgumentException dateString이 null 또는 빈 문자열일 경우
     * @throws DateTimeParseException 날짜 형식이 잘못되었을 경우
     */
    public static LocalDate fromDateString(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            throw new IllegalArgumentException("Date string cannot be null or empty");
        }
        return LocalDate.parse(dateString, DATE_FORMATTER);
    }

    /**
     * 특정 날짜에 일수를 더합니다.
     *
     * @param date 기준 날짜
     * @param days 더할 일수 (음수 가능)
     * @return 계산된 날짜
     * @throws IllegalArgumentException date가 null일 경우
     */
    public static LocalDate addDays(LocalDate date, int days) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        return date.plusDays(days);
    }

    /**
     * 두 날짜 사이의 일수를 계산합니다.
     *
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 두 날짜 사이의 일수 (음수 가능)
     * @throws IllegalArgumentException startDate 또는 endDate가 null일 경우
     */
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * 특정 날짜가 주말인지 확인합니다.
     *
     * @param date 확인할 날짜
     * @return 주말이면 true, 그렇지 않으면 false
     * @throws IllegalArgumentException date가 null일 경우
     */
    public static boolean isWeekend(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        return date.getDayOfWeek() == java.time.DayOfWeek.SATURDAY ||
               date.getDayOfWeek() == java.time.DayOfWeek.SUNDAY;
    }

    /**
     * 특정 날짜가 윤년인지 확인합니다.
     *
     * @param date 확인할 날짜
     * @return 윤년이면 true, 그렇지 않으면 false
     * @throws IllegalArgumentException date가 null일 경우
     */
    public static boolean isLeapYear(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        return date.isLeapYear();
    }

    /**
     * 특정 날짜의 마지막 날을 반환합니다.
     *
     * @param date 기준 날짜
     * @return 해당 월의 마지막 날짜
     * @throws IllegalArgumentException date가 null일 경우
     */
    public static LocalDate getLastDayOfMonth(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        return date.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * 특정 날짜가 공휴일인지 확인합니다.
     *
     * @param date 확인할 날짜
     * @param holidays 공휴일 리스트
     * @return 공휴일이면 true, 그렇지 않으면 false
     * @throws IllegalArgumentException date가 null일 경우
     */
    public static boolean isHoliday(LocalDate date, LocalDate[] holidays) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (holidays == null) {
            return false;
        }
        for (LocalDate holiday : holidays) {
            if (date.equals(holiday)) {
                return true;
            }
        }
        return false;
    }
}

