package com.spider.demo.util;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;


import static org.junit.jupiter.api.Assertions.*;

class DateUtilsTest {

    @Test
    void testGetCurrentDateAsString() {
        System.out.println("[TEST] 현재 날짜를 yyyyMMdd 형식 문자열로 변환하는 기능 테스트");
        String expectedDate = LocalDate.now().format(DateUtils.DATE_FORMATTER);
        assertEquals(expectedDate, DateUtils.getCurrentDateAsString());
    }

    @Test
    void testToDateString() {
        System.out.println("[TEST] LocalDate를 yyyyMMdd 문자열로 변환하는 기능 테스트");
        LocalDate date = LocalDate.of(2024, 12, 11);
        assertEquals("20241211", DateUtils.toDateString(date));
        assertThrows(IllegalArgumentException.class, () -> DateUtils.toDateString(null));
    }

    @Test
    void testFromDateString() {
        System.out.println("[TEST] yyyyMMdd 문자열을 LocalDate로 변환하는 기능 테스트");
        LocalDate expectedDate = LocalDate.of(2024, 12, 11);
        assertEquals(expectedDate, DateUtils.fromDateString("20241211"));
        assertThrows(IllegalArgumentException.class, () -> DateUtils.fromDateString(null));
        assertThrows(DateTimeParseException.class, () -> DateUtils.fromDateString("invalid-date"));
    }

    @Test
    void testAddDays() {
        System.out.println("[TEST] 날짜에 일수를 더하는 기능 테스트");
        LocalDate date = LocalDate.of(2024, 12, 11);
        assertEquals(LocalDate.of(2024, 12, 15), DateUtils.addDays(date, 4));
        assertEquals(LocalDate.of(2024, 12, 7), DateUtils.addDays(date, -4));
        assertThrows(IllegalArgumentException.class, () -> DateUtils.addDays(null, 4));
    }

    @Test
    void testDaysBetween() {
        System.out.println("[TEST] 두 날짜 사이의 일수를 계산하는 기능 테스트");
        LocalDate startDate = LocalDate.of(2024, 12, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 11);
        assertEquals(10, DateUtils.daysBetween(startDate, endDate));
        assertEquals(-10, DateUtils.daysBetween(endDate, startDate));
        assertThrows(IllegalArgumentException.class, () -> DateUtils.daysBetween(null, endDate));
        assertThrows(IllegalArgumentException.class, () -> DateUtils.daysBetween(startDate, null));
    }

    @Test
    void testIsWeekend() {
        System.out.println("[TEST] 주말 여부를 확인하는 기능 테스트");
        LocalDate saturday = LocalDate.of(2024, 12, 14);
        LocalDate monday = LocalDate.of(2024, 12, 16);
        assertTrue(DateUtils.isWeekend(saturday));
        assertFalse(DateUtils.isWeekend(monday));
        assertThrows(IllegalArgumentException.class, () -> DateUtils.isWeekend(null));
    }

    @Test
    void testIsLeapYear() {
        System.out.println("[TEST] 윤년 여부를 확인하는 기능 테스트");
        LocalDate leapYearDate = LocalDate.of(2024, 1, 1);
        LocalDate nonLeapYearDate = LocalDate.of(2023, 1, 1);
        assertTrue(DateUtils.isLeapYear(leapYearDate));
        assertFalse(DateUtils.isLeapYear(nonLeapYearDate));
        assertThrows(IllegalArgumentException.class, () -> DateUtils.isLeapYear(null));
    }

    @Test
    void testGetLastDayOfMonth() {
        System.out.println("[TEST] 특정 날짜의 마지막 날 확인 기능 테스트");
        LocalDate date = LocalDate.of(2024, 2, 15);
        assertEquals(LocalDate.of(2024, 2, 29), DateUtils.getLastDayOfMonth(date)); // 윤년
        date = LocalDate.of(2023, 2, 15);
        assertEquals(LocalDate.of(2023, 2, 28), DateUtils.getLastDayOfMonth(date)); // 평년
        assertThrows(IllegalArgumentException.class, () -> DateUtils.getLastDayOfMonth(null));
    }

    @Test
    void testIsHoliday() {
        System.out.println("[TEST] 특정 날짜가 공휴일인지 확인하는 기능 테스트");
        LocalDate holiday = LocalDate.of(2024, 12, 25);
        LocalDate nonHoliday = LocalDate.of(2024, 12, 26);
        LocalDate[] holidays = {LocalDate.of(2024, 12, 25), LocalDate.of(2024, 1, 1)};
        assertTrue(DateUtils.isHoliday(holiday, holidays));
        assertFalse(DateUtils.isHoliday(nonHoliday, holidays));
        assertThrows(IllegalArgumentException.class, () -> DateUtils.isHoliday(null, holidays));
    }
}

