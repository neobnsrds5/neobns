package com.spider.demo.util;

/**
 * 수학 연산을 위한 유틸리티 클래스.
 */
public class MathUtils {

    /**
     * 주어진 숫자의 팩토리얼을 계산합니다.
     *
     * @param n 계산할 숫자 (0 이상)
     * @return n의 팩토리얼 값
     * @throws IllegalArgumentException n이 음수일 경우
     */
    public static long factorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Number must be non-negative.");
        }
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    /**
     * 주어진 숫자가 소수인지 확인합니다.
     *
     * @param n 확인할 숫자
     * @return n이 소수이면 true, 그렇지 않으면 false
     */
    public static boolean isPrime(int n) {
        if (n <= 1) {
            return false;
        }
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 두 숫자의 최대공약수를 계산합니다.
     *
     * @param a 첫 번째 숫자
     * @param b 두 번째 숫자
     * @return a와 b의 최대공약수
     */
    public static int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    /**
     * 두 숫자의 최소공배수를 계산합니다.
     *
     * @param a 첫 번째 숫자
     * @param b 두 번째 숫자
     * @return a와 b의 최소공배수
     */
    public static int lcm(int a, int b) {
        if (a == 0 || b == 0) {
            return 0;
        }
        return Math.abs(a * b) / gcd(a, b);
    }

    /**
     * 주어진 숫자의 제곱근을 계산합니다.
     *
     * @param n 계산할 숫자 (0 이상)
     * @return n의 제곱근 값
     * @throws IllegalArgumentException n이 음수일 경우
     */
    public static double squareRoot(double n) {
        if (n < 0) {
            throw new IllegalArgumentException("Number must be non-negative.");
        }
        return Math.sqrt(n);
    }

    /**
     * 주어진 두 숫자 사이의 랜덤 정수를 생성합니다.
     *
     * @param min 최소값 (포함)
     * @param max 최대값 (포함)
     * @return min과 max 사이의 랜덤 정수
     * @throws IllegalArgumentException min이 max보다 클 경우
     */
    public static int randomInt(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("Min must be less than or equal to Max.");
        }
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    /**
     * 피보나치 수열의 n번째 숫자를 계산합니다.
     *
     * @param n 계산할 위치 (0 이상)
     * @return n번째 피보나치 숫자
     * @throws IllegalArgumentException n이 음수일 경우
     */
    public static int fibonacci(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Index must be non-negative.");
        }
        if (n == 0) {
            return 0;
        }
        if (n == 1) {
            return 1;
        }
        int a = 0, b = 1;
        for (int i = 2; i <= n; i++) {
            int temp = a + b;
            a = b;
            b = temp;
        }
        return b;
    }
}