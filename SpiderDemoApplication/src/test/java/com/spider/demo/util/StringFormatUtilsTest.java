package com.spider.demo.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StringFormatUtilsTest {

    @Test
    void testZeroPad() {
        System.out.println("[TEST] 숫자를 0으로 채운 문자열로 변환하는 기능 테스트");
        assertEquals("00123", StringFormatUtils.zeroPad(123, 5));
        assertEquals("123", StringFormatUtils.zeroPad(123, 3));
        assertThrows(IllegalArgumentException.class, () -> StringFormatUtils.zeroPad(123, -1));
    }

    @Test
    void testLeftAlign() {
        System.out.println("[TEST] 문자열을 왼쪽 정렬로 변환하는 기능 테스트");
        assertEquals("abc   ", StringFormatUtils.leftAlign("abc", 6));
        assertEquals("abc", StringFormatUtils.leftAlign("abc", 3));
        assertThrows(IllegalArgumentException.class, () -> StringFormatUtils.leftAlign("abc", -1));
    }

    @Test
    void testRightAlign() {
        System.out.println("[TEST] 문자열을 오른쪽 정렬로 변환하는 기능 테스트");
        assertEquals("   abc", StringFormatUtils.rightAlign("abc", 6));
        assertEquals("abc", StringFormatUtils.rightAlign("abc", 3));
        assertThrows(IllegalArgumentException.class, () -> StringFormatUtils.rightAlign("abc", -1));
    }

    @Test
    void testCapitalize() {
        System.out.println("[TEST] 문자열의 첫 글자를 대문자로 변환하는 기능 테스트");
        assertEquals("Hello", StringFormatUtils.capitalize("hello"));
        assertEquals("Hello world", StringFormatUtils.capitalize("hello world"));
        assertNull(StringFormatUtils.capitalize(null));
    }

    @Test
    void testToTitleCase() {
        System.out.println("[TEST] 문자열을 타이틀 케이스로 변환하는 기능 테스트");
        assertEquals("Hello World", StringFormatUtils.toTitleCase("hello world"));
        assertEquals("Java Programming", StringFormatUtils.toTitleCase("java programming"));
        assertEquals("", StringFormatUtils.toTitleCase(""));
    }
}
