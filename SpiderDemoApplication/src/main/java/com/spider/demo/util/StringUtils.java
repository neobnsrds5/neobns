package com.spider.demo.util;

/**
 * 문자열 처리를 위한 유틸리티 클래스.
 */
public class StringUtils {

    /**
     * 주어진 문자열을 뒤집습니다.
     *
     * @param input 입력 문자열
     * @return 뒤집힌 문자열, 입력이 null이면 null 반환
     */
    public static String reverse(String input) {
        if (input == null) {
            return null;
        }
        return new StringBuilder(input).reverse().toString();
    }

    /**
     * 문자열을 대문자로 변환합니다.
     *
     * @param input 입력 문자열
     * @return 대문자로 변환된 문자열, 입력이 null이면 null 반환
     */
    public static String toUpperCase(String input) {
        if (input == null) {
            return null;
        }
        return input.toUpperCase();
    }

    /**
     * 문자열을 소문자로 변환합니다.
     *
     * @param input 입력 문자열
     * @return 소문자로 변환된 문자열, 입력이 null이면 null 반환
     */
    public static String toLowerCase(String input) {
        if (input == null) {
            return null;
        }
        return input.toLowerCase();
    }

    /**
     * 주어진 문자열이 회문인지 확인합니다.
     *
     * @param input 입력 문자열
     * @return 문자열이 회문이면 true, 그렇지 않으면 false, null이면 false 반환
     */
    public static boolean isPalindrome(String input) {
        if (input == null) {
            return false;
        }
        String reversed = reverse(input);
        return input.equals(reversed);
    }

    /**
     * 배열의 문자열을 지정된 구분자로 결합합니다.
     *
     * @param delimiter 구분자
     * @param elements 문자열 배열
     * @return 결합된 문자열, 배열이 null이면 null 반환
     */
    public static String join(String delimiter, String... elements) {
        if (elements == null) {
            return null;
        }
        return String.join(delimiter, elements);
    }

    /**
     * 문자열을 지정된 구분자로 분리합니다.
     *
     * @param input 입력 문자열
     * @param delimiter 구분자
     * @return 분리된 문자열 배열, 입력 또는 구분자가 null이면 null 반환
     */
    public static String[] split(String input, String delimiter) {
        if (input == null || delimiter == null) {
            return null;
        }
        return input.split(delimiter);
    }

    /**
     * 문자열 앞뒤의 공백을 제거합니다.
     *
     * @param input 입력 문자열
     * @return 공백이 제거된 문자열, 입력이 null이면 null 반환
     */
    public static String trim(String input) {
        if (input == null) {
            return null;
        }
        return input.trim();
    }

    /**
     * 주어진 길이만큼 문자열을 축약하고 "..."을 추가합니다.
     *
     * @param input 입력 문자열
     * @param maxLength 최대 길이
     * @return 축약된 문자열
     * @throws IllegalArgumentException maxLength가 3보다 작을 경우
     */
    public static String abbreviate(String input, int maxLength) {
        if (maxLength < 3) {
            throw new IllegalArgumentException("최대 길이는 3보다 작을 수 없습니다.");
        }
        if (input == null || input.length() <= maxLength) {
            return input;
        }
        return input.substring(0, maxLength - 3) + "...";
    }

    /**
     * 문자열이 빈 문자열인지 확인합니다.
     *
     * @param input 입력 문자열
     * @return 빈 문자열이면 true, 그렇지 않으면 false, null이면 false 반환
     */
    public static boolean isEmpty(String input) {
        return input == null || input.isEmpty();
    }

    /**
     * 문자열이 공백으로만 이루어져 있는지 확인합니다.
     *
     * @param input 입력 문자열
     * @return 공백 문자열이면 true, 그렇지 않으면 false, null이면 false 반환
     */
    public static boolean isBlank(String input) {
        return input == null || input.trim().isEmpty();
    }

