package com.spider.demo.util;

/**
 * 문자열 포맷팅을 위한 유틸리티 클래스.
 */
public class StringFormatUtils {

    /**
     * 숫자를 지정된 길이만큼 0으로 채워진 문자열로 변환합니다.
     *
     * @param number 변환할 숫자
     * @param length 출력 문자열의 길이
     * @return 0으로 채워진 숫자 문자열
     * @throws IllegalArgumentException 길이가 0보다 작을 경우
     */
    public static String zeroPad(int number, int length) {
        if (length < 0) {
            throw new IllegalArgumentException("Length must be non-negative.");
        }
        return String.format("%0" + length + "d", number);
    }

    /**
     * 주어진 문자열을 왼쪽 정렬로 변환합니다.
     *
     * @param input 입력 문자열
     * @param length 출력 문자열의 길이
     * @return 왼쪽 정렬된 문자열
     * @throws IllegalArgumentException 길이가 0보다 작을 경우
     */
    public static String leftAlign(String input, int length) {
        if (length < 0) {
            throw new IllegalArgumentException("Length must be non-negative.");
        }
        if (input == null) {
            input = "";
        }
        return String.format("%-" + length + "s", input);
    }

    /**
     * 주어진 문자열을 오른쪽 정렬로 변환합니다.
     *
     * @param input 입력 문자열
     * @param length 출력 문자열의 길이
     * @return 오른쪽 정렬된 문자열
     * @throws IllegalArgumentException 길이가 0보다 작을 경우
     */
    public static String rightAlign(String input, int length) {
        if (length < 0) {
            throw new IllegalArgumentException("Length must be non-negative.");
        }
        if (input == null) {
            input = "";
        }
        return String.format("%" + length + "s", input);
    }

    /**
     * 문자열의 첫 글자를 대문자로 변환합니다.
     *
     * @param input 입력 문자열
     * @return 첫 글자가 대문자로 변환된 문자열, null 입력 시 null 반환
     */
    public static String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    /**
     * 문자열을 타이틀 케이스(각 단어의 첫 글자가 대문자)로 변환합니다.
     *
     * @param input 입력 문자열
     * @return 타이틀 케이스로 변환된 문자열
     */
    public static String toTitleCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        String[] words = input.split(" ");
        StringBuilder titleCase = new StringBuilder();
        for (String word : words) {
            titleCase.append(capitalize(word)).append(" ");
        }
        return titleCase.toString().trim();
    }
}
