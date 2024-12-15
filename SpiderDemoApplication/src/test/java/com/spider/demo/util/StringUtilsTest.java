package com.spider.demo.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    @Test
    void testReverse() {
        System.out.println("[TEST] 문자열 뒤집기 기능 테스트");
        assertEquals("dcba", StringUtils.reverse("abcd"));
        assertNull(StringUtils.reverse(null));
    }

    @Test
    void testToUpperCase() {
        System.out.println("[TEST] 문자열 대문자 변환 기능 테스트");
        assertEquals("HELLO", StringUtils.toUpperCase("hello"));
        assertNull(StringUtils.toUpperCase(null));
    }

    @Test
    void testToLowerCase() {
        System.out.println("[TEST] 문자열 소문자 변환 기능 테스트");
        assertEquals("world", StringUtils.toLowerCase("WORLD"));
        assertNull(StringUtils.toLowerCase(null));
    }

    @Test
    void testIsPalindrome() {
        System.out.println("[TEST] 회문 확인 기능 테스트");
        assertTrue(StringUtils.isPalindrome("madam"));
        assertFalse(StringUtils.isPalindrome("hello"));
        assertFalse(StringUtils.isPalindrome(null));
    }

    @Test
    void testJoin() {
        System.out.println("[TEST] 문자열 결합 기능 테스트");
        assertEquals("a,b,c", StringUtils.join(",", "a", "b", "c"));
        assertNull(StringUtils.join(",", (String[]) null));
    }

    @Test
    void testSplit() {
        System.out.println("[TEST] 문자열 분리 기능 테스트");
        assertArrayEquals(new String[]{"a", "b", "c"}, StringUtils.split("a,b,c", ","));
        assertNull(StringUtils.split(null, ","));
    }

    @Test
    void testTrim() {
        System.out.println("[TEST] 문자열 공백 제거 기능 테스트");
        assertEquals("hello", StringUtils.trim("  hello  "));
        assertNull(StringUtils.trim(null));
    }

    @Test
    void testAbbreviate() {
        System.out.println("[TEST] 문자열 축약 기능 테스트");
        assertEquals("hel...", StringUtils.abbreviate("hello world", 6));
        assertEquals("hello", StringUtils.abbreviate("hello", 10));
        assertThrows(IllegalArgumentException.class, () -> StringUtils.abbreviate("hello", 2));
    }

    @Test
    void testIsEmpty() {
        System.out.println("[TEST] 빈 문자열 확인 기능 테스트");
        assertTrue(StringUtils.isEmpty(""));
        assertFalse(StringUtils.isEmpty("hello"));
        assertTrue(StringUtils.isEmpty(null));
    }

    @Test
    void testIsBlank() {
        System.out.println("[TEST] 공백 문자열 확인 기능 테스트");
        assertTrue(StringUtils.isBlank(""));
        assertTrue(StringUtils.isBlank("   "));
        assertFalse(StringUtils.isBlank("hello"));
        assertTrue(StringUtils.isBlank(null));
    }

    @Test
    void testRemoveWhitespace() {
        System.out.println("[TEST] 문자열 공백 제거 기능 테스트");
        assertEquals("helloworld", StringUtils.removeWhitespace("hello world"));
        assertNull(StringUtils.removeWhitespace(null));
    }

    @Test
    void testContainsIgnoreCase() {
        System.out.println("[TEST] 대소문자 무시 포함 여부 확인 기능 테스트");
        assertTrue(StringUtils.containsIgnoreCase("Hello World", "hello"));
        assertFalse(StringUtils.containsIgnoreCase("Hello World", "hi"));
        assertFalse(StringUtils.containsIgnoreCase(null, "hello"));
    }

    @Test
    void testReverseWords() {
        System.out.println("[TEST] 단어 역순 변환 기능 테스트");
        assertEquals("hello world", StringUtils.reverseWords("olleh dlrow"));
        assertNull(StringUtils.reverseWords(null));
    }

    @Test
    void testCountOccurrences() {
        System.out.println("[TEST] 특정 문자 발생 횟수 계산 기능 테스트");
        assertEquals(2, StringUtils.countOccurrences("hello world hello", 'h'));
        assertEquals(0, StringUtils.countOccurrences("hello world", 'z'));
        assertEquals(0, StringUtils.countOccurrences(null, 'h'));
    }

    @Test
    void testTruncate() {
        System.out.println("[TEST] 문자열 잘라내기 기능 테스트");
        assertEquals("hel", StringUtils.truncate("hello", 3));
        assertEquals("hello", StringUtils.truncate("hello", 10));
        assertNull(StringUtils.truncate(null, 3));
    }

    @Test
    void testStartsWith() {
        System.out.println("[TEST] 문자열 접두사 확인 기능 테스트");
        assertTrue(StringUtils.startsWith("hello world", "hello"));
        assertFalse(StringUtils.startsWith("hello world", "world"));
        assertFalse(StringUtils.startsWith(null, "hello"));
    }

    @Test
    void testEndsWith() {
        System.out.println("[TEST] 문자열 접미사 확인 기능 테스트");
        assertTrue(StringUtils.endsWith("hello world", "world"));
        assertFalse(StringUtils.endsWith("hello world", "hello"));
        assertFalse(StringUtils.endsWith(null, "world"));
    }
}