    /**
     * 문자열에서 공백을 제거합니다.
     *
     * @param input 입력 문자열
     * @return 공백이 제거된 문자열, 입력이 null이면 null 반환
     */
    public static String removeWhitespace(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("\\s", "");
    }

    /**
     * 두 문자열을 비교할 때 대소문자를 무시하고 포함 여부를 확인합니다.
     *
     * @param str 입력 문자열
     * @param search 검색할 문자열
     * @return 포함되면 true, 그렇지 않으면 false
     */
    public static boolean containsIgnoreCase(String str, String search) {
        if (str == null || search == null) {
            return false;
        }
        return str.toLowerCase().contains(search.toLowerCase());
    }

    /**
     * 문자열의 각 단어를 역순으로 만듭니다.
     *
     * @param input 입력 문자열
     * @return 각 단어가 역순으로 된 문자열
     */
    public static String reverseWords(String input) {
        if (input == null) {
            return null;
        }
        String[] words = input.split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            result.append(new StringBuilder(word).reverse()).append(" ");
        }
        return result.toString().trim();
    }

    /**
     * 문자열에서 특정 문자의 발생 횟수를 계산합니다.
     *
     * @param input 입력 문자열
     * @param ch 검색할 문자
     * @return 문자의 발생 횟수, 입력이 null이면 0 반환
     */
    public static int countOccurrences(String input, char ch) {
        if (input == null) {
            return 0;
        }
        int count = 0;
        for (char c : input.toCharArray()) {
            if (c == ch) {
                count++;
            }
        }
        return count;
    }

    /**
     * 문자열을 지정된 길이로 자르고 나머지를 생략합니다.
     *
     * @param input 입력 문자열
     * @param length 잘라낼 길이
     * @return 잘린 문자열, 입력이 null이면 null 반환
     */
    public static String truncate(String input, int length) {
        if (input == null) {
            return null;
        }
        return input.length() > length ? input.substring(0, length) : input;
    }

    /**
     * 문자열에서 특정 접두사가 있는지 확인합니다.
     *
     * @param input 입력 문자열
     * @param prefix 접두사 문자열
     * @return 접두사가 있으면 true, 그렇지 않으면 false
     */
    public static boolean startsWith(String input, String prefix) {
        if (input == null || prefix == null) {
            return false;
        }
        return input.startsWith(prefix);
    }

    /**
     * 문자열에서 특정 접미사가 있는지 확인합니다.
     *
     * @param input 입력 문자열
     * @param suffix 접미사 문자열
     * @return 접미사가 있으면 true, 그렇지 않으면 false
     */
    public static boolean endsWith(String input, String suffix) {
        if (input == null || suffix == null) {
            return false;
        }
        return input.endsWith(suffix);
    }

    /**
     * 문자열에서 모든 알파벳 문자를 제거합니다.
     *
     * @param input 입력 문자열
     * @return 알파벳이 제거된 문자열, 입력이 null이면 null 반환
     */
    public static String removeAlphabets(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("[a-zA-Z]", "");
    }
    
    /**
     * 문자열에서 특정 단어를 대체합니다.
     *
     * @param input 입력 문자열
     * @param target 대체할 단어
     * @param replacement 대체 후 단어
     * @return 대체된 문자열
     */
    public static String replaceWord(String input, String target, String replacement) {
        if (input == null || target == null || replacement == null) {
            return input;
        }
        return input.replace(target, replacement);
    }

    /**
     * 문자열에서 중복된 공백을 하나로 줄입니다.
     *
     * @param input 입력 문자열
     * @return 공백이 하나로 축소된 문자열
     */
    public static String reduceSpaces(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("\\s+", " ");
    }

    /**
     * 주어진 문자로 시작하는지 확인합니다.
     *
     * @param input 입력 문자열
     * @param ch 확인할 문자
     * @return 주어진 문자로 시작하면 true, 아니면 false
     */
    public static boolean startsWithChar(String input, char ch) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        return input.charAt(0) == ch;
    }

    
    
}
